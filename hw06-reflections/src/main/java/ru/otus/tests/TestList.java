package ru.otus.tests;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestList {
    private List<Integer> example;

    @Before
    void before() {
        System.out.println("before");
        example = new ArrayList<>();
        initExample();
    }

    private void initExample() {
        example.add(1);
        example.addAll(List.of(6,5,4,3,2,1,0));
    }

    @Test
    private void testAdd() {
        System.out.println("testAdd");

        example.clear();
        initExample();

        // example = 1,6,5,4,3,2,1,0
        if (!(example.size() == 8)) throw new AssertionError();
    }

    @Test
    void testRemove() {
        System.out.println("testRemove");

        // example = 1,6,5,4,3,2,1,0
        example.remove(1);
        // example = 1,5,4,3,2,1,0
        example.remove(2);
        // example = 1,5,3,2,1,0
        example.remove(3);
        // example = 1,5,3,1,0

        if (!(example.size() == 5)) throw new AssertionError();
        if (!example.contains(1))   throw new AssertionError();
        if (example.contains(2))    throw new AssertionError();
        if (!(example.contains(3))) throw new AssertionError();
    }

    @Test
    void testRemoveByIndex() {
        System.out.println("testRemoveByIndex");

        // example = 1,6,5,4,3,2,1,0
        example.remove((Integer) 1);
        // example = 6,5,4,3,2,1,0
        example.remove((Integer) 2);
        // example = 6,5,4,3,1,0
        example.remove((Integer) 3);
        // example = 6,5,4,1,0

        if (!(example.size() == 5)) throw new AssertionError();
        if (!(example.contains(1))) throw new AssertionError();
        if (example.contains(2))    throw new AssertionError();
        if (example.contains(3))    throw new AssertionError();
    }

    @After
    void after() {
        System.out.println("after");
    }

}
