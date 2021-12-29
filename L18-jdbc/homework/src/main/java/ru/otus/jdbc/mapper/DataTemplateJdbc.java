package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor,
            EntitySQLMetaData entitySQLMetaData,
            EntityClassMetaData<T> entityClassMetaData
    ) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect
                (connection,
                        entitySQLMetaData.getSelectByIdSql(),
                        Collections.singletonList(id),
                        getHandlerFindById());
    }

    private Function<ResultSet, T> getHandlerFindById() {
        return rs -> {
            try {
                if (rs.next()) {
                    return getResult(rs);
                }
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
            return null;
        };
    }

    private T getResult(ResultSet rs) {
        try {
            Constructor<T> constructor = entityClassMetaData.getConstructor();
            return constructor.newInstance(getParamsFromResult(rs));

        }  catch (InvocationTargetException |
                InstantiationException |
                IllegalAccessException e
        ) {
                throw new DataTemplateException(e);
        }
    }

    private Object[] getParamsFromResult(ResultSet rs) {
        try {
            int columnCount = rs.getMetaData().getColumnCount();
            Object[] constructorParams = new Object[columnCount];

            for (int i = 1, k = 0; i <= columnCount; i++, k++) {
                constructorParams[k] = rs.getObject(i);
            }

            return constructorParams;

        } catch (SQLException e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect
                (connection,
                        entitySQLMetaData.getSelectAllSql(),
                        Collections.emptyList(),
                        getHandlerFindAll())
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    private Function<ResultSet, List<T>> getHandlerFindAll() {
        return rs -> {
            try {
                List<T> records = new ArrayList<>();
                while (rs.next()) {
                    records.add(getResult(rs));
                }
                return records;

            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        };
    }

    @Override
    public long insert(Connection connection, T object) {
        return dbExecutor.executeStatement
                (connection,
                        entitySQLMetaData.getInsertSql(),
                        getFieldsNoIdValues(object));
    }

    private List<Object> getFieldsNoIdValues(T object) {
        List<Object> values = new ArrayList<>();

        for(Field field : entityClassMetaData.getFieldsWithoutId()) {
            values.add(getFieldValue(field, object));
        }

        return values;
    }

    private Object getFieldValue(Field field, T object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T object) {
        List<Object> params = new ArrayList<>(getFieldsNoIdValues(object));
        params.add(getFieldIdValue(object));

        dbExecutor.executeStatement
                (connection,
                        entitySQLMetaData.getUpdateSql(),
                        params);
    }

    private Object getFieldIdValue(T object) {
        return getFieldValue(entityClassMetaData.getIdField(), object);
    }
}
