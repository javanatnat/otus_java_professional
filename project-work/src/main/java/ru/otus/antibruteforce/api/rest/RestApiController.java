package ru.otus.antibruteforce.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.antibruteforce.service.AntiBruteforceService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RestApiController {

    private static final Logger LOG = LoggerFactory.getLogger(RestApiController.class);
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String IP = "ip";

    private final AntiBruteforceService service;

    public RestApiController(AntiBruteforceService service) {
        this.service = service;
    }

    @GetMapping("/checkbruteforce")
    public ResponseEntity<Object> isBruteforce(@RequestBody Map<String, String> params) {
        LOG.info("isBruteforce: login={}, password={}, ip={}",
                getLogin(params), getPassword(params), getIp(params));

        Map<String, String> responseBody = Collections.singletonMap(
                "value",
                (service.isBruteforce(
                        getLogin(params),
                        getPassword(params),
                        getIp(params)) ? "yes" : "no")
        );
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/iploginbucket")
    public ResponseEntity<Object> dropBucket(@RequestBody Map<String, String> params) {
        service.deleteByLoginIp(getLogin(params), getIp(params));
        return getNoContent();
    }

    @PostMapping("/blacklist")
    public ResponseEntity<Object> addIpToBlacklist(@RequestBody Map<String, String> params) {
        LOG.info("addIpToBlacklist: ip={}", getIp(params));
        service.addIpBlacklist(getIp(params));
        return getNoContent();
    }

    @DeleteMapping("/blacklist")
    public ResponseEntity<Object> dropIpFromBlacklist(@RequestBody Map<String, String> params) {
        service.deleteIpBlacklist(getIp(params));
        return getNoContent();
    }

    @PostMapping("/whitelist")
    public ResponseEntity<Object> addIpToWhitelist(@RequestBody Map<String, String> params) {
        service.addIpWhitelist(getIp(params));
        return getNoContent();
    }

    @DeleteMapping("/whitelist")
    public ResponseEntity<Object> dropIpFromWhitelist(@RequestBody Map<String, String> params) {
        service.deleteIpWhitelist(getIp(params));
        return getNoContent();
    }

    private static String getLogin(Map<String, String> params) {
        return params.get(LOGIN);
    }

    private static String getPassword(Map<String, String> params) {
        return params.get(PASSWORD);
    }

    private static String getIp(Map<String, String> params) {
        return params.get(IP);
    }

    private ResponseEntity<Object> getNoContent() {
        return ResponseEntity.ok().build();
    }
}
