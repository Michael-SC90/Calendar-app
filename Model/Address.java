package ScheduleSystem.Model;

/** @author Michael **/
public class Address extends City {
  private int addressId;
  private String address, phone;
                  //address2,
                    //postalCode;
  
/** Constructors **/
  public Address(int addressId, String address, String phone, City city) {
    super(city.getCityId(), city.getCityName(), city);
    this.addressId = addressId;
    this.address = address;
    this.phone = phone;
  }
  
  /**
  public Address(City city) {
    super(city);
  }
  
  public Address(Country country) {
    super(country);
  } **/
  
/** Getters **/  
  public int getAddressId() {
    return addressId;
  }
  
  public City getCity() {
    return this;
  }
  
  public String getAddress() {
    return address;
  }
  
  public String getPhone() {
    return phone;
  }
  
/** Setters **/
  public void setAddressId(int addressId) {
    this.addressId = addressId;
  }
  
  public void setCity(City city) {
    this.setCityId(city.getCityId());
    this.setCityName(city.getCityName());
    this.setCountry(city);
  }

  public void setAddress(String address) {
    this.address = address;
  }
  
  public void setPhone(String phone) {
    this.phone = phone;
  }
}