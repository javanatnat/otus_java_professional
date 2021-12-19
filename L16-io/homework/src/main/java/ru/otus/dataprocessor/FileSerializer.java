package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

public class FileSerializer implements Serializer {
    private final String filename;
    private final ObjectMapper mapper;

    public FileSerializer(String fileName) {
        this.filename = fileName;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void serialize(Map<String, Double> data) {
        //формирует результирующий json и сохраняет его в файл
        try {
            mapper.writeValue(new File(filename), data);
        } catch (Exception ex) {
            throw new FileProcessException(ex);
        }
    }
}
