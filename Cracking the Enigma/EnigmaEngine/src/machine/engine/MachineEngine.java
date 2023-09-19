package machine.engine;

import exceptions.input.*;
import exceptions.machine.*;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.configuration.MachineConfiguration;
import object.machine.state.MachineState;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;

public interface MachineEngine {
    void loadMachineFile(String xmlFilePath) throws InvalidPathException, IOException, JAXBException, XMLLogicException, EmptyInputException;

    int getActiveRotorsCountInMachine() throws MachineNotLoadedException;

    int getAllRotorsInStorageCount() throws MachineNotLoadedException;

    int getAllReflectorsInStorageCount() throws MachineNotLoadedException;

    MachineState showMachineState() throws MachineNotLoadedException;

    void checkIfConfigurationIsSet() throws ConfigurationNotSetException;

    void checkIfMachineIsLoaded() throws MachineNotLoadedException;

    void setConfiguration(MachineConfiguration machineConfiguration) throws MachineNotLoadedException;

    String processInput(String messageToProcess) throws MachineNotLoadedException, ConfigurationNotSetException;

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

    void saveMachineToMagicFile(String filePath) throws MachineNotLoadedException, MachineSaveException;

    void loadMachineFromMagicFile(String filePath) throws MachineNotLoadedException, IOException, EmptyInputException, ClassNotFoundException, MachineLoadException;

    List<Character> getAllKeys();
}