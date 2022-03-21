package ru.otus.antibruteforce.service;

public class IllegalIpv4Exception extends RuntimeException{
    public IllegalIpv4Exception(String message) {
        super(message);
    }
}
