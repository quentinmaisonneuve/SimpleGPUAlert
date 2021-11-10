package service.notification;

import com.jcabi.aspects.Async;

import data.GPUInfo;

/**
 * Skeleton of notification services
 */
public interface NotificationService {

    /**
     * Send notification on the channel implemented
     * @param gpuInfo Nvidia GPU informations
     */
    @Async
    void sendNotification(GPUInfo gpuInfo);
}
