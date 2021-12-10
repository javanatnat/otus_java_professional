package ru.otus.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectForMessage {
    private List<String> data;

    public ObjectForMessage() {
        this.data = new ArrayList<>();
    }

    public ObjectForMessage(List<String> data) {
        this.data = new ArrayList<>(data);
    }

    public ObjectForMessage(ObjectForMessage objectForMessage) {
        this(objectForMessage.getData());
    }

    public List<String> getData() {
        return new ArrayList<>(data);
    }

    public void setData(List<String> data) {
        this.data = new ArrayList<>(data);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
