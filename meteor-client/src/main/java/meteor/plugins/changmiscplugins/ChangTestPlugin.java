package meteor.plugins.changmiscplugins;

import com.google.inject.Inject;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.commons.Time;
import meteor.plugins.api.items.Inventory;
import meteor.plugins.api.packets.ItemPackets;
import meteor.plugins.api.packets.MousePackets;
import meteor.plugins.api.widgets.Dialog;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;

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

    @Subscribe
    public void onGameTick(GameTick event){
        handleTP();
        toggle();
    }
}
