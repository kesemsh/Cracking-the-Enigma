package validators;

import exceptions.machine.XMLLogicException;
import jaxb.generated.*;
import object.numbering.RomanNumber;
import java.util.*;

public class MachineValidator {
    private  String abc;
    private CTEMachine cteMachine;
    private CTEDecipher cteDecipher;
    private List<CTERotor> allRotors;
    private List<CTEReflector> allReflectors;
    private long ABCCount;

    public void checkCTEEnigma(CTEEnigma enigma) throws XMLLogicException {
        try {
            updateDataMembers(enigma);
            isABCEven();
            isRotorsCountValid();
            doesDecipherExist();
            isAgentsCountValid();
            isMachineRotorsVSAllRotorsCountValid();
            isRotorsIDsUnique();
            isRotorsMappingValid();
            isRotorsNotchValid();
            isReflectorsIDsUnique();
            isReflectorsMappingValid();
        }
        catch (XMLLogicException e) {
            throw new XMLLogicException(e.getMessage());
        }
    }

    private void doesDecipherExist() throws XMLLogicException {
        if (cteDecipher == null) {
            throw new XMLLogicException("No decipher detected! Please enter a new XML file!");
        }
    }

    private void updateDataMembers(CTEEnigma enigma) {
        cteMachine = enigma.getCTEMachine();
        cteDecipher = enigma.getCTEDecipher();
        CTERotors cteRotors = cteMachine.getCTERotors();
        allRotors = cteRotors.getCTERotor();
        allReflectors = cteMachine.getCTEReflectors().getCTEReflector();
        abc = cteMachine.getABC().trim().toUpperCase();
    }

    private void isABCEven() throws XMLLogicException {
        Set<Character> set = new HashSet<>();

        for (Character key: abc.toCharArray()) {
            if(!set.add(key)) {
                throw new XMLLogicException(String.format("ABC contains the letter %s multiple times!", key));
            }
        }

        ABCCount = abc.chars().count();

        if (ABCCount % 2 != 0) {
            throw new XMLLogicException("ABC is not even!");
        }
    }

    private void isRotorsCountValid() throws XMLLogicException {
        if (cteMachine.getRotorsCount() < 2) {
            throw new XMLLogicException(String.format("Expected rotors count of at least 2, but received %d!", cteMachine.getRotorsCount()));
        }
    }

    private void isAgentsCountValid() throws XMLLogicException {
        if (cteDecipher.getAgents() < 2 || cteDecipher.getAgents() > 50) {
            throw new XMLLogicException(String.format("Expected agents count of 2 to 50, but received %d!", cteDecipher.getAgents()));
        }
    }

    private void isMachineRotorsVSAllRotorsCountValid() throws XMLLogicException {
        if (cteMachine.getRotorsCount() > allRotors.size()) {
            throw new XMLLogicException(String.format("You are attempting to use %d rotors, but you may not use more than %d!", cteMachine.getRotorsCount(), allRotors.size()));
        }
    }

    private void isRotorsIDsUnique() throws XMLLogicException {
        Set<Integer> set = new HashSet<>();

        for (CTERotor rotor: allRotors) {
            if (!set.add(rotor.getId())) {
                throw new XMLLogicException(String.format("The rotor ID \"%d\" appears multiple times!", rotor.getId()));
            }
        }

        for (CTERotor rotor: allRotors) {
            if (rotor.getId() < 1 || rotor.getId() > allRotors.size()) {
                throw new XMLLogicException(String.format("Rotor ID \"%d\" is invalid! ID must be a number between 1 and %d!", rotor.getId(), allRotors.size()));
            }
        }
    }

    private void isRotorsMappingValid() throws XMLLogicException {
        for (CTERotor rotor: allRotors) {
            checkRotorMapping(rotor);
        }
    }

