package meteor.plugins.worldmapwalker.overlay;

import meteor.game.SpriteManager;
import meteor.plugins.worldmapwalker.WorldMapWalkerPlugin;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.Collectors;

public class WorldMapWalkerWorldMapOverlay extends Overlay {
    WorldMapWalkerPlugin plugin;
    @Inject
    private WorldMapOverlay worldMapOverlay;
    @Inject
    private SpriteManager spriteManager;

    @Inject
    private WorldMapWalkerWorldMapOverlay(WorldMapWalkerPlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }


    BufferedImage flagSprite;

    private Point worldPointToScreenPoint(WorldPoint p, Widget map) {
        if (p == null)
            return null;
        var canvasPoint = worldMapOverlay.mapWorldPointToGraphicsPoint(p);
        if (canvasPoint == null) {
            return null;
        }
        if (!map.contains(canvasPoint)) {
            return null;
        }
        return OverlayUtils.convertToSwingPoint(canvasPoint);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.mapPoint == null || !plugin.getConfig().showPaths()) {
            return null;
        }
        final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
        if (map == null)
            return null;
        var path = OverlayUtils.getDisplayPath(plugin.mapPoint);
        if (path == null)
            return null;
        var screenPath = path.stream().map(x -> worldPointToScreenPoint(x, map)).collect(Collectors.toList());
        OverlayUtils.drawPathOnScreen(graphics, screenPath);

        var destinationFlagPoint = worldMapOverlay.mapWorldPointToGraphicsPoint(plugin.mapPoint);
        if (destinationFlagPoint == null)
            return null;
        destinationFlagPoint = destinationFlagPoint.offset(-6, -14);
        if (map.getBounds().contains(destinationFlagPoint.getX(), destinationFlagPoint.getY())) {
            if (flagSprite == null) {
                flagSprite = spriteManager.getSprite(SpriteID.MINIMAP_DESTINATION_FLAG, 0);
            }
            graphics.drawImage(flagSprite, destinationFlagPoint.getX(), destinationFlagPoint.getY(), null);
        }
        return null;
    }
}
