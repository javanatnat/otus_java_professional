package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lib.SensorDataBufferedWriter;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static ru.otus.api.model.SensorData.sensorDataNaturalOrder;

// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final BlockingQueue<SensorData> queue;
    private final SensorDataBufferedWriter writer;
    private final int bufferSize;

    private volatile int flag = 0;
    private static final AtomicIntegerFieldUpdater<SensorDataProcessorBuffered> FLAG_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SensorDataProcessorBuffered.class, "flag");

    public SensorDataProcessorBuffered(
            int bufferSize,
            SensorDataBufferedWriter writer
    ) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.queue = new PriorityBlockingQueue<>(bufferSize, sensorDataNaturalOrder());
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
                if (!queue.isEmpty()) {
                    log.info("write: buffer size = " + queue.size());
                    List<SensorData> buffer = new ArrayList<>(queue.size());
                    queue.drainTo(buffer);
                    writer.writeBufferedData(buffer);
                    queue.clear();
                    log.info("SUCCESS finish write");
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
        return queue.offer(data);
    }

    private boolean isFull() {
        return queue.size() >= bufferSize;
    }
}
