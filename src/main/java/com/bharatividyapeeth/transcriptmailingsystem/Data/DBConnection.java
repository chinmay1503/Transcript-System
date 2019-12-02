package com.bharatividyapeeth.transcriptmailingsystem.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ShAd0w
 */
public class DBConnection {

   private static DBConnection dbConn = new DBConnection();

   public DBConnection() { }

   public static DBConnection getInstance() {
      return dbConn;
   }

   public Connection getConnection () {
      Connection conn = null;
      try {
         // db parameters
         String url = "jdbc:sqlite:resources\\db\\transcripts.db";
         // create a connection to the database
         conn = DriverManager.getConnection(url);
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return conn;
   }

}