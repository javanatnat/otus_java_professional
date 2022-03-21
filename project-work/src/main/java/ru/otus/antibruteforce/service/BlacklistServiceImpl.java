package ru.otus.antibruteforce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.antibruteforce.model.Blacklist;
import ru.otus.antibruteforce.repository.BlacklistRepository;

import java.time.LocalDateTime;
import java.util.*;

import static ru.otus.antibruteforce.service.Ipv4Check.*;

@Service
public class BlacklistServiceImpl implements BlacklistService{

    private static final Logger LOG = LoggerFactory.getLogger(BlacklistServiceImpl.class);

    private final List<Blacklist> blacklist;
    private final BlacklistRepository repository;

    public BlacklistServiceImpl(BlacklistRepository repository) {
        this.repository = repository;
        this.blacklist = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void addIpBlacklist(String ip) {
        checkIsIpv4OrIpv4Range(ip);
        Blacklist newRec = new Blacklist(ip);
        if (!blacklist.contains(newRec)) {
            blacklist.add(newRec);
            repository.save(newRec);
        }
    }

    @Override
    public void deleteIpBlacklist(String ip) {
        checkIsIpv4OrIpv4Range(ip);
        Blacklist blacklistRow = new Blacklist(ip);
        if (blacklist.remove(blacklistRow)) {
            repository.deleteByIp(blacklistRow.getIp());
            LOG.info("deleteIpBlacklist: {}", ip);
        }
    }

    @Override
    public boolean isInList(String ip) {
        checkIsIpv4(ip);
        return blacklist.stream()
                .map(Blacklist::getIp)
                .anyMatch(range -> isInRangeOrEqualIp(ip, range.getValue()));
    }

    @Scheduled(fixedDelayString = "${cash.schedule.blacklist.fixedDelay}")
    public void cashBlacklist() {
        LOG.info(" start scheduled cash blacklist: {}", LocalDateTime.now());
        synchronized (blacklist) {
            blacklist.clear();
            blacklist.addAll(repository.findAll());
            LOG.info("end cash blacklist: size = {}", blacklist.size());
        }
    }
}
