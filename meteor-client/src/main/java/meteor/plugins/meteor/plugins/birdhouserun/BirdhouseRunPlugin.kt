package meteor.plugins.meteor.plugins.birdhouserun

import meteor.eventbus.Subscribe
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import dev.hoot.api.game.Game
import dev.hoot.api.game.Skills
import dev.hoot.api.items.Bank
import dev.hoot.api.items.Inventory
import net.runelite.api.*
import net.runelite.api.events.GameTick
import java.util.function.Predicate

@PluginDescriptor(
    name = "Birdhouse Run",
    description = "Does birdhouse runs for you!"

)
class BirdhouseRunPlugin : Plugin() {

    internal enum class BirdhouseType(
        val logID: Int,
        val birdhouseID: Int,
        val hunterLevelReq: Int,
        val craftingLevelReq: Int
    ) {
        REGULAR(ItemID.LOGS, ItemID.BIRD_HOUSE, 5, 5), OAK(ItemID.OAK_LOGS, ItemID.OAK_BIRD_HOUSE, 15, 14), WILLOW(
            ItemID.WILLOW_LOGS,
            ItemID.WILLOW_BIRD_HOUSE,
            25,
            24
        ),
        TEAK(ItemID.TEAK_LOGS, ItemID.TEAK_BIRD_HOUSE, 35, 34), MAPLE(
            ItemID.MAPLE_LOGS,
            ItemID.MAPLE_BIRD_HOUSE,
            45,
            44
        ),
        MAHAOGANY(ItemID.MAHOGANY_LOGS, ItemID.MAHOGANY_BIRD_HOUSE, 50, 49), YEW(
            ItemID.YEW_LOGS,
            ItemID.YEW_BIRD_HOUSE,
            60,
            59
        ),
        MAGIC(ItemID.MAGIC_LOGS, ItemID.MAGIC_BIRD_HOUSE, 75, 74), REDWOOD(
            ItemID.REDWOOD_LOGS,
            ItemID.REDWOOD_BIRD_HOUSE,
            90,
            89
        );


        fun canMake(): Boolean {
            val hunterLevel = Skills.getLevel(Skill.HUNTER)
            val craftingLevel = Skills.getLevel(Skill.CRAFTING)
            if (hunterLevel >= hunterLevelReq && craftingLevel >= craftingLevelReq) {
                val logs = Inventory.getAll(logID)
                if (!logs.isEmpty()) {
                    return true
                }
            }
            return false
        }

        companion object {
            val typeToBuild: BirdhouseType?
                get() {
                    val types = BirdhouseType.values()
                    return types.reversed().firstOrNull { it.canMake() }
                }
        }
    }

    private class InventoryRequirement(val quantity: Int) : Predicate<Item> {
        var ids: Collection<Int>? = null
        var id: Int? = null

        constructor(ids: Collection<Int>, quantity: Int) : this(quantity) {
            this.ids = ids
        }

        constructor(id: Int, quantity: Int) : this(quantity) {
            this.id = id
        }

        override fun test(t: Item): Boolean {
            val ids = this.ids
            return (ids?.contains(t.id)) ?: (t.id == id)
        }
    }

    private val baseRequirements = listOf(
        InventoryRequirement(ItemID.CHISEL, 1),
        InventoryRequirement(ItemID.HAMMER, 1),
        InventoryRequirement(ItemID.BARLEY_SEED, 40),
    )
    private val requirements: List<InventoryRequirement>
        get() {
            val items = ArrayList(requirements)
            items.add(InventoryRequirement(birdhouseType.logID, 4))
            return items
        }


    fun shutDownWithError(error: String) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "BirdhouseRunPlugin", error, null)
        toggle(false)
    }

    private lateinit var birdhouseType: BirdhouseType
    override fun startup() {
        super.startup()
        if (!Bank.isOpen()) {
            shutDownWithError("Bank is not open jogger!")
        }
        var type = BirdhouseType.typeToBuild
        if (type == null) {
            shutDownWithError("Missing level requirements!")
        } else {
            birdhouseType = type
        }
    }

    override fun shutdown() {
        super.shutdown()
    }

    internal enum class State {
        BANKING,
        BANKING2,
        MUSHTREE1,
        HOUSE1,
        HOUSE2,
        MUSHTREE2,
        HOUSE3,
        HOUSE4
    }

    internal var state = State.BANKING

    @Subscribe
    fun onGameTick(event: GameTick) {
        when (state) {
            State.BANKING -> doBanking()
        }
    }

    private fun inventoryContainsN(itemID: Int, quantity: Int): Boolean {
        val container = Game.getClient().getItemContainer(InventoryID.INVENTORY) ?: return false
        var count = 0;
        for (item in container.items) {
            if (item.id == itemID) {
                count += item.quantity

                if (count >= quantity)
                    return true
            }
        }
        return false
    }

    private fun doBanking() {
        fun handleReq(req: InventoryRequirement): Boolean {
            var quant = req.quantity
            val bankItem = Bank.getFirst(req) ?: return false
            if (bankItem.quantity >= quant) {
                for (x in 0 until quant) {
                    Bank.withdraw(req, 1, Bank.WithdrawMode.ITEM)
                }
                return true
            }
            return false
        }
        for (x in requirements) {
            if (!handleReq(x)) {
                shutDownWithError("Missing item requirement! ")
            }
        }
        state = State.BANKING2
    }
}