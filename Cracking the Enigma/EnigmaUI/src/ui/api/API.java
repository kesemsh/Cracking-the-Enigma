package ui.api;

import exceptions.input.*;
import exceptions.machine.ConfigurationNotSetException;
import exceptions.machine.MachineNotLoadedException;
import machine.engine.MachineEngine;
import machine.engine.MachineEngineImpl;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.configuration.MachineConfiguration;
import object.numbering.RomanNumber;

import java.util.*;
import java.util.stream.Collectors;

public class API {
    MachineEngine engine;

    public API() {
        this.engine = new MachineEngineImpl();
    }

    public void loadMachineFromXMLFile() {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        boolean tryAgain = true;

        while (tryAgain) {
            System.out.println("Please enter full path for the machine .xml file, and then press Enter:");
            System.out.println("(Example - C:\\enigma.xml)");
            userInput = scanner.nextLine();
            try {
                engine.loadMachineFile(userInput.replaceAll("%20", " "));
                tryAgain = false;
                System.out.println("Machine file loaded successfully.");
            } catch (Exception e) {
                tryAgain = doesUserWantToTryAgainAfterError(e.getMessage());
            } finally {
                printDivider();
            }
        }
    }

    public void showMachineState() {
        try {
            System.out.print(engine.showMachineState());
        } catch (MachineNotLoadedException e) {
            System.out.println(e.getMessage());
        } finally {
            printDivider();
        }
    }

    private String getAllKeysString() {
        StringBuilder allKeysString = new StringBuilder();

        engine.getAllKeys().forEach(allKeysString::append);

        return allKeysString.toString();
    }

    public void setConfiguration() {
        try {
            engine.checkIfMachineIsLoaded();
            List<Integer> rotorIDsInOrder = getRotorIDsFromUser();

            if (rotorIDsInOrder != null) {
                List<Character> rotorStartPositionsByChar = getRotorStartPositionsFromUser();

                if (rotorStartPositionsByChar != null) {
                    RomanNumber reflectorID = getReflectorIDFromUser();

                    if (reflectorID != null) {
                        Map<Character, Character> plugsToUse = getPlugsFromUser();

                        if (plugsToUse != null) {
                            engine.setConfiguration(new MachineConfiguration(rotorIDsInOrder, rotorStartPositionsByChar, reflectorID, plugsToUse));
                            System.out.println("Configuration set successfully.");
                        }
                    }
                }
            }
        } catch (MachineNotLoadedException e) {
            System.out.println(e.getMessage());
        } finally {
            printDivider();
        }
    }

    private Map<Character, Character> getPlugsFromUser() {
        Map<Character, Character> result = null;
        Scanner scanner = new Scanner(System.in);
        String userInput;
        boolean tryAgain = true;

        while (tryAgain) {
            System.out.println("Please enter your desired plugs, character by character, in one line, and then press Enter:");
            System.out.println("(Example - If you wanted the plugs A|B and D|K, enter: ABDK)");
            System.out.println(("Note - If you don't want to use plugs, just press Enter.)"));
            System.out.println("Note - You may only use valid characters for plugs.");
            System.out.printf("All of the following characters in the brackets are valid: [%s]%n", getAllKeysString());
            userInput = scanner.nextLine().toUpperCase();
            try {
                engine.checkPlugsInput(userInput);
                result = convertInputToPlugsMap(userInput);
                tryAgain = false;
            } catch (Exception e) {
                tryAgain = doesUserWantToTryAgainAfterError(e.getMessage());
            } finally {
                printDivider();
            }
        }

        return result;
    }

    private Map<Character, Character> convertInputToPlugsMap(String userInput) {
        Map<Character, Character> result = new HashMap<>();

        char[] inputDividedToChars = userInput.toCharArray();

        for (int i = 0; i < inputDividedToChars.length - 1; i += 2) {
            result.put(inputDividedToChars[i], inputDividedToChars[i + 1]);
        }

        return result;
    }

