package ru.otus.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class Ioc {
    private Ioc() {}

    static MyInterface createMyClass() {
        InvocationHandler handler = new MyInvocationHandler(new MyInterfaceImpl());
        return (MyInterface) Proxy.newProxyInstance(Ioc.class.getClassLoader(),
                new Class<?>[]{MyInterface.class}, handler);
    }

    static class MyInvocationHandler implements InvocationHandler {
        private final MyInterface myClassObject;

        MyInvocationHandler(MyInterface myClassObject) {
            this.myClassObject = myClassObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (methodNeedLog(getSameClassMethod(method))) {
                System.out.println("\nexecuted method: " + method.getName() + ", params: " + getArgsToString(args));
            }
            return method.invoke(myClassObject, args);
        }

        private Method getSameClassMethod(Method method) {
            Class<?> myClassClass = myClassObject.getClass();
            try {
                return myClassClass.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return null;
        }

        private boolean methodNeedLog(Method method) {
            if (!(method == null)) {
                return method.isAnnotationPresent(Log.class);
            }
            return false;
        }

        private String getArgsToString(Object[] args) {
            if (args == null) {
                return "no params";
            } else {
                StringBuilder str = new StringBuilder();
                for (Object o : args) {
                    if(o.getClass().isArray()) {
                        str.append(Arrays.toString((Object[]) o));
                    } else {
                        str.append(o);
                    }
                    str.append(", ");
                }
                return str.toString();
            }
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" +
                    "myClassObject = " + myClassObject +
                    '}';
        }
    }
}
