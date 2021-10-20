package meteor.plugins.api.packets;

import meteor.plugins.api.game.Game;
import meteor.plugins.api.input.Mouse;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.packets.PacketWriter;

import java.util.List;

public class NPCPackets {
    public static void queueNPCActionPacket(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCActionPacket(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByteA$api(ctrlDown);
        packet.getPacketBuffer().writeShort$api(NPCIndex);
        writer.queuePacket(packet);
    }

    public static void queueNPCAction2Packet(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCAction2Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByte01$api(ctrlDown);
        packet.getPacketBuffer().writeShortA$api(NPCIndex);
        writer.queuePacket(packet);
    }

    public static void queueNPCAction3Packet(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCAction3Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByte$api(ctrlDown);
        packet.getPacketBuffer().writeShort01$api(NPCIndex);
        writer.queuePacket(packet);
    }

    public static void queueNPCAction4Packet(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCAction4Packet(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeByteA$api(ctrlDown);
        packet.getPacketBuffer().writeShort$api(NPCIndex);
        writer.queuePacket(packet);
    }

    public static void queueNPCAction5Packet(int NPCIndex, int ctrlDown) {
        PacketWriter writer = Game.getClient().getPacketWriter();
        PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getNPCActionPacket(), writer.getIsaacCipher());
        packet.getPacketBuffer().writeShortA$api(NPCIndex);
        packet.getPacketBuffer().writeByte01A$api(ctrlDown);
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
