package meteor.plugins.chaosaltar;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.coords.Area;
import dev.hoot.api.coords.RectangularArea;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.movement.pathfinder.Walker;
import dev.hoot.api.packets.ItemPackets;
import dev.hoot.api.packets.MousePackets;
import dev.hoot.api.packets.NPCPackets;
import dev.hoot.api.scene.Tiles;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.*;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.WallObjectQuery;

import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Chaos Altar",
        enabledByDefault = false
)
@Slf4j
public class ChaosAltarPlugin extends Plugin {

    Instant timer;
    public static boolean enabled = false;
    private static final List<Integer> bones = ImmutableList.of(
            ItemID.BONES, ItemID.WOLF_BONE, ItemID.BURNT_BONES, ItemID.MONKEY_BONES, ItemID.BAT_BONES,
            ItemID.JOGRE_BONE, ItemID.BIG_BONES, ItemID.ZOGRE_BONE, ItemID.SHAIKAHAN_BONES, ItemID.BABYDRAGON_BONES,
            ItemID.WYRM_BONES, ItemID.DRAGON_BONES, ItemID.DRAKE_BONES, ItemID.FAYRG_BONES, ItemID.LAVA_DRAGON_BONES,
            ItemID.RAURG_BONES, ItemID.HYDRA_BONES, ItemID.DAGANNOTH_BONES, ItemID.OURG_BONES, ItemID.SUPERIOR_DRAGON_BONES,
            ItemID.WYVERN_BONES
    );

    private static final Predicate<Item> boneID = x -> bones.contains(x.getId() );
    private static final Predicate<Item> notedBoneID = x -> bones.contains(x.getId() - 1);
    enum ChaosAltarState {
        SACRIFICE,
        UNNOTE,
        SUICIDE,
        WALKBACK
    }

    public ChaosAltarState state = ChaosAltarState.SACRIFICE;

    Area area = Area.union(
            new RectangularArea(2947, 3819, 2957, 3822),
            new RectangularArea(2949, 3817, 2952, 3824));
    int tickDelay = 0;


    @Provides
    public ChaosAltarConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ChaosAltarConfig.class);
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (event.getGroup().equalsIgnoreCase("chaosaltar")) {
            if (event.getKey().equals("startStop")) {
                enabled = !enabled;
                timer = Instant.now();
            }
        }
    }

    @Override
    public void startUp() {
        state = ChaosAltarState.SACRIFICE;
    }

    @Override
    public void shutDown() {
        enabled = false;
    }

    public void doSacrifice() {
        var player = client.getLocalPlayer();
        var altar = new GameObjectQuery().idEquals(ObjectID.CHAOS_ALTAR_411).result(client).first();
        var dist = altar.distanceTo(player);
        if (Inventory.contains(boneID)) {
            var t1 = Tiles.getAt(altar.getLocalLocation());
            var t2 = Tiles.getAt(player.getLocalLocation());
            if (!area.contains(player) && handleDoor()) {
                return;
            }
            MousePackets.queueClickPacket(0,0);
            ItemPackets.useItemOnTileObject(
                    Inventory.getFirst(boneID),
                    altar
            );
            if (dist > 1) {
                tickDelay = 5;
            }
        } else {
            state = ChaosAltarState.UNNOTE;
        }
    }

    public void doUnnote() {
        var player = client.getLocalPlayer();
        if (client.getLocalPlayer().isMoving()) {
            return;
        } else if (Inventory.contains(boneID)) {
            state = ChaosAltarState.SACRIFICE;
        } else if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                Dialog.continueSpace();
            } else if (Dialog.isViewingOptions()) {
                Dialog.chooseOption(3);
            }
        } else {
            if (Inventory.contains(notedBoneID)) {
                var notes = Inventory.getFirst(notedBoneID);
                var druid = NPCs.getNearest(NpcID.ELDER_CHAOS_DRUID_7995);
                if (area.contains(player) && handleDoor()) {
                    return;
                }
                notes.useOn(druid);
                MousePackets.queueClickPacket(0,0);
                client.setSelectedItemWidget(notes.getWidgetId());
                client.setSelectedItemSlot(notes.getSlot());
                client.setSelectedItemID(notes.getId());
                client.invokeMenuAction("","",druid.getId(),MenuAction.ITEM_USE_ON_NPC.getId(),0,0);
            }
        }
    }

    public boolean handleDoor() {
        var door = new WallObjectQuery()
                .nameEquals("Large door")
                .result(client)
                .nearestTo(client.getLocalPlayer());
        if (door.hasAction("Open")) {
            door.interact("Open");
            tickDelay = 3;
            return true;
        } else {
            return false;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {

        if (!enabled) {
            return;
        }
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }

        var area = new RectangularArea(2944, 3827, 2960, 3814);
        var player = client.getLocalPlayer();
        if (area.contains(player)) {
            //doorCheck
            switch (state) {
                case SACRIFICE:
                    doSacrifice();
                    break;
                case UNNOTE:
                    doUnnote();
                    break;
            }
        }
    }
}
