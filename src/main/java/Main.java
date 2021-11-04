import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Main {

    // Json link attributes from nvidia web page
    public static final String DIRECT_PURCHASE_LINK = "directPurchaseLink";
    public static final String PURCHASE_LINK = "purchaseLink";

    // Gecko driver constants
    public static final String GECKODRIVER_BASE_PATH = "src/main/resources/geckodriver_";
    public static final String OS_ARCH = "os.arch";
    public static final String OS_NAME = "os.name";
    public static final String WINDOWS = "win";
    public static final String ARCH64 = "64";
    public static final String MAC_OS = "mac";
    public static final String LINUX = "linux";
    public static final String ARCH32 = "32";

    // Properties of the program
    public static Properties properties;

    public static void main(String[] args) {

        System.setProperty("webdriver.gecko.driver", getGeckoDriverPath());
        WebDriver driver = new FirefoxDriver();
        properties = PropertiesManager.loadPropertiesFromFile("src/main/resources/configuration.properties");

        try {

            while(true) {

                driver.get(properties.getProperty("NVIDIA_WEB_GPU_PAGE"));

                Thread.sleep(Long.parseLong(properties.getProperty("REFRESH_INTERVAL")));

                List<GPU> gpuToFind = Arrays.asList(GPU._3060ti, GPU._3080);
                String purchaseLink;

                for (GPU gpu : gpuToFind) {

                    System.out.println(gpu);
                    purchaseLink = getPurchaseLink(driver, gpu);
                    System.out.println(Objects.requireNonNullElse(purchaseLink, "No drop available yet"));

                    if(purchaseLink != null) {

                        sendEmailNotification(gpu, purchaseLink);
                    }
                }
            }

        } catch (InterruptedException e) {

            e.printStackTrace();

        } finally {

            driver.quit();
        }
    }

    /**
     * Send email notification
     * Gmail configuration requirements :
     *  Two Steps Verification should be turned off.
     *  Allow Less Secure App(should be turned on) : https://myaccount.google.com/lesssecureapps
     *  Try this link if it still doesn't work : https://accounts.google.com/DisplayUnlockCaptcha
     * @param gpu Nvidia GPU
     * @param purchaseLink Purchase link
     */
    private static void sendEmailNotification(GPU gpu, String purchaseLink) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", properties.getProperty("SMTP_SERVER_ADRESS"));
        props.put("mail.smtp.port", properties.getProperty("PORT"));

        // Get the Session object
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(properties.getProperty("USER_MAIL"), properties.getProperty("USER_PASSWORD"));
                    }
                });

        try {

            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(properties.getProperty("USER_MAIL")));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(properties.getProperty("USER_MAIL_DEST")));

            // Set Subject
            message.setSubject("NVIDIA ".concat(gpu.toString()).concat(" is available"));

            // Put the content of your message
            message.setText("Purchase link : ".concat(purchaseLink));

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (Exception e) {

            System.out.println(e.getCause());
        }
    }

    /**
     * Get the gecko driver file base on os and architecture
     * @return Gecko driver path
     */
    private static String getGeckoDriverPath() {

        StringBuilder pathToGeckoDriver = new StringBuilder(GECKODRIVER_BASE_PATH);

        boolean arch64 = System.getProperty(OS_ARCH).contains(ARCH64);

        if(System.getProperty(OS_NAME).toLowerCase().contains(WINDOWS)) {

            pathToGeckoDriver.append(WINDOWS);

        } else if(System.getProperty(OS_NAME).toLowerCase().contains(MAC_OS)) {

            pathToGeckoDriver.append(MAC_OS);

        } else if(System.getProperty(OS_NAME).toLowerCase().contains(LINUX)) {

            pathToGeckoDriver.append(LINUX);

        } else {

            pathToGeckoDriver = null;
        }

        if(pathToGeckoDriver != null) {

            if(arch64) {

                pathToGeckoDriver.append(ARCH64);

            } else {

                pathToGeckoDriver.append(ARCH32);
            }
        }

        return Objects.requireNonNullElse(pathToGeckoDriver.toString(), null);
    }

    /**
     * Return the class name of the div who contains supply information of the gpu by giving the GPU name
     * @param gpu Nvidia FE GPU
     * @return Div class of supply information, example : NVGFT060T if for an 3060ti
     */
    public static String getClassDivGPU(GPU gpu) {

        StringBuilder result = new StringBuilder("NVGFT");

        result.append(gpu.toString(), 1, 4);

        if(gpu.toString().contains("ti")) {

            result.append('T');
        }

        return result.toString();
    }

    /**
     * Retrieve JSON from GPU class div
     * @param driver Webconnexion
     * @param gpu Nvidia FE GPU
     * @return JSON of supply information
     */
    public static String getGPUJson(WebDriver driver, GPU gpu) {

        return driver.findElement(
                        By.className(getClassDivGPU(gpu)))
                .getAttribute("innerText")
                .replace('[',' ')
                .replace(']',' ');
    }

    /**
     * Parse the JSON to get the purchase link
     * @param json JSON of GPU
     * @return Purchase link if exist null otherwise
     */
    public static String parsePurchaseLink(String json) {

        String link = null;

        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        if(jsonObject.get(DIRECT_PURCHASE_LINK) != null) {

            link = jsonObject.get(DIRECT_PURCHASE_LINK).getAsString();

        } else if(jsonObject.get(PURCHASE_LINK) != null) {

            link = jsonObject.get(PURCHASE_LINK).getAsString();
        }

        return link;
    }

    /**
     * Retrieve purchase link
     * @param driver Webconnexion
     * @param gpu Nvidia FE GPU
     * @return Purchase link if exist null otherwise
     */
    public static String getPurchaseLink(WebDriver driver, GPU gpu) {

        return parsePurchaseLink(getGPUJson(driver, gpu));
    }
}
