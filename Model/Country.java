package ScheduleSystem.Model;

/** @author Michael **/
public class Country {
  private int countryId; //Index for position in original LIST before being sorted
  private String country;
  
/** Constructors **/
  public Country(int countryId, String country) {
    this.countryId = countryId;
    this.country = country;
  }
  
/** Getters **/  
  public int getCountryId() {
    return countryId;
  }
  
  public String getCountryName() {
    return country;
  }
  
/** Setters **/
  public void setCountryId(int countryId) {
    this.countryId = countryId;
  }
  
  public void setCountryName(String country) {
    this.country = country;
  }
}