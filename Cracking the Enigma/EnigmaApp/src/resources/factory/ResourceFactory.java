package resources.factory;

import resources.components.CharacterComponent;
import resources.components.RotorComponent;
import resources.components.WordComponent;

public class ResourceFactory {
    private static final String ROTOR_POSITION_CHARACTER_IMAGE_URL = "/resources/images/RotorPosition.png";
    private static final String KEY_CHARACTER_IMAGE_URL = "/resources/images/Key.png";

    public static RotorComponent createRotorComponent(int rotorID) {
        return new RotorComponent(rotorID);
    }

    public static CharacterComponent createRotorPositionCharacterComponent(Character character) {
        return new CharacterComponent(character, ROTOR_POSITION_CHARACTER_IMAGE_URL);
    }

    public static CharacterComponent createKeyCharacterComponent(Character character) {
        return new CharacterComponent(character, KEY_CHARACTER_IMAGE_URL);
    }

    public static CharacterComponent createLightBulbCharacterComponent(Character character, String LIGHT_BULB_CHARACTER_IMAGE_URL) {
        return new CharacterComponent(character, LIGHT_BULB_CHARACTER_IMAGE_URL);
    }

    public static WordComponent createWordComponent(String word) {
        return new WordComponent(word);
    }
}