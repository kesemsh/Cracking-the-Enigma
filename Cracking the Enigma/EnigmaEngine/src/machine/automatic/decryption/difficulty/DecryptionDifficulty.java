package machine.automatic.decryption.difficulty;

import object.numbering.RomanNumber;

public enum DecryptionDifficulty {
    BASIC("Basic", 1),
    INTERMEDIATE("Intermediate",2),
    ADVANCED("Advanced",3),
    IMPOSSIBLE("Impossible",4);

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
