package ru.otus.proxy;

public class MyInterfaceImpl implements MyInterface{
    @Override
    public String toString() {
        return "MyInterfaceImpl{}";
    }

    @Override
    public void func1() {
        System.out.println("\nMyInterfaceImpl.func1, 0p (no log)");
    }

    @Log
    @Override
    public void func1(int i) {
        System.out.println("MyInterfaceImpl.func1, 1p = " + i);
    }

    @Override
    public void func1(int i, int j) {
        System.out.println("\nMyInterfaceImpl.func1, 2p (no log)");
    }

    @Log
    @Override
    public void func1(int i, int j, Class<?> clazz) {
        System.out.println("MyInterfaceImpl.func1, 3p clazz");
        func1(i,j,10);
    }

    @Log
    public void func1(int i, int j, int k) {
        System.out.println("MyInterfaceImpl.func1, 3p");
    }

    @Log
    @Override
    public void func2(String str) {
        System.out.println("MyInterfaceImpl.func2, 1p");
        int i = func2(str, 10);
    }

    @Log
    public int func2(String str, int i) {
        System.out.println("MyInterfaceImpl.func2, 2p");
        return 0;
    }

    @Override
    public int func3(Object o) {
        System.out.println("\nMyInterfaceImpl.func3 (no log)");
        return 0;
    }

    @Log
    @Override
    public void func4(String... strings) {
        System.out.println("MyInterfaceImpl.func4");
        funcS(this);
    }

    static void funcS(MyInterface o) {
        System.out.println("\nMyInterfaceImpl.funcS (no log)");
        o.func1(10);
    }
}
