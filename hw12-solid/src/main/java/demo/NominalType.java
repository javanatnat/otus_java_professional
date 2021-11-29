package demo;

public enum NominalType {
    COIN("монета"),
    BANKNOTE("банкнота");

    private final String nominalType;

    NominalType(String value) {
        this.nominalType = value;
    }

    @Override
    public String toString() {
        return nominalType;
    }
}
