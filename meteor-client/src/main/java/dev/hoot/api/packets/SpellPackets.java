package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import dev.hoot.api.magic.Spell;
import dev.hoot.api.widgets.Widgets;
import net.runelite.api.Item;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.packets.PacketWriter;
import net.runelite.api.widgets.Widget;

public class SpellPackets {
	public static void spellOnItem(Spell spell, Item item) {
		Widget spellWidget = Widgets.get(spell.getWidget());
		if (spellWidget == null) {
			return;
		}

		queueSpellOnItemPacket(spellWidget.getId(), spellWidget.getIndex(),
						item.getWidgetId(), item.getId(), item.getSlot());
	}

	public static void queueSpellOnItemPacket(int spellWidgetID, int spellWidgetIndex, int inventoryID, int itemID, int itemIndex) {
		PacketWriter writer = Game.getClient().getPacketWriter();
		PacketBufferNode packet = Game.getClient().preparePacket(Game.getClient().getItemOnWidgetPacket(), writer.getIsaacCipher());
		packet.getPacketBuffer().writeByteC$api(itemIndex);
		packet.getPacketBuffer().writeInt$api(spellWidgetID);
		packet.getPacketBuffer().writeShort$api(spellWidgetIndex);
		packet.getPacketBuffer().writeIntME2$api(inventoryID);
		packet.getPacketBuffer().writeByteA$api(itemID);
		writer.queuePacket(packet);
	}
}
