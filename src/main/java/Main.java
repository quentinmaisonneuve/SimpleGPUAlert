import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Main {

    private static long lastStartRequest;
    private static long lastEndRequest;

    // Properties of the program
    public static Properties properties;

    // TODO :
    //  Run thread for the daemon only and try catch all exception to secure his execution
    //  Service class for each functionality
    //  Asynchronous call for notification
    //  Get the response from telegram notification message
    //  .
    //  Optional :
    //      Expose API REST with last data

    public static void main(String[] args) {

        properties = PropertyManager.loadPropertiesFromFile("src/main/resources/configuration.properties");

        try {

            while(true) {

                Calendar now = Calendar.getInstance();
                System.out.println(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));

                System.out.println(lastEndRequest - lastStartRequest);

                Thread.sleep(Long.parseLong(properties.getProperty("REFRESH_INTERVAL"))-(lastEndRequest - lastStartRequest));

                // Begin of process
                lastStartRequest = System.currentTimeMillis();

                List<GPUName> gpuToFind = Arrays.asList(GPUName._3060ti, GPUName._3080);

                List<GPUInfo> gpuInfos = getListInfoGPU(new Locale(properties.getProperty("LOCALE")));

                for (GPUInfo gpuInfo : gpuInfos) {

                    if(gpuInfo.isActive() && gpuToFind.contains(gpuInfo.getGpuName())) {

                        System.out.println(gpuInfo.getGpuName());

                        //sendEmailNotification(gpuInfo.getGpuName(), gpuInfo.getProductUrl());
                        sendTelegramNotification(gpuInfo, new Locale(properties.getProperty("LOCALE")));
                    }
                }

                // End of process
                lastEndRequest = System.currentTimeMillis();
            }

        } catch (InterruptedException e) {

            e.printStackTrace();

        }
    }

    private static List<GPUInfo> getListInfoGPU(Locale locale) throws InterruptedException {

        List<GPUInfo> gpuInfos = new ArrayList<>();

        Map<String, GPUName> GPUNameToId = new HashMap<>();

        for (GPUName gpuName : GPUName.values()) {

            GPUNameToId.put(getClassDivGPU(gpuName), gpuName);
        }

        JSONArray lineProducts = readJsonFromUrl(String.format(properties.getProperty("NVIDIA_API_LINK"),
                locale.toString().toUpperCase(),
                locale.toString().toUpperCase()))
                .getJSONArray("listMap");

        for (Object o : lineProducts) {

            JSONObject jsonLineItem = (JSONObject) o;

            if (jsonLineItem.getString("fe_sku").startsWith("NVGFT")) {

                GPUInfo gpuInfo = new GPUInfo();

                gpuInfo.setIdGPU(jsonLineItem.getString("fe_sku").replace("_".concat(locale.toString().toUpperCase()), ""));
                gpuInfo.setGpuName(GPUNameToId.get(gpuInfo.getIdGPU()));
                gpuInfo.setActive(Boolean.parseBoolean(jsonLineItem.getString("is_active")));
                gpuInfo.setProductUrl(jsonLineItem.getString("product_url"));
                gpuInfo.setPrice(Double.parseDouble(jsonLineItem.getString("price")));

                gpuInfos.add(gpuInfo);
            }
        }

        return gpuInfos;
    }

    public static JSONObject readJsonFromUrl(String url)  {

        JSONObject json = null;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = null;

        try {

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
        }

        if (response != null) {

            json = new JSONObject(response.body());
        }

        return json;
    }

    /**
     * Return the class name of the div who contains supply information of the gpu by giving the GPU name
     * @param gpu Nvidia FE GPU
     * @return Div class of supply information, example : NVGFT060T if for an 3060ti
     */
    public static String getClassDivGPU(GPUName gpu) {

        StringBuilder result = new StringBuilder("NVGFT");

        result.append(gpu.toString(), 1, 4);

        if(gpu.toString().contains("ti")) {

            result.append('T');
        }

        return result.toString();
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
    private static void sendEmailNotification(GPUName gpu, String purchaseLink) {

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

            e.printStackTrace();
        }
    }

    public static void sendTelegramNotification(GPUInfo gpuInfo, Locale locale) {

        String urlString = properties.getProperty("TELEGRAM_API_LINK");
        String message = "Nvidia " + locale.toString().toUpperCase() +
                "%0AFE Nvidia GeForce RTX " + gpuInfo.getGpuName() +
                "%0A" + gpuInfo.getProductUrl();

        urlString = String.format(urlString, properties.getProperty("API_TOKEN"), properties.getProperty("CHAT_ID"), message);

        try {

            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
