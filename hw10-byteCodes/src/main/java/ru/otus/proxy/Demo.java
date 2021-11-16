package ru.otus.proxy;

public class Demo {
    public static void main(String[] args) {
        MyInterface myInterfaceObject = Ioc.createMyClass();
        System.out.println("myInterfaceObject.class = " + myInterfaceObject.getClass().getName());
        System.out.println("");

        myInterfaceObject.func1();
        myInterfaceObject.func1(100);
        myInterfaceObject.func1(1,2);
        myInterfaceObject.func1(1,2,myInterfaceObject.getClass());
        myInterfaceObject.func2("Hello");
        int r = myInterfaceObject.func3(new MyInterfaceImpl());
        myInterfaceObject.func4("Hello", "Hi");
        myInterfaceObject.func4("Good");
        myInterfaceObject.func5();

        MyInterface.funcS(myInterfaceObject);
    }
}
