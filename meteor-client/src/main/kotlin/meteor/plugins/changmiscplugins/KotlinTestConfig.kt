package meteor.plugins.changmiscplugins

import meteor.config.*

@ConfigGroup("Kotlintest")
interface KotlinTestConfig : Config {
    @Icon(canToggle = true)
    @ConfigItem(keyName = "startStop", name = "Start/Stop", description = "Starts the plugin or stops it", position = 0)
    fun startStop(): Button? {
        return Button()
    }
}
