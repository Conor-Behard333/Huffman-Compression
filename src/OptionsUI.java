import Huffman.Decoder;
import Huffman.Encoder;
import Huffman.HuffmanTree;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Creates the components of the GUI and adds their functionality.
 * Makes the UI for the options to compress and the uncompress files.
 */
public class OptionsUI {
    private File fileSelected = null;
    private File outputDir = null;
    private File encoderFile = null;
    private CheckBox saveEncoder = null;

    /**
     * Instantiates a new Options ui.
     * If the boolean 'compress' is true then the compression window will be created
     * if it is false then the decompression window will be created
     *
     * @param title    the title of the GUI window
     * @param compress whether or not the UI is for compression
     */
    public OptionsUI(String title, boolean compress) {
        // creates the stage for the GUI
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> System.exit(0));//exit program if the main window is closed

        //creates the UI to select a file
        Text select = getTextElement("File: ");
        Button selectBtn;
        if (compress) {
            selectBtn = getSelectFileButton(stage, "*.txt");
        } else {
            selectBtn = getSelectFileButton(stage, "*.bin");
        }
        HBox hBox1 = new HBox();

        HBox hBox2 = null;
        if (compress) {
            // creates the UI to save and load compression encoders (only needed for the compression UI)
            Text saveEncoderText = getTextElement("Save encoder: ");
            saveEncoderText.setTranslateX(30);
            saveEncoder = getCheckBoxElement();
            hBox1.getChildren().addAll(select, selectBtn, saveEncoderText, saveEncoder);

            Text useDifferentEncoder = getTextElement("Use different Encoder: ");
            Button selectEncoder = getSelectEncoderButton(stage);
            hBox2 = new HBox();
            hBox2.getChildren().addAll(useDifferentEncoder, selectEncoder);
        } else {
            // adds the UI functions to a horizontal box.
            hBox1.getChildren().addAll(select, selectBtn);
        }


        // creates the UI option to select an output directory
        Text selectOut = getTextElement("Select output location: ");
        Button selectDirBtn = getSelectDirButton(stage);
        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(selectOut, selectDirBtn);

        // creates an ok and back button to allow the user to navigate the UI
        Button okButton = createOkButton(title, compress);
        Button backButton = createBackButton(stage);
        HBox hBox4 = new HBox();
        hBox4.getChildren().addAll(backButton, okButton);

        //adds all the components together
        VBox root = getVBox(hBox1, hBox2, hBox3, hBox4);

        //creates the window with all the components
        if (compress) {
            stage.setScene(new Scene(root, 600, 400));
        } else {
            stage.setScene(new Scene(root, 600, 300));
        }

