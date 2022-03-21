package ru.otus.antibruteforce.service;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ipv4Check {
    private static final Logger LOG = LoggerFactory.getLogger(Ipv4Check.class);

    public static boolean isValidIpv4(String ip)
    {
        try {
            getIpv4(ip);
            LOG.debug("isValidIpv4: ip={}", ip);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static boolean isValidIpv4Range(String ip) {
        try {
            getIpv4Range(ip);
            LOG.debug("isValidIpv4Range: ip={}", ip);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static void checkIsIpv4(String ip) {
        if (!isValidIpv4(ip)) {
            LOG.error("checkIsIpv4: ip is not valid ipv4 address: {}", ip);
            throw new IllegalIpv4Exception("ip is not valid ipv4 address: " + ip);
        }
    }

    public static void checkIsIpv4OrIpv4Range(String ip) {
        if (!isValidIpv4(ip) && !isValidIpv4Range(ip)) {
            LOG.error("checkIsIpv4OrIpv4Range: ip is not valid ipv4 address or ipv4range: {}", ip);
            throw new IllegalIpv4Exception("ip is not valid ipv4 address: " + ip);
        }
    }

    public static boolean isInRangeOrEqualIp(String ip, String rangeIp) {
        LOG.debug("isInRange: ip={}, rangeIp={}", ip, rangeIp);

        checkIsIpv4(ip);
        checkIsIpv4OrIpv4Range(rangeIp);

        Ipv4 ipv4 = Ipv4.of(ip);

        if (isValidIpv4Range(rangeIp)) {
            Ipv4Range ipRange = getIpv4Range(rangeIp);
            boolean result = ipRange.contains(ipv4);
            LOG.debug("isInRange: result={}", result);
            return result;
        }

        boolean result = ipv4.equals(getIpv4(rangeIp));
        LOG.debug("isInRange: result={}", result);
        return result;
    }

    private static Ipv4 getIpv4(String ip) {
        return Ipv4.of(ip);
    }

    private static Ipv4Range getIpv4Range(String ip) {
        return Ipv4Range.parse(ip);
    }

}
