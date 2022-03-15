package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lib.SensorDataBufferedWriter;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static ru.otus.api.model.SensorData.sensorDataNaturalOrder;

// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final BlockingQueue<SensorData> queue;
    private final SensorDataBufferedWriter writer;
    private final int bufferSize;

    public SensorDataProcessorBuffered(
            int bufferSize,
            SensorDataBufferedWriter writer
    ) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.queue = new ArrayBlockingQueue<>(bufferSize);
    }

    @Override
    public synchronized void process(SensorData data) {
        if (dataIsNotCorrect(data)) {
            log.warn("incorrect data");
            return;
        }

        if (!queue.offer(data)) {
            log.warn("lost value: {}", data);
        }

        if (isFull()) {
            flush();
        }
    }

    public synchronized void flush() {
        if (!queue.isEmpty()) {
            log.info("write: buffer size: {}", queue.size());
            List<SensorData> buffer = new ArrayList<>(queue.size());
            queue.drainTo(buffer);
            buffer.sort(sensorDataNaturalOrder());
            writer.writeBufferedData(buffer);
            queue.clear();
            log.info("SUCCESS finish write");
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }

    private boolean dataIsNotCorrect(SensorData data) {
        return (data.getValue() == null || data.getValue().isNaN());
    }

    private boolean isFull() {
        return queue.size() >= bufferSize;
    }
}
