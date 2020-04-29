package ScheduleSystem.Model;

import ScheduleSystem.View.ExceptionControls;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** @author Michael **/
public class ServerAuth implements ExceptionControls {
  
/** Instantiates singleton; only one user will be authorized per session **/
  public final static ServerAuth AUTH = new ServerAuth();
  private User currentUser;
  
  public User getCurrentUser() {
    return currentUser;
  }
  
  public void loadUser(ResultSet userSet) {
    try {
          currentUser = new User(userSet.getInt("userId"),
                                  userSet.getString("userName")
          );
    } catch (SQLException e) {
      System.out.println("SQLException: " + e.getMessage());
      System.out.println("SQLState: " + e.getSQLState());
      System.out.println("VendorError: " + e.getErrorCode());
    }
  }
  
/** Checks parameters against existing users and their passwords **/
  public int verifyCredentials(String username, String password) {
    try {
      if (username.isEmpty() | password.isEmpty()) {
        return 0; //null value is present
      }
      String prompt = "SELECT "
                        + "* "
                          + "FROM user "
                            + "WHERE (password='" + password + "')";
      ResultSet userTable = getPreparedStatement(prompt).executeQuery();
      while (userTable.next()) {
        if (userTable.getString("userName").equalsIgnoreCase(username)) {
          loadUser(userTable);
          return 2;
        }
      }
    } catch (SQLException e) {
      System.out.println("SQLException: " + e.getMessage());
      System.out.println("SQLState: " + e.getSQLState());
      System.out.println("VendorError: " + e.getErrorCode());
    }
    return 1; //invalid value(s)
  }
  
/************ Prepared Statement methods ***********/
/** Connects to server **/
  public Connection createConnection() {
    Connection conn;
    String driver = "com.mysql.cj.jdbc.Driver";
    String database = "U05NuP";
    String url = "jdbc:mysql://52.206.157.109/" + database;
    String user = "U05NuP";
    String password = "53688552680";
    try {
      Class.forName(driver);
      try {
        conn = DriverManager.getConnection(url, user, password);
        return conn;
      } catch (SQLException e) { //Authentication/connection failure
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
      }
    } catch (ClassNotFoundException e) {  //Driver failure
      e.printStackTrace();
    }
    return null;
  }
  
  public PreparedStatement getPreparedStatement(String prompt) {
    try {
      PreparedStatement pstmt = createConnection().prepareStatement(prompt);
      return pstmt;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}