package meteor.plugins.changmiscplugins;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import meteor.chat.ChatMessageManager;
import meteor.chat.QueuedMessage;
import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.coords.Area;
import meteor.plugins.api.coords.RectangularArea;
import meteor.plugins.api.entities.NPCs;
import meteor.plugins.api.entities.TileItems;
import meteor.plugins.api.game.GameThread;
import meteor.plugins.api.game.Worlds;
import meteor.plugins.api.items.Bank;
import meteor.plugins.api.items.Inventory;
import meteor.plugins.api.magic.Magic;
import meteor.plugins.api.magic.Regular;
import meteor.plugins.api.movement.Movement;
import meteor.plugins.api.movement.pathfinder.RuneRequirement;
import meteor.plugins.api.packets.ItemPackets;
import meteor.plugins.api.packets.MousePackets;
import meteor.plugins.api.packets.MovementPackets;
import meteor.plugins.api.packets.WidgetPackets;
import meteor.util.Timer;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@PluginDescriptor(
        name = "Chang Lava Drags",
        description = "Farms lava drags,",
        enabledByDefault = false
)
public class LavaDragScript extends Plugin {
    static final WorldPoint fightingSpot = new WorldPoint(3205,3803,0);
    static final WorldPoint vetionSafeSpot = new WorldPoint(3184,3801,0);
    static final Area generalArea = new RectangularArea(3175, 3793, 3223, 3808);
    static final Area lavaDragonTargetArea = new RectangularArea(3204, 3805, 3214, 3814);

    enum LavaDragsState{
        FIGHTING,
        FLEEING_VETION,
        LOOTING,
        WAITING_VETION
    }
    ImmutableSet<Integer> vetionIDs = ImmutableSet.of(
           NpcID.VETION,
           NpcID.VETION_REBORN
    );
    LavaDragsState state = LavaDragsState.FIGHTING;
    Timer stateTimer = new Timer();

    void transitionState(LavaDragsState newState){
        stateTimer.reset();
        state = newState;
        if(newState == LavaDragsState.LOOTING)
            lootingBagTries = 0;
    }

    @Inject
    ChatMessageManager chatMessageManager;
    @Inject
    ItemManager itemManager;

    @Override
    public void startup() {
        var player = client.getLocalPlayer();
        if(player == null || !generalArea.contains(player)){
            toggle(false);
            sendChatMessage("Invalid start state!");
        }
        stateTimer.reset();
    }

    @Subscribe
    public void onGameTick(GameTick event){
        switch(state){
            case FIGHTING -> doFighting();
            case LOOTING -> doLooting();
            case FLEEING_VETION -> doFleeingVetion();
            case WAITING_VETION -> doWaitingVetion();
        }
    }

    private static final WorldPoint walkBackPathPoint = new WorldPoint(3200 ,3801,0);
    private void doWaitingVetion() {
        if(stateTimer.getSecondsFromStart() >= 10){
            var player = client.getLocalPlayer();
            if(player.getWorldLocation().equals(walkBackPathPoint)) {
                transitionState(LavaDragsState.FIGHTING);
                return;
            }
            if(player.isIdle()) {
                MousePackets.queueClickPacket(0, 0);
                MovementPackets.sendMovement(walkBackPathPoint);
            }
        }
    }

    private void doFleeingVetion() {
        var player = client.getLocalPlayer();
        if(player.getWorldLocation().equals(vetionSafeSpot)){
            transitionState(LavaDragsState.WAITING_VETION);
        }
        if(player.isIdle()){
            MousePackets.queueClickPacket(0,0);
            MovementPackets.sendMovement(vetionSafeSpot);
        }
    }

    void teleGrab(TileItem item){
        Magic.selectSpell(Regular.TELEKINETIC_GRAB);
        MousePackets.queueClickPacket(0,0);
        client.invokeMenuAction(
                "",
                "",
                item.getId(),
                MenuAction.SPELL_CAST_ON_GROUND_ITEM.getId(),
                item.getTile().getSceneLocation().getX(),
                item.getTile().getSceneLocation().getY()
        );
    }

    int lootingBagTries = 0;

    public void putItemInLootingBag(Item item, Item bag){
        client.setSelectedItemWidget(item.getWidgetId());
        client.setSelectedItemSlot(item.getSlot());
        client.setSelectedItemID(item.getId());
        MousePackets.queueClickPacket(0,0);
        client.invokeMenuAction(
                "",
                "",
                bag.getId(),
                MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
                bag.getSlot(),
                bag.getWidgetId()
        );
    }

    public boolean handleLavaScales(){
        var scale = Inventory.getFirst(ItemID.LAVA_SCALE);
        var pestleAndMortar = Inventory.getFirst(ItemID.PESTLE_AND_MORTAR);
        if(scale!= null && pestleAndMortar != null) {
            scale.useOn(pestleAndMortar);
            return true;
        }
        return false;
    }

    public boolean shouldAlchItem(Item item){
        var composition = item.getComposition();
        var alchValue = composition.getHaPrice();
        var geValue = itemManager.getItemPrice(item.getId()) - 400;
        if(alchValue > 800 && alchValue > geValue - 400){
            return true;
        }
        return false;
    }

    public boolean handleHighAlch(){
        if(Inventory.contains(
                x -> x.getId() == ItemID.FIRE_RUNE && x.getQuantity() >= 5
        ) && Inventory.contains(
                x -> x.getId() == ItemID.LAW_RUNE && x.getQuantity() >= 5
        )) {
            var itemToAlch = Inventory.getFirst(this::shouldAlchItem);
            if (itemToAlch != null) {
                Magic.cast(Regular.HIGH_LEVEL_ALCHEMY,itemToAlch);
                return true;
            }
        }
        return false;
    }

    private void doLooting() {
        var player = client.getLocalPlayer();
        if(!player.getWorldLocation().equals(fightingSpot)){
            MousePackets.queueClickPacket(0,0);
            Movement.setDestination(fightingSpot.getX(),fightingSpot.getY());
            return;
        }
        if(Inventory.isFull()){
            if(lootingBagTries > 4){
                toggle();
                sendChatMessage("Looting bag and Inventory full. Shutting down.");
            }
            var lootBag = Inventory.getFirst("Looting bag");
            var loot = Inventory.getFirst(
                    ItemID.LAVA_DRAGON_BONES,
                    ItemID.BLACK_DRAGONHIDE,
                    ItemID.LAVA_SCALE
            );
            if(lootBag != null && loot != null){
                //loot.useOn(client.getWidget(lootBag.getWidgetId()));
                putItemInLootingBag(loot,lootBag);
                lootingBagTries++;
                return;
            }
        }
        var loot = getLoot();
        if(loot.isEmpty()) {
            if(!handleLavaScales() && !handleHighAlch())
                transitionState(LavaDragsState.FIGHTING);
            return;
        }
        if(stateTimer.getSecondsFromStart() > 90){
            toggle();
            sendChatMessage("LOOT timeout, shutting down");
            return;
        }
        var itemToLoot = loot.stream().max(Comparator.comparingInt(x -> x.getQuantity() * itemManager.getItemPrice(x.getId())));
        teleGrab(itemToLoot.get());
    }

    private List<TileItem> getLoot(){
        return TileItems.getAll(x ->
                lavaDragonTargetArea.contains(x) && itemManager.getItemPrice(x.getId()) * x.getQuantity() > 1000
                );
    }
    private void doFighting() {
        var loot = getLoot();
        if(!loot.isEmpty()){
            transitionState(LavaDragsState.LOOTING);
        }
        if(stateTimer.getMinutesFromStart() > 2){
            sendChatMessage("FIGHTING timeout, shutting down");
            toggle();
        }
        //Move to right spot
        var player = client.getLocalPlayer();
        if(!player.getWorldLocation().equals(fightingSpot)){
            MousePackets.queueClickPacket(0,0);
            Movement.setDestination(fightingSpot.getX(),fightingSpot.getY());
            return;
        }
        else{
            if(client.getBoostedSkillLevel(Skill.MAGIC) == client.getRealSkillLevel(Skill.MAGIC)){
                var pot = Inventory.getFirst(
                        x -> x.getName().toLowerCase().contains("magic potion")
                );
                if(pot != null){
                    pot.interact("Drink");
                }
            }
            var lavaDrag = NPCs.getNearest(
                    x -> x.getId() == NpcID.LAVA_DRAGON && lavaDragonTargetArea.contains(x) && !x.isDead()
            );
            if(lavaDrag != null && (!Objects.equals(player.getInteracting(),lavaDrag) || player.isIdle())){
				lavaDrag.interact("Attack");
            }
        }
    }

    private void hopWorlds(){
        Worlds.hopTo(Worlds.getRandom(
                x -> x.getId() != Worlds.getCurrentId() && x.isNormal() && x.isMembers()
                ), false
        );
    }
    @Subscribe
    public void onInteractingChanged(InteractingChanged event){
        if(vetionIDs.contains(event.getSource().getId()) && Objects.equals(event.getTarget(),client.getLocalPlayer())){
            state = LavaDragsState.FLEEING_VETION;
            GameThread.invoke(() -> {
                //Movement.setDestination(vetionSafeSpot.getX(),vetionSafeSpot.getY());
                MousePackets.queueClickPacket(0,0);
                MovementPackets.sendMovement(vetionSafeSpot);
            });
        }
    }
    private void sendChatMessage(final String message)
    {
        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(message)
                .build());
    }

    boolean lootingBagFull;
    @Subscribe
    public void onChatMessage(ChatMessage message){
        if(message.getMessage().equals("The bag's too full!")){
           lootingBagFull = true;
        }
    }
}
