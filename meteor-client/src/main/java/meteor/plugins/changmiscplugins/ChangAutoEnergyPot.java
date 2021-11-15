package meteor.plugins.changmiscplugins;

import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.entities.Players;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.packets.ItemPackets;
import dev.hoot.api.packets.MousePackets;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;

import java.util.Random;

@PluginDescriptor(
        name = "Chang Auto Energy",
        description = "Drinks energy pots for you!",
        enabledByDefault = false
)
public class ChangAutoEnergyPot extends Plugin {

    int staminaThreshold = randThreshold();
    int tickDelay = 0;

    @Override
    public void startUp() {
        randThreshold();
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if(tickDelay > 0){
            tickDelay--;
        }
        if(client.getEnergy() > staminaThreshold){
            return;
        }
        var target = client.getLocalDestinationLocation();
        if(target == null){
            return;
        }
        var distance = target.distanceTo(Players.getLocal().getLocalLocation());
        if(distance > 4){
            drinkPot();
        }
    }

    private void drinkPot(){
        var pot = Inventory.getFirst(
                ItemID.ENERGY_POTION1,
                ItemID.ENERGY_POTION2,
                ItemID.ENERGY_POTION3,
                ItemID.ENERGY_POTION4
        );
        if(pot != null){
            MousePackets.queueClickPacket(0,0);
            client.invokeMenuAction(
                    "Drink",
                    "<col=ff9040>Energy Potion",
                    pot.getId(),
                    MenuAction.ITEM_FIRST_OPTION.getId(),
                    pot.getSlot(),
                    WidgetInfo.INVENTORY.getPackedId()
            );
            //ItemPackets.itemAction(pot,"Drink");
        }
        tickDelay = 5;
        staminaThreshold = randThreshold();
    }

    static Random random = new Random();
    private int randThreshold(){
        int threshold;
        do{
            threshold = (int)(Math.round(random.nextGaussian() * 15 + 35));
        }while(threshold < 10 || threshold > 60);
        return threshold;
    }
}
