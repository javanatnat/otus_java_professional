package demo;

public enum Nominal {
    N_1(1, NominalType.COIN),
    N_5(5, NominalType.COIN),
    N_10(10, NominalType.COIN),
    N_50(50, NominalType.BANKNOTE),
    N_100(100, NominalType.BANKNOTE),
    N_200(200, NominalType.BANKNOTE),
    N_500(500, NominalType.BANKNOTE),
    N_1000(1000, NominalType.BANKNOTE),
    N_5000(5000, NominalType.BANKNOTE);

    private final int count;
    private final NominalType nominalType;

    Nominal(int count, NominalType nominalType) {
        this.count = count;
        this.nominalType = nominalType;
    }

    int getCount() {
        return this.count;
    }

    @Override
    public String toString() {
        return nominalType + " номиналом " + getCount();
    }
}
