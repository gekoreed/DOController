package digitalOcean.services;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by gekoreed on 5/3/15.
 */
public class EmailSender {
    public void send(String reciver, String content) {


        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("controllreports@gmail.com", "feErt6Om4far1aF1");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("controllreports@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(reciver));
            message.setSubject("DOControl Report");
            message.setText(content + "\nPlease, do not respond to this E-Mail");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
