package Services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Properties;

import Services.GPU.GPUInfo;
import Services.GPU.GPUName;
import Services.GPU.Service;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Manage the different type of notification the program can send
 */
public class NotificationService extends Service {

    public NotificationService(Properties properties) {

        super(properties);
    }

    /**
     * Send email notification
     * Gmail configuration requirements :
     *  Two Steps Verification should be turned off.
     *  Allow Less Secure App(should be turned on) : https://myaccount.google.com/lesssecureapps
     *  Try this link if it still doesn't work : https://accounts.google.com/DisplayUnlockCaptcha
     * @param gpu Nvidia Services.GPU
     * @param purchaseLink Purchase link
     */
    public void sendEmailNotification(GPUName gpu, String purchaseLink) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", getProperties().getProperty("SMTP_SERVER_ADRESS"));
        props.put("mail.smtp.port", getProperties().getProperty("PORT"));

        // Get the Session object
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getProperties().getProperty("USER_MAIL"), getProperties().getProperty("USER_PASSWORD"));
                    }
                });

        try {

            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(getProperties().getProperty("USER_MAIL")));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(getProperties().getProperty("USER_MAIL_DEST")));

            // Set Subject
            message.setSubject("NVIDIA ".concat(gpu.toString()).concat(" is available"));

            // Put the content of your message
            message.setText("Purchase link : ".concat(purchaseLink));

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Send telegram notification
     * @param gpuInfo Nvidia Services.GPU informations
     */
    public void sendTelegramNotification(GPUInfo gpuInfo) {

        String urlString = getProperties().getProperty("TELEGRAM_API_LINK");
        String message = "Nvidia " + new Locale(getProperties().getProperty("LOCALE")).toString().toUpperCase() +
                "%0AFE Nvidia GeForce RTX " + gpuInfo.getGpuName() +
                "%0A" + gpuInfo.getProductUrl();

        urlString = String.format(urlString, getProperties().getProperty("API_TOKEN"), getProperties().getProperty("CHAT_ID"), message);

        try {

            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
