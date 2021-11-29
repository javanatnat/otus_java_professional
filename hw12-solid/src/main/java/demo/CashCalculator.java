package demo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;

public class CashCalculator {
    static Cash calcDebetCash(Cash cash, Long sum) {

        EnumMap<Nominal, Integer> banknotes = cash.getBanknotes();
        Cash debetCash = new Cash();

        Nominal[] nominals = Nominal.values();
        Arrays.sort(nominals, Comparator.reverseOrder());

        long rest = sum;
        for (var nominal : nominals) {

            int countBanks = banknotes.get(nominal);
            int nominalValue = nominal.getCount();
            int sumBanks = countBanks * nominalValue;

            if (sumBanks > 0 && nominalValue <= rest) {
                int count = 0;
                if (sumBanks == rest) {
                    count = countBanks;
                } else {
                    count = (int) rest/nominalValue;
                }

                if (count > 0 && count <= countBanks) {
                    debetCash.creditBanknote(nominal, count);
                    rest -= (long) nominalValue * count;
                }
            }

            if (rest == 0) {
                break;
            }
        }

        if (rest > 0) {
            throw new IllegalStateException();
        }

        return debetCash;
    }
}
