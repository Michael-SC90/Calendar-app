package ScheduleSystem.View;

import ScheduleSystem.Model.Appointment;
import ScheduleSystem.Model.Customer;
import static ScheduleSystem.Model.ServerAuth.AUTH;
import static ScheduleSystem.View.MainScreenController.APPOINTMENT_LIST;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/** @author Michael **/
public class AppointmentScreenController implements Initializable, ScreenHandler {

  Boolean loaded = false;
  Appointment appointment;
  Customer customer;
  @FXML
  Label customerName;
  @FXML
  GridPane detailsGrid;
  @FXML
  MenuButton typeMenu, startHour, startMin, endHour, endMin;
  @FXML
  TextField titleField, contactField, dateField; //
  @FXML
  Button saveBtn, cancelBtn, change, details;
  @FXML
  ToggleGroup typeGroup, sHour, sMin, eHour, eMin;
  @FXML
  RadioMenuItem meet, consult;
  @FXML
  DatePicker datePicker;
  private double xOffset = 0;
  private double yOffset = 0;
  
/** Getters **/
  public Appointment getAppointment() {
    return this.appointment;
  }
  
  public Customer getCustomer() {
    return this.customer;
  }
  
  public Boolean getState() {
    return this.loaded;
  }
  
/** Setters **/
  public void setAppointment(Appointment appointment) {
    this.appointment = appointment;
  }
  
  public void setCustomer(Customer customer) {
    this.customer = customer;
  }
  
  public void setCustomerLabel(String name) {
    customerName.setText(name);
  }
  
  public void setState(Boolean state) {
    this.loaded = state;
  }
  
/** Observers ToggleGroups' selection **/
  private void observeTypeToggle() {
    typeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
      public void changed(ObservableValue<? extends Toggle> ov,
                          Toggle old_toggle, Toggle new_toggle) {
        if (typeGroup.getSelectedToggle() == meet) {
          typeMenu.setText("Meeting");
        }
        if (typeGroup.getSelectedToggle() == consult) {
          typeMenu.setText("Consultation");
        }
      }
    });
  }
  
