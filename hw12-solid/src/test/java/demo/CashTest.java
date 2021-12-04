package demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CashTest {
    private Cash cash;

    @BeforeEach
    void before() {
        cash = new Cash(CashCalculator.getInstance());
    }

    @Test
    void cashCreateTest() {
        assertThat(cash.getSum()).isEqualTo(0L);

        var banknotes = cash.getBanknotes();
        for(var nominal : Nominal.values()) {
            assertThat(banknotes.containsKey(nominal)).isEqualTo(true);
        }
    }

    @Test
    void getBanknotesTest() {
        var banknotes = cash.getBanknotes();
        banknotes.put(Nominal.N_1, 1);

        assertThat(cash.getBanknotes().get(Nominal.N_1)).isEqualTo(0);
    }

    @Test
    void creditBanknoteTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cash.creditBanknote(Nominal.N_1, -1);
        });
        cash.creditBanknote(Nominal.N_1, 1);
        assertThat(cash.getSum()).isEqualTo(1L);
    }

    @Test
    void debetCashTest() {
        cash.creditBanknote(Nominal.N_1, 2);
        cash.creditBanknote(Nominal.N_100, 2);

        Cash debetCash = new Cash(CashCalculator.getInstance());
        debetCash.creditBanknote(Nominal.N_1, 1);
        debetCash.creditBanknote(Nominal.N_100, 2);

        cash.debetCash(debetCash);
        assertThat(cash.getSum()).isEqualTo(1L);
    }

    @Test
    void debetBanknoteTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cash.debetBanknote(Nominal.N_1, -1);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cash.debetBanknote(Nominal.N_1, 1);
        });

        cash.creditBanknote(Nominal.N_1, 2);
        cash.debetBanknote(Nominal.N_1, 1);
        assertThat(cash.getSum()).isEqualTo(1L);
    }

    @Test
    void checkDebetSumTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cash.checkDebetSum(1L);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cash.checkDebetSum(-1L);
        });

        cash.creditBanknote(Nominal.N_100, 1);

        Assertions.assertThrows(IllegalStateException.class, () -> {
            cash.checkDebetSum(1L);
        });

        cash.checkDebetSum(100L);
    }

    @Test
    void getDebetCashTest() {
        cash.creditBanknote(Nominal.N_1, 100);
        cash.creditBanknote(Nominal.N_500, 1);

        Cash debetCash = cash.getDebetCash(100L);
        assertThat(debetCash.getBanknotes().get(Nominal.N_1)).isEqualTo(100);
        assertThat(debetCash.getBanknotes().get(Nominal.N_500)).isEqualTo(0);

        debetCash = cash.getDebetCash(500L);
        assertThat(debetCash.getBanknotes().get(Nominal.N_1)).isEqualTo(0);
        assertThat(debetCash.getBanknotes().get(Nominal.N_500)).isEqualTo(1);

        debetCash = cash.getDebetCash(600L);
        assertThat(debetCash.getBanknotes().get(Nominal.N_1)).isEqualTo(100);
        assertThat(debetCash.getBanknotes().get(Nominal.N_500)).isEqualTo(1);

        Assertions.assertThrows(IllegalStateException.class, () -> {
            cash.getDebetCash(150L);
        });

        cash.creditBanknote(Nominal.N_1, 100);
        cash.creditBanknote(Nominal.N_200, 1);

        debetCash = cash.getDebetCash(600L);
        assertThat(debetCash.getBanknotes().get(Nominal.N_1)).isEqualTo(100);
        assertThat(debetCash.getBanknotes().get(Nominal.N_200)).isEqualTo(0);
        assertThat(debetCash.getBanknotes().get(Nominal.N_500)).isEqualTo(1);

        debetCash = cash.getDebetCash(700L);
        assertThat(debetCash.getBanknotes().get(Nominal.N_1)).isEqualTo(0);
        assertThat(debetCash.getBanknotes().get(Nominal.N_200)).isEqualTo(1);
        assertThat(debetCash.getBanknotes().get(Nominal.N_500)).isEqualTo(1);

        debetCash = cash.getDebetCash(800L);
        assertThat(debetCash.getBanknotes().get(Nominal.N_1)).isEqualTo(100);
        assertThat(debetCash.getBanknotes().get(Nominal.N_200)).isEqualTo(1);
        assertThat(debetCash.getBanknotes().get(Nominal.N_500)).isEqualTo(1);
    }

    @Test
    void getSumTest() {
        assertThat(cash.getSum()).isEqualTo(0L);
        cash.creditBanknote(Nominal.N_1, 1);
        assertThat(cash.getSum()).isEqualTo(1L);
    }
}
