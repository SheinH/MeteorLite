package meteor.plugins.changmiscplugins;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provides;
import meteor.callback.ClientThread;
import meteor.chat.ChatMessageManager;
import meteor.chat.QueuedMessage;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.commons.Time;
import dev.hoot.api.coords.Area;
import dev.hoot.api.coords.RectangularArea;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileItems;
import dev.hoot.api.game.*;
import dev.hoot.api.items.Bank;
import dev.hoot.api.items.Equipment;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.magic.Magic;
import dev.hoot.api.magic.Regular;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.packets.*;
import dev.hoot.api.scene.Tiles;
import dev.hoot.api.widgets.Dialog;
import dev.hoot.api.widgets.Prayers;
import meteor.ui.FontManager;
import meteor.ui.overlay.OverlayManager;
import meteor.util.PvPUtil;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.queries.WallObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import org.apache.commons.lang3.time.StopWatch;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "Chang Lava Drags",
        description = "Farms lava drags,",
        enabledByDefault = false,
        disabledOnStartup = true
)
public class ChangLavaDrags extends Plugin
{
    //static final WorldPoint fightingSpot = new WorldPoint(3205, 3803, 0);
    static final WorldPoint fightingSpot = new WorldPoint(3200, 3807, 0);
    static final WorldPoint vetionSafeSpot = new WorldPoint(3184, 3801, 0);
    static final Area generalArea = new RectangularArea(3175, 3793, 3223, 3808);
    //static final Area lavaDragonTargetArea = new RectangularArea(3204, 3805, 3214, 3814);
    static final Area lavaDragonTargetArea = new RectangularArea(3197, 3817, 3204, 3810);
    static final Area geArea = new RectangularArea(3137, 3518, 3194, 3467);
    static final Area corpCave = new RectangularArea(2953, 4405, 3008, 4366, 2);
    static final Area validArea = Area.union(
            new RectangularArea(3136, 3820, 3217, 3780),
            new RectangularArea(3186, 3655, 3219, 3809),
            new RectangularArea(3139, 3468, 3188, 3516),
            corpCave
    );
    private static final WorldPoint walkBackPathPoint = new WorldPoint(3200, 3801, 0);
    ImmutableSet<Integer> vetionIDs = ImmutableSet.of(
            NpcID.VETION,
            NpcID.VETION_REBORN
    );
    LavaDragsState state = LavaDragsState.FIGHTING;
    StopWatch stateTimer = new StopWatch();
    int inventoryItemsValue;
    int killCount;
    @Inject
    private ScheduledExecutorService executor;
    @Inject
    ChatMessageManager chatMessageManager;
    @Inject
    ItemManager itemManager;
    @Inject
    OverlayManager overlayManager;
    @Inject
    ChangLavaDragOverlay overlay;
    @Inject
    ClientThread clientThread;
    @Inject
    ChangLavaDragsConfig config;
    StopWatch runTime = new StopWatch();
    int lootingBagTries = 0;
    NPC currentTarget;
    boolean lootingBagFull;
    private Future<?> bankTask;
    int bankTries = 0;
    boolean readyToLeave;
    private ItemContainer previousContainerState;
    private int previousTripsProfit = 0;
    private int startGold;
    private boolean alchedLastTick;
    private static final int FOOD_ITEM_ID = ItemID.RAINBOW_FISH;
    static int alchItem = ItemID.RUNE_ARROW;

    static class InventoryRequirement
    {
        int itemID;
        int quantityRequired;

        public InventoryRequirement(int itemID, int quantityRequired)
        {
            this.itemID = itemID;
            this.quantityRequired = quantityRequired;
        }

        boolean containedInBank()
        {
            var item = Bank.getFirst(itemID);
            if (item == null)
                return false;
            return item.getQuantity() >= quantityRequired;
        }

        boolean containedInInventory()
        {
            var item = Inventory.getAll(itemID);
            var sum = 0;
            for (var i : item)
            {
                sum += Math.max(i.getQuantity(), 1);
            }
            return sum >= quantityRequired;
        }
    }

    static ImmutableList<InventoryRequirement> tridentInvRequirements = ImmutableList.of(
            new InventoryRequirement(ItemID.AIR_RUNE, 80),
            new InventoryRequirement(ItemID.LAW_RUNE, 80),
            new InventoryRequirement(ItemID.NATURE_RUNE, 3),
            new InventoryRequirement(ItemID.FIRE_RUNE, 15),
            new InventoryRequirement(ItemID.PESTLE_AND_MORTAR, 1),
            new InventoryRequirement(ItemID.LOOTING_BAG, 1),
            new InventoryRequirement(FOOD_ITEM_ID, 3),
            new InventoryRequirement(ItemID.DIVINE_MAGIC_POTION1, 1),
            new InventoryRequirement(ItemID.DIVINE_MAGIC_POTION4, 1)
            );
    static ImmutableList<InventoryRequirement> alchRequirements = ImmutableList.of(
            new InventoryRequirement(ItemID.NATURE_RUNE, 600),
            new InventoryRequirement(alchItem, 600)
            );
    static ImmutableList<InventoryRequirement> lowLevelReqiurements = ImmutableList.of(
            new InventoryRequirement(ItemID.AIR_RUNE, 2000),
            new InventoryRequirement(ItemID.LAW_RUNE, 80),
            new InventoryRequirement(ItemID.PESTLE_AND_MORTAR, 1),
            new InventoryRequirement(ItemID.LOOTING_BAG, 1),
            new InventoryRequirement(FOOD_ITEM_ID, 3),
            new InventoryRequirement(ItemID.DIVINE_MAGIC_POTION4, 2),
            new InventoryRequirement(ItemID.DEATH_RUNE, 500)
    );

    void transitionState(LavaDragsState newState)
    {
        state = newState;
        if (newState == LavaDragsState.LOOTING)
        {
            lootingBagTries = 0;
            lavaDragAttacked = false;
        }
        stateTimer.reset();
        stateTimer.start();
    }

    @Override
    public void startup()
    {
        var player = client.getLocalPlayer();
        if (player == null || !Area.union(generalArea, geArea).contains(player))
        {
            toggle(false);
            sendChatMessage("Invalid start state!");
        }
        state = LavaDragsState.FIGHTING;
        if (Bank.isOpen() || geArea.contains(player))
        {
            state = LavaDragsState.BANKING;
        }
        clientThread.invoke(() ->
        {
            startGold = getInventoryValue();
        });
        inventoryItemsValue = 0;
        previousTripsProfit = 0;
        stateTimer.reset();
        stateTimer.start();
        runTime.reset();
        runTime.start();
        killCount = 0;
        overlayManager.add(overlay);
        tridentCharges = 0;
    }

    @Override
    public void shutdown()
    {
        overlayManager.remove(overlay);
    }

    public boolean isNearPlayer()
    {
        var me = client.getLocalPlayer();
        var player = Players.getNearest(x -> !x.equals(me));
        return player != null && player.distanceTo(me) < 10;
    }

    public boolean protectItemCheck()
    {
        var in_wildy = client.getVar(Varbits.IN_WILDERNESS) == 1;
        if (in_wildy && isNearPlayer() && !Prayers.isEnabled(Prayer.PROTECT_ITEM))
        {
            Prayers.toggle(Prayer.PROTECT_ITEM);
            return true;
        }
        return false;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (!validArea.contains(Players.getLocal()))
        {
            halt();
            return;
        }
        if (protectItemCheck())
            return;
        switch (state)
        {
            case FIGHTING -> doFighting();
            case LOOTING -> doLooting();
            case FLEEING_VETION -> doFleeingVetion();
            case WAITING_VETION -> doWaitingVetion();
            case LEAVING -> doLeaving();
            case BANKING -> doBanking();
            case GOING -> doGoing();
        }
    }

    private void halt()
    {
        turnOffPrayer();
        if (Vars.getBit(Varbits.IN_WILDERNESS) == 1 || WorldType.isPvpWorld(client.getWorldType()))
        {
            Widget logoutButton = client.getWidget(182, 8);
            Widget logoutDoorButton = client.getWidget(69, 23);
            int param1 = -1;
            if (logoutButton != null)
            {
                param1 = logoutButton.getId();
            } else if (logoutDoorButton != null)
            {
                param1 = logoutDoorButton.getId();
            }
            if (param1 == -1)
            {
                return;
            }
            int p1 = param1;
            MousePackets.queueClickPacket(0, 0);
            GameThread.invoke(() -> client.invokeMenuAction(
                    "Logout",
                    "",
                    1,
                    MenuAction.CC_OP.getId(),
                    -1,
                    p1
            ));
        }
        toggle(false);
    }