    private void checkRotorMapping(CTERotor rotor) throws XMLLogicException {
        Set<String> rightSet = new HashSet<>();
        Set<String> leftSet = new HashSet<>();
        List<CTEPositioning> rotorCTEPositioning= rotor.getCTEPositioning();

        for (CTEPositioning positioning: rotorCTEPositioning) {
            if (!rightSet.add(positioning.getRight().toUpperCase())) {
                throw new XMLLogicException(String.format("In rotor with ID \"%d\" - mapping is invalid! Key \"%s\" appears multiple times in the right side!", rotor.getId(), positioning.getRight().toUpperCase()));
            }
            if (!leftSet.add(positioning.getLeft().toUpperCase())) {
                throw new XMLLogicException(String.format("In rotor with ID \"%d\" - mapping is invalid! Key \"%s\" appears multiple times in the left side!", rotor.getId(), positioning.getLeft().toUpperCase()));
            }
        }

        for (Character key: abc.toCharArray()) {
            if (!rightSet.contains(key.toString())) {
                throw new XMLLogicException(String.format("In rotor with ID \"%d\" - mapping is invalid! Key \"%s\" is not mapped!", rotor.getId(), key.toString().toUpperCase()));
            }
            if (!leftSet.contains(key.toString())) {
                throw new XMLLogicException(String.format("In rotor with ID \"%d\" - mapping is invalid! Key \"%s\" is not mapped!", rotor.getId(), key.toString().toUpperCase()));
            }
        }
    }

    private void isRotorsNotchValid() throws XMLLogicException {
        for (CTERotor rotor: allRotors) {
            if(rotor.getNotch() > ABCCount || rotor.getNotch() < 1){
                throw new XMLLogicException(String.format("In rotor with ID \"%d\" - Notch position \"%d\" is invalid! Notch position may be a number between 1 and %d!", rotor.getId(), rotor.getNotch(), ABCCount));
            }
        }
    }

    private void isReflectorsIDsUnique() throws XMLLogicException {
        Set<String> set = new HashSet<>();

        for (CTEReflector reflector: allReflectors) {
            if (!set.add(reflector.getId())) {
                throw new XMLLogicException(String.format("The reflector ID \"%s\" appears multiple times!", reflector.getId()));
            }
        }

        if (allReflectors.size() > 5 || allReflectors.size() < 1) {
            throw new XMLLogicException(String.format("Expected 1 to 5 reflectors, but received %d!", allReflectors.size()));
        }

        for (CTEReflector reflector: allReflectors) {
            RomanNumber number = RomanNumber.fromString(reflector.getId());
            if (number == null || number.getIntValue() < 1 || number.getIntValue() > allReflectors.size()) {
                final String EXCEPTION_MESSAGE = "Reflector ID \"%s\" is invalid! ID must be a roman number between 1 and %d (%s)!";
                StringBuilder romanNumbers = new StringBuilder();

                for (int i = 1; i <= allReflectors.size(); i++) {
                    romanNumbers.append(RomanNumber.fromInt(i).getStringValue());
                    if (i != allReflectors.size()) {
                        romanNumbers.append(", ");
                    }
                }

                throw new XMLLogicException(String.format(EXCEPTION_MESSAGE, reflector.getId(), allReflectors.size(), romanNumbers));
            }
        }
    }

    private void isReflectorsMappingValid() throws XMLLogicException {
        for (CTEReflector reflector: allReflectors) {
            isReflectorMappingValid(reflector);
        }
    }

    private void isReflectorMappingValid(CTEReflector reflector) throws XMLLogicException {
        Set<Integer> inputSet = new HashSet<>();
        Set<Integer> outputSet = new HashSet<>();
        List<CTEReflect> reflectorCTEReflect= reflector.getCTEReflect();

        for (CTEReflect reflect: reflectorCTEReflect) {
            if (!inputSet.add(reflect.getInput())) {
                throw new XMLLogicException(String.format("In reflector with ID \"%s\" - In input the number %d appears multiple times!", reflector.getId(), reflect.getInput()));
            }
            if (!outputSet.add(reflect.getOutput())) {
                throw new XMLLogicException(String.format("In reflector with ID \"%s\" - In output the number %d appears multiple times!", reflector.getId(), reflect.getOutput()));
            }
        }

        for (Integer number: inputSet) {
            if (number < 1 || number > ABCCount) {
                throw new XMLLogicException(String.format("In reflector with ID \"%s\" - Input number %d is invalid! Numbers may be between 1 and %d!", reflector.getId(), number, ABCCount));
            }
        }

        for (Integer number: outputSet) {
            if (number < 1 || number > ABCCount) {
                throw new XMLLogicException(String.format("In reflector with ID \"%s\" - Output number %d is invalid! Numbers may be between 1 and %d!", reflector.getId(), number, ABCCount));
            }
        }

        if (!Collections.disjoint(inputSet, outputSet)) {
            throw new XMLLogicException(String.format("In reflector with ID \"%s\" - Output and input contains the same number! (Double mapping).", reflector.getId()));
        }

        if (inputSet.size() != (ABCCount / 2)) {
            throw new XMLLogicException(String.format("In reflector with ID \"%s\" - A mapping is missing!", reflector.getId()));
        }
    }
}