package ru.otus.server;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;
import ru.otus.servlet.AuthorizationFilter;
import ru.otus.servlet.LoginServlet;


import java.util.Arrays;

public class ClientsWebServerWithFilterBasedSecurity extends ClientsWebServerSimple {
    private static final String PATH_LOGIN = "/login";
    private final UserAuthService authService;

    public ClientsWebServerWithFilterBasedSecurity(int port,
                                                   DBServiceClient serviceClient,
                                                   UserAuthService authService,
                                                   Gson gson,
                                                   TemplateProcessor templateProcessor) {
        super(port, serviceClient, gson, templateProcessor);
        this.authService = authService;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(
                new ServletHolder(new LoginServlet(templateProcessor, authService)),
                PATH_LOGIN
        );
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths).forEachOrdered(
                path -> servletContextHandler.addFilter(
                        new FilterHolder(authorizationFilter),
                        path,
                        null
                )
        );
        return servletContextHandler;
    }
}
