package ru.otus.antibruteforce.service;

public interface AntiBruteforceService {
    boolean isBruteforce(String login, String password, String ip);
    void deleteByLoginIp(String login, String ip);
    void addIpBlacklist(String ip);
    void deleteIpBlacklist(String ip);
    void addIpWhitelist(String ip);
    void deleteIpWhitelist(String ip);
}
