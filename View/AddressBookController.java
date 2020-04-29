package ScheduleSystem.View;

import ScheduleSystem.Model.Address;
import ScheduleSystem.Model.City;
import ScheduleSystem.Model.Country;
import ScheduleSystem.Model.Customer;
import static ScheduleSystem.View.MainScreenController.ADDRESS_LIST;
import static ScheduleSystem.View.MainScreenController.CITY_LIST;
import static ScheduleSystem.View.MainScreenController.COUNTRY_LIST;
import static ScheduleSystem.View.MainScreenController.CUSTOMER_LIST;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** @author Michael **/
public class AddressBookController implements Initializable {

  @FXML
  ToggleGroup tableFilter;
  @FXML
  RadioButton allToggle, addressToggle, cityToggle, countryToggle;
  ObservableList<RowData> rowList = FXCollections.observableArrayList();
  @FXML
  AnchorPane anchor;
  @FXML
  Button addButton, delButton, applyButton, saveButton, closeButton;
  @FXML
  TableView bookTable;
  @FXML
  TableColumn<RowData, String> customerCol, addressCol, cityCol, countryCol;
  @FXML
  Label country_label, city_label, address_label, phone_label, customer_label;
  @FXML
  TextField country_field, city_field, address_field, phone_field, customer_field;
  private double xOffset = 0;
  private double yOffset = 0;
  
/** Must call before loading Stage, else Table first appears blank **/
  @FXML
  void initializeList() {
    for (Iterator<Customer> iter = CUSTOMER_LIST.listIterator(); iter.hasNext();) {
      RowData newRow = new RowData(iter.next());
      rowList.add(newRow);
    }
    bookTable.setItems(rowList);
  }
  
  /*****************EXECUTE WHEN LOADED******************/
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    anchor  = (AnchorPane) bookTable.getParent();
    enableWindowMovement();
    setFactories();
    enableSelectionListener();
  }

/** Is passed RowData object and parses TextField information from object's own reference; RowData still contains Country **/
  private void setCountryField(RowData data) {
    country_field.setText(data.getCountry().getCountryName());
  }
  
/** Is passed RowData object and parses TextField info from own reference; city reference is lost here, but RowData still contains City **/
  private void setCityFields(RowData data) {
    country_field.setText(data.getCountry().getCountryName());
    city_field.setText(data.getCity().getCityName());
  }
  
/** Same as setCityFields, but with additional Address data **/
  private void setAddressFields(RowData data) {
    country_field.setText(data.getCountry().getCountryName());
    city_field.setText(data.getCity().getCityName());
    address_field.setText(data.getAddress().getAddress());
    phone_field.setText(data.getAddress().getPhone());
  }
  
/** Parses Country, City, Address, and Customer data from RowData Object (references to MAIN_LISTS) **/
  private void setCustomerField(RowData data) {
    country_field.setText(data.getCountry().getCountryName());
    city_field.setText(data.getCity().getCityName());
    address_field.setText(data.getAddress().getAddress());
    phone_field.setText(data.getAddress().getPhone());
    customer_field.setText(data.getCustomer().getCustomerName().getValue());
  }

/** Columns read from RowData object to populate values in Rows; data values are consistent **/
  private void setFactories() {
    customerCol.setCellValueFactory(cellData -> {
      return cellData.getValue().customerName;
    });
    addressCol.setCellValueFactory(cellData -> {
      return cellData.getValue().addrString;
    });
    cityCol.setCellValueFactory(cellData -> {
      return cellData.getValue().cityName;
    });
    countryCol.setCellValueFactory(cellData -> {
      return cellData.getValue().countryName;
    });
  }
  
/** Enables double-clicking Table items; opens AppointmentScreen window **/
  @FXML
  private void enableSelectionListener() {
    bookTable.setRowFactory(selection -> {
      TableRow row = new TableRow<>();
      row.setOnMouseClicked(event -> {
          if ((event.getClickCount() == 1) && (!row.isEmpty())) {
            if (tableFilter.getSelectedToggle() == countryToggle) {
              setCountryField((RowData) row.getItem());
            }
            if (tableFilter.getSelectedToggle() == cityToggle) {
              setCityFields((RowData) row.getItem());
            }
            if (tableFilter.getSelectedToggle() == addressToggle) {
              setAddressFields((RowData) row.getItem());
            }
            if (tableFilter.getSelectedToggle() == allToggle) {
              setCustomerField((RowData) row.getItem());
            }
          }
      });
      return row;
    });
  }
  
/** Listener for toggles that produces new list for selected toggle **/
  @FXML
  void changeListener() {
    tableFilter.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
      public void changed(ObservableValue<? extends Toggle> ov,
                          Toggle old_toggle, Toggle new_toggle) {
        rowList.clear();
        if (tableFilter.getSelectedToggle() == countryToggle) {
          for (Iterator<Country> iter = COUNTRY_LIST.listIterator(); iter.hasNext();) {
            RowData newRow = new RowData(iter.next());
            rowList.add(newRow);
          }
          setCountryState();
        }
        if (tableFilter.getSelectedToggle() == cityToggle) {
          for (Iterator<City> iter = CITY_LIST.listIterator(); iter.hasNext();) {
            RowData newRow = new RowData(iter.next());
            rowList.add(newRow);
          }
          setCityState();
        }
        if (tableFilter.getSelectedToggle() == addressToggle) {
          for (Iterator<Address> iter = ADDRESS_LIST.listIterator(); iter.hasNext();) {
            RowData newRow = new RowData(iter.next());
            rowList.add(newRow);
          }
          setAddressState();
        }
        if (tableFilter.getSelectedToggle() == allToggle) {
          for (Iterator<Customer> iter = CUSTOMER_LIST.listIterator(); iter.hasNext();) {
            RowData newRow = new RowData(iter.next());
            rowList.add(newRow);
          }
          setCustomerState();
        }
      }
        });
    bookTable.setItems(rowList);
  }
  
/** Adjusts column size in table for when changing between toggles **/
  private void updateColumnSize(TableColumn col, double factor) {
    col.prefWidthProperty().bind(bookTable.widthProperty().multiply(factor));
  }
  
  @FXML
  private void handleButtonAction(ActionEvent event) {
  /** Renames labels, subtext, and columns also resizes 1 & 2 (address, phone); hides columns 3 & 4;  **/
  /** Saves any current changes **/
    if (event.getSource() == applyButton) {
      if (tableFilter.getSelectedToggle() == countryToggle) {
        String newName = country_field.getText();
        RowData row = (RowData) bookTable.getSelectionModel().getSelectedItem();
        Country toUpdate = row.getCountry();
        toUpdate.setCountryName(newName);
      }
      if (tableFilter.getSelectedToggle() == cityToggle) {
        
      }
      if (tableFilter.getSelectedToggle() == addressToggle) {
        
      }
      if (tableFilter.getSelectedToggle() == allToggle) {
        
      }
    }
  /** Saves any current changes and closes window **/
    if (event.getSource() == saveButton) {
      if (tableFilter.getSelectedToggle() == countryToggle) {
        
      }
      if (tableFilter.getSelectedToggle() == cityToggle) {
        
      }
      if (tableFilter.getSelectedToggle() == addressToggle) {
        
      }
      if (tableFilter.getSelectedToggle() == allToggle) {
        
      }
    }
  /** Closes current Stage without saving **/
    if (event.getSource() == closeButton) {
      Stage stage = (Stage) closeButton.getScene().getWindow();
      stage.close();
    }
  }
  
/** Resizes, sets visibility, and sets text for columns, labels, and fields for Country **/
  private void setCountryState() {
    updateColumnSize(customerCol, 0);
      updateColumnSize(addressCol, 0);
        updateColumnSize(cityCol, 0);
          updateColumnSize(countryCol, .95);
    customerCol.setVisible(false);
      addressCol.setVisible(false);
        cityCol.setVisible(false);
          countryCol.setVisible(true);
      country_label.setVisible(true);
        city_label.setVisible(false);
          address_label.setVisible(false);
            phone_label.setVisible(false);
              customer_label.setVisible(false);
      country_field.setVisible(true);
        city_field.setVisible(false);
          address_field.setVisible(false);
            phone_field.setVisible(false);
              customer_field.setVisible(false);
      country_field.setEditable(true);
        city_field.setEditable(false);
          address_field.setEditable(false);
            phone_field.setEditable(false);
              customer_field.setEditable(false);
  }
  
/** Resizes, sets visibility, and sets text for columns, labels, and fields for City **/
  private void setCityState() {
    updateColumnSize(customerCol, 0);
      updateColumnSize(addressCol, 0);
        updateColumnSize(cityCol, .5);
          updateColumnSize(countryCol, .45);
    customerCol.setVisible(false);
      addressCol.setVisible(false);
        cityCol.setVisible(true);
          countryCol.setVisible(true);
      country_label.setVisible(true);
        city_label.setVisible(true);
          address_label.setVisible(false);
            phone_label.setVisible(false);
              customer_label.setVisible(false);
      country_field.setVisible(true);
        city_field.setVisible(true);
          address_field.setVisible(false);
            phone_field.setVisible(false);
              customer_field.setVisible(false);
      country_field.setEditable(false);
        city_field.setEditable(true);
          address_field.setEditable(false);
            phone_field.setEditable(false);
              customer_field.setEditable(false);
  }
  
