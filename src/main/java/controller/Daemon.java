package controller;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import controller.service.GPUInfoService;
import controller.service.PropertyManager;
import model.GPUInfo;
import model.GPUName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.utils.StringUtils;

import controller.service.notification.NotificationFactory;
import controller.service.notification.NotificationService;

public class Daemon implements Runnable {

    // Constants
    public static final String REFRESH_INTERVAL = "REFRESH_INTERVAL";
    public static final String LOCALES = "LOCALES";
    public static final String NOTIFICATION_CHANNELS = "NOTIFICATION_CHANNELS";
    public static final String SEPARATOR = ",";
    public static final String GPUS = "GPUS";
    public static final String TEST_NOTIFICATION = "TEST_NOTIFICATION";

    // Properties of the program
    private static long lastStartRequest;
    private static long lastEndRequest;

    // Logger
    public static final Logger logger = LogManager.getLogger(Daemon.class);

    @Override
    public void run() {

        logger.info("SimpleGPUAlert");
        logger.info("Searching GPUs : ".concat(PropertyManager.properties.getProperty(GPUS)));
        logger.info("In : ".concat(PropertyManager.properties.getProperty(LOCALES)));

        // Services
        GPUInfoService gpuService = new GPUInfoService();
        List<NotificationService> notificationServices = NotificationFactory.getNotificationService();

        // Get the list of GPUs
        List<GPUName> gpuToFind = Arrays.stream(PropertyManager.properties.getProperty(GPUS).split(SEPARATOR))
                .map(GPUName::StringToGPU)
                .toList();

        // Get the locales
        String[] locales = PropertyManager.properties.getProperty(LOCALES).split(SEPARATOR);

        boolean testNotification = Boolean.parseBoolean(PropertyManager.properties.getProperty(TEST_NOTIFICATION));

        boolean runDaemon = PropertyManager.properties != null;

        try {

            while(runDaemon) {

                Thread.sleep(Long.parseLong(PropertyManager.properties.getProperty(REFRESH_INTERVAL))-(lastEndRequest - lastStartRequest));

                logger.debug((lastEndRequest - lastStartRequest) + "ms");

                // Begin of process
                lastStartRequest = System.currentTimeMillis();

                // Loop on the locales
                for (String locale : locales) {

                    if (StringUtils.isNotBlank(locale)) {

                        List<GPUInfo> gpuInfos = gpuService.getListInfoGPU(new Locale(locale));

                        // Loop on the notification services
                        for (NotificationService notificationService : notificationServices) {

                            // Loop on the infos of the GPUs
                            for (GPUInfo gpuInfo : gpuInfos) {

                                if ((gpuInfo.isActive() || testNotification) && gpuToFind.contains(gpuInfo.getGpuName())) {

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
}
