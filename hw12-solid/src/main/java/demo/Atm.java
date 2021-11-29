package demo;

public interface Atm {
    Long getRest();
    Cash debet(Long sum);
    void credit(Cash cash);
}
