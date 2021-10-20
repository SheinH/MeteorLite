package meteor.plugins.autologhop;

import com.google.inject.Provides;
import meteor.PluginManager;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.game.WorldService;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.items.Equipment;
import meteor.plugins.api.packets.ItemPackets;
import meteor.plugins.api.packets.MousePackets;
import meteor.plugins.api.packets.WidgetPackets;
import meteor.ui.overlay.OverlayManager;
import meteor.util.PvPUtil;
import meteor.util.WorldUtil;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import static java.awt.event.KeyEvent.VK_ENTER;

@PluginDescriptor(
        name = "Soxs' AutoLogHop",
        description = "Auto hops/logs out when another player is seen.",
        enabledByDefault = false
)
@SuppressWarnings("unused")
@Singleton
public class AutoLogHop extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ExecutorService executorService;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AutoLogHopConfig config;

    @Inject
    private EventBus eventBus;

    @Inject
    private ClientThread clientThread;

    @Inject
    private WorldService worldService;

    @Inject
    private ExecutorService executor;

    private boolean login;

    @Provides
    public AutoLogHopConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoLogHopConfig.class);
    }

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (nearPlayer()) {
            handleAction();
        }

    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (!login || event.getGameState() != GameState.LOGIN_SCREEN || config.user().isBlank() || config.password().isBlank()) {
            return;
        }
        hopToWorld(getValidWorld());
        executorService.submit(() -> {
            sleep(600);
            pressKey(VK_ENTER);
            client.setUsername(config.user());
            client.setPassword(config.password());
            sleep(600);
            pressKey(VK_ENTER);
            pressKey(VK_ENTER);
        });
        login = false;
    }

    @Subscribe
    public void onPlayerSpawned(PlayerSpawned event) {
        if (isPlayerBad(event.getPlayer()))
            handleAction();
    }

    private boolean nearPlayer() {
        List<Player> players = client.getPlayers();
        for (Player p : players) {
            if (!isPlayerBad(p))
                continue;
            return true;
        }
        return false;
    }

    private void handleAction() {
        switch (config.method()) {
            case HOP:
                hopToWorld(getValidWorld());
                break;

            case TELEPORT:
                teleportAway();
                break;

            default:
                logout();
                login = config.method() == Method.LOGOUT_HOP; //only login if we caused logout
                break;
        }
    }

    private void teleportAway() {
        //can't use ring of wealth above lv 30 wilderness.
        if (PvPUtil.getWildernessLevelFrom(client.getLocalPlayer().getWorldLocation()) > 30)
            return;
        //not as janky as inventory items kek
        Widget equipment = client.getWidget(WidgetInfo.EQUIPMENT_RING);
        ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
        if (equipment == null)
            return;
        //don't attempt to tele if we don't have a ring lol
        if (container != null && Arrays.stream(container.getItems()).noneMatch(item -> client.getItemComposition(item.getId()).getName().toLowerCase().contains("ring of wealth (")))
            return;

        //client.invokeMenuAction("Grand Exchange", "<col=ff9040>Ring of wealth ( )</col>", 3, MenuAction.CC_OP.getId(), -1, equipment.getId());
        var ring = WidgetInfo.EQUIPMENT_RING;
        var ringWidget = client.getWidget(ring);
        MousePackets.queueClickPacket(0, 0);
        WidgetPackets.widgetAction(ringWidget,"Grand Exchange");
    }

    private boolean passedWildernessChecks() {
        return config.disableWildyChecks() || inWilderness();
    }

    private boolean isPlayerBad(Player player) {
        if (player == client.getLocalPlayer())
            return false;

        if (isInWhitelist(player.getName()))
            return false;

        if (config.combatRange() && !PvPUtil.isAttackable(client, player))
            return false;

        if (config.skulledOnly() && !isPlayerSkulled(player))
            return false;

        if (!passedWildernessChecks())
            return false;

        return true;
    }

    private int getValidWorld() {
        WorldResult result = worldService.getWorlds();
        if (result == null)
            return -1;
        List<World> worlds = result.getWorlds();
        Collections.shuffle(worlds);
        for (World w : worlds) {
            if (client.getWorld() == w.getId())
                continue;

            if (w.getTypes().contains(net.runelite.http.api.worlds.WorldType.HIGH_RISK) ||
                    w.getTypes().contains(net.runelite.http.api.worlds.WorldType.DEADMAN) ||
                    w.getTypes().contains(net.runelite.http.api.worlds.WorldType.PVP) ||
                    w.getTypes().contains(net.runelite.http.api.worlds.WorldType.SKILL_TOTAL) ||
                    w.getTypes().contains(net.runelite.http.api.worlds.WorldType.BOUNTY) ||
                    config.membersWorlds() != w.getTypes().contains(net.runelite.http.api.worlds.WorldType.MEMBERS))
                continue;
            return w.getId();
        }
        return -1;
    }

    private void hopToWorld(int worldId) {
        assert client.isClientThread();

        WorldResult worldResult = worldService.getWorlds();
        // Don't try to hop if the world doesn't exist
        World world = worldResult.findWorld(worldId);
        if (world == null) {
            return;
        }

        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN) {
            // on the login screen we can just change the world by ourselves
            client.changeWorld(rsWorld);
            return;
        }

        if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            client.openWorldHopper();

            executor.submit(() -> {
                try {
                    Thread.sleep(25 + ThreadLocalRandom.current().nextInt(125));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                injector.getInstance(ClientThread.class).invokeLater(() -> {
                    if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) != null)
                        client.hopToWorld(rsWorld);
                });
            });
        } else {
            client.hopToWorld(rsWorld);
        }


    }

    private void logout() {
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
        client.invokeMenuAction(
                "Logout",
                "",
                1,
                MenuAction.CC_OP.getId(),
                -1,
                param1
        );
    }

    public boolean inWilderness() {
        return client.getVar(Varbits.IN_WILDERNESS) == 1;
    }

    public boolean isInWhitelist(String username) {
        username = username.toLowerCase().replace(" ", "_");
        String[] names = config.whitelist().toLowerCase().replace(" ", "_").split(",");

        for (String whitelisted : names) {
            if (whitelisted.isBlank() || whitelisted.isEmpty() || whitelisted.equals("_"))
                continue;

            //remove trailing whitespace on names.
            //if (whitelisted.charAt(whitelisted.length() - 1) == ' ')
            //	whitelisted = whitelisted.substring(0, whitelisted.length() - 1);

            if (whitelisted.equals(username))
                return true;
        }
        return false;
    }

    private boolean isPlayerSkulled(Player player) {
        if (player == null) {
            return false;
        }

        return player.getSkullIcon() == SkullIcon.SKULL;
    }

    public void pressKey(int key) {
        keyEvent(KeyEvent.KEY_PRESSED, key);
        keyEvent(KeyEvent.KEY_RELEASED, key);
    }

    private void keyEvent(int id, int key) {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, key, KeyEvent.CHAR_UNDEFINED
        );
        client.getCanvas().dispatchEvent(e);
    }

    public static void sleep(long time) {
        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
