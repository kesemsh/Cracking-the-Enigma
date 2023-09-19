package object.machine.configuration;

import object.numbering.RomanNumber;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineConfiguration implements Serializable, Cloneable {
    private final List<Integer> rotorIDsInOrder;
    private final List<Character> rotorStartPositionsByChar;
    private Map<Integer, Integer> rotorIDToRotationsLeftForNotchPerRotor;
    private final RomanNumber reflectorID;
    private final Map<Character, Character> plugsToUse;

    public MachineConfiguration(List<Integer> rotorIDsInOrder, List<Character> rotorStartPositionsByChar, Map<Integer, Integer> rotorIDToRotationsLeftForNotchPerRotor, RomanNumber reflectorID, Map<Character, Character> plugsToUse) {
        this.rotorIDsInOrder = rotorIDsInOrder;
        this.rotorStartPositionsByChar = rotorStartPositionsByChar;
        this.rotorIDToRotationsLeftForNotchPerRotor = rotorIDToRotationsLeftForNotchPerRotor;
        this.reflectorID = reflectorID;
        this.plugsToUse = plugsToUse;
    }

    public MachineConfiguration(List<Integer> rotorIDsInOrder, List<Character> rotorStartPositionsByChar, RomanNumber reflectorID, Map<Character, Character> plugsToUse) {
        this.rotorIDsInOrder = rotorIDsInOrder;
        this.rotorStartPositionsByChar = rotorStartPositionsByChar;
        this.reflectorID = reflectorID;
        this.plugsToUse = plugsToUse;
        rotorIDToRotationsLeftForNotchPerRotor = null;
    }

    public MachineConfiguration(List<Integer> rotorIDsInOrder, List<Character> rotorStartPositionsByChar, RomanNumber reflectorID) {
        this.rotorIDsInOrder = rotorIDsInOrder;
        this.rotorStartPositionsByChar = rotorStartPositionsByChar;
        this.reflectorID = reflectorID;
        plugsToUse = new HashMap<>();
        rotorIDToRotationsLeftForNotchPerRotor = null;
    }

    public List<Integer> getRotorIDsInOrder() {
        return rotorIDsInOrder;
    }

    public List<Character> getRotorStartPositionsByChar() {
        return rotorStartPositionsByChar;
    }

    public RomanNumber getReflectorID() {
        return reflectorID;
    }

    public Map<Character, Character> getPlugsToUse() {
        return plugsToUse;
    }

    @Override
    public String toString() {
        String result = "<%s><%s><%s>%s";
        StringBuilder rotorsIDs = new StringBuilder();
        StringBuilder rotorPositions = new StringBuilder();
        String plugs = "";

        rotorIDsInOrder.forEach(x -> rotorsIDs.append(x).append(","));
        for (int i = 0; i < rotorIDsInOrder.size(); i++) {
            if (rotorIDToRotationsLeftForNotchPerRotor != null) {
                rotorPositions.append(rotorStartPositionsByChar.get(i)).append(String.format("(%d)", rotorIDToRotationsLeftForNotchPerRotor.get(rotorIDsInOrder.get(i))));
            } else {
                rotorPositions.append(rotorStartPositionsByChar.get(i));
            }
        }

        if (!plugsToUse.isEmpty()) {
            StringBuilder finalPlugs = new StringBuilder();
            plugsToUse.forEach((char1, char2) -> finalPlugs.append(char1).append("|").append(char2).append(","));
            plugs = "<" + finalPlugs.substring(0, finalPlugs.length() - 1) + ">";
        }

        result = String.format(result, rotorsIDs.substring(0, rotorsIDs.length() - 1), rotorPositions, reflectorID.getStringValue(), plugs);

        return result;
    }

    public void setRotorNotchPositionsPerID(Map<Integer, Integer> rotorIDToRotationsLeftForNotchPerRotor) {
        this.rotorIDToRotationsLeftForNotchPerRotor = rotorIDToRotationsLeftForNotchPerRotor;
    }

    @Override
    public MachineConfiguration clone() {
        try {
            MachineConfiguration clone = (MachineConfiguration) super.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}