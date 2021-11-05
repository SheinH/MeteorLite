package meteor.plugins.worldmapwalker;

import com.google.inject.Provides;
import lombok.Getter;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.game.SpriteManager;
import meteor.input.KeyListener;
import meteor.input.KeyManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.entities.NPCs;
import meteor.plugins.api.entities.Players;
import meteor.plugins.api.movement.Movement;
import meteor.plugins.api.movement.Reachable;
import meteor.plugins.api.movement.pathfinder.Walker;
import meteor.plugins.api.scene.Tiles;
import meteor.plugins.worldmapwalker.overlay.WorldMapWalkerOverlay;
import meteor.plugins.worldmapwalker.overlay.WorldMapWalkerWorldMapOverlay;
import meteor.ui.overlay.OverlayManager;
import meteor.ui.overlay.worldmap.WorldMapOverlay;
import meteor.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static net.runelite.api.widgets.WidgetInfo.MINIMAP_WORLDMAP_OPTIONS;

@PluginDescriptor(
        name = "World Map Walker",
        description = "Right click anywhere within the World Map to walk there",
        enabledByDefault = false
)
@Singleton
public class WorldMapWalkerPlugin extends Plugin {

    @Inject
    private WorldMapOverlay worldMapOverlay;
    @Inject
    private WorldMapWalkerWorldMapOverlay worldMapWalkerWorldMapOverlayOverlay;
    @Getter
    @Inject
    WorldMapWalkerConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WorldMapWalkerOverlay overlay;
    @Inject
    private SpriteManager spriteManager;



    private Point lastMenuOpenedPoint;
    @Getter
    WorldPoint mapPoint;
    WorldMapPoint worldMapPoint;

    void setMapPoint(WorldPoint wp){
        mapPoint = wp;
    }

    private static final String DESTINATION_MENU_TARGET = "<col=00ff00>Destination";

    @Inject
    private KeyManager keyManager;
    private boolean hotKeyPressed;
    @Override
    public void startup() {
        keyManager.registerKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                hotKeyPressed = e.isShiftDown();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                hotKeyPressed = e.isShiftDown();
            }
        },WorldMapWalkerPlugin.class);
        overlayManager.add(overlay);
        overlayManager.add(worldMapWalkerWorldMapOverlayOverlay);
    }

    @Override
    public void shutdown() {
        overlayManager.remove(overlay);
    }

    @Provides
    public WorldMapWalkerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(WorldMapWalkerConfig.class);
    }

    private boolean destinationIsInScene(){
        return Tiles.getAt(mapPoint) != null;
    }

    private void walkUsingWalker(){
        if(!Movement.isWalking()){
            Movement.walkTo(mapPoint);
        }
    }


    public boolean darwinFilter(Player player){
        return player.getName().equals("Darwin");
    }

    //Special logic if the destination is loaded in scene
    private void handleLocalDestination(){
        //var darwin = Players.getAll(this::darwinFilter).get(0);
        var darwin = Players.getAll( x -> x.getName().equals("Darwin")).get(0);



        var player = client.getLocalPlayer();
        if(player == null || mapPoint == null)
            return;
        if(player.distanceTo(mapPoint) <= 1){
            setMapPoint(null);
            return;
        }
        //Getting path
        List<WorldPoint> path;
        try {
            path = Walker.PATH_CACHE.get(mapPoint);
        }
        catch (ExecutionException e) {
            path = Walker.buildPath(mapPoint, false);
        }
        if(path.size() < 2){
            setMapPoint(null);
            return;
        }
        for (int i = 0; i < path.size() - 1; i++) {
            //Check for doors along path
            Tile source = Tiles.getAt(path.get(i));
            Tile dest = Tiles.getAt(path.get(i + 1));
            //Path is not fully loaded in scene
            if(source == null || dest == null){
                walkUsingWalker();
                return;
            }
            //Path contains a door
            if(Reachable.isDoored(source,dest) || Reachable.isDoored(dest,source)){
                walkUsingWalker();
                return;
            }
        }
        walkDirectly();
    }

    //Click on point
    private void walkDirectly() {
        var destination = client.getLocalDestinationLocation();
        if(destination == null || !WorldPoint.fromLocal(client,destination).equals(mapPoint)){
            Movement.setDestination(mapPoint.getX(),mapPoint.getY());
            if(client.getLocalPlayer().distanceTo(mapPoint) < 7){
                setMapPoint(null);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {

        if (mapPoint == null || mapPoint.equals(Players.getLocal().getWorldLocation())) {
            setMapPoint(null);
            return;
        }
        if(client.getGameState() == GameState.LOADING){
            return;
        }
        if(destinationIsInScene()){
            handleLocalDestination();
            return;
        }

        if (Movement.isWalking()) {
            return;
        }
        logger.debug("Destination is {} {}", mapPoint.getX(), mapPoint.getY());
        Movement.walkTo(mapPoint);
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) { //TODO: Event doesn't work
        lastMenuOpenedPoint = client.getMouseCanvasPosition();
    }

    //Check if menu options have already been added
    private boolean menuContainsEntries(){
        MenuEntry[] entries = client.getMenuEntries();
        if(entries != null){
            for (MenuEntry entry : entries) {
                if(entry == null){
                    continue;
                }
                if(entry.getTarget().equals(DESTINATION_MENU_TARGET)){
                    return true;
                }
            }
        }
        return false;
    }

    public void reorderMenuEntries(){
        var entries = client.getMenuEntries();
        ArrayList<MenuEntry> pluginEntries = new ArrayList<>(entries.length);
        ArrayList<MenuEntry> otherEntries = new ArrayList<>(entries.length);
        for (MenuEntry e : entries) {
            if (e.getTarget() == DESTINATION_MENU_TARGET) {
                pluginEntries.add(e);
            }
            else {
                otherEntries.add(e);
            }
        }
        if(pluginEntries.isEmpty())
            return;
        otherEntries.removeIf(x -> x.getOption().equals("Focus on"));
        otherEntries.addAll(pluginEntries);
        client.setMenuEntries(otherEntries.toArray(new MenuEntry[0]));
    }

    @Subscribe
    public void onClientTick(ClientTick event) {

        final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);

        // If user clicks on map
        if (map != null && map.getBounds().contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())) {
            if (!menuContainsEntries()) {
                if(hotKeyPressed){
                    addMenuEntryFront( "Walk to");
                    addMenuEntry( "Clear",1);
                }
            }
        }
        // If user clicks on globe icon on minimap
        for(var menuEntry : client.getMenuEntries()) {
            if (menuEntry.getActionParam1() == MINIMAP_WORLDMAP_OPTIONS.getId()) {
                if (!menuContainsEntries()) {
                    addMenuEntry("Clear",1);
                    return;
                }
            }
            if (mapPoint != null && menuEntry.getOption().equals("Walk here")) {
                if (!menuContainsEntries()) {
                    addMenuEntry("Clear",1);
                    return;
                }
            }
        }
    }
    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);

        // If user clicks on map
        if (map != null && map.getBounds().contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())) {
            if (!menuContainsEntries()) {
                if(!hotKeyPressed){
                    addMenuEntry("Walk to",0);
                    addMenuEntry("Clear",0);
                }
                return;
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if(e.getMenuTarget().equals(DESTINATION_MENU_TARGET)) {
            if (e.getMenuOption().equals("Walk to")) {
                setMapPoint(calculateMapPoint(client.isMenuOpen() ? lastMenuOpenedPoint : client.getMouseCanvasPosition()));
                Movement.walkTo(mapPoint);
                logger.debug("Walking to: {}", mapPoint.toString());
            }

            if (e.getMenuOption().equals("Clear")) {
                logger.info("Stopping walking");
                setMapPoint(null);
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.LOGIN_SCREEN) {
            client.setUsername("");
            client.setPassword("");
        }
    }

    private WorldPoint calculateMapPoint(Point point) {
        float zoom = client.getRenderOverview().getWorldMapZoom();
        RenderOverview renderOverview = client.getRenderOverview();
        final WorldPoint mapPoint = new WorldPoint(renderOverview.getWorldMapPosition().getX(), renderOverview.getWorldMapPosition().getY(), 0);
        final Point middle = worldMapOverlay.mapWorldPointToGraphicsPoint(mapPoint);

        final int dx = (int) ((point.getX() - middle.getX()) / zoom);
        final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

        return mapPoint.dx(dx).dy(dy);
    }

    private void addMenuEntry( String option, int index) {
        List<MenuEntry> entries = new ArrayList<>(Arrays.asList(client.getMenuEntries()));

        MenuEntry entry = new MenuEntry();
        entry.setOption(option);
        entry.setTarget(DESTINATION_MENU_TARGET);
        entry.setOpcode(MenuAction.RUNELITE.getId());
        entries.add(index,entry);

        client.setMenuEntries(entries.toArray(new MenuEntry[0]));
    }
    private void addMenuEntryFront(String option) {
        client.insertMenuItem(option, DESTINATION_MENU_TARGET, MenuAction.UNKNOWN.getId(), 0, 0, 0,false);
        /*List<MenuEntry> entries = new LinkedList<>(Arrays.asList(client.getMenuEntries()));

        MenuEntry entry = new MenuEntry();
        entry.setOption(option);
        entry.setTarget(DESTINATION_MENU_TARGET);
        entry.setOpcode(MenuAction.RUNELITE.getId());
        entries.add(entry);

        client.setMenuEntries(entries.toArray(new MenuEntry[0]));*/
    }

    @Subscribe
    private void onFocusChanged(FocusChanged focusChanged)
    {
        if (!focusChanged.isFocused())
        {
            hotKeyPressed = false;
        }
    }

}
