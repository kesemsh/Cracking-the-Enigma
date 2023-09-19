package machine.engine;

import battlefield.Battlefield;
import exceptions.input.*;
import exceptions.machine.*;
import machine.Machine;
import machine.components.dictionary.Dictionary;
import object.automatic.decryption.data.pre.decryption.PreDecryptionData;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.configuration.MachineConfiguration;
import object.machine.state.MachineState;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.function.Predicate;

public interface MachineEngine {
    Battlefield loadMachineFromXMLFile(InputStream xmlFileStream, Predicate<String> doesBattlefieldNameExist) throws InvalidPathException, IOException, JAXBException, XMLLogicException;

    int getActiveRotorsCountInMachine() throws MachineNotLoadedException;

    int getAllRotorsInStorageCount() throws MachineNotLoadedException;

    int getAllReflectorsInStorageCount() throws MachineNotLoadedException;

    MachineState getMachineState() throws MachineNotLoadedException;

    void checkIfConfigurationIsSet() throws ConfigurationNotSetException;

    void checkIfMachineIsLoaded() throws MachineNotLoadedException;

    void setConfiguration(MachineConfiguration machineConfiguration) throws MachineNotLoadedException;

    String processInput(String messageToProcess, boolean addMessageToHistory, boolean saveMessageForLater) throws MachineNotLoadedException, ConfigurationNotSetException, InvalidCharacterException, EmptyInputException;

    void resetConfiguration() throws MachineNotLoadedException, ConfigurationNotSetException;

    void setRandomConfiguration() throws MachineNotLoadedException;

    int getKeyCount();

    List<MachineHistoryPerConfiguration> getHistoryAndStatistics() throws MachineNotLoadedException, ConfigurationNotSetException;

    MachineConfiguration getCurrentMachineConfiguration() throws MachineNotLoadedException, ConfigurationNotSetException;

    void checkRotorIDsInput(String userInput) throws InvalidRotorIDException, InvalidArgumentsCountException, RotorsDuplicateIDException, EmptyInputException;

    void checkPlugsInput(String userInput) throws InvalidCharacterException, PlugsSameKeyException, PlugsDuplicateKeyException, PlugsKeyAmountException;

    void checkReflectorIDInput(String userInput) throws InvalidReflectorIDException, EmptyInputException;

    void checkRotorStartPositionsInput(String userInput) throws InvalidCharacterException, InvalidArgumentsCountException, EmptyInputException;

    void checkMessageToProcessInput(String userInput) throws InvalidCharacterException, EmptyInputException;

    void insertAccumulatedMessageToHistory();

    List<Character> getAllKeys();

    boolean isConfigurationSet();

    PreDecryptionData getPreDecryptionData(String messageToDecrypt) throws InvalidWordException;

    Dictionary getDictionary();
}