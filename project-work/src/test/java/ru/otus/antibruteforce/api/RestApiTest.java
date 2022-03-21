package ru.otus.antibruteforce.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.antibruteforce.api.rest.RestApiController;
import ru.otus.antibruteforce.service.AntiBruteforceService;
import ru.otus.antibruteforce.service.IllegalIpv4Exception;

import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestApiController.class)
public class RestApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AntiBruteforceService service;

    private final ObjectMapper mapper = new JsonMapper();

    private static final String LOGIN = "login";
    private static final String PASSWORD = "012345678";
    private static final String IP = "128.11.0.1";

    private static final String LOGIN_BRUT = "loginBRUT";

    private static final String IP_BLACKLIST = "128.11.0.2";
    private static final String IP_WHITELIST = "128.11.0.3";
    private static final String IP_ERROR = "128110.3";

    private static final String LOGIN_PARAM = "login";
    private static final String PASSWORD_PARAM = "password";
    private static final String IP_PARAM = "ip";

    private static final String ERROR_MSG = "error";

    @Test
    void isBruteforceFalseTest() throws Exception {
        given(service.isBruteforce(LOGIN, PASSWORD, IP)).willReturn(false);
        var params = Map.of(LOGIN_PARAM, LOGIN, PASSWORD_PARAM, PASSWORD, IP_PARAM, IP);
        mvc.perform(get("/api/v1/checkbruteforce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("value")
                        .value("no"));
    }

    @Test
    void isBruteforceTrueTest() throws Exception {
        given(service.isBruteforce(LOGIN_BRUT, PASSWORD, IP)).willReturn(true);
        var params = Map.of(LOGIN_PARAM, LOGIN_BRUT, PASSWORD_PARAM, PASSWORD, IP_PARAM, IP);
        mvc.perform(get("/api/v1/checkbruteforce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("value")
                        .value("yes"));
    }

    @Test
    void addIpBlacklistTest() throws Exception {
        willDoNothing().given(service).addIpBlacklist(IP_BLACKLIST);
        var params = Map.of(IP_PARAM, IP_BLACKLIST);
        mvc.perform(post("/api/v1/blacklist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk());
    }

    @Test
    void addIpBlacklistError422Test() throws Exception {
        willThrow(new IllegalIpv4Exception(ERROR_MSG)).given(service).addIpBlacklist(IP_ERROR);
        var params = Map.of(IP_PARAM, IP_ERROR);
        mvc.perform(post("/api/v1/blacklist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void addIpBlacklistError500Test() throws Exception {
        willThrow(new IllegalArgumentException(ERROR_MSG)).given(service).addIpBlacklist(IP_WHITELIST);
        var params = Map.of(IP_PARAM, IP_WHITELIST);
        mvc.perform(post("/api/v1/blacklist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addIpWhitelistTest() throws Exception {
        willDoNothing().given(service).addIpWhitelist(IP_WHITELIST);
        var params = Map.of(IP_PARAM, IP_WHITELIST);
        mvc.perform(post("/api/v1/whitelist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteIpBlacklistTest() throws Exception {
        willDoNothing().given(service).deleteIpBlacklist(IP_BLACKLIST);
        var params = Map.of(IP_PARAM, IP_BLACKLIST);
        mvc.perform(delete("/api/v1/blacklist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteIpWhitelistTest() throws Exception {
        willDoNothing().given(service).deleteIpWhitelist(IP_WHITELIST);
        var params = Map.of(IP_PARAM, IP_WHITELIST);
        mvc.perform(delete("/api/v1/whitelist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByLoginIpTest() throws Exception {
        willDoNothing().given(service).deleteByLoginIp(LOGIN, IP_WHITELIST);
        var params = Map.of(LOGIN_PARAM, LOGIN, IP_PARAM, IP_WHITELIST);
        mvc.perform(delete("/api/v1/iploginbucket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk());
    }
}
