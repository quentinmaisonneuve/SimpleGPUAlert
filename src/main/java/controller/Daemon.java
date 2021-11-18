package controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import controller.service.GPUInfoService;
import controller.service.PropertyManager;
import data.GPUInfo;
import data.GPUName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import controller.service.notification.NotificationFactory;
import controller.service.notification.NotificationService;

public class Daemon implements Runnable {

    // Properties of the program
    private static long lastStartRequest;
    private static long lastEndRequest;

    // Logger
    public static Logger logger = LogManager.getLogger(Daemon.class);

    @Override
    public void run() {

        // Services
        GPUInfoService gpuService = new GPUInfoService();
        List<NotificationService> notificationServices = NotificationFactory.getNotificationService();

        boolean runDaemon = PropertyManager.properties != null;

        try {

            while(runDaemon) {

                logger.debug((lastEndRequest - lastStartRequest) + "ms");

                Thread.sleep(Long.parseLong(PropertyManager.properties.getProperty("REFRESH_INTERVAL"))-(lastEndRequest - lastStartRequest));

                // Begin of process
                lastStartRequest = System.currentTimeMillis();

                List<GPUName> gpuToFind = Arrays.asList(GPUName._3060ti, GPUName._3080);

                List<GPUInfo> gpuInfos = gpuService.getListInfoGPU(new Locale(PropertyManager.properties.getProperty("LOCALE")));

                for (NotificationService notificationService : notificationServices) {

                    for (GPUInfo gpuInfo : gpuInfos) {

                        logger.info(gpuInfo.getGpuName());

                        if(/*gpuInfo.isActive() &&*/ gpuToFind.contains(gpuInfo.getGpuName())) {

                            new Thread(() -> notificationService.sendNotification(gpuInfo)).start();
                        }
                    }
                }

                // End of process
                lastEndRequest = System.currentTimeMillis();
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
