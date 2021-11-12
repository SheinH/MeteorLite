package meteor.plugins.meteorlite;

import meteor.PluginManager;
import meteor.config.Config;
import meteor.config.ConfigManager;
import meteor.config.MeteorLiteConfig;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.input.MouseManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.meteorlite.interaction.InteractionManager;
import meteor.plugins.meteorlite.interaction.InteractionOverlay;
import dev.hoot.api.movement.regions.RegionManager;
import net.runelite.api.GameState;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import org.sponge.util.Logger;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;


@PluginDescriptor(
        name = "MeteorLite",
        cantDisable = true
)
public class MeteorLitePlugin extends Plugin {

    @Inject
    private MeteorLiteConfig config;

    @Inject
    private InteractionManager interactionManager;

    @Inject
    private InteractionOverlay interactionOverlay;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private MeteorLiteConfig meteorLiteConfig;

    @Inject
    private ConfigManager configManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private RegionManager regionManager;

    @Inject
    private ExecutorService executorService;

    @Override
    public void updateConfig() {
        Logger.setDebugEnabled(config.debugEnabled());
    }

    @Override
    public void startup() {
        overlayManager.add(interactionOverlay);
        mouseManager.registerMouseListener(interactionOverlay);
        eventBus.register(interactionManager);
    }

    @Override
    public void shutdown() {
        overlayManager.remove(interactionOverlay);
        mouseManager.unregisterMouseListener(interactionOverlay);
        eventBus.unregister(interactionManager);
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals(MeteorLiteConfig.GROUP_NAME)) {
            return;
        }

        if (event.getKey().equals("reloadExternals")) {
            pluginManager.startExternals();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(MeteorLiteConfig.GROUP_NAME)) {
            return;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        executorService.execute(() -> regionManager.sendRegion());
    }

    @Override
    public Config getConfig(ConfigManager configManager) {
        return meteorLiteConfig;
    }
}
