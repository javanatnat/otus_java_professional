package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.model.Measurement;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ResourcesFileLoader implements Loader {
    private static final String NAME  = "name";
    private static final String VALUE = "value";

    private final String filename;
    private final ObjectMapper mapper;

    public ResourcesFileLoader(String fileName) {
        this.filename = fileName;
        this.mapper = new ObjectMapper();
    }

    @Override
    public List<Measurement> load() {
        //читает файл, парсит и возвращает результат
        try {
            JsonNode root = mapper.readTree(getFile());
            Iterator<JsonNode> iterator = root.elements();
            List<Measurement> result = new ArrayList<>();

            while (iterator.hasNext()) {
                Measurement measurement = parseNode(iterator.next());
                result.add(measurement);
            }
            return result;

        } catch (Exception ex) {
            throw new FileProcessException(ex);
        }
    }

    private File getFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());
    }

    private static Measurement parseNode(JsonNode e) {
        String name = e.get(NAME).asText();
        double value = e.get(VALUE).asDouble();

        return new Measurement(name, value);
    }
}
