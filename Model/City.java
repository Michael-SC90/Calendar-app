package ScheduleSystem.Model;

/** @author Michael **/
public class City extends Country {
  private int cityId;
  private String city;
  
/** Constructors **/
  public City(int cityId, String city, Country country) {
    super(country.getCountryId(), country.getCountryName());
    this.cityId = cityId;
    this.city = city;
  }
  
  /**
  public City(City city) {
    super(city.getCountryId(), city.getCountryName());
    this.cityId = city.getCityId();
    this.city = city.getCityName();
  }
  
  public City (Country country) {
    super(country.getCountryId(), country.getCountryName());
  } **/
  
/** Getters **/
  public int getCityId() {
    return cityId;
  }
  
  public String getCityName() {
    return city;
  }
  
  public Country getCountry() {
    return this;
  }
  
/** Setters **/
  public void setCityId(int cityId) {
    this.cityId = cityId;
  }
  
  public void setCityName(String city) {
    this.city = city;
  }
  
  public void setCountry(Country country) {
    this.setCountryId(country.getCountryId());
    this.setCountryName(country.getCountryName());
  }
}