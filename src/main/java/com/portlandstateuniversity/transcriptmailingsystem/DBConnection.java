/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.portlandstateuniversity.transcriptmailingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.portlandstateuniversity.transcriptmailingsystem.Data.Student;
import com.portlandstateuniversity.transcriptmailingsystem.Exception.TranscriptException;
import com.portlandstateuniversity.transcriptmailingsystem.Data.Constants;
import org.apache.commons.dbutils.DbUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *  This class has all the logic related to Database Handling.
 *  From creating connections to performing all the necessary CRUD operations
 */
public class DBConnection {
    public Connection getConnection () {
        Connection conn = null;
        try {
            // db parameters
            String currDir = System.getProperty("user.dir");
            String url = "jdbc:sqlite:" + currDir + "\\db\\transcripts.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
        return conn;
    }

    /**
     *  This method is used to enable cascade delete
     * @throws SQLException raises SQL exception when delete record
     */
    public void enableSQLiteCascadeDeletes() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "PRAGMA foreign_keys=ON";
            ps = conn.prepareStatement(sql);
            ps.execute();
        } finally {
            try {
                DbUtils.close(ps);
                DbUtils.close(conn);
            } catch (SQLException ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is used to return department table
     * @param departmentComboBox It takes departmentComboBox as a parameter
     * @throws SQLException It raises SQL Exception when the department table not exists
     */
    public void fillDeptCombo(JComboBox<String> departmentComboBox) throws SQLException {
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            psmt = conn.prepareStatement("SELECT * FROM DEPARTMENT");
            rs = psmt.executeQuery();

            while (rs.next()) {
                String deptCode = rs.getString("code");
                departmentComboBox.addItem(deptCode);
            }
        } finally {
            DbUtils.closeQuietly(psmt);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * This method is used to check whether all the students have unique phone number or not
     * If the unique count is greater than zero then it raises the exception
     * @param number It takes phone number as the input parameter
     * @throws TranscriptException Raises TranscriptException
     */
    public void checkUnique(String number) throws TranscriptException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int checkUniqueCount = 0;
            conn = getConnection();
            String sql = "SELECT COUNT(1) FROM Students WHERE phone_number = " + number;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                checkUniqueCount = rs.getInt(1);
            }
            if (checkUniqueCount > 0) {
                throw new TranscriptException("This phone number already exists in the database, Enter unique 10-digit phone number");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                DbUtils.close(ps);
                DbUtils.close(conn);
            } catch (SQLException ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is used to update the Student record
     * @param studentId Student id as the input parameter
     * @param name  Student name as the input parameter
     * @param number  Student phone number as the input parameter
     * @param emailAdd  Student email_id as the input parameter
     * @param dept       Student dept as the input parameter
     * @param formattedDate  Student formattedDate as the input parameter
     * @throws SQLException It raises the SQl Exception when Student Record not found
     */
    public void updateStudentRecord(int studentId, String name, String number, String emailAdd, String dept, String formattedDate) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "UPDATE Students set name = '" + name + "' , phone_number =  '" + number + "'"
                    + ", email_address = '" + emailAdd + "', dept_code = '" + dept + "'"
                    + ", passing_year = strftime('%d-%m-%Y', '" + formattedDate + "') "
                    + "WHERE id = " + studentId;
            ps = conn.prepareStatement(sql);
            ps.execute();
            updateStudentRecordQuery(conn, ps, "Transcript_Received", studentId, name, number, emailAdd, dept);
            updateStudentRecordQuery(conn, ps, "Transcript_Ready", studentId, name, number, emailAdd, dept);
            updateStudentRecordQuery(conn, ps, "Transcript_Collected", studentId, name, number, emailAdd, dept);
        } finally {
            try {
                DbUtils.close(ps);
                DbUtils.close(conn);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is used to prepare updateStudent record query
     * @param conn SQL connection statement
     * @param ps SQL prepared Statement
     * @param tableName It takes tableName as an input
     * @param studentId It takes StudentId as an input paramater
     * @param name Student Name
     * @param number Student Phone number
     * @param emailAdd Student Email ID
     * @param dept Student department
     * @throws SQLException Raises an SQL Exception of given student record not found
     */
    public void updateStudentRecordQuery(Connection conn, PreparedStatement ps, String tableName, int studentId, String name, String number, String emailAdd, String dept) throws SQLException {
        String sql = "UPDATE " + tableName + " set name = '" + name + "' , phone_number =  '" + number + "'"
                + ", email_address = '" + emailAdd + "', dept_code = '" + dept + "'"
                + "WHERE id = " + studentId;
        ps = conn.prepareStatement(sql);
        ps.execute();
    }

    public void setSentMailTrue(String tableName, int studentId) throws SQLException {
        String sql = "UPDATE " + tableName + " set isMailSent = 1 WHERE id = " + studentId;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } finally {
            DbUtils.close(ps);
            DbUtils.close(conn);
        }
    }

    /**
     * This method is used to add a new student record
     * @param name Student Name
     * @param number Student Phone Number
     * @param emailAdd Student Email Id
     * @param dept Student Department Name
     * @param formattedDate Date when student record added
     * @return return the 1 if record added successfully else 0
     */
    public int addStudentRecord(String name, String number, String emailAdd, String dept, String formattedDate) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int studentId = 0;
        try {
            conn = getConnection();
            String sql = "INSERT INTO Students(name, phone_number, email_address, dept_code, passing_year, is_recieved) "
                    + "VALUES('" + name + "', '" + number + "' , '" + emailAdd + "', '" + dept + "',strftime('%d-%m-%Y', '" + formattedDate + "'), true)";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            sql = "SELECT MAX(id) FROM Students";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                studentId = rs.getInt(1);
            }

            return studentId;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(pstmt);
        }
        return studentId;
    }

    /**
     * This method is used to add transcript into a given student record
     * @param studentId Student Id
     * @param name Student Name
     * @param emailAdd Student Email ID
     * @return return List student inserted student list
     */
    public List<Student> addTranscriptReceivedRecord(int studentId, String name, String emailAdd) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            Student student = new Student(studentId, name, emailAdd);
            List<Student> studentList = new ArrayList();
            studentList.add(student);
            String sql = "INSERT INTO Transcript_Received (id, name, phone_number, email_address, dept_code, received_date) "
                    + "SELECT id, name, phone_number, email_address, dept_code, datetime('now') FROM Students WHERE id = " + studentId;
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            return studentList;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(pstmt);
        }
        return null;
    }

    /**
     * This method is used to retrieve the list of students
     * @param studentDetailsTable Student Details Table
     */
    public void getStudentList(JTable studentDetailsTable) {
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            psmt = conn.prepareStatement("SELECT id AS ID,name AS Name,phone_number AS 'Mobile Number',"
                    + "                   email_address AS 'Email Address', dept_code AS Department,"
                    + "                   passing_year AS 'Passing Year', "
                    + "                   is_recieved as Recieved,"
                    + "                   is_ready as Ready,"
                    + "                   is_collected as Collected FROM Students");
            rs = psmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // for changing column and row model
            DefaultTableModel tableModel = (DefaultTableModel) studentDetailsTable.getModel();
            // clear existing columns
            tableModel.setColumnCount(0);
            // add specified columns to table
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }
            // clear existing rows
            tableModel.setRowCount(0);
            // add rows to table
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                tableModel.addRow(row);
            }
            tableModel.fireTableDataChanged();
            //studentDetailsTable.setDefaultEditor(Object.class, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(psmt);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * This method is used to retrieve a list of students whose transcripts are received
     * @param receivedTable Table with list of student whose transcripts are received
     */
    public void getRecievedList(JTable receivedTable) {
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            psmt = conn.prepareStatement("SELECT id AS ID,name AS Name,phone_number AS 'Mobile Number',"
                    + "                   email_address AS 'Email Address', dept_code AS Department,"
                    + "                   received_date AS 'Date Received' "
                    + "                   FROM Transcript_Received");
            rs = psmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // for changing column and row model
            DefaultTableModel tableModel = (DefaultTableModel) receivedTable.getModel();
            // clear existing columns
            tableModel.setColumnCount(0);
            // add specified columns to table
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }
            // clear existing rows
            tableModel.setRowCount(0);
            // add rows to table
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                tableModel.addRow(row);
            }
            tableModel.fireTableDataChanged();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(psmt);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * This method is used to retrieve a list of students whose transcripts are ready(available)
     * @param readyTable Table with list of students whose transcript are ready
     */
    public void getReadyList(JTable readyTable) {
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            psmt = conn.prepareStatement("SELECT id AS ID,name AS Name,phone_number AS 'Mobile Number',"
                    + "                   email_address AS 'Email Address', dept_code AS Department,"
                    + "                   ready_date AS 'Date Ready' "
                    + "                   FROM Transcript_Ready");
            rs = psmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // for changing column and row model
            DefaultTableModel tableModel = (DefaultTableModel) readyTable.getModel();
            // clear existing columns
            tableModel.setColumnCount(0);
            // add specified columns to table
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }
            // clear existing rows
            tableModel.setRowCount(0);
            // add rows to table
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                tableModel.addRow(row);
            }
            tableModel.fireTableDataChanged();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(psmt);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * This method is used to retrieve a list of students whose transcripts are collected
     * @param collectedTable Table with list of students with list of students who collected their transcripts
     */
    public void getCollectedList(JTable collectedTable) {
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            psmt = conn.prepareStatement("SELECT id AS ID,name AS Name,phone_number AS 'Mobile Number',"
                    + "                   email_address AS 'Email Address', dept_code AS Department,"
                    + "                   collected_date AS 'Date Collected' "
                    + "                   FROM Transcript_Collected");
            rs = psmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // for changing column and row model
            DefaultTableModel tableModel = (DefaultTableModel) collectedTable.getModel();
            // clear existing columns
            tableModel.setColumnCount(0);
            // add specified columns to table
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }
            // clear existing rows
            tableModel.setRowCount(0);
            // add rows to table
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                tableModel.addRow(row);
            }
            tableModel.fireTableDataChanged();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(psmt);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * This method is used to remove the student record from the student details table
     * @param studentDetailsTable Student Details Table
     */
    public void deleteRecords(JTable studentDetailsTable) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            int[] rows = studentDetailsTable.getSelectedRows();
            for (int i : rows) {
                conn = getConnection();
                String id = (String) studentDetailsTable.getValueAt(i, 0);
                int studentId = Integer.parseInt(id);
                String sql = "Delete FROM Students WHERE id = " + studentId;
                ps = conn.prepareStatement(sql);
                ps.execute();

                sql = "Delete FROM Transcript_Received WHERE id = " + studentId;
                ps = conn.prepareStatement(sql);
                ps.execute();

                sql = "Delete FROM Transcript_Ready WHERE id = " + studentId;
                ps = conn.prepareStatement(sql);
                ps.execute();

                sql = "Delete FROM Transcript_Collected WHERE id = " + studentId;
                ps = conn.prepareStatement(sql);
                ps.execute();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(studentDetailsTable, ex, ex.getMessage(), JOptionPane.ERROR_MESSAGE, null);
        } finally {
            try {
                DbUtils.close(ps);
                DbUtils.close(conn);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is used to move the transcript system web application next state
     * @param studentId Student Id
     * @param tableFrom Source Table
     * @param tableTo Destination Table
     * @param dateColumn Date Column
     * @throws SQLException It raises SQL exception when any of the following field values not found
     */
    public void moveToNextState(int studentId, String tableFrom, String tableTo, String dateColumn) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + tableTo + " (id, name, phone_number, email_address, dept_code, " + dateColumn + ")"
                    + "SELECT id, name, phone_number, email_address, dept_code, datetime('now', 'localtime') FROM Students WHERE id = " + studentId;
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.execute();
            sql = "DELETE FROM " + tableFrom + " WHERE id = " + studentId;
            ps = conn.prepareStatement(sql);
            ps.execute();
            updateStudentStatus(studentId, tableTo);
        } finally {
            try {
                DbUtils.close(ps);
                DbUtils.close(conn);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is used to update the student status
     * @param studentId
     * @param tableTo
     * @throws SQLException
     */
    private void updateStudentStatus(int studentId, String tableTo) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String columnName = getFlagColumn(tableTo);
            String sql = "UPDATE Students set " + columnName + " = 1 WHERE id = " + studentId;
            ps = conn.prepareStatement(sql);
            ps.execute();
        } finally {
            try {
                DbUtils.close(ps);
                DbUtils.close(conn);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is used to return a string which indicates the status of transcript
     * @param tableTo
     * @return
     */
    private String getFlagColumn(String tableTo) {
        if (tableTo.equals(Constants.TRANSCRIPT_READY)) {
            return "is_ready";
        } else if (tableTo.equals(Constants.TRANSCRIPT_COLLECTED)) {
            return "is_collected";
        }
        return "";
    }
}
