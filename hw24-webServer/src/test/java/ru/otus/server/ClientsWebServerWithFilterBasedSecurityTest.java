package ru.otus.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.crm.ClientDTO;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.dao.UserDao;
import ru.otus.model.User;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;

import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static ru.otus.server.utils.WebServerHelper.*;

public class ClientsWebServerWithFilterBasedSecurityTest {
    private static final int WEB_SERVER_PORT = 8989;
    private static final String WEB_SERVER_URL = "http://localhost:" + WEB_SERVER_PORT + "/";
    private static final String LOGIN_URL = "login";
    private static final String API_CLIENT_URL = "api/client";

    private static final long DEFAULT_USER_ID = 1L;
    private static final String DEFAULT_USER_LOGIN = "user1";
    private static final String DEFAULT_USER_PASSWORD = "11111";
    private static final User DEFAULT_USER = new User(DEFAULT_USER_ID, "Vasya", DEFAULT_USER_LOGIN, DEFAULT_USER_PASSWORD);
    private static final String INCORRECT_USER_LOGIN = "BadUser";

    private static final long CLIENT_ID = 2L;
    private static final String CLIENT_NAME = "Max";
    private static final String CLIENT_ADDRESS = "Moscow";
    private static final String CLIENT_PHONES = "8-913-70";

    private static final ClientDTO CLIENT_DTO = new ClientDTO(CLIENT_ID, CLIENT_NAME, CLIENT_ADDRESS, CLIENT_PHONES);

    private static Gson gson;
    private static ClientsWebServer webServer;
    private static HttpClient http;

    @BeforeAll
    static void setUp() throws Exception {
        http = HttpClient.newHttpClient();

        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        UserDao userDao = mock(UserDao.class);
        UserAuthService userAuthService = mock(UserAuthService.class);
        DBServiceClient serviceClient = mock(DBServiceClient.class);

        given(userAuthService.authenticate(DEFAULT_USER_LOGIN, DEFAULT_USER_PASSWORD)).willReturn(true);
        given(userAuthService.authenticate(INCORRECT_USER_LOGIN, DEFAULT_USER_PASSWORD)).willReturn(false);
        given(userDao.findById(DEFAULT_USER_ID)).willReturn(Optional.of(DEFAULT_USER));

        Client client = CLIENT_DTO.getClient();
        client.setId(CLIENT_ID);
        given(serviceClient.getClient(CLIENT_ID)).willReturn(Optional.of(client));

        gson = new GsonBuilder().serializeNulls().create();
        webServer = new ClientsWebServerWithFilterBasedSecurity(
                WEB_SERVER_PORT,
                serviceClient,
                userAuthService,
                gson,
                templateProcessor
        );
        webServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        webServer.stop();
    }

    @DisplayName("???????????????????? 302 ?????? ?????????????? ???????????????????????? ???? id ???????? ???? ???????????????? ???????? ")
    @Test
    void shouldReturnForbiddenStatusForUserRequestWhenUnauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_CLIENT_URL, String.valueOf(CLIENT_ID))))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_MOVED_TEMP);
    }

    @DisplayName("???????????????????? ID ???????????? ?????? ???????????????????? ?????????? ?? ?????????????? ??????????????")
    @Test
    void shouldReturnJSessionIdWhenLoggingInWithCorrectData() throws Exception {
        HttpCookie jSessionIdCookie = login(buildUrl(WEB_SERVER_URL, LOGIN_URL), DEFAULT_USER_LOGIN, DEFAULT_USER_PASSWORD);
        assertThat(jSessionIdCookie).isNotNull();
    }

    @DisplayName("???? ???????????????????? ID ???????????? ?????? ???????????????????? ?????????? ???????? ???????????? ?????????? ???? ??????????")
    @Test
    void shouldNotReturnJSessionIdWhenLoggingInWithIncorrectData() throws Exception {
        HttpCookie jSessionIdCookie = login(buildUrl(WEB_SERVER_URL, LOGIN_URL), INCORRECT_USER_LOGIN, DEFAULT_USER_PASSWORD);
        assertThat(jSessionIdCookie).isNull();
    }

    @DisplayName("???????????????????? ???????????????????? ???????????? ?????? ?????????????? ???????????????????????? ???? id ???????? ???????? ????????????????")
    @Test
    void shouldReturnCorrectUserWhenAuthorized() throws Exception {
        HttpCookie jSessionIdCookie = login(buildUrl(WEB_SERVER_URL, LOGIN_URL), DEFAULT_USER_LOGIN, DEFAULT_USER_PASSWORD);
        assertThat(jSessionIdCookie).isNotNull();

        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_CLIENT_URL,String.valueOf(CLIENT_ID))))
                .setHeader(COOKIE_HEADER, String.format("%s=%s", jSessionIdCookie.getName(), jSessionIdCookie.getValue()))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.body()).isEqualTo(gson.toJson(CLIENT_DTO));
    }
}
