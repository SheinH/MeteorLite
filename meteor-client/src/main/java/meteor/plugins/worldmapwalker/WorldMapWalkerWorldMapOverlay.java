package meteor.plugins.worldmapwalker;

import meteor.game.SpriteManager;
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
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WorldMapWalkerWorldMapOverlay extends Overlay {
    WorldMapWalkerPlugin plugin;
    static final Color TRANSPARENT_GREEN = new Color(0,255,0,128);
    static final Color TRANSPARENT_RED = new Color(255,0,0,128);
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


    public void renderPathOnWorldMap(Graphics2D graphics, List<WorldPoint> path, int startIndex){
        GeneralPath generalPath = new GeneralPath(Path2D.WIND_EVEN_ODD,path.size());
        for (int i = startIndex; i < path.size(); i++) {
            var currentWorldPoint = path.get(i);
            final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
            Point pathPoint = worldMapOverlay.mapWorldPointToGraphicsPoint(currentWorldPoint);
            if(map.getBounds().contains(pathPoint.getX(),pathPoint.getY())){
                if (generalPath.getCurrentPoint() == null) {
                    generalPath.moveTo(pathPoint.getX(), pathPoint.getY());
                } else {
                    generalPath.lineTo(pathPoint.getX(), pathPoint.getY());
                }
            }
            else{
                graphics.setColor(TRANSPARENT_GREEN);
                graphics.draw(generalPath);
                generalPath.reset();
            }
        }
        if(generalPath.getCurrentPoint() != null){
            graphics.setColor(TRANSPARENT_GREEN);
            graphics.draw(generalPath);
        }
    }
    BufferedImage flagSprite;

    @Override
    public Dimension render(Graphics2D graphics) {
        if(plugin.mapPoint == null || !plugin.config.showPaths()){
            return null;
        }
        List<WorldPoint> path;
        try {
            path = Walker.PATH_CACHE.get(plugin.mapPoint);
        }
        catch (ExecutionException e) {
            path = Walker.buildPath(plugin.mapPoint, false);
        }
        if(path.isEmpty()){
            return null;
        }
        var player = Players.getLocal();
        int minDist = path.get(0).distanceTo(player);
        int index = 0;
        for (int i = 1; i < path.size(); i++) {
            var dist = path.get(i).distanceTo(player);
            if(dist == 0){
                index = i;
                minDist = 0;
                break;
            }
            if(dist < minDist){
                index = i;
                minDist = dist;
            }
        }
        if(index > 0){
            index--;
        }
        final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
        if(map != null){
            renderPathOnWorldMap(graphics,path,index);
            Point pathPoint = worldMapOverlay.mapWorldPointToGraphicsPoint(path.get(path.size() - 1));
            //Offset
            pathPoint = pathPoint.offset(-7,-15);
            if(map.getBounds().contains(pathPoint.getX(),pathPoint.getY())){
                if(flagSprite == null){
                    flagSprite = spriteManager.getSprite(SpriteID.MINIMAP_DESTINATION_FLAG,0);
                }
                graphics.drawImage(flagSprite,pathPoint.getX(),pathPoint.getY(),null);
            }
        }
        return null;
    }
}
