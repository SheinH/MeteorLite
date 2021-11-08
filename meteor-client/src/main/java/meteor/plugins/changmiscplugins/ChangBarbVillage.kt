package meteor.plugins.changmiscplugins

import com.google.common.collect.ImmutableSet
import meteor.eventbus.Subscribe
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import meteor.plugins.api.entities.NPCs
import meteor.plugins.api.entities.TileObjects
import net.runelite.api.coords.WorldPoint
import meteor.plugins.api.packets.MousePackets
import meteor.plugins.api.items.Inventory
import meteor.plugins.api.widgets.Dialog
import meteor.ui.overlay.OverlayManager
import meteor.util.Timer
import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import javax.inject.Inject

@PluginDescriptor(name = "Chang barb village", description = "Fishes n stuff", enabledByDefault = false)
class ChangBarbVillage : Plugin() {
    private var dropList: List<Item>? = null
    private var dropListIterator: ListIterator<Item>? = null
    private var currentFishingSpotLoc: WorldPoint? = null
    private var currentFishingSpot: NPC? = null
    private var tickDelay = 0

    enum class WCState {
        FISHING, COOKING, DROPPING
    }

    var state = WCState.FISHING

    private fun getCookAllButton(): Widget? = client.getWidget(270,14)

    override fun startup() {}
    override fun shutdown() {}
    var random = Random()
    private fun nextRandom(mean: Int, deviation: Int, min: Int, max: Int): Int {
        while (true) {
            val gaussian = random.nextGaussian()
            val sample = gaussian * deviation + mean
            val rounded = Math.round(sample).toInt()
            if (rounded >= min && rounded <= max) {
                return rounded
            }
        }
    }

    private fun dropItem(item: Item) {
        MousePackets.queueClickPacket(0, 0)
        //        ItemPackets.itemAction(item,"Drop");
        item.interact("Drop")
    }

    private fun updateDropList() {
        dropList = Inventory.getAll { x: Item -> x.name.contains("logs") }
        if (dropList == null || dropList!!.size == 0) {
            state = WCState.FISHING
            return
        }
        dropListIterator = dropList!!.listIterator()
    }

    private val fishing: Boolean
        private get() = (client.localPlayer!!.interacting != null && client.localPlayer!!.interacting.name!!.contains(
            "Rod Fishing spot"
        )
                && client.localPlayer!!.interacting.graphic != GraphicID.FLYING_FISH && FISHING_ANIMATIONS.contains(
            client.localPlayer!!.animation
        ))
    var timer = Timer()
    var alreadyDropped = false
    var isActive = true

    fun doFishing(){
        if (!fishing || Dialog.isOpen() || currentFishingSpot == null || currentFishingSpot!!.worldLocation != currentFishingSpotLoc) {
            val fishingSpot = NPCs.getNearest("Rod Fishing spot")
            currentFishingSpot = fishingSpot
            currentFishingSpotLoc = fishingSpot.worldLocation
            MousePackets.queueClickPacket(0, 0)
            //                NPCPackets.npcAction(tree, "Lure",0);
            fishingSpot.interact("Lure")
            //                client.invokeMenuAction("","",tree.getIndex(),MenuAction.NPC_FIRST_OPTION.getId(),0,0);
        }
        if (Inventory.isFull()) {
            state = WCState.COOKING
            alreadyDropped = false
            doCooking()
        }
    }
    fun doDropping(){
        val logs = Inventory.getAll(ItemID.TROUT, ItemID.SALMON)
        if (logs.isEmpty()) {
            state = WCState.FISHING
            currentFishingSpot = null
            currentFishingSpotLoc = null
        } else {
            if (!alreadyDropped) {
                logs.forEach(Consumer { item: Item -> dropItem(item) })
                alreadyDropped = true
            }
        }
    }
    var idleTickCount = 0;
    fun doCooking(){
        var player = client.localPlayer
        WidgetInfo.CHATBOX_TAB_ALL
        if(player == null)
            return
        if(!player.isIdle) {
            idleTickCount = 0;
            return
        }
        idleTickCount++
        val button = getCookAllButton()
        if(button != null){
            button.interact(0)
            return
        }
        var fish = Inventory.getFirst( ItemID.RAW_SALMON, ItemID.RAW_TROUT )
        if(fish != null){
            if(idleTickCount > 2){
                val fire = TileObjects.getNearest("Fire")
                fish.useOn(fire)
            }
        }
        else {
            state = WCState.DROPPING
            return
        }
    }
    @Subscribe
    fun onGameTick(event: GameTick?) {
        if (timer.minutesFromStart > 220) toggle()
        if (tickDelay > 0) {
            tickDelay--
            return
        }
        when (state){
            WCState.FISHING -> doFishing()
            WCState.COOKING -> doCooking()
            WCState.DROPPING -> doDropping()
        }
    }

    companion object {
        val fishingSpotFilter = Predicate { x: NPC -> x.name == "Rod Fishing spot" }
        private val FISHING_ANIMATIONS: Set<Int> = ImmutableSet.of(
            AnimationID.FISHING_BARBTAIL_HARPOON,
            AnimationID.FISHING_BAREHAND,
            AnimationID.FISHING_BAREHAND_CAUGHT_SHARK_1,
            AnimationID.FISHING_BAREHAND_CAUGHT_SHARK_2,
            AnimationID.FISHING_BAREHAND_CAUGHT_SWORDFISH_1,
            AnimationID.FISHING_BAREHAND_CAUGHT_SWORDFISH_2,
            AnimationID.FISHING_BAREHAND_CAUGHT_TUNA_1,
            AnimationID.FISHING_BAREHAND_CAUGHT_TUNA_2,
            AnimationID.FISHING_BAREHAND_WINDUP_1,
            AnimationID.FISHING_BAREHAND_WINDUP_2,
            AnimationID.FISHING_BIG_NET,
            AnimationID.FISHING_CAGE,
            AnimationID.FISHING_CRYSTAL_HARPOON,
            AnimationID.FISHING_DRAGON_HARPOON,
            AnimationID.FISHING_DRAGON_HARPOON_OR,
            AnimationID.FISHING_HARPOON,
            AnimationID.FISHING_INFERNAL_HARPOON,
            AnimationID.FISHING_TRAILBLAZER_HARPOON,
            AnimationID.FISHING_KARAMBWAN,
            AnimationID.FISHING_NET,
            AnimationID.FISHING_OILY_ROD,
            AnimationID.FISHING_POLE_CAST,
            AnimationID.FISHING_PEARL_ROD,
            AnimationID.FISHING_PEARL_FLY_ROD,
            AnimationID.FISHING_PEARL_BARBARIAN_ROD,
            AnimationID.FISHING_PEARL_ROD_2,
            AnimationID.FISHING_PEARL_FLY_ROD_2,
            AnimationID.FISHING_PEARL_BARBARIAN_ROD_2,
            AnimationID.FISHING_PEARL_OILY_ROD
        )
        private const val FISHING_SPOT = "Rod fishing spot"
    }
}