    private void showAllReflectorSelections() throws MachineNotLoadedException {
        System.out.println("Reflectors In Storage:");
        for (int i = 0; i < engine.getAllReflectorsInStorageCount(); i++) {
            System.out.printf("%d. %s%n", i + 1, RomanNumber.values()[i]);
        }
    }
    
    private RomanNumber getReflectorIDFromUser() throws MachineNotLoadedException {
        RomanNumber result = null;
        Scanner scanner = new Scanner(System.in);
        String userInput;
        boolean tryAgain = true;

        while (tryAgain) {
            showAllReflectorSelections();
            System.out.println("Please enter the number for your desired reflector, and then press Enter:");
            System.out.println("(Example - 1)");
            userInput = scanner.nextLine();
            try {
                engine.checkReflectorIDInput(userInput);
                result = convertInputToReflectorID(userInput);
                tryAgain = false;
            } catch (Exception e) {
                tryAgain = doesUserWantToTryAgainAfterError(e.getMessage());
            } finally {
                printDivider();
            }
        }

        return result;
    }

    private RomanNumber convertInputToReflectorID(String userInput) {
        return RomanNumber.fromInt(Integer.parseInt(userInput));
    }

    private List<Character> getRotorStartPositionsFromUser() throws MachineNotLoadedException {
        List<Character> result = null;
        Scanner scanner = new Scanner(System.in);
        int rotorsCount = engine.getActiveRotorsCountInMachine();
        String userInput;
        boolean tryAgain = true;

        while (tryAgain) {
            System.out.printf("Please enter %d characters for rotor positions, ordered left to right, and then press Enter:%n", rotorsCount);
            System.out.println("(Example - ABC...)");
            System.out.println("Note - You may only use valid characters for rotor start positions.");
            System.out.printf("All of the following characters in the brackets are valid: [%s]%n", getAllKeysString());
            userInput = scanner.nextLine().toUpperCase();
            try {
                engine.checkRotorStartPositionsInput(userInput);
                result = convertInputToRotorStartPositionsList(userInput);
                tryAgain = false;
            } catch (Exception e) {
                tryAgain = doesUserWantToTryAgainAfterError(e.getMessage());
            } finally {
                printDivider();
            }
        }

        return result;
    }

    private List<Character> convertInputToRotorStartPositionsList(String userInput) {
        return Arrays.asList(userInput.chars().mapToObj(c -> (char) c).toArray(Character[]::new));
    }

    private List<Integer> getRotorIDsFromUser() throws MachineNotLoadedException {
        List<Integer> result = null;
        Scanner scanner = new Scanner(System.in);
        boolean tryAgain = true;
        int expectedRotorsCount = engine.getActiveRotorsCountInMachine();
        int allRotorsCount = engine.getAllRotorsInStorageCount();
        String userInput;

        while (tryAgain) {
            System.out.printf("Please enter %d rotor IDs to use in the machine, ordered left to right, %n", expectedRotorsCount);
            System.out.println("Separated by commas, and then press Enter:");
            System.out.println("(Example - FirstRotorID,SecondRotorID...)");
            System.out.printf("Note - Valid rotor ID is a number between 1 and %d.%n", allRotorsCount);
            System.out.println("Note - Each rotor ID may only appear once.");
            userInput = scanner.nextLine();
            try {
                engine.checkRotorIDsInput(userInput);
                result = convertInputToRotorIDsList(userInput);
                tryAgain = false;
            } catch (Exception e) {
                tryAgain = doesUserWantToTryAgainAfterError(e.getMessage());
            } finally {
                printDivider();
            }
        }

        return result;
    }

    private List<Integer> convertInputToRotorIDsList(String userInput) {
        return Arrays.stream(userInput.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
    }

    private boolean doesUserWantToTryAgainAfterError(String errorMessage) {
        Scanner scanner = new Scanner(System.in);
        int userInput = 0;

        System.out.println("An error has occurred:");
        System.out.println(errorMessage);
        System.out.println();
        System.out.println("Would you like to try again, or go to the main menu?");
        System.out.println("1. Try again.");
        System.out.println("2. Main menu.");
        while (userInput != 1 && userInput != 2) {
            try {
                System.out.println("Please enter your selection, and then press Enter:");
                System.out.println("(Example - 1)");
                userInput = Integer.parseInt(scanner.nextLine());
                if (userInput < 1 || userInput > 2) {
                    throw new InputMismatchException();
                }
                else if (userInput == 1) {
                    System.out.println("You have decided to try again.");
                }
                else {
                    System.out.println("Returning to the main menu..");
                }
            } catch (NumberFormatException | InputMismatchException e) {
                System.out.println("Error - Input must be the number 1 or 2!");
            }
        }

        return userInput == 1;
    }

    public void processInput() {
        String userInput = null;

        try {
            engine.checkIfMachineIsLoaded();
            engine.checkIfConfigurationIsSet();
            Scanner scanner = new Scanner(System.in);
            boolean tryAgain = true;

            while (tryAgain) {
                System.out.println("Please enter a message to process, and then press Enter:");
                System.out.println("(Example - OK)");
                System.out.println("Note - You may only use valid characters in your message.");
                System.out.printf("All of the following characters in the brackets are valid: [%s]%n", getAllKeysString());
                userInput = scanner.nextLine().toUpperCase();
                try {
                    engine.checkMessageToProcessInput(userInput);
                    System.out.print("The message was processed to: ");
                    System.out.println(engine.processInput(userInput));
                    tryAgain = false;
                } catch (Exception e) {
                    tryAgain = doesUserWantToTryAgainAfterError(e.getMessage());
                } finally {
                    printDivider();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            printDivider();
        }
    }

    public static void printDivider() {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println();
    }

    public void setRandomConfiguration() {
        try {
            engine.checkIfMachineIsLoaded();
            engine.setRandomConfiguration();
            System.out.println("The randomly selected configuration is:");
            System.out.println(engine.getCurrentMachineConfiguration());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            printDivider();
        }
    }

    public void resetConfiguration() {
        try {
            engine.checkIfMachineIsLoaded();
            engine.checkIfConfigurationIsSet();
            engine.resetConfiguration();
            System.out.println("Reset to the last loaded configuration successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            printDivider();
        }
    }

    public void showHistoryAndStatistics() {
        try {
            List<MachineHistoryPerConfiguration> machineHistoryAndStatistics = engine.getHistoryAndStatistics();

            System.out.println("Machine History: ");
            machineHistoryAndStatistics.forEach(System.out::print);
        } catch (MachineNotLoadedException | ConfigurationNotSetException e) {
            System.out.println(e.getMessage());
        } finally {
            printDivider();
        }
    }

    public void saveMachineToMagicFile() {
        Scanner scanner = new Scanner(System.in);
        String userInput;

        try {
            boolean tryAgain = true;

            engine.checkIfMachineIsLoaded();
            while (tryAgain) {
                System.out.println("Please enter full path for the machine save file (WITHOUT EXTENSION), and then press Enter:");
                System.out.println("(Example - C:\\enigma)");
                System.out.println("Note - It will be saved as a .magic file!");
                userInput = scanner.nextLine();
                try {
                    engine.saveMachineToMagicFile(userInput.replaceAll("%20", " "));
                    tryAgain = false;
                    System.out.println("Machine file saved successfully.");
                } catch (Exception e) {
                    tryAgain = doesUserWantToTryAgainAfterError(e.getMessage());
                } finally {
                    printDivider();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            printDivider();
        }
    }

    public void loadMachineFromMagicFile() {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        boolean tryAgain = true;

        while (tryAgain) {
            System.out.println("Please enter full path for the machine load file (WITHOUT EXTENSION), and then press Enter:");
            System.out.println("(Example - C:\\enigma)");
            System.out.println("Note - File must be a .magic file!");
            userInput = scanner.nextLine();
            try {
                engine.loadMachineFromMagicFile(userInput.replaceAll("%20", " "));
                tryAgain = false;
                System.out.println("Machine file loaded successfully.");
            } catch (Exception e) {
                tryAgain = doesUserWantToTryAgainAfterError(e.getMessage());
            } finally {
                printDivider();
            }
        }
    }
}