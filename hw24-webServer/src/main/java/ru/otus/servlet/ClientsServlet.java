package ru.otus.servlet;

import ru.otus.crm.ClientDTO;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientsServlet extends HttpServlet {

    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";
    private static final String TEMPLATE_CLIENTS = "clients";

    private final DBServiceClient serviceClient;
    private final TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient serviceClient) {
        this.templateProcessor = templateProcessor;
        this.serviceClient = serviceClient;
    }

    // получить список клиентов
    // GET /clients
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(TEMPLATE_CLIENTS, getAllClients());

        response.setContentType("text/html");
        response.getWriter().println(
                templateProcessor.getPage(
                        CLIENTS_PAGE_TEMPLATE,
                        paramsMap
                )
        );
    }

    private List<ClientDTO> getAllClients() {
        List<Client> clients = serviceClient.findAll();
        List<ClientDTO> clientDtoS = new ArrayList<>();
        for(Client client : clients) {
            clientDtoS.add(new ClientDTO(client));
        }
        return clientDtoS;
    }

}
