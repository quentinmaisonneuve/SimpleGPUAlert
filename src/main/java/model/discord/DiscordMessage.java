package model.discord;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscordMessage {

    private String content;
    private boolean tts;
    private List<Embed> embeds;
}
