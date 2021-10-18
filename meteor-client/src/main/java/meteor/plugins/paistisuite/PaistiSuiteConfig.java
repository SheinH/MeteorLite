package meteor.plugins.paistisuite;

import meteor.config.Config;
import meteor.config.ConfigGroup;
import meteor.config.ConfigItem;

@ConfigGroup(PaistiSuite.CONFIG_GROUP)
public interface PaistiSuiteConfig extends Config {

    String SPIRIT_TREES = "spirit";

    @ConfigItem(
            keyName = "daxApiKey",
            name = "Dax API Key",
            description = "Key to use for dax's webwalker requests",
            position = 110,
            hidden = false
    )
    default String daxApiKey()
    {
        return "sub_DPjXXzL5DeSiPf";
    }

    @ConfigItem(
            keyName = "daxSecretKey",
            name = "Dax API key secret",
            description = "Secret to use for dax's webwalker requests",
            position = 135,
            hidden = false
    )
    default String daxSecretKey()
    {
        return "PUBLIC-KEY";
    }
}
