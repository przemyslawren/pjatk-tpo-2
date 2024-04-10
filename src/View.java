import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class View extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        initializeWindow();
    }

    private void initializeWindow() {


        BorderPane borderPane = new BorderPane();

        primaryStage = new Stage();
        TextField textField = new TextField("Pole tekstowe");
        Label label2 = new Label("Etykieta 2");
        Label label3 = new Label("Etykieta 3");

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(textField, label2, label3);

        Scene scene = new Scene(hbox, 300, 100);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Przykład");
        primaryStage.show();
    }
}
