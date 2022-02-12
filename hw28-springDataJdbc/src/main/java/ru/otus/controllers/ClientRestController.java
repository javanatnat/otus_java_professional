package ru.otus.controllers;

import org.springframework.web.bind.annotation.*;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.ClientDTO;
import ru.otus.crm.service.DBServiceClient;

@RestController
public class ClientRestController {

    private final DBServiceClient serviceClient;

    public ClientRestController(DBServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    // curl -X 'GET' http://localhost:8080/api/client/1
    @GetMapping("/api/client/{id}")
    public ClientDTO getClientById(@PathVariable(name = "id") long id) {
        return serviceClient.getClient(id).map(ClientDTO::new).orElseGet(ClientDTO::new);
    }

    // curl -X 'POST' -H 'Content-Type: application/json' --data '{"name":"Ivan"}' http://localhost:8080/api/client
    // curl -X 'POST' -H 'Content-Type: application/json' --data '{"name":"Ivan","address":"Avenue 5"}' http://localhost:8080/api/client
    // curl -X 'POST' -H 'Content-Type: application/json' --data '{"name":"Ivan","address":"Avenue 5","phones":"123, 453"}' http://localhost:8080/api/client
    @PostMapping("/api/client")
    public ClientDTO saveClient(@RequestBody ClientDTO clientDTO) {
        Client client = clientDTO.getClient();
        Client savedClient = serviceClient.saveClient(client);
        return new ClientDTO(savedClient);
    }
}
