package ru.otus.antibruteforce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AntiBruteforceServiceImpl implements AntiBruteforceService{

    private static final Logger LOG = LoggerFactory.getLogger(AntiBruteforceServiceImpl.class);

    private final RedisService redisService;
    private final BlacklistService blacklistService;
    private final WhitelistService whitelistService;

    public AntiBruteforceServiceImpl(
            RedisService redisService,
            BlacklistService blacklistService,
            WhitelistService whitelistService
    ) {
        this.redisService = redisService;
        this.blacklistService = blacklistService;
        this.whitelistService = whitelistService;
    }

    @Override
    public boolean isBruteforce(String login, String password, String ip) {
        LOG.info("isBruteforce: login={}, password={}, ip={}", login, password, ip);
        if (whitelistService.isInList(ip)) {
            LOG.info("isBruteforce: ip={} in whitelist, result=false", ip);
            return false;
        }
        if (blacklistService.isInList(ip)) {
            LOG.info("isBruteforce: ip={} in blacklist, result=true", ip);
            return true;
        }
        if (redisService.isBruteforceByLogin(login)) {
            LOG.info("isBruteforce: (by login={}) result=true", login);
            return true;
        }
        if (redisService.isBruteforceByPassword(password)) {
            LOG.info("isBruteforce: (by password={}) result=true", password);
            return true;
        }
        boolean result = redisService.isBruteforceByIp(ip);
        LOG.info("isBruteforce: (by ip={}) result={}", ip, result);
        return result;
    }

    @Override
    public void deleteByLoginIp(String login, String ip) {
        LOG.info("deleteByLoginIp: login={}, ip={}", login, ip);
        redisService.cleanByLoginIp(login, ip);
    }

    @Override
    public void addIpBlacklist(String ip) {
        LOG.info("addIpBlacklist: ip={}", ip);
        blacklistService.addIpBlacklist(ip);
    }

    @Override
    public void deleteIpBlacklist(String ip) {
        LOG.info("deleteIpBlacklist: ip={}", ip);
        blacklistService.deleteIpBlacklist(ip);
    }

    @Override
    public void addIpWhitelist(String ip) {
        LOG.info("addIpWhitelist: ip={}", ip);
        whitelistService.addIpWhitelist(ip);
    }

    @Override
    public void deleteIpWhitelist(String ip) {
        LOG.info("deleteIpWhitelist: ip={}", ip);
        whitelistService.deleteIpWhitelist(ip);
    }
}
