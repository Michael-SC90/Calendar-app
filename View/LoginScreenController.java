package ScheduleSystem.View;

import static ScheduleSystem.Model.ServerAuth.AUTH;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**@author Michael **/
public class LoginScreenController implements Initializable, ScreenHandler, ExceptionControls {
  
  @FXML
  private Button loginButton, exitButton;
  @FXML
  private MenuButton langMenu;
  @FXML
  ToggleGroup langGroup;
  @FXML
  private RadioMenuItem en, de, jp;
  @FXML
  private Label welcome, prompt, credit;
  @FXML
  private TextField nameField, pwField;
  private String nullTitle, nullText, invalidTitle, invalidText;
  private double xOffset = 0;
  private double yOffset = 0;
  
/** Called by Action Listener; changes text based on menu selection **/
  @FXML
  private void setLanguage(Locale locale) {
    ResourceBundle textLanguage = ResourceBundle.getBundle("ScheduleSystem/Language/Bundle", locale);
    langMenu.setText(textLanguage.getString("langLocal"));
    welcome.setText(textLanguage.getString("welcome"));
    prompt.setText(textLanguage.getString("prompt"));
    loginButton.setText(textLanguage.getString("login"));
    exitButton.setText(textLanguage.getString("exit"));
    credit.setText(textLanguage.getString("credit"));
    nullTitle = textLanguage.getString("nullTitle");
    nullText = textLanguage.getString("nullText");
    invalidTitle = textLanguage.getString("invalidTitle");
    invalidText = textLanguage.getString("invalidText");
  }
  
/** Adds Action Listener to menu items **/
  private void menuListener() {
    langGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
      public void changed(ObservableValue<? extends Toggle> ov,
                          Toggle old_toggle, Toggle new_toggle) {
        if (langGroup.getSelectedToggle() != null) {
          if (langGroup.getSelectedToggle() == en) {
            setLanguage(Locale.US);
          }
          if (langGroup.getSelectedToggle() == de) {
            setLanguage(Locale.GERMANY);
          }
          if (langGroup.getSelectedToggle() == jp) {
            setLanguage(Locale.JAPAN);
          }
        }
      }
    });
  }
  
/** Enables window movement by dragging MenuBar **/
  private void enableWindowMovement(AnchorPane pane) {
    pane.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
      }
    });
    pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        pane.getScene().getWindow().setX(event.getScreenX() - xOffset);
        pane.getScene().getWindow().setY(event.getScreenY() - yOffset);
      }
    });
  }
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    setLanguage(Locale.getDefault());
    menuListener();
    enableWindowMovement((AnchorPane) credit.getParent());
  }

/** Handles function calls dependent on button clicked **/
  @FXML
  private void handleButtonAction(ActionEvent event) throws IOException {
    try {
      if ((event.getSource() == loginButton) && (verifyFields())) {
        loadMainScreen((Stage) loginButton.getScene().getWindow());
      } else {
        if (event.getSource() == exitButton) {
          Platform.exit();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

/** Parses and passes text from fields to ServerAuth singleton for data verification **/
  private Boolean verifyFields() {
    String username = nameField.getText();
    String password = pwField.getText();
    switch (AUTH.verifyCredentials(username, password)) {
      case 0: errorAlert(nullTitle, nullText);
              break;
      case 1: errorAlert(invalidTitle, invalidText);
              break;
      case 2: return true;
    }
  return false;
  }
}