package meteor.plugins.changmiscplugins

import com.google.inject.Inject
import meteor.eventbus.Subscribe
import meteor.input.KeyListener
import meteor.input.KeyManager
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import meteor.plugins.api.packets.MousePackets
import net.runelite.api.MenuAction
import net.runelite.api.events.GameTick
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.queries.NPCQuery
import java.awt.event.KeyEvent

@PluginDescriptor(
    name = "Chang Autocast"
)
class ChangAutoCast : Plugin() {
    @Inject
    lateinit var keyManager : KeyManager
    var isActive = false
    var npcIndex : Int? = null
    var spellWidgetID : Int? = null
    val isNPCAlive : Boolean
        get() = NPCQuery().filter {
                it.index == npcIndex && !it.isDead
            }.result(client).isNotEmpty()
    val playerIsWalking : Boolean
        get() = client.localDestinationLocation != null
    val killSwitchListener = object : KeyListener{
        override fun keyTyped(e: KeyEvent?) {
        }

        override fun keyReleased(e: KeyEvent?) {
        }

        override fun keyPressed(e: KeyEvent) {
            if(e.keyCode == KeyEvent.VK_K && e.isControlDown)
                isActive = false
        }
    }
    override fun startup() {
        super.startup()
        keyManager.registerKeyListener(killSwitchListener,this.javaClass)
    }

    override fun shutdown() {
        super.shutdown()
        keyManager.unregisterKeyListener(killSwitchListener)
    }

    @Subscribe
    fun onMenuOptionClicked(event : MenuOptionClicked){
        if(event.menuAction != MenuAction.SPELL_CAST_ON_NPC)
            return
        npcIndex = event.id
        spellWidgetID = client.selectedSpellWidget
        isActive = true
    }
    @Subscribe
    fun onGameTick(event : GameTick){
        if(! isNPCAlive)
            isActive = false
        if(isActive && !playerIsWalking){
            client.selectedSpellWidget = spellWidgetID!!
            MousePackets.queueClickPacket(0,0)
            client.invokeMenuAction("","",npcIndex!!,MenuAction.SPELL_CAST_ON_NPC.id,0,0)
        }
    }
}