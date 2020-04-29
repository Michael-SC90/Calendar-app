package ScheduleSystem.View;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** @author Michael **/
public interface ExceptionControls {
  
/** Notification of invalid parameters **/
  default void errorAlert(String title, String text) {
    Alert selectAlert = new Alert(Alert.AlertType.ERROR);
    selectAlert.setTitle(title);
    selectAlert.setContentText(text);
    selectAlert.showAndWait();
  }

/** Request for confirmation **/
  default Optional<ButtonType> confirmAlert(String title, String text) {
    Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION);
    cancelAlert.setTitle(title);
    cancelAlert.setContentText(text);
    Optional<ButtonType> cancelConfirm = cancelAlert.showAndWait();
    return cancelConfirm;
  }
}