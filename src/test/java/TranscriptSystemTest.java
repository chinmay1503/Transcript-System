import com.portlandstateuniversity.transcriptmailingsystem.DBConnection;
import com.portlandstateuniversity.transcriptmailingsystem.Data.Constants;
import com.portlandstateuniversity.transcriptmailingsystem.Exception.TranscriptException;
import com.portlandstateuniversity.transcriptmailingsystem.MailHandler;
import com.portlandstateuniversity.transcriptmailingsystem.mainFrame;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TranscriptSystemTest {
    static Properties properties = new Properties();
    static String currDir = System.getProperty("user.dir");
    DBConnection dbConn = new DBConnection();
    static final String COLLEGE_NAME = "Portland State University";

    @DisplayName("Hello World Test")
    @Test
    public void testHelloWorld() {
        assertEquals("Hello World", "Hello World");
    }

    @BeforeAll
    public static void init() {
        loadProperties();
    }

    private static void loadProperties() {
        InputStream input = null;
        try {
            input = new FileInputStream(currDir + "\\resources\\mail.properties");
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Resource file not found.");
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    @DisplayName("Check if all required properties are defined")
    @Test
    public void checkProperties() {
        assertNotNull(properties.getProperty("mail.smtp.host"));
        assertNotNull(properties.getProperty("mail.smtp.socketFactory.port"));
        assertNotNull(properties.getProperty("mail.smtp.socketFactory.class"));
        assertNotNull(properties.getProperty("mail.smtp.auth"));
        assertNotNull(properties.getProperty("mail.smtp.port"));
        assertNotNull(properties.getProperty("mail.smtp.starttls.enable"));
        assertNotNull(properties.getProperty("email.id"));
        assertNotNull(properties.getProperty("email.password"));
    }

    @DisplayName("Valid email test")
    @Test
    public void emailTest() throws TranscriptException {
        assertTrue(mainFrame.checkValidEmailAdd("test@gmail.com"));
    }

    @DisplayName("Invalid email test")
    @Test
    public void invalidEmailTest() {
        assertFalse(mainFrame.checkValidEmailAdd(""));
        assertFalse(mainFrame.checkValidEmailAdd("test"));
        assertFalse(mainFrame.checkValidEmailAdd("test@gmail..com"));
    }

    @DisplayName("Check Invalid Name")
    @Test
    public void testInvalidName() {
        TranscriptException transcriptException = assertThrows(TranscriptException.class, () -> {
            mainFrame.checkValidName("");
        });

        assertEquals("The name field cannot be blank", transcriptException.getMessage());

        transcriptException = assertThrows(TranscriptException.class, () -> {
            mainFrame.checkValidName("chinmay15@96");
        });

        assertEquals("Name cannot contain digits or special characters", transcriptException.getMessage());

    }

    @DisplayName("Check Invalid Phone Number")
    @Test
    public void testInvalidPhoneNumber() {
        TranscriptException transcriptException = assertThrows(TranscriptException.class, () -> {
            mainFrame.checkValidNumber("90c");
        });

        assertEquals("Should only contain numbers", transcriptException.getMessage());

        transcriptException = assertThrows(TranscriptException.class, () -> {
            mainFrame.checkValidNumber("999999999999999999999999999");
        });

        assertEquals("Mobile number entered in invalid", transcriptException.getMessage());
    }

    @DisplayName("Get DBConnection Test")
    @Test
    public void getConnectionTest() {
        assertNotNull(dbConn.getConnection());
    }

    @DisplayName("Verify Dept Combo Box is Filled")
    @Test
    public void fillDeptComboTest() {
        javax.swing.JComboBox<String> departmentComboBox = new JComboBox<>();
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            conn = dbConn.getConnection();
            psmt = conn.prepareStatement("SELECT * FROM DEPARTMENT");
            rs = psmt.executeQuery();

            while (rs.next()) {
                String deptCode = rs.getString("code");
                departmentComboBox.addItem(deptCode);
            }
        } catch (SQLException ex) {
            System.err.println("SQL connection error occurred");
        } finally {
            DbUtils.closeQuietly(psmt);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(conn);
        }

        Dimension size = departmentComboBox.getSize();
        assertNotNull(size);
    }

    @DisplayName("Verify correct subject of email as per purpose")
    @Test
    public void emailSubjectTest() {
        String subject = MailHandler.getSubject(Constants.RECEIVED);
        assertEquals("Transcript Received - " + COLLEGE_NAME, subject);
        subject = MailHandler.getSubject(Constants.READY);
        assertEquals("Transcript Ready - " + COLLEGE_NAME, subject);
        subject = MailHandler.getSubject(Constants.COLLECTED);
        assertEquals("Transcript Collected - " + COLLEGE_NAME, subject);
    }

}
