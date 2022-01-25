package ru.otus.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;

import java.io.IOException;
import java.util.Collections;

import static jakarta.servlet.http.HttpServletResponse.*;

public class LoginServlet extends HttpServlet {

    private static final String PARAM_LOGIN = "login";
    private static final String PARAM_PASSWORD = "password";
    private static final int MAX_INACTIVE_INTERVAL = 300;
    private static final String LOGIN_PAGE_TEMPLATE = "login.html";
    private static final String PATH_CLIENTS = "/clients";

    private final TemplateProcessor templateProcessor;
    private final UserAuthService userAuthService;

    public LoginServlet(TemplateProcessor templateProcessor, UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.getWriter().println(
                templateProcessor.getPage(
                        LOGIN_PAGE_TEMPLATE,
                        Collections.emptyMap()
                )
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException
    {
        if (userAuthService.authenticate(
                getUserLogin(request),
                getUserPassword(request)
        )) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
            response.sendRedirect(PATH_CLIENTS);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }

    private String getUserLogin(HttpServletRequest request) {
        return request.getParameter(PARAM_LOGIN);
    }

    private String getUserPassword(HttpServletRequest request) {
        return request.getParameter(PARAM_PASSWORD);
    }
}
