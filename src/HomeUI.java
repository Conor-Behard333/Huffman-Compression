import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The type Home ui.
 */
public class HomeUI extends Application {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Compress or Uncompress file");
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> System.exit(0));

        Button compress = getButton("Compress file");
        compress.setOnAction(event -> {
            new OptionsUI("Compress", true);
            stage.close();
        });

        Button uncompress = getButton("Uncompress file");
        uncompress.setOnAction(event -> {
            new OptionsUI("Uncompress", false);
            stage.close();
        });

        VBox vBox = new VBox(compress, uncompress);
        Scene scene = new Scene(vBox, 500, 200);
        stage.setScene(scene);
        stage.show();
    }

    private Button getButton(String s) {
        Button compress = new Button(s);
        compress.setPrefSize(520, 100);
        compress.setStyle("-fx-font-size: 50");
        return compress;
    }
}