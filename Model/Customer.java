package ScheduleSystem.Model;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;

/** @author Michael **/
public class Customer extends Address {
  private int customerId;
  private ObservableValue<String> customerName;
  //private int active;

/** Constructors **/
  public Customer(int customerId, String customerName, Address address) {
    super(address.getAddressId(), address.getAddress(), address.getPhone(), address);
    this.customerId = customerId;
    this.customerName = new ReadOnlyStringWrapper(customerName);
  }
  
/** Getters **/  
  public int getCustomerId() {
    return this.customerId;
  }
  
  public ObservableValue<String> getCustomerName() {
    return this.customerName;
  }
  
  public Address getCustomerAddress() {
    return this;
  }
  
/** Setters **/  
  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }

  public void setCustomerName(String name) {
    this.customerName = new ReadOnlyStringWrapper(name);
  }
  
  public void setCustomerAddress(Address address) {
    this.setAddressId(address.getAddressId());
    this.setAddress(address.getAddress());
    this.setCity(address);
  }
}