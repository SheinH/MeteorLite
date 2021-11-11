package meteor.plugins.changmiscplugins

import com.google.inject.Inject
import meteor.eventbus.Subscribe
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import dev.hoot.api.entities.NPCs
import dev.hoot.api.game.GameThread
import dev.hoot.api.items.Inventory
import dev.hoot.api.packets.MousePackets
import net.runelite.api.Item
import net.runelite.api.ItemID
import net.runelite.api.MenuAction
import net.runelite.api.NPC
import net.runelite.api.events.GameTick
import org.apache.commons.lang3.time.StopWatch
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.asJavaRandom

@PluginDescriptor(
    name = "Chang's Barbarian Fishing",
    disabledOnStartup = true
)
class ChangBarbFish : Plugin() {
    @Inject
    lateinit var executor: ScheduledExecutorService
    val player
        get() = client.localPlayer

    val fishingSpotFilter: Predicate<NPC> =
        Predicate { x -> (x.name?.lowercase()?.contains("fishing spot") ?: false) && x.hasAction("Use-rod") }
    val adjacentFishingSpot: NPC?
        get() {
            val spots = NPCs.getAll(fishingSpotFilter)
            val playerLoc = player?.worldLocation ?: return null
            for (s in spots) {
                val dx = abs(playerLoc.x - s.worldLocation.x)
                val dy = abs(playerLoc.y - s.worldLocation.y)
                if (dx + dy == 1)
                    return s
            }
            return null
        }
    val nearestFishingSpot: NPC?
        get() = NPCs.getNearest(fishingSpotFilter)
    val fishInInventory: Item?
        get() = Inventory.getFirst(ItemID.LEAPING_STURGEON, ItemID.LEAPING_TROUT, ItemID.LEAPING_SALMON)

    override fun startup() {
        runtime.reset()
    }

    override fun shutdown() {
    }

    fun handleFishingSpotMoved() {
        val nearestFishingSpot = nearestFishingSpot
        if (player?.interacting == nearestFishingSpot)
            return
        if (nearestFishingSpot == null) {
            return
        } else {
            nearestFishingSpot.interact(0)
        }
    }

    var fishNextTick = false
    var tickCtr = 0L
    var tickCycle = 0
    var isFourTick = false;
    fun logout() {
        val logoutButton = client.getWidget(182, 8)
        val logoutDoorButton = client.getWidget(69, 23)
        var param1 = -1
        if (logoutButton != null) {
            param1 = logoutButton.id
        } else if (logoutDoorButton != null) {
            param1 = logoutDoorButton.id
        }
        if (param1 == -1) {
            return
        }
        val p1 = param1
        MousePackets.queueClickPacket(0, 0)
        GameThread.invoke {
            client.invokeMenuAction(
                "Logout",
                "",
                1,
                MenuAction.CC_OP.id,
                -1,
                p1
            )
        }
    }

    var runtime = StopWatch()

    @Subscribe
    fun onGameTick(event: GameTick?) {
        if (runtime.getTime(TimeUnit.MINUTES) >= 210 || !Inventory.contains(ItemID.FEATHER)) {
            logout()
            toggle()
        }
        if (handleBreaks())
            return
        tickCtr++
        tickCycle++
        val fishingSpot = adjacentFishingSpot
        if (fishingSpot == null) {
            handleFishingSpotMoved()
            return
        }
        if ((tickCycle > 3 || (!isFourTick && tickCycle == 3))) {
            tickFish(fishingSpot)
            tickCycle = 0
            isFourTick = Random.nextInt(50) == 0
        }
    }


    var breakTimer = StopWatch()
    var random = Random.asJavaRandom()
    var minutesUntilBreak = gaussianRandom(30, 5, 10)
    var timeToSleep = -1
    var isSleeping = false
    private fun gaussianRandom(mean: Int, deviation: Int, range: Int): Int {
        var rand = -1
        do {
            rand = (random.nextGaussian() * deviation + mean).roundToInt()
        } while (abs(rand - mean) > range)
        return rand;
    }

    private fun handleBreaks(): Boolean {
        if (isSleeping) {
            if (breakTimer.getTime(TimeUnit.MINUTES) > timeToSleep) {
                isSleeping = false
                breakTimer.reset()
                return false
            } else return true
        } else {
            if (breakTimer.getTime(TimeUnit.MINUTES) >= minutesUntilBreak) {
                timeToSleep = gaussianRandom(6, 3, 4)
                isSleeping = true
                minutesUntilBreak = gaussianRandom(30, 5, 10)
                breakTimer.reset()
                return true
            } else return false
        }
    }

    private fun tickFish(fishingSpot: NPC) {
        val marrentill = Inventory.getFirst(ItemID.MARRENTILL)
        val swampTar = Inventory.getFirst(ItemID.SWAMP_TAR)
        val knife = Inventory.getFirst(ItemID.KNIFE)
        val fish = fishInInventory
//        if (fish != null) {
//            knife.useOn(fish)
//            fishingSpot.interact(0)
//        } else {
//            swampTar.useOn(marrentill)
//            Inventory.getFirst(ItemID.ROE, ItemID.CAVIAR)?.interact("Drop")
//            fishingSpot.interact(0)
//        }
//        executor.schedule({
//            Inventory.getFirst(ItemID.ROE, ItemID.CAVIAR)?.interact("Eat")
//            var item = Inventory.getFirst(ItemID.ROE, ItemID.CAVIAR)
//            if(item != null) {
//                item.interact("Eat")
//                Time.sleep(100)
//            }
//        }, 100, TimeUnit.MILLISECONDS)
        swampTar.useOn(marrentill)
        fish?.interact("Drop")
        fishingSpot.interact(0)
    }

}