/*
 * Copyright (c) 2019-2020, ganom <https://github.com/Ganom>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package meteor.plugins.leftclickcast;

import com.google.inject.Provides;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;

import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.game.ChatIconManager;
import meteor.input.KeyManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.util.HotkeyListener;
import meteor.util.PvPUtil;
import meteor.util.Text;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;

import static net.runelite.api.MenuAction.SPELL_CAST_ON_NPC;
import static net.runelite.api.MenuAction.SPELL_CAST_ON_PLAYER;

import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

@PluginDescriptor(
        name = "Left Click Cast",
        description = "Casting made even easier.",
        tags = "ganom",
        enabledByDefault = false
)

@SuppressWarnings("unused")
public class LeftClickCastPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private LeftClickConfig config;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ChatIconManager friendsManager;

    private final Set<Integer> whitelist = new HashSet<>();

    private boolean isMage;
    private Spells currentSpell = Spells.ICE_BARRAGE;

    boolean registered = false;

    private final HotkeyListener spellOneSwap = new HotkeyListener(() -> config.spellOneSwap()) {
        @Override
        public void hotkeyPressed() {
            currentSpell = config.spellOne();
            logger.info(config.spellOne());
        }
    };

    private final HotkeyListener spellTwoSwap = new HotkeyListener(() -> config.spellTwoSwap()) {
        @Override
        public void hotkeyPressed() {
            currentSpell = config.spellTwo();
        }
    };

    private final HotkeyListener spellThreeSwap = new HotkeyListener(() -> config.spellThreeSwap()) {
        @Override
        public void hotkeyPressed() {
            currentSpell = config.spellThree();
        }
    };

    private final HotkeyListener spellFourSwap = new HotkeyListener(() -> config.spellFourSwap()) {
        @Override
        public void hotkeyPressed() {
            currentSpell = config.spellFour();
        }
    };

    private final HotkeyListener spellFiveSwap = new HotkeyListener(() -> config.spellFiveSwap()) {
        @Override
        public void hotkeyPressed() {
            currentSpell = config.spellFive();
        }
    };

    private final HotkeyListener spellSixSwap = new HotkeyListener(() -> config.spellSixSwap()) {
        @Override
        public void hotkeyPressed() {
            currentSpell = config.spellSix();
        }
    };

    @Provides
    public LeftClickConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(LeftClickConfig.class);
    }

    @Override
    public void startup() {
        if (client.getGameState() == GameState.LOGGED_IN) {
            registerListeners();
        }
        updateConfig();
    }

    @Override
    public void shutdown() {
        unregisterListeners();
    }

    private void registerListeners() {
        keyManager.registerKeyListener(spellOneSwap, this.getClass());
        keyManager.registerKeyListener(spellTwoSwap, this.getClass());
        keyManager.registerKeyListener(spellThreeSwap, this.getClass());
        keyManager.registerKeyListener(spellFourSwap, this.getClass());
        keyManager.registerKeyListener(spellFiveSwap, this.getClass());
        keyManager.registerKeyListener(spellSixSwap, this.getClass());
    }

    private void unregisterListeners() {
        keyManager.unregisterKeyListener(spellOneSwap);
        keyManager.unregisterKeyListener(spellTwoSwap);
        keyManager.unregisterKeyListener(spellThreeSwap);
        keyManager.unregisterKeyListener(spellFourSwap);
        keyManager.unregisterKeyListener(spellFiveSwap);
        keyManager.unregisterKeyListener(spellSixSwap);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        updateConfig();
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (event.isForceLeftClick()) {
            return;
        }

        if (event.getOpcode() == MenuAction.PLAYER_SECOND_OPTION.getId() && isMage) {
            final String name = Text.standardize(event.getTarget());

            if (!config.disableFriendlyRegionChecks() && (client.getVarbitValue(5314) == 0 && (client.isFriended(name, false)))) {
                return;
            }

            if (!config.disableFriendlyRegionChecks()) {
                try {
                    boolean b = (!PvPUtil.isAttackable(client, client.getCachedPlayers()[event.getIdentifier()]));
                } catch (IndexOutOfBoundsException ex) {
                    return;
                }
            }

            setSelectSpell(currentSpell.getSpell());
            MenuEntry e = event.clone();
            e.setOption("(P) Left Click " + client.getSelectedSpellName() + " -> ");
            e.setForceLeftClick(true);
            insert(e);
        } else if (event.getOpcode() == MenuAction.NPC_SECOND_OPTION.getId() && isMage) {
            try {
                NPC npc = validateNpc(event.getIdentifier());

                if (npc == null) {
                    return;
                }

                if (config.disableStaffChecks() && !whitelist.contains(npc.getId())) {
                    return;
                }

                setSelectSpell(currentSpell.getSpell());
                MenuEntry e = event.clone();
                e.setOption("(N) Left Click " + client.getSelectedSpellName() + " -> ");
                e.setForceLeftClick(true);
                insert(e);
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
            if (event.getMenuOption().contains("(P)")) {
                event.setMenuAction(SPELL_CAST_ON_PLAYER);
                event.setActionParam(0);
                event.setWidgetId(0);
            } else if (event.getMenuOption().contains("(N)")) {
                event.setMenuAction(SPELL_CAST_ON_NPC);
                event.setActionParam(0);
                event.setWidgetId(0);
            }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        final ItemContainer ic = event.getItemContainer();

        if (client.getItemContainer(InventoryID.EQUIPMENT) != ic) {
            return;
        }

        isMage = false;

        for (Item item : ic.getItems()) {
            final String name = client.getItemComposition(item.getId()).getName().toLowerCase();
            if (name.contains("staff") || name.contains("wand") || name.contains("sceptre") || name.contains("trident")) {
                isMage = true;
                break;
            }
        }

        if (config.disableStaffChecks()) {
            isMage = true;
        }
    }

    public void updateConfig() {
        whitelist.clear();
            if (config.disableStaffChecks()) {
                List<String> string = Text.fromCSV(config.whitelist());
                for (String s : string) {
                    try {
                        whitelist.add(Integer.parseInt(s));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
    }

    private void setSelectSpell(WidgetInfo info) {
        final Widget widget = client.getWidget(info);
        client.setSelectedSpellName("<col=00ff00>" + widget.getName() + "</col>");
        client.setSelectedSpellWidget(widget.getId());
        client.setSelectedSpellChildIndex(-1);
    }

    private void insert(MenuEntry e) {
        client.insertMenuItem(
                e.getOption(),
                e.getTarget(),
                e.getOpcode(),
                e.getIdentifier(),
                e.getParam0(),
                e.getParam1(),
                true
        );
    }

    /**
     * This method is not ideal, as its going to create a ton of junk
     * but its the most reliable method i've found so far for validating
     * NPCs on menu events. Another solution would be to use string
     * comparison, however most users are used to the id concept
     * so this was the path of least resistance. I'm open to
     * suggestions however if anyone wants to offer them.
     * -Ganom
     *
     * @param index Menu event index.
     * @return {@link NPC} object for comparison.
     */
    @Nullable
    private NPC validateNpc(int index) {
        NPC npc = null;

        for (NPC clientNpc : client.getNpcs()) {
            if (index == clientNpc.getIndex()) {
                npc = clientNpc;
                break;
            }
        }

        return npc;
    }
}
