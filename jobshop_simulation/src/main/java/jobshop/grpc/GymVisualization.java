package jobshop.grpc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GymVisualization extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource(
        "/jobshop/gui/EnvironmentGym.fxml"));
    primaryStage.setTitle("MATE");
    primaryStage.setScene(new Scene(root, 1200, 800));
    primaryStage.show();
  }
}
