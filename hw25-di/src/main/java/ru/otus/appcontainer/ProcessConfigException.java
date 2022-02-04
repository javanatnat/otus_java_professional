package ru.otus.appcontainer;

public class ProcessConfigException extends RuntimeException {
    public ProcessConfigException(String message) {
        super(message);
    }

    public ProcessConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
