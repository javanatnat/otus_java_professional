package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.HwListenerClient;
import ru.otus.cachehw.MyCache;
import ru.otus.crmcore.repository.DataTemplate;
import ru.otus.crm.model.Client;
import ru.otus.crmcore.sessionmanager.TransactionManager;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbServiceClientImpl.class);
    private static final HwListener<String, Client> LISTENER = new HwListenerClient();

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    private HwCache<String, Client> cache;

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        initCache();
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
        Client client = getCacheClient(id);
        if (client != null) {
            return Optional.of(client);
        }

        Optional<Client> result = transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            LOGGER.info("client: {}", clientOptional);
            return clientOptional;
        });

        result.ifPresent(this::cacheClient);

        return result;
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            LOGGER.info("clientList:{}", clientList);
            return clientList;
        });

        cacheClients(clients);

        return clients;
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
