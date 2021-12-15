package ru.otus.listener.homework;

import ru.otus.listener.Listener;
import ru.otus.model.Message;
import java.util.Optional;

public class HistoryListener implements Listener, HistoryReader {

    private final HistoryMessage historyMessage;

    public HistoryListener() {
        historyMessage = new HistoryMessage();
    }

    @Override
    public void onUpdated(Message msg) {
        historyMessage.addMessage(msg);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        Message findMessage = historyMessage.getMessage(id);
        return (findMessage == null) ? Optional.empty() : Optional.of(new Message(findMessage));
    }
}
