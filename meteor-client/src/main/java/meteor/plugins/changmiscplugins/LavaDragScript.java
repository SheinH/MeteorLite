package meteor.plugins.changmiscplugins;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import meteor.chat.ChatMessageManager;
import meteor.chat.QueuedMessage;
import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.commons.Time;
import meteor.plugins.api.coords.Area;
import meteor.plugins.api.coords.RectangularArea;
import meteor.plugins.api.entities.NPCs;
import meteor.plugins.api.entities.Players;
import meteor.plugins.api.entities.TileItems;
import meteor.plugins.api.game.Combat;
import meteor.plugins.api.game.Game;
import meteor.plugins.api.game.GameThread;
import meteor.plugins.api.game.Worlds;
import meteor.plugins.api.items.Bank;
import meteor.plugins.api.items.Equipment;
import meteor.plugins.api.items.Inventory;
import meteor.plugins.api.magic.Magic;
import meteor.plugins.api.magic.Regular;
import meteor.plugins.api.movement.Movement;
import meteor.plugins.api.packets.*;
import meteor.plugins.api.widgets.Dialog;
import meteor.plugins.api.widgets.Prayers;
import meteor.ui.overlay.OverlayManager;
import meteor.util.PvPUtil;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.queries.WallObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import org.apache.commons.lang3.time.StopWatch;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "Chang Lava Drags",
        description = "Farms lava drags,",
        enabledByDefault = false
)
public class LavaDragScript extends Plugin {
    //static final WorldPoint fightingSpot = new WorldPoint(3205, 3803, 0);
    static final WorldPoint fightingSpot = new WorldPoint(3200, 3807, 0);
    static final WorldPoint vetionSafeSpot = new WorldPoint(3184, 3801, 0);
    static final Area generalArea = new RectangularArea(3175, 3793, 3223, 3808);
    //static final Area lavaDragonTargetArea = new RectangularArea(3204, 3805, 3214, 3814);
    static final Area lavaDragonTargetArea = new RectangularArea(3197, 3817, 3204, 3810);
    static final Area geArea = new RectangularArea(3137, 3518, 3194, 3467);
    private static final WorldPoint walkBackPathPoint = new WorldPoint(3200, 3801, 0);
    ImmutableSet<Integer> vetionIDs = ImmutableSet.of(
            NpcID.VETION,
            NpcID.VETION_REBORN
    );
    LavaDragsState state = LavaDragsState.FIGHTING;
    StopWatch stateTimer = new StopWatch();
    int totalGEValue;
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
    LavaDragOverlay overlay;
    StopWatch runTime = new StopWatch();
    int lootingBagTries = 0;
    NPC currentTarget;
    boolean lootingBagFull;
    private Future<?> bankTask;
    int bankTries = 0;

    void transitionState(LavaDragsState newState) {
        state = newState;
        if (newState == LavaDragsState.LOOTING) {
            lootingBagTries = 0;
            lavaDragAttacked = false;
        }
        stateTimer.reset();
        stateTimer.start();
    }

    @Override
    public void startup() {
        var player = client.getLocalPlayer();
        if (player == null || !Area.union(generalArea,geArea).contains(player)) {
            toggle(false);
            sendChatMessage("Invalid start state!");
        }
        state = LavaDragsState.FIGHTING;
        if(Bank.isOpen()){
            state = LavaDragsState.BANKING;
        }
        stateTimer.reset();
        stateTimer.start();
        runTime.reset();
        runTime.start();
        killCount = 0;
        overlayManager.add(overlay);
    }

    @Override
    public void shutdown() {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        switch (state) {
            case FIGHTING -> doFighting();
            case LOOTING -> doLooting();
            case FLEEING_VETION -> doFleeingVetion();
            case WAITING_VETION -> doWaitingVetion();
            case LEAVING -> doLeaving();
            case BANKING -> doBanking();
            case GOING -> doGoing();
        }
    }

    private void halt(){
        Widget logoutButton = client.getWidget(182, 8);
        Widget logoutDoorButton = client.getWidget(69, 23);
        int param1 = -1;
        if (logoutButton != null) {
            param1 = logoutButton.getId();
        } else if (logoutDoorButton != null) {
            param1 = logoutDoorButton.getId();
        }
        if (param1 == -1) {
            return;
        }
        int p1 = param1;
        MousePackets.queueClickPacket(0,0);
        GameThread.invoke(() -> client.invokeMenuAction(
                "Logout",
                "",
                1,
                MenuAction.CC_OP.getId(),
                -1,
                p1
        ));
        toggle(false);
    }

    private void flickPrayer(){
        boolean quickPrayer = client.getVar(Varbits.QUICK_PRAYER) == 1;
        if (quickPrayer) {
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
        }
        MousePackets.queueClickPacket(0, 0);
        WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
    }
    private void turnOffPrayer(){
        if (Prayers.isQuickPrayerEnabled()) {
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
        }
    }

    private void doGoing() {
        if(geArea.contains(Players.getLocal())){
            handleTP();
            return;
        }
        var corpCave = new RectangularArea(2953, 4405, 3008, 4366,2);
        if(corpCave.contains(Players.getLocal())){
            if(Dialog.isViewingOptions()){
                Dialog.chooseOption(1);
                return;
            }
            var exit = new WallObjectQuery()
                    .nameEquals("Cave exit")
                    .result(client)
                    .first();
            if(exit != null){
                exit.interact("Exit");
            }
            else{
                logger.info("Cave exit not found!");
            }
        }
        else {
            flickPrayer();
            if (!Movement.isWalking()) {
                Movement.walkTo(fightingSpot);
            }
            if (generalArea.contains(Players.getLocal())) {
                transitionState(LavaDragsState.FIGHTING);
                if (Prayers.isQuickPrayerEnabled()) {
                    MousePackets.queueClickPacket(0, 0);
                    WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
                }
                return;
            }
        }
    }

    private void withdraw(int itemID, int quantity){
        if(!Bank.contains(x -> x.getId() == itemID && x.getQuantity() >= quantity))
            return;
        if(quantity == 1){
            GameThread.invoke(() -> Bank.withdraw(itemID,quantity, Bank.WithdrawMode.ITEM));
            Time.sleepUntil(() -> Inventory.contains(x -> x.getId() == itemID),50,5000);
        }
        else{
            GameThread.invoke(() -> Bank.withdraw(itemID,quantity, Bank.WithdrawMode.ITEM));
            Time.sleepUntil(() -> Inventory.contains(x -> x.getId() == itemID && x.getQuantity() >= quantity),50,10000);
        }
    }

    private void bankItems(){
        var lootingBag = Inventory.getFirst(ItemID.LOOTING_BAG);

        if(lootingBag != null) {
                Time.sleepUntil(() -> client.getWidget(15, 8) != null, 200, 10000);
                var widget = client.getWidget(15, 8);
                if (widget == null) {
                    return;
                }
                GameThread.invoke(() -> widget.interact("Deposit loot"));
                logger.info("Dumping looting bag");
                Time.sleep(600);
        }


        GameThread.invoke(() -> Bank.depositInventory());
        Time.sleepUntil(() -> Inventory.isEmpty(),50,5000);
        Time.sleep(600);
        Bank.setQuantityMode(Bank.QuantityMode.ONE);
        Time.sleep(600);
        withdraw(ItemID.AIR_RUNE,80);
        withdraw(ItemID.LAW_RUNE,80);
        withdraw(ItemID.NATURE_RUNE,3);
        withdraw(ItemID.FIRE_RUNE,15);
        withdraw(ItemID.PESTLE_AND_MORTAR,1);
        withdraw(ItemID.LOOTING_BAG,1);
        withdraw(ItemID.SHARK,1);
        withdraw(ItemID.SHARK,1);
        withdraw(ItemID.SHARK,1);
        withdraw(ItemID.DIVINE_MAGIC_POTION4,1);
        withdraw(ItemID.DIVINE_MAGIC_POTION4,1);

        Predicate<Item> isGamesNeck = x -> x.getId() >= ItemID.GAMES_NECKLACE8 && x.getId() <= ItemID.GAMES_NECKLACE1 && x.getId() % 2 == 1;
        var neck = Bank.getFirst(isGamesNeck);
        if(neck != null){
            withdraw(neck.getId(),1);
        }

        handleROW();
        //Check ROW
        Time.sleep(2000);
        GameThread.invoke(() -> Game.getClient().runScript(138)); // closes the input dialog
        if(readyForAnotherTrip()) {
            transitionState(LavaDragsState.GOING);
            lootingBagFull = false;
        }
        else{
            bankTries++;
            if(bankTries > 3)
                halt();
        }
    }

    private boolean inventoryHasItemAmount(int itemID,int amount){
        var items = Inventory.getAll(itemID);
        int total = 0;
        for(var x : items){
            total += x.getQuantity();
        }
        return Math.max(total,items.size()) >= amount;
    }
    private boolean readyForAnotherTrip() {
        if(Inventory.isFull())
            return false;
        if(!inventoryHasItemAmount(ItemID.LAW_RUNE,80) || !inventoryHasItemAmount(ItemID.AIR_RUNE,80))
            return false;
        if(!inventoryHasItemAmount(ItemID.SHARK,3))
            return false;
        Predicate<Item> isGamesNeck = x -> x.getId() >= ItemID.GAMES_NECKLACE8 && x.getId() <= ItemID.GAMES_NECKLACE1 && x.getId() % 2 == 1;
        if(!Inventory.contains(isGamesNeck))
            return false;
        if(!Inventory.contains(ItemID.PESTLE_AND_MORTAR))
            return false;
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
        if(ring == null || !validRings.contains(ring.getId()))
            return false;
        numCharges = -1;

        var wep = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);
        wep.interact("Check");
        Time.sleepUntil(() -> numCharges != -1,50,2000);
        if(numCharges == -1 || numCharges < 500)
            return false;
        return true;
    }

    private void handleROW() {
        var oldRing = Equipment.fromSlot(EquipmentInventorySlot.RING);
        if(oldRing == null || oldRing.getId() == ItemID.RING_OF_WEALTH){
            withdraw(ItemID.RING_OF_WEALTH_5,1);
        }
        var ring = Inventory.getFirst(ItemID.RING_OF_WEALTH_5);
        if(ring != null){
            ItemPackets.queueBankItemActionPacket(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(),ring.getId(), ring.getSlot());
            Time.sleepUntil(() -> {
                var ringSlot = Equipment.fromSlot(EquipmentInventorySlot.RING);
                return ringSlot != null && ringSlot.getId() == ItemID.RING_OF_WEALTH_5;
            },50,4000);
            if(Inventory.contains(ItemID.RING_OF_WEALTH)){
                Bank.deposit(ItemID.RING_OF_WEALTH,1);
            }
        }
    }

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

    private void doBanking() {
        turnOffPrayer();
        if(Bank.isOpen()){
            if(bankTask == null || bankTask.isDone()) {
                bankTask = executor.submit(this::bankItems);
            }
            return;
        }
        if(stateTimer.getTime(TimeUnit.SECONDS) > 60){
            toggle(false);
            return;
        }
        if(client.getLocalPlayer().isIdle()){
            var banker = NPCs.getNearest("Banker");
            banker.interact("Bank");
        }
    }

    private void doLeaving() {
        flickPrayer();
        if (geArea.contains(client.getLocalPlayer())) {
            turnOffPrayer();
            transitionState(LavaDragsState.BANKING);
            return;
        }
        if (stateTimer.getTime(TimeUnit.MINUTES) >= 2) {
            halt();
            return;
        }
        final WorldPoint exitPoint = new WorldPoint(3205, 3740, 0);
        if (!Movement.isWalking()) {
            Movement.walkTo(exitPoint);
        }
        var ring = WidgetInfo.EQUIPMENT_RING;
        var ringWidget = client.getWidget(ring);
        if (PvPUtil.getWildernessLevelFrom(client.getLocalPlayer().getWorldLocation()) < 30){
            turnOffPrayer();
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.widgetAction(ringWidget, "Grand Exchange");
        }
    }

    private void doWaitingVetion() {
        turnOffPrayer();
        if (stateTimer.getTime(TimeUnit.SECONDS) >= 10) {
            var player = client.getLocalPlayer();
            if (player.getWorldLocation().equals(walkBackPathPoint)) {
                transitionState(LavaDragsState.FIGHTING);
                return;
            }
            if (player.isIdle()) {
                MousePackets.queueClickPacket(0, 0);
                MovementPackets.sendMovement(walkBackPathPoint);
            }
        }
    }

    private void doFleeingVetion() {
        flickPrayer();
        if (state == LavaDragsState.LEAVING)
            return;
        var player = client.getLocalPlayer();
        if (player.getWorldLocation().equals(vetionSafeSpot)) {
            transitionState(LavaDragsState.WAITING_VETION);
            turnOffPrayer();
        }
        if (player.isIdle()) {
            MousePackets.queueClickPacket(0, 0);
            MovementPackets.sendMovement(vetionSafeSpot);
        }
    }

    void teleGrab(TileItem item) {
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

    public void putItemInLootingBag(Item item, Item bag) {
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

    public boolean handleLavaScales() {
        var scale = Inventory.getFirst(ItemID.LAVA_SCALE);
        var pestleAndMortar = Inventory.getFirst(ItemID.PESTLE_AND_MORTAR);
        if (scale != null && pestleAndMortar != null) {
            scale.useOn(pestleAndMortar);
            return true;
        }
        return false;
    }

    public boolean shouldAlchItem(Item item) {
        var composition = item.getComposition();
        var alchValue = composition.getHaPrice();
        var geValue = itemManager.getItemPrice(item.getId()) - 400;
        return alchValue > 800 && alchValue > geValue - 400;
    }

    public boolean canAlch(){
        //Nature
        var nature = Inventory.contains(x -> x.getId() == ItemID.NATURE_RUNE && x.getQuantity() >= 1);
        var fire = Inventory.contains(x -> x.getId() == ItemID.FIRE_RUNE && x.getQuantity() >= 5);
        var shield = Equipment.fromSlot(EquipmentInventorySlot.SHIELD);
        var wep = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);
        if(shield != null && shield.getId() == ItemID.TOME_OF_FIRE)
            fire = true;
        if(wep != null && wep.getId() == ItemID.STAFF_OF_FIRE)
            fire = true;
        return nature && fire;
    }
    public boolean handleHighAlch() {
        if(Dialog.isOpen()){
            if(Dialog.isViewingOptions())
                Dialog.chooseOption(1);
            if(Dialog.canContinue())
                Dialog.continueSpace();
            return true;
        }
        if(canAlch()){
            var itemToAlch = Inventory.getFirst(this::shouldAlchItem);
            if (itemToAlch != null) {
                Magic.cast(Regular.HIGH_LEVEL_ALCHEMY, itemToAlch);
                return true;
            }
        }
        return false;
    }

    boolean lavaDragAttacked = false;
    private void doLooting() {
        var player = client.getLocalPlayer();
        if (!player.getWorldLocation().equals(fightingSpot)) {
            MousePackets.queueClickPacket(0, 0);
            Movement.setDestination(fightingSpot.getX(), fightingSpot.getY());
            return;
        }
        var lavaDrag = NPCs.getNearest(
                x -> x.getId() == NpcID.LAVA_DRAGON && lavaDragonTargetArea.contains(x) && !x.isDead()
        );
        handleAttackStyle(lavaDrag);
        if(lavaDrag != null && !Objects.equals(lavaDrag.getInteracting(),player)){
            attackDragon();
            return;
        }
        if (Inventory.getFreeSlots() < 2) {
            if (lootingBagTries > 4 || lootingBagFull) {
                if(!Inventory.isFull()) {
                    var loot = getLoot();
                    if (loot.isEmpty()) {
                        transitionState(LavaDragsState.LEAVING);
                        return;
                    }
                    var itemToLoot = loot.stream().max(Comparator.comparingInt(x -> x.getQuantity() * itemManager.getItemPrice(x.getId())));
                    teleGrab(itemToLoot.get());
                    return;
                }
                transitionState(LavaDragsState.LEAVING);
                return;
            }
            var lootBag = Inventory.getFirst("Looting bag");
            var loot = Inventory.getFirst(ItemID.LAVA_DRAGON_BONES);
            if (lootBag != null && loot != null) {
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
        if (loot.isEmpty()) {
            if (!handleLavaScales() && !handleHighAlch())
                transitionState(LavaDragsState.FIGHTING);
            return;
        }
        if (stateTimer.getTime(TimeUnit.SECONDS) > 90) {
            halt();
            sendChatMessage("LOOT timeout, shutting down");
            return;
        }
        var itemToLoot = loot.stream().max(Comparator.comparingInt(x -> x.getQuantity() * itemManager.getItemPrice(x.getId())));
        teleGrab(itemToLoot.get());
    }

    private List<TileItem> getLoot() {
        return TileItems.getAll(x ->
                lavaDragonTargetArea.contains(x) && itemManager.getItemPrice(x.getId()) * x.getQuantity() > 1000
        );
    }

    private void attackDragon(){
        var player = client.getLocalPlayer();
        var lavaDrag = NPCs.getNearest(
                x -> x.getId() == NpcID.LAVA_DRAGON && lavaDragonTargetArea.contains(x) && !x.isDead()
        );
        handleAttackStyle(lavaDrag);
        if (lavaDrag != null && (!Objects.equals(player.getInteracting(), lavaDrag) || player.isIdle() || Dialog.isOpen())) {
            lavaDrag.interact("Attack");
            currentTarget = lavaDrag;
        }
    }

    private void doFighting() {
        var player = client.getLocalPlayer();
        if(!generalArea.contains(player)){
            logger.info("Fighting state in incorrect area! Shutting down.");
            halt();
        }
        if (currentTarget != null && currentTarget.isDead()) {
            killCount++;
            currentTarget = null;
        }
        //Equipment
        var loot = getLoot();
        if (!loot.isEmpty()) {
            transitionState(LavaDragsState.LOOTING);
        }
        if (stateTimer.getTime(TimeUnit.MINUTES) > 2) {
            sendChatMessage("FIGHTING timeout, shutting down");
            halt();
        }
        //Move to right spot
        if (!player.getWorldLocation().equals(fightingSpot)) {
            MousePackets.queueClickPacket(0, 0);
            Movement.setDestination(fightingSpot.getX(), fightingSpot.getY());
            return;
        } else {
            if(Combat.getMissingHealth() > 20){
                var shark = Inventory.getFirst("Shark");
                if(shark != null) {
                    shark.interact("Eat");
                    return;
                }

            }
            if (client.getBoostedSkillLevel(Skill.MAGIC) == client.getRealSkillLevel(Skill.MAGIC)) {
                var pot = Inventory.getFirst(
                        x -> x.getName().toLowerCase().contains("magic potion")
                );
                if (pot != null) {
                    if(pot.getName().contains("Divine")){
                       if(Combat.getCurrentHealth() >= 20)
                           pot.interact("Drink");
                    }
                    else{
                        pot.interact("Drink");
                    }
                    return;
                }
            }
            attackDragon();
        }
    }

    private void handleAttackStyle(NPC lavaDrag) {
        if(lavaDrag == null)
            return;
        var weapon = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);
        if(weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS || weapon.getId() == ItemID.TRIDENT_OF_THE_SWAMP) {
            if (lavaDrag.distanceTo(client.getLocalPlayer()) < 6) {
                    Combat.setAttackStyle(Combat.AttackStyle.FIRST);
            } else {
                if(Combat.getAttackStyle() != Combat.AttackStyle.FOURTH)
                    Combat.setAttackStyle(Combat.AttackStyle.FOURTH);
            }
        }
    }

    private void hopWorlds() {
        Worlds.hopTo(Worlds.getRandom(
                        x -> x.getId() != Worlds.getCurrentId() && x.isNormal() && x.isMembers()
                ), false
        );
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        var newItems = event.getItemContainer().getItems();
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (vetionIDs.contains(event.getSource().getId()) && Objects.equals(event.getTarget(), client.getLocalPlayer())) {
            if(state != LavaDragsState.LEAVING)
                state = LavaDragsState.FLEEING_VETION;
            GameThread.invoke(() -> {
                //Movement.setDestination(vetionSafeSpot.getX(),vetionSafeSpot.getY());
                MousePackets.queueClickPacket(0, 0);
                MovementPackets.sendMovement(vetionSafeSpot);
            });
        }
    }

    private void sendChatMessage(final String message) {
        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(message)
                .build());
    }

    int numCharges = -1;
    @Subscribe
    public void onChatMessage(ChatMessage message) {
        if (message.getMessage().equals("The bag's too full.")) {
            lootingBagFull = true;
        }
        if(message.getType() != ChatMessageType.GAMEMESSAGE)
            return;
        if(message.getMessage().toLowerCase().contains("out of charges")){
            halt();
        }
        //Check charges
        Pattern pattern = Pattern.compile("Your weapon has ([\\d,]+) charges.");
        Matcher matcher = pattern.matcher(message.getMessage());
        if(matcher.matches()){
            var numChargesString = matcher.group(1);
            try {
                numCharges= NumberFormat.getNumberInstance(java.util.Locale.US).parse(numChargesString).intValue();
                logger.info("NUM CHARGES: " + numCharges);
            } catch (ParseException e) {
            }
        }
    }

    enum LavaDragsState {
        FIGHTING,
        FLEEING_VETION,
        LOOTING,
        WAITING_VETION,
        BANKING, GOING, LEAVING
    }
}
