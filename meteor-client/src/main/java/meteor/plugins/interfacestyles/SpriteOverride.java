/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * Copyright (c) 2018, Raqes <j.raqes@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package meteor.plugins.interfacestyles;

import lombok.Getter;
import net.runelite.api.SpriteID;
import static meteor.plugins.interfacestyles.Skin.AROUND_2005;
import static meteor.plugins.interfacestyles.Skin.AROUND_2006;
import static meteor.plugins.interfacestyles.Skin.AROUND_2010;

@Getter
enum SpriteOverride
{
	TAB_COMBAT(SpriteID.TAB_COMBAT, AROUND_2005, AROUND_2010),
	TAB_STATS(SpriteID.TAB_STATS, AROUND_2005, AROUND_2010),
	TAB_QUESTS(SpriteID.TAB_QUESTS, AROUND_2005),
	TAB_QUESTS_PURPLE_KOUREND_1299(SpriteID.TAB_QUESTS_PURPLE_KOUREND, AROUND_2005),
	TAB_QUESTS_RED_MINIGAMES(SpriteID.TAB_QUESTS_RED_MINIGAMES, AROUND_2005),
	TAB_QUESTS_GREEN_ACHIEVEMENT_DIARIES(SpriteID.TAB_QUESTS_GREEN_ACHIEVEMENT_DIARIES, AROUND_2005),
	TAB_INVENTORY(SpriteID.TAB_INVENTORY, AROUND_2005, AROUND_2010),
	TAB_EQUIPMENT(SpriteID.TAB_EQUIPMENT, AROUND_2005, AROUND_2010),
	TAB_PRAYER(SpriteID.TAB_PRAYER, AROUND_2005, AROUND_2010),
	TAB_MAGIC(SpriteID.TAB_MAGIC, AROUND_2005, AROUND_2010),
	TAB_MAGIC_SPELLBOOK_ANCIENT_MAGICKS(SpriteID.TAB_MAGIC_SPELLBOOK_ANCIENT_MAGICKS, AROUND_2005),
	TAB_MAGIC_SPELLBOOK_LUNAR(SpriteID.TAB_MAGIC_SPELLBOOK_LUNAR, AROUND_2005),
	TAB_MAGIC_SPELLBOOK_ARCEUUS(SpriteID.TAB_MAGIC_SPELLBOOK_ARCEUUS, AROUND_2005),
	TAB_CLAN_CHAT(SpriteID.TAB_FRIENDS_CHAT, AROUND_2005, AROUND_2010),
	TAB_FRIENDS(SpriteID.TAB_FRIENDS, AROUND_2005, AROUND_2010),
	TAB_IGNORES(SpriteID.TAB_IGNORES, AROUND_2005, AROUND_2010),
	TAB_LOGOUT(SpriteID.TAB_LOGOUT, AROUND_2005, AROUND_2010),
	TAB_OPTIONS(SpriteID.TAB_OPTIONS, AROUND_2005, AROUND_2010),
	TAB_EMOTES(SpriteID.TAB_EMOTES, AROUND_2005, AROUND_2010),
	TAB_MUSIC(SpriteID.TAB_MUSIC, AROUND_2005, AROUND_2010),
	TAB_CHATBOX(SpriteID.CHATBOX, AROUND_2005),

	BUTTON_FRIENDS(SpriteID.BUTTON_FRIENDS, AROUND_2005),
	BUTTON_IGNORES(SpriteID.BUTTON_IGNORES, AROUND_2005),

	SKILL_ATTACK(SpriteID.SKILL_ATTACK, AROUND_2010),
	SKILL_STRENGTH(SpriteID.SKILL_STRENGTH, AROUND_2010),
	SKILL_DEFENCE(SpriteID.SKILL_DEFENCE, AROUND_2010),
	SKILL_RANGED(SpriteID.SKILL_RANGED, AROUND_2010),
	SKILL_PRAYER(SpriteID.SKILL_PRAYER, AROUND_2005, AROUND_2010),
	SKILL_MAGIC(SpriteID.SKILL_MAGIC, AROUND_2010),
	SKILL_HITPOINTS(SpriteID.SKILL_HITPOINTS, AROUND_2010),
	SKILL_AGILITY(SpriteID.SKILL_AGILITY, AROUND_2010),
	SKILL_HERBLORE(SpriteID.SKILL_HERBLORE, AROUND_2010),
	SKILL_THIEVING(SpriteID.SKILL_THIEVING, AROUND_2010),
	SKILL_CRAFTING(SpriteID.SKILL_CRAFTING, AROUND_2010),
	SKILL_FLETCHING(SpriteID.SKILL_FLETCHING, AROUND_2010),
	SKILL_MINING(SpriteID.SKILL_MINING, AROUND_2010),
	SKILL_SMITHING(SpriteID.SKILL_SMITHING, AROUND_2010),
	SKILL_FISHING(SpriteID.SKILL_FISHING, AROUND_2010),
	SKILL_COOKING(SpriteID.SKILL_COOKING, AROUND_2010),
	SKILL_FIREMAKING(SpriteID.SKILL_FIREMAKING, AROUND_2010),
	SKILL_WOODCUTTING(SpriteID.SKILL_WOODCUTTING, AROUND_2010),
	SKILL_RUNECRAFT(SpriteID.SKILL_RUNECRAFT, AROUND_2010),
	SKILL_SLAYER(SpriteID.SKILL_SLAYER, AROUND_2010),
	SKILL_HUNTER(SpriteID.SKILL_HUNTER, AROUND_2010),
	SKILL_CONSTRUCTION(SpriteID.SKILL_CONSTRUCTION, AROUND_2010),

	COMPASS(SpriteID.COMPASS_TEXTURE, AROUND_2005),
	WINDOW_CLOSE_BUTTON_RED_X(SpriteID.WINDOW_CLOSE_BUTTON_RED_X, AROUND_2010),
	WINDOW_CLOSE_BUTTON_RED_X_HOVERED(SpriteID.WINDOW_CLOSE_BUTTON_RED_X_HOVERED, AROUND_2010),
	WINDOW_CLOSE_BUTTON_BROWN_X(SpriteID.WINDOW_CLOSE_BUTTON_BROWN_X, AROUND_2010),
	WINDOW_CLOSE_BUTTON_BROWN_X_HOVERED(SpriteID.WINDOW_CLOSE_BUTTON_BROWN_X_HOVERED, AROUND_2010),
	MINIMAP_ORB_FRAME(SpriteID.MINIMAP_ORB_FRAME, AROUND_2010),
	MINIMAP_ORB_FRAME_HOVERED(SpriteID.MINIMAP_ORB_FRAME_HOVERED, AROUND_2010),
	MINIMAP_ORB_XP(SpriteID.MINIMAP_ORB_XP, AROUND_2010),
	MINIMAP_ORB_XP_ACTIVATED(SpriteID.MINIMAP_ORB_XP_ACTIVATED, AROUND_2010),
	MINIMAP_ORB_XP_HOVERED(SpriteID.MINIMAP_ORB_XP_HOVERED, AROUND_2010),
	MINIMAP_ORB_XP_ACTIVATED_HOVERED(SpriteID.MINIMAP_ORB_XP_ACTIVATED_HOVERED, AROUND_2010),
	MINIMAP_ORB_WORLD_MAP_FRAME(SpriteID.MINIMAP_ORB_WORLD_MAP_FRAME, AROUND_2010),
	MINIMAP_ORB_WORLD_MAP_PLANET(SpriteID.MINIMAP_ORB_WORLD_MAP_PLANET, AROUND_2010),

	//CHATBOX(SpriteID.CHATBOX, AROUND_2005, AROUND_2006),
	CHATBOX_BUTTONS_BACKGROUND_STONES(SpriteID.CHATBOX_BUTTONS_BACKGROUND_STONES, AROUND_2005, AROUND_2006),
	CHATBOX_BUTTON(SpriteID.CHATBOX_BUTTON, AROUND_2005, AROUND_2006),
	CHATBOX_BUTTON_HOVERED(SpriteID.CHATBOX_BUTTON_HOVERED, AROUND_2005, AROUND_2006),
	CHATBOX_BUTTON_NEW_MESSAGES( SpriteID.CHATBOX_BUTTON_NEW_MESSAGES, AROUND_2005, AROUND_2006),
	CHATBOX_BUTTON_SELECTED(SpriteID.CHATBOX_BUTTON_SELECTED, AROUND_2005, AROUND_2006),
	CHATBOX_BUTTON_SELECTED_HOVERED(SpriteID.CHATBOX_BUTTON_SELECTED_HOVERED, AROUND_2005, AROUND_2006),
	CHATBOX_REPORT_BUTTON(SpriteID.CHATBOX_REPORT_BUTTON, AROUND_2005, AROUND_2006),
	CHATBOX_REPORT_BUTTON_HOVERED(SpriteID.CHATBOX_REPORT_BUTTON_HOVERED, AROUND_2005, AROUND_2006),

	SCROLLBAR_ARROW_UP(SpriteID.SCROLLBAR_ARROW_UP, AROUND_2005),
	SCROLLBAR_ARROW_DOWN(SpriteID.SCROLLBAR_ARROW_DOWN, AROUND_2005),
	SCROLLBAR_THUMB_TOP(SpriteID.SCROLLBAR_THUMB_TOP, AROUND_2005),
	SCROLLBAR_THUMB_MIDDLE(SpriteID.SCROLLBAR_THUMB_MIDDLE, AROUND_2005),
	SCROLLBAR_THUMB_BOTTOM(SpriteID.SCROLLBAR_THUMB_BOTTOM, AROUND_2005),
	SCROLLBAR_THUMB_MIDDLE_DARK(SpriteID.SCROLLBAR_THUMB_MIDDLE_DARK, AROUND_2005),

	TAB_STONE_TOP_LEFT_SELECTED(SpriteID.TAB_STONE_TOP_LEFT_SELECTED, AROUND_2010),
	TAB_STONE_TOP_RIGHT_SELECTED(SpriteID.TAB_STONE_TOP_RIGHT_SELECTED, AROUND_2010),
	TAB_STONE_BOTTOM_LEFT_SELECTED(SpriteID.TAB_STONE_BOTTOM_LEFT_SELECTED, AROUND_2010),
	TAB_STONE_BOTTOM_RIGHT_SELECTED(SpriteID.TAB_STONE_BOTTOM_RIGHT_SELECTED, AROUND_2010),
	TAB_STONE_MIDDLE_SELECTED(SpriteID.TAB_STONE_MIDDLE_SELECTED, AROUND_2010),

	FIXED_MODE_SIDE_PANEL_BACKGROUND(SpriteID.FIXED_MODE_SIDE_PANEL_BACKGROUND, AROUND_2005, AROUND_2006),
	FIXED_MODE_TABS_ROW_BOTTOM(SpriteID.FIXED_MODE_TABS_ROW_BOTTOM, AROUND_2005, AROUND_2006, AROUND_2010),

	OLD_SCHOOl_MODE_SIDE_PANEL_EDGE_LEFT_UPPER(SpriteID.OLD_SCHOOl_MODE_SIDE_PANEL_EDGE_LEFT_UPPER, AROUND_2005, AROUND_2006, AROUND_2010),
	OLD_SCHOOl_MODE_SIDE_PANEL_EDGE_LEFT_LOWER(SpriteID.OLD_SCHOOl_MODE_SIDE_PANEL_EDGE_LEFT_LOWER, AROUND_2005, AROUND_2006, AROUND_2010),
	OLD_SCHOOl_MODE_SIDE_PANEL_EDGE_RIGHT(SpriteID.OLD_SCHOOl_MODE_SIDE_PANEL_EDGE_RIGHT, AROUND_2005, AROUND_2006, AROUND_2010),

	FIXED_MODE_TABS_TOP_ROW(SpriteID.FIXED_MODE_TABS_TOP_ROW, AROUND_2005, AROUND_2006, AROUND_2010),
	FIXED_MODE_MINIMAP_LEFT_EDGE(SpriteID.FIXED_MODE_MINIMAP_LEFT_EDGE, AROUND_2005, AROUND_2006, AROUND_2010),
	FIXED_MODE_MINIMAP_RIGHT_EDGE(SpriteID.FIXED_MODE_MINIMAP_RIGHT_EDGE, AROUND_2005, AROUND_2006, AROUND_2010),
	FIXED_MODE_WINDOW_FRAME_EDGE_TOP(SpriteID.FIXED_MODE_WINDOW_FRAME_EDGE_TOP, AROUND_2005, AROUND_2006, AROUND_2010),
	FIXED_MODE_MINIMAP_AND_COMPASS_FRAME(SpriteID.FIXED_MODE_MINIMAP_AND_COMPASS_FRAME, AROUND_2005, AROUND_2006, AROUND_2010),
	FIXED_MODE_MINIMAP_FRAME_BOTTOM(SpriteID.FIXED_MODE_MINIMAP_FRAME_BOTTOM, AROUND_2005, AROUND_2006),
	FIXED_MODE_TOP_RIGHT_CORNER(SpriteID.FIXED_MODE_TOP_RIGHT_CORNER, AROUND_2005, AROUND_2006),

	RESIZEABLE_MODE_TABS_TOP_ROW(SpriteID.RESIZEABLE_MODE_TABS_TOP_ROW, AROUND_2010),
	RESIZEABLE_MODE_TABS_BOTTOM_ROW(SpriteID.RESIZEABLE_MODE_TABS_BOTTOM_ROW, AROUND_2010),
	RESIZEABLE_MODE_SIDE_PANEL_EDGE_LEFT(SpriteID.RESIZEABLE_MODE_SIDE_PANEL_EDGE_LEFT, AROUND_2010),
	RESIZEABLE_MODE_SIDE_PANEL_EDGE_RIGHT(SpriteID.RESIZEABLE_MODE_SIDE_PANEL_EDGE_RIGHT, AROUND_2010),
	RESIZEABLE_MODE_MINIMAP_AND_COMPASS_FRAME(SpriteID.RESIZEABLE_MODE_MINIMAP_AND_COMPASS_FRAME, AROUND_2010),
	RESIZEABLE_MODE_TAB_STONE_MIDDLE(SpriteID.RESIZEABLE_MODE_TAB_STONE_MIDDLE, AROUND_2010),
	RESIZEABLE_MODE_TAB_STONE_MIDDLE_SELECTED(SpriteID.RESIZEABLE_MODE_TAB_STONE_MIDDLE_SELECTED, AROUND_2010);

	private int spriteID;
	private Skin[] skin;

	SpriteOverride(int spriteID, Skin... skin)
	{
		this.spriteID = spriteID;
		this.skin = skin;
	}
}