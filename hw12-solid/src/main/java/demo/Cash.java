package demo;

import java.util.EnumMap;

public class Cash {
    private final EnumMap<Nominal, Integer> banknotes;

    Cash() {
        this.banknotes = new EnumMap<>(Nominal.class);
        setBanknotesToZero();
    }

    private void setBanknotesToZero() {
        for (var nominal : Nominal.values()) {
            setBanknoteToZero(nominal);
        }
    }

    private void setBanknoteToZero(Nominal nominal) {
        banknotes.put(nominal, 0);
    }

    EnumMap<Nominal, Integer> getBanknotes() {
        return new EnumMap<>(this.banknotes);
    }

    void creditBanknote(Nominal nominal, Integer count) {
        checkCount(count);

        Integer currCount = banknotes.getOrDefault(nominal, 0);
        banknotes.put(nominal, currCount + count);
    }

    private void checkCount(Integer count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
    }

    void debetCash(Cash debetCash) {
        for(var entry : debetCash.getBanknotes().entrySet()) {
            debetBanknote(entry.getKey(), entry.getValue());
        }
    }

    void debetBanknote(Nominal nominal, Integer count) {
        checkCount(count);

        Integer currCount = banknotes.getOrDefault(nominal, 0);
        Integer resCount = currCount - count;
        checkCount(resCount);

        banknotes.put(nominal, resCount);
    }

    void checkDebetSum(Long sum) {
        if (sum < 0 || sum > getSum()) {
            throw new IllegalArgumentException();
        }

        getDebetCash(sum);
    }

    Cash getDebetCash(Long sum) {
        return CashCalculator.calcDebetCash(this, sum);
    }

    long getSum() {
        long sum = 0L;
        for(var entry : banknotes.entrySet()) {
            sum += (long) entry.getKey().getCount() * entry.getValue();
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (var entry : banknotes.entrySet()) {
            builder.append(entry.getKey());
            builder.append(" в количестве ");
            builder.append(entry.getValue());
            builder.append(";\n");
        }
        return builder.toString();
    }
}
