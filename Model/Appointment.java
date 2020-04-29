package ScheduleSystem.Model;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;

/** @author Michael **/
public class Appointment extends Customer {
  private int appointmentId, userId;
  private ObservableValue<String> title, type, startTime, endTime;
  //private String url, description, location;
  private String contact;
  private ZonedDateTime start, end;

/** Constructors **/
/** For loading existing Appointments into Appointment Screen **/
  public Appointment(int appointmentId, Customer customer, int userId, Timestamp start, Timestamp end, 
                      String title, String contact, String type) {
    super(customer.getCustomerId(), customer.getCustomerName().getValue(), customer);
    this.appointmentId = appointmentId;
    this.userId = userId;
    this.start = ZonedDateTime.from(start.toLocalDateTime().atZone(ZoneId.systemDefault()));
    this.end = ZonedDateTime.from(end.toLocalDateTime().atZone(ZoneId.systemDefault()));
    this.title = new ReadOnlyStringWrapper(title);
    this.contact = contact;
    this.type = new ReadOnlyStringWrapper(type);
    this.startTime = new ReadOnlyStringWrapper(this.start.toLocalDateTime().toString());
    this.endTime = new ReadOnlyStringWrapper(this.end.toLocalDateTime().toString());
  }
  
/** For loading new Appointments into Appointment Screen **/
  public Appointment(Customer customer) {
    super(customer.getCustomerId(), customer.getCustomerName().getValue(), customer);
    this.appointmentId = -1;
  }
  
/** Getters **/
  public int getAptId() {
    return this.appointmentId;
  }
  
  public int getUserId() {
    return this.userId;
  }
  
  public Customer getCustomer() {
    return this;
  }
  
  public ObservableValue<String> getTitle() {
    return title;
  }
  
  public String getContact() {
    return this.contact;
  }
  
  public ObservableValue<String> getType () {
    return type;
  }
  
  public ZonedDateTime getStart() {
    return this.start;
  }
  
  public ObservableValue<String> getStartTime () {
    return startTime;
  }
  
  public ZonedDateTime getEnd() {
    return this.end;
  }
  
  public ObservableValue<String> getEndTime () {
    return endTime;
  }
  
/** Setters **/
  public void setAptId(int appointmentId) {
    this.appointmentId = appointmentId;
  }
  
  public void setUserId(int userId) {
    this.userId = userId;
  }
  
  public void setCustomer(Customer customer) {
    this.setCustomerId(customer.getCustomerId());
    this.setCustomerName(customer.getCustomerName().getValue());
    this.setCustomerAddress(customer);
  }
  
  public void setTitle(String title) {
    this.title = new ReadOnlyStringWrapper(title);
  }
  
  public void setContact(String contact) {
    this.contact = contact;
  }
  
  public void setType(String type) {
    this.type = new ReadOnlyStringWrapper(type);
  }
  
  public void setStart(ZonedDateTime start) {
    this.start = start;
    this.startTime = new ReadOnlyStringWrapper(start.toLocalDateTime().toString());
  }
  
  public void setEnd(ZonedDateTime end) {
    this.end = end;
    this.endTime = new ReadOnlyStringWrapper(end.toLocalDateTime().toString());
  }
}