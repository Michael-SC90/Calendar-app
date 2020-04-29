package ScheduleSystem.View;

import static ScheduleSystem.Model.ServerAuth.AUTH;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/** @author Michael **/
public class ReportScreenController implements Initializable {

  @FXML
  Button monthlyAppts, allSchedules, allCustomers, closeBtn;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // TODO
  }
  
  @FXML
  private void handleButtonAction(ActionEvent event) {
    try {
      if (event.getSource() == monthlyAppts) {
        getMonthlyAppts();
      }
      if (event.getSource() == allSchedules) {
        getAllUserSchedules();
      }
      if (event.getSource() == allCustomers) {
        getAllCustomers();
      }
      if (event.getSource() == closeBtn) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
/** Number of appointment types by month **/
  private void getMonthlyAppts() {
    String prompt = new String();
    prompt = "";
    try {
      AUTH.getPreparedStatement(prompt).executeQuery();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
/** Schedule for each User **/
  private void getAllUserSchedules() {
    StringBuilder report = new StringBuilder();
    Alert scheduleReport = new Alert(Alert.AlertType.INFORMATION);
    scheduleReport.setTitle("Listing Users with Schedules");
    String prompt2 = "SELECT ("
                + "SELECT userName "
                  + "FROM user), "
                + "(SELECT COUNT(appointmentId) as appt_Count "
                  + "FROM appointment) "
                    + "FROM user, appointment "
                      + "WHERE (user.userId = appointment.userId);";
    String prompt = "SELECT ("
                      + "SELECT userName "
                        + "FROM user"
                      + ") as User, "
                      + "(SELECT * "
                        + "FROM appointment;";
    try {
      ResultSet results = AUTH.getPreparedStatement(prompt).executeQuery();
      while (results.next()) {
        report.append(results.getString("user.userName")).append(": ").append(results.getString("appointment.appointmentId")).append("\n");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    Stage stage = (Stage) allCustomers.getScene().getWindow();
    stage.hide();
    scheduleReport.setContentText(report.toString());
    Optional<ButtonType> close = scheduleReport.showAndWait();
    if (close.get() == ButtonType.OK) {
      stage.show();
    } else {stage.close();}
  }
  
/** All customers in database **/
  private void getAllCustomers() {
    StringBuilder report = new StringBuilder();
    Alert allCustomersReport = new Alert(Alert.AlertType.INFORMATION);
    allCustomersReport.setTitle("Listing All Customers");
    String prompt = "SELECT "
                    + "customerName "
                        + "FROM customer";
    try {
      ResultSet results = AUTH.getPreparedStatement(prompt).executeQuery();
      while (results.next()) {
        report.append(results.getString("customerName")).append("\n");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    Stage stage = (Stage) allCustomers.getScene().getWindow();
    stage.hide();
    allCustomersReport.setContentText(report.toString());
    Optional<ButtonType> close = allCustomersReport.showAndWait();
    if (close.get() == ButtonType.OK) {
      stage.show();
    } else {stage.close();}
  }
}