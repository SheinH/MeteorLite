package meteor.plugins.changmiscplugins;


import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.commons.Time;
import meteor.plugins.api.entities.NPCs;
import meteor.plugins.api.game.GameThread;
import meteor.plugins.api.items.Bank;
import meteor.plugins.api.items.Inventory;
import meteor.plugins.api.magic.Regular;
import meteor.plugins.api.magic.Spell;
import meteor.plugins.api.packets.MousePackets;
import meteor.plugins.api.packets.NPCPackets;
import meteor.plugins.api.packets.SpellPackets;
import meteor.plugins.api.packets.WidgetPackets;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@PluginDescriptor(
        name = "Chang Enchanger",
        description = "EZ Enchants",
        enabledByDefault = false
)
public class ChangEnchanter extends Plugin {


    enum State {
        ENCHANTING,
        BANKING
    }

    State state = State.ENCHANTING;

    Set<Integer> set = Set.of(1274, 1340, 1344, 1116, 1358, 19582, 892);
    int tickDelay;

    Spell spell = Regular.LVL_1_ENCHANT;
    int itemToEnchant = ItemID.SAPPHIRE_RING;
    int itemOutput = ItemID.RING_OF_RECOIL;

    int bankItemSlot = -1;
    @Inject
    private ExecutorService executorService;

    void doPacketBank() {
        if (Inventory.contains(itemToEnchant)) {
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.queueWidgetAction2Packet(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), itemToEnchant, 0);
        }
        MousePackets.queueClickPacket(0, 0);
        WidgetPackets.queueWidgetActionPacket(WidgetInfo.BANK_ITEM_CONTAINER.getPackedId(), 1973, bankItemSlot);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }
        if (state == State.ENCHANTING) {
            if (!Inventory.contains(ItemID.COSMIC_RUNE)) {
                toggle();
                return;
            }
            var ring = Inventory.getFirst(itemToEnchant);
            if (ring == null) {
                state = State.BANKING;
                onGameTick(event);
                return;
            } else {
                MousePackets.queueClickPacket(0, 0);
                SpellPackets.spellOnItem(spell, ring);
            }
        } else {
            if (Bank.isOpen()) {
                if (Inventory.contains(itemToEnchant)) {
                    state = State.ENCHANTING;
                    onGameTick(event);
                    return;
                }
                if (Inventory.contains(itemOutput)) {
                    Bank.depositAll(itemOutput);
                    Bank.withdrawAll(itemToEnchant, Bank.WithdrawMode.ITEM);
                } else if (!Inventory.isFull()) {
                    if (Bank.contains(itemToEnchant))
                        Bank.withdrawAll(itemToEnchant, Bank.WithdrawMode.ITEM);
                    else
                        toggle();
                } else {
                    toggle();
                }
            } else {
                NPC banker = NPCs.getNearest("Banker");
                banker.interact("Bank");
                tickDelay = 1;
            }
        }
    }
}
