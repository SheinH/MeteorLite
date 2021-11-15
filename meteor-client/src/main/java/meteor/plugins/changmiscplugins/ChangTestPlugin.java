package meteor.plugins.changmiscplugins;

import com.google.inject.Inject;
import meteor.callback.ClientThread;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.commons.Time;
import dev.hoot.api.game.Game;
import dev.hoot.api.game.GameThread;
import dev.hoot.api.input.Mouse;
import dev.hoot.api.items.Bank;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.packets.*;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemObtained;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

@PluginDescriptor(name = "Chang Test Plugin",description = "For testing use.")
public class ChangTestPlugin extends Plugin {

    @Inject
    private ScheduledExecutorService executor;

    private void handleTP(){
        Predicate<Item> isGamesNeck = x -> x.getId() >= ItemID.GAMES_NECKLACE8 && x.getId() <= ItemID.GAMES_NECKLACE1 && x.getId() % 2 == 1;
        var necklace = Inventory.getFirst(isGamesNeck);
        if(necklace != null){
            MousePackets.queueClickPacket(0,0);
            ItemPackets.itemAction(necklace,"Rub");
            executor.execute(() -> {
                Time.sleepUntil(Dialog::isViewingOptions,50,2000);
                Dialog.chooseOption(3);
            });
        }

    }

    private void withdrawX(int itemID, int quantity){
        var bankItem = Bank.getFirst(itemID);
        GameThread.invoke(() -> {
            MousePackets.queueClickPacket(0,0);
            client.invokeMenuAction("", "", 6, MenuAction.CC_OP_LOW_PRIORITY.getId(), bankItem.getSlot(), WidgetInfo.BANK_ITEM_CONTAINER.getPackedId());
            Packets.queuePacket(Game.getClient().getNumberInputPacket(), quantity);
        });
    }
    private void withdrawOne(int itemID){
        var bankItem = Bank.getFirst(itemID);
        MousePackets.queueClickPacket(0,0);
        GameThread.invoke(() -> {
            MousePackets.queueClickPacket(0,0);
            client.invokeMenuAction("","",1,MenuAction.CC_OP.getId(),bankItem.getSlot(), WidgetInfo.BANK_ITEM_CONTAINER.getPackedId());
        });
    }
    private void withdraw(int itemID, int quantity){
        if(quantity == 1){
            withdrawOne(itemID);
        }
        else{
            withdrawX(itemID,quantity);
        }
    }
    @Subscribe
    public void onGameTick(GameTick event){
        if(Bank.isOpen()) {
            executor.execute(this::bankingTest);
            toggle(false);
        }
    }

    private void bankingTest() {
        GameThread.invoke(() -> {
            MousePackets.queueClickPacket(0,0);
            client.invokeMenuAction("","",1,MenuAction.CC_OP.getId(),-1,WidgetInfo.BANK_DEPOSIT_INVENTORY.getPackedId());
        });
        withdraw(ItemID.LAW_RUNE,50);
        withdraw(ItemID.AIR_RUNE,10);
        withdraw(ItemID.FIRE_RUNE,25);
        withdraw(ItemID.NATURE_RUNE,3);
        withdraw(ItemID.SHARK,1);
        withdraw(ItemID.SHARK,1);
        withdraw(ItemID.SHARK,1);
        GameThread.invoke(() -> Game.getClient().runScript(138));
    }

    @Subscribe
    public void onItemObtained(ItemObtained event){
        logger.info("ItemObtained!");
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event){
        try {
            if (event.getContainerId() == InventoryID.LOOTING_BAG.getId()) {
                //LOOTING BAG CHANGE
                logger.info("LOOTING BAG INVENTORY CHANGE");
            }
        }
        catch (Exception e ){
            e.printStackTrace();
        }
    }
}
