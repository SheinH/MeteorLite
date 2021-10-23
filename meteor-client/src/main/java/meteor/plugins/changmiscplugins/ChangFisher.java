package meteor.plugins.changmiscplugins;

import com.google.common.collect.ImmutableSet;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.entities.NPCs;
import meteor.plugins.api.items.Inventory;
import meteor.plugins.api.packets.ItemPackets;
import meteor.plugins.api.packets.MousePackets;
import meteor.plugins.api.packets.NPCPackets;
import meteor.plugins.api.widgets.Dialog;
import meteor.ui.overlay.OverlayManager;
import meteor.util.Timer;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;

import javax.inject.Inject;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

@PluginDescriptor(
        name = "Chang Fisher",
        description = "Fishes n stuff",
        enabledByDefault = false
)
public class ChangFisher extends Plugin {
    private List<Item> dropList;
    private ListIterator<Item> dropListIterator;
    private WorldPoint currentFishingSpotLoc;
    private NPC currentFishingSpot;

    enum WCState {
        CHOPPING,
        DROPPING
    }
    WCState state = WCState.CHOPPING;

    static final Predicate<NPC> fishingSpotFilter = x -> x.getName().equals("Rod Fishing spot");
    @Inject
    private OverlayManager overlayManager;

    @Override
    public void startup() {
    }

    @Override
    public void shutdown() {
    }

    Random random = new Random();
    private int nextRandom(int mean, int deviation, int min, int max){
        while(true) {
            var gaussian = random.nextGaussian();
            var sample = gaussian * deviation + mean;
            var rounded = (int)(Math.round(sample));
            if(rounded >= min && rounded <= max){
                return rounded;
            }
        }
    }

    private void dropItem(Item item){
        MousePackets.queueClickPacket(0,0);
        ItemPackets.itemAction(item,"Drop");
    }

    private void updateDropList()
    {
        dropList = Inventory.getAll( x -> x.getName().contains("logs"));
        if( dropList == null || dropList.size() == 0)
        {
            state = WCState.CHOPPING;
            return;
        }
        dropListIterator = dropList.listIterator();
    }

    private static final Set<Integer> FISHING_ANIMATIONS = ImmutableSet.of(
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
            AnimationID.FISHING_PEARL_OILY_ROD);
    private static final String FISHING_SPOT = "Fishing spot";
    private boolean getFishing(){
        return client.getLocalPlayer().getInteracting() != null
                && client.getLocalPlayer().getInteracting().getName().contains(FISHING_SPOT)
                && client.getLocalPlayer().getInteracting().getGraphic() != GraphicID.FLYING_FISH
                && FISHING_ANIMATIONS.contains(client.getLocalPlayer().getAnimation());
    }

    Timer timer = new Timer();

    @Subscribe
    public void onGameTick(GameTick event) {
        if(timer.getMinutesFromStart() > 220)
            toggle();
        if(state == WCState.CHOPPING){
            if(!getFishing() || Dialog.isOpen() || currentFishingSpot == null || !currentFishingSpot.getWorldLocation().equals(currentFishingSpotLoc)){
                var tree = NPCs.getNearest("Rod Fishing spot");
                currentFishingSpot = tree;
                currentFishingSpotLoc = tree.getWorldLocation();
                MousePackets.queueClickPacket(0,0);
                NPCPackets.npcAction(tree, "Lure",0);
            }
            if(Inventory.isFull()){
                state = WCState.DROPPING;
                onGameTick(event);
            }
        }
        else{
            var logs = Inventory.getAll(ItemID.RAW_TROUT, ItemID.RAW_SALMON);
            if(logs.isEmpty()){
                state = WCState.CHOPPING;
                currentFishingSpot = null;
                currentFishingSpotLoc = null;
            }
            else{
                logs.forEach(this::dropItem);
            }
        }
    }
}
