package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.packets.PacketWriter;
import net.runelite.api.widgets.WidgetInfo;

import java.util.List;

public class ItemPackets {
	public static void itemAction(Item item, String action) {
		List<String> actions = item.getActions();
		int index = actions.indexOf(action);
		switch (index) {
			case 0 -> itemFirstOption(item);
			case 1 -> itemSecondOption(item);
			case 2 -> itemThirdOption(item);
			case 3 -> itemFourthOption(item);
			case 4 -> itemFifthOption(item);
			default ->{
				WidgetPackets.widgetAction(Game.getClient().getWidget(item.getWidgetId()),action);
			}
		}
	}

	public static void itemFirstOption(Item item) {
		queueItemActionPacket(item.getWidgetId(), item.getId(), item.getSlot());
	}

	public static void itemSecondOption(Item item) {
		queueItemAction2Packet(item.getWidgetId(), item.getId(), item.getSlot());
	}
	public static void itemThirdOption(Item item) {
		queueItemAction3Packet(item.getWidgetId(), item.getId(), item.getSlot());
	}
	public static void itemFourthOption(Item item) {
		queueItemAction4Packet(item.getWidgetId(), item.getId(), item.getSlot());
	}

	public static void itemFifthOption(Item item) {
		queueItemAction5Packet(item.getWidgetId(), item.getId(), item.getSlot());
	}

	public static void useItemOnItem(Item item, Item item2) {
		if (item.getType().getInventoryID() != InventoryID.INVENTORY
						|| item2.getType().getInventoryID() != InventoryID.INVENTORY) {
			return;
		}

		queueItemOnItemPacket(item.getId(), item.getSlot(), item2.getId(), item2.getSlot());
	}

	public static void useItemOnTileObject(Item item, TileObject object) {
		WorldPoint wp = object.getWorldLocation();
		queueItemUseOnTileObjectPacket(object.getId(), wp.getX(), wp.getY(), item.getSlot(), item.getId(), item.getWidgetId(), 0);
	}
	public static void queueBankItemActionPacket(int inventoryID, int itemID, int itemSlot){
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getBankItemActionPacket(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeInt$api(inventoryID);
		packet.getPacketBuffer().writeShort$api(itemSlot);
		packet.getPacketBuffer().writeShort$api(itemID);
		writer.queuePacket(packet);
	}
	public static void queueItemOnNpcPacket(int npcIndex,int itemWidgetID,int itemID,int itemSlot,int ctrlDown){
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemUseOnNpcPacket(), writer.getIsaacCipher());
		packet.getPacketBuffer().write2$api(ctrlDown);
		packet.getPacketBuffer().writeByteA$api(itemID);
		packet.getPacketBuffer().writeByteB0$api(itemSlot);
		packet.getPacketBuffer().writeIntME3$api(itemWidgetID);
		packet.getPacketBuffer().writeByteC$api(npcIndex);
	}
	public static void queueItemUseOnTileObjectPacket(int objectID, int objectX, int objectY, int itemSlot, int itemID, int itemWidgetID, int ctrlDown){
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemUseOnGameObjectPacket(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeByteB0$api(itemSlot);
		packet.getPacketBuffer().writeByteB0$api(objectY);
		packet.getPacketBuffer().writeIntME2$api(itemWidgetID);
		packet.getPacketBuffer().writeByteA$api(objectX);
		packet.getPacketBuffer().writeByteB0$api(objectID);
		packet.getPacketBuffer().writeByteC$api(itemID);
		packet.getPacketBuffer().writeByteB$api(ctrlDown);
		writer.queuePacket(packet);
	}
	public static void queueItemActionPacket(int inventoryID, int itemID, int itemSlot) {
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemActionPacket(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeByteC$api(itemID);
		packet.getPacketBuffer().writeByteB0$api(itemSlot);
		packet.getPacketBuffer().writeIntME$api(inventoryID);
		writer.queuePacket(packet);
	}
	public static void queueItemAction3Packet(int inventoryID, int itemID, int itemSlot){
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemAction3Packet(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeByteA$api(itemID);
		packet.getPacketBuffer().writeByteA$api(itemSlot);
		packet.getPacketBuffer().writeIntME$api(inventoryID);
		writer.queuePacket(packet);
	}
	public static void queueItemAction4Packet(int inventoryID, int itemID, int itemSlot){
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemAction4Packet(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeByteC$api(itemID);
		packet.getPacketBuffer().writeByteA$api(itemSlot);
		packet.getPacketBuffer().writeIntME$api(inventoryID);
		writer.queuePacket(packet);
	}

	public static void queueItemAction2Packet(int inventoryID, int itemID, int itemSlot) {
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemAction2Packet(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeByteC$api(itemID);
		packet.getPacketBuffer().writeByteB0$api(itemSlot);
		packet.getPacketBuffer().writeIntME3$api(inventoryID);
		writer.queuePacket(packet);
	}
	
	public static void queueItemAction5Packet(int inventoryID, int itemID, int itemSlot) {
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemAction5Packet(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeByteA$api(itemSlot); //var0
		packet.getPacketBuffer().writeShort$api(itemID); //var3
		packet.getPacketBuffer().writeIntME2$api(inventoryID); //var1
		writer.queuePacket(packet);
	}

	public static void queueItemOnItemPacket(int itemId1, int slot1, int itemId2, int slot2) {
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemOnItemPacket(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeByteA$api(itemId2);
		packet.getPacketBuffer().writeIntME2$api(WidgetInfo.INVENTORY.getPackedId());
		packet.getPacketBuffer().writeShort$api(slot2);
		packet.getPacketBuffer().writeIntME2$api(WidgetInfo.INVENTORY.getPackedId());
		packet.getPacketBuffer().writeByteC$api(itemId1);
		packet.getPacketBuffer().writeByteA$api(slot1);
		writer.queuePacket(packet);
	}
}