/** Observers time-related ToggleGroups' selection **/
/** Seems terribly inefficient; will use for now **/
  private void observeTimeToggle(MenuButton button, ToggleGroup group) {
    group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
      public void changed(ObservableValue<? extends Toggle> ov,
                          Toggle old_toggle, Toggle new_toggle) {
          int i = 0;
          while (!group.getToggles().get(i).isSelected()) {
            i++;
          }
          button.setText(button.getItems().get(i).getId());
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
    observeTypeToggle();
    observeTimeToggle(startHour, sHour);
    observeTimeToggle(startMin, sMin);
    observeTimeToggle(endHour, eHour);
    observeTimeToggle(endMin, eMin);
    enableWindowMovement((AnchorPane) detailsGrid.getParent());
  }
  
  @FXML
  private void handleButtonAction(ActionEvent event) {
    try {
      Stage stage = (Stage) cancelBtn.getScene().getWindow();
      if (event.getSource() == saveBtn) {
        if (loaded) {
          updateAppointment();
        } else {uploadNewAppointment(createAppointment(-1));}
        stage.close();
      }
      if (event.getSource() == cancelBtn) {
        stage.close();
      }
      if (event.getSource() == change) {
        loadSelectScreen(this);
      }
      if (event.getSource() == details) {
        changeSubScreen(stage, "CustomerScreen.fxml", "AppointmentScreen.fxml");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
/** Returns UTC timestamp; for saving appointments **/
  private Timestamp getTimestamp(ZonedDateTime zoneDate) {
    LocalDateTime utc = zoneDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    Timestamp stamp = Timestamp.valueOf(utc);
    return stamp;
  }

/** Returns localized date and time **/
  private ZonedDateTime getZonedDateTime(String hour, String min, LocalDate date) {
    LocalDateTime local = date.atTime(Integer.valueOf(hour), Integer.valueOf(min));
    ZonedDateTime zoneDate = local.atZone(ZoneId.systemDefault());
    return zoneDate;
  }

/** Loads Object reference from TableView; populates fields with data **/
  public void loadAppointment(Appointment appointment) {
    this.appointment = appointment;
    this.customer = appointment;
    customerName.setText(appointment.getCustomerName().getValue());
    titleField.setText(appointment.getTitle().getValue());
    contactField.setText(appointment.getContact());
    if (!appointment.getType().getValue().matches("Meeting")) {
      consult.setSelected(true);
      typeMenu.setText("Consultation");
    }
    datePicker.setValue(appointment.getStart().toLocalDate());
    setTimeComponent(sHour, appointment.getStart().getHour());
    setTimeComponent(sMin, appointment.getStart().getMinute() % 14);
    setTimeComponent(eHour, appointment.getEnd().getHour());
    setTimeComponent(eMin, appointment.getEnd().getMinute() % 14);
    this.setState(true);
  }
  
/** Updates Database, locally deletes with this reference, then adds new Object to LIST **/
  private void updateAppointment() {
    String prompt = "UPDATE "
                      + "appointment "
                        + "SET "
                          + "customerId = ?, "
                          + "title = ?, "
                          + "contact = ?, "
                          + "type = ?, "
                          + "start = ?, "
                          + "end = ?, "
                          + "lastUpdate = NOW(), "
                          + "lastUpdateBy = ? "
                            + "WHERE appointmentId = ?";
    PreparedStatement pstmt = AUTH.getPreparedStatement(prompt);
    Appointment updated = createAppointment(appointment.getAptId());
    try {
      pstmt.setInt(1, appointment.getCustomerId());
      pstmt.setString(2, titleField.getText());
      pstmt.setString(3, contactField.getText());
      pstmt.setString(4, typeMenu.getText());
      pstmt.setTimestamp(5, getTimestamp(getZonedDateTime(startHour.getText(), startMin.getText(), datePicker.getValue())));
      pstmt.setTimestamp(6, getTimestamp(getZonedDateTime(endHour.getText(), endMin.getText(), datePicker.getValue())));
      pstmt.setString(7, AUTH.getCurrentUser().getName());
      pstmt.setInt(8, appointment.getAptId());
      pstmt.executeUpdate();
      APPOINTMENT_LIST.remove(appointment);
      APPOINTMENT_LIST.add(updated);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
 /** Adds new Object to Database;
  * SQL generates appointmentId with AUTO_INCREMENT and returns value before adding appointment to LIST **/
  private void uploadNewAppointment(Appointment appointment) {
    String prompt = "INSERT "
                      + "INTO appointment"
                        + "(userId, customerId, title, contact, type, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                          + "values "
                            + "(?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?);";
    try {
      PreparedStatement pstmt = AUTH.createConnection().prepareStatement(prompt, Statement.RETURN_GENERATED_KEYS);
      pstmt.setInt(1, appointment.getUserId());
      pstmt.setInt(2, appointment.getCustomerId());
      pstmt.setString(3, appointment.getTitle().getValue());
      pstmt.setString(4, appointment.getContact());
      pstmt.setString(5, appointment.getType().getValue());
      pstmt.setTimestamp(6, getTimestamp(appointment.getStart()));
      pstmt.setTimestamp(7, getTimestamp(appointment.getEnd()));
      pstmt.setString(8, AUTH.getCurrentUser().getName());
      pstmt.setString(9, AUTH.getCurrentUser().getName());
      pstmt.executeUpdate();
      ResultSet newId = pstmt.getGeneratedKeys();
      newId.next();
      int id = newId.getInt(1);
      appointment.setAptId(id);
      APPOINTMENT_LIST.add(appointment);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
/** Gets values from fields and creates new Appointment Object;
 * accepts ID parameter for existing appointments, else is -1 **/
  Appointment createAppointment(int id) {
    Appointment newAppointment = new Appointment(id, this.customer, AUTH.getCurrentUser().getUserId(),
      getTimestamp(getZonedDateTime(startHour.getText(), startMin.getText(), datePicker.getValue())),
      getTimestamp(getZonedDateTime(endHour.getText(), endMin.getText(), datePicker.getValue())),
      titleField.getText(), contactField.getText(), typeMenu.getText());
    return newAppointment;
  }
  
/** Selects toggle from group based on index **/
  private void setTimeComponent(ToggleGroup group, int timeComponent) {
    group.selectToggle(group.getToggles().get(timeComponent));
  }
}