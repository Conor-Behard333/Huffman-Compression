import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The main class where the program is run from.
 * Initialises and runs the UI.
 */
public class HomeUI extends Application {
    /**
     * The entry point of application.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        // Set the title of the window and make it non-resizable
        stage.setTitle("Compress or Uncompress file");
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> System.exit(0));// Closes the program when the user closes the window

        // Create a compress button that opens the Compress UI option when clicked
        Button compress = getButton("Compress file");
        compress.setOnAction(event -> {
            new OptionsUI("Compress", true);
            stage.close();
        });

        // Create an uncompress button that opens the Uncompress UI option when clicked
        Button uncompress = getButton("Uncompress file");
        uncompress.setOnAction(event -> {
            new OptionsUI("Uncompress", false);
            stage.close();
        });

        // Adds the buttons to the window
        VBox vBox = new VBox(compress, uncompress);
        Scene scene = new Scene(vBox, 500, 200);
        stage.setScene(scene);
        stage.show(); // Shows the window
    }

    /**
     * Creates a button with specific size and style
     *
     * @param text the text displayed on the button
     * @return Button
     */
    private Button getButton(String text) {
        Button compress = new Button(text);
        compress.setPrefSize(520, 100);
        compress.setStyle("-fx-font-size: 50");
        return compress;
    }
}