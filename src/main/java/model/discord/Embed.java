package model.discord;

import lombok.Getter;
import lombok.Setter;

/**
 * Discord embed object
 */
@Getter
@Setter
public class Embed {

    /** Title */
    private String title;

    /** Description */
    private String description;

    /** URL if type is link */
    private String url;
}
