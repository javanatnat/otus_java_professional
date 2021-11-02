package ru.calculator;

public class Data {
    private final Integer value;

    public Data(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public int getPrimitiveValue() {
        return value.intValue();
    }
}
