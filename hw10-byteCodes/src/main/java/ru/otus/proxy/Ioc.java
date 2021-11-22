package ru.otus.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Ioc {
    private Ioc() {}

    static MyInterface getProxyObject(MyInterface impl) {
        InvocationHandler handler = new MyInvocationHandler(impl);
        return (MyInterface) Proxy.newProxyInstance(Ioc.class.getClassLoader(),
                new Class<?>[]{MyInterface.class}, handler);
    }

    static class MyInvocationHandler implements InvocationHandler {
        private final MyInterface myClassObject;
        private final Set<String> logClassMethods;

        MyInvocationHandler(MyInterface myClassObject) {
            this.myClassObject = myClassObject;
            this.logClassMethods = getLogClassMethods(myClassObject.getClass());
        }

        private static Set<String> getLogClassMethods(Class<?> clazz) {
            Set<String> logMethods = new HashSet<>();

            for(Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Log.class)) {
                    logMethods.add(getSignatureMethod(m));
                }
            }

            return logMethods;
        }

        private static String getSignatureMethod(Method m) {
            return m.getName() + Arrays.toString(m.getParameterTypes());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String signatureMethod = getSignatureMethod(method);
            if (logClassMethods.contains(signatureMethod)) {
                System.out.println("\nexecuted method: " + method.getName() + ", params: " + getArgsToString(args));
            }
            return method.invoke(myClassObject, args);
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
