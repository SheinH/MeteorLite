package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.packets.PacketWriter;

public class MousePackets {
	public static void queueClickPacket(int x, int y) {
		Game.getClient().setMouseLastPressedMillis(System.currentTimeMillis());
		int mousePressedTime = (int)((Game.getClient().getMouseLastPressedMillis()) - (Game.getClient().getClientMouseLastPressedMillis()));
		if (mousePressedTime < 0) {
			return;
		}

		if (mousePressedTime > 32767) {
			mousePressedTime = 32767;
		}

		Game.getClient().setClientMouseLastPressedMillis(Game.getClient().getMouseLastPressedMillis());
		int mouseInfo = (mousePressedTime << 1) + (1);
		queueClickPacket(mouseInfo, x, y);
	}

	public static void queueClickPacket(int mouseinfo, int x, int y){
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getClickPacket(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeShort$api(mouseinfo);
		packet.getPacketBuffer().writeShort$api(x);
		packet.getPacketBuffer().writeShort$api(y);
		writer.queuePacket(packet);
	}
}
