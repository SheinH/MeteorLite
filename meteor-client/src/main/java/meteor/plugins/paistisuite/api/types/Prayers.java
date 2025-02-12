/*
 * Copyright (c) 2019-2020, ganom <https://github.com/Ganom>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package meteor.plugins.paistisuite.api.types;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.WidgetInfo;

import javax.annotation.Nullable;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Prayers
{
	THICK_SKIN("Thick Skin", WidgetInfo.PRAYER_THICK_SKIN, Varbits.PRAYER_THICK_SKIN),
	BURST_OF_STRENGTH("Burst of Strength", WidgetInfo.PRAYER_BURST_OF_STRENGTH, Varbits.PRAYER_BURST_OF_STRENGTH),
	CLARITY_OF_THOUGHT("Clarity of Thought", WidgetInfo.PRAYER_CLARITY_OF_THOUGHT, Varbits.PRAYER_CLARITY_OF_THOUGHT),
	SHARP_EYE("Sharp Eye", WidgetInfo.PRAYER_SHARP_EYE, Varbits.PRAYER_SHARP_EYE),
	MYSTIC_WILL("Mystic Will", WidgetInfo.PRAYER_MYSTIC_WILL, Varbits.PRAYER_MYSTIC_WILL),
	ROCK_SKIN("Rock Skin", WidgetInfo.PRAYER_ROCK_SKIN, Varbits.PRAYER_ROCK_SKIN),
	SUPERHUMAN_STRENGTH("Superhuman Strength", WidgetInfo.PRAYER_SUPERHUMAN_STRENGTH, Varbits.PRAYER_SUPERHUMAN_STRENGTH),
	IMPROVED_REFLEXES("Improved Reflexes", WidgetInfo.PRAYER_IMPROVED_REFLEXES, Varbits.PRAYER_IMPROVED_REFLEXES),
	RAPID_RESTORE("Rapid Restore", WidgetInfo.PRAYER_RAPID_RESTORE, Varbits.PRAYER_RAPID_RESTORE),
	RAPID_HEAL("Rapid Heal", WidgetInfo.PRAYER_RAPID_HEAL, Varbits.PRAYER_RAPID_HEAL),
	PROTECT_ITEM("Protect Item", WidgetInfo.PRAYER_PROTECT_ITEM, Varbits.PRAYER_PROTECT_ITEM),
	HAWK_EYE("Hawk Eye", WidgetInfo.PRAYER_HAWK_EYE, Varbits.PRAYER_HAWK_EYE),
	MYSTIC_LORE("Mystic Lore", WidgetInfo.PRAYER_MYSTIC_LORE, Varbits.PRAYER_MYSTIC_LORE),
	STEEL_SKIN("Steel Skin", WidgetInfo.PRAYER_STEEL_SKIN, Varbits.PRAYER_STEEL_SKIN),
	ULTIMATE_STRENGTH("Ultimate Strength", WidgetInfo.PRAYER_ULTIMATE_STRENGTH, Varbits.PRAYER_ULTIMATE_STRENGTH),
	INCREDIBLE_REFLEXES("Incredible Reflexes", WidgetInfo.PRAYER_INCREDIBLE_REFLEXES, Varbits.PRAYER_INCREDIBLE_REFLEXES),
	PROTECT_FROM_MAGIC("Protect from Magic", WidgetInfo.PRAYER_PROTECT_FROM_MAGIC, Varbits.PRAYER_PROTECT_FROM_MAGIC),
	PROTECT_FROM_MISSILES("Protect from Missiles", WidgetInfo.PRAYER_PROTECT_FROM_MISSILES, Varbits.PRAYER_PROTECT_FROM_MISSILES),
	PROTECT_FROM_MELEE("Protect from Melee", WidgetInfo.PRAYER_PROTECT_FROM_MELEE, Varbits.PRAYER_PROTECT_FROM_MELEE),
	EAGLE_EYE("Eagle Eye", WidgetInfo.PRAYER_EAGLE_EYE, Varbits.PRAYER_EAGLE_EYE),
	MYSTIC_MIGHT("Mystic Might", WidgetInfo.PRAYER_MYSTIC_MIGHT, Varbits.PRAYER_MYSTIC_MIGHT),
	RETRIBUTION("Retribution", WidgetInfo.PRAYER_RETRIBUTION, Varbits.PRAYER_RETRIBUTION),
	REDEMPTION("Redemption", WidgetInfo.PRAYER_REDEMPTION, Varbits.PRAYER_REDEMPTION),
	SMITE("Smite", WidgetInfo.PRAYER_SMITE, Varbits.PRAYER_SMITE),
	CHIVALRY("Chivalry", WidgetInfo.PRAYER_CHIVALRY, Varbits.PRAYER_CHIVALRY),
	PIETY("Piety", WidgetInfo.PRAYER_PIETY, Varbits.PRAYER_PIETY),
	PRESERVE("Preserve", WidgetInfo.PRAYER_PRESERVE, Varbits.PRAYER_PRESERVE),
	RIGOUR("Rigour", WidgetInfo.PRAYER_RIGOUR, Varbits.PRAYER_RIGOUR),
	AUGURY("Augury", WidgetInfo.PRAYER_AUGURY, Varbits.PRAYER_AUGURY);

	private final String name;
	private final WidgetInfo info;
	private final Varbits varbitInfo;
	private static final Map<String, WidgetInfo> map;

	static
	{
		ImmutableMap.Builder<String, WidgetInfo> builder = ImmutableMap.builder();

		for (Prayers spells : values())
		{
			builder.put(spells.getName(), spells.getInfo());
		}

		map = builder.build();
	}

	@Nullable
	public static WidgetInfo getWidget(String prayer)
	{
		return map.getOrDefault(prayer, null);
	}
}
