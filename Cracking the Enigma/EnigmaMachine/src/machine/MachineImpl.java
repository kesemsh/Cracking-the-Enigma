package machine;

import machine.components.history.MachineHistory;
import object.machine.history.MachineHistoryPerConfiguration;
import machine.components.keyboard.Keyboard;
import machine.components.storage.MachineStorage;
import machine.components.translators.plugs.PlugBoard;
import machine.components.translators.plugs.PlugBoardImpl;
import machine.components.translators.reflector.Reflector;
import machine.components.rotor.Direction;
import machine.components.rotor.Rotor;
import object.machine.configuration.MachineConfiguration;
import object.machine.state.MachineState;
import object.numbering.RomanNumber;

import java.util.*;

public class MachineImpl implements Machine {
    private final Keyboard keyboard;
    private final MachineStorage machineStorage;
    private final MachineHistory machineHistory = new MachineHistory();
    private final int activeRotorsCount;
    private PlugBoard plugBoard;
    private List<Rotor> activeRotors;
    private Reflector activeReflector;
    private MachineConfiguration initialConfiguration = null;

    public MachineImpl(Keyboard keyboard, MachineStorage machineStorage, int activeRotorsCount) {
        this.keyboard = keyboard;
        this.machineStorage = machineStorage;
        this.activeRotorsCount = activeRotorsCount;
    }

    @Override
    public int getActiveRotorsCount() {
        return activeRotorsCount;
    }

    @Override
    public int getAllRotorsInStorageCount() {
        return machineStorage.getAllRotors().size();
    }

    @Override
    public int getAllReflectorsInStorageCount() {
        return machineStorage.getAllReflectors().size();
    }

    @Override
    public int getKeyCount() {
        return keyboard.getKeyCount();
    }

    @Override
    public Character getKeyForIndex(int index) {
        return keyboard.getKeyForIndex(index);
    }

    @Override
    public MachineState exportState() {
        final int possibleRotorsCount = machineStorage.getAllRotors().size();
        int reflectorsInStorageCount = machineStorage.getAllReflectors().size();

        if (initialConfiguration != null) {
            return new MachineState(possibleRotorsCount, activeRotorsCount, reflectorsInStorageCount, machineHistory.getProcessedMessagesCount(), initialConfiguration, getCurrentMachineConfiguration());
        }
        else {
            return new MachineState(possibleRotorsCount, activeRotorsCount, reflectorsInStorageCount, machineHistory.getProcessedMessagesCount());
        }
    }

    @Override
    public void setConfiguration(MachineConfiguration machineConfiguration) {
        initialConfiguration = machineConfiguration;
        setActiveRotors(machineConfiguration.getRotorIDsInOrder());
        setActiveReflector(machineConfiguration.getReflectorID());
        setPlugs(machineConfiguration.getPlugsToUse());
        setRotorsStartPositions(machineConfiguration.getRotorStartPositionsByChar());
        initialConfiguration.setRotorNotchPositionsPerID(getRotorIDToRotationsLeftForNotchPerRotor());
        machineHistory.addConfigurationToHistory(initialConfiguration);
    }

    @Override
    public void resetConfiguration() {
        setConfiguration(initialConfiguration);
    }

    @Override
    public boolean isCharacterInKeyboard(Character charToCheck) {
        return keyboard.isKeyInKeyboard(charToCheck);
    }

    @Override
    public List<MachineHistoryPerConfiguration> getHistoryAndStatistics() {
        return machineHistory.exportMachineHistory();
    }

    @Override
    public MachineConfiguration getCurrentMachineConfiguration() {
        Map<Integer, Integer> rotorIDToRotationsLeftForNotchPerRotor = getRotorIDToRotationsLeftForNotchPerRotor();
        List<Character> currentRotorPositions = getCurrentRotorPositions();

        return new MachineConfiguration(initialConfiguration.getRotorIDsInOrder(), currentRotorPositions, rotorIDToRotationsLeftForNotchPerRotor, initialConfiguration.getReflectorID(), initialConfiguration.getPlugsToUse());
    }

    @Override
    public List<Character> getAllKeys() {
        return keyboard.getAllKeys();
    }

    @Override
    public boolean isConfigurationSet() {
        return initialConfiguration != null;
    }

    private Map<Integer, Integer> getRotorIDToRotationsLeftForNotchPerRotor() {
        Map<Integer, Integer> rotorIDToRotationsLeftForNotchPerRotor = new HashMap<>();

        activeRotors.forEach(x -> rotorIDToRotationsLeftForNotchPerRotor.put(x.getRotorID(), Math.floorMod(x.getNotchPosition() - x.getRotorPosition(), getKeyCount())));
        for (int rotorID : rotorIDToRotationsLeftForNotchPerRotor.keySet()) {
            if (rotorIDToRotationsLeftForNotchPerRotor.get(rotorID) == 0) {
                rotorIDToRotationsLeftForNotchPerRotor.put(rotorID, getKeyCount());
            }
        }

        return rotorIDToRotationsLeftForNotchPerRotor;
    }

