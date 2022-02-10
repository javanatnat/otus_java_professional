package ru.otus.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.crm.model.ClientDTO;
import ru.otus.crm.service.DBServiceClient;

import java.util.List;

@Controller
public class ClientController {

    private final DBServiceClient serviceClient;

    public ClientController(DBServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @GetMapping("/clients")
    public String clientsListView(Model model) {
        model.addAttribute("clients", getAllClients());
        return "clients";
    }

    private List<ClientDTO> getAllClients() {
        return serviceClient.findAll().stream().map(ClientDTO::new).toList();
    }
}
