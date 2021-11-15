package meteor.plugins.worldmapwalker.overlay;

import dev.hoot.api.entities.Players;
import dev.hoot.api.movement.pathfinder.Walker;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OverlayUtils {
    static final Color TRANSPARENT_GREEN = new Color(0,255,0,128);
    static final Color TRANSPARENT_RED = new Color(255,0,0,128);
    public static List<WorldPoint> getCachedPath(WorldPoint destination){
        List<WorldPoint> path;
        try {
            path = Walker.PATH_CACHE.get(destination);
        }
        catch (ExecutionException e) {
            path = Walker.buildPath(destination, false);
        }
        return path;
    }
    // This is a performant way to get the current path
    // It takes the path from Walker's cache and shortens it based on the player's position
    public static List<WorldPoint> getDisplayPath(WorldPoint destination){
        var path = getCachedPath(destination);
        if(path == null)
            return null;
        var player = Players.getLocal();
        // Get the index of the WorldPoint that the player is closest to
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
        // If the player is too far from the path, just return the whole path
        if(minDist > 10){
            return path;
        }
        if(index > 0){
            index--;
        }
        // Return the path starting from the closest point to the player
        return path.subList(index,path.size());
    }
    //Takes a list of coordinates on screen and connects them together with a green line, discarding null values
    public static void drawPathOnScreen(Graphics2D graphics, List<Point> path){
        GeneralPath generalPath = new GeneralPath(Path2D.WIND_EVEN_ODD,path.size());
        for (var pathPoint : path) {
            if (pathPoint == null) {
                if (generalPath.getCurrentPoint() != null) {
                    graphics.setColor(TRANSPARENT_GREEN);
                    graphics.draw(generalPath);
                    generalPath.reset();
                }
            } else {
                if (generalPath.getCurrentPoint() == null) {
                    generalPath.moveTo(pathPoint.getX(), pathPoint.getY());
                } else {
                    generalPath.lineTo(pathPoint.getX(), pathPoint.getY());
                }
            }
        }
        if(generalPath.getCurrentPoint() != null){
            graphics.setColor(TRANSPARENT_GREEN);
            graphics.draw(generalPath);
        }
    }
    public static Point convertToSwingPoint(net.runelite.api.Point runelitePoint){
        return new Point(runelitePoint.getX(), runelitePoint.getY());
    }
}
