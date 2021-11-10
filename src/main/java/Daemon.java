import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import data.GPUInfo;
import data.GPUName;
import service.GPUInfoService;
import service.PropertyManager;
import service.notification.NotificationFactory;
import service.notification.NotificationService;

public class Daemon implements Runnable {

    // Properties of the program
    private static long lastStartRequest;
    private static long lastEndRequest;

    // Services
    private static GPUInfoService gpuService;

    @Override
    public void run() {

        gpuService = new GPUInfoService();
        List<NotificationService> notificationServices = NotificationFactory.getNotificationService();

        boolean runDaemon = PropertyManager.properties != null;

        try {

            while(runDaemon) {

                Calendar now = Calendar.getInstance();
                System.out.println(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));

                System.out.println(lastEndRequest - lastStartRequest);

                Thread.sleep(Long.parseLong(PropertyManager.properties.getProperty("REFRESH_INTERVAL"))-(lastEndRequest - lastStartRequest));

                // Begin of process
                lastStartRequest = System.currentTimeMillis();

                List<GPUName> gpuToFind = Arrays.asList(GPUName._3060ti, GPUName._3080);

                List<GPUInfo> gpuInfos = gpuService.getListInfoGPU(new Locale(PropertyManager.properties.getProperty("LOCALE")));

                for (GPUInfo gpuInfo : gpuInfos) {

                    if(gpuInfo.isActive() && gpuToFind.contains(gpuInfo.getGpuName())) {

                        System.out.println(gpuInfo.getGpuName());
                        for (NotificationService notificationService : notificationServices) {

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
