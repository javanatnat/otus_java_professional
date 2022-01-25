package ru.otus.crm.service;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.HwListenerClient;
import ru.otus.cachehw.MyCache;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Phone;
import ru.otus.crmcore.repository.DataTemplate;
import ru.otus.crm.model.Client;
import ru.otus.crmcore.repository.DataTemplateHibernate;
import ru.otus.crmcore.repository.HibernateUtils;
import ru.otus.crmcore.sessionmanager.TransactionManager;
import ru.otus.crmcore.sessionmanager.TransactionManagerHibernate;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbServiceClientImpl.class);
    private static final HwListener<String, Client> LISTENER = new HwListenerClient();
    private static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    private HwCache<String, Client> cache;

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        initCache();
    }

    public static DBServiceClient createDbService() {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(
                configuration,
                Client.class,
                Address.class,
                Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        return new DbServiceClientImpl(transactionManager, clientTemplate);
    }

    @Override
    public Client saveClient(Client client) {
        Client saved = transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                clientDataTemplate.insert(session, clientCloned);
                LOGGER.info("created client: {}", clientCloned);
                return clientCloned;
            }
            clientDataTemplate.update(session, clientCloned);
            LOGGER.info("updated client: {}", clientCloned);
            return clientCloned;
        });

        cacheClient(saved);

        return saved;
    }

    @Override
    public Optional<Client> getClient(long id) {
        return Optional.ofNullable(getCacheClient(id))
                .or(() -> transactionManager.doInReadOnlyTransaction(session -> {
                    var clientOptional = clientDataTemplate.findById(session, id);
                    LOGGER.info("client: {}", clientOptional);
                    clientOptional.ifPresent(this::cacheClient);
                    return clientOptional;
                }));
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            LOGGER.info("clientList:{}", clientList);
            this.cacheClients(clientList);
            return clientList;
        });
    }

    private void initCache() {
        cache = new MyCache<>();
        cache.addListener(LISTENER);
    }

    private void cacheClient(Client client) {
        cache.put(client.getId().toString(), client);
    }

    private void cacheClients(List<Client> clients) {
        clients.forEach(this::cacheClient);
    }

    private Client getCacheClient(long id) {
        return cache.get(Long.toString(id));
    }
}
