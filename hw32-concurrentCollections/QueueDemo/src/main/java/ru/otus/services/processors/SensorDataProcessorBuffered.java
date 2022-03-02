package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lib.SensorDataBufferedWriter;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;

import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.otus.api.model.SensorData.sensorDataNaturalOrder;

// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final SensorDataBufferedWriter writer;
    private final int bufferSize;
    private final Lock lock = new ReentrantLock();

    private SensorData[] queue;
    private volatile int size = 0;
    private volatile int flag = 0;
    private static final AtomicIntegerFieldUpdater<SensorDataProcessorBuffered> FLAG_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SensorDataProcessorBuffered.class, "flag");

    public SensorDataProcessorBuffered(
            int bufferSize,
            SensorDataBufferedWriter writer
    ) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.queue = new SensorData[bufferSize];
    }

    @Override
    public void process(SensorData data) {
        if (dataIsNotCorrect(data)) {
            log.warn("incorrect data");
            return;
        }

        if (offer(data)) {
            if (isFull()) {
                flush();
            }
        }
    }

    public void flush() {
        try {
            if (FLAG_UPDATER.compareAndSet(this, 0, 1)) {
                if (isNotEmpty()) {
                    writer.writeBufferedData(Arrays.asList(getSortData()));
                    clearData();
                }
                FLAG_UPDATER.compareAndSet(this, 1, 0);
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }

    private boolean dataIsNotCorrect(SensorData data) {
        return (data.getValue() == null || data.getValue().isNaN());
    }

    private boolean offer(SensorData data) {
        if (isFull()) {
            return false;
        }
        lock.lock();
        try {
            queue[size] = data;
            size++;
            return true;
        } finally {
            lock.unlock();
        }
    }

    private boolean isFull() {
        return size >= bufferSize;
    }

    private boolean isNotEmpty() {
        return size > 0;
    }

    private SensorData[] getSortData() {
        if (isFull()) {
            sortData(queue);
            return queue;
        }

        SensorData[] writeData = getNotEmptyPartData();
        sortData(writeData);
        return writeData;
    }

    private void sortData(SensorData[] sensorData) {
        Arrays.sort(sensorData, sensorDataNaturalOrder());
    }

    private SensorData[] getNotEmptyPartData() {
        if (size > 0) {
            return Arrays.copyOfRange(queue, 0, size);
        }
        return new SensorData[0];
    }

    private void clearData() {
        lock.lock();
        try {
            queue = new SensorData[bufferSize];
            size = 0;
        } finally {
            lock.unlock();
        }
    }
}
