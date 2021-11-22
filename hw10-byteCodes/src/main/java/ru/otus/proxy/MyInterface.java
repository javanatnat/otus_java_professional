package ru.otus.proxy;

public interface MyInterface {
    @Log
    void func1();
    void func1(int i);
    void func1(int i, int j);
    void func1(int i, int j, Class<?> clazz);
    void func2(String str);
    int  func3(Object o);
    void func4(String... strings);

    default void func5() {
        System.out.println("MyInterface.func5");
    }

    static void funcS(MyInterface o) {
        System.out.println("MyInterface.funcS");
        o.func1(100, 200);
        o.func1(500);
    }
}
