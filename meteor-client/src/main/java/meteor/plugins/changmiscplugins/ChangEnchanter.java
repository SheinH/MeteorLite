package meteor.plugins.changmiscplugins;


import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.commons.Time;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.game.GameThread;
import dev.hoot.api.items.Bank;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.magic.Regular;
import dev.hoot.api.magic.Spell;
import dev.hoot.api.packets.*;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@PluginDescriptor(
        name = "Chang Enchanter",
        description = "EZ Enchants",
        enabledByDefault = false
)
public class ChangEnchanter extends Plugin {


    private int failed;

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

    @Override
    public void startup()
    {
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
                bankTick = 0;
                onGameTick(event);
                return;
            } else {
                MousePackets.queueClickPacket(0, 0);
                SpellPackets.spellOnItem(spell, ring);
            }
        } else
        {
            if (Inventory.contains(itemToEnchant))
            {
                state = State.ENCHANTING;
                onGameTick(event);
                return;
            }
            if (bankItemSlot == -1)
            {
                if (Bank.isOpen())
                {
                    if (Inventory.contains(itemOutput))
                    {
                        Bank.depositAll(itemOutput);
                        Bank.withdrawAll(itemToEnchant, Bank.WithdrawMode.ITEM);
                        bankItemSlot = Bank.getFirst(itemToEnchant).getSlot();
                    } else if (!Inventory.isFull())
                    {
                        if (Bank.contains(itemToEnchant))
                        {
                            Bank.withdrawAll(itemToEnchant, Bank.WithdrawMode.ITEM);
                        }
                        else
                            toggle();
                    } else
                    {
                        toggle();
                    }
                } else
                {
                    NPC banker = NPCs.getNearest("Banker");
                    banker.interact("Bank");
                    tickDelay = 1;
                }
            }
            else{
                quickBank();
            }
        }
    }
    int bankTick = 0;
    private void quickBank()
    {
        NPC bank = NPCs.getNearest("Banker");
        if (bankTick == 0)
        {
            if (bank != null)
            {
                if (Inventory.getFreeSlots() > 0)
                {
                    failed++;
                    if (failed > 2)
                    {
                        this.toggle();
                        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "ChocoGrinder", "Out of chocolate", null);
                        return;
                    }
                    MousePackets.queueClickPacket(0, 0);
                    NPCPackets.queueNPCAction3Packet(bank.getIndex(), 0);
                    return;
                }
                failed = 0;
                MousePackets.queueClickPacket(0, 0);
                NPCPackets.queueNPCAction3Packet(bank.getIndex(), 0);
            }
        }
        if(bankTick == 1){
            if(!Inventory.getAll(itemOutput).isEmpty()) {
                MousePackets.queueClickPacket(0, 0);
                WidgetPackets.queueWidgetAction2Packet(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(),itemOutput,0);
            }
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.queueWidgetActionPacket(WidgetInfo.BANK_ITEM_CONTAINER.getPackedId(),itemToEnchant,bankItemSlot);
        }
        if(bankTick == 3){
            MousePackets.queueClickPacket(0, 0);
        }
        bankTick++;
    }
}
