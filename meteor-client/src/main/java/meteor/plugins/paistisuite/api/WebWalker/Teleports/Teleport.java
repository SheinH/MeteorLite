package meteor.plugins.paistisuite.api.WebWalker.Teleports;


import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetInfo;
import meteor.plugins.paistisuite.api.*;
import meteor.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportConstants;
import meteor.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportLimit;
import meteor.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils.TeleportScrolls;
import meteor.plugins.paistisuite.api.WebWalker.api_lib.models.Requirement;
import meteor.plugins.paistisuite.api.WebWalker.shared.helpers.RSItemHelper;
import meteor.plugins.paistisuite.api.WebWalker.shared.helpers.magic.Spell;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import meteor.plugins.paistisuite.api.WebWalker.wrappers.*;
import meteor.plugins.paistisuite.api.types.Filters;
import meteor.plugins.paistisuite.api.types.PItem;
import meteor.plugins.paistisuite.api.types.Spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public enum Teleport {
    VARROCK_TELEPORT(
            35, new RSTile(3212, 3424, 0),
            Spell.VARROCK_TELEPORT::canUse,
            () -> castSpell("Varrock Teleport","Cast")
    ),

    VARROCK_TELEPORT_TAB(
            35, new RSTile(3212, 3424, 0),
            () -> inMembersWorld() && PInventory.getCount("Varrock teleport") > 0,
            () -> RSItemHelper.click("Varrock t.*", "Break")
    ),

    VARROCK_TELEPORT_GRAND_EXCHANGE(
            35, new RSTile(3161, 3478, 0),
            () -> Spell.VARROCK_TELEPORT.canUse() && TeleportConstants.isVarrockTeleportAtGE(),
            () -> castSpell("Varrock Teleport","Grand Exchange")
    ),

    LUMBRIDGE_TELEPORT(
            35, new RSTile(3225, 3219, 0),
            Spell.LUMBRIDGE_TELEPORT::canUse,
            () -> castSpell("Lumbridge Teleport","Cast")
    ),

    LUMBRIDGE_TELEPORT_TAB(
            35, new RSTile(3225, 3219, 0),
            () -> inMembersWorld() && PInventory.getCount("Lumbridge teleport") > 0,
            () -> RSItemHelper.click("Lumbridge t.*", "Break")
    ),

    FALADOR_TELEPORT(
            35, new RSTile(2966, 3379, 0),
            Spell.FALADOR_TELEPORT::canUse,
            () -> castSpell("Falador Teleport","Cast")
    ),

    FALADOR_TELEPORT_TAB(
            35, new RSTile(2966, 3379, 0),
            () -> inMembersWorld() && PInventory.getCount("Falador teleport") > 0,
            () -> RSItemHelper.click("Falador t.*", "Break")
    ),

    CAMELOT_TELEPORT(
            35, new RSTile(2757, 3479, 0),
            () -> inMembersWorld() && Spell.CAMELOT_TELEPORT.canUse(),
            () -> castSpell("Camelot Teleport","Cast")

    ),

    CAMELOT_TELEPORT_TAB(
            35, new RSTile(2757, 3479, 0),
            () -> inMembersWorld() && PInventory.getCount("Camelot teleport") > 0,
            () -> RSItemHelper.click("Camelot t.*", "Break")
    ),

    SEERS_TELEPORT(
            35, new RSTile(2757, 3479, 0),
            () -> inMembersWorld() && Spell.CAMELOT_TELEPORT.canUse() && RSVarBit.get(4560).getValue() == 1,
            () -> castSpell("Camelot Teleport","Seers'")
    ),

    ARDOUGNE_TELEPORT(
            35, new RSTile(2661, 3300, 0),
            () -> inMembersWorld() && Spell.ARDOUGNE_TELEPORT.canUse(),
            () -> castSpell("Ardougne Teleport","Cast")

    ),

    ARDOUGNE_TELEPORT_TAB(
            35, new RSTile(2661, 3300, 0),
            () -> inMembersWorld() && PInventory.getCount("Ardougne teleport") > 0,
            () -> RSItemHelper.click("Ardougne t.*", "Break")
    ),

    NARDAH_TELEPORT(
            35, TeleportScrolls.NARDAH
    ),
    DIGSITE_TELEPORT(
            35, TeleportScrolls.DIGSITE
    ),
    FELDIP_HILLS_TELEPORT(
            35, TeleportScrolls.FELDIP_HILLS
    ),
    LUNAR_ISLE_TELEPORT(
            35, TeleportScrolls.LUNAR_ISLE
    ),
    MORTTON_TELEPORT(
            35, TeleportScrolls.MORTTON
    ),
    PEST_CONTROL_TELEPORT(
            35, TeleportScrolls.PEST_CONTROL
    ),
    PISCATORIS_TELEPORT(
            35, TeleportScrolls.PISCATORIS
    ),
    TAI_BWO_WANNAI_TELEPORT(
            35, TeleportScrolls.TAI_BWO_WANNAI
    ),
    ELF_CAMP_TELEPORT(
            35, TeleportScrolls.ELF_CAMP
    ),
    MOS_LE_HARMLESS_TELEPORT(
            35, TeleportScrolls.MOS_LE_HARMLESS
    ),
    LUMBERYARD_TELEPORT(
            35, TeleportScrolls.LUMBERYARD
    ),
    ZULLANDRA_TELEPORT(
            35, TeleportScrolls.ZULLANDRA
    ),
    KEY_MASTER_TELEPORT(
            35, TeleportScrolls.KEY_MASTER
    ),
    REVENANT_CAVES_TELEPORT(
            35, TeleportScrolls.REVENANT_CAVES
    ),
    WATSON_TELEPORT(
            35, TeleportScrolls.WATSON
    ),


    RING_OF_WEALTH_GRAND_EXCHANGE(
            35, new RSTile(3161, 3478, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)Grand Exchange"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    RING_OF_WEALTH_FALADOR(
            35, new RSTile(2994, 3377, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)falador.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    RING_OF_WEALTH_MISCELLANIA(
            35, new RSTile(2535, 3861, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER) && PVars.getSetting(359) >= 100,
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)misc.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    RING_OF_DUELING_DUEL_ARENA (
            35, new RSTile(3313, 3233, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*duel arena.*")
    ),

    RING_OF_DUELING_CASTLE_WARS (
            35, new RSTile(2440, 3090, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*Castle Wars.*")
    ),

    RING_OF_DUELING_FEROX_ENCLAVE (
            35, new RSTile(3150, 3635, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*Ferox Enclave.*")
    ),

    NECKLACE_OF_PASSAGE_WIZARD_TOWER (
            35, new RSTile(3113, 3179, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*wizard.+tower.*")
    ),

    NECKLACE_OF_PASSAGE_OUTPOST (
            35, new RSTile(2430, 3347, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*the.+outpost.*")
    ),

    NECKLACE_OF_PASSAGE_EYRIE (
            35, new RSTile(3406, 3156, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*eagl.+eyrie.*")
    ),

    COMBAT_BRACE_WARRIORS_GUILD (
            35, new RSTile(2882, 3550, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*warrior.+guild.*")
    ),

    COMBAT_BRACE_CHAMPIONS_GUILD (
            35, new RSTile(3190, 3366, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*champion.+guild.*")
    ),

    COMBAT_BRACE_MONASTARY (
            35, new RSTile(3053, 3486, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*monastery.*")
    ),

    COMBAT_BRACE_RANGE_GUILD (
            35, new RSTile(2656, 3442, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*rang.+guild.*")
    ),

    GAMES_NECK_BURTHORPE (
            35, new RSTile(2897, 3551, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*burthorpe.*")
    ),

    GAMES_NECK_BARBARIAN_OUTPOST (
            35, new RSTile(2520, 3570, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*barbarian.*")
    ),

    GAMES_NECK_CORPOREAL (
            35, new RSTile(2965, 4382, 2),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*corporeal.*")
    ),

    GAMES_NECK_WINTERTODT (
            35, new RSTile(1623, 3937, 0),
            () -> inMembersWorld() && hasBeenToZeah() && WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*wintertodt.*")
    ),

    GLORY_EDGEVILLE (
            35, new RSTile(3087, 3496, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*edgeville.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    GLORY_KARAMJA (
            35, new RSTile(2918, 3176, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER,"(?i).*karamja.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    GLORY_DRAYNOR (
            35, new RSTile(3105, 3251, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER,"(?i).*draynor.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    GLORY_AL_KHARID (
            35, new RSTile(3293, 3163, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*al kharid.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    SKILLS_FISHING_GUILD (
            35, new RSTile(2610, 3391, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Fishing.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    SKILLS_MINING_GUILD (
            35, new RSTile(3052, 9764, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Mining.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    SKILLS_CRAFTING_GUILD (
            35, new RSTile(2935, 3293, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Craft.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    SKILLS_COOKING_GUILD (
            35, new RSTile(3145, 3442, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Cooking.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    SKILLS_WOODCUTTING_GUILD (
            35, new RSTile(1663, 3507, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Woodcutting.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    SKILLS_FARMING_GUILD (
            35, new RSTile(1248, 3719, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Farming.*"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),
    BURNING_AMULET_CHAOS_TEMPLE (
            35, new RSTile(3236, 3635, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Chaos.*|Okay, teleport to level.*)"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    BURNING_AMULET_BANDIT_CAMP (
            35, new RSTile(3039, 3652, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Bandit.*|Okay, teleport to level.*)"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    BURNING_AMULET_LAVA_MAZE (
            35, new RSTile(3029, 3843, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Lava.*|Okay, teleport to level.*)"),
            TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
    ),

    DIGSITE_PENDANT (
            35, new RSTile(3346,3445,0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.DIGSITE_PENDANT_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.DIGSITE_PENDANT_FILTER, "Digsite")
    ),

    ECTOPHIAL (
            0, new RSTile(3660, 3524, 0),
            () -> inMembersWorld() && PInventory.findItem(Filters.Items.nameContains("Ectophial")) != null,
            () -> RSItemHelper.click(Filters.Items.nameContains("Ectophial"), "Empty")
    ),

    LLETYA (
            35, new RSTile(2330,3172,0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER, "Lletya")
    ),

    XERICS_GLADE(
            35, new RSTile(1753, 3565, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Glade")
    ),
    XERICS_INFERNO(
            35, new RSTile(1505,3809,0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Inferno")
    ),
    XERICS_LOOKOUT(
            35, new RSTile(1575, 3531, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Lookout")
    ),

    WEST_ARDOUGNE_TELEPORT_TAB(
            35, new RSTile(2500,3290,0),
            () -> inMembersWorld() && PInventory.getCount("West ardougne teleport") > 0,
            () -> RSItemHelper.click("West ardougne t.*", "Break")
    ),

    RADAS_BLESSING_KOUREND_WOODLAND(
            0, new RSTile(1558, 3458, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.RADAS_BLESSING_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RADAS_BLESSING_FILTER, "Kourend .*")
    ),
    RADAS_BLESSING_MOUNT_KARUULM(
            0, new RSTile(1310, 3796, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.RADAS_BLESSING_FILTER.and(Filters.Items.nameContains("3","4"))),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.RADAS_BLESSING_FILTER, "Mount.*")
    ),

    CRAFTING_CAPE_TELEPORT(
            0, new RSTile(2931, 3286, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.CRAFTING_CAPE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.CRAFTING_CAPE_FILTER, "Teleport")
    ),

    CABBAGE_PATCH_TELEPORT(
            0, new RSTile(3049, 3287, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.EXPLORERS_RING_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.EXPLORERS_RING_FILTER, "Teleport")
    ),

    LEGENDS_GUILD_TELEPORT(
            0, new RSTile(2729, 3348, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.QUEST_CAPE_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.QUEST_CAPE_FILTER, "Teleport")
    ),

    KANDARIN_MONASTERY_TELEPORT(
            0, new RSTile(2606, 3216, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.ARDOUGNE_CLOAK_FILTER),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.ARDOUGNE_CLOAK_FILTER, "Monastery.*")
    ),

    RIMMINGTON_TELEPORT_TAB(
            35, new RSTile(2954,3224, 0),
            () -> inMembersWorld() && PInventory.getCount("Rimmington teleport") > 0,
            () -> RSItemHelper.click("Rimmington t.*", "Break")
    ),

    TAVERLEY_TELEPORT_TAB(
            35, new RSTile(2894, 3465, 0),
            () -> inMembersWorld() && PInventory.getCount("Taverley teleport") > 0,
            () -> RSItemHelper.click("Taverley t.*", "Break")
    ),

    RELLEKKA_TELEPORT_TAB(
            35, new RSTile(2668, 3631, 0),
            () -> inMembersWorld() && PInventory.getCount("Rellekka teleport") > 0,
            () -> RSItemHelper.click("Rellekka t.*", "Break")
    ),

    BRIMHAVEN_TELEPORT_TAB(
            35, new RSTile(2758, 3178, 0),
            () -> inMembersWorld() && PInventory.getCount("Brimhaven teleport") > 0,
            () -> RSItemHelper.click("Brimhaven t.*", "Break")
    ),

    POLLNIVNEACH_TELEPORT_TAB(
            35, new RSTile(3340, 3004, 0),
            () -> inMembersWorld() && PInventory.getCount("Pollnivneach teleport") > 0,
            () -> RSItemHelper.click("Pollnivneach t.*", "Break")
    ),

    YANILLE_TELEPORT_TAB(
            35, new RSTile(2544, 3095, 0),
            () -> inMembersWorld() && PInventory.getCount("Yanille teleport") > 0,
            () -> RSItemHelper.click("Yanille t.*", "Break")
    ),

    HOSIDIUS_TELEPORT_TAB(
            35, new RSTile(1744, 3517, 0),
            () -> inMembersWorld() && PInventory.getCount("Hosidius teleport") > 0,
            () -> RSItemHelper.click("Hosidius t.*", "Break")
    ),

    CONSTRUCTION_CAPE_RIMMINGTON(
            0, new RSTile(2954,3224, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Rimmington")
    ),

    CONSTRUCTION_CAPE_TAVERLEY(
            0, new RSTile(2894, 3465, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Taverley")
    ),

    CONSTRUCTION_CAPE_RELLEKKA(
            0, new RSTile(2668, 3631, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Rellekka")
    ),

    CONSTRUCTION_CAPE_BRIMHAVEN(
            0, new RSTile(2758, 3178, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Brimhaven")
    ),

    CONSTRUCTION_CAPE_POLLNIVNEACH(
            0, new RSTile(3340, 3004, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Pollnivneach")
    ),

    CONSTRUCTION_CAPE_YANILLE(
            0, new RSTile(2544, 3095, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Yanille")
    ),

    CONSTRUCTION_CAPE_HOSIDIUS(
            0, new RSTile(1744, 3517, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
            () -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Hosidius")
    ),

    SLAYER_RING_GNOME_STRONGHOLD(
            35, new RSTile(2433, 3424, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Stronghold")
    ),

    SLAYER_RING_MORYTANIA(
            35, new RSTile(3422, 3537, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Tower")
    ),

    SLAYER_RING_RELLEKKA_CAVE(
            35, new RSTile(2801, 9999, 0),
            () -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING),
            () -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Rellekka")
    ),

    SALVE_GRAVEYARD_TAB(
            35, new RSTile(3432, 3460, 0),
            () -> inMembersWorld() && PInventory.getCount("Salve graveyard teleport") > 0,
            () -> RSItemHelper.click("Salve graveyard t.*", "Break")
    )
    ;
    private int moveCost;
    private RSTile location;
    private Requirement requirement;
    private Action action;
    private TeleportLimit teleportLimit;

    private boolean canUse = true;

    private int failedAttempts = 0;

    Teleport(int moveCost, RSTile location, Requirement requirement, Action action) {
        this.moveCost = moveCost;
        this.location = location;
        this.requirement = requirement;
        this.action = action;
        this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
    }

    Teleport(int moveCost, RSTile location, Requirement requirement, Action action, TeleportLimit limit) {
        this.moveCost = moveCost;
        this.location = location;
        this.requirement = requirement;
        this.action = action;
        this.teleportLimit = limit;
    }

    Teleport(int movecost, TeleportScrolls scroll){
        this.moveCost = movecost;
        this.location = scroll.getLocation();
        this.requirement = () -> inMembersWorld() && scroll.canUse();
        this.action = () -> scroll.teleportTo(false);
        this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
    }


    public int getMoveCost() {
        return moveCost;
    }

    public void setMoveCost(int cost){
        if(this.moveCost == 0)
            return;
        this.moveCost = cost;
    }

    public RSTile getLocation() {
        return location;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public boolean trigger() {
        boolean value = this.action.trigger();
        if(!value){
            failedAttempts++;
            if(failedAttempts > 3){
                canUse = false;
            }
        }
        failedAttempts = 0;
        return value;
    }

    public boolean isAtTeleportSpot(RSTile tile) {
        return tile.distanceTo(location) < 10;
    }

    public static void setMoveCosts(int moveCost){
        Arrays.stream(values()).forEach(t -> t.setMoveCost(moveCost));
    }

    private static List<Teleport> blacklist = new ArrayList<>();

    public static void blacklistTeleports(Teleport... teleports){
        blacklist.addAll(Arrays.asList(teleports));
    }

    public static void clearTeleportBlacklist(){
        blacklist.clear();
    }

    public static List<RSTile> getValidStartingRSTiles() {
        List<RSTile> RSTiles = new ArrayList<>();
        for (Teleport teleport : values()) {

            if (blacklist.contains(teleport) || !teleport.teleportLimit.canCast() ||
                    !teleport.canUse || !teleport.requirement.satisfies()) continue;
            RSTiles.add(teleport.location);
        }
        return RSTiles;
    }

    private interface Action {
        boolean trigger();
    }

    private static boolean inMembersWorld() {
        return PUtils.isMembersWorld();
    }

    private static Predicate<PItem> notNotedFilter() {
        return itm -> itm.getDefinition() != null && itm.getDefinition().getNote() == -1;
    }

    private static boolean itemAction(String name, String... actions) {
        PItem item = PInventory.findItem(Filters.Items.nameEquals(name));
        return PInteraction.item(item, actions);
    }

    private static boolean teleportWithScrollInterface(Predicate<PItem> itemFilter, String regex){
        ArrayList<PItem> items = new ArrayList<>();
        items.addAll(PInventory.findAllItems(itemFilter));
        items.addAll(PInventory.findAllEquipmentItems(itemFilter));

        if (items.size() == 0) {
            return false;
        }

        if(!PWidgets.isSubstantiated(TeleportConstants.SCROLL_INTERFACE_MASTER)){
            PItem teleportItem = items.get(0);
            if (!RSItemHelper.clickMatch(teleportItem, "(Rub|Teleport|" + regex + ")")
                    || WaitFor.condition(2500,
                    () -> PWidgets.isSubstantiated(TeleportConstants.SCROLL_INTERFACE_MASTER) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)
                    != WaitFor.Return.SUCCESS) {
                return false;
            }
        }

        return handleScrollInterface(regex);
    }

    private static boolean handleScrollInterface(String regex){
        RSInterface box = new RSInterface(PWidgets.get(187, 3));
        if(box == null || box.getWidget() == null) return false;

        RSInterface[] children = box.getChildren();
        if(children == null)
            return false;
        for(RSInterface child : children){
            String txt = child.getText();
            if(txt != null && txt.matches(regex)){
                Keyboard.typeString(Text.removeTags(txt).substring(0,1));
                return true;
            }
        }
        return false;
    }

    public static boolean castSpell(String spellName, String action){
        WidgetInfo spellWidgetInfo = Spells.getWidget(spellName);
        if (PWidgets.isValid(spellWidgetInfo)) {
            return PInteraction.widget(PWidgets.get(spellWidgetInfo), action);
        }
        return false;
    }

    private static boolean hasBeenToZeah(){
        return RSVarBit.get(4897).getValue() > 0;
    }
}