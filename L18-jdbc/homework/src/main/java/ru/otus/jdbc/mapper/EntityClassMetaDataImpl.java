package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplateException;
import ru.otus.crm.model.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T>{

    private final Class<T> clazz;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName().toLowerCase();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            Class<?>[] paramTypes = getConstructorParamTypes();
            return clazz.getConstructor(paramTypes);

        } catch (Exception ex) {
            throw new DataTemplateException(ex);
        }
    }

    private Class<?>[] getConstructorParamTypes() {
        Field[] fields = getClassFields();
        Class<?>[] paramTypes = new Class[fields.length];

        int i = 0;
        for(Field field : fields) {
            paramTypes[i] = field.getType();
            i++;
        }

        return paramTypes;
    }

    private Field[] getClassFields() {
        return clazz.getDeclaredFields();
    }

    @Override
    public Field getIdField() {
        for(Field field : getClassFields()) {
            if (fieldIsId(field)) {
                return field;
            }
        }
        throw new IllegalArgumentException();
    }

    private static boolean fieldIsId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    @Override
    public List<Field> getAllFields() {
        return Arrays.asList(getClassFields());
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return getAllFields()
                .stream()
                .filter(x -> !fieldIsId(x))
                .collect(Collectors.toList());
    }
}
