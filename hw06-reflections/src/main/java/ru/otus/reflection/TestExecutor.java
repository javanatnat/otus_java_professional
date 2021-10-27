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
        passed = 0;
        total = 0;
    }

    private void initMethods(Class<?> clazz) {
        clearMethods();

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

    private void clearMethods() {
        beforeMethods.clear();
        testMethods.clear();
        afterMethods.clear();
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

                runBefore(testObject);
                runTest(test, testObject);
                runAfter(testObject);

            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
//                    e.printStackTrace();
            }
        }
    }

    private void runBefore(Object testObject) {
        for(Method before : beforeMethods) {
            runBeforeAfterMethod(before, testObject);
        }
    }

    private static void runBeforeAfterMethod(
            Method method,
            Object testObject
    ) {
        method.setAccessible(true);

        try {
            method.invoke(testObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
//            e.printStackTrace();
        }
    }

    private void runTest(
            Method test,
            Object testObject
    ) {
        test.setAccessible(true);

        try {
            test.invoke(testObject);
            passed++;
            System.out.println("\t" + test.getName() + " - success");

        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("\t" + test.getName() + " - error");
        }
    }

    private void runAfter(Object testObject) {
        for (Method after : afterMethods) {
            runBeforeAfterMethod(after, testObject);
        }
    }

    public void printStatistic() {
        System.out.println("\nСтатистика выполнения тестов для " + clazz.getName() + " :");
        System.out.println("всего тестов - " + total);
        System.out.println("успешно выполнено - " + passed);
        System.out.println("ошибки при выполнении - " + (total - passed));
    }
}
