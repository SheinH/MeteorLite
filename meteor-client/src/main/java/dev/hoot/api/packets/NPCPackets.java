package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.NPC;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.packets.PacketWriter;

import java.util.List;

public class NPCPackets {
    public static void queueNPCActionPacket(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCActionPacket(), writer.getIsaacCipher());
        packet.getPacketBuffer().write2$api(ctrlDown);
        packet.getPacketBuffer().writeByteB0$api(NPCIndex);
        writer.queuePacket(packet);
    }

    public static void queueNPCAction2Packet(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCAction2Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByteB0$api(NPCIndex);
        packet.getPacketBuffer().writeByte$api(ctrlDown);
        writer.queuePacket(packet);
    }

    public static void queueNPCAction3Packet(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCAction3Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().write2$api(ctrlDown);
        packet.getPacketBuffer().writeByteA$api(NPCIndex);
        writer.queuePacket(packet);
    }

    public static void queueNPCAction4Packet(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCAction4Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByteB0$api(NPCIndex);
        packet.getPacketBuffer().writeByteB$api(ctrlDown);
        writer.queuePacket(packet);
    }

    public static void queueNPCAction5Packet(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCAction5Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().write1$api(ctrlDown);
        packet.getPacketBuffer().writeShort$api(NPCIndex);
        writer.queuePacket(packet);
    }
    public static void npcFirstOption(NPC npc, int ctrlDown) {
        queueNPCActionPacket(npc.getIndex(),ctrlDown);
    }
    public static void npcSecondOption(NPC npc, int ctrlDown) {
        queueNPCAction2Packet(npc.getIndex(),ctrlDown);
    }
    public static void npcThirdOption(NPC npc, int ctrlDown) {
        queueNPCAction3Packet(npc.getIndex(),ctrlDown);
    }
    public static void npcFourthOption(NPC npc, int ctrlDown) {
        queueNPCAction4Packet(npc.getIndex(),ctrlDown);
    }
    public static void npcFifthOption(NPC npc, int ctrlDown) {
        queueNPCAction5Packet(npc.getIndex(),ctrlDown);
    }
    public static void npcAction(NPC npc, String action, int ctrlDown) {
        List<String> actions = npc.getActions();
        int index = actions.indexOf(action);
        switch (index) {
            case 0 -> npcFirstOption(npc,ctrlDown);
            case 1 -> npcSecondOption(npc,ctrlDown);
            case 2 -> npcThirdOption(npc,ctrlDown);
            case 3 -> npcFourthOption(npc,ctrlDown);
            case 4 -> npcFifthOption(npc,ctrlDown);
        }
    }

}
