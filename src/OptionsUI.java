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
        // Creates the stage for the GUI
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> System.exit(0));// Exit program if the main window is closed

        // Creates the UI to select a file
        Text select = getTextElement("File: ");
        Button selectBtn;
        if (compress) {
            selectBtn = getSelectFileButton(stage, "*.txt", "Select .txt File");
        } else {
            selectBtn = getSelectFileButton(stage, "*.bin", "Select .bin File");
        }
        HBox hBox1 = new HBox();

        HBox hBox2 = null;
        if (compress) {
            // Creates the UI to save and load compression encoders (only needed for the compression UI)
            Text saveEncoderText = getTextElement("Save encoder: ");
            saveEncoderText.setTranslateX(30);
            saveEncoder = getCheckBoxElement();
            hBox1.getChildren().addAll(select, selectBtn, saveEncoderText, saveEncoder);

            Text useDifferentEncoder = getTextElement("Use different Encoder: ");
            Button selectEncoder = getSelectEncoderButton(stage);
            hBox2 = new HBox();
            hBox2.getChildren().addAll(useDifferentEncoder, selectEncoder);
        } else {
            // Adds the UI functions to a horizontal box.
            hBox1.getChildren().addAll(select, selectBtn);
        }


        // Creates the UI option to select an output directory
        Text selectOut = getTextElement("Select output location: ");
        Button selectDirBtn = getSelectDirButton(stage);
        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(selectOut, selectDirBtn);

        // Creates an ok and back button to allow the user to navigate the UI
        Button okButton = createOkButton(title, compress);
        Button backButton = createBackButton(stage);
        HBox hBox4 = new HBox();
        hBox4.getChildren().addAll(backButton, okButton);

        // Adds all the components together
        VBox root = getVBox(hBox1, hBox2, hBox3, hBox4);

        // Creates the window with all the components
        if (compress) {
            stage.setScene(new Scene(root, 600, 400));
        } else {
            stage.setScene(new Scene(root, 600, 300));
        }

        // Shows the window
        stage.show();
    }

    /**
     * Gets the check box element which is used to check if the user wants to save an encoding.
     *
     * @return the check box element
     */
    private CheckBox getCheckBoxElement() {
        CheckBox saveEncoder = new CheckBox();
        // Set its position in the window
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
        Text textElement = new Text(text);
        // Sets the font size and the y location
        textElement.setStyle("-fx-font-size: 30");
        textElement.setTranslateY(5);
        return textElement;
    }

    /**
     * Gets a v box which stores each component that is shown in the window.
     *
     * @param hBox1 a hBox containing the UI to select a file
     * @param hBox2 a hBox containing the UI to save and load an encoder (may be null)
     * @param hBox3 a hBox containing the UI to select a output directory
     * @param hBox4 a hBox containing the ok and back buttons
     * @return the v box containing all the hBoxes
     */
    private VBox getVBox(HBox hBox1, HBox hBox2, HBox hBox3, HBox hBox4) {
        VBox root = new VBox();
        // Spaces them so they are not right next to each other
        hBox1.setPadding(new Insets(20, 0, 20, 10));
        hBox3.setPadding(new Insets(0, 0, 20, 10));
        hBox4.setPadding(new Insets(0, 0, 20, 50));
        hBox3.setTranslateY(20);
        hBox4.setTranslateY(50);

        // Only adds the UI to save and load an encoder for the compression UI therfore it may be null
        if (hBox2 != null) {
            hBox2.setPadding(new Insets(0, 0, 20, 10));
            root.getChildren().addAll(hBox1, hBox2, hBox3, hBox4);
        } else {
            root.getChildren().addAll(hBox1, hBox3, hBox4);
        }
        return root;
    }

    /**
     * Creates a back button which when clicked closes the current window and opens the Home UI.
     *
     * @param stage the stage that is currently open
     * @return the back button
     */
    private Button createBackButton(Stage stage) {
        Button backButton = new Button("Back");

        // Sets font and style of the button
        backButton.setPrefSize(250, 50);
        backButton.setStyle("-fx-font-size: 30");

        // Closes window and opens new home window
        backButton.setOnAction(event -> {
            new HomeUI().start(new Stage());
            stage.close();
        });
        return backButton;
    }

    /**
     * Creates ok button that is used to either compress of uncompress a file.
     *
     * @param title    the title for the button
     * @param compress whether it is used to compress or uncompress
     * @return the button
     */
    private Button createOkButton(String title, boolean compress) {
        Button okButton = new Button(title);
        // Sets font and style of the button
        okButton.setPrefSize(250, 50);
        okButton.setStyle("-fx-font-size: 30");

        // Adds functionality for when the button is clicked
        okButton.setOnAction(event -> {
            if (fileSelected != null && outputDir != null) {// only run if the user has selected a file
                try {
                    // Creates a window that shows the programs loading bar
                    Stage loadingStage = new Stage();

                    // Creates the loading bar UI with a title and text, if its for compression
                    // the title and text is slightly different.
                    ProgressBar loading = getLoadingBar(loadingStage, "Uncompressing Please Wait...", "Uncompressing: ");
                    if (compress) {
                        loading = getLoadingBar(loadingStage, "Compressing Please Wait...", "Compressing: ");
                    }

                    // Creates a task object which runs the compression or decompression algorithm
                    Task runner = runner(compress);

                    // Binds the progress of the task running the compression/decompression algorithm to the loading bar
                    loading.progressProperty().bind(runner.progressProperty());

                    // Runs the task in another thread so that the progress bar can be animated on the main thread
                    new Thread(runner).start();

                    // Shows loading bar
                    loadingStage.show();

                    // When the task is complete it will output the results to the user
                    runner.setOnSucceeded(closeEvent -> displayCompletedInfo(compress, loadingStage, runner));

                    // If the user closes the loading bar window then the program shuts down
                    loadingStage.setOnCloseRequest(closeEvent -> System.exit(0));
                } catch (Exception e) {
                    // Alert the user if an error occurred during either compression or decompression
                    showAlert(Alert.AlertType.ERROR, "Unsuccessful", "Something went wrong!", Arrays.toString(e.getStackTrace()));
                }
            } else {
                // Alert the user if they haven't selected a file and an output directory
                showAlert(Alert.AlertType.INFORMATION, "Error", "Error", "Please select a file and an output directory");
            }
        });
        return okButton;
    }

    /**
     * Displays the info about the completed task.
     *
     * @param compress     whether the user is compressing/decompressing a file
     * @param loadingStage the loading bar window
     * @param runner       the task which is running the compression/decompression algorithm
     */
    private void displayCompletedInfo(boolean compress, Stage loadingStage, Task runner) {
        // Closes the loading bar window
        loadingStage.close();

        // Stops the task
        runner.cancel();

        if (compress) {
            // Calculate the compression ratio from compression the file
            long ogSize = fileSelected.length();
            long newSize = new File(outputDir.getAbsolutePath() + "\\" + removeExtension(fileSelected.getName()) + "-compressed.bin").length();
            double ratio = ((double) (ogSize - newSize) / ogSize) * 100;

            // Displays to the user that the compression was successful
            showAlert(Alert.AlertType.INFORMATION, "Successfully compressed",
                    "Successfully Compressed File by " + new DecimalFormat("##.##").format(ratio) + "%",
                    fileSelected.getName() + " was successful compressed and placed in " + outputDir.getAbsolutePath());
        } else {
            // Displays to the user that the decompression was successful
            showAlert(Alert.AlertType.INFORMATION, "Successfully uncompressed", "Successfully uncompressed",
                    fileSelected.getName() + " was successfully uncompressed and placed in " + outputDir.getAbsolutePath());
        }
    }

    /**
     * Saves the encoder object as a .ser file.
     *
     * @param encoder the encoder object to be saved
     * @throws IOException a possible io exception
     */
    private void saveEncoder(Encoder encoder) throws IOException {
        FileOutputStream serializer = new FileOutputStream(outputDir + "\\" + removeExtension(fileSelected.getName()) + "-encoder.ser");
        ObjectOutputStream out = new ObjectOutputStream(serializer);
        out.writeObject(encoder);
        out.close();
        serializer.close();
    }

    /**
     * Gets a saved encoder object.
     *
     * @return the saved encoder
     * @throws IOException            a possible io exception
     * @throws ClassNotFoundException a possible class not found exception
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
     * Removes an extension from a file name.
     *
     * @param fileName the file name (example.txt)
     * @return the file name without the extension (example)
     */
    private String removeExtension(String fileName) {
        return fileName.split("\\.")[0];
    }

    /**
     * Removes the '-compressed' tag string.
     *
     * @param fileName the file name with '-compressed'
     * @return the file name without '-compressed'
     */
    private String removeCompressedTag(String fileName) {
        return fileName.split("-")[0];
    }

    /**
     * Shows alert to the user.
     * <p>
     * Used to display errors and confirmations.
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
     * Gets the select file button.
     * <p>
     * When pressed the user can locate a .txt or .bin file in their file system to compress/decompress
     *
     * @param stage     the current window
     * @param extension the file extension that the file chooser can select (i.e .txt or .bin)
     * @return the select file button
     */
    private Button getSelectFileButton(Stage stage, String extension, String buttonName) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                // Filter only files with a specified extension
                new FileChooser.ExtensionFilter("Text Files", extension)
        );
        Button button = new Button(buttonName);
        // Set the style and size of the button
        button.setPrefSize(200, 60);
        button.setStyle("-fx-font-size: 18");

        // Set the action for what happens when the button is pressed
        button.setOnAction(event -> {
            if (fileSelected != null) {
                // sets the initial directory to the directory of where the user selected the previous file
                fc.setInitialDirectory(fileSelected.getParentFile());
            }

            // Allows the user to navigate their file system and select a file
            fileSelected = fc.showOpenDialog(stage);
            if (fileSelected != null) {
                button.setText(fileSelected.getName());
            }
        });
        return button;
    }

    /**
     * Gets select file button.
     * <p>
     * When pressed the user can locate a .ser file in their file system to save
     *
     * @param stage the stage
     * @return the select file button
     */
    private Button getSelectEncoderButton(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                // Filter only files with the .ser extension
                new FileChooser.ExtensionFilter("Ser files", "*.ser")
        );
        Button button = new Button("Select .ser File");
        button.setPrefSize(200, 60);
        button.setStyle("-fx-font-size: 18");

        // Set the action for what happens when the button is pressed
        button.setOnAction(event -> {
            if (encoderFile != null) {
                // Sets the initial directory to the directory of where the user selected the previous file
                fc.setInitialDirectory(fileSelected.getParentFile());
            }
            // Allows the user to navigate their file system and select a file
            encoderFile = fc.showOpenDialog(stage);
            if (encoderFile != null) {
                button.setText(encoderFile.getName());
            }
        });
        return button;
    }

    /**
     * Gets select directory button.
     * <p>
     * When pressed the user can locate a directory in their file system to write the file to
     *
     * @param stage the stage
     * @return the select dir button
     */
    private Button getSelectDirButton(Stage stage) {
        DirectoryChooser dc = new DirectoryChooser();
        Button button = new Button("Select Output directory");

        // Set the style and size of the button
        button.setPrefSize(250, 60);
        button.setStyle("-fx-font-size: 18");
        button.setOnAction(event -> {
            if (fileSelected != null) {
                // Sets the initial directory to the directory of where the user selected the previous file
                dc.setInitialDirectory(fileSelected.getParentFile());
            }

            // Allows the user to navigate their file system and select a file directory
            outputDir = dc.showDialog(stage);
            if (outputDir != null) {
                button.setText(outputDir.getPath());
                button.setTooltip(new Tooltip(button.getText()));
            }
        });
        return button;
    }

    /**
     * Task which runs either the compression or decompression algorithm
     *
     * @param compress the compress
     * @return the task
     */
    private Task runner(boolean compress) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                if (compress) {
                    // Create a huffman tree for the file selected
                    HuffmanTree huffman = new HuffmanTree(fileSelected.getAbsolutePath());
                    Encoder encoder;

                    if (encoderFile != null) {
                        // If the user has selected a saved encoder file load the encoder object
                        encoder = getSavedEncoder();
                    } else {
                        // Create the encoder using the huffman tree
                        encoder = new Encoder(huffman.getCharacterFrequencies(), huffman.getCodes());
                    }

                    // Save the encoder object if the user selects the 'save encoder' option
                    if (saveEncoder.isSelected()) {
                        saveEncoder(encoder);
                    }

                    // Compress the file
                    encoder.compress(huffman.getFileContents(), outputDir.getAbsolutePath(), removeExtension(fileSelected.getName()));
                } else {
                    // Decompress the file
                    Decoder.decompress(fileSelected.getAbsolutePath(), outputDir.getAbsolutePath(), removeCompressedTag(removeExtension(fileSelected.getName())));
                }
                return null;
            }
        };
    }

    /**
     * Gets the loading bar.
     *
     * @param loadingStage the loading stage
     * @param title        the title for the loading window
     * @param text         the text fot the loading window
     * @return the loading bar
     */
    private ProgressBar getLoadingBar(Stage loadingStage, String title, String text) {
        // Creates the progress bar
        ProgressBar loading = new ProgressBar(0);
        loading.setPrefSize(400, 60);
        loading.setStyle("-fx-accent: #5BC2E7");

        // Creates a label for the progress bar
        Label progress = new Label(text);
        progress.setPrefWidth(300);
        progress.setTranslateX(10);
        progress.setTranslateY(10);
        progress.setStyle("-fx-font-size: 25");

        // Adds the progress bar and the label to a HBox
        HBox hBox = new HBox(progress, loading);
        hBox.setSpacing(20);

        // Adds the HBox to the window
        Scene loadingScene = new Scene(hBox, 500, 60);
        loadingStage.setTitle(title);
        loadingStage.setScene(loadingScene);
        return loading;
    }
}
