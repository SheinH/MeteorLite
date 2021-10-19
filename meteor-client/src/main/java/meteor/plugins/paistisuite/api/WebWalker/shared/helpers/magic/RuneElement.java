package meteor.plugins.paistisuite.api.WebWalker.shared.helpers.magic;


import kotlin.Pair;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.WidgetItem;
import meteor.plugins.paistisuite.api.PInventory;
import meteor.plugins.paistisuite.api.PUtils;
import meteor.plugins.paistisuite.api.types.PItem;

import java.util.Arrays;
import java.util.List;

public enum RuneElement {

    AIR("Air", "Smoke", "Mist", "Dust"),
    EARTH("Earth", "Lava", "Mud", "Dust"),
    FIRE("Fire", "Lava", "Smoke", "Steam"),
    WATER("Water", "Mud", "Steam", "Mist"),
    LAW("Law"),
    NATURE("Nature"),
    SOUL("Soul");

    private String[] alternativeNames;

    RuneElement(String... alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public String[] getAlternativeNames() {
        return alternativeNames;
    }

    public int getCount() {
        if (haveStaff()) {
            return Integer.MAX_VALUE;
        }
        List<PItem> items = PInventory.findAllItems((PItem pair) -> {
            if(pair.getDefinition().isMembers() && !PUtils.isMembersWorld()){
                return false;
            }

            String name = getItemName(pair).toLowerCase();

            if (!name.contains("rune")) {
                return false;
            }

            for (String alternativeName : alternativeNames) {
                if (name.startsWith(alternativeName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });

        return items.stream().mapToInt(i -> i.getFirst().getQuantity()).sum() + RunePouch.getQuantity(this);
    }

    private boolean haveStaff() {
        return PInventory.findEquipmentItem((PItem item) -> {
            if(item.getDefinition().isMembers() && !PUtils.isMembersWorld()){
                return false;
            }

            String name = getItemName(item).toLowerCase();
            if (!name.contains("staff")) {
                return false;
            }
            for (String alternativeName : alternativeNames) {
                if (name.contains(alternativeName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }) != null;
    }

    /**
     * @param item
     * @return item name. Never null. "null" if no name.
     */
    private static String getItemName(PItem item) {
        ItemComposition definition = item.getDefinition();
        String name;
        return definition == null || (name = definition.getName()) == null ? "null" : name;
    }


}