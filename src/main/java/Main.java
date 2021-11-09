import java.util.*;

import Services.GPU.GPUInfo;
import Services.GPU.GPUName;
import Services.GPU.GPUInfoService;
import Services.NotificationService;

public class Main {

    // Properties of the program
    private static Properties properties;
    private static long lastStartRequest;
    private static long lastEndRequest;

    // Services
    private static NotificationService notificationService;
    private static GPUInfoService gpuService;

    // TODO :
    //  Make desktop notification for windows, linux and macos
    //  Run thread for the daemon only and try catch all exception to secure his execution
    //  Asynchronous call for notification
    //  Get the response from telegram notification message
    //  Limit the number of telegram notification for one second
    //  Optional :
    //      Expose API REST with last data

    public static void main(String[] args) {

        // Loading properties
        properties = PropertyManager.loadProperties(args);

        notificationService = new NotificationService(properties);
        gpuService = new GPUInfoService(properties);

        boolean runDaemon = properties != null;

        try {

            while(runDaemon) {

                Calendar now = Calendar.getInstance();
                System.out.println(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));

                System.out.println(lastEndRequest - lastStartRequest);

                Thread.sleep(Long.parseLong(properties.getProperty("REFRESH_INTERVAL"))-(lastEndRequest - lastStartRequest));

                // Begin of process
                lastStartRequest = System.currentTimeMillis();

                List<GPUName> gpuToFind = Arrays.asList(GPUName._3060ti, GPUName._3080);

                List<GPUInfo> gpuInfos = gpuService.getListInfoGPU(new Locale(properties.getProperty("LOCALE")));

                for (GPUInfo gpuInfo : gpuInfos) {

                    if(gpuInfo.isActive() && gpuToFind.contains(gpuInfo.getGpuName())) {

                        System.out.println(gpuInfo.getGpuName());

                        //sendEmailNotification(gpuInfo.getGpuName(), gpuInfo.getProductUrl());
                        notificationService.sendTelegramNotification(gpuInfo);
                    }
                }

                // End of process
                lastEndRequest = System.currentTimeMillis();
            }

        } catch (InterruptedException e) {

            e.printStackTrace();

        }
    }
}
