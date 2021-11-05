package meteor.plugins.prayerFlicker;

import com.google.inject.Provides;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.input.KeyListener;
import meteor.input.KeyManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.coords.Area;
import meteor.plugins.api.coords.RectangularArea;
import meteor.plugins.api.packets.MousePackets;
import meteor.plugins.api.packets.WidgetPackets;
import meteor.plugins.api.widgets.Prayers;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

@PluginDescriptor(
        name = "Prayer Flicker",
        description = "prayer flicker for quick prayers",
        tags = {},
        enabledByDefault = false
)
public class PrayerFlickerPlugin extends Plugin
{
    public int timeout = 0;
    @Inject
    private ClientThread clientThread;

    @Inject
    private KeyManager keyManager;
    @Inject
    private PrayerFlickerConfig config;

    @Provides
    public PrayerFlickerConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PrayerFlickerConfig.class);
    }

    @Override
    public void startup()
    {
        keyManager.registerKeyListener(prayerToggle, this.getClass());
    }

    @Override
    public void shutdown()
    {
        keyManager.unregisterKeyListener(prayerToggle);
        toggle = false;
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        clientThread.invoke(() ->
        {
            if (Prayers.isQuickPrayerEnabled())
            {
                MousePackets.queueClickPacket(0, 0);
                var widget = client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
                client.invokeMenuAction("","",1, MenuAction.CC_OP.getId(),widget.getIndex(),widget.getId());
            }
        });
    }

    boolean toggle;

    Area fightArea = new RectangularArea(3218, 3593, 3256, 3632);

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        var player = client.getLocalPlayer();
//        if(!fightArea.contains(player)){
//            toggle = false;
//            if (Prayers.isQuickPrayerEnabled()) {
//                MousePackets.queueClickPacket(0, 0);
//                WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
//            }
//        }
        if (toggle)
        {
            boolean quickPrayer = client.getVar(Varbits.QUICK_PRAYER) == 1;
            if (quickPrayer)
            {
                MousePackets.queueClickPacket(0, 0);
                var widget = client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
                client.invokeMenuAction("","",1, MenuAction.CC_OP.getId(),widget.getIndex(),widget.getId());
//                WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
            }
            MousePackets.queueClickPacket(0, 0);
            var widget = client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
            client.invokeMenuAction("","",1, MenuAction.CC_OP.getId(),widget.getIndex(),widget.getId());
//            WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
        }
    }

    //    private final HotkeyListener prayerToggle = new HotkeyListener(() -> config.toggle()) {
//        @Override
//        public void hotkeyPressed() {
//            toggle = !toggle;
//            if (client.getGameState() != GameState.LOGGED_IN) {
//                return;
//            }
//            if (!toggle) {
//                clientThread.invoke(() -> {
//                    if (Prayers.isQuickPrayerEnabled()) {
//                        MousePackets.queueClickPacket(0, 0);
//                        WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
//                    }
//                });
//            }
//        }
//    };
    private final KeyListener prayerToggle = new KeyListener()
    {
        @Override
        public void keyTyped(KeyEvent e)
        {

        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET)
            {
                e.consume();
                toggle = !toggle;
                if (client.getGameState() != GameState.LOGGED_IN)
                {
                    return;
                }
                if (!toggle)
                {
                    clientThread.invoke(() ->
                    {
                        if (Prayers.isQuickPrayerEnabled())
                        {
                            MousePackets.queueClickPacket(0, 0);
                            WidgetPackets.queueWidgetActionPacket(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
                        }
                    });
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e)
        {

        }
    };

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN)
        {
            keyManager.unregisterKeyListener(prayerToggle);
            return;
        }
        keyManager.registerKeyListener(prayerToggle, this.getClass());
    }
}
