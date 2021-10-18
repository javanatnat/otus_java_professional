package ru.otus;

import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 * To start the application:
 * ./gradlew build
 *
 * To unzip the jar:
 * unzip -l ./L02-gradle2/build/libs/L02-gradle2-libApi.jar
 *
 */
public class AppLibApi {
    public static void main(String... args) {
        System.out.println("I am from L02-gradle2-libApi module!");
        System.out.println(Lists.reverse(List.of(1,25,5)));
    }
}
