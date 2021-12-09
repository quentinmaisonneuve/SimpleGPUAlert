package controller.service.notification;

import controller.Daemon;
import controller.service.PropertyManager;
import model.GPUInfo;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterNotificationService implements NotificationService {

    // Constants
    public static final String CONSUMER_KEY = "CONSUMER_KEY";
    public static final String CONSUMER_SECRET = "CONSUMER_SECRET";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";
    public static final String TWEET_TEMPLATE = "TWEET_TEMPLATE";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(PropertyManager.getProperty(CONSUMER_KEY))
                .setOAuthConsumerSecret(PropertyManager.getProperty(CONSUMER_SECRET))
                .setOAuthAccessToken(PropertyManager.getProperty(ACCESS_TOKEN))
                .setOAuthAccessTokenSecret(PropertyManager.getProperty(ACCESS_TOKEN_SECRET));
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {

            twitter.updateStatus(NotificationManager.formatString(PropertyManager.getProperty(TWEET_TEMPLATE), gpuInfo, false));

        } catch (TwitterException e) {

            Daemon.logger.error(e);
        }
    }
}
