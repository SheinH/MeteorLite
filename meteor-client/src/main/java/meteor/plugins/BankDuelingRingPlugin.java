package meteor.plugins;

import com.google.common.collect.ImmutableSet;
import meteor.eventbus.Subscribe;
import meteor.plugins.api.commons.Time;
import meteor.plugins.api.coords.Area;
import meteor.plugins.api.coords.RectangularArea;
import meteor.plugins.api.entities.TileObjects;
import meteor.plugins.api.game.GameThread;
import meteor.plugins.api.items.Bank;
import meteor.plugins.api.items.Equipment;
import meteor.plugins.api.packets.MousePackets;
import meteor.plugins.api.packets.TileObjectPackets;
import meteor.util.ColorUtil;
import meteor.util.Timer;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;


@PluginDescriptor(
        name = "Bank Dueling Ring",
        description = "Bank with a single click using dueling rings"
)
public class BankDuelingRingPlugin extends Plugin {


    static final Area CASTLE_WARS = new RectangularArea(2434, 3080, 2446, 3099);
    private static final String MENU_OPTION = "Open Bank";
    static boolean duelingRingEquipped = false;
    private static ImmutableSet<Integer> DUELING_RING_IDS = ImmutableSet.of(
            ItemID.RING_OF_DUELING1,
            ItemID.RING_OF_DUELING2,
            ItemID.RING_OF_DUELING3,
            ItemID.RING_OF_DUELING4,
            ItemID.RING_OF_DUELING5,
            ItemID.RING_OF_DUELING6,
            ItemID.RING_OF_DUELING7,
            ItemID.RING_OF_DUELING8
    );
    private static ImmutableSet<Integer> ROW_IDS = ImmutableSet.of(
            ItemID.RING_OF_WEALTH_1,
            ItemID.RING_OF_WEALTH_2,
            ItemID.RING_OF_WEALTH_3,
            ItemID.RING_OF_WEALTH_4,
            ItemID.RING_OF_WEALTH_5
    );
    @Inject
    private ExecutorService executorService;
    private Future<?> currentTask;

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.EQUIPMENT.getId()) {
            var ring = Equipment.fromSlot(EquipmentInventorySlot.RING);
            if (ring != null && DUELING_RING_IDS.contains(ring.getId())) {
                duelingRingEquipped = true;
                return;
            }
            duelingRingEquipped = false;
            return;
        }
    }

    boolean shouldAddMenuEntry() {
        var ring = Equipment.fromSlot(EquipmentInventorySlot.RING);
        if (Arrays.stream(client.getMenuEntries()).anyMatch(
                e -> e.getActionParam1() == ring.getWidgetId()
        ))
            return true;
        return false;
    }

    void insertMenuEntry() {
        MenuEntry entryToAdd = new MenuEntry();
        var ring = Equipment.fromSlot(EquipmentInventorySlot.RING);
        entryToAdd.setOption("Open-bank");
        entryToAdd.setTarget(ColorUtil.wrapWithColorTag(ring.getName(), Color.GREEN));
        entryToAdd.setOpcode(MenuAction.RUNELITE.getId());
        entryToAdd.setIdentifier(ring.getWidgetId());
        client.insertMenuItem(
                "Open-bank",
                ColorUtil.wrapWithColorTag(ring.getName(), Color.GREEN),
                0,
                ring.getWidgetId(),
                0,
                0,
                false
        );
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (!duelingRingEquipped)
            return;
        if (shouldAddMenuEntry()) {
            insertMenuEntry();
        }
    }

    public void reorderMenuEntries() {
        var entries = client.getMenuEntries();
        ArrayList<MenuEntry> pluginEntries = new ArrayList<>(entries.length);
        ArrayList<MenuEntry> otherEntries = new ArrayList<>(entries.length);
        for (MenuEntry e : entries) {
            if (e.getOption().equals("Open-bank")) {
                pluginEntries.add(e);
            } else {
                otherEntries.add(e);
            }
        }
        if (pluginEntries.isEmpty())
            return;
        otherEntries.addAll(pluginEntries);
        client.setMenuEntries(otherEntries.toArray(new MenuEntry[0]));
        logger.info("reordered: " + Arrays.asList(client.getMenuEntries()));
    }

    private void runScript() {
        BooleanSupplier castleWarsCheck = () -> {
            var player = client.getLocalPlayer();
            if (player != null && CASTLE_WARS.contains(player)) {
                return true;
            }
            return false;
        };
        Time.sleepUntil(castleWarsCheck, 50, 5000);
        if(!castleWarsCheck.getAsBoolean()){
            return;
        }
        var player = client.getLocalPlayer();
        Timer timer = new Timer();
        while(true){
            //Bank chest
            if(timer.getMilliecondsFromStart() > 4000){
                return;
            }
            if(Bank.isOpen()){
                return;
            }
            if(player.isIdle()) {
                var chest = TileObjects.getNearest("Bank chest");
                if(chest != null) {
//                    MousePackets.queueClickPacket(0, 0);
//                    TileObjectPackets.tileObjectAction(chest, "Use", 0);
                    chest.interact("Use");
                    Time.sleep(100);
                }
            }
            Time.sleep(50);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if (!duelingRingEquipped)
            return;
        var ring = Equipment.fromSlot(EquipmentInventorySlot.RING);
        if (e.getId() == ring.getWidgetId() && e.getMenuOption().equals("Open-bank")) {
            var entries = client.getMenuEntries();
            var castleWarsEntry = Arrays.stream(entries).filter(x -> x.getOption().equals("Castle Wars")).findFirst();
            castleWarsEntry.ifPresent(e::setMenuEntry);
            if(currentTask == null || currentTask.isDone()) {
                currentTask = executorService.submit(this::runScript);
            }
        }
    }


}
