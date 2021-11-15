package meteor.plugins.changmiscplugins;


import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.items.Bank;
import dev.hoot.api.items.Equipment;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.magic.Magic;
import dev.hoot.api.magic.Regular;
import dev.hoot.api.packets.ItemPackets;
import dev.hoot.api.packets.MousePackets;
import dev.hoot.api.packets.SpellPackets;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import org.apache.commons.math3.distribution.GammaDistribution;

import java.util.Random;
import java.util.Set;

@PluginDescriptor(
        name = "Chang Alcher",
        description = "Reclined gaming",
        enabledByDefault = false
)
public class ChangAlcher extends Plugin {

    public static final int salt = ItemID.SALTPETRE;
    public static final int compost = ItemID.COMPOST;



    enum State {
        COMBINING,
        BANKING
    }
    State state = State.COMBINING;

    Set<Integer> set = Set.of(1274,1340,1344,1116,1358,19582,892);
    int tickDelay;

    int tick = 0;
    @Subscribe
    public void onGameTick(GameTick event) {

        var item = Inventory.getFirst(1274,1340,1344,1116,1358,19582,ItemID.MITHRIL_SCIMITAR + 1, ItemID.STEEL_PLATEBODY + 1, ItemID.RUNE_JAVELIN_HEADS, ItemID.RUNE_JAVELIN_HEADS + 1);
        var natureRunes = Inventory.getFirst(ItemID.NATURE_RUNE);
        if(item != null && natureRunes != null){
            if(tick == 0)
                Magic.cast(Regular.HIGH_LEVEL_ALCHEMY,item);
            if(tick == 1)
                Magic.cast(Regular.CAMELOT_TELEPORT);
        }
        tick++;
        if(tick >= 5)
            tick = 0;
    }


    private void randDelay(){
        var distribution = new GammaDistribution(1.5,4.0 / 1.5);
        var sample = distribution.sample();
        var rounded = (int)(Math.round(sample));
        if(rounded < 20)
            tickDelay = rounded;
        else
            randDelay();
    }
}
