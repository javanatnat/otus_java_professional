package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class TimeExcProcessor implements Processor {
//    Сделать процессор, который будет выбрасывать исключение в четную секунду
    private final DateTimeProvider dateTimeProvider;

    public TimeExcProcessor(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        if(timeSecondIsEven()) {
            throw new RuntimeException();
        }

        return message;
    }

    private boolean timeSecondIsEven() {
        return DateTimeFactory.timeSecondIsEven(dateTimeProvider.getDate());
    }
}
