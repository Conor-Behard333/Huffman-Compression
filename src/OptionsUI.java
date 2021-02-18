import Huffman.HuffmanTree;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class OptionsUI {
    private File fileSelected = null;
    private File outputDir = null;

    public OptionsUI(String title, boolean compress) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(false);

        Text select = new Text("File: ");
        Button selectBtn = getSelectFileButton(stage);
        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(select, selectBtn);


        Text selectOut = new Text("Select output location: ");
        Button selectDirBtn = getSelectDirButton(stage);
        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(selectOut, selectDirBtn);

        Button ok = new Button(title);
        ok.setOnAction(event -> {
            HuffmanTree huffman = new HuffmanTree();
            if (fileSelected != null && outputDir != null) {
                try {
                    if (compress) {
                        huffman.compress(fileSelected.getAbsolutePath(), outputDir.getAbsolutePath(), fileSelected.getName());
                        System.out.println("Compression Successful");
                    } else {
                        huffman.uncompress(fileSelected.getAbsolutePath(), outputDir.getAbsolutePath(), fileSelected.getName());
                        System.out.println("Uncompress Successful");
                    }
                } catch (Exception e) {
                    System.out.println("Compression Unsuccessful");
                }
            }
        });

        Button cancel = new Button("Back");
        cancel.setOnAction(event -> {
            new HomeUI().start(new Stage());
            stage.close();
        });
        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(cancel, ok);

        VBox root = new VBox();
        root.getChildren().addAll(hBox1, hBox2, hBox3);
        stage.setOnCloseRequest(event -> System.exit(0));//exit program if the main window is closed
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }

    private Button getSelectFileButton(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        Button button = new Button("Select txt File");
        button.setOnAction(event -> {
            fileSelected = fc.showOpenDialog(stage);
            button.setText(fileSelected.getName());
        });
        return button;
    }

    private Button getSelectDirButton(Stage stage) {
        DirectoryChooser dc = new DirectoryChooser();
        Button button = new Button("Select Output directory");
        button.setOnAction(event -> {
            outputDir = dc.showDialog(stage);
            button.setText(outputDir.getPath());
            button.setTooltip(new Tooltip(button.getText()));
        });
        return button;
    }
}
