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
import controller.service.notification.NotificationFactory;
import controller.service.notification.NotificationService;
import model.GPUInfo;
import model.GPUName;

public class Daemon implements Runnable {

    // Constants
    public static final String REFRESH_INTERVAL = "REFRESH_INTERVAL";
    public static final String LOCALES = "LOCALES";
    public static final String NOTIFICATION_CHANNELS = "NOTIFICATION_CHANNELS";
    public static final String SEPARATOR = ",";
    public static final String GPUS = "GPUS";
    public static final String TIMEOUT_NOTIFICATION = "TIMEOUT_NOTIFICATION";
    public static final String TEST_NOTIFICATION = "TEST_NOTIFICATION";
    public static final String LOG_LEVEL = "LOG_LEVEL";

    // Properties of the program
    private static long lastStartRequest;
    private static long lastEndRequest;

    // Logger
    public static final Logger logger = LogManager.getLogger(Daemon.class);

    @Override
    public void run() {

        setLogLevel();

        logger.info("SimpleGPUAlert");
        logger.info("Searching GPUs : ".concat(PropertyManager.getProperty(GPUS)));
        logger.info("In : ".concat(PropertyManager.getProperty(LOCALES)));

        // Services
        GPUInfoService gpuService = new GPUInfoService();
        List<NotificationService> notificationServices = NotificationFactory.getNotificationService();

        // Get the list of GPUs
        List<GPUName> gpuToFind = Arrays.stream(PropertyManager.getProperty(GPUS).split(SEPARATOR))
                .map(GPUName::StringToGPU)
                .toList();

        // Get the locales
        String[] locales = PropertyManager.getProperty(LOCALES).split(SEPARATOR);

        // Last notification
        Map<String, LocalDateTime> lastNotification = new HashMap<>();

        boolean testNotification = Boolean.parseBoolean(PropertyManager.getProperty(TEST_NOTIFICATION));

        boolean runDaemon = PropertyManager.isLoaded();

        try {

            while(runDaemon) {

                long timeToWait = Long.parseLong(PropertyManager.getProperty(REFRESH_INTERVAL))-(lastEndRequest - lastStartRequest);

                if (timeToWait > 0) {

                    Thread.sleep(timeToWait);
                }

                logger.debug((lastEndRequest - lastStartRequest) + "ms");

                // Begin of process
                lastStartRequest = System.currentTimeMillis();

                // Loop on the locales
                for (String locale : locales) {

                    if (StringUtils.isNotBlank(locale)) {

                        List<GPUInfo> gpuInfos = gpuService.getListInfoGPU(new Locale(locale.trim()));

                        // Loop on the notification services
                        for (NotificationService notificationService : notificationServices) {

                            // Loop on the infos of the GPUs
                            for (GPUInfo gpuInfo : gpuInfos) {

                                String keyLastnotification = locale.concat(":")
                                        .concat(notificationService.getClass().getName())
                                        .concat(":")
                                        .concat(gpuInfo.getGpuName().toString());

                                if (((gpuInfo.isActive() && isTimeoutOver(lastNotification.get(keyLastnotification))) || testNotification)
                                        && gpuToFind.contains(gpuInfo.getGpuName())) {

                                    lastNotification.put(keyLastnotification, LocalDateTime.now());
                                    new Thread(() -> notificationService.sendNotification(gpuInfo)).start();
                                }
                            }
                        }
                    }
                }

                // End of process
                lastEndRequest = System.currentTimeMillis();
            }

        } catch (Exception e) {

            Daemon.logger.error(e);
        }
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
     * Return true if the timeout required is reach
     * @param lastNotification Last notification from a channel and a type of GPU
     * @return True if null or the time between two notifications has been reached false otherwise
     */
    private boolean isTimeoutOver(LocalDateTime lastNotification) {

        return lastNotification == null || Duration.between(lastNotification, LocalDateTime.now()).toMinutes() >
                    Long.parseLong(PropertyManager.getProperty(TIMEOUT_NOTIFICATION));
    }
}
