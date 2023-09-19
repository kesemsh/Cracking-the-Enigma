package machine.components.storage;

import machine.components.translators.reflector.Reflector;
import object.numbering.RomanNumber;
import machine.components.rotor.Rotor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MachineStorage implements Serializable, Cloneable {
    private List<Rotor> allRotors;
    private final Map<RomanNumber, Reflector> allReflectors;

    public MachineStorage(List<Rotor> allRotors, Map<RomanNumber, Reflector> allReflectors) {
        this.allRotors = allRotors;
        this.allReflectors = allReflectors;
    }

    public List<Rotor> getAllRotors() {
        return allRotors;
    }

    public Map<RomanNumber, Reflector> getAllReflectors() {
        return allReflectors;
    }

    @Override
    public MachineStorage clone() {
        try {
            MachineStorage clone = (MachineStorage) super.clone();

            clone.allRotors = new ArrayList<>();
            allRotors.forEach(rotor -> clone.allRotors.add(rotor.clone()));

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}