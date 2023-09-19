package object.user.type;

public enum UserType {
    UBOAT("UBoat"),
    ALLY("Ally"),
    AGENT("Agent");

    private final String stringValue;

    UserType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static UserType fromString(String text) {
        for (UserType n : UserType.values()) {
            if (n.stringValue.equalsIgnoreCase(text)) {
                return n;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
