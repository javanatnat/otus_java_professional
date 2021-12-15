package ru.otus.listener.homework;

import ru.otus.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HistoryMessage {
    private final Map<Long, Message> history;

    public HistoryMessage() {
        history = new HashMap<>();
    }

    public void addMessage(Message message) {
        Objects.requireNonNull(message);
        history.put(message.getId(), new Message(message));
    }

    public Message getMessage(long id) {
        return history.get(id);
    }
}
