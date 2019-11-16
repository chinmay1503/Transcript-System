/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bharatividyapeeth.transcriptmailingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author ShAd0w
 */
public class DBConnection {
    public Connection getConnection () {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:F:\\Projects\\transcriptMailingSystem\\db\\transcripts.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
        return conn;
    }
}
