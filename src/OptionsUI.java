import Huffman.HuffmanTree;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

public class OptionsUI {
    private File fileSelected = null;
    private File outputDir = null;

    public OptionsUI(String title, boolean compress) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> System.exit(0));//exit program if the main window is closed

        Text select = getTextElement("File: ");
        Button selectBtn = getSelectFileButton(stage);
        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(select, selectBtn);

        Text selectOut = getTextElement("Select output location: ");
        Button selectDirBtn = getSelectDirButton(stage);
        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(selectOut, selectDirBtn);

        Button okButton = createOkButton(title, compress);
        Button backButton = createBackButton(stage);
        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(backButton, okButton);

        VBox root = getVBox(hBox1, hBox2, hBox3);

        stage.setScene(new Scene(root, 600, 300));
        stage.show();
    }

    private Text getTextElement(String s) {
        Text selectOut = new Text(s);
        selectOut.setStyle("-fx-font-size: 30");
        selectOut.setTranslateY(5);
        return selectOut;
    }

    private VBox getVBox(HBox hBox1, HBox hBox2, HBox hBox3) {
        VBox root = new VBox();
        hBox1.setPadding(new Insets(20, 0, 20, 10));
        hBox2.setPadding(new Insets(0, 0, 20, 10));
        hBox3.setPadding(new Insets(0, 0, 20, 50));
        hBox2.setTranslateY(20);
        hBox3.setTranslateY(50);
        root.getChildren().addAll(hBox1, hBox2, hBox3);
        return root;
    }

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

    private Button createOkButton(String title, boolean compress) {
        Button okButton = new Button(title);
        okButton.setPrefSize(250, 50);
        okButton.setStyle("-fx-font-size: 30");
        okButton.setOnAction(event -> {
            HuffmanTree huffman = new HuffmanTree();
            if (fileSelected != null && outputDir != null) {
                try {
                    System.out.println(fileSelected.getName());
                    if (compress) {
                        huffman.compress(fileSelected.getAbsolutePath(), outputDir.getAbsolutePath(), removeTxtExtension(fileSelected.getName()));
                        showAlert(Alert.AlertType.INFORMATION, "Successfully compressed", "Successfully compressed",
                                fileSelected.getName() + " was successful compressed and placed in " + outputDir.getAbsolutePath());
                    } else {
                        huffman.uncompress(fileSelected.getAbsolutePath(), outputDir.getAbsolutePath(), removeCompressedTag(removeTxtExtension(fileSelected.getName())));
                        showAlert(Alert.AlertType.INFORMATION, "Successfully uncompressed", "Successfully uncompressed",
                                fileSelected.getName() + " was successfully uncompressed and placed in " + outputDir.getAbsolutePath());
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Unsuccessful", "Something went wrong!", Arrays.toString(e.getStackTrace()));
                }
            }
        });
        return okButton;
    }

    private String removeTxtExtension(String fileName) {
        return fileName.split("\\.")[0];
    }

    private String removeCompressedTag(String fileName) {
        return fileName.split("_")[0];
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private Button getSelectFileButton(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        Button button = new Button("Select txt File");
        button.setPrefSize(200, 60);
        button.setStyle("-fx-font-size: 18");
        button.setOnAction(event -> {
            fileSelected = fc.showOpenDialog(stage);
            if (fileSelected != null) {
                button.setText(fileSelected.getName());
            }
        });
        return button;
    }

    private Button getSelectDirButton(Stage stage) {
        DirectoryChooser dc = new DirectoryChooser();
        Button button = new Button("Select Output directory");
        button.setPrefSize(250, 60);
        button.setStyle("-fx-font-size: 18");
        button.setOnAction(event -> {
            outputDir = dc.showDialog(stage);
            if (outputDir != null) {
                button.setText(outputDir.getPath());
                button.setTooltip(new Tooltip(button.getText()));
            }
        });
        return button;
    }
}
