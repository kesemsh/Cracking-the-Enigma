package validators;

import exceptions.input.*;
import exceptions.machine.InvalidFileTypeException;
import exceptions.machine.InvalidMachinePathException;
import exceptions.machine.NotAFileException;
import machine.Machine;
import machine.components.dictionary.Dictionary;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class InputValidator {
    public void checkFilePath(String xmlFilePath, String requiredFileExtension) throws InvalidMachinePathException, NotAFileException, InvalidFileTypeException, EmptyInputException {
        Path filePath;

        checkIfInputIsEmpty(xmlFilePath);
        try {
            filePath = Paths.get(xmlFilePath);
        } catch (InvalidPathException e) {
            throw new InvalidMachinePathException();
        }

        if (!Files.isRegularFile(filePath)) {
            throw new NotAFileException();
        }

        if (!filePath.toString().endsWith(requiredFileExtension)) {
            throw new InvalidFileTypeException(filePath.getFileName().toString());
        }
    }

    private void checkIfInputIsEmpty(String userInput) throws EmptyInputException {
        if (userInput.isEmpty()) {
            throw new EmptyInputException();
        }
    }

    public void checkRotorIDsInput(Machine machine, String userInput) throws RotorsDuplicateIDException, InvalidArgumentsCountException, InvalidRotorIDException, EmptyInputException {
        checkIfInputIsEmpty(userInput);
        int expectedRotorsCount = machine.getActiveRotorsCount();

        for (String s : userInput.split(",")) {
            checkIfReceivedInputIsRotorID(machine, s);
        }

        if (userInput.split(",").length != expectedRotorsCount) {
            throw new InvalidArgumentsCountException(expectedRotorsCount, userInput.split(",").length);
        }

        List<Integer> listFromInput = Arrays.stream(userInput.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());

        for (int currID : listFromInput) {
            if (Collections.frequency(listFromInput, currID) > 1) {
                throw new RotorsDuplicateIDException(currID);
            }
        }
    }

    private  void checkIfReceivedInputIsRotorID(Machine machine, String receivedInput) throws InvalidRotorIDException {
        final int MAXIMUM_ROTOR_ID = machine.getAllRotorsInStorageCount();

        try {
            int receivedID = Integer.parseInt(receivedInput);

            if (receivedID < 1 || receivedID > MAXIMUM_ROTOR_ID) {
                throw new InvalidRotorIDException(receivedInput, MAXIMUM_ROTOR_ID);
            }
        } catch (NumberFormatException e) {
            throw new InvalidRotorIDException(receivedInput, MAXIMUM_ROTOR_ID);
        }
    }

    public void checkPlugsInput(Machine machine, String userInput) throws InvalidCharacterException, PlugsKeyAmountException, PlugsSameKeyException, PlugsDuplicateKeyException {
        char[] inputDividedToChars = userInput.toCharArray();
        int keyCount = machine.getKeyCount();

        for (Character c : inputDividedToChars) {
            checkIfReceivedInputIsValidCharacter(machine, c);
        }

        if (inputDividedToChars.length % 2 != 0 || inputDividedToChars.length > keyCount) {
            throw new PlugsKeyAmountException(inputDividedToChars.length, keyCount);
        }

        for (int i = 0; i < inputDividedToChars.length - 1; i++) {
            if (inputDividedToChars[i] == inputDividedToChars[i + 1]) {
                throw new PlugsSameKeyException(inputDividedToChars[i]);
            }
            else {
                for (int j = i + 1; j < inputDividedToChars.length; j++) {
                    if (inputDividedToChars[i] == inputDividedToChars[j]) {
                        throw new PlugsDuplicateKeyException(inputDividedToChars[i]);
                    }
                }
            }
        }
    }

    private void checkIfReceivedInputIsValidCharacter(Machine machine, Character c) throws InvalidCharacterException {
        if (!machine.isCharacterInKeyboard(c)) {
            throw new InvalidCharacterException(c);
        }
    }

    public void checkReflectorIDInput(Machine machine, String userInput) throws InvalidReflectorIDException, EmptyInputException {
        checkIfInputIsEmpty(userInput);
        checkIfReceivedInputIsValidReflectorID(machine, userInput);
    }

    private void checkIfReceivedInputIsValidReflectorID(Machine machine, String receivedInput) throws InvalidReflectorIDException {
        final int MAXIMUM_REFLECTOR_ID = machine.getAllReflectorsInStorageCount();

        try {
            int receivedID = Integer.parseInt(receivedInput);

            if (receivedID < 1 || receivedID > MAXIMUM_REFLECTOR_ID) {
                throw new InvalidReflectorIDException(receivedInput);
            }
        } catch (NumberFormatException e) {
            throw new InvalidReflectorIDException(receivedInput);
        }
    }

    public void checkRotorStartPositionsInput(Machine machine, String userInput) throws InvalidCharacterException, InvalidArgumentsCountException, EmptyInputException {
        checkIfInputIsEmpty(userInput);
        int rotorsCount = machine.getActiveRotorsCount();

        for (Character c : userInput.toCharArray()) {
            checkIfReceivedInputIsValidCharacter(machine, c);
        }

        if (userInput.toCharArray().length != rotorsCount) {
            throw new InvalidArgumentsCountException(rotorsCount, userInput.toCharArray().length);
        }
    }

    public void checkMessageToProcessInput(Machine machine, String userInput) throws InvalidCharacterException, EmptyInputException {
        checkIfInputIsEmpty(userInput);

        for (Character c : userInput.toCharArray()) {
            checkIfReceivedInputIsValidCharacter(machine, c);
        }
    }

    public void checkIfMessageIncludesOnlyWordsFromDictionary(String originalMessageWithoutExcludedCharacters, Dictionary dictionary) throws InvalidWordException {
        if (!dictionary.areWordsInDictionary(Arrays.asList(originalMessageWithoutExcludedCharacters.split(" ")))) {
            throw new InvalidWordException(dictionary.getInvalidWordFromList(Arrays.asList(originalMessageWithoutExcludedCharacters.split(" "))));
        }
    }
}