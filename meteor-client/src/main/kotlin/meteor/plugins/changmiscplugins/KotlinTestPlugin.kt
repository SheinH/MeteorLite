package meteor.plugins.changmiscplugins

import com.google.inject.Inject
import com.google.inject.Provides
import meteor.chat.ChatMessageManager
import meteor.config.ConfigManager
import meteor.eventbus.Subscribe
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import dev.hoot.api.entities.NPCs
import meteor.ui.FontManager
import meteor.ui.overlay.OverlayManager
import net.runelite.api.events.GameTick

@PluginDescriptor(
    name = "Kotlin Test",
    description = "Kotlin"
)
class KotlinTestPlugin : Plugin() {
    @Inject
    lateinit var chatMessageManager : ChatMessageManager
    @Inject
    lateinit var config : KotlinTestConfig
    @Inject
    lateinit var fontManager : FontManager
    @Inject
    lateinit var overlayManager : OverlayManager


    @Provides
    override fun getConfig(configManager : ConfigManager): KotlinTestConfig {
        return configManager.getConfig(KotlinTestConfig::class.java)
    }
    @Subscribe
    fun onGameTick(event: GameTick) {
        sendChatMessage("Testing Kotlin!")
        NPCs.getNearest("Banker")?.interact("Bank")
        toggle()
    }
    fun sendChatMessage(message: String) {
    }
}