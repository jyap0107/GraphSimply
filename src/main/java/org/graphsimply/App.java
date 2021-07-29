package org.graphsimply;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        loader.setLocation(getClass().getResource("GraphSimplyFXML.fxml"));
        root = loader.load();
        GraphSimplyController view = loader.getController();
        view.init(new GraphSimplyViewModel());
        stage.setTitle("GraphSimply");
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }
}