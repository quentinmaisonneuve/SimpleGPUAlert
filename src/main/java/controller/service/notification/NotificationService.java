package controller.service.notification;

import model.GPUInfo;

/**
 * Skeleton of notification services
 */
public interface NotificationService {

    /**
     * Send notification on the channel implemented
     * @param gpuInfo Nvidia GPU informations
     */
    void sendNotification(GPUInfo gpuInfo);
}
