package ScheduleSystem.View;

import ScheduleSystem.Model.Appointment;
import ScheduleSystem.Model.Customer;
import static ScheduleSystem.View.MainScreenController.CUSTOMER_LIST;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**@author Michael **/
public class SelectScreenController implements Initializable, ScreenHandler {

  Boolean cancelled = false;
  Appointment appointment;
  static Customer customer = (Customer) CUSTOMER_LIST.get(0); //Sets default customer for loading TableView
  @FXML
  Button addCustomer, cancelBtn;
  @FXML
  TableView custTable;
  @FXML
  TableColumn<Customer, String> name;
  
/** Getters **/
  //Provides reference to this customer
  public Customer getCustomer() {
    return this.customer;
  }
  
  //Provides reference to this appointment; doesn't seem to be needed
  public Appointment getAppointment() { 
    return this.appointment;
  }
  
  //Allows this state to be passed to Screen Handler
  public Boolean getState() {
    return this.cancelled;
  }
  
/** Setters **/
  //Mutates this customer and all references to it
  public void setCustomer(Customer customer) {
    this.customer = customer;
  }
  
  //Mutates this appointment and all references to it; doesn't seem to be needed
  public void setAppointment(Appointment appointment) {
    this.appointment = appointment;
  }
  
  //Mutates state to allow cancellation to be passed to Screen Handler
  private void setState(Boolean state) {
    this.cancelled = state;
  }
  
/** Populates table with list name data **/
  private void setFactories() {
    name.setCellValueFactory(cellData -> {
      return cellData.getValue().getCustomerName();
    });
  }
  
/** Listens for double-clicks on Table Items **/
  @FXML
  private void selectionListener() {
    custTable.setRowFactory(selection -> {
      TableRow<Customer> row = new TableRow<>();
      row.setOnMouseClicked((MouseEvent event) -> {
        if ((event.getClickCount() == 2) && (!row.isEmpty())) {
          Stage stage = (Stage) custTable.getScene().getWindow();
          setCustomer(row.getItem()); //Sets reference to customer; allows pass to Screen Handler
          stage.close();
        }
      });
      return row;
    });
  }
  
/** On start **/
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    setFactories();
    selectionListener();
    custTable.setItems(CUSTOMER_LIST);
  }

/** User Actions **/
  @FXML
  private void handleButtonAction(ActionEvent event) {
    try {
      Stage stage = (Stage) cancelBtn.getScene().getWindow();
      if (event.getSource() == addCustomer) {
        changeSubScreen(stage, "SelectScreen.fxml", "CustomerScreen.fxml");
      }
      if (event.getSource() == cancelBtn) {
          this.setState(true);
          stage.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}