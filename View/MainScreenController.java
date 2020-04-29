package ScheduleSystem.View;

import ScheduleSystem.Model.Address;
import ScheduleSystem.Model.Appointment;
import ScheduleSystem.Model.City;
import ScheduleSystem.Model.Country;
import ScheduleSystem.Model.Customer;
import static ScheduleSystem.Model.ServerAuth.AUTH;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/** @author Michael **/
public class MainScreenController implements Initializable, ScreenHandler {

/** Static for now; will pass by reference using Screen Handler (see NEW APPOINTMENT PROCESS) **/
  static ObservableList APPOINTMENT_LIST = FXCollections.observableArrayList();
  static ObservableList CUSTOMER_LIST = FXCollections.observableArrayList();
  static ObservableList ADDRESS_LIST = FXCollections.observableArrayList();
  static ObservableList CITY_LIST = FXCollections.observableArrayList();
  static ObservableList COUNTRY_LIST = FXCollections.observableArrayList();
  
  private int calYear;
  private String calMonth;
  @FXML
  private Label welcomeLabel, tableTitle;
  @FXML
  private MenuBar bar;
  @FXML
  private MenuItem changeUser, closeBtn, deleteBtn;
  @FXML
  private Button prevBtn, nextBtn, addAptBtn, custBtn, reportBtn;
  @FXML
  TableView<Appointment> calTable;
  @FXML
  private TableColumn<Appointment, String> title, type, customer, startTime, endTime;
  @FXML
  private MenuButton filterMenu;
  @FXML
  private ToggleGroup filterGroup, calOptions;
  @FXML
  private RadioButton weekToggle, monthToggle;
  @FXML
  private RadioMenuItem none, meet, consult;
  private double xOffset = 0;
  private double yOffset = 0;

/*****************UI CONTROLS******************/
/** Updates Calendar Label with Month and Year parameters **/
  private void updateCalLabel(Month month, int year) {
    String monthString = month.getDisplayName(TextStyle.FULL, Locale.getDefault());
    this.calMonth = monthString;
    this.calYear = year;
    tableTitle.setText(monthString + " " + year);
  }
  
/** Enables double-clicking Table items; opens AppointmentScreen window **/
  @FXML
  private void selectionListener() {
    calTable.setRowFactory(selection -> {
      TableRow<Appointment> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if ((event.getClickCount() == 2) && (!row.isEmpty())) {
          loadAppointmentScreen(row.getItem());
        }
      });
      return row;
    });
  }
  
/** Enables window movement by dragging MenuBar **/
  private void enableWindowMovement() {
    bar.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
      }
    });
    bar.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        bar.getScene().getWindow().setX(event.getScreenX() - xOffset);
        bar.getScene().getWindow().setY(event.getScreenY() - yOffset);
      }
    });
  }
  
/** Forces columns to resize based on width of parent window **/
  private void updateCalColumn() {
      title.prefWidthProperty().bind(calTable.widthProperty().multiply(.23));
      type.prefWidthProperty().bind(calTable.widthProperty().multiply(.15));
      customer.prefWidthProperty().bind(calTable.widthProperty().multiply(.19));
      startTime.prefWidthProperty().bind(calTable.widthProperty().multiply(.20));
      endTime.prefWidthProperty().bind(calTable.widthProperty().multiply(.20));
      startTime.setSortType(TableColumn.SortType.ASCENDING);
      calTable.getSortOrder().add(startTime);
  }
  
/** Set TableColumns' cell factories **/
  private void setFactories() {
    title.setCellValueFactory(cellData -> {
      return cellData.getValue().getTitle();
    });
    type.setCellValueFactory(cellData -> {
      return cellData.getValue().getType();
    });
    customer.setCellValueFactory(cellData -> {
      return cellData.getValue().getCustomerName();
    });
    startTime.setCellValueFactory(cellData -> {
      return cellData.getValue().getStartTime();
    });
    endTime.setCellValueFactory(cellData -> {
      return cellData.getValue().getEndTime();
    });
  }
  
/** Pulls and parses data from SQL ResultSet **/
  private void loadData() {
    String prompt = "SELECT " /** Extracts all information from database with and without associations **/
                    + "userId, appointmentId, title, contact, start, end, type, a.customerId, "
                    + "c.customerId, customerName, c.addressId, "
                    + "d.addressId, address, phone, d.cityId, "
                    + "t.cityId, city, t.countryId, "
                    + "n.countryId, country "
                      + "FROM "
                        + "appointment a "
                          + "RIGHT JOIN customer c "
                            + "ON a.customerId = c.customerId "
                              + "RIGHT JOIN address d "
                                + "ON c.addressId = d.addressId "
                                  + "RIGHT JOIN city t "
                                    + "ON d.cityId = t.cityId "
                                      + "RIGHT JOIN country n "
                                        + "ON t.countryId = n.countryId "
                                          + "ORDER BY userId, start, city;";
    try {
      ResultSet rs = AUTH.getPreparedStatement(prompt).executeQuery();
      rs.beforeFirst();
      /** Ends data extraction when list is empty **/
      while (rs.next()) {
        /** Records will always contain Country data **/
        Country country = new Country(rs.getInt("countryId"), rs.getString("country"));
        int cityId = rs.getInt("cityId");
        if (!rs.wasNull()) {
          /** Records without City data will not have Address, Customer, or Appointment data  **/
          City city = new City(cityId, rs.getString("city"), country);
          int addressId = rs.getInt("addressId");
          if (!rs.wasNull()) {
            /** Records without Address data will not have Customer, or Appointment data  **/
            Address address = new Address(addressId, rs.getString("address"), rs.getString("phone"), city);
            int customerId = rs.getInt("customerId");
            if (!rs.wasNull()) {
              /** Records without Customer data will not have Appointment data  **/
              Customer customer = new Customer(customerId, rs.getString("customerName"), address);
              int appointmentId = rs.getInt("appointmentId");
              if (!rs.wasNull()) {
                /** All Appointment records are unique and added to LIST **/
                Appointment appointment = 
                  new Appointment(appointmentId, customer, rs.getInt("userId"),
                    rs.getTimestamp("start"), rs.getTimestamp("end"), rs.getString("title"),
                    rs.getString("contact"), rs.getString("type"));
                APPOINTMENT_LIST.add(appointment);
              }
              /** Iteration over LIST; adds missing Objects **/
              Boolean found = false;
              for (Iterator<Customer> checkCust = CUSTOMER_LIST.listIterator(); checkCust.hasNext() && (found == false);) {
                Customer exists = checkCust.next();
                if (customer.getCustomerId() == exists.getCustomerId())
                  found = true;
              }
              if (!found) CUSTOMER_LIST.add(customer);
            }
            /** Iteration over LIST; adds missing Objects **/
            Boolean found = false;
            for (Iterator<Address> checkAddr = ADDRESS_LIST.listIterator(); checkAddr.hasNext() && (found == false);) {
              Address exists = checkAddr.next();
              if (address.getAddressId() == exists.getAddressId())
                found = true;
            }
            if (!found) ADDRESS_LIST.add(address);
          }
          /** Iteration over LIST; adds missing Objects **/
          Boolean found = false;
          for (Iterator<City> checkCity = CITY_LIST.listIterator(); checkCity.hasNext() && (found == false);) {
            City exists = checkCity.next();
            if (city.getCityId() == exists.getCityId())
              found = true;
          }
          if (!found) CITY_LIST.add(city);
        }
        /** Iteration over LIST; adds missing Objects **/
        Boolean found = false;
        for (Iterator<Country> checkCountry = COUNTRY_LIST.listIterator(); checkCountry.hasNext() && (found == false);) {
          Country exists = checkCountry.next();
          if (country.getCountryId() == exists.getCountryId())
            found = true;
        }
        if (!found) COUNTRY_LIST.add(country);
      }
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
/** Adds action listener to MenuButton; filters ObservableList within Table  **/
  private void filterCalendarByType(ObservableList list) {
    FilteredList<Appointment> filteredAppts = new FilteredList<>(list, p -> true);
    filterGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
      public void changed(ObservableValue<? extends Toggle> ov,
                          Toggle old_toggle, Toggle new_toggle) {
        filteredAppts.setPredicate(calEntry -> {
          if (calEntry.getUserId() == AUTH.getCurrentUser().getUserId()) {
            if (filterGroup.getSelectedToggle() == none) {
              filterMenu.setText("None");
              return true;
            }
            if (filterGroup.getSelectedToggle() == meet) {
              filterMenu.setText("Meetings");
              if (calEntry.getType().getValue().contains("Meeting")) {
                return true;
              }
            }
            if (filterGroup.getSelectedToggle() == consult) {
              filterMenu.setText("Consults");
              if (calEntry.getType().getValue().matches("Consultation")) {
                return true;
              }
            }
          }
          return false;
        });
      }
    });
    SortedList<Appointment> sortedAppts = new SortedList<>(filteredAppts);
    sortedAppts.comparatorProperty().bind(calTable.comparatorProperty());
    calTable.setItems(sortedAppts);
  }
  
  private ObservableList filterCalendarByPeriod() {
    FilteredList<Appointment> filteredAppts = new FilteredList<>(APPOINTMENT_LIST, p-> true);
    calOptions.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
      public void changed(ObservableValue<? extends Toggle> ov,
                          Toggle old_toggle, Toggle new_toggle) {
        filteredAppts.setPredicate(calEntry -> {
          if (calEntry.getStart().getYear() == calYear) {
            if (calOptions.getSelectedToggle() == monthToggle) {
              if (calEntry.getStart().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()).matches(calMonth)) {
                return true;
              }
            }
            /** if (calOptions.getSelectedToggle() == weekToggle) {
              if (calEntry.getStart().getDayOfWeek())
            } **/
          }
          return false;
        });
      }
    });
    SortedList<Appointment> sortedAppts = new SortedList<>(filteredAppts);
    return sortedAppts;
  }
  
/*****************EXECUTE WHEN LOADED******************/
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    loadData();
    updateCalColumn();
    welcomeLabel.setText("Welcome " + AUTH.getCurrentUser().getName());
    setFactories();
    selectionListener();
    enableWindowMovement();
    updateCalLabel(LocalDate.now().getMonth(), LocalDate.now().getYear());
    filterCalendarByType(filterCalendarByPeriod());
    updateCalView();
  }
  
/*****************USER CONTROLS******************/
  @FXML
  private void handleButtonAction(ActionEvent event) {
      if (event.getSource() == changeUser) {
        changeUser();        
      }
      if (event.getSource() == closeBtn) {
        Platform.exit();
      }
      if (event.getSource() == prevBtn) {
        if (calMonth.matches("January")) {
          updateCalLabel(Month.valueOf(calMonth.toUpperCase()).minus(1), --calYear);
        } else updateCalLabel(Month.valueOf(calMonth.toUpperCase()).minus(1), calYear);
        updateCalView();
      }
      if (event.getSource() == nextBtn) {
        if (calMonth.matches("December")) {
          updateCalLabel(Month.valueOf(calMonth.toUpperCase()).plus(1), ++calYear);
        } else updateCalLabel(Month.valueOf(calMonth.toUpperCase()).plus(1), calYear);
        updateCalView();
      }
      if (event.getSource() == deleteBtn) {
/** Attempts to delete appointment from Database; if succeeds, deletes from LIST **/
        if (dbDeleteApt(calTable.getSelectionModel().getSelectedItem().getAptId())) {
          APPOINTMENT_LIST.remove(calTable.getSelectionModel().getSelectedItem());
        }
      }
/** Loads Appointment Screen with Select Screen on top **/
      if (event.getSource() == addAptBtn) {
        loadAppointmentScreen();
      }
      if (event.getSource() == custBtn) {
        loadSubScreen("AddressBook.fxml");
      }
      if (event.getSource() == reportBtn) {
        loadSubScreen("ReportScreen.fxml");
      }
  }
  
/** Deletes appointments from database with UPDATE **/
  private Boolean dbDeleteApt(int id) {
    try {
      String prompt = "DELETE "
                      + "FROM appointment "
                        + "WHERE (appointmentId = '" + id + "');";
      AUTH.getPreparedStatement(prompt).executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }
  
/** Forces update of TableView; can replace with .refresh() **/
  private void updateCalView() {
    calOptions.selectToggle(weekToggle);
    calOptions.selectToggle(monthToggle);
    filterGroup.getToggles().get(1).setSelected(true);
    filterGroup.getToggles().get(0).setSelected(true);
  }

/*****************LOGOUT HANDLER******************/
/** Logs out of current user; returns to LoginScreen **/
  private void changeUser() {
    Stage stage = (Stage) calTable.getScene().getWindow();
    stage.close();
    resetApp();    
    loadLogin("LoginScreen.fxml");
  }
  
/** Clears lists upon log-out; needed while lists are static **/
  private void resetApp() {
    APPOINTMENT_LIST.clear();
    CUSTOMER_LIST.clear();
    ADDRESS_LIST.clear();
    CITY_LIST.clear();
    COUNTRY_LIST.clear();
  }
}