package service.notification;

import java.util.Properties;

import data.GPUInfo;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import service.PropertyManager;

/**
 * Manage the different type of notification the program can send
 */
public class MailNotificationService implements NotificationService {

    /**
     * Send email notification
     * Gmail configuration requirements :
     *  Two Steps Verification should be turned off.
     *  Allow Less Secure App(should be turned on) : https://myaccount.google.com/lesssecureapps
     *  Try this link if it still doesn't work : https://accounts.google.com/DisplayUnlockCaptcha
     * @param gpuInfo Nvidia GPU informations
     */
    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", PropertyManager.properties.getProperty("SMTP_SERVER_ADRESS"));
        props.put("mail.smtp.port", PropertyManager.properties.getProperty("PORT"));

        // Get the Session object
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(PropertyManager.properties.getProperty("USER_MAIL"), PropertyManager.properties.getProperty("USER_PASSWORD"));
                    }
                });

        try {

            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(PropertyManager.properties.getProperty("USER_MAIL")));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(PropertyManager.properties.getProperty("USER_MAIL_DEST")));

            // Set Subject
            message.setSubject("NVIDIA ".concat(gpuInfo.getGpuName().toString()).concat(" is available"));

            // Put the content of your message
            message.setText("Purchase link : ".concat(gpuInfo.getProductUrl()));

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {

            e.printStackTrace();

        }
    }
}
