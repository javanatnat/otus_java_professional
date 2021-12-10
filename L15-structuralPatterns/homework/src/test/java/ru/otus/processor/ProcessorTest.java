package ru.otus.processor;

import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessorTest {
    @Test
    void processorConcatFieldsTest() {
        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .build();

        var processor = new ProcessorConcatFields();
        var messageProc = processor.process(message);

        var field4 = messageProc.getField4();

        assertThat(field4).contains("field1");
        assertThat(field4).contains("field2");
        assertThat(field4).contains("field3");
    }

    @Test
    void processorUpperField10Test() {
        var message = new Message.Builder(1L)
                .field10("field10")
                .build();

        var processor = new ProcessorUpperField10();
        var messageProc = processor.process(message);

        assertThat(messageProc.getField4()).isEqualTo("field10".toUpperCase());
    }
}
