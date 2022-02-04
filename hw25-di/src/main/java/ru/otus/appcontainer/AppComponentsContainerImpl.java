package ru.otus.appcontainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private static final Logger logger = LoggerFactory.getLogger(AppComponentsContainerImpl.class);

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        try {
            Object config = getConfigInstance(configClass);
            List<Method> methods = getConfigMethods(configClass);

            for (Method m : methods) {
                processMethod(m, config);
            }

        } catch (NoSuchMethodException | InvocationTargetException
                | InstantiationException | IllegalAccessException e) {
            throw new ProcessConfigException("process config exception", e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private Object getConfigInstance(Class<?> configClass) throws
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException,
            NoSuchMethodException
    {
        Constructor<?> constructor = configClass.getConstructor();
        return constructor.newInstance();
    }

    private List<Method> getConfigMethods(Class<?> configClass) {
        Method[] methods = configClass.getDeclaredMethods();
        return Arrays.stream(methods)
                .filter(m -> methodIsComponent(m))
                .sorted(getComponentComparator())
                .toList();
    }

    private static boolean methodIsComponent(Method m) {
        return m.isAnnotationPresent(AppComponent.class);
    }

    private Comparator<Method> getComponentComparator() {
        return Comparator.comparingInt(m -> m.getAnnotation(AppComponent.class).order());
    }

    private void processMethod(Method method, Object config) throws
            InvocationTargetException,
            IllegalAccessException
    {
        Class<?>[] paramClasses = method.getParameterTypes();
        Object[] parameters = getMethodParameters(paramClasses);
        Object component = method.invoke(config, parameters);
        setComponent(component, method);
    }

    private Object[] getMethodParameters(Class<?>[] paramClasses) {
        int size = paramClasses.length;
        Object[] parameters = new Object[size];

        for (int i = 0; i < size; i++) {
            Object object = getAppComponent(paramClasses[i]);
            if (object == null) {
                throw new ProcessConfigException("app component ( class = " + paramClasses[i].getName()
                        + " ) not exists");
            }
            parameters[i] = object;
        }

        return parameters;
    }

    private void setComponent(Object component, Method method) {
        String nameComponent = getComponentName(method);
        checkComponent(nameComponent);

        appComponents.add(component);
        appComponentsByName.put(nameComponent, component);
        logger.info("add component: name={}, class={}", nameComponent, component.getClass().getName());
    }

    private String getComponentName(Method method) {
        return method.getAnnotation(AppComponent.class).name();
    }

    private void checkComponent(String nameComponent) {
        if (appComponentsByName.containsKey(nameComponent)) {
            throw new ProcessConfigException("component ( name = " + nameComponent + " ) already exists");
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        for( Object component : appComponents ) {
            if (componentClass.isInstance(component)) {
                return componentClass.cast(component);
            }
        }
        return null;
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        @SuppressWarnings("unchecked") C component = (C) appComponentsByName.get(componentName);
        return component;
    }
}
