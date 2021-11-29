package demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AtmImplTest {
    private Atm atm;

    @BeforeEach
    void before() {
        atm = new AtmImpl();
    }

    @Test
    void getRestTest() {
        assertThat(atm.getRest()).isEqualTo(0L);

        Cash creditCash = new Cash();
        creditCash.creditBanknote(Nominal.N_1, 1);
        atm.credit(creditCash);

        assertThat(atm.getRest()).isEqualTo(1L);
    }

    @Test
    void debetTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            atm.debet(-1L);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            atm.debet(1L);
        });

        Cash creditCash = new Cash();
        creditCash.creditBanknote(Nominal.N_1, 150);
        creditCash.creditBanknote(Nominal.N_500, 1);
        atm.credit(creditCash);

        Assertions.assertThrows(IllegalStateException.class, () -> {
            atm.debet(350L);
        });

        Cash debetCash = atm.debet(100L);

        assertThat(atm.getRest()).isEqualTo(550L);
        assertThat(debetCash.getBanknotes().get(Nominal.N_1)).isEqualTo(100);
        assertThat(debetCash.getBanknotes().get(Nominal.N_500)).isEqualTo(0);
    }

    @Test
    void creditTest() {
        Cash creditCash = new Cash();
        creditCash.creditBanknote(Nominal.N_1, 1);
        atm.credit(creditCash);

        assertThat(atm.getRest()).isEqualTo(1L);
    }
}
