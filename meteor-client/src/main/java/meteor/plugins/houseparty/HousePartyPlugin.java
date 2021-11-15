package meteor.plugins.houseparty;

import com.google.inject.Inject;
import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.game.Worlds;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.magic.Regular;
import dev.hoot.api.magic.Rune;
import dev.hoot.api.movement.pathfinder.RuneRequirement;
import dev.hoot.api.widgets.Dialog;
import dev.hoot.api.widgets.Widgets;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import org.apache.commons.math3.distribution.GammaDistribution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BooleanSupplier;

@PluginDescriptor(
        name = "House Party",
        enabledByDefault = false
)
public class HousePartyPlugin extends Plugin {

    boolean enabled = false;
    boolean visitLastFailed = false;

    enum HousePartyPluginState {
        TELEPORTING,
        ENTERING_VISITLAST,
        ENTERING_MENU,
        DRINKING,
        IDLE
    }

    HousePartyPluginState state = HousePartyPluginState.IDLE;

    int tickDelay = 0;

    private BooleanSupplier waitCond = null;

    @Override
    public void startUp() {
        enabled = true;
    }

    @Override
    public void shutDown() {
        enabled = false;
    }

    private final int HOUSE_TELETAB = ItemID.TELEPORT_TO_HOUSE;

    private boolean hasTeleportRunes(){
        return true;
//        var lawRune = new RuneRequirement(1, Rune.LAW);
//        var airRune = new RuneRequirement(1, Rune.AIR);
//        var earthRune = new RuneRequirement(1, Rune.EARTH);
//        return lawRune.meetsRequirements() && airRune.meetsRequirements() && earthRune.meetsRequirements();
    }


    public void doTeleport() {
        if(hasTeleportRunes()) {
            var tpSpell = Regular.TELEPORT_TO_HOUSE;
            var tpSpellWidget = Widgets.get(tpSpell.getWidget());
            tpSpellWidget.interact(1);
        }
        else if (Inventory.contains(HOUSE_TELETAB)){
            Inventory.getFirst(HOUSE_TELETAB).interact("Outside");
        }
    }

    private GameObject getHouseAdvert(){
        var objs = new GameObjectQuery()
                .idEquals(ObjectID.HOUSE_ADVERTISEMENT)
                .result(client);
        if(objs.isEmpty())
            return null;
        else return objs.first();
    }
    public void tryVisitLast() {
        var advert = getHouseAdvert();
        advert.interact(3);
    }

    private boolean isHouseMenuOpen(){
        var groupID = WidgetID.RESIZABLE_VIEWPORT_BOTTOM_LINE_GROUP_ID;
        var enterButtons = Widgets.get(groupID,3407891).getChildren();
        logger.info(enterButtons);
        var button = Arrays.stream(enterButtons).filter(x -> !x.isHidden() && x.hasAction("Enter House")).min(Comparator.comparingInt(Widget::getOriginalY));
        return button.isPresent();
    }
    private void enterHouseFromMenu(){
        var groupID = WidgetID.RESIZABLE_VIEWPORT_BOTTOM_LINE_GROUP_ID;
//        var constructionLevelWidgets = Widgets.get(groupID,3407884).getChildren();
//        var nexusTierWidgets= Widgets.get(groupID, 3407879).getChildren();
//        var jewelleryBoxTierWidgets = Widgets.get(groupID,3407887).getChildren();
//        var enterButtonWidgets = Widgets.get(groupID,3407891).getChildren();
//
//        //Sanity check
//        if(constructionLevelWidgets == null || nexusTierWidgets == null || jewelleryBoxTierWidgets == null || enterButtonWidgets== null){
//            return;
//        }
//        int len = constructionLevelWidgets.length;
//        if(len != nexusTierWidgets.length || len != jewelleryBoxTierWidgets.length || len != enterButtonWidgets.length){
//            return;
//        }
//
//
//        for (int i = 0; i < nexusTierWidgets.length; i++) {
//            var constructionLevel = constructionLevelWidgets[i];
//            var nexusTier = nexusTierWidgets[i];
//            var jewelleryBoxTier = jewelleryBoxTierWidgets[i];
//            if(nexusTier == null || jewelleryBoxTier == null){
//                return;
//            }
//            if(constructionLevel.getText() == "99" && jewelleryBoxTier.getText() == "3" && nexusTier.getText() == "3"){
//                enterButtonWidgets[i].interact(0);
//                tickDelay = 6;
//                return;
//            }
//        }
//        if(enterButtonWidgets.length > 0) {
//            enterButtonWidgets[0].interact(0);
//            tickDelay = 6;
//            return;
//        }
        if(isInHouse()){
            state = HousePartyPluginState.DRINKING;
            return;
        }
        var enterButtons = Widgets.get(groupID,3407891).getChildren();
        var button = Arrays.stream(enterButtons).filter(x -> !x.isHidden() && x.hasAction("Enter House")).min(Comparator.comparingInt(Widget::getOriginalY));
        if (button != null){
            logger.info(button.get());
            logger.info(button.get().getBounds());
            button.get().interact("Enter House");
            tickDelay = 3;
        }else {
            state = HousePartyPluginState.IDLE;
        }
    }
    private void doEnterHouseMenu(){
        if(isHouseMenuOpen()){
            enterHouseFromMenu();
        }
        var advert = getHouseAdvert();
        if(advert != null){
            advert.interact(0);
            tickDelay = 1;
        }

    }
    private void doGammaWait(int average, int min, int max){
        double beta = 2;
        double alpha = average / beta;
        GammaDistribution distribution = new GammaDistribution(beta, alpha);
        long result;
        do{
            result = Math.round(distribution.sample());
        }while(result < min || result > max);
        tickDelay = (int) result;
    }

    private void doTeleportingState(){
        logger.info("Doing tp");
        var advert = getHouseAdvert();
        if(advert != null && advert.distanceTo(client.getLocalPlayer()) < 15){
            //Hopping
            var world = Worlds.getCurrentWorld();
            if(world != null && world.getId() != 330){
                Worlds.hopTo(Worlds.getFirst(330),false);
                doGammaWait(10,6,14);
                return;
            }
            else {
                state = HousePartyPluginState.ENTERING_VISITLAST;
                return;
            }
        }
        if(hasTeleportRunes() || Inventory.contains(HOUSE_TELETAB)){
            if(Worlds.getCurrentWorld().getId() != 330){
                Worlds.hopTo(Worlds.getFirst(330),false);
            }
            else {
                doTeleport();
            }
            tickDelay = 2;
        }
        else if(Inventory.contains(ItemID.DUST_BATTLESTAFF) || Inventory.contains(ItemID.MYSTIC_DUST_STAFF)){
            var staff = Inventory.getFirst(ItemID.DUST_BATTLESTAFF,ItemID.MYSTIC_DUST_STAFF);
            staff.interact("Wield");
            doGammaWait(3,2,5);
        }
        else {
            logger.info("Cannot TP");
            state = HousePartyPluginState.IDLE;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        /*if (!enabled) {
            logger.info("Disabled");
            return;
        }
        else */
        if (tickDelay > 0) {
            logger.info("Delayed");
            tickDelay--;
            return;
        }
        switch(state){
            case TELEPORTING -> doTeleportingState();
            case ENTERING_VISITLAST -> doEnterVisitLast();
            //case ENTERING_MENU -> doEnterHouseMenu();
            case DRINKING -> doDrinking();
        }

    }

    private void doDrinking() {
        var player = client.getLocalPlayer();
        if(player.getAnimation() == 7305){
            state = HousePartyPluginState.IDLE;
            return;
        }
        if(player.isMoving() || player.isAnimating()){
            return;
        }
        var pool = getPool();
        if(pool == null){
            state = HousePartyPluginState.IDLE;
            return;
        }
        else {
            pool.interact(0);
        }
    }

    private GameObject getPool(){
        var query = new GameObjectQuery()
                .filter(x -> x.getName().toLowerCase().contains("pool") && x.hasAction("Drink"))
                .result(client).nearestTo(client.getLocalPlayer());
        return query;
    }
    private boolean isInHouse(){
        var query = new GameObjectQuery()
                .nameEquals("Portal")
                .actionEquals("Lock")
                .result(client);
        GameState state = client.getGameState();
        return !query.isEmpty();
    }


    private void doEnterVisitLast() {
        if(isInHouse()){
            state = HousePartyPluginState.DRINKING;
            return;
        }
        var advert = getHouseAdvert();
        if(advert == null){
            return;
        }
        else if(visitLastFailed || Dialog.isOpen()){
            visitLastFailed = false;
            advert.interact(0);
            //state = HousePartyPluginState.ENTERING_MENU;
            state = HousePartyPluginState.IDLE;
        }
        else if(client.getLocalPlayer().isIdle()){
            advert.interact(2);
        }
    }

    @Subscribe(priority = -1) // run after all plugins
    public void onChatMessage(ChatMessage chatMessage){
        if(chatMessage.getMessage().equals("You haven't visited anyone this session.")){
            visitLastFailed = true;
        }
    }

    @Inject
    private HousePartyConfig config;
    @Provides
    public HousePartyConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(HousePartyConfig.class);
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (event.getGroup().equalsIgnoreCase("housepartyplugin")) {
            if (event.getKey().equals("startStop")) {
                logger.info("Button press");
                if(state == HousePartyPluginState.IDLE){
                    state = HousePartyPluginState.TELEPORTING;
                }
            }
        }
    }
}
