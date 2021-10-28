package ru.otus.reflection;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TestExecutor {
    private final Class<?> clazz;
    private final Constructor<?> constructor;
    private final List<Method> beforeMethods;
    private final List<Method> testMethods;
    private final List<Method> afterMethods;

    private int passed;
    private int total;

    public TestExecutor(String className) throws ClassNotFoundException, NoSuchMethodException {
        clazz = Class.forName(className);
        constructor = clazz.getConstructor();

        beforeMethods = new ArrayList<>();
        testMethods   = new ArrayList<>();
        afterMethods  = new ArrayList<>();

        passed = 0;
        total = 0;
    }

    public void runTests() {
        clearDataBeforeRun();
        initMethods(clazz);

        executeTests();
        printStatistic();
    }

    private void clearDataBeforeRun() {
        clearMethods();

        passed = 0;
        total = 0;
    }

    private void clearMethods() {
        beforeMethods.clear();
        testMethods.clear();
        afterMethods.clear();
    }

    private void initMethods(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (methodNeedSkip(method)) {
                if (methodIsTest(method)) {
                    total++;
                    printErrorTestBeforeStart(method);
                }
                continue;
            }

            if (methodIsTest(method))   testMethods.add(method);
            if (methodIsBefore(method)) beforeMethods.add(method);
            if (methodIsAfter(method))  afterMethods.add(method);
        }
    }

    private static boolean methodNeedSkip(Method method) {
        return methodIsStatic(method)
                || !methodHasVoidReturnType(method)
                || methodHasParams(method);
    }

    private static boolean methodIsStatic(Method method) {
        int modifiers = method.getModifiers();
        return Modifier.isStatic(modifiers);
    }

    private static boolean methodHasVoidReturnType(Method method) {
        return void.class.isAssignableFrom(method.getReturnType());
    }

    private static boolean methodHasParams(Method method) {
        return method.getParameterCount() > 0;
    }

    private static boolean methodIsTest(Method method) {
        return method.isAnnotationPresent(Test.class);
    }

    private static void printErrorTestBeforeStart(Method method) {
        String testName = method.getName();
        System.out.println("\n" + testName);
        System.out.println("\t" + testName + " - error");
    }

    private static boolean methodIsBefore(Method method) {
        return method.isAnnotationPresent(Before.class);
    }

    private static boolean methodIsAfter(Method method) {
        return method.isAnnotationPresent(After.class);
    }

    private void executeTests() {
        for(Method test : testMethods) {
            total++;
            System.out.println("");
            try {
                Object testObject = constructor.newInstance();

                runMethods(beforeMethods, testObject);
                try {
                    runMethod(test, testObject);
                } finally {
                    runMethods(afterMethods, testObject);
                }

                passed++;
                System.out.println("\t" + test.getName() + " - success");

            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                System.out.println("\t" + test.getName() + " - error");
            }
        }
    }

    private void runMethods(
            List<Method> methods,
            Object testObject
    ) throws InvocationTargetException, IllegalAccessException {
        for(Method before : methods) {
            runMethod(before, testObject);
        }
    }

    private static void runMethod(
            Method method,
            Object testObject
    ) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        method.invoke(testObject);
    }

    public void printStatistic() {
        System.out.println("\nСтатистика выполнения тестов для " + clazz.getName() + " :");
        System.out.println("всего тестов - " + total);
        System.out.println("успешно выполнено - " + passed);
        System.out.println("ошибки при выполнении - " + (total - passed));
    }
}