    private List<Character> getCurrentRotorPositions() {
        List<Character> result = new ArrayList<>();

        activeRotors.forEach(x -> result.add(keyboard.getKeyForIndex(x.getCurrentPositionKeyIndex())));

        return result;
    }

    private void setRotorsStartPositions(List<Character> rotorStartPositionsByChar) {
        int currRotorToUpdate = 0;

        for (Character startPosKey : rotorStartPositionsByChar) {
            activeRotors.get(currRotorToUpdate).setPosition(keyboard.getIndexForKey(Character.toUpperCase(startPosKey)));
            currRotorToUpdate++;
        }
    }

    private void setPlugs(Map<Character, Character> plugsToUse) {
        plugBoard = new PlugBoardImpl(keyboard.getKeyCount());

        for (Character c : plugsToUse.keySet()) {
            int firstKeyIndex = keyboard.getIndexForKey(c);
            int secondKeyIndex = keyboard.getIndexForKey(plugsToUse.get(c));

            plugBoard.setPlugPair(firstKeyIndex, secondKeyIndex);
        }
    }

    private void setActiveReflector(RomanNumber reflectorID) {
        activeReflector = machineStorage.getAllReflectors().get(reflectorID);
    }

    private void setActiveRotors(List<Integer> rotorIDsInOrder) {
        activeRotors = new ArrayList<>();

        for (Integer currRotorToAdd : rotorIDsInOrder) {
            activeRotors.add(machineStorage.getAllRotors().get(currRotorToAdd - 1));
        }
    }

    @Override
    public String processInput(String stringToProcess) {
        StringBuilder resultString = new StringBuilder();
        Long timeStart = System.nanoTime();

        for (Character currChar : stringToProcess.toCharArray()) {
            resultString.append(processSingleChar(currChar));
        }
        Long timeEnd = System.nanoTime();
        machineHistory.addMessageToHistory(initialConfiguration, stringToProcess, resultString.toString(), timeEnd - timeStart);

        return resultString.toString();
    }

    private Character processSingleChar(Character charToProcess) {
        int resultCharIndex = keyboard.getIndexForKey(charToProcess);

        resultCharIndex = plugBoard.translate(resultCharIndex);
        handleRotorPosition();
        resultCharIndex = translateThroughRotorsInDirection(Direction.Forwards, resultCharIndex);
        resultCharIndex = activeReflector.translate(resultCharIndex);
        resultCharIndex = translateThroughRotorsInDirection(Direction.Backwards, resultCharIndex);
        resultCharIndex = plugBoard.translate(resultCharIndex);

        return keyboard.getKeyForIndex(resultCharIndex);
    }

    private void handleRotorPosition() {
        int currRotorIndex = activeRotors.size() - 1;
        boolean continueRotorMovement = true;

        while (continueRotorMovement) {
            activeRotors.get(currRotorIndex).rotate();
            if (!activeRotors.get(currRotorIndex).isNotchReached() || currRotorIndex == 0) {
                continueRotorMovement = false;
            }

            currRotorIndex--;
        }
    }

    private int translateThroughRotorsInDirection(Direction directionToTranslate, int startingKeyIndex) {
        int currRotorIndex = (directionToTranslate == Direction.Forwards ? activeRotors.size() - 1 : 0);
        int endIndex = (directionToTranslate == Direction.Backwards ? activeRotors.size() : -1);
        int resultKeyIndex = startingKeyIndex + activeRotors.get(currRotorIndex).getRotorPosition();
        int currPosition, nextPosition;

        resultKeyIndex = Math.floorMod(resultKeyIndex, keyboard.getKeyCount());
        while (currRotorIndex != endIndex) {
            currPosition = activeRotors.get(currRotorIndex).getRotorPosition();
            resultKeyIndex = activeRotors.get(currRotorIndex).translate(directionToTranslate, resultKeyIndex);
            if (directionToTranslate == Direction.Forwards) {
                currRotorIndex--;
            }
            else {
                currRotorIndex++;
            }

            if (currRotorIndex != endIndex) {
                nextPosition = activeRotors.get(currRotorIndex).getRotorPosition();
            }
            else {
                nextPosition = 0;
            }

            resultKeyIndex = resultKeyIndex - currPosition + nextPosition;
            resultKeyIndex = Math.floorMod(resultKeyIndex, keyboard.getKeyCount());
        }

        return resultKeyIndex;
    }
}