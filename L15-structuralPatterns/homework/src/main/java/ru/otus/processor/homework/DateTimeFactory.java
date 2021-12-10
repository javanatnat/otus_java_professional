package ru.otus.processor.homework;

import java.time.LocalDateTime;

public class DateTimeFactory {
    public static DateTimeProvider getProvider(DateTimeProviderType providerType) {
        if (providerType == DateTimeProviderType.EVEN) {
            return new DateTimeNowEven();
        }
        if (providerType == DateTimeProviderType.ODD) {
            return new DateTimeNowOdd();
        }
        throw new IllegalArgumentException("unknown provider type: " + providerType);
    }

    static boolean timeSecondIsEven(LocalDateTime dateTime) {
        return (dateTime.getSecond()%2 == 0);
    }
}
