package ru.otus.antibruteforce.model;

public class Ipv4Type {
    private String value;

    public Ipv4Type() {}

    public Ipv4Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "value=" + value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (! (obj instanceof Ipv4Type ipv4Type)) {
            return false;
        }

        return value.equals(ipv4Type.getValue());
    }
}
