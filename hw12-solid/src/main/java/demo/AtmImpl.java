package demo;

import java.util.Objects;

public class AtmImpl implements Atm{
    private final Cash cash;

    AtmImpl() {
        this.cash = new Cash();
    }

    @Override
    public Long getRest() {
        return cash.getSum();
    }

    @Override
    public Cash debet(Long sum) {
        cash.checkDebetSum(sum);

        Cash debetCash = cash.getDebetCash(sum);
        cash.debetCash(debetCash);

        return debetCash;
    }

    @Override
    public void credit(Cash creditCash) {
        Objects.requireNonNull(creditCash);
        for (var entry : creditCash.getBanknotes().entrySet()) {
            cash.creditBanknote(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        return cash.toString();
    }
}