    @Provides
    public ChangLavaDragsConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ChangLavaDragsConfig.class);
    }

    private void flickPrayer()
    {
        int currentPrayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
        if (currentPrayerPoints == 0)
            return;
        boolean quickPrayer = client.getVar(Varbits.QUICK_PRAYER) == 1;
        if (quickPrayer)
        {
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
        }
        MousePackets.queueClickPacket(0, 0);
        WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
    }

    private void turnOffPrayer()
    {
        if (Prayers.isQuickPrayerEnabled())
        {
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
        }
    }

    private void doGoing()
    {
        if (geArea.contains(Players.getLocal()))
        {
            handleTP();
            return;
        }
        if (corpCave.contains(Players.getLocal()))
        {
            if (Dialog.isViewingOptions())
            {
                Dialog.chooseOption(1);
                return;
            }
            var exit = new WallObjectQuery()
                    .nameEquals("Cave exit")
                    .result(client)
                    .first();
            if (exit != null)
            {
                exit.interact("Exit");
            } else
            {
                logger.info("Cave exit not found!");
            }
        } else
        {
            flickPrayer();
            var dest = client.getLocalDestinationLocation();
            if (!Movement.isWalking() || (dest != null && client.getLocalPlayer().distanceTo(WorldPoint.fromLocal(client, dest)) <= 3))
            {
                Movement.walkTo(fightingSpot);
            }
            if (generalArea.contains(Players.getLocal()))
            {
                transitionState(LavaDragsState.FIGHTING);
                if (Prayers.isQuickPrayerEnabled())
                {
                    MousePackets.queueClickPacket(0, 0);
                    WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
                }
                return;
            }
        }
    }

    private void withdraw(int itemID, int quantity)
    {
        if (quantity < 5)
        {
            for (int i = 0; i < quantity; i++)
            {
                withdrawOne(itemID);
            }
        } else
        {
            withdrawX(itemID, quantity);
        }
    }

    private void withdrawX(int itemID, int quantity)
    {
        var bankItem = Bank.getFirst(itemID);
        GameThread.invoke(() ->
        {
            MousePackets.queueClickPacket(0, 0);
            client.invokeMenuAction("", "", 6, MenuAction.CC_OP_LOW_PRIORITY.getId(), bankItem.getSlot(), WidgetInfo.BANK_ITEM_CONTAINER.getPackedId());
            Packets.queuePacket(Game.getClient().getNumberInputPacket(), quantity);
        });
    }

    private void withdrawOne(int itemID)
    {
        var bankItem = Bank.getFirst(itemID);
        MousePackets.queueClickPacket(0, 0);
        int menuIndex = 1;
        if (Bank.getQuantityMode() != Bank.QuantityMode.ONE)
            menuIndex = 2;
        int menuIndexCopy = menuIndex;
        GameThread.invoke(() ->
        {
            MousePackets.queueClickPacket(0, 0);
            client.invokeMenuAction("", "", menuIndexCopy, MenuAction.CC_OP.getId(), bankItem.getSlot(), WidgetInfo.BANK_ITEM_CONTAINER.getPackedId());
        });
    }


    private void bankItems()
    {
        readyToLeave = false;
        var lootingBag = Inventory.getFirst(ItemID.LOOTING_BAG);

        if (lootingBag != null)
        {
            Time.sleepUntil(() -> client.getWidget(15, 8) != null, 200, 10000);
            var widget = client.getWidget(15, 8);
            if (widget == null)
            {
                return;
            }
            GameThread.invoke(() -> widget.interact("Deposit loot"));
            logger.info("Dumping looting bag");
            Time.sleep(600);
        }


        GameThread.invoke(() -> Bank.depositInventory());
        Time.sleepUntil(() -> Inventory.isEmpty(), 50, 5000);
        if(config.enableAlching()){
            alchRequirements.forEach(
                    x -> withdraw(x.itemID,x.quantityRequired)
            );
        }
        if(config.lowLevelMode()){
            lowLevelReqiurements.forEach(
                    x -> withdraw(x.itemID,x.quantityRequired)
            );
        }
        else
        {
            tridentInvRequirements.forEach(
                    x -> withdraw(x.itemID, x.quantityRequired)
            );
        }
//        withdraw(ItemID.AIR_RUNE, 80);
//        withdraw(ItemID.LAW_RUNE, 80);
//        if (config.enableAlching())
//        {
//            withdraw(ItemID.NATURE_RUNE, 600);
//            withdraw(alchItem, 600);
//        } else
//        {
//            withdraw(ItemID.NATURE_RUNE, 3);
//            withdraw(ItemID.FIRE_RUNE, 15);
//        }
//        Time.sleep(600);
//        withdraw(ItemID.PESTLE_AND_MORTAR, 1);
//        withdraw(ItemID.LOOTING_BAG, 1);
//        withdraw(FOOD_ITEM_ID, 1);
//        withdraw(FOOD_ITEM_ID, 1);
//        withdraw(FOOD_ITEM_ID, 1);
//        withdraw(ItemID.DIVINE_MAGIC_POTION1, 1);
//        withdraw(ItemID.DIVINE_MAGIC_POTION4, 1);

        Predicate<Item> isGamesNeck = x -> x.getId() >= ItemID.GAMES_NECKLACE8 && x.getId() <= ItemID.GAMES_NECKLACE1 && x.getId() % 2 == 1;
        var neck = Bank.getFirst(isGamesNeck);
        if (neck != null)
        {
            withdraw(neck.getId(), 1);
        }

        handleROW();
        //Check ROW
        GameThread.invoke(() -> Game.getClient().runScript(138)); // closes the input dialog
        //Time.sleepUntil(() -> readyToLeave == true,100,5000);
        Time.sleepUntil(() -> lastInventoryChange.getTime() >= 600, 2000);
        if (readyForAnotherTrip() && tridentCheck())
        {
            transitionState(LavaDragsState.GOING);
            startGold = 0;
            lootingBagFull = false;
        } else
        {
            bankTries++;
            if (bankTries > 3)
                halt();
        }
    }

    private boolean tridentCheck()
    {
        var wep = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);
        wep.interact("Check");
        Time.sleepUntil(() -> numCharges != -1, 50, 2000);
        return numCharges >= 500;
    }

    private boolean inventoryHasItemAmount(int itemID, int amount)
    {
        var items = Inventory.getAll(itemID);
        int total = 0;
        for (var x : items)
        {
            total += x.getQuantity();
        }
        return Math.max(total, items.size()) >= amount;
    }

    private boolean readyForAnotherTrip()
    {
        var items = Inventory.getAll();
        logger.info(items);
        if (Inventory.isFull())
        {
            logger.info("Inventory full!");
            return false;
        }
        var req = tridentInvRequirements;
        if(config.lowLevelMode()){
            req = lowLevelReqiurements;
        }
        var notMet = req.stream().filter(x -> !x.containedInInventory()).findFirst();
        if(notMet.isPresent()){
            logger.info(notMet.get().itemID + "is missing");
            return false;
        }
        Predicate<Item> isGamesNeck = x -> x.getId() >= ItemID.GAMES_NECKLACE8 && x.getId() <= ItemID.GAMES_NECKLACE1 && x.getId() % 2 == 1;
        if (!Inventory.contains(isGamesNeck))
        {
            logger.info("Missing games neck!");
            return false;
        }
        if (config.enableAlching())
        {
            if (!inventoryHasItemAmount(ItemID.NATURE_RUNE, 600) || !inventoryHasItemAmount(alchItem, 600))
            {
                logger.info("Missing alch items!");
                return false;
            }
        }

        //Trident check
        //ROW CHECk
        var ring = Equipment.fromSlot(EquipmentInventorySlot.RING);
        final ImmutableSet<Integer> validRings = ImmutableSet.of(
                ItemID.RING_OF_WEALTH_1,
                ItemID.RING_OF_WEALTH_2,
                ItemID.RING_OF_WEALTH_3,
                ItemID.RING_OF_WEALTH_4,
                ItemID.RING_OF_WEALTH_5
        );
        if (ring == null || !validRings.contains(ring.getId()))
            return false;
        numCharges = -1;

        return true;
    }

    private void handleROW()
    {
        var oldRing = Equipment.fromSlot(EquipmentInventorySlot.RING);
        if (oldRing == null || oldRing.getId() == ItemID.RING_OF_WEALTH)
        {
            withdraw(ItemID.RING_OF_WEALTH_5, 1);
        }
        Time.sleepUntil(() -> Inventory.contains(ItemID.RING_OF_WEALTH_5), 50, 2000);
        var ring = Inventory.getFirst(ItemID.RING_OF_WEALTH_5);
        if (ring != null)
        {
            MousePackets.queueClickPacket(0, 0);
            ItemPackets.queueBankItemActionPacket(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), ring.getId(), ring.getSlot());
            Time.sleepUntil(() ->
            {
                var ringSlot = Equipment.fromSlot(EquipmentInventorySlot.RING);
                return ringSlot != null && ringSlot.getId() == ItemID.RING_OF_WEALTH_5;
            }, 50, 4000);
            if (Inventory.contains(ItemID.RING_OF_WEALTH))
            {
                Bank.deposit(ItemID.RING_OF_WEALTH, 1);
            }
        }
    }

    private void handleTP()
    {
        Predicate<Item> isGamesNeck = x -> x.getId() >= ItemID.GAMES_NECKLACE8 && x.getId() <= ItemID.GAMES_NECKLACE1 && x.getId() % 2 == 1;
        var necklace = Inventory.getFirst(isGamesNeck);
        if (necklace != null)
        {
            MousePackets.queueClickPacket(0, 0);
            ItemPackets.itemAction(necklace, "Rub");
            executor.execute(() ->
            {
                Time.sleepUntil(Dialog::isViewingOptions, 50, 2000);
                Dialog.chooseOption(3);
            });
        }

    }

    private void doBanking()
    {
        if (stateTimer.getTime(TimeUnit.SECONDS) > 180)
            halt();
        turnOffPrayer();
        if (Bank.isOpen())
        {
            if (bankTask == null || bankTask.isDone())
            {
                bankTask = executor.submit(this::bankItems);
            }
            return;
        }
        if (stateTimer.getTime(TimeUnit.SECONDS) > 60)
        {
            toggle(false);
            return;
        }
        if (client.getLocalPlayer().isIdle() && bankTask == null || bankTask.isDone())
        {
            var banker = NPCs.getNearest("Banker");
            banker.interact("Bank");
        }
    }

    private void doLeaving()
    {
        flickPrayer();
        if (geArea.contains(client.getLocalPlayer()))
        {
            turnOffPrayer();
            transitionState(LavaDragsState.BANKING);
            previousTripsProfit += getInventoryValue() - startGold;
            FontManager.getDefaultFont();
            inventoryItemsValue = 0;
            return;
        }
        if (stateTimer.getTime(TimeUnit.MINUTES) >= 2)
        {
            halt();
            return;
        }
        final WorldPoint exitPoint = new WorldPoint(3205, 3740, 0);
        var destination = client.getLocalDestinationLocation();
        if (!Movement.isWalking())
        {
            Movement.walkTo(exitPoint);
        }
        if (destination != null)
        {
            if (Players.getLocal().getLocalLocation().distanceTo(destination) < 4)
                Movement.walkTo(exitPoint);
        }
        var ring = WidgetInfo.EQUIPMENT_RING;
        var ringWidget = client.getWidget(ring);
        if (PvPUtil.getWildernessLevelFrom(client.getLocalPlayer().getWorldLocation()) < 30)
        {
            turnOffPrayer();
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.widgetAction(ringWidget, "Grand Exchange");
        }
    }

    private void doWaitingVetion()
    {
        turnOffPrayer();
        if (stateTimer.getTime(TimeUnit.SECONDS) >= 10)
        {
            var player = client.getLocalPlayer();
            transitionState(LavaDragsState.FIGHTING);
            MousePackets.queueClickPacket(0, 0);
            MovementPackets.sendMovement(fightingSpot);
        }
    }

    private void doFleeingVetion()
    {
        flickPrayer();
        if (state == LavaDragsState.LEAVING)
            return;
        var player = client.getLocalPlayer();
        if (player.getWorldLocation().equals(vetionSafeSpot))
        {
            transitionState(LavaDragsState.WAITING_VETION);
            turnOffPrayer();
        }
        if (player.isIdle())
        {
            MousePackets.queueClickPacket(0, 0);
            MovementPackets.sendMovement(vetionSafeSpot);
        }
    }

    StopWatch lastLoot = new StopWatch();

    void teleGrab(TileItem item)
    {
        var player = client.getLocalPlayer();
        var alchCheck = player.getAnimation() == 713;
        if (!config.enableAlching())
            alchCheck = true;
        if (alchCheck || lastLoot.getTime() > 10000)
        {
            lastLoot.reset();
            Magic.selectSpell(Regular.TELEKINETIC_GRAB);
            MousePackets.queueClickPacket(0, 0);
            client.invokeMenuAction(
                    "",
                    "",
                    item.getId(),
                    MenuAction.SPELL_CAST_ON_GROUND_ITEM.getId(),
                    item.getTile().getSceneLocation().getX(),
                    item.getTile().getSceneLocation().getY()
            );
        }
    }

    public void putItemInLootingBag(Item item, Item bag)
    {
        client.setSelectedItemWidget(item.getWidgetId());
        client.setSelectedItemSlot(item.getSlot());
        client.setSelectedItemID(item.getId());
        MousePackets.queueClickPacket(0, 0);
        client.invokeMenuAction(
                "",
                "",
                bag.getId(),
                MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
                bag.getSlot(),
                bag.getWidgetId()
        );
    }

    public boolean handleLavaScales()
    {
        var scale = Inventory.getFirst(ItemID.LAVA_SCALE);
        var pestleAndMortar = Inventory.getFirst(ItemID.PESTLE_AND_MORTAR);
        if (scale != null && pestleAndMortar != null)
        {
            scale.useOn(pestleAndMortar);
            return true;
        }
        return false;
    }

    public boolean shouldAlchItem(Item item)
    {
        var composition = item.getComposition();
        var alchValue = composition.getHaPrice();
        var geValue = itemManager.getItemPrice(item.getId()) - 400;
        return alchValue > 800 && alchValue > geValue - 400;
    }

    public boolean shouldAlchItem(int item)
    {
        var composition = itemManager.getItemComposition(item);
        var alchValue = composition.getHaPrice();
        var geValue = itemManager.getItemPrice(item) - 400;
        return alchValue > 800 && alchValue > geValue - 400;
    }

    public boolean canAlch()
    {
        //Nature
        var nature = Inventory.contains(x -> x.getId() == ItemID.NATURE_RUNE && x.getQuantity() >= 1);
        var fire = Inventory.contains(x -> x.getId() == ItemID.FIRE_RUNE && x.getQuantity() >= 5);
        var shield = Equipment.fromSlot(EquipmentInventorySlot.SHIELD);
        var wep = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);
        var level = client.getRealSkillLevel(Skill.MAGIC) >= 55;
        if (shield != null && shield.getId() == ItemID.TOME_OF_FIRE)
            fire = true;
        if (wep != null && wep.getId() == ItemID.STAFF_OF_FIRE)
            fire = true;
        return nature && fire && level;
    }

    public boolean handleHighAlch()
    {
        if (Dialog.isOpen())
        {
            if (Dialog.isViewingOptions())
                Dialog.chooseOption(1);
            if (Dialog.canContinue())
                Dialog.continueSpace();
            return true;
        }
        if (canAlch())
        {
            var itemToAlch = Inventory.getFirst(this::shouldAlchItem);
            if (itemToAlch != null)
            {
                Magic.cast(Regular.HIGH_LEVEL_ALCHEMY, itemToAlch);
                return true;
            }
        }
        return false;
    }

    int alchCycle = 0;
    boolean shouldAlchFlag = true;
    boolean lavaDragAttacked = false;

    private void doLooting()
    {
        var player = client.getLocalPlayer();
        if (!player.getWorldLocation().equals(fightingSpot))
        {
            MousePackets.queueClickPacket(0, 0);
            Movement.setDestination(fightingSpot.getX(), fightingSpot.getY());
            return;
        }
        var lavaDrag = NPCs.getNearest(
                x -> x.getId() == NpcID.LAVA_DRAGON && lavaDragonTargetArea.contains(x) && !x.isDead()
        );
        handleAttackStyle(lavaDrag);
        if (lavaDrag != null && !Objects.equals(lavaDrag.getInteracting(), player))
        {
            attackDragon();
            return;
        }
        if (config.enableAlching())
        {
            if (tryAlch())
                return;
        }
        if (Inventory.getFreeSlots() < 2)
        {
            if (lootingBagTries > 4 || lootingBagFull)
            {
                if (!Inventory.isFull())
                {
                    var loot = getLoot();
                    if (loot.isEmpty())
                    {
                        transitionState(LavaDragsState.LEAVING);
                        return;
                    }
                    var itemToLoot = loot.stream().max(Comparator.comparingInt(x -> x.getQuantity() * itemManager.getItemPrice(x.getId())));
                    if (player.getAnimation() != 723)
                        teleGrab(itemToLoot.get());
                    return;
                }
                transitionState(LavaDragsState.LEAVING);
                return;
            }
            var lootBag = Inventory.getFirst("Looting bag");
            var loot = Inventory.getFirst(ItemID.LAVA_DRAGON_BONES);
            if (lootBag != null && loot != null)
            {
                //loot.useOn(client.getWidget(lootBag.getWidgetId()));
                putItemInLootingBag(loot, lootBag);
                var loot2 = Inventory.getFirst(ItemID.BLACK_DRAGONHIDE);
                if (loot2 != null)
                    putItemInLootingBag(loot2, lootBag);
                lootingBagTries++;
                return;
            }
        }
        var loot = getLoot();
        if (loot.isEmpty())
        {
            if (!handleLavaScales() && !handleJavelins() && !handleHighAlch())
                transitionState(LavaDragsState.FIGHTING);
            return;
        }
        if (stateTimer.getTime(TimeUnit.SECONDS) > 90)
        {
            halt();
            sendChatMessage("LOOT timeout, shutting down");
            return;
        }
        var itemToLoot = loot.stream().max(Comparator.comparingInt(x -> x.getQuantity() * itemManager.getItemPrice(x.getId())));
        teleGrab(itemToLoot.get());
    }

    Random random = new Random();

    private boolean shouldAlch()
    {
        if (alchCycle > 0)
        {
            alchCycle--;
            return false;
        } else if (alchCycle == 0)
        {
            alchedLastTick = true;
            alchCycle--;
            return true;
        } else if (alchCycle < 2)
            alchCycle = 0;
        return false;
    }

    private boolean handleJavelins()
    {
        Item item;
        if ((item = Inventory.getFirst(ItemID.RUNE_JAVELIN)) == null)
        {
            return false;
        } else
        {
            item.interact("Wield");
            return true;
        }
    }

    int lastNatureRunes = 0;

    private List<TileItem> getLoot()
    {
        return TileItems.getAll(x ->
                lavaDragonTargetArea.contains(x) && itemManager.getItemPrice(x.getId()) * x.getQuantity() > 1000
        );
    }

    private void attackDragon()
    {
        var player = client.getLocalPlayer();
        var lavaDrag = NPCs.getNearest(
                x -> x.getId() == NpcID.LAVA_DRAGON && lavaDragonTargetArea.contains(x) && !x.isDead() && x.distanceTo(player) <= 9
        );
        handleAttackStyle(lavaDrag);
        if (lavaDrag != null && (!Objects.equals(player.getInteracting(), lavaDrag) || player.isIdle() || alchedLastTick || Dialog.isOpen()))
        {
            alchedLastTick = false;
            lavaDrag.interact("Attack");
            currentTarget = lavaDrag;
        }
    }


    private void doFighting()
    {
        var player = client.getLocalPlayer();
        if (!generalArea.contains(player))
        {
            logger.info("Fighting state in incorrect area! Shutting down.");
            halt();
        }
        if (currentTarget != null && currentTarget.isDead())
        {
            killCount++;
            var tile = Tiles.getAt(currentTarget.getLocalLocation()).getSceneLocation();
            currentTarget = null;
        }
        if (stateTimer.getTime(TimeUnit.MINUTES) > 2)
        {
            sendChatMessage("FIGHTING timeout, shutting down");
            halt();
        }
        //Move to right spot
        if (!player.getWorldLocation().equals(fightingSpot))
        {
            MousePackets.queueClickPacket(0, 0);
            Movement.setDestination(fightingSpot.getX(), fightingSpot.getY());
            return;
        } else
        {
            if (Combat.getMissingHealth() > 15)
            {
                var shark = Inventory.getFirst(FOOD_ITEM_ID);
                if (shark != null)
                {
                    shark.interact("Eat");
                    return;
                }
            }
            if (client.getBoostedSkillLevel(Skill.MAGIC) == client.getRealSkillLevel(Skill.MAGIC))
            {
                var pot = Inventory.getFirst(
                        x -> x.getName().toLowerCase().contains("magic potion")
                );
                if (pot != null)
                {
                    if (pot.getName().contains("Divine"))
                    {
                        if (Combat.getCurrentHealth() >= 20)
                            pot.interact("Drink");
                    } else
                    {
                        pot.interact("Drink");
                    }
                    return;
                }
            }
            if (config.enableAlching())
            {
                if (tryAlch())
                    return;
            }
            attackDragon();
        }
    }

    private boolean tryAlch()
    {
        if (shouldAlch())
        {
            if (random.nextInt(10) == 0)
                return true;
            if (Inventory.contains(alchItem) && Inventory.contains(ItemID.NATURE_RUNE))
            {
                Magic.cast(Regular.HIGH_LEVEL_ALCHEMY, Inventory.getFirst(alchItem));
                return true;
            }
        }
        return false;
    }

    private void handleAttackStyle(NPC lavaDrag)
    {
        if (lavaDrag == null)
            return;
        var weapon = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);
        if (weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS || weapon.getId() == ItemID.TRIDENT_OF_THE_SWAMP)
        {
            if (lavaDrag.distanceTo(client.getLocalPlayer()) < 6)
            {
                if (Combat.getAttackStyle() != Combat.AttackStyle.FIRST)
                    Combat.setAttackStyle(Combat.AttackStyle.FIRST);
            } else
            {
                if (Combat.getAttackStyle() != Combat.AttackStyle.FOURTH)
                    Combat.setAttackStyle(Combat.AttackStyle.FOURTH);
            }
        }
    }

    private void hopWorlds()
    {
        Worlds.hopTo(Worlds.getRandom(
                        x -> x.getId() != Worlds.getCurrentId() && x.isNormal() && x.isMembers()
                ), false
        );
    }


    ImmutableSet<Integer> coinIDs = ImmutableSet.of(
            ItemID.COINS,
            ItemID.COINS_995,
            ItemID.COINS_6964,
            ItemID.COINS_8890
    );

    private int getTridentCost()
    {
        int[] items = new int[]{
                ItemID.DEATH_RUNE,
                ItemID.FIRE_RUNE,
                ItemID.CHAOS_RUNE,
                ItemID.ZULRAHS_SCALES
        };
        int[] quantities = new int[]{1, 5, 1, 1};
        int total = 0;
        for (int i = 0; i < items.length; i++)
        {
            total += itemManager.getItemPrice(items[i]) * quantities[i];
        }
        return total * tridentCharges;
    }

    public int getTotalProfit()
    {
        if (state != LavaDragsState.BANKING)
            return getInventoryValue() - getTridentCost() - startGold + previousTripsProfit;
        else
            return previousTripsProfit - getTridentCost();
    }

    public int getInventoryValue()
    {
        return inventoryItemsValue;
    }

    @Subscribe
    public void onItemObtained(ItemObtained event)
    {
        if (state == LavaDragsState.FIGHTING || state == LavaDragsState.LOOTING)
        {
            //IGNORED ITEMS
            ImmutableSet ignoredItems = ImmutableSet.of(
                    ItemID.LAVA_SCALE,
                    ItemID.DIVINE_MAGIC_POTION1,
                    ItemID.DIVINE_MAGIC_POTION2,
                    ItemID.DIVINE_MAGIC_POTION3,
                    ItemID.DIVINE_MAGIC_POTION4
            );
            if (ignoredItems.contains(event.getItemId()))
                return;
            if (shouldAlchItem(event.getItemId()))
                return;
            var price = itemManager.getItemPrice(event.getItemId());
            inventoryItemsValue += price * event.getAmount();
        }
    }

    StopWatch lastInventoryChange = new StopWatch();

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        var newItems = event.getItemContainer().getItems();
        for (var item : newItems)
        {
            if (item.getId() == ItemID.NATURE_RUNE)
            {
                var count = item.getQuantity();
                if (count != lastNatureRunes)
                {
                    lastNatureRunes = count;
                    shouldAlchFlag = false;
                    alchCycle = random.nextInt(4) + 1;
                }
            }
        }
        if (state == LavaDragsState.BANKING)
        {
            lastInventoryChange.reset();
        }
        previousContainerState = event.getItemContainer();
    }

    int tridentCharges = 0;

    @Subscribe
    public void onInteractingChanged(ExperienceGained event)
    {
        if (event.getSkill() == Skill.HITPOINTS)
        {
            tridentCharges++;
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event)
    {
        if (vetionIDs.contains(event.getSource().getId()) && Objects.equals(event.getTarget(), client.getLocalPlayer()))
        {
            if (state != LavaDragsState.LEAVING)
            {
                state = LavaDragsState.FLEEING_VETION;
                GameThread.invoke(() ->
                {
                    //Movement.setDestination(vetionSafeSpot.getX(),vetionSafeSpot.getY());
                    MousePackets.queueClickPacket(0, 0);
                    MovementPackets.sendMovement(vetionSafeSpot);
                });
            }
        }
    }

    private void sendChatMessage(final String message)
    {
        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(message)
                .build());
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned)
    {
        if (state != LavaDragsState.FIGHTING)
            return;
        TileItem item = itemSpawned.getItem();
        if (lavaDragonTargetArea.contains(item))
        {
            GameThread.invoke(() -> teleGrab(item));
        }
        transitionState(LavaDragsState.LOOTING);
    }

    int numCharges = -1;

    @Subscribe
    public void onChatMessage(ChatMessage message)
    {
        if (message.getMessage().equals("The bag's too full."))
        {
            lootingBagFull = true;
        }
        if (message.getType() != ChatMessageType.GAMEMESSAGE)
            return;
        if (message.getMessage().toLowerCase().contains("out of charges"))
        {
            halt();
        }
        //Check charges
        Pattern pattern = Pattern.compile("Your weapon has ([\\d,]+) charges.");
        Matcher matcher = pattern.matcher(message.getMessage());
        if (matcher.matches())
        {
            var numChargesString = matcher.group(1);
            try
            {
                numCharges = NumberFormat.getNumberInstance(java.util.Locale.US).parse(numChargesString).intValue();
                logger.info("NUM CHARGES: " + numCharges);
            } catch (ParseException e)
            {
            }
        }
    }

    enum LavaDragsState
    {
        FIGHTING,
        FLEEING_VETION,
        LOOTING,
        WAITING_VETION,
        BANKING, GOING, LEAVING
    }
}
