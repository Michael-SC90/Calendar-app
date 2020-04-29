package ScheduleSystem.View;

import ScheduleSystem.Model.Appointment;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** @author Michael **/
public interface ScreenHandler {
  
/** Loads Login Screen; called by MainApp and MainScreen **/
  default void loadLogin(String docLocation) {
    Stage stage = new Stage();
    try {
      Parent root = FXMLLoader.load(getClass().getResource(docLocation));
      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(scene);
      stage.setMinWidth(300);
      stage.setMinHeight(350);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
/** Loads Main Screen; called by LoginScreen **/
  default void loadMainScreen(Stage stage) {
    Parent root;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
    try {
      root = loader.load();
      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      stage.setScene(scene);
      stage.setMinWidth(850);
      stage.setMinHeight(350);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
 
/** Loads Table Screen (used for viewing and editing appointments); called by MainScreen **/
  default void loadAppointmentScreen(Appointment appointment) {
    Parent root;
    Stage stage = new Stage();
    stage.setAlwaysOnTop(true);
    FXMLLoader loader = new FXMLLoader(getClass().getResource("AppointmentScreen.fxml"));
    try {
      root = loader.load();
      AppointmentScreenController controller = loader.getController();
      controller.loadAppointment(appointment);
      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(scene);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
/** For new Appointments **/
  default void loadAppointmentScreen() {
    try {
/** Open Appointment Screen first (Owner) **/
      Parent root;
      Stage stage = new Stage();
      stage.setAlwaysOnTop(true);
      FXMLLoader loader = new FXMLLoader(getClass().getResource("AppointmentScreen.fxml"));
      root = loader.load();
      AppointmentScreenController controller = loader.getController();
      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(scene);
      stage.initModality(Modality.WINDOW_MODAL);
/** Open Selection Screen second (child); might want to try using separate method to return subStage **/
      Parent subRoot;
      Stage subStage = new Stage(); //Get subStage from method
      subStage.setAlwaysOnTop(true);
      FXMLLoader subLoader = new FXMLLoader(getClass().getResource("SelectScreen.fxml")); //get subLoader from method
      subRoot = subLoader.load();
      Scene subScene = new Scene(subRoot);
      subScene.setFill(Color.TRANSPARENT);
      subStage.initStyle(StageStyle.TRANSPARENT);
      subStage.setScene(subScene);
      subStage.initModality(Modality.WINDOW_MODAL);
      subStage.initOwner(stage);
/** Set reference between parent and child to get Customer info **/
      SelectScreenController subController = subLoader.getController();
      subStage.showAndWait();
      if (subController.getState()) {
        stage.close();
      } else {
        controller.setCustomer(subController.getCustomer());
        controller.setCustomerLabel(subController.getCustomer().getCustomerName().getValue());
        stage.show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
/** For changing customer selection from Appointment Screen **/
  default void loadSelectScreen(AppointmentScreenController controller) {
    Parent root;
    Stage subStage = new Stage();
    subStage.setAlwaysOnTop(true);
    FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectScreen.fxml"));
    try {
      root = loader.load();
      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      subStage.initStyle(StageStyle.TRANSPARENT);
      subStage.initModality(Modality.APPLICATION_MODAL);
      subStage.setScene(scene);
      SelectScreenController subController = loader.getController();
      subStage.showAndWait();
      if (!subController.getState()) {
        controller.setCustomer(subController.getCustomer());
        controller.setCustomerLabel(subController.getCustomer().getCustomerName().getValue());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
/** Loads new window; for Appt, Customer, Address, and Report screens; called recursively and by MainScreen **/
  default void loadSubScreen(String docLocation) {
    Parent root;
    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    FXMLLoader loader = new FXMLLoader(getClass().getResource(docLocation));
    try {
      root = loader.load();
      if (docLocation.matches("AddressBook.fxml")) {
        AddressBookController controller = loader.getController();
        controller.setCustomerState();
        controller.changeListener();
        controller.initializeList();
      }
      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(scene);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
/******* MAYBE TRASH BELOW ******/  
/** TEST: Modulize Screen Loading process **/
  default FXMLLoader getLoader(String docLoc) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(docLoc));
      return loader;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  default AppointmentScreenController getAppointmentController(FXMLLoader loader) {
    AppointmentScreenController controller = loader.getController();
    return controller;
  }
  
/** Swap root node of stage **/
  default void changeSubScreen(Stage stage, String newDoc, String prevDoc) {
    Parent root;
    FXMLLoader loader = new FXMLLoader(getClass().getResource(newDoc));
    try {
      root = loader.load();
      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      stage.setScene(scene);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}