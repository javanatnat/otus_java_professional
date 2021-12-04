package demo;

import java.util.EnumMap;
import java.util.EnumSet;

public class Cash {
    private final EnumMap<Nominal, Integer> banknotes;
    private final CashCalculator calculator;

    Cash(CashCalculator calculator) {
        this.calculator = calculator;
        this.banknotes = new EnumMap<>(Nominal.class);
        setBanknotesToZero(EnumSet.allOf(Nominal.class));
    }

    private void setBanknotesToZero(EnumSet<Nominal> nominals) {
        for (var nominal : nominals) {
            setBanknoteToZero(nominal);
        }
    }

    private void setBanknoteToZero(Nominal nominal) {
        banknotes.put(nominal, 0);
    }

    EnumMap<Nominal, Integer> getBanknotes() {
        return new EnumMap<>(this.banknotes);
    }

    void creditBanknote(Nominal nominal, int count) {
        checkCount(count);

        int currCount = banknotes.getOrDefault(nominal, 0);
        banknotes.put(nominal, currCount + count);
    }

    private void checkCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
    }

    void debetCash(Cash debetCash) {
        for(var entry : debetCash.getBanknotes().entrySet()) {
            debetBanknote(entry.getKey(), entry.getValue());
        }
    }

    void debetBanknote(Nominal nominal, int count) {
        checkCount(count);

        int currCount = banknotes.getOrDefault(nominal, 0);
        int resCount = currCount - count;
        checkCount(resCount);

        banknotes.put(nominal, resCount);
    }

    void checkDebetSum(long sum) {
        if (sum < 0 || sum > getSum()) {
            throw new IllegalArgumentException();
        }

        getDebetCash(sum);
    }

    Cash getDebetCash(long sum) {
        return calculator.calcDebetCash(this, sum);
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
