package ru.otus.reflection;

import ru.otus.tests.TestInteger;
import ru.otus.tests.TestList;

public class TestApp {

    public static void main(String[] args) {
        String classNotExist = "ru.otus.tests.TestMap";

        runTests(classNotExist);
        runTests(TestInteger.class.getName());
        runTests(TestList.class.getName());
    }

    private static void runTests(String className) {
        System.out.println("\n---------------------------------------------");
        System.out.println("className = " + className);

        try {
            TestExecutor testExecutor = new TestExecutor(className);

            testExecutor.runTests();
            testExecutor.runTests();// Повторный запуск тестов для проверки чистоты выполнения

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
