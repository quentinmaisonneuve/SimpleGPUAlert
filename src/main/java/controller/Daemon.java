package controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.maven.shared.utils.StringUtils;

import controller.service.GPUInfoService;
import controller.service.PropertyManager;
import controller.service.notification.NotificationManager;
import model.GPUInfo;
import model.GPUName;
import model.NotificationChannel;

public class Daemon implements Runnable {

    // Constants
    public static final String REFRESH_INTERVAL = "REFRESH_INTERVAL";
    public static final String LOCALES = "LOCALES";
    public static final String SEPARATOR = ",";
    public static final String GPU = "GPU";
    public static final String TIMEOUT_NOTIFICATION_DROP = "TIMEOUT_NOTIFICATION_DROP";
    public static final String TEST_NOTIFICATION = "TEST_NOTIFICATION";
    public static final String LOG_LEVEL = "LOG_LEVEL";

    // Logger
    public static final Logger logger = LogManager.getLogger(Daemon.class);

    // Properties
    private long timeoutNotification;
    private String[] locales;
    private List<GPUName> gpuToFind;
    private long refreshInterval;
    private boolean testNotification;

    // List of information of last drop
    private final Map<String, GPUInfo> lastDrops = new HashMap<>();
    private final Map<String, LocalDateTime> lastNotifications = new HashMap<>();

    @Override
    public void run() {

        setLogLevel();

        logger.info("SimpleGPUAlert");
        logger.info("Searching GPUs : ".concat(PropertyManager.getProperty(GPU)));
        logger.info("In : ".concat(PropertyManager.getProperty(LOCALES)));

        // Service
        // GPU service
        GPUInfoService gpuService = new GPUInfoService();

        // Initialize properties
        initProperties();

        try {

            while(PropertyManager.isLoaded()) {

                // Begin of process
                // Request timer variables
                long lastStartRequest = System.currentTimeMillis();

                // Loop on the notification services
                for (NotificationChannel notificationChannel : NotificationManager.getListNotificationChannel()) {

                    // Loop on the locales
                    for (String locale : locales) {

                        if (StringUtils.isNotBlank(locale)) {

                            List<GPUInfo> gpuInfos = gpuService.getListInfoGPU(new Locale(locale.trim()));

                            // Loop on the infos of the GPUs
                            for (GPUInfo gpuInfo : gpuInfos) {

                                String keyLastnotification = locale.concat(":")
                                        .concat(notificationChannel.toString())
                                        .concat(":")
                                        .concat(gpuInfo.getGpuName().toString());

                                if (gpuToFind.contains(gpuInfo.getGpuName()) &&
                                        (testNotification || (gpuInfo.isActive() && sendNotification(gpuInfo, keyLastnotification)))) {

                                    lastDrops.put(keyLastnotification, gpuInfo);
                                    lastNotifications.put(keyLastnotification, LocalDateTime.now());
                                    NotificationManager.sendNotification(notificationChannel, gpuInfo);
                                }
                            }
                        }
                    }
                }

                // End of process
                long lastEndRequest = System.currentTimeMillis();

                logger.debug("Loop duration : ".concat(String.valueOf(lastEndRequest - lastStartRequest).concat("ms")));

                // Sleep before requesting again
                long timeToWait = refreshInterval - (lastEndRequest - lastStartRequest);

                if (timeToWait > 0) {

                    Thread.sleep(timeToWait);
                }
            }

        } catch (Exception e) {

            Daemon.logger.error(e);
        }
    }

    /**
     * Initialize properties
     */
    private void initProperties() {

        // Time between each loop
        refreshInterval = Long.parseLong(PropertyManager.getProperty(REFRESH_INTERVAL));

        // Get the list of GPUs
        gpuToFind = Arrays.stream(PropertyManager.getProperty(GPU).split(SEPARATOR))
                .map(GPUName::StringToGPU)
                .toList();

        // Get the locales
        locales = PropertyManager.getProperty(LOCALES).split(SEPARATOR);

        // Timeout notification
        timeoutNotification = Long.parseLong(PropertyManager.getProperty(TIMEOUT_NOTIFICATION_DROP));

        // Test notification
        testNotification = Boolean.parseBoolean(PropertyManager.getProperty(TEST_NOTIFICATION));
    }

    /**
     * Set the log level from properties
     */
    private void setLogLevel() {

        String loglLevel = PropertyManager.getProperty(LOG_LEVEL);

        if (StringUtils.isNotBlank(loglLevel)) {

            Configurator.setLevel(logger.getName(), Level.getLevel(loglLevel));
        }
    }


    /**
     * True if it's a new drop or the timeout is over false otherwise
     * @param gpuInfo Last info of the GPU
     * @param keyLastnotification key (locale, GPU, notification channel)
     * @return true if it's ok to send a notification
     */
    private boolean sendNotification(GPUInfo gpuInfo, String keyLastnotification) {

        boolean sendNotification;

        // If this is a new URL ie a new drop, just send the notification
        if (lastDrops.get(keyLastnotification) == null || !lastDrops.get(keyLastnotification).getProductUrl().equals(gpuInfo.getProductUrl())) {

            sendNotification = true;

        // Otherwise send only if the timeout to send a new notification for the same drop is over
        } else {

            sendNotification = isTimeoutOver(lastNotifications.get(keyLastnotification));
        }

        return sendNotification;
    }

    /**
     * Return true if the timeout required is reach
     * @param lastNotification Last notification from a channel and a type of GPU
     * @return True if null or the time between two notifications has been reached false otherwise
     */
    private boolean isTimeoutOver(LocalDateTime lastNotification) {

        return timeoutNotification > 0 &&
                Duration.between(lastNotification, LocalDateTime.now()).toSeconds() > (timeoutNotification * 60) - (refreshInterval / 1000);
    }
}
