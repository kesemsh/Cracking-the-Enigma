package machine.engine;

import battlefield.Battlefield;
import exceptions.input.*;
import exceptions.machine.*;
import machine.builder.MachineBuilder;
import jaxb.generated.CTEEnigma;
import jaxb.xml.reader.XMLReader;
import machine.Machine;
import machine.components.dictionary.Dictionary;
import object.automatic.decryption.data.pre.decryption.PreDecryptionData;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.configuration.MachineConfiguration;
import validators.InputValidator;
import validators.MachineValidator;
import object.machine.state.MachineState;
import object.numbering.RomanNumber;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.util.*;
import java.util.function.Predicate;

public class MachineEngineImpl implements MachineEngine {
    private Machine machine = null;
    private boolean isConfigurationSet = false;
    private final MachineValidator machineValidator = new MachineValidator();
    private final MachineBuilder machineBuilder = new MachineBuilder();
    private final InputValidator inputValidator = new InputValidator();

    @Override
    public Battlefield loadMachineFromXMLFile(InputStream xmlFileStream, Predicate<String> doesBattlefieldNameExist) throws InvalidPathException, IOException, JAXBException, XMLLogicException {
        CTEEnigma cteEnigma = XMLReader.getEnigmaFromXMLFile(xmlFileStream);

        machineValidator.checkCTEEnigma(cteEnigma, doesBattlefieldNameExist);
        machine = machineBuilder.buildEnigmaMachine(cteEnigma);
        isConfigurationSet = false;

        return machineBuilder.buildBattlefield(cteEnigma, machine);
    }

    @Override
    public int getActiveRotorsCountInMachine() throws MachineNotLoadedException {
        checkIfMachineIsLoaded();

        return machine.getActiveRotorsCount();
    }

    @Override
    public int getAllRotorsInStorageCount() throws MachineNotLoadedException {
        checkIfMachineIsLoaded();

        return machine.getAllRotorsInStorageCount();
    }

    @Override
    public int getAllReflectorsInStorageCount() throws MachineNotLoadedException {
        checkIfMachineIsLoaded();

        return machine.getAllReflectorsInStorageCount();
    }

    public MachineState getMachineState() throws MachineNotLoadedException {
        checkIfMachineIsLoaded();

        return machine.exportState();
    }

    @Override
    public void checkIfConfigurationIsSet() throws ConfigurationNotSetException {
        if (!isConfigurationSet) {
            throw new ConfigurationNotSetException();
        }
    }

    @Override
    public void checkIfMachineIsLoaded() throws MachineNotLoadedException {
        if (machine == null) {
            throw new MachineNotLoadedException();
        }
    }

    @Override
    public void setConfiguration(MachineConfiguration machineConfiguration) throws MachineNotLoadedException {
        checkIfMachineIsLoaded();
        machine.setConfiguration(machineConfiguration, true);
        isConfigurationSet = true;
    }

    @Override
    public String processInput(String messageToProcess, boolean addMessageToHistory, boolean saveMessageForLater) throws MachineNotLoadedException, ConfigurationNotSetException, InvalidCharacterException, EmptyInputException {
        checkIfMachineIsLoaded();
        checkIfConfigurationIsSet();
        messageToProcess = messageToProcess.toUpperCase();
        checkMessageToProcessInput(messageToProcess);

        return machine.processInput(messageToProcess, addMessageToHistory, saveMessageForLater);
    }

    @Override
    public void resetConfiguration() throws MachineNotLoadedException, ConfigurationNotSetException {
        checkIfMachineIsLoaded();
        checkIfConfigurationIsSet();
        machine.resetConfiguration();
    }

    @Override
    public void setRandomConfiguration() throws MachineNotLoadedException {
        List<Integer> rotorIDsInOrder = getRandomRotorIDs();
        List<Character> rotorStartPositionsByChar = getRandomRotorStartPositions();
        RomanNumber reflectorID = getRandomReflectorID();
        Map<Character, Character> plugsToUse = getRandomPlugBoard();

        setConfiguration(new MachineConfiguration(rotorIDsInOrder, rotorStartPositionsByChar, reflectorID, plugsToUse));
    }

    private List<Integer> getRandomRotorIDs() throws MachineNotLoadedException {
        List<Integer> rotorIDsInOrder = new ArrayList<>();
        Random random = new Random();
        int randomNumber;

        for (int i = 0; i < getActiveRotorsCountInMachine(); i++) {
            do {
                randomNumber = random.nextInt(machine.getAllRotorsInStorageCount()) + 1;
            } while (rotorIDsInOrder.contains(randomNumber));

            rotorIDsInOrder.add((randomNumber));
        }

        return rotorIDsInOrder;
    }

