package ru.otus.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import ru.otus.crm.ClientDTO;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.crmcore.repository.DataTemplateHibernate;
import ru.otus.crmcore.repository.HibernateUtils;
import ru.otus.crmcore.sessionmanager.TransactionManagerHibernate;
import ru.otus.server.utils.TestContainersConfig;
import ru.otus.services.TemplateProcessor;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static ru.otus.crm.service.DbServiceClientImpl.*;
import static ru.otus.crmdemo.DbServiceDemo.HIBERNATE_CFG_FILE;
import static ru.otus.server.utils.TestContainersConfig.*;
import static ru.otus.server.utils.WebServerHelper.*;

@DisplayName("Тест сервера должен ")
public class ClientsWebServerSimpleTest {
    private static final int WEB_SERVER_PORT = 8989;
    private static final String WEB_SERVER_URL = "http://localhost:" + WEB_SERVER_PORT + "/";
    private static final String API_CLIENT_URL = "api/client";

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private static final String FORM_PARAM_NAME = "name";
    private static final String FORM_PARAM_ADDRESS = "address";
    private static final String FORM_PARAM_PHONES = "phones";
    private static final String AND ="&";

    private static final long CLIENT_ID = 2L;
    private static final String CLIENT_NAME = "Max";
    private static final String CLIENT_ADDRESS = "Moscow";
    private static final String CLIENT_PHONES = "8-913-70";
    private static final long INCORRECT_CLIENT_ID = 10L;
    private static final ClientDTO CLIENT_DTO = new ClientDTO(CLIENT_ID, CLIENT_NAME, CLIENT_ADDRESS, CLIENT_PHONES);
    private static final ClientDTO CLIENT_DTO_NULL = new ClientDTO(null, null, null, null);

    private static Gson gson;
    private static HttpClient http;
    private static ClientsWebServer webServer;

    private static TestContainersConfig.CustomPostgreSQLContainer CONTAINER;

    @BeforeAll
    static void setUp() throws Exception {
        CONTAINER = TestContainersConfig.CustomPostgreSQLContainer.getInstance();
        CONTAINER.start();

        String dbUrl = System.getProperty(JDBC_URL);
        String dbUserName = System.getProperty(DB_LOGIN);
        String dbPassword = System.getProperty(DB_PASSWORD);

        var migrationsExecutor = new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword);
        migrationsExecutor.executeMigrations();

        Configuration configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        configuration.setProperty(HIBERNATE_URL, dbUrl);
        configuration.setProperty(HIBERNATE_LOGIN, dbUserName);
        configuration.setProperty(HIBERNATE_PASSWORD, dbPassword);

        var sessionFactory = HibernateUtils.buildSessionFactory(
                configuration,
                Client.class,
                Address.class,
                Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);

        http = HttpClient.newHttpClient();
        gson = new GsonBuilder().serializeNulls().create();
        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);

        webServer = new ClientsWebServerSimple(WEB_SERVER_PORT, dbServiceClient, gson, templateProcessor);
        webServer.start();
    }

    @AfterAll
    static void shutdown() throws Exception {
        CONTAINER.stop();
        webServer.stop();
    }

    @DisplayName("возвращать пустые данные при запросе пользователя по несуществующему id")
    @Test
    void shouldReturnNullClient() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_CLIENT_URL, String.valueOf(INCORRECT_CLIENT_ID))))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.body()).isEqualTo(gson.toJson(CLIENT_DTO_NULL));
    }

    @DisplayName("возвращать корректные данные при запросе пользователя по id")
    @Test
    void shouldReturnCorrectClient() throws Exception {
        getPostCorrectResponse();

        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_CLIENT_URL, String.valueOf(CLIENT_ID))))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.body()).isEqualTo(gson.toJson(CLIENT_DTO));
    }

    @DisplayName("возвращать успех при записи корректных данных пользователя")
    @Test
    void shouldReturnSuccessPostCorrectData() throws Exception {
        HttpResponse<String> responsePost = getPostCorrectResponse();
        assertThat(responsePost.statusCode()).isEqualTo(HttpURLConnection.HTTP_MOVED_TEMP);

        Optional<HttpResponse<String>> prResponsePost = responsePost.previousResponse();
        prResponsePost.ifPresent(r -> assertThat(r.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK));
    }

    @DisplayName("возвращать ошибку при записи некорректных данных пользователя")
    @Test
    void shouldReturnErrorPostNullData() throws Exception {
        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_CLIENT_URL)))
                .header(CONTENT_TYPE, FORM_URLENCODED)
                .build();

        HttpResponse<String> responsePost = http.send(requestPost, HttpResponse.BodyHandlers.ofString());
        assertThat(responsePost.statusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    private HttpResponse<String> getPostCorrectResponse() throws Exception {
        String requestBody = FORM_PARAM_NAME + "=" + CLIENT_NAME
                + AND + FORM_PARAM_ADDRESS + "=" + CLIENT_ADDRESS
                + AND + FORM_PARAM_PHONES + "=" + CLIENT_PHONES;

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_CLIENT_URL)))
                .header(CONTENT_TYPE, FORM_URLENCODED)
                .build();

        return http.send(requestPost, HttpResponse.BodyHandlers.ofString());
    }
}
