package meteor.plugins.changmiscplugins;

import meteor.config.*;

@ConfigGroup("changlavadrags")
public interface ChangLavaDragsConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "doAlching",
            name = "Alch while fighting",
            description = "Living life dangerously"
    )
    default boolean enableAlching()
    {
        return false;
    }
    @ConfigItem(
            position = 0,
            keyName = "lowLeveLMode",
            name = "Low level mode",
            description = "Use blast spells instead"
    )
    default boolean lowLevelMode()
    {
        return false;
    }
}
