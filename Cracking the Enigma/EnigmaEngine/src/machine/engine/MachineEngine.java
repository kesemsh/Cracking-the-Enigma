package machine.engine;

import exceptions.input.*;
import exceptions.machine.*;
import machine.automatic.decryption.decrypted.message.candidate.DecryptedMessageCandidate;
import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;
import machine.components.dictionary.Dictionary;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.configuration.MachineConfiguration;
import object.machine.state.MachineState;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;

public interface MachineEngine {
    void loadMachineFromXMLFile(String xmlFilePath) throws InvalidPathException, IOException, JAXBException, XMLLogicException, EmptyInputException;

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

    void saveMachineToMAGICFile(String filePath) throws MachineNotLoadedException, MachineSaveException;

    void loadMachineFromMAGICFile(String filePath) throws MachineNotLoadedException, IOException, EmptyInputException, ClassNotFoundException, MachineLoadException;

    List<Character> getAllKeys();

    boolean isConfigurationSet();

    PreDecryptionData getPreDecryptionData(DecryptionInputData decryptionInputData) throws InvalidWordException;

    void startAutomaticDecryption();

    void pauseAutomaticDecryption();

    void resumeAutomaticDecryption();

    void stopAutomaticDecryption();

    Dictionary getDictionary();

    int getAgentsCount();

    void checkIfPaused();

    boolean isDecryptedMessageCorrect(DecryptedMessageCandidate decryptedMessageCandidate);
}