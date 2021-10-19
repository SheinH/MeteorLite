package meteor.plugins.webwalker;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.WebWalkerDebugRenderer;
import meteor.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;
import meteor.ui.overlay.worldmap.WorldMapOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Area;

public class WebWalkerWorldmapOverlay extends Overlay {
    private final Client client;
    private final WebWalker plugin;
    private final WebWalkerConfig config;

    @Inject
    private WorldMapOverlay worldMapOverlay;
    private Area mapClipArea;

    @Inject
    private WebWalkerWorldmapOverlay(final Client client, final WebWalker plugin, final WebWalkerConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!WalkerEngine.getInstance().isNavigating()) return null;
        WebWalkerDebugRenderer.renderWorldMap(graphics);
        return null;
        /*
        if (client.getWidget(WidgetInfo.WORLD_MAP_VIEW) == null) {
            return null;
        }
        mapClipArea = getWorldMapClipArea(client.getWidget(WidgetInfo.WORLD_MAP_VIEW).getBounds());

        if (plugin.path == null) return null;

        for (RSTile tile : plugin.path) {
            drawOnMap(graphics, tile.toWorldPoint(), Color.cyan);
        }
        return null;
        *

         */
    }


    private void drawOnMap(Graphics2D graphics, WorldPoint point, Color color) {
        Point start = worldMapOverlay.mapWorldPointToGraphicsPoint(point);
        Point end = worldMapOverlay.mapWorldPointToGraphicsPoint(point.dx(1).dy(-1));

        if (start == null || end == null) {
            return;
        }

        if (!mapClipArea.contains(start.getX(), start.getY()) || !mapClipArea.contains(end.getX(), end.getY())) {
            return;
        }

        graphics.setColor(color);
        graphics.fillRect(start.getX(), start.getY(), end.getX() - start.getX(), end.getY() - start.getY());
    }

    private Area getWorldMapClipArea(Rectangle baseRectangle) {
        final Widget overview = client.getWidget(WidgetInfo.WORLD_MAP_OVERVIEW_MAP);
        final Widget surfaceSelector = client.getWidget(WidgetInfo.WORLD_MAP_SURFACE_SELECTOR);

        Area clipArea = new Area(baseRectangle);

        if (overview != null && !overview.isHidden()) {
            clipArea.subtract(new Area(overview.getBounds()));
        }

        if (surfaceSelector != null && !surfaceSelector.isHidden()) {
            clipArea.subtract(new Area(surfaceSelector.getBounds()));
        }

        return clipArea;
    }
}