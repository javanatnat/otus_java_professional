package ru.otus.antibruteforce.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitConfig(RedisServiceTest.TestRedisConfiguration.class)
public class RedisServiceTest {

    @Value( "${countAttempt.login:0}" )
    private Integer loginCountPerMinute;

    @Value( "${countAttempt.password:0}" )
    private Integer passwordCountPerMinute;

    @Value( "${countAttempt.ip:0}" )
    private Integer ipCountPerMinute;

    @TestConfiguration
    @TestPropertySource(properties = { "spring.config.location=classpath:application.yml" })
    static class TestRedisConfiguration {

        private final int redisPort;
        private final String redisHost;
        private final RedisServer redisServer;

        public TestRedisConfiguration() {
            this.redisPort = 6379;
            this.redisHost = "localhost";
            this.redisServer = RedisServer.builder().port(redisPort).build();
        }

        @PostConstruct
        public void postConstruct() {
            redisServer.start();
        }

        @PreDestroy
        public void preDestroy() {
            redisServer.stop();
        }

        @Bean
        public LettuceConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory(redisHost, redisPort);
        }

        @Bean
        RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(connectionFactory);
            redisTemplate.setEnableTransactionSupport(true);

            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new StringRedisSerializer());

            connectionFactory.getConnection().flushDb();
            return redisTemplate;
        }

        @Bean
        RedisService redisService() {
            return new RedisServiceImpl();
        }
    }

    private static final String LOGIN = "login";
    private static final String PASSWORD = "012345678";
    private static final String IP = "128.11.0.1";

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisService redisService;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void isBruteforceByLoginTest() {
        if (loginCountPerMinute > 0) {
            for (int i = 0; i < (loginCountPerMinute - 1); i++) {
                assertThat(redisService.isBruteforceByLogin(LOGIN)).isFalse();
            }
            assertThat(redisService.isBruteforceByLogin(LOGIN)).isTrue();
        }
    }

    @Test
    void isBruteforceByPasswordTest() {
        if (passwordCountPerMinute > 0) {
            for (int i = 0; i < (passwordCountPerMinute - 1); i++) {
                assertThat(redisService.isBruteforceByPassword(PASSWORD)).isFalse();
            }
            assertThat(redisService.isBruteforceByPassword(PASSWORD)).isTrue();
        }
    }

    @Test
    void isBruteforceByIpTest() {
        if (ipCountPerMinute > 0) {
            for (int i = 0; i < (ipCountPerMinute - 1); i++) {
                assertThat(redisService.isBruteforceByIp(IP)).isFalse();
            }
            assertThat(redisService.isBruteforceByIp(IP)).isTrue();
        }
    }

    @Test
    void cleanByLoginIpTest() {
        if (loginCountPerMinute > 0) {
            for (int i = 0; i < (loginCountPerMinute - 1); i++) {
                assertThat(redisService.isBruteforceByLogin(LOGIN)).isFalse();
            }
            assertThat(redisService.isBruteforceByLogin(LOGIN)).isTrue();
            assertThat(redisService.isBruteforceByLogin(LOGIN)).isTrue();
        }

        if (ipCountPerMinute > 0) {
            for (int i = 0; i < (ipCountPerMinute - 1); i++) {
                assertThat(redisService.isBruteforceByIp(IP)).isFalse();
            }
            assertThat(redisService.isBruteforceByIp(IP)).isTrue();
            assertThat(redisService.isBruteforceByIp(IP)).isTrue();
        }

        redisService.cleanByLoginIp(LOGIN, IP);

        if (loginCountPerMinute > 1) {
            assertThat(redisService.isBruteforceByLogin(LOGIN)).isFalse();
        }

        if (ipCountPerMinute > 1) {
            assertThat(redisService.isBruteforceByIp(IP)).isFalse();
        }
    }
}
