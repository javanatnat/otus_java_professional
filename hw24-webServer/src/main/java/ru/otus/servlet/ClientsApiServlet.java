package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import ru.otus.crm.ClientDTO;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


public class ClientsApiServlet extends HttpServlet {

    private static final String PARAM_NAME = "name";
    private static final String PARAM_ADDRESS = "address";
    private static final String PARAM_PHONES = "phones";
    private static final int ID_PATH_PARAM_POSITION = 1;

    private static final String PATH_CLIENTS = "/clients";

    private final DBServiceClient serviceClient;
    private final Gson gson;

    public ClientsApiServlet(DBServiceClient serviceClient, Gson gson) {
        this.serviceClient = serviceClient;
        this.gson = gson;
    }

    // получить клиента
    // GET /api/client/{id},
    // example: GET /api/client/1
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long id = extractIdFromRequest(request);
        Optional<Client> client = serviceClient.getClient(id);
        ClientDTO clientDTO = client.map(ClientDTO::new).orElseGet(ClientDTO::new);

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(clientDTO));
    }

    // добавить клиента
    // POST /api/client
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ClientDTO clientDTO = new ClientDTO(
                null,
                getClientName(req),
                getClientAddress(req),
                getClientPhones(req)
        );
        Client client = clientDTO.getClient();
        Client savedClient = serviceClient.saveClient(client);
        resp.sendRedirect(PATH_CLIENTS);
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1)? path[ID_PATH_PARAM_POSITION]: String.valueOf(- 1);
        return Long.parseLong(id);
    }

    private String getClientName(HttpServletRequest request) {
        return request.getParameter(PARAM_NAME);
    }

    private String getClientAddress(HttpServletRequest request) {
        return request.getParameter(PARAM_ADDRESS);
    }

    private String getClientPhones(HttpServletRequest request) {
        return request.getParameter(PARAM_PHONES);
    }
}
