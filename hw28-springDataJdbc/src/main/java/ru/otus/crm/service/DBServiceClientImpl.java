package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.repository.AddressRepository;
import ru.otus.crm.repository.ClientRepository;
import ru.otus.sessionmanager.TransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DBServiceClientImpl implements DBServiceClient {

    private static final Logger log = LoggerFactory.getLogger(DBServiceClientImpl.class);

    private final TransactionManager transactionManager;
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;

    public DBServiceClientImpl(
            TransactionManager transactionManager,
            ClientRepository clientRepository,
            AddressRepository addressRepository
    ) {
        this.transactionManager = transactionManager;
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(() -> {
            Address savedAddress = null;
            if (client.getAddress() != null && client.getAddressStr() != null) {
                savedAddress = addressRepository.save(client.getAddress());
                client.setAddressId(savedAddress.getId());
            }

            Client savedClient = clientRepository.save(client);
            if (savedAddress != null) {
                savedClient.setAddress(savedAddress);
            }

            log.info("saved client: {}", savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        log.info("client: {}", clientOptional);
        return clientOptional;
    }

    @Override
    public List<Client> findAll() {
        List<Client> clientList = new ArrayList<>(clientRepository.findAll());
        log.info("clientList:{}", clientList);
        return clientList;
    }
}
