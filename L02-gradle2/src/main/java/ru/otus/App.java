package ru.otus;

import com.zaxxer.hikari.HikariConfig;

/**
 *
 * To start the application:
 * ./gradlew build
 * java -jar ./L02-gradle2/build/libs/L02-gradle2-uber-0.1.jar
 *
 * To unzip the jar:
 * unzip -l ./L02-gradle2/build/libs/L02-gradle2.jar
 * unzip -l ./L02-gradle2/build/libs/L02-gradle2-uber-0.1.jar
 *
 */
public class App {
    public static void main(String... args) {
        HikariConfig config = new HikariConfig();
        System.out.println(config);
    }
}
