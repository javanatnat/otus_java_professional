package ru.otus.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainersConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestContainersConfig.class);

    public static String JDBC_URL = "app.datasource.demo-db.jdbcUrl";
    public static String DB_LOGIN = "app.datasource.demo-db.username";
    public static String DB_PASSWORD = "app.datasource.demo-db.password";

    public static class CustomPostgreSQLContainer extends PostgreSQLContainer<CustomPostgreSQLContainer> {
        private static CustomPostgreSQLContainer container;
        private static final String IMAGE_VERSION = "postgres:12";

        public CustomPostgreSQLContainer() {
            super(IMAGE_VERSION);
        }

        public static CustomPostgreSQLContainer getInstance() {
            if (container == null) {
                container = new CustomPostgreSQLContainer();
            }
            return container;
        }

        @Override
        public void start() {
            super.start();

            String url = getUrl();

            System.setProperty(JDBC_URL, url);
            System.setProperty(DB_LOGIN, container.getUsername());
            System.setProperty(DB_PASSWORD, container.getPassword());

            logger.info("postgres in docker started: url={}", url);
        }

        @Override
        public void stop() {
            logger.info("postgres in docker was stopped: url={}", getUrl());
            super.stop();
        }

        private String getUrl() {
            return container.getJdbcUrl() + "&stringtype=unspecified";
        }
    }
}
