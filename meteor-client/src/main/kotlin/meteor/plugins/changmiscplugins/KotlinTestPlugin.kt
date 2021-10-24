package meteor.plugins.changmiscplugins

import com.google.inject.Inject
import meteor.chat.ChatMessageManager
import meteor.eventbus.Subscribe
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import meteor.plugins.api.entities.NPCs
import meteor.plugins.api.kotlin.KotlinUtils
import net.runelite.api.events.GameTick

@PluginDescriptor(
    name = "Kotlin Test",
    description = "Kotlin"
)
class KotlinTestPlugin : Plugin() {
    @Inject
    lateinit var chatMessageManager : ChatMessageManager

    @Subscribe
    fun onGameTick(event: GameTick) {
        sendChatMessage("Testing Kotlin!")
        NPCs.getNearest("Banker")?.interact("Bank")
        toggle()
    }
    private fun sendChatMessage(message: String) {
        KotlinUtils.sendChatMessage(chatMessageManager,message)
    }
}