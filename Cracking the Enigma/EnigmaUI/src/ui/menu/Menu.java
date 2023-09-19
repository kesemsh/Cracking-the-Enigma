package ui.menu;

import ui.api.API;
import ui.menu.options.MenuOption;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    API api = new API();

    public void run() {
        MenuOption selectedOption;

        showWelcomeMessage();
        do {
            showOptions();
            selectedOption = getMenuSelectionFromUser();

            switch (selectedOption) {
                case LoadMachineFromXMLFile:
                    api.loadMachineFromXMLFile();
                    break;
                case ShowMachineState:
                    api.showMachineState();
                    break;
                case SetConfiguration:
                    api.setConfiguration();
                    break;
                case SetRandomConfiguration:
                    api.setRandomConfiguration();
                    break;
                case ProcessInput:
                    api.processInput();
                    break;
                case ResetConfiguration:
                    api.resetConfiguration();
                    break;
                case ShowHistoryAndStatistics:
                    api.showHistoryAndStatistics();
                    break;
                case SaveMachineToMagicFile:
                    api.saveMachineToMagicFile();
                    break;
                case LoadMachineFromMagicFile:
                    api.loadMachineFromMagicFile();
                    break;
                case Exit:
                    showGoodbyeMessage();
                    break;
            }
        } while (selectedOption != MenuOption.Exit);
    }

    private void showGoodbyeMessage() {
        System.out.println("**************************************************");
        System.out.println("*******************| Goodbye! |*******************");
        System.out.println("**************************************************");
        System.out.println();
    }

    private MenuOption getMenuSelectionFromUser() {
        Scanner scanner = new Scanner(System.in);
        boolean isInputValid = false;
        MenuOption selectedOption = null;
        int userInput;

        while (!isInputValid) {
            try {
                System.out.println("Please enter your selection and then press Enter:");
                System.out.println("(Example - 1)");
                userInput = Integer.parseInt(scanner.nextLine());
                if (userInput < 1 || userInput > MenuOption.values().length) {
                    throw new InputMismatchException();
                }
                else {
                    isInputValid = true;
                    selectedOption = MenuOption.fromInt(userInput);
                    System.out.printf("You have selected the option: %s%n", selectedOption.getOptionString());
                }
            } catch (NumberFormatException | InputMismatchException e) {
                System.out.printf("Error - Input must be a number between 1 and %d!%n", MenuOption.values().length);
            } finally {
                API.printDivider();
            }
        }

        return selectedOption;
    }

    private void showOptions() {
        System.out.println(MenuOption.getAllOptions());
    }

    private void showWelcomeMessage() {
        System.out.println("**************************************************");
        System.out.println("*********| Welcome to the Magic Enigma! |*********");
        System.out.println("**************************************************");
        System.out.println();
    }
}