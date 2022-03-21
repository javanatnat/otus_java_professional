package ru.otus.antibruteforce.service;

public interface RedisService {
    boolean isBruteforceByLogin(String login);
    boolean isBruteforceByPassword(String password);
    boolean isBruteforceByIp(String ip);
    void cleanByLoginIp(String login, String ip);
}
