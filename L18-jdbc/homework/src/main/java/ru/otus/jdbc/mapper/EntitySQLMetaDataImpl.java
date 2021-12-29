package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData{

    private static final String SPACE = " ";
    private static final String SELECT = "SELECT ";
    private static final String FROM = "FROM ";
    private static final String WHERE = "WHERE ";
    private static final String INSERT = "INSERT ";
    private static final String UPDATE = "UPDATE ";
    private static final String SET = "SET ";
    private static final String INTO = "INTO ";
    private static final String VALUES = "VALUES";
    private static final String EQUAL_WILDCARD = " = ?";
    private static final String LEFT_BKT = " ( ";
    private static final String RIGHT_BKT = " ) ";
    private static final String WILDCARD = "?";
    private static final String COMMA = ",";

    private final EntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return SELECT + getAllFields() + SPACE + FROM + getTableName();
    }

    private String getAllFields() {
        return getFieldsNames(entityClassMetaData.getAllFields());
    }

    private String getFieldsNames(List<Field> fields) {
        return fields
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(","));
    }

    private String getTableName() {
        return entityClassMetaData.getName();
    }

    @Override
    public String getSelectByIdSql() {
        return getSelectAllSql() + SPACE + WHERE + getIdEqualParamCondition();
    }

    private String getIdEqualParamCondition() {
        return getIdName() + EQUAL_WILDCARD;
    }

    private String getIdName() {
        return entityClassMetaData.getIdField().getName();
    }

    @Override
    public String getInsertSql() {
        return INSERT + INTO + getTableName() + LEFT_BKT + getFieldsNoId() + RIGHT_BKT
                + VALUES + LEFT_BKT + getAllFieldsNoIdWildcards() + RIGHT_BKT;
    }

    private String getFieldsNoId() {
        return getFieldsNames(entityClassMetaData.getFieldsWithoutId());
    }

    private String getAllFieldsNoIdWildcards() {
        int countFields = entityClassMetaData.getFieldsWithoutId().size();
        return getWildcards(countFields);
    }

    private String getWildcards(int countFields) {
        if ( countFields == 1 ) {
            return WILDCARD;
        } else if ( countFields > 1) {
            String repeat = WILDCARD + ",";
            return repeat.repeat(countFields - 1) + WILDCARD;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getUpdateSql() {
        return UPDATE + getTableName() + SPACE + SET
                + getFieldsNoIdEqualWildcards() + WHERE + getIdEqualParamCondition();
    }

    private String getFieldsNoIdEqualWildcards() {
        List<Field> fields = entityClassMetaData.getFieldsWithoutId();
        int countFields = fields.size();
        int lastIndex = countFields - 1;

        if (countFields <= 0) throw new IllegalArgumentException();

        StringBuilder builder = new StringBuilder();

        if (lastIndex > 0) {
            for(int i = 0; i < lastIndex; i++) {
                addFieldToBuilderWithComma(fields.get(i), builder);
            }
        }
        addFieldToBuilder(fields.get(lastIndex), builder);

        return builder.toString();
    }

    private void addFieldToBuilderWithComma(Field field, StringBuilder builder) {
        addFieldToBuilder(field, builder);
        builder.append(COMMA);
    }

    private void addFieldToBuilder(Field field, StringBuilder builder) {
        builder.append(field.getName());
        builder.append(EQUAL_WILDCARD);
    }
}
