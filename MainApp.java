package ScheduleSystem;

import ScheduleSystem.View.ScreenHandler;
import javafx.application.Application;
import javafx.stage.Stage;

/**@author Michael **/
public class MainApp extends Application implements ScreenHandler {
  
  @Override
  public void start(Stage stage) throws Exception {
    loadLogin("View/LoginScreen.fxml");
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
  
}
