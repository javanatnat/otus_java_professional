package ru.otus.antibruteforce.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.antibruteforce.service.Ipv4Check.*;

public class Ipv4CheckTest {

    private static final String IP_CORRECT = "128.11.0.1";
    private static final String IP_INCORRECT = "12345678";
    private static final String IP_RANGE_CORRECT = "128.11.0.0/25";
    private static final String IP_NOT_IN_RANGE = "128.25.0.23";

    @Test
    void isValidIpv4Test() {
        assertThat(isValidIpv4(IP_CORRECT)).isTrue();
        assertThat(isValidIpv4(IP_INCORRECT)).isFalse();
    }

    @Test
    void isValidIpv4RangeTest() {
        assertThat(isValidIpv4Range(IP_RANGE_CORRECT)).isTrue();
        assertThat(isValidIpv4Range(IP_CORRECT)).isFalse();
        assertThat(isValidIpv4Range(IP_INCORRECT)).isFalse();
    }

    @Test
    void checkIsIpv4Test() {
        assertThatThrownBy(() -> checkIsIpv4(IP_INCORRECT))
                .isInstanceOf(IllegalIpv4Exception.class);
        assertThatThrownBy(() -> checkIsIpv4(IP_RANGE_CORRECT))
                .isInstanceOf(IllegalIpv4Exception.class);
    }

    @Test
    void checkIsIpv4OrIpv4RangeTest() {
        assertThatThrownBy(() -> checkIsIpv4OrIpv4Range(IP_INCORRECT))
                .isInstanceOf(IllegalIpv4Exception.class);
    }

    @Test
    void isInRangeTest() {
        assertThat(isInRangeOrEqualIp(IP_CORRECT, IP_RANGE_CORRECT)).isTrue();
        assertThat(isInRangeOrEqualIp(IP_CORRECT, IP_CORRECT)).isTrue();
        assertThat(isInRangeOrEqualIp(IP_NOT_IN_RANGE, IP_RANGE_CORRECT)).isFalse();

        assertThatThrownBy(() -> isInRangeOrEqualIp(IP_INCORRECT, IP_RANGE_CORRECT))
                .isInstanceOf(IllegalIpv4Exception.class);
        assertThatThrownBy(() -> isInRangeOrEqualIp(IP_INCORRECT, IP_CORRECT))
                .isInstanceOf(IllegalIpv4Exception.class);
        assertThatThrownBy(() -> isInRangeOrEqualIp(IP_CORRECT, IP_INCORRECT))
                .isInstanceOf(IllegalIpv4Exception.class);
    }
}