        //shows the window
        stage.show();
    }

    /**
     * Gets check box element.
     *
     * @return the check box element
     */
    private CheckBox getCheckBoxElement() {
        CheckBox saveEncoder = new CheckBox();
        saveEncoder.setTranslateX(40);
        saveEncoder.setTranslateY(20);
        return saveEncoder;
    }

    /**
     * Gets text element.
     *
     * @param text the text displayed
     * @return the text element
     */
    private Text getTextElement(String text) {
        Text selectOut = new Text(text);
        selectOut.setStyle("-fx-font-size: 30");
        selectOut.setTranslateY(5);
        return selectOut;
    }

    /**
     * Gets v box.
     *
     * @param hBox1 the h box 1
     * @param hBox2 the h box 2
     * @param hBox3 the h box 3
     * @return the v box
     */
    private VBox getVBox(HBox hBox1, HBox hBox2, HBox hBox3, HBox hBox4) {
        VBox root = new VBox();
        hBox1.setPadding(new Insets(20, 0, 20, 10));
        hBox3.setPadding(new Insets(0, 0, 20, 10));
        hBox4.setPadding(new Insets(0, 0, 20, 50));
        hBox3.setTranslateY(20);
        hBox4.setTranslateY(50);

        if (hBox2 != null) {
            hBox2.setPadding(new Insets(0, 0, 20, 10));
            root.getChildren().addAll(hBox1, hBox2, hBox3, hBox4);
        } else {
            root.getChildren().addAll(hBox1, hBox3, hBox4);
        }
        return root;
    }

    /**
     * Create back button.
     *
     * @param stage the stage
     * @return the button
     */
    private Button createBackButton(Stage stage) {
        Button backButton = new Button("Back");
        backButton.setPrefSize(250, 50);
        backButton.setStyle("-fx-font-size: 30");
        backButton.setOnAction(event -> {
            new HomeUI().start(new Stage());
            stage.close();
        });
        return backButton;
    }

    /**
     * Creates ok button.
     *
     * @param title    the title
     * @param compress where it is used to compress or uncompress
     * @return the button
     */
    private Button createOkButton(String title, boolean compress) {
        Button okButton = new Button(title);
        okButton.setPrefSize(250, 50);
        okButton.setStyle("-fx-font-size: 30");
        okButton.setOnAction(event -> {
            if (fileSelected != null && outputDir != null) {
                try {
                    Stage loadingStage = new Stage();
                    ProgressBar loading = getLoadingBar(loadingStage, "Uncompressing Please Wait...", "Uncompressing: ");
                    if (compress) {
                        loading = getLoadingBar(loadingStage, "Compressing Please Wait...", "Compressing: ");
                    }

                    Task runner = runner(compress);
                    loading.progressProperty().bind(runner.progressProperty());
                    new Thread(runner).start();
                    loadingStage.show();
                    runner.setOnSucceeded(closeEvent -> {
                        displayCompletedInfo(compress, loadingStage, runner);
                    });
                    loadingStage.setOnCloseRequest(closeEvent -> System.exit(0));
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Unsuccessful", "Something went wrong!", Arrays.toString(e.getStackTrace()));
                }
            }
        });
        return okButton;
    }

    private void displayCompletedInfo(boolean compress, Stage loadingStage, Task runner) {
        loadingStage.close();
        runner.cancel();
        long ogSize = fileSelected.length();
        long newSize = new File(outputDir.getAbsolutePath() + "\\" + removeExtension(fileSelected.getName()) + "-compressed.bin").length();
        double ratio = ((double) (ogSize - newSize) / ogSize) * 100;

        if (compress) {
            showAlert(Alert.AlertType.INFORMATION, "Successfully compressed",
                    "Successfully Compressed File by " + new DecimalFormat("##.##").format(ratio) + "%",
                    fileSelected.getName() + " was successful compressed and placed in " + outputDir.getAbsolutePath());

        } else {
            showAlert(Alert.AlertType.INFORMATION, "Successfully uncompressed", "Successfully uncompressed",
                    fileSelected.getName() + " was successfully uncompressed and placed in " + outputDir.getAbsolutePath());

        }
    }

    /**
     * Save encoder.
     *
     * @param encoder the encoder
     * @throws IOException the io exception
     */
    private void saveEncoder(Encoder encoder) throws IOException {
        FileOutputStream serializer = new FileOutputStream(outputDir + "\\" + removeExtension(fileSelected.getName()) + "-encoder.ser");
        ObjectOutputStream out = new ObjectOutputStream(serializer);
        out.writeObject(encoder);
        out.close();
        serializer.close();
    }

    /**
     * Gets saved encoder.
     *
     * @return the saved encoder
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    private Encoder getSavedEncoder() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(encoderFile.getAbsolutePath());
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Encoder encoder = (Encoder) in.readObject();
        in.close();
        fileIn.close();
        return encoder;
    }

    /**
     * Removes the txt extension from a file name.
     *
     * @param fileName the file name (example.txt)
     * @return the file name without the extension (example)
     */
    private String removeExtension(String fileName) {
        return fileName.split("\\.")[0];
    }

    /**
     * Remove compressed tag string.
     *
     * @param fileName the file name
     * @return the string
     */
    private String removeCompressedTag(String fileName) {
        return fileName.split("-")[0];
    }

    /**
     * Shows alert.
     *
     * @param alertType   the alert type
     * @param title       the title
     * @param headerText  the header text
     * @param contentText the content text
     */
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);

        if (alertType.equals(Alert.AlertType.ERROR)) {
            TextArea fullMessage = new TextArea(contentText);
            fullMessage.setWrapText(true);
            fullMessage.setEditable(false);
            alert.getDialogPane().setContent(fullMessage);
        } else {
            alert.setContentText(contentText);
        }

        alert.showAndWait();
    }

    /**
     * Gets select file button.
     *
     * @param stage the stage
     * @return the select file button
     */
    private Button getSelectFileButton(Stage stage, String extension) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", extension)
        );
        Button button = new Button("Select .txt File");
        button.setPrefSize(200, 60);
        button.setStyle("-fx-font-size: 18");
        button.setOnAction(event -> {
            if (fileSelected != null) {
                fc.setInitialDirectory(fileSelected.getParentFile());
            }
            fileSelected = fc.showOpenDialog(stage);
            if (fileSelected != null) {
                button.setText(fileSelected.getName());
            }
        });
        return button;
    }

    /**
     * Gets select file button.
     *
     * @param stage the stage
     * @return the select file button
     */
    private Button getSelectEncoderButton(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Ser files", "*.ser")
        );
        Button button = new Button("Select .ser File");
        button.setPrefSize(200, 60);
        button.setStyle("-fx-font-size: 18");
        button.setOnAction(event -> {
            if (encoderFile != null) {
                fc.setInitialDirectory(encoderFile.getParentFile());
            }
            encoderFile = fc.showOpenDialog(stage);
            if (encoderFile != null) {
                button.setText(encoderFile.getName());
            }
        });
        return button;
    }

    /**
     * Gets select directory button.
     *
     * @param stage the stage
     * @return the select dir button
     */
    private Button getSelectDirButton(Stage stage) {
        DirectoryChooser dc = new DirectoryChooser();
        Button button = new Button("Select Output directory");
        button.setPrefSize(250, 60);
        button.setStyle("-fx-font-size: 18");
        button.setOnAction(event -> {
            if (fileSelected != null) {
                dc.setInitialDirectory(fileSelected.getParentFile());
            }
            outputDir = dc.showDialog(stage);
            if (outputDir != null) {
                button.setText(outputDir.getPath());
                button.setTooltip(new Tooltip(button.getText()));
            }
        });
        return button;
    }

    private Task runner(boolean compress) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                if (compress) {
                    HuffmanTree huffman = new HuffmanTree(fileSelected.getAbsolutePath());
                    Encoder encoder = new Encoder(huffman.getCharacterFrequencies(), huffman.getCodes());
                    if (encoderFile != null) {
                        encoder = getSavedEncoder();
                    }

                    if (saveEncoder.isSelected()) {
                        saveEncoder(encoder);
                    }

                    encoder.compress(huffman.getFileContents(), outputDir.getAbsolutePath(), removeExtension(fileSelected.getName()));
                } else {
                    Decoder.decompress(fileSelected.getAbsolutePath(), outputDir.getAbsolutePath(), removeCompressedTag(removeExtension(fileSelected.getName())));
                }
                return null;
            }
        };
    }

    private ProgressBar getLoadingBar(Stage loadingStage, String title, String text) {
        loadingStage.setTitle(title);
        ProgressBar loading = new ProgressBar(0);
        loading.setPrefSize(400, 60);
        loading.setStyle("-fx-accent: #5BC2E7");
        Label progress = new Label(text);
        progress.setPrefWidth(300);
        progress.setTranslateX(10);
        progress.setTranslateY(10);
        progress.setStyle("-fx-font-size: 25");
        HBox hBox = new HBox(progress, loading);
        hBox.setSpacing(20);
        Scene loadingScene = new Scene(hBox, 500, 60);
        loadingStage.setScene(loadingScene);
        return loading;
    }
}