/** Resizes, sets visibility, and sets text for columns, labels, and fields for Address **/
  void setAddressState() {
    updateColumnSize(customerCol, 0);
      updateColumnSize(addressCol, .39);
        updateColumnSize(cityCol, .34);
          updateColumnSize(countryCol, .25);
    customerCol.setVisible(false);
      addressCol.setVisible(true);
        cityCol.setVisible(true);
          countryCol.setVisible(true);
      country_label.setVisible(true);
        city_label.setVisible(true);
          address_label.setVisible(true);
            phone_label.setVisible(true);
              customer_label.setVisible(false);
      country_field.setVisible(true);
        city_field.setVisible(true);
          address_field.setVisible(true);
            phone_field.setVisible(true);
              customer_field.setVisible(false);
      country_field.setEditable(false);
        city_field.setEditable(false);
          address_field.setEditable(true);
            phone_field.setEditable(true);
              customer_field.setEditable(false);
  }
  
  /** Resizes, sets visibility, and sets text for columns, labels, and fields for Address **/
  void setCustomerState() {
    updateColumnSize(customerCol, .35);
      updateColumnSize(addressCol, .3);
        updateColumnSize(cityCol, .2);
          updateColumnSize(countryCol, .1);
    customerCol.setVisible(true);
      addressCol.setVisible(true);
        cityCol.setVisible(true);
          countryCol.setVisible(true);
      country_label.setVisible(true);
        city_label.setVisible(true);
          address_label.setVisible(true);
            phone_label.setVisible(true);
              customer_label.setVisible(true);
      country_field.setVisible(true);
        city_field.setVisible(true);
          address_field.setVisible(true);
            phone_field.setVisible(true);
              customer_field.setVisible(true);
      country_field.setEditable(false);
        city_field.setEditable(false);
          address_field.setEditable(true);
            phone_field.setEditable(true);
              customer_field.setEditable(true);
  }
  
  /** Enables window movement by dragging MenuBar **/
  private void enableWindowMovement() {
    anchor.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
      }
    });
    anchor.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        anchor.getScene().getWindow().setX(event.getScreenX() - xOffset);
        anchor.getScene().getWindow().setY(event.getScreenY() - yOffset);
      }
    });
  }
}

/** Stores table data references for filtered rows containing multiple object types **/
class RowData {
  Country country;
  City city;
  Address address;
  Customer customer;
  
/** TO DISPLAY ON TABLE **/
  ObservableValue<String> countryName, cityName, addrString, customerName;
  
/** Constructors **/
/** CURRENTLY IN USE FOR PRODUCING ROWDATA FOR COUNTRIES **/
  RowData(Country country) {
    this.country = country;
    this.countryName = new ReadOnlyStringWrapper(country.getCountryName());
    this.cityName = new ReadOnlyStringWrapper(" ");
    this.addrString = new ReadOnlyStringWrapper(" ");
    this.customerName = new ReadOnlyStringWrapper(" ");
  }
  
/** CURRENTLY IN USE FOR PRODUCING ROWDATA FOR CITIES **/
  RowData(City city) {
    this.city = city;
    this.country = city.getCountry();
    this.countryName = new ReadOnlyStringWrapper(city.getCountryName());
    this.cityName = new ReadOnlyStringWrapper(city.getCityName());
    this.addrString = new ReadOnlyStringWrapper(" ");
    this.customerName = new ReadOnlyStringWrapper(" ");
  }
  
/** CURRENTLY IN USE FOR PRODUCING ROWDATA FOR ADDRESSES **/
  RowData(Address address) {
    this.address = address;
    this.city = address.getCity();
    this.country = address.getCountry();
    this.countryName = new ReadOnlyStringWrapper(address.getCountryName());
    this.cityName = new ReadOnlyStringWrapper(address.getCityName());
    this.addrString = new ReadOnlyStringWrapper(address.getAddress());
    this.customerName = new ReadOnlyStringWrapper(" ");
  }
  
/** CURRENTLY IN USE FOR PRODUCING ROWDATA FOR CUSTOMERS **/
  RowData(Customer customer) {
    this.customer = customer;
    this.address = customer.getCustomerAddress();
    this.city = customer.getCity();
    this.country = customer.getCountry();
    this.countryName = new ReadOnlyStringWrapper(customer.getCountryName());
    this.cityName = new ReadOnlyStringWrapper(customer.getCityName());
    this.addrString = new ReadOnlyStringWrapper(customer.getAddress());
    this.customerName = customer.getCustomerName();
  }
  
/** Getters **/
  public Country getCountry() {
    return this.country;
  }
  
  public City getCity() {
    return this.city;
  }
  
  public Address getAddress() {
    return this.address;
  }
  
  public Customer getCustomer() {
    return this.customer;
  }
  
/** Setters **/
  public void setAddress(Address address) {
    this.address = address;
  }
  
  public void setCity(City city) {
    this.city = city;
  }
  
  public void setCountry(Country country) {
    this.country = country;
  }
}