package ru.otus.processor.homework;

import java.time.LocalDateTime;

public class DateTimeNowOdd implements DateTimeProvider{
    @Override
    public LocalDateTime getDate() {
        LocalDateTime now = LocalDateTime.now();
        if (DateTimeFactory.timeSecondIsEven(now)) {
            return now.plusSeconds(1);
        }
        return now;
    }
}