    private List<Character> getRandomRotorStartPositions() throws MachineNotLoadedException {
        List<Character> rotorStartPositionsByChar = new ArrayList<>();
        Random random = new Random();

        checkIfMachineIsLoaded();
        for (int i = 0; i < machine.getActiveRotorsCount(); i++) {
            rotorStartPositionsByChar.add(machine.getKeyForIndex(random.nextInt(getKeyCount())));
        }

        return rotorStartPositionsByChar;
    }

    private RomanNumber getRandomReflectorID() throws MachineNotLoadedException {
        checkIfMachineIsLoaded();
        return RomanNumber.values()[new Random().nextInt(machine.getAllReflectorsInStorageCount())];
    }

    private Map<Character, Character> getRandomPlugBoard() throws MachineNotLoadedException {
        Map<Character, Character> plugsToUse = new HashMap<>();
        Random random = new Random();
        Character firstKey;
        Character secondKey;

        checkIfMachineIsLoaded();
        if (isPlugBoardInUse()) {
            List<Character> allKeys = machine.getAllKeys();
            List<Character> availableKeysForUse = new ArrayList<>(allKeys);
            int amountOfPlugs = getRandomPlugsAmount();
            for (int i = 0; i < amountOfPlugs; i++) {
                firstKey = availableKeysForUse.get(random.nextInt(availableKeysForUse.size()));
                availableKeysForUse.remove(firstKey);
                secondKey = availableKeysForUse.get(random.nextInt(availableKeysForUse.size()));
                availableKeysForUse.remove(secondKey);
                plugsToUse.put(firstKey, secondKey);
            }
        }

        return plugsToUse;
    }

    private int getRandomPlugsAmount() throws MachineNotLoadedException {
        checkIfMachineIsLoaded();

        return new Random().nextInt(getKeyCount() / 2) + 1;
    }

    private boolean isPlugBoardInUse() {
        return new Random().nextBoolean();
    }

    @Override
    public int getKeyCount() {
        return machine.getKeyCount();
    }

    @Override
    public List<MachineHistoryPerConfiguration> getHistoryAndStatistics() throws MachineNotLoadedException, ConfigurationNotSetException {
        checkIfMachineIsLoaded();
        checkIfConfigurationIsSet();

        return machine.getHistoryAndStatistics();
    }

    @Override
    public MachineConfiguration getCurrentMachineConfiguration() throws MachineNotLoadedException, ConfigurationNotSetException {
        checkIfMachineIsLoaded();
        checkIfConfigurationIsSet();

        return machine.getCurrentMachineConfiguration();
    }

    @Override
    public void checkRotorIDsInput(String userInput) throws InvalidRotorIDException, InvalidArgumentsCountException, RotorsDuplicateIDException, EmptyInputException {
        inputValidator.checkRotorIDsInput(machine, userInput);
    }

    @Override
    public void checkPlugsInput(String userInput) throws InvalidCharacterException, PlugsSameKeyException, PlugsDuplicateKeyException, PlugsKeyAmountException {
        inputValidator.checkPlugsInput(machine, userInput);
    }

    @Override
    public void checkReflectorIDInput(String userInput) throws InvalidReflectorIDException, EmptyInputException {
        inputValidator.checkReflectorIDInput(machine, userInput);
    }

    @Override
    public void checkRotorStartPositionsInput(String userInput) throws InvalidCharacterException, InvalidArgumentsCountException, EmptyInputException {
        inputValidator.checkRotorStartPositionsInput(machine, userInput);
    }

    @Override
    public void checkMessageToProcessInput(String userInput) throws InvalidCharacterException, EmptyInputException {
        inputValidator.checkMessageToProcessInput(machine, userInput);
    }

    @Override
    public void insertAccumulatedMessageToHistory() {
        machine.insertAccumulatedMessageToHistory();
    }

    @Override
    public List<Character> getAllKeys() {
        return machine.getAllKeys();
    }

    @Override
    public boolean isConfigurationSet() {
        return isConfigurationSet;
    }

    @Override
    public PreDecryptionData getPreDecryptionData(String inputMessage) throws InvalidWordException {
        String originalMessageWithoutExcludedCharacters = machine.getDictionary().getMessageWithoutExcludedCharacters(inputMessage.toUpperCase());
        MachineConfiguration currentConfiguration = machine.getCurrentMachineConfiguration();

        inputValidator.checkIfMessageIncludesOnlyWordsFromDictionary(originalMessageWithoutExcludedCharacters, machine.getDictionary());
        String messageToDecrypt = machine.processInput(originalMessageWithoutExcludedCharacters, true, false);

        return new PreDecryptionData(inputMessage.toUpperCase(), originalMessageWithoutExcludedCharacters, messageToDecrypt, currentConfiguration);
    }

    @Override
    public Dictionary getDictionary() {
        return machine.getDictionary();
    }
}