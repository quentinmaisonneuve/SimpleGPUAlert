package controller.service.notification;

import controller.service.PropertyManager;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import model.GPUInfo;

public class DiscordNotificationService implements NotificationService {

    // Constant
    public static final String DISCORD_TOKEN = "DISCORD_TOKEN";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        final DiscordClient client = DiscordClient.create(PropertyManager.getProperty(DISCORD_TOKEN));

        final GatewayDiscordClient gateway = client.login().block();


        client.getChannelById(Snowflake.of("ChannelId"))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.createMessage("Your content here"))
                .subscribe();
        /*gateway.getChan

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if ("!ping".equals(message.getContent())) {
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("Pong!").block();
            }
        });*/

        gateway.onDisconnect().block();
    }
}
