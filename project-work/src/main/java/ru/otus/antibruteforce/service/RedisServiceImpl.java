package ru.otus.antibruteforce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static ru.otus.antibruteforce.service.Ipv4Check.checkIsIpv4;

@Service
@Transactional
public class RedisServiceImpl implements RedisService{

    private static final Logger LOG = LoggerFactory.getLogger(RedisServiceImpl.class);
    private static final int COUNT_MINUTES = 1;
    private static final String KEY_VALUE = "1";
    private static final String PATTERN_POSTFIX = "*";

    @Autowired
    RedisTemplate<String, String>  redisTemplate;

    /*
    не более N = 10 попыток в минуту для данного логина.
    не более M = 100 попыток в минуту для данного пароля (защита от обратного brute-force).
    не более K = 1000 попыток в минуту для данного IP (число большое, т.к. NAT).
    */
    @Value( "${countAttempt.login:0}" )
    private Integer loginCountPerMinute;

    @Value( "${countAttempt.password:0}" )
    private Integer passwordCountPerMinute;

    @Value( "${countAttempt.ip:0}" )
    private Integer ipCountPerMinute;

    @Override
    public boolean isBruteforceByLogin(String login) {
        return isBruteforce(getLoginKey(login), loginCountPerMinute);
    }

    @Override
    public boolean isBruteforceByPassword(String password) {
        return isBruteforce(getPasswordKey(password), passwordCountPerMinute);
    }

    @Override
    public boolean isBruteforceByIp(String ip) {
        checkIsIpv4(ip);
        return isBruteforce(getIpKey(ip), ipCountPerMinute);
    }

    @Override
    public void cleanByLoginIp(String login, String ip) {
        checkIsIpv4(ip);
        cleanBucket(getLoginKey(login));
        cleanBucket(getIpKey(ip));
    }

    private String getLoginKey(String login) {
        return buildKey(RedisKeyCountType.LOGIN, login);
    }

    private String buildKey(RedisKeyCountType keyCountType, String key) {
        return String.format("%s:%s", keyCountType, key);
    }

    private String getPasswordKey(String password) {
        return buildKey(RedisKeyCountType.PASSWORD, password);
    }

    private String getIpKey(String ip) {
        return buildKey(RedisKeyCountType.IP, ip);
    }

    private boolean isBruteforce(String key, int countPerMinute) {
        if (countPerMinute <= 0) {
            return false;
        }

        int count = getKeyCount(key);
        LOG.debug("REDIS GET KEY: key={}, count={}, countPerMinute={}", key, count, countPerMinute);

        count++;
        setKey(key);

        if (count >= countPerMinute) {
            LOG.info("redis: isBruteforce=true, key={}, count attempt={}, countPerMinute={}",
                    key, count, countPerMinute);
            return true;
        }
        return false;
    }

    private int getKeyCount(String key) {
        Set<String> keys = getRedisKeys(key);
        return (keys == null) ? 0 : keys.size();
    }

    private Set<String> getRedisKeys(String key) {
        return redisTemplate.keys(getKeyPattern(key));
    }

    private String getKeyPattern(String key) {
        return key + PATTERN_POSTFIX;
    }

    private void setKey(String key) {
        getValueOps().set(enrichKey(key), KEY_VALUE, COUNT_MINUTES, TimeUnit.MINUTES);
    }

    private ValueOperations<String, String> getValueOps() {
        return redisTemplate.opsForValue();
    }

    private String enrichKey(String key) {
        return key + ":" + System.currentTimeMillis() + ":" + ThreadLocalRandom.current().nextInt();
    }

    private void cleanBucket(String key) {
        for (String redisKey : getRedisKeys(key)) {
            getValueOps().getAndDelete(redisKey);
        }
    }
}
