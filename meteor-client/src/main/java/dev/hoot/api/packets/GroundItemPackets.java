package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.TileItem;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.packets.PacketWriter;

import java.util.List;

public class GroundItemPackets {
    public static void queueGroundItemActionPacket(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGroundItemActionPacket(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByteA$api(worldPointX);
        packet.getPacketBuffer().writeShort$api(itemID);
        packet.getPacketBuffer().writeShort$api(worldPointY);
        packet.getPacketBuffer().write2$api(ctrlDown ? 1 : 0);
        writer.queuePacket(packet);
    }
    public static void queueGroundItemAction2Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGroundItemAction2Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByteC$api(worldPointY);
        packet.getPacketBuffer().write2$api(ctrlDown ? 1 : 0);
        packet.getPacketBuffer().writeByteB0$api(itemID);
        packet.getPacketBuffer().writeShort$api(worldPointX);
        writer.queuePacket(packet);
    }
    public static void queueGroundItemAction3Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGroundItemAction3Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByteA$api(worldPointY);
        packet.getPacketBuffer().write2$api(ctrlDown ? 1 : 0);
        packet.getPacketBuffer().writeByteA$api(worldPointX);
        packet.getPacketBuffer().writeByteA$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueGroundItemAction4Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGroundItemAction4Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByteA$api(worldPointY);
        packet.getPacketBuffer().writeShort$api(itemID);
        packet.getPacketBuffer().writeByteC$api(worldPointX);
        packet.getPacketBuffer().write2$api(ctrlDown ? 1 : 0);
        writer.queuePacket(packet);
    }
    public static void queueGroundItemAction5Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getGroundItemAction5Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().write1$api(ctrlDown ? 1 : 0);
        packet.getPacketBuffer().writeByteA$api(worldPointX);
        packet.getPacketBuffer().writeByteC$api(itemID);
        packet.getPacketBuffer().writeShort(worldPointY);
        writer.queuePacket(packet);
    }

    public static void groundItemFirstOption(TileItem item, boolean runEnabled){
        queueGroundItemActionPacket(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemSecondOption(TileItem item,boolean runEnabled){
        queueGroundItemAction2Packet(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemThirdOption(TileItem item,boolean runEnabled){
        queueGroundItemAction3Packet(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemFourthOption(TileItem item,boolean runEnabled){
        queueGroundItemAction4Packet(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemFifthOption(TileItem item,boolean runEnabled){
        queueGroundItemAction5Packet(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemAction(TileItem item, String action,boolean runEnabled) {
        List<String> actions = item.getActions();
        int index = actions.indexOf(action);
        switch (index) {
            case 0 -> groundItemFirstOption(item,runEnabled);
            case 1 -> groundItemSecondOption(item,runEnabled);
            case 2 -> groundItemThirdOption(item,runEnabled);
            case 3 -> groundItemFourthOption(item,runEnabled);
            case 4 -> groundItemFifthOption(item,runEnabled);
        }
    }
}
