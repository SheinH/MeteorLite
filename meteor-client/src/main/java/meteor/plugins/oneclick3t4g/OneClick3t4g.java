package meteor.plugins.oneclick3t4g;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import dev.hoot.api.items.Inventory;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import meteor.plugins.*;
import meteor.plugins.PluginDescriptor;
import java.util.*;
import static net.runelite.api.MenuAction.*;

@PluginDescriptor(
        name = "One Click 3t4g",
        description = "mines granite efficiently in 1 click. YOU NEED A KNIFE AND TEAK LOG",
        enabledByDefault = false
)
public class OneClick3t4g extends Plugin {
    @Inject
    private OneClick3t4gConfig config;
    @Inject
    private Client client;
    private ItemContainer inv;
    private int startingTickCount=-1;
    int stage = 0;
    private int currentRock=0;
    @Provides
    public OneClick3t4gConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(OneClick3t4gConfig.class);
    }
    List<GameObject> rocks = new ArrayList<>();
    @Override
    public void shutDown(){
        reset();
    }
    @Override
    public void startUp(){
        reset();
    }
    public void reset(){
        startingTickCount = client.getTickCount();
        stage =0;
        rocks = new ArrayList<>();
    }
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if(event.getMenuOption().contains("one click 3t4g")) {
            clickHandler(event);
        }
        if(event.getMenuOption().contains("one click 3t4g")) {
            event.consume();
        }
    }
    public void clickHandler(MenuOptionClicked event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if(inv==null){
            inv= client.getItemContainer(InventoryID.INVENTORY);
            return;
        }
        if(config.humidify()){
            if(Inventory.getFirst(1825,1827,1829,1823)==null){
                if(Inventory.getFirst(1831)!=null){
                    event.setMenuEntry(new MenuEntry("Cast","<col=00ff00>Humidify</col>",1,CC_OP.getId(),-1, client.getWidget(WidgetInfo.SPELL_HUMIDIFY).getId(),false));
                    return;
                }else{
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "you need to bring waterskins for this disabling plugin", null);
                    this.toggle();
                    return;
                }
            }
        }
        if(!inv.contains(6333)||!inv.contains(946)){
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "you need to bring a teak log and knife. disabling plugin", null);
            this.toggle();
            return;
        }
        if(rocks.size()==0) {
            GameObject rock1 = new GameObjectQuery().atWorldLocation(new WorldPoint(3165,2908,0)).result(client).first();
            GameObject rock2 = new GameObjectQuery().atWorldLocation(new WorldPoint(3165,2909,0)).result(client).first();
            GameObject rock3 = new GameObjectQuery().atWorldLocation(new WorldPoint(3165,2910,0)).result(client).first();
            GameObject rock4 = new GameObjectQuery().atWorldLocation(new WorldPoint(3167,2911,0)).result(client).first();
            if(rock1==null||rock2==null||rock3==null||rock4==null){
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "you need to be at the 3t4g spot. disabling plugin", null);
                this.toggle();
                return;
            }
            rocks.add(rock1);
            rocks.add(rock2);
            rocks.add(rock3);
            rocks.add(rock4);
            return;
        }
        if(startingTickCount==-1||(client.getTickCount()-startingTickCount)>2){
            if(config.humidify()){
                if(Inventory.getAll(1825,1827,1829,1823).isEmpty()){
                    if(Inventory.getFirst(1831)!=null){
                        event.setMenuEntry(new MenuEntry("Cast","<col=00ff00>Humidify</col>",1,CC_OP.getId(),-1,14286958,false));
                        return;
                    }else{
                        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "you need to bring waterskins for this disabling plugin", null);
                        this.toggle();
                        return;
                    }
                }
            }
            WidgetItem knife = getItemFromInv(946);
            WidgetItem teak = getItemFromInv(6333);
            if(teak==null){
                return;
            }
            if(knife!=null) {
                event.setSelectedItemIndex(knife.getSlot());
                client.setSelectedItemWidget(WidgetInfo.INVENTORY.getPackedId());
                client.setSelectedItemSlot(knife.getSlot());
                client.setSelectedItemID(knife.getId());
                stage = 1;
                event.setMenuEntry(new MenuEntry("Use", "<col=ff9040>Knife<col=ffffff> -> <col=ff9040>Teak logs", 6333, ITEM_USE_ON_WIDGET_ITEM.getId(), teak.getSlot(), 9764864, false));
                startingTickCount = client.getTickCount();
                return;
            }
        }
        if(client.getTickCount()==startingTickCount){
            if(stage==1){
                event.setMenuEntry(new MenuEntry("Mine", "<col=ffff>Rocks", 11387, GAME_OBJECT_FIRST_OPTION.getId(),rocks.get(currentRock).getLocalLocation().getSceneX() , rocks.get(currentRock).getLocalLocation().getSceneY(), false));
                currentRock++;
                if(currentRock==4){
                    currentRock=0;
                }
                stage++;
            }
        }else if(client.getTickCount()==(startingTickCount+1)){
            if(stage==2){
                if (getItemFromInv(6979) != null) {
                    WidgetItem granite = getItemFromInv(6979);
                    if(granite==null){
                        return;
                    }
                    event.setMenuEntry(new MenuEntry("Drop", "<col=ff9040>Granite (500g)", granite.getId(), ITEM_FIFTH_OPTION.getId(), granite.getSlot(), 9764864, false));
                    stage=3;
                } else if (getItemFromInv(6981) != null) {
                    WidgetItem granite = getItemFromInv(6981);
                    if(granite==null){
                        return;
                    }
                    event.setMenuEntry(new MenuEntry("Drop", "<col=ff9040>Granite (2kg)", granite.getId(), ITEM_FIFTH_OPTION.getId(), granite.getSlot(), 9764864, false));
                    stage=3;
                } else if (getItemFromInv(6983) != null) {
                    WidgetItem granite = getItemFromInv(6983);
                    if(granite==null){
                        return;
                    }
                    event.setMenuEntry(new MenuEntry("Drop", "<col=ff9040>Granite (5kg)", granite.getId(), ITEM_FIFTH_OPTION.getId(), granite.getSlot(), 9764864, false));
                    stage=3;
                }
            }else if(stage!=3){
                if(config.humidify()){
                    if(!inv.contains(1825)&&!inv.contains(1827)||!inv.contains(1829)||!inv.contains(1823)){
                        if(inv.contains(1831)){
                            event.setMenuEntry(new MenuEntry("Cast","<col=00ff00>Humidify</col>",1,CC_OP.getId(),-1,14286958,false));
                        }else{
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "you need to bring waterskins for this disabling plugin", null);
                            this.toggle();
                            return;
                        }
                    }
                }
                WidgetItem knife = getItemFromInv(946);
                WidgetItem teak = getItemFromInv(6333);
                if(teak==null){
                    return;
                }
                if(knife!=null) {
                    event.setSelectedItemIndex(knife.getSlot());
                    client.setSelectedItemWidget(WidgetInfo.INVENTORY.getPackedId());
                    client.setSelectedItemSlot(knife.getSlot());
                    client.setSelectedItemID(knife.getId());
                    stage = 1;
                    event.setMenuEntry(new MenuEntry("Use", "<col=ff9040>Knife<col=ffffff> -> <col=ff9040>Teak logs", 6333, ITEM_USE_ON_WIDGET_ITEM.getId(), teak.getSlot(), 9764864, false));
                    startingTickCount = client.getTickCount();
                }
            }
        }else if(client.getTickCount()==(startingTickCount+2)){
            if(stage==2){
                return;
            }
            if(stage==3){
                if(currentRock==0){
                    currentRock=4;
                }
                event.setMenuEntry(new MenuEntry("Mine", "<col=ffff>Rocks", 11387, GAME_OBJECT_FIRST_OPTION.getId(),rocks.get(currentRock-1).getLocalLocation().getSceneX() , rocks.get(currentRock-1).getLocalLocation().getSceneY(), false));
                stage=0;
                if(currentRock==4){
                    currentRock=0;
                }
            }
        }
    }
    @Subscribe
    public void onChatMessage(ChatMessage message){
        if(message.getMessage().contains("You do not have enough")){
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "you had no humidify runes. disabling plugin", null);
            this.toggle();
        }
    }
    private WidgetItem getItemFromInv(int id) {
        Widget playerInv = client.getWidget(WidgetInfo.INVENTORY);
        if (playerInv != null) {
            for (WidgetItem widgetItem : playerInv.getWidgetItems()) {
                if (widgetItem.getId() == id) {
                    return widgetItem;
                }
            }
        }
        return null;
    }
    @Subscribe
    private void onClientTick(ClientTick event)
    {
        if(client.getLocalPlayer() == null || client.getGameState() != GameState.LOGGED_IN){
            return;
        }
        client.insertMenuItem("one click 3t4g", "", 0, 0, 0, 0, true);
    }
}
