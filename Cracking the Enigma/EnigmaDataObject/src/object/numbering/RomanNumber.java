package object.numbering;

public enum RomanNumber {
    I("I", 1),
    II("II", 2),
    III("III", 3),
    IV("IV", 4),
    V("V", 5);

    private final String stringValue;
    private final int intValue;

    RomanNumber(String stringValue, int intValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getIntValue() { return intValue; }

    public static RomanNumber fromString(String text) {
        for (RomanNumber n : RomanNumber.values()) {
            if (n.stringValue.equalsIgnoreCase(text)) {
                return n;
            }
        }

        return null;
    }

    public static RomanNumber fromInt(int number) {
        for (RomanNumber n : RomanNumber.values()) {
            if (n.intValue == number) {
                return n;
            }
        }

        return null;
    }
}
