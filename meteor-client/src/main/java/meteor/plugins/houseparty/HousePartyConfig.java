package meteor.plugins.houseparty;

import meteor.config.*;

@ConfigGroup("housepartyplugin")
public interface HousePartyConfig extends Config {
    @Icon(canToggle = false)
    @ConfigItem(
            keyName = "startStop",
            name = "Start",
            description = "Starts the TP process",
            position = 0
    )
    default Button startStop() {
        return new Button();
    }
}
