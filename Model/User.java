package ScheduleSystem.Model;

/** @author Michael **/
public class User {
  private int userId;
  private String userName, password;
  //private int active;
  
/** Constructors **/
  public User(int userId, String userName) {
    this.userId = userId;
    this.userName = userName;
  }
  
/** Getters **/
  public int getUserId() {
    return this.userId;
  }
  
  public String getName() {
    return this.userName;
  }
  
/** Setters **/
  public void setUserId(int userId) {
    this.userId = userId;
  }
  
  public void setName(String userName) {
    this.userName = userName;
  }
}