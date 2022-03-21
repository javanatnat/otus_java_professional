package ru.otus.antibruteforce.service;

public interface WhitelistService {
    void addIpWhitelist(String ip);
    void deleteIpWhitelist(String ip);
    boolean isInList(String ip);
}
