package utils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javax.mail.*;

public class EmailUtilities {

    Printer log = new Printer(EmailUtilities.class);

    public static void main(String[] args) {}

    public void sendEmail(String subject, String content, String receiver, String ID, String Password) {

        // Assuming you are sending email from through gmail's smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ID, Password);
            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(ID));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(content);

            log.new info("Sending...");
            Transport.send(message);// Send message
            log.new success("Sent message successfully....");
        }
        catch (MessagingException mex) {log.new error(mex.getMessage());}
    }

}
