package meteor.plugins.worldmapwalker.overlay;

import meteor.plugins.api.entities.Players;
import meteor.plugins.api.game.Game;
import meteor.plugins.api.movement.pathfinder.Walker;
import meteor.plugins.worldmapwalker.WorldMapWalkerPlugin;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;
import meteor.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Singleton
public class WorldMapWalkerOverlay extends Overlay {
    WorldMapWalkerPlugin plugin;
    @Inject
    private WorldMapOverlay worldMapOverlay;

    @Inject
    private WorldMapWalkerOverlay(WorldMapWalkerPlugin plugin) {
        this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGHEST);
    }

    private Point worldPointToScreenPoint(WorldPoint p){
        var localPoint = LocalPoint.fromWorld(client,p);
        if(localPoint == null)
            return null;
        var canvasPoint = Perspective.localToCanvas(client,localPoint,client.getPlane());
        if(canvasPoint == null)
            return null;
        return OverlayUtils.convertToSwingPoint(canvasPoint);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(plugin.getMapPoint() == null || !plugin.getConfig().showPaths()){
            return null;
        }
        var path = OverlayUtils.getDisplayPath(plugin.getMapPoint());
        if(path == null || path.isEmpty())
            return null;
        var screenPath = path.stream().map(this::worldPointToScreenPoint).collect(Collectors.toList());
        OverlayUtils.drawPathOnScreen(graphics,screenPath);
        return null;
    }
}
