package meteor.plugins.changmiscplugins;

import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.entities.TileItems;
import dev.hoot.api.game.Game;
import dev.hoot.api.items.Bank;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.packets.ItemPackets;
import dev.hoot.api.packets.MousePackets;
import dev.hoot.api.packets.WidgetPackets;
import meteor.util.Text;
import net.runelite.api.*;
import net.runelite.api.events.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

@PluginDescriptor(
        name = "Chang's Firemaker",
        description = "Lights logs",
        enabledByDefault = false
)
public class ChangFiremaker extends Plugin {
    private static final String MENU_OPTION = "Light-all";
    Set<Integer> logs = Set.of(
            ItemID.LOGS,
            ItemID.OAK_LOGS,
            ItemID.WILLOW_LOGS,
            ItemID.TEAK_LOGS,
            ItemID.MAPLE_LOGS,
            ItemID.MAHOGANY_LOGS,
            ItemID.YEW_LOGS,
            ItemID.MAGIC_LOGS,
            ItemID.REDWOOD_LOGS
    );


    private static boolean active = false;
    private static boolean hasTinderbox = false;
    private int logID;

    @Subscribe
    public void onGameTick(GameTick event) {
        if(Bank.isOpen() && !Inventory.isFull()){
            handleBank();
        }
        if(!active)
            return;
        if(getTimeSinceLastXPDrop() > 15000){
            active = false;
            return;
        }
        var player = client.getLocalPlayer();
        if (player == null || !player.isIdle()) return;
        var groundItems = TileItems.getAt(player.getWorldLocation(), x -> logs.contains(x.getId()));
        if (!groundItems.isEmpty()) {
            groundItems.get(0).interact("Light");
            return;
        }
        var tinderbox = Inventory.getFirst(ItemID.TINDERBOX);
        var logs = Inventory.getFirst(logID);
        if (logs != null && tinderbox != null) {
            MousePackets.queueClickPacket(0, 0);
            ItemPackets.useItemOnItem(tinderbox, logs);
        }
        else{
            active = false;
        }
    }

    private void handleBank() {
        var item = Bank.getFirst(ItemID.MAHOGANY_LOGS);
        WidgetPackets.widgetAction(client.getWidget(item.getWidgetId()),"Withdraw-All");
        logger.info("Withdraw Packets sent");
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged){
        hasTinderbox = Inventory.contains(ItemID.TINDERBOX);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE){
            var message = chatMessage.getMessage();
            if( message.equals("You can't light a fire here.")) {
                active = false;
            }
            if(message.contains("You need a Firemaking level")){
                active = false;
            }
        }
    }

    public boolean anyMenuEntriesMatch(Predicate<MenuEntry> predicate){
        for(var entry : client.getMenuEntries()){
            if(predicate.test(entry))
                return true;
        }
        return false;
    }
    public boolean shouldAddMenuEntry(MenuEntryAdded event) {
        if(!hasTinderbox)
            return false;
        return event.getOpcode() == MenuAction.ITEM_USE.getId() && !event.isForceLeftClick() &&
                logs.contains(event.getIdentifier());
    }
    public boolean shouldAddBankMenuEntry(MenuEntryAdded event) {
        return anyMenuEntriesMatch(x -> x.getOption().equals("Bank"));
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if(shouldAddMenuEntry(event)){
            MenuEntry menuEntry = new MenuEntry();
            menuEntry.setOption(MENU_OPTION);
            menuEntry.setTarget("<col=00ff00>" + Text.removeTags(event.getTarget()) + "</col>");
            menuEntry.setOpcode(MenuAction.RUNELITE.getId());
            menuEntry.setIdentifier(event.getIdentifier());
            var entries = new ArrayList<MenuEntry>(Arrays.asList(client.getMenuEntries()));
            entries.add(entries.size() - 1, menuEntry);
            client.setMenuEntries(entries.toArray(new MenuEntry[0]));
        }
        if(shouldAddBankMenuEntry(event)){
            //addBankMenuEntry();
        }
    }

    static long getTimeSinceLastXPDrop(){
        return System.currentTimeMillis() - lastXPDrop;
    }
    static long lastXPDrop;

    @Subscribe
    public void onStatChanged(StatChanged event){
        if(event.getSkill() == Skill.FIREMAKING && event.getXpChange() > 0){
            lastXPDrop = System.currentTimeMillis();
        }
    }
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if(e.getMenuAction() == MenuAction.RUNELITE && e.getMenuOption().equals(MENU_OPTION)){
            logID = e.getId();
            active = true;
            lastXPDrop = System.currentTimeMillis();
            var tinderbox = Inventory.getFirst(ItemID.TINDERBOX);
            var logs = Inventory.getFirst(logID);
            if (logs != null && tinderbox != null) {
                MousePackets.queueClickPacket(0, 0);
                ItemPackets.useItemOnItem(tinderbox, logs);
            }
            else{
                active = false;
            }
        }
    }
}
