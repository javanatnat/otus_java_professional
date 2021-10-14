package ru.otus;

import java.util.List;

/**
 *
 * To start the application:
 * ./gradlew build
 *
 * To unzip the jar:
 * unzip -l ./L02-gradle2/build/libs/L02-gradle2-libApiUse.jar
 *
 */
public class App {
    public static void main(String... args) {
        System.out.println("I am in L02-gradle2-libApiUse module!");

        // Видим класс из модуля L02-gradle2-libApi
        AppLibApi.main();

        System.out.println("I am still in L02-gradle2-libApiUse module!");
        // Случай 1
        // в модуле L02-gradle2-libApi стоит api 'com.google.guava:guava'
        // и тут guava доступна, т.е. зависимость "протекла"
        List<Integer> ints = List.of(1,3,5);
        System.out.println(com.google.common.collect.Lists.reverse(ints));

        // Случай 2
        // в модуле L02-gradle2-libApi стоит implementation 'com.google.guava:guava'
        // и тут guava НЕ доступна, т.е. зависимость "изолирована"
        //System.out.println(com.google.common.collect.Lists.reverse(List.of(1,3,5)));
    }
}
