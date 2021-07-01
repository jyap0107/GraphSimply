package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("GraphSimply.fxml"));
        stage.setTitle("Sample 2");
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }
    //TODO
    // DISABLE INTERACTION ONCLICK UNTIL POP-UP MENU IS CLOSED.
    // IMPLEMENT SELF LOOP AND TWO NODE LOOP
    // DISPLAY NODES ON THE LEFT SIDE
    // FILTER SUCH THAT CLICKING ON A NODE CONSUMES EVENT BEFORE THE PANE
    // REACHES IT.
}
