package object.automatic.decryption.difficulty;

public enum DecryptionDifficulty {
    EASY("Easy", 1),
    MEDIUM("Medium",2),
    HARD("Hard",3),
    INSANE("Insane",4);

    private final String stringValue;
    private final int intValue;

    DecryptionDifficulty(String stringValue, int intValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    public static DecryptionDifficulty fromString(String value) {
        for (DecryptionDifficulty d : DecryptionDifficulty.values()) {
            if (d.stringValue.equalsIgnoreCase(value)) {
                return d;
            }
        }

        return null;
    }

    public static DecryptionDifficulty fromInt(int number) {
        for (DecryptionDifficulty d : DecryptionDifficulty.values()) {
            if (d.intValue == number) {
                return d;
            }
        }

        return null;
    }

    public int getIntValue() { return intValue; }

    public String getStringValue() {
        return stringValue;
    }
}
