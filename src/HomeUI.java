import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HomeUI extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Compress or Uncompress file");
        stage.setResizable(false);
        Button compress = new Button("Compress file");
        compress.setOnAction(event -> {
            new OptionsUI("Compress", true);
            stage.close();
        });

        Button uncompress = new Button("Uncompress file");
        uncompress.setOnAction(event -> {
            new OptionsUI("Uncompress", false);
            stage.close();
        });

        VBox vBox = new VBox(compress, uncompress);
        Scene scene = new Scene(vBox, 500, 100);
        stage.setScene(scene);
        stage.show();
    }
}