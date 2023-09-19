package machine.engine;

import exceptions.input.*;
import exceptions.machine.*;
import machine.builder.MachineBuilder;
import jaxb.generated.CTEEnigma;
import jaxb.xml.reader.XMLReader;
import machine.Machine;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.configuration.MachineConfiguration;
import validators.InputValidator;
import validators.MachineValidator;
import object.machine.state.MachineState;
import object.numbering.RomanNumber;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;

public class MachineEngineImpl implements MachineEngine {
    private Machine machine = null;
    private boolean isConfigurationSet = false;
    MachineValidator machineValidator = new MachineValidator();
    MachineBuilder machineBuilder = new MachineBuilder();
    InputValidator inputValidator = new InputValidator();

    @Override
    public void loadMachineFile(String xmlFilePath) throws InvalidPathException, IOException, JAXBException, XMLLogicException, EmptyInputException {
        inputValidator.checkFilePath(xmlFilePath, ".xml");

        CTEEnigma cteEnigma = XMLReader.getEnigmaFromXMLFile(xmlFilePath);

        machineValidator.checkCTEEnigma(cteEnigma);
        machine = machineBuilder.buildEnigmaMachine(cteEnigma);
        isConfigurationSet = false;
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

    public MachineState showMachineState() throws MachineNotLoadedException {
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
        machine.setConfiguration(machineConfiguration);
        isConfigurationSet = true;
    }

    @Override
    public String processInput(String messageToProcess) throws MachineNotLoadedException, ConfigurationNotSetException {
        checkIfMachineIsLoaded();
        checkIfConfigurationIsSet();

        return machine.processInput(messageToProcess);
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
    public void saveMachineToMagicFile(String filePath) throws MachineNotLoadedException, MachineSaveException {
        checkIfMachineIsLoaded();

        if (filePath.isEmpty()) {
            throw new MachineSaveException("Error - Machine save file name must not be empty!");
        }

        filePath = filePath + ".magic";
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            out.writeObject(machine);
        } catch (Exception e) {
            throw new MachineSaveException();
        }
    }

    @Override
    public void loadMachineFromMagicFile(String filePath) throws IOException, EmptyInputException, MachineLoadException {

        if (filePath.isEmpty()) {
            throw new MachineLoadException("Error - Machine load file name must not be empty!");
        }

        filePath = filePath + ".magic";
        inputValidator.checkFilePath(filePath, ".magic");
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(filePath)))) {
            machine = (Machine) in.readObject();
            isConfigurationSet = machine.isConfigurationSet();
        } catch (Exception e) {
            throw new MachineLoadException();
        }
    }

    @Override
    public List<Character> getAllKeys() {
        return machine.getAllKeys();
    }
}