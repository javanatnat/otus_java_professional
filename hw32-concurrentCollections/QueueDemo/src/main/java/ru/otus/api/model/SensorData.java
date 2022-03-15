package ru.otus.api.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class SensorData {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final LocalDateTime measurementTime;
    private final String room;
    private final Double value;

    public SensorData(LocalDateTime measurementTime, String room, Double value) {
        this.measurementTime = measurementTime;
        this.room = room;
        this.value = value;
    }

    public LocalDateTime getMeasurementTime() {
        return measurementTime;
    }

    public String getRoom() {
        return room;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "measurementTime = " + measurementTime.format(FORMATTER)
                + ", room = " + room + ", value = " + value;
    }

    public static Comparator<SensorData> sensorDataNaturalOrder() {
        return Comparator.comparing(SensorData::getMeasurementTime);
    }
}
