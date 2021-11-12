package meteor.plugins.prayerFlicker;

import com.google.inject.Provides;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.input.KeyManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.packets.MousePackets;
import dev.hoot.api.packets.WidgetPackets;
import dev.hoot.api.widgets.Prayers;
import meteor.util.HotkeyListener;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Prayer Flicker",
        description = "prayer flicker for quick prayers",
        enabledByDefault = false
)
public class PrayerFlickerPlugin extends Plugin {
    public int timeout = 0;
    @Inject
    private ClientThread clientThread;

    @Inject
    private KeyManager keyManager;
    @Inject
    private PrayerFlickerConfig config;

    @Provides
    public PrayerFlickerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PrayerFlickerConfig.class);
    }

    private void togglePrayer(){
        MousePackets.queueClickPacket(0, 0);
        client.invokeMenuAction("","",1, MenuAction.CC_OP.getId(),-1,WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId());
    }

    @Override
    public void startup() {
        keyManager.registerKeyListener(prayerToggle, this.getClass());
    }

    @Override
    public void shutdown() {
        keyManager.unregisterKeyListener(prayerToggle);
        toggle=false;
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        clientThread.invoke(() -> {
            if (Prayers.isQuickPrayerEnabled()) {
                togglePrayer();
            }
        });
    }

    boolean toggle;

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if (toggle) {
            boolean quickPrayer = client.getVar(Varbits.QUICK_PRAYER) == 1;
            if (quickPrayer) {
                togglePrayer();
            }
            togglePrayer();
        }
    }

    private final HotkeyListener prayerToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle = !toggle;
            if (client.getGameState() != GameState.LOGGED_IN) {
                return;
            }
            if (!toggle) {
                clientThread.invoke(() -> {
                    if (Prayers.isQuickPrayerEnabled()) {
                        togglePrayer();
                    }
                });
            }
        }
    };

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOGGED_IN) {
            keyManager.unregisterKeyListener(prayerToggle);
            return;
        }
        keyManager.registerKeyListener(prayerToggle, this.getClass());
    }
}
