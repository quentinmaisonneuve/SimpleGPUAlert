package controller.service.notification;

import java.util.Locale;
import java.util.Properties;

import model.GPUInfo;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.maven.shared.utils.StringUtils;
import controller.Daemon;
import controller.service.PropertyManager;

/**
 * Manage the different type of notification the program can send
 */
public class MailNotificationService implements NotificationService {

    public static final String MAIL_SUBJECT_TEMPLATE = "MAIL_SUBJECT_TEMPLATE";
    public static final String MAIL_MESSAGE_TEMPLATE = "MAIL_MESSAGE_TEMPLATE";
    public static final String USER_MAIL_RECIPIENT = "USER_MAIL_RECIPIENT";
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
        props.put("mail.smtp.host", PropertyManager.properties.getProperty(SMTP_SERVER_ADRESS));
        props.put("mail.smtp.port", PropertyManager.properties.getProperty(PORT));

        // Get the Session object
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(PropertyManager.properties.getProperty(USER_MAIL), PropertyManager.properties.getProperty(USER_PASSWORD));
                    }

                });

        try {

            // Create a default MimeMessage object
            Message message = new MimeMessage(session);
            String sender = PropertyManager.properties.getProperty(USER_MAIL);
            String recipient = PropertyManager.properties.getProperty(USER_MAIL_RECIPIENT);

            // If recipient email is null then use sender mail for the recipient email
            if (StringUtils.isBlank(recipient)) {

                recipient = sender;
            }

            // Set the sender
            message.setFrom(new InternetAddress(sender));

            // Set the recipient
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));

            // Set Subject
            message.setSubject(String.format(PropertyManager.properties.getProperty(MAIL_SUBJECT_TEMPLATE),
                    new Locale(PropertyManager.properties.getProperty(Daemon.LOCALES).toUpperCase()),
                    gpuInfo.getGpuName()));

            // Put the content of your message
            message.setText(String.format(PropertyManager.properties.getProperty(MAIL_MESSAGE_TEMPLATE), gpuInfo.getProductUrl()));

            // Send message
            Transport.send(message);

            Daemon.logger.info("Mail sent successfully");

        } catch (AuthenticationFailedException e) {

            Daemon.logger.error("Bad credentials : Username and Password not accepted.");

        } catch (MessagingException e) {

            Daemon.logger.error(e.getStackTrace());
        }
    }
}
