package controller.service.notification;

import java.util.Properties;

import controller.Daemon;
import controller.service.PropertyManager;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import model.GPUInfo;

/**
 * Manage the different type of notification the program can send
 */
public class MailNotificationService implements NotificationService {

    public static final String MAIL_SUBJECT_TEMPLATE = "MAIL_SUBJECT_TEMPLATE";
    public static final String MAIL_MESSAGE_TEMPLATE = "MAIL_MESSAGE_TEMPLATE";
    public static final String USER_MAIL_RECIPIENTS = "USER_MAIL_RECIPIENTS";
    public static final String USER_MAIL = "USER_MAIL";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String SMTP_SERVER_ADRESS = "SMTP_SERVER_ADRESS";
    public static final String PORT = "PORT";

    /**
     * Send email notification
     * @param gpuInfo Nvidia GPU informations
     */
    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", PropertyManager.getProperty(SMTP_SERVER_ADRESS));
        props.put("mail.smtp.port", PropertyManager.getProperty(PORT));

        // Get the Session object
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(PropertyManager.getProperty(USER_MAIL), PropertyManager.getProperty(USER_PASSWORD));
                    }
                });

        try {

            // Create a default MimeMessage object
            Message message = new MimeMessage(session);
            InternetAddress sender = new InternetAddress(PropertyManager.getProperty(USER_MAIL));
            InternetAddress[] recipient = InternetAddress.parse(PropertyManager.getProperty(USER_MAIL_RECIPIENTS));

            // If recipient email is null then use sender mail for the recipient email
            if (recipient.length == 0) {

                recipient = new InternetAddress[] {sender};
            }

            // Set the sender
            message.setFrom(sender);

            // Set the recipient
            message.setRecipients(Message.RecipientType.TO, recipient);

            // Set Subject
            message.setSubject(NotificationManager.formatString(PropertyManager.getProperty(MAIL_SUBJECT_TEMPLATE), gpuInfo, false));

            // Put the content of your message
            message.setText(NotificationManager.formatString(PropertyManager.getProperty(MAIL_MESSAGE_TEMPLATE), gpuInfo, false));

            // Send message
            Transport.send(message);

            Daemon.logger.info("Mail sent successfully to : ".concat(sender.toString()));

        } catch (AuthenticationFailedException e) {

            Daemon.logger.error("Mail not sent : Bad credentials - Username and Password not accepted.");

        } catch (MessagingException e) {

            Daemon.logger.error(e);
        }
    }
}
