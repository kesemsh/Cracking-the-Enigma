package ui.menu.options;

public enum MenuOption {
    LoadMachineFromXMLFile(1, "1. Load Machine From XML File."),
    ShowMachineState(2, "2. Show Machine State."),
    SetConfiguration(3, "3. Set Configuration."),
    SetRandomConfiguration(4, "4. Set Random Configuration."),
    ProcessInput(5, "5. Process Input."),
    ResetConfiguration(6, "6. Reset Configuration."),
    ShowHistoryAndStatistics(7, "7. Show History and Statistics."),
    SaveMachineToMagicFile(8, "8. Save Machine to Magic File."),
    LoadMachineFromMagicFile(9, "9. Load Machine From Magic File."),
    Exit(10, "10. Exit.");

    private final int optionNumber;
    private final String optionString;

    MenuOption(int optionNumber, String optionString) {
        this.optionNumber = optionNumber;
        this.optionString = optionString;
    }

    public String getOptionString() {
        return optionString;
    }

    public static MenuOption fromInt(int number) {
        for (MenuOption option : MenuOption.values()) {
            if (option.optionNumber == number) {
                return option;
            }
        }

        return null;
    }

     public static String getAllOptions() {
        StringBuilder allOptions = new StringBuilder();
        final String newLine = System.lineSeparator();

        for (MenuOption option : MenuOption.values()) {
            allOptions.append(option.optionString).append(newLine);
        }

        return allOptions.toString();
     }
}
