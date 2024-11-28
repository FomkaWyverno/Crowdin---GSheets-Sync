package ua.wyverno.google.sheets.model;

public class GoogleCell {
    private final int index;
    private final String value;
    public GoogleCell(String value, int index) {
        this.value = value;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public int getValueAsInteger() {
        return Integer.parseInt(this.value);
    }

    public long getValueAsLong() {
        return Long.parseLong(this.value);
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
