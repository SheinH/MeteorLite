package meteor.plugins.api.packets;

import meteor.plugins.api.game.Game;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.packets.PacketWriter;

import java.util.List;

public class TileObjectPackets {

    public static void queueTileObjectActionPacket(int objectID, int worldPointX, int worldPointY, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGameObjectActionPacket(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeShort01A$api(objectID);
        packet.getPacketBuffer().writeByte01A$api(ctrlDown);
        packet.getPacketBuffer().writeShort01A$api(worldPointY);
        packet.getPacketBuffer().writeShort01A$api(worldPointX);
        writer.queuePacket(packet);
    }

    public static void queueTileObjectAction2Packet(int objectID, int worldPointX, int worldPointY, int ctrlDown){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGameObjectAction2Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeShort01A$api(worldPointY);
        packet.getPacketBuffer().writeShortA$api(worldPointX);
        packet.getPacketBuffer().writeByte$api(ctrlDown);
        packet.getPacketBuffer().writeShortA$api(objectID);
        writer.queuePacket(packet);
    }

    public static void queueTileObjectAction3Packet(int objectID, int worldPointX, int worldPointY, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGameObjectAction3Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeShort01$api(worldPointY);
        packet.getPacketBuffer().writeByte$api(ctrlDown);
        packet.getPacketBuffer().writeShort01A$api(worldPointX);
        packet.getPacketBuffer().writeShortA$api(objectID);
        writer.queuePacket(packet);
    }
    public static void queueTileObjectAction4Packet(int objectID, int worldPointX, int worldPointY, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGameObjectAction4Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeShortA$api(worldPointY);
        packet.getPacketBuffer().writeShortA$api(worldPointX);
        packet.getPacketBuffer().writeByte01A$api(ctrlDown);
        packet.getPacketBuffer().writeShort01A$api(objectID);
        writer.queuePacket(packet);
    }
    public static void queueTileObjectAction5Packet(int objectID, int worldPointX, int worldPointY, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGameObjectAction5Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeShort01$api(objectID);
        packet.getPacketBuffer().writeShort01$api(worldPointY);
        packet.getPacketBuffer().writeShort01$api(worldPointX);
        packet.getPacketBuffer().writeByte01$api(ctrlDown);
        writer.queuePacket(packet);
    }
    public static void tileObjectFirstOption(TileObject object, int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectActionPacket(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectSecondOption(TileObject object,int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectAction2Packet(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectThirdOption(TileObject object,int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectAction3Packet(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectFourthOption(TileObject object,int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectAction4Packet(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectFifthOption(TileObject object,int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectAction5Packet(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectAction(TileObject object, String action, int ctrlDown) {
        List<String> actions = object.getActions();
        int index = actions.indexOf(action);
        switch (index) {
            case 0 -> tileObjectFirstOption(object,ctrlDown);
            case 1 -> tileObjectSecondOption(object,ctrlDown);
            case 2 -> tileObjectThirdOption(object,ctrlDown);
            case 3 -> tileObjectFourthOption(object,ctrlDown);
            case 4 -> tileObjectFifthOption(object,ctrlDown);
        }
    }
}
