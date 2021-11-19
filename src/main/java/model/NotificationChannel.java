package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.shared.utils.StringUtils;

public enum NotificationChannel {

    TELEGRAM, MAIL; // TODO : DISCORD, TWITTER, FACEBOOK, MESSENGER, DESKTOP;

    /**
     * Convert a string name of notication channel to an enum NotificationChannel
     * @param notificationChannels Name of the notication channel
     * @return Enum of corresponding notication channel
     */
    public static List<NotificationChannel> StringToNotificationChannel(String notificationChannels) {

        List<NotificationChannel> listNotificationChannel = new ArrayList<>();

        if (StringUtils.isNotBlank(notificationChannels)) {

            for (String sNC : notificationChannels.split(",")) {

                for (NotificationChannel nC : NotificationChannel.values()) {

                    if (sNC.contains(nC.toString())) {

                        listNotificationChannel.add(nC);
                    }
                }
            }
        }

        return listNotificationChannel;
    }
}
