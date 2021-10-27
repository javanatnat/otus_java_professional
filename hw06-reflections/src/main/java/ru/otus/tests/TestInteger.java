package ru.otus.tests;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class TestInteger {
    @Before
    void before() {
        System.out.println("before");
    }

    @Before
    void beforeException() {
        System.out.println("beforeException");
        throw new IllegalArgumentException();
    }

    @Before
    static void beforeStatic() {
        System.out.println("beforeStatic");
    }

    @Before
    void beforeAgain() {
        System.out.println("beforeAgain");
    }

    void beforeNotAnnotate() {
        System.out.println("beforeNotAnnotate");
    }

    @Test
    void testGetLowBit() {
        System.out.println("testGetLowBit");

        int a = Integer.lowestOneBit(-1);
        int b = Integer.lowestOneBit(1);

        if (!(a == 1)) throw new AssertionError();
        if (!(b == 1)) throw new AssertionError();
    }

    @Test
    private void testGetHighBit() {
        System.out.println("testGetHighBit");

        int a = Integer.highestOneBit(-1);
        int b = Integer.highestOneBit(1);

        if (!(a == Integer.MIN_VALUE)) throw new AssertionError();
        if (!(b == 1))                 throw new AssertionError();
    }

    @Test
    void testException() {
        System.out.println("testException");
        throw new ArithmeticException();
    }

    @Test
    static void testStatic() {
        System.out.println("testStatic");
    }

    void testNotAnnotate() {
        System.out.println("testNotAnnotate");
    }

    @Test
    int testReturnInt() {
        System.out.println("testReturnInt");
        return 0;
    }

    @Test
    void testHasParams(int param) {
        System.out.println("testHasParams");
    }

    @After
    void after() {
        System.out.println("after");
    }

    @After
    void afterException() {
        System.out.println("afterException");
        throw new IllegalStateException();
    }

    @After
    static void afterStatic() {
        System.out.println("afterStatic");
    }

    void afterNotAnnotate() {
        System.out.println("afterNotAnnotate");
    }

    @Before
    @Test
    @After
    private void beforeAfterTest() {
        System.out.println("beforeAfterTest");
    }
}
