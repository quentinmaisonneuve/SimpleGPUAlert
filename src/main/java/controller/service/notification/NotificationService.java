package controller.service.notification;

import data.GPUInfo;

/**
 * Skeleton of notification services
 */
public interface NotificationService {

    String LOCALE = "LOCALE";

    /**
     * Send notification on the channel implemented
     * @param gpuInfo Nvidia GPU informations
     */
    void sendNotification(GPUInfo gpuInfo);
}
