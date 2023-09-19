package machine;

import machine.components.dictionary.Dictionary;
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
    private final Dictionary dictionary;
    private final int activeRotorsCount;
    private final int agentsCount;
    private MachineStorage machineStorage;
    private MachineHistory machineHistory = new MachineHistory();
    private PlugBoard plugBoard;
    private List<Rotor> activeRotors;
    private Reflector activeReflector;
    private MachineConfiguration initialConfiguration = null;

    public MachineImpl(Keyboard keyboard, MachineStorage machineStorage, Dictionary dictionary, int activeRotorsCount, int agentsCount) {
        this.keyboard = keyboard;
        this.machineStorage = machineStorage;
        this.dictionary = dictionary;
        this.activeRotorsCount = activeRotorsCount;
        this.agentsCount = agentsCount;
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
    public void setConfiguration(MachineConfiguration machineConfiguration, boolean saveToHistory) {
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
        setConfiguration(initialConfiguration, true);
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
    public MachineConfiguration getInitialMachineConfiguration() {
        return initialConfiguration;
    }

    @Override
    public List<Character> getAllKeys() {
        return keyboard.getAllKeys();
    }

    @Override
    public boolean isConfigurationSet() {
        return initialConfiguration != null;
    }

    @Override
    public MachineStorage getMachineStorage() {
        return machineStorage;
    }

    @Override
    public Dictionary getDictionary() {
        return dictionary;
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
    public String processInput(String stringToProcess, boolean addMessageToHistory, boolean saveMessageForLater) {
        StringBuilder resultString = new StringBuilder();
        Long timeStart = System.nanoTime();

        for (Character currChar : stringToProcess.toCharArray()) {
            resultString.append(processSingleChar(currChar));
        }
        Long timeEnd = System.nanoTime();
        if (addMessageToHistory) {
            machineHistory.addMessageToHistory(initialConfiguration, stringToProcess, resultString.toString(), timeEnd - timeStart);
        } else if (saveMessageForLater) {
            machineHistory.saveMessageForLater(initialConfiguration, stringToProcess, resultString.toString(), timeEnd - timeStart);
        }

        return resultString.toString();
    }

    public void insertAccumulatedMessageToHistory() {
        machineHistory.insertAccumulatedMessageToHistory();
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

    @Override
    public Machine clone() {
        try {
            MachineImpl clone = (MachineImpl) super.clone();

            clone.initialConfiguration = initialConfiguration.clone();
            clone.machineStorage = machineStorage.clone();
            clone.activeRotors = new ArrayList<>();
            activeRotors.forEach(originalRotor -> {
                clone.machineStorage.getAllRotors().forEach(rotorCopy -> {
                    if (rotorCopy.getRotorID() == originalRotor.getRotorID()) {
                        clone.activeRotors.add(rotorCopy);
                    }
                });
            });
            clone.activeReflector = activeReflector.clone();
            clone.machineHistory = new MachineHistory();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int getAgentsCount() {
        return agentsCount;
    }
}