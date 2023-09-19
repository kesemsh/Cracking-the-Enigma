package components.file.manager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.function.Consumer;

public class FileManager {
    private final StringProperty loadedFilePath;
    private Stage primaryStage;
    private Consumer<String> loadMachineFromXMLFileConsumer;
    private Consumer<String> loadMachineFromMAGICFileConsumer;
    private Consumer<String> saveMachineToMAGICFileConsumer;
    @FXML private TextField loadedFilePathTextField;
    @FXML private Button saveMachineToMAGICFileButton;

    public FileManager() {
        loadedFilePath = new SimpleStringProperty();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showLoadedFilePath() {
        loadedFilePathTextField.setText(loadedFilePath.get());
    }

    public void setUpFileManager(Consumer<String> loadMachineFromXMLFileConsumer, Consumer<String> loadMachineFromMAGICFileConsumer, Consumer<String> saveMachineToMAGICFileConsumer, BooleanProperty isMachineLoaded) {
        this.loadMachineFromXMLFileConsumer = loadMachineFromXMLFileConsumer;
        this.loadMachineFromMAGICFileConsumer = loadMachineFromMAGICFileConsumer;
        this.saveMachineToMAGICFileConsumer = saveMachineToMAGICFileConsumer;
        saveMachineToMAGICFileButton.disableProperty().bind(isMachineLoaded.not());
    }

    @FXML
    private void loadMachineFromFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Machine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML / MAGIC Files ", "*.xml", "*.magic"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files ", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MAGIC Files ", "*.magic"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file == null) {
            return;
        }

        loadedFilePath.set(file.getAbsolutePath());
        handleFileLoad();
    }

    private void handleFileLoad() {
        if (loadedFilePath.get().endsWith(".xml")) {
            loadMachineFromXMLFileConsumer.accept(loadedFilePath.get());
        }
        else {
            loadMachineFromMAGICFileConsumer.accept(loadedFilePath.get());
        }
    }

    @FXML
    private void saveMachineToMAGICFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("Enigma.magic");
        fileChooser.setTitle("Save Machine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MAGIC Files ", "*.magic"));
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file == null) {
            return;
        }

        saveMachineToMAGICFileConsumer.accept(file.getAbsolutePath());
    }
}