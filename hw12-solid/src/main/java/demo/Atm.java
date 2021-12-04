package demo;

public interface Atm {
    long getRest();
    Cash debet(long sum);
    void credit(Cash cash);
}
