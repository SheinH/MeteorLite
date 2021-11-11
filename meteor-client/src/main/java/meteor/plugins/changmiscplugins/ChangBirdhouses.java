package meteor.plugins.changmiscplugins;

import lombok.AllArgsConstructor;
import meteor.callback.ClientThread;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.commons.Time;
import meteor.plugins.api.entities.TileObjects;
import meteor.plugins.api.game.Game;
import meteor.plugins.api.game.GameThread;
import meteor.plugins.api.game.Skills;
import meteor.plugins.api.input.Mouse;
import meteor.plugins.api.items.Inventory;
import meteor.plugins.api.packets.MousePackets;
import meteor.plugins.api.widgets.Dialog;
import meteor.util.Timer;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Chang's Birdhouse Plugin",
        description = "Speed up your birdhouse runs.",
        enabledByDefault = false
)
public class ChangBirdhouses extends Plugin {

    private BirdhouseType birdhouseType;

    @AllArgsConstructor
    enum BirdhouseType {
        REGULAR(ItemID.LOGS, ItemID.BIRD_HOUSE, 5,5),
        OAK(ItemID.OAK_LOGS, ItemID.OAK_BIRD_HOUSE, 15,14),
        WILLOW(ItemID.WILLOW_LOGS, ItemID.WILLOW_BIRD_HOUSE, 25,24),
        TEAK(ItemID.TEAK_LOGS, ItemID.TEAK_BIRD_HOUSE, 35,34),
        MAPLE(ItemID.MAPLE_LOGS, ItemID.MAPLE_BIRD_HOUSE, 45,44),
        MAHAOGANY(ItemID.MAHOGANY_LOGS, ItemID.MAHOGANY_BIRD_HOUSE, 50,49),
        YEW(ItemID.YEW_LOGS, ItemID.YEW_BIRD_HOUSE, 60,59),
        MAGIC(ItemID.MAGIC_LOGS, ItemID.MAGIC_BIRD_HOUSE, 75,74),
        REDWOOD(ItemID.REDWOOD_LOGS, ItemID.REDWOOD_BIRD_HOUSE, 90,89);
        int logID, birdhouseID, craftingLevelReq, hunterLevelReq;

        boolean canMake(){
            var hunterLevel = Skills.getLevel(Skill.HUNTER);
            var craftingLevel = Skills.getLevel(Skill.CRAFTING);
            if(hunterLevel >= hunterLevelReq && craftingLevel >= craftingLevelReq){
                var logs = Inventory.getAll(logID);
                if(!logs.isEmpty()){
                    return true;
                }
            }
            return false;
        }
        static BirdhouseType getTypeToBuild(){
            var types = values();
            for (int i = values().length - 1; i >= 0; i--) {
                if(types[i].canMake()){
                    return types[i];
                }
            }
            return null;
        }
    }
    @Inject
    private ClientThread clientThread;

    private static final Set<Integer> seedIDs = Set.of(
            ItemID.BARLEY_SEED,
            ItemID.HAMMERSTONE_SEED,
            ItemID.ASGARNIAN_SEED,
            ItemID.JUTE_SEED,
            ItemID.MARRENTILL_SEED,
            ItemID.YANILLIAN_SEED,
            ItemID.KRANDORIAN_SEED,
            ItemID.GUAM_SEED
    );

    private static final Set<Integer> birdhouseIDs = Set.of(
            30565,
            30566,
            30567,
            30568
    );

    private final String MENU_OPTION = "Auto-replace";
    private final String MENU_TARGET = "<col=00ff00>Birdhouse</col>";

    private final int BIRDHOUSE_BUILDING_ANIMATION = 7057;

    int tickDelay;

    public boolean anyMenuEntriesMatch(Predicate<MenuEntry> predicate){
        for(var entry : client.getMenuEntries()){
            if(predicate.test(entry))
                return true;
        }
        return false;
    }
    public boolean shouldAddBirdhouseOption(){

        if(anyMenuEntriesMatch( x -> birdhouseIDs.contains(x.getIdentifier()))){
            return true;
        }
        return false;
    }

    Timer timer = new Timer();
    @Subscribe
    public void onClientTick(ClientTick event) {
        if(shouldAddBirdhouseOption()){
            var currentEntries = client.getMenuEntries();
            int id = 0;
            for(var e : currentEntries){
                if(birdhouseIDs.contains(e.getIdentifier())){
                    id = e.getIdentifier();
                    break;
                }
            }
            client.insertMenuItem(
                    MENU_OPTION,
                    MENU_TARGET,
                    //MenuAction.RUNELITE.getId(),
                    MenuAction.UNKNOWN.getId(),
                    id,
                    0,
                    0,
                    true
            );
            if(timer.getSecondsFromStart() > 3){
                logger.info(Arrays.stream(client.getMenuEntries()).map(x -> x.getOption()).collect(Collectors.toList()).toString());
            }
//            int i;
//            for (i = 0; i < currentEntries.length; i++) {
//                if(currentEntries[i].getOption().equals("Empty")){
//                    break;
//                }
//            }
//            var newEntries = Arrays.copyOf(currentEntries,currentEntries.length + 1)
//            client.insertMenuItem(
//                    MENU_OPTION,
//                    MENU_TARGET,
//                    MenuAction.RUNELITE.getId(),
//                    id,
//                    0,
//                    0,
//                    false
//            );
//            if(timer.getSecondsFromStart() > 3){
//                logger.info(Arrays.toString(client.getMenuEntries()));
//                timer.reset();
//            }
            /*int id = 0;
            for(var e : client.getMenuEntries()){
                if(birdhouseIDs.contains(e.getIdentifier())){
                    id = e.getIdentifier();
                    break;
                }
            }
            var entries = client.getMenuEntries();
            int i;
            for (i = 0; i < entries.length; i++) {
                if(entries[i].getOption().equals("Empty")){
                    break;
                }
            }
            var entryToModify = entries[i];
            var cloned = entryToModify.clone();
            entryToModify.setTarget(MENU_TARGET);
            entryToModify.setOption(MENU_OPTION);
            entryToModify.setOpcode(MenuAction.RUNELITE.getId());
            entryToModify.setIdentifier(id);
            entryToModify.setParam0(0);
            entryToModify.setParam1(0);*/
        }
    }
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if(e.getMenuTarget().equals(MENU_TARGET) && e.getMenuOption().equals(MENU_OPTION)){
            init(e.getId());

            //Get the "Empty" menu entry;
            var empty = Arrays.stream(client.getMenuEntries())
                            .filter(x -> x.getOption().equals("Empty") && x.getIdentifier() == currentBirdhouseID).findFirst();
            if(empty.isPresent()){
                e.setMenuEntry(empty.get());
            }
            else {
                e.consume();
            }
        }
    }

    public MenuEntry createMenuEntry(){
        var entries = client.getMenuEntries();
        int id = 0;
        for(var e : entries){
            if(birdhouseIDs.contains(e.getIdentifier())){
                id = e.getIdentifier();
                break;
            }
        }
        MenuEntry menuEntry = new MenuEntry();
        menuEntry.setOption(MENU_OPTION);
        menuEntry.setTarget(MENU_TARGET);
        menuEntry.setOpcode(MenuAction.RUNELITE.getId());
        menuEntry.setIdentifier(id);
        return menuEntry;
    }

    enum AutoBirdhouseState {
        IDLE,
        EMPTYING,
        REBUILDING,
        REPLACING,
        INSERTING_SEEDS
    }
    static AutoBirdhouseState state = AutoBirdhouseState.IDLE;
    int currentBirdhouseID;


    TileObject getCurrentBirdhouse(){
        var player = client.getLocalPlayer();
        return TileObjects.getAll(currentBirdhouseID)
                .stream()
                .min(Comparator.comparing(x -> x.distanceTo(player)))
                .get();
    }

    @Subscribe
    public void onGameTick(GameTick event){
        if(tickDelay > 0){
            tickDelay--;
        }
        if(state != AutoBirdhouseState.IDLE){
            logger.info("State: " + state.name());
        }
        var player = client.getLocalPlayer();
        if(player != null && !player.isIdle()){
            return;
        }
        switch(state){
            case IDLE -> {return;}
            case EMPTYING -> doEmptyBirdhouse();
            case REBUILDING -> doRebuild();
            case REPLACING -> doReplace();
            case INSERTING_SEEDS -> doInsertingSeeds();
        }
    }

    private void init(int targetID){
        //Check if Inventory has enough room.
        if( Inventory.getFreeSlots() < 1){
            return;
        }
        if(! Inventory.contains( ItemID.CHISEL ) || !Inventory.contains(ItemID.HAMMER)){
            return;
        }
        if(! Inventory.contains( x -> seedIDs.contains(x.getId()) && x.getQuantity() >= 10 )){
            return;
        }
        birdhouseType = BirdhouseType.getTypeToBuild();
        if(birdhouseType == null){
            return;
        }
        logger.info("Starting...");
        currentBirdhouseID = targetID;
        var seeds = Inventory.getFirst(
                x -> seedIDs.contains(x.getId())
        );
        seedsToUse = seeds.getId();
        prevSeedCount = seeds.getQuantity();
        state = AutoBirdhouseState.EMPTYING;
    }

    int numClockworks;
    int numBirdhouses;
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged){
        //Clockworks
        if(state == AutoBirdhouseState.IDLE){
            return;
        }
        switch(state){
            case EMPTYING -> {
                var newNumClockworks = Inventory.getAll(ItemID.CLOCKWORK).size();
                if (newNumClockworks == numClockworks + 1) {
                    state = AutoBirdhouseState.REBUILDING;
                }
            }
            case REBUILDING -> {
                var newNumClockworks = Inventory.getAll(ItemID.CLOCKWORK).size();
                if (newNumClockworks == numClockworks - 1) {
                    state = AutoBirdhouseState.REPLACING;
                }
            }
            case REPLACING -> {
                var newNumBirdhouses = Inventory.getAll(
                        birdhouseType.birdhouseID
                ).size();
                if(newNumBirdhouses == numBirdhouses - 1){
                    state = AutoBirdhouseState.INSERTING_SEEDS;
                }
            }
        }
        numClockworks = Inventory.getAll(ItemID.CLOCKWORK).size();
        if(birdhouseType != null) {
            numBirdhouses = Inventory.getAll(
                    birdhouseType.birdhouseID
            ).size();
        }
        else{
            numBirdhouses = 0;
        }

    }
    int prevSeedCount;
    int seedsToUse;

    private void useItemOnObject(Item item, TileObject object){
        WorldPoint wp = object.getWorldLocation();
        MousePackets.queueClickPacket(0,0);
        item.useOn(object);
        var sceneLoc = object.getLocalLocation();
        client.invokeMenuAction("","",object.getId(),MenuAction.ITEM_USE_ON_GAME_OBJECT.getId(),sceneLoc.getSceneX(),sceneLoc.getSceneY());
    }

    private void doInsertingSeeds() {
        var seed = Inventory.getFirst(seedsToUse);
        if(seed == null || seed.getQuantity() < prevSeedCount){
            clientThread.invokeLater(()->{
                Time.sleepUntil(() -> Dialog.isOpen(),50,2000);
                if (Dialog.isOpen()) {
                    Dialog.continueSpace();
                }
            });
            state = AutoBirdhouseState.IDLE;
            return;
        }
        //seed.useOn(getCurrentBirdhouse());
        //ItemPackets.queueItemUseOnGameObjectPacket();
        useItemOnObject(seed,getCurrentBirdhouse());
    }

    private void doReplace() {
        var birdhouseTileObject = getCurrentBirdhouse();
        if(birdhouseTileObject.getName().equals("Birdhouse (empty)")){
            state = AutoBirdhouseState.INSERTING_SEEDS;
            return;
        }
        Item birdhouse = Inventory.getFirst(
                birdhouseType.birdhouseID
        );
        useItemOnObject(birdhouse,getCurrentBirdhouse());
    }

    private void doRebuild() {
        var player = client.getLocalPlayer();
        Item birdhouse = Inventory.getFirst(
                birdhouseType.birdhouseID
        );
        if(birdhouse != null){
            state = AutoBirdhouseState.REPLACING;
            return;
        }
        Item clockwork = Inventory.getFirst(ItemID.CLOCKWORK);
        Item logs = Inventory.getFirst(
                birdhouseType.logID
        );
        MousePackets.queueClickPacket(0,0);
//        ItemPackets.useItemOnItem(clockwork,logs);
        clockwork.useOn(logs);
        return;
    }

    private void doEmptyBirdhouse() {
        if(Dialog.isOpen() && Dialog.isViewingOptions()){
            state = AutoBirdhouseState.IDLE;
            return;
        }
        var birdhouse = getCurrentBirdhouse();
        MousePackets.queueClickPacket(0,0);
//        TileObjectPackets.tileObjectAction(birdhouse,"Empty",0);
        birdhouse.interact("Empty");
    }

}
