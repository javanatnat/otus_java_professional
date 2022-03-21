package ru.otus.antibruteforce.service;

public interface BlacklistService {
    void addIpBlacklist(String ip);
    void deleteIpBlacklist(String ip);
    boolean isInList(String ip);
}
