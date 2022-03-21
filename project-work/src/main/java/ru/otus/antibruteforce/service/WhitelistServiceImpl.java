package ru.otus.antibruteforce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.antibruteforce.model.Whitelist;
import ru.otus.antibruteforce.repository.WhitelistRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.otus.antibruteforce.service.Ipv4Check.*;

@Service
public class WhitelistServiceImpl implements WhitelistService{

    private static final Logger LOG = LoggerFactory.getLogger(WhitelistServiceImpl.class);

    private final List<Whitelist> whitelist;
    private final WhitelistRepository repository;

    private WhitelistServiceImpl(WhitelistRepository repository) {
        this.repository = repository;
        this.whitelist = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void addIpWhitelist(String ip) {
        checkIsIpv4OrIpv4Range(ip);
        Whitelist newRec = new Whitelist(ip);
        if (!whitelist.contains(newRec)) {
            whitelist.add(newRec);
            repository.save(newRec);
        }
    }

    @Override
    public void deleteIpWhitelist(String ip) {
        LOG.debug("deleteIpWhitelist: {}", ip);
        checkIsIpv4OrIpv4Range(ip);
        Whitelist whitelistRow = new Whitelist(ip);
        if (whitelist.remove(whitelistRow)) {
            repository.deleteByIp(whitelistRow.getIp());
            LOG.debug("deleteIpWhitelist: {} SUCCESS", ip);
        }
    }

    @Override
    public boolean isInList(String ip) {
        checkIsIpv4(ip);
        return whitelist.stream()
                .map(Whitelist::getIp)
                .anyMatch(range -> isInRangeOrEqualIp(ip, range.getValue()));
    }

    @Scheduled(fixedDelayString = "${cash.schedule.blacklist.fixedDelay}")
    public void cashWhiteList() {
        LOG.info(" start scheduled cash whitelist: {}", LocalDateTime.now());
        synchronized (whitelist) {
            whitelist.clear();
            whitelist.addAll(repository.findAll());
            LOG.info("end cash whitelist: size = {}", whitelist.size());
        }
    }
}
