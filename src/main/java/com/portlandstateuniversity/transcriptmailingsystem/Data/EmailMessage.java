package com.portlandstateuniversity.transcriptmailingsystem.Data;

/**
 * This class is used to set html template format various fields of the email
 */
public class EmailMessage {

    public static String header_message = "<div>" +
                                        "<h2> <img src=\"https://i.ibb.co/9cMr7RC/PSU-logo-transparent-1.png\" alt=\"mail-header\" style=\"width:599px;height:165px\"</h2>" +
                                        "<h2> <img src=\"https://behnisch.com/thumbs/work/projects/1051/1051_01_brad-feinknopf-1500x1000.jpg\" alt=\"intro-Image\" style=\"width:800px;height:480px\"</h2>";
    public static String greeting = "<h1> Hi %name%!</h1>";

    public static String received_message = "<span style=\"font-size:16px; font-family:tahoma,geneva,sans-serif;\"><strong>Your transcripts have been received and will be ready in 10-15 working days. Once they are ready you will be notified through mail.</strong></span><br>";

    public static String ready_message = "<span style =\"font-size:16px; font-family:tahoma,geneva,sans-serif;\"><strong>Your transcripts are ready, please collect it within 10-15 working days.</strong></span><br>";

    public static String collected_message = "<span style =\"font-size:16px; font-family:tahoma,geneva,sans-serif;\"><strong>Thanks for picking up the transcripts, for any updates or corrections. Please notify the exam cell as soon as possible.</strong></span><br>";

    public static String end_message =  "<span style =\"font-size:16px; font-family:tahoma,geneva,sans-serif;\"><strong>For any further questions or queries, you can mail at psu-studentservices@fakemail.com.</strong><br>" +
                                        "<b>Greetings,</b><br>" +
                                        "<b>Exam Cell,</b><br>" +
                                        "<b>Portland State University, Portland Oregon.</b></span>";


}
