package ru.otus.processor.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class hwProcessorTest {
    private Message message;

    @BeforeEach
    void before() {
        message = new Message.Builder(1L)
                .field11("field11")
                .field12("field12")
                .build();
    }

    @Test
    void changeProcessorTest() {
        var processor = new ChangeProcessor();
        var messageProc = processor.process(message);

        assertThat(messageProc.getField11()).isEqualTo(message.getField12());
        assertThat(messageProc.getField12()).isEqualTo(message.getField11());
    }

    @Test
    void getProviderException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeFactory.getProvider(DateTimeProviderType.NOT_USE));
    }

    @Test
    void timeExcProcessorEvenTest() {
        DateTimeProvider dateTimeProviderEven = DateTimeFactory.getProvider(DateTimeProviderType.EVEN);
        var processor = new TimeExcProcessor(dateTimeProviderEven);

        assertThrows(RuntimeException.class, () -> processor.process(message));
    }

    @Test
    void timeExcProcessorOddTest() {
        DateTimeProvider dateTimeProviderOdd = DateTimeFactory.getProvider(DateTimeProviderType.ODD);
        var processor = new TimeExcProcessor(dateTimeProviderOdd);

        var messageProc = processor.process(message);
        assertThat(messageProc).isEqualTo(message);
    }
}
