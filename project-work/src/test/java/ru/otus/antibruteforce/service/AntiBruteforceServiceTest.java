package ru.otus.antibruteforce.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest(properties = "countAttempt.login=2")
@SpringJUnitConfig(AntiBruteforceServiceTest.Config.class)
public class AntiBruteforceServiceTest {

    private static final String LOGIN_TIME = "login";
    private static final String LOGIN = "login_simple";
    private static final String PASSWORD = "012345678";
    private static final String PASSWORD_BRUTEFORCE = "0123456789";
    private static final String IP_1 = "128.11.0.1";
    private static final String IP_BRUTEFORCE = "128.11.0.2";
    private static final String IP_SIMPLE_NOT_LIST = "128.11.0.4";
    private static final String IP_IN_WHITELIST = "128.11.0.12";
    private static final String IP_IN_BLACKLIST = "128.11.0.13";

    @Configuration
    static class Config {

        @Bean
        BlacklistService blacklistService() {
            BlacklistService blacklistService = mock(BlacklistService.class);
            given(blacklistService.isInList(IP_IN_BLACKLIST)).willReturn(true);
            given(blacklistService.isInList(IP_IN_WHITELIST)).willReturn(false);
            given(blacklistService.isInList(IP_1)).willReturn(false);
            given(blacklistService.isInList(IP_BRUTEFORCE)).willReturn(false);
            given(blacklistService.isInList(IP_SIMPLE_NOT_LIST)).willReturn(false);
            return blacklistService;
        }

        @Bean
        WhitelistService whitelistService() {
            WhitelistService whitelistService = mock(WhitelistService.class);
            given(whitelistService.isInList(IP_IN_WHITELIST)).willReturn(true);
            given(whitelistService.isInList(IP_IN_BLACKLIST)).willReturn(false);
            given(whitelistService.isInList(IP_1)).willReturn(false);
            given(whitelistService.isInList(IP_BRUTEFORCE)).willReturn(false);
            given(whitelistService.isInList(IP_SIMPLE_NOT_LIST)).willReturn(false);
            return whitelistService;
        }

        @Bean
        RedisService redisService() {
            RedisService redisService = mock(RedisService.class);
            given(redisService.isBruteforceByLogin(LOGIN_TIME)).willReturn(false, false, true);
            given(redisService.isBruteforceByLogin(LOGIN)).willReturn(false);
            given(redisService.isBruteforceByPassword(PASSWORD)).willReturn(false);
            given(redisService.isBruteforceByPassword(PASSWORD_BRUTEFORCE)).willReturn(true);
            given(redisService.isBruteforceByIp(IP_1)).willReturn(false);
            given(redisService.isBruteforceByIp(IP_BRUTEFORCE)).willReturn(true);
            return redisService;
        }

        @Bean
        AntiBruteforceService antiBruteforceService() {
            return new AntiBruteforceServiceImpl(
                    redisService(),
                    blacklistService(),
                    whitelistService()
            );
        }
    }

    @Autowired
    private AntiBruteforceService antiBruteforceService;

    @Test
    void isBruteforceTest() {
        assertThat(antiBruteforceService.isBruteforce(LOGIN_TIME, PASSWORD, IP_1)).isFalse();
        assertThat(antiBruteforceService.isBruteforce(LOGIN_TIME, PASSWORD, IP_1)).isFalse();
        assertThat(antiBruteforceService.isBruteforce(LOGIN_TIME, PASSWORD, IP_1)).isTrue(); // by login

        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_1)).isFalse();
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD_BRUTEFORCE, IP_1)).isTrue(); // by password
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_BRUTEFORCE)).isTrue(); // by ip
    }

    @Test
    void whitelistTest() {
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_IN_WHITELIST)).isFalse();
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_IN_WHITELIST)).isFalse();
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_IN_WHITELIST)).isFalse();
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_SIMPLE_NOT_LIST)).isFalse();
    }

    @Test
    void blacklistTest() {
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_IN_BLACKLIST)).isTrue();
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_IN_BLACKLIST)).isTrue();
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_IN_BLACKLIST)).isTrue();
        assertThat(antiBruteforceService.isBruteforce(LOGIN, PASSWORD, IP_SIMPLE_NOT_LIST)).isFalse();
    }
}
