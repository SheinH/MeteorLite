package meteor.plugins.changmiscplugins;

import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.entities.Players;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.packets.ItemPackets;
import dev.hoot.api.packets.MousePackets;
import dev.hoot.api.packets.TileObjectPackets;
import meteor.ui.overlay.OverlayManager;
import net.runelite.api.AnimationID;
import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;

import javax.inject.Inject;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

@PluginDescriptor(
        name = "Chang Woodcutter",
        description = "Chops Logs",
        enabledByDefault = false
)
public class ChangWoodCutter extends Plugin {
    private List<Item> dropList;
    private ListIterator<Item> dropListIterator;

    enum WCState {
        CHOPPING,
        DROPPING
    }

    WCState state = WCState.CHOPPING;

    static final Predicate<GameObject> treeMatcher = x -> x.hasAction("Chop down") && x.getName().equals("Maple");

    @Inject
    private OverlayManager overlayManager;

    @Override
    public void startup() {
    }

    @Override
    public void shutdown() {
    }

    Set<Integer> anims = Set.of(
            AnimationID.WOODCUTTING_BRONZE,
            AnimationID.WOODCUTTING_IRON,
            AnimationID.WOODCUTTING_STEEL,
            AnimationID.WOODCUTTING_BLACK,
            AnimationID.WOODCUTTING_MITHRIL,
            AnimationID.WOODCUTTING_ADAMANT,
            AnimationID.WOODCUTTING_RUNE,
            AnimationID.WOODCUTTING_GILDED,
            AnimationID.WOODCUTTING_DRAGON,
            AnimationID.WOODCUTTING_DRAGON_OR,
            AnimationID.WOODCUTTING_INFERNAL,
            AnimationID.WOODCUTTING_3A_AXE,
            AnimationID.WOODCUTTING_CRYSTAL,
            AnimationID.WOODCUTTING_TRAILBLAZER
            );

    Random random = new Random();

    private int nextRandom(int mean, int deviation, int min, int max) {
        while (true) {
            var gaussian = random.nextGaussian();
            var sample = gaussian * deviation + mean;
            var rounded = (int) (Math.round(sample));
            if (rounded >= min && rounded <= max) {
                return rounded;
            }
        }
    }

    private void dropItem(Item item) {
        MousePackets.queueClickPacket(0, 0);
        ItemPackets.itemAction(item, "Drop");
        item.interact("Drop");
    }

    private void updateDropList() {
        dropList = Inventory.getAll(x -> x.getName().contains("logs"));
        if (dropList == null || dropList.size() == 0) {
            state = WCState.CHOPPING;
            return;
        }
        dropListIterator = dropList.listIterator();
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (state == WCState.CHOPPING) {
            if ((!Movement.isWalking() && !anims.contains(Players.getLocal().getAnimation()))) {
                var tree = new GameObjectQuery()
                        .filter(treeMatcher)
                        .isWithinDistance(Players.getLocal().getWorldLocation(), 20)
                        .result(client)
                        .nearestTo(Players.getLocal());
                MousePackets.queueClickPacket(0, 0);
                TileObjectPackets.tileObjectFirstOption(tree,0);
                logger.info("Chopping");
            }
            if (Inventory.isFull()) {
                state = WCState.DROPPING;
                onGameTick(event);
            }
        } else {
            var logs = Inventory.getAll(ItemID.OAK_LOGS);
            if (logs.isEmpty()) {
                state = WCState.CHOPPING;
            } else {
                logs.forEach(this::dropItem);
            }
        }
    }
}
