package ru.otus.dataprocessor;

import ru.otus.model.Measurement;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.*;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        return data.stream()
                .collect(toMap(Measurement::getName,
                        Measurement::getValue,
                        Double::sum,
                        TreeMap::new));
    }
}
