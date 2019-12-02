/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bharatividyapeeth.transcriptmailingsystem.threadexecutor;

/**
 *
 * @author chinmay
 */

import com.bharatividyapeeth.transcriptmailingsystem.Data.DBConnection;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EmailScheduler {

    private static DBConnection dbConn = new DBConnection();

    public static void scheduleEmailSending() {

        ScheduledExecutorService mailSchedulor  = Executors.newScheduledThreadPool(1);

        Runnable task1 = () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = dbConn.getConnection();
                String sql = "SELECT * FROM BOARD WHERE email_status IN (1,3)";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                while(rs.next()) {
                    String emailTo = rs.getString("email_to");
                    String purpose = rs.getString("purpose");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(ps);
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(conn);
            }
        };

        try {
        ScheduledFuture<?> scheduledFuture = mailSchedulor.schedule(task1, 5, TimeUnit.MINUTES);
        } catch ( RejectedExecutionException e ) {
            System.out.println(e);
        }
    }
}