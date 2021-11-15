package meteor.plugins.changmiscplugins;

import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.input.KeyListener;
import meteor.input.KeyManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.coords.Area;
import dev.hoot.api.coords.RectangularArea;
import dev.hoot.api.entities.TileItems;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.magic.Magic;
import dev.hoot.api.magic.Regular;
import dev.hoot.api.packets.MousePackets;
import meteor.plugins.worldmapwalker.WorldMapWalkerPlugin;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import javax.inject.Inject;

import java.awt.event.KeyEvent;
import java.util.Comparator;


@PluginDescriptor(
        name = "Chang's Telegrabber",
        description = "Telegrabs expensive shit"
)
public class ChangTelegrabber extends Plugin {

    @Inject
    private KeyManager keyManager;
    private boolean hotKeyPressed;
    @Inject
    private ItemManager itemManager;

    boolean isLooting = false;

    @Override
    public void startup() {
        keyManager.registerKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_9 && e.isControlDown()) {
                    isLooting = false;
                    client.addChatMessage(
                            ChatMessageType.GAMEMESSAGE,
                            "Telegrabber",
                            "Looting disabled!",
                            null
                    );
                    e.consume();
                }
                if(e.getKeyCode() == KeyEvent.VK_0 && e.isControlDown()) {
                    isLooting = true;
                    client.addChatMessage(
                            ChatMessageType.GAMEMESSAGE,
                            "Telegrabber",
                            "Looting enabled!",
                            null
                    );
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        }, WorldMapWalkerPlugin.class);
    }

    Area lootArea = Area.union(
            new RectangularArea(3203, 3805, 3214, 3817),
            new RectangularArea(3199, 3818, 3220, 3808)
    );
    boolean itemFilter(TileItem item){
       return itemManager.getItemPrice(item.getId()) * item.getQuantity() >= 1000;
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
    @Subscribe
    public void onGameTick(GameTick tick){
        if(!isLooting){
            return;
        }
        var player = client.getLocalPlayer();
        if(player == null || !player.isIdle()){
            return;
        }
        if(Inventory.isFull())
            return;
        var items = TileItems.getAll(this::itemFilter);
        if(items.isEmpty())
            return;

        var nearest = items.stream().min(Comparator.comparingInt(player::distanceTo));
        teleGrab(nearest.get());
    }
}
