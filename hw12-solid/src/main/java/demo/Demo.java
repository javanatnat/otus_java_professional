package demo;

public class Demo {
    public static void main(String[] args) {
        Atm atm = new AtmImpl();

        System.out.println("Остаток денег в банкомате = " + atm.getRest() + " :");
        System.out.println(atm);

        Cash creditCash = new Cash(CashCalculator.getInstance());
        creditCash.creditBanknote(Nominal.N_100, 5);
        creditCash.creditBanknote(Nominal.N_200, 10);
        creditCash.creditBanknote(Nominal.N_1000, 5);
        atm.credit(creditCash);

        System.out.println("Остаток денег в банкомате = " + atm.getRest() + " :");
        System.out.println(atm);

        Cash debetCash = atm.debet(300L);

        System.out.println("Сумма снятых наличных = " + debetCash.getSum() + " :");
        System.out.println(debetCash);

        System.out.println("Остаток денег в банкомате = " + atm.getRest() + " :");
        System.out.println(atm);

    }
}
