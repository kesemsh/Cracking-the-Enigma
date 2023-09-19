package machine.components.storage;

import machine.components.translators.reflector.Reflector;
import object.numbering.RomanNumber;
import machine.components.rotor.Rotor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MachineStorage implements Serializable {
    private final List<Rotor> allRotors;
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
}