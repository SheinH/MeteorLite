package meteor.plugins.chaosaltar;

import meteor.config.*;

@ConfigGroup("chaosaltar")
public interface ChaosAltarConfig extends Config {
    @Icon(canToggle = true)
    @ConfigItem(
            keyName = "startStop",
            name = "Start/Stop",
            description = "Starts the plugin or stops it",
            position = 0
    )
    default Button startStop(){
        return new Button();
    }
}
