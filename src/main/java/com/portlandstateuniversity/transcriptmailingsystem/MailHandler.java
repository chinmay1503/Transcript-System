package com.portlandstateuniversity.transcriptmailingsystem;

import com.portlandstateuniversity.transcriptmailingsystem.Data.Student;
import com.portlandstateuniversity.transcriptmailingsystem.Data.Constants;
import com.portlandstateuniversity.transcriptmailingsystem.Data.EmailMessage;
import org.apache.commons.io.IOUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This class managed all the logic related to sending emails.
 *  MailHandler class used to send email notifications to respective students
 */
public class MailHandler {
    Properties props = new Properties();
    static String currDir = System.getProperty("user.dir");
    static final String COLLEGE_NAME = "Portland State University";

    public MailHandler() {
        loadProperties();
    }

    /**
     * This method is used to load the all meta data information required to send the email
     */
    private void loadProperties() {
        InputStream input = null;
        try {
            input = new FileInputStream(currDir + "\\resources\\mail.properties");
            props.load(input);
        } catch (IOException ex) {
            Logger.getLogger(MailHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * This method is used to establish the session as soon as the user login
     * @param from
     * @param password
     * @return
     */
    private Session getSession(String from, String password) {
        //get Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });
        return session;
    }

    /**
     * This method return the Transcript status along with College name
     * @param purpose It indicates the transcript purpose
     * @return Returns a String indicating transcript subject status
     */
    public static String getSubject(String purpose) {
        switch (purpose) {
            case Constants.RECEIVED:
                return "Transcript Received - " + COLLEGE_NAME;
            case Constants.READY:
                return "Transcript Ready - " + COLLEGE_NAME;
            case Constants.COLLECTED:
                return "Transcript Collected - " + COLLEGE_NAME;
        }
        return "";
    }

    /**
     * This method is used to return the transcript status message
     * @param purpose
     * @return
     */
    private String getMessage(String purpose) {
        switch (purpose) {
            case Constants.RECEIVED:
                return EmailMessage.received_message;
            case Constants.READY:
                return EmailMessage.ready_message;
            case Constants.COLLECTED:
                return EmailMessage.collected_message;
        }
        return "";
    }

    /**
     * This method is used to send a email notification to the student
     * @param dbConn Database connection
     * @param students List of Students
     * @param purpose Transcript purpose
     * @throws MessagingException Raises Message Exception
     */
    public void sendMail(DBConnection dbConn, List<Student> students, String purpose) throws MessagingException {

        String from = props.getProperty("email.id");
        String password = props.getProperty("email.password");
        String sub = getSubject(purpose);
        String header = EmailMessage.header_message + EmailMessage.greeting;
        String msg = header + " " + getMessage(purpose) + " " + EmailMessage.end_message;
        Session session = getSession(from, password);
        //compose message
        MimeMessage message = new MimeMessage(session);
        message.setSubject(sub);

        for (Student student : students) {
            //send message
            msg = msg.replace("%name%", student.getName());
            message.setContent(msg, "text/html");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(student.getEmailAddress()));
            Transport.send(message);
            setSentMailTrue(dbConn, purpose, student.getId());
        }
    }

    /**
     * This method is used to return the table name based on transcript status
     * @param dbConn
     * @param purpose
     * @param studentId
     */
    private void setSentMailTrue(DBConnection dbConn, String purpose, int studentId) {
        String tableName = null;
        switch (purpose) {
            case Constants.RECEIVED:
                tableName = "Transcript_Received";
                break;
            case Constants.READY:
                tableName = "Transcript_Ready";
                break;
        }
        try {
            dbConn.setSentMailTrue(tableName, studentId);
        }
        catch (SQLException ex) {
            Logger.getLogger(MailHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
