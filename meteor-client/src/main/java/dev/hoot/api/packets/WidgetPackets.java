package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.packets.PacketWriter;
import net.runelite.api.widgets.Widget;

import java.util.List;

public class WidgetPackets {

    public static void queueWidgetActionPacket(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetActionPacket(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }

    public static void queueResumePauseWidgetPacket(int var1, int var2){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getResumePauseWidgetPacket(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeIntME2$api(var1);
        packet.getPacketBuffer().writeShort(var2);
    }
    public static void queueWidgetAction2Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetAction2Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueWidgetAction3Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetAction3Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueWidgetAction4Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetAction4Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueWidgetAction5Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetAction5Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueWidgetAction6Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetAction6Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueWidgetAction7Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetAction7Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueWidgetAction8Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetAction8Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueWidgetAction9Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getBankItemActionPacket(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void queueWidgetAction10Packet(int widgetID, int itemID, int itemSlot){
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getWidgetAction10Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeInt$api(widgetID);
        packet.getPacketBuffer().writeShort$api(itemSlot);
        packet.getPacketBuffer().writeShort$api(itemID);
        writer.queuePacket(packet);
    }
    public static void widgetFirstOption(Widget widget){
        queueWidgetActionPacket(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetSecondOption(Widget widget){
        queueWidgetAction2Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetThirdOption(Widget widget){
        queueWidgetAction3Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetFourthOption(Widget widget){
        queueWidgetAction4Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetFifthOption(Widget widget){
        queueWidgetAction5Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetSixthOption(Widget widget){
        queueWidgetAction6Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetSeventhOption(Widget widget){
        queueWidgetAction7Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetEighthOption(Widget widget){
        queueWidgetAction8Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetNinthOption(Widget widget){
        queueWidgetAction9Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetTenthOption(Widget widget){
        queueWidgetAction10Packet(widget.getId(),widget.getItemId(),widget.getIndex());
    }
    public static void widgetAction(Widget widget,String action){
        List<String> actions = widget.getActions();
        if(actions==null){
            return;
        }
        int index = actions.indexOf(action);
        switch (index) {
            case 0 -> widgetFirstOption(widget);
            case 1 -> widgetSecondOption(widget);
            case 2 -> widgetThirdOption(widget);
            case 3 -> widgetFourthOption(widget);
            case 4 -> widgetFifthOption(widget);
            case 5 -> widgetSixthOption(widget);
            case 6 -> widgetSeventhOption(widget);
            case 7 -> widgetEighthOption(widget);
            case 8 -> widgetNinthOption(widget);
            case 9 -> widgetTenthOption(widget);
        }
    }
}
