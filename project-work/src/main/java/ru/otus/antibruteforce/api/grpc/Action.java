package ru.otus.antibruteforce.api.grpc;

@FunctionalInterface
public interface Action {
    void execute();
}
