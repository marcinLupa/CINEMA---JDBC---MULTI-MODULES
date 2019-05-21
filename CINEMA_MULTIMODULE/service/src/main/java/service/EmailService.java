package service;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author Marcin Lupa
 * class used to sending email
 */
 class EmailService {
    private static final String emailAddress = "adamwrzeszczowicz@gmail.com";
    private static final String emailPassword = "TOjestPRZYKLADOWEhaslo2955!";

     void sendAsHtml(String to, String title, String html) throws MessagingException {
        System.out.println("Sending email to " + to + " ...");

        Session session = createSession();

        MimeMessage message = new MimeMessage(session);
        prepareEmailMessage(message, to, title, html);

        Transport.send(message);
        System.out.println("Email has been sent to " + to);
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, emailPassword);
            }
        });
    }

    private void prepareEmailMessage(MimeMessage message, String to, String title, String html) throws MessagingException {
        message.setContent(html, "text/html; charset=utf-8");
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(title);
    }
}
