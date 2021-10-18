package meteor.plugins.paistisuite.api.WebWalker.shared.helpers;


import meteor.plugins.paistisuite.api.types.Filters;
import meteor.plugins.paistisuite.api.PBanking;
import meteor.plugins.paistisuite.api.PInteraction;
import meteor.plugins.paistisuite.api.PInventory;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import meteor.plugins.paistisuite.api.types.PItem;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


public class RSItemHelper {

    public static boolean click(String itemNameRegex, String itemAction){
        return click((PItem item) -> {
            return getItemName(item).matches(itemNameRegex) && getItemActions(item).stream().anyMatch(s -> s.equals(itemAction));
        }, itemAction);
    }

    public static boolean clickMatch(PItem item, String regex){
        var actions = item.getCurrentActions();
        String action = actions.stream().filter(a -> a != null && a.matches(regex)).findFirst().orElse(null);
        if (action == null) return false;
        return click(item, action);
    }

    public static boolean click(int itemID){
        return click(itemID, null);
    }

    public static boolean click(int itemID, String action){
        return click(Filters.Items.idEquals(itemID), action);
    }

    public static boolean click(Predicate<PItem> filter, String action){
        if (PBanking.isBankOpen()){
            PBanking.closeBank();
            return WaitFor.condition(2000, () -> !PBanking.isBankOpen() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }

        if (action == null){
            action = "";
        }
        List<PItem> list = PInventory.findAllItems(filter);
        list.addAll(PInventory.findAllEquipmentItems(filter));

        if (list.size() == 0) return false;

        return PInteraction.item(list.get(0), action);
    }

    public static boolean click(PItem item, String action){
        if (PBanking.isBankOpen()){
            PBanking.closeBank();
            return WaitFor.condition(2000, () -> !PBanking.isBankOpen() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }
        return action != null ? PInteraction.item(item, action) : PInteraction.clickItem(item);
    }

    public static boolean isNoted(PItem item) {
        return item.getDefinition() != null && item.getDefinition().getNote() != -1;
    }

    public static List<String> getItemActions(PItem item){
        return item.getCurrentActions();
    }

    public static String getItemName(PItem item){
        return item.getDefinition().getName();
    }
}