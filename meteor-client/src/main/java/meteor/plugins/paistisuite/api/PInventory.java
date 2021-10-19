package meteor.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import meteor.plugins.paistisuite.PaistiSuite;
import meteor.plugins.paistisuite.api.types.PItem;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class PInventory {
    public static ItemComposition getItemDef(WidgetItem item) {
        return PUtils.clientOnly(() -> PaistiSuite.getInstance().itemManager.getItemComposition(item.getId()), "getItemDef");
    }

    public static ItemComposition getItemDef(int id) {
        return PUtils.clientOnly(() -> PaistiSuite.getInstance().itemManager.getItemComposition(id), "getItemDef");
    }

    public static ItemComposition getItemDef(Item item) {
        return PUtils.clientOnly(() -> PaistiSuite.getInstance().itemManager.getItemComposition(item.getId()), "getItemDef");
    }

    private static Future<ItemComposition> getFutureItemDef(WidgetItem item) {
        if (item == null) return null;

        return PaistiSuite.getInstance().clientExecutor.schedule(() -> PaistiSuite.getInstance().itemManager.getItemComposition(item.getId()), "getItemDef");
    }

    public static boolean isFull() {
        return getEmptySlots() <= 0;
    }

    public static boolean isEmpty() {
        return getEmptySlots() >= 28;
    }

    public static Integer getEmptySlots() {
        return PUtils.clientOnly(() -> {
            Widget inventoryWidget = PUtils.getClient().getWidget(WidgetInfo.INVENTORY);
            if (inventoryWidget != null) {
                return 28 - inventoryWidget.getWidgetItems().size();
            } else {
                return -1;
            }
        }, "getEmptySlots");
    }

    public static List<PItem> getAllItems() {
        return PUtils.clientOnly(() -> {
            Widget inventoryWidget = PUtils.getClient().getWidget(WidgetInfo.INVENTORY);
            if (inventoryWidget == null) return new ArrayList<PItem>();
            Collection<WidgetItem> widgetItems = inventoryWidget.getWidgetItems();
            List<PItem> pItems = widgetItems
                    .stream()
                    .map(PItem::new)
                    .collect(Collectors.toList());
            return pItems;
        }, "getAllPItems");
    }

    public static int getCount(String name) {
        int count = 0;
        List<PItem> items = getAllItems();
        for (PItem i : items) {
            if (i.getDefinition().getName().equalsIgnoreCase(name)) {
                count += i.getQuantity();
            }
        }
        return count;
    }

    public static int getCount(int id) {
        int count = 0;
        List<PItem> items = getAllItems();
        for (PItem i : items) {
            if (i.getId() == id) {
                count += i.getQuantity();
            }
        }
        return count;
    }

    public static List<PItem> findAllItems(Predicate<PItem> filter) {
        return getAllItems()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static PItem findItem(Predicate<PItem> filter) {
        return getAllItems()
                .stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public static List<PItem> getEquipmentItems() {
        return PUtils.clientOnly(() -> {
            List<PItem> equippedPItems = new ArrayList<PItem>();
            ItemContainer container = PUtils.getClient().getItemContainer(InventoryID.EQUIPMENT);
            if (container == null) return equippedPItems;
            Item[] eqitems = PUtils.getClient().getItemContainer(InventoryID.EQUIPMENT).getItems();
            int slot = 0;
            for (Item i : eqitems) {
                if (i.getId() != -1) equippedPItems.add(PItem.fromEquipmentItem(i, slot));
                slot++;
            }
            return equippedPItems;
        }, "getEquippedPItems");
    }

    public static List<PItem> findAllEquipmentItems(Predicate<PItem> filter) {
        List<PItem> eq = getEquipmentItems();
        if (eq == null) return new ArrayList<PItem>();
        return eq
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static PItem findEquipmentItem(Predicate<PItem> filter) {
        List<PItem> eq = getEquipmentItems();
        if (eq == null) return null;
        return eq
                .stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public static List<Item> legacyGetEquipmentItems() {
        List<Item> equipped = new ArrayList<>();
        Item[] items = null;
        if (PUtils.getClient().isClientThread()) {
            items = PUtils.getClient().getItemContainer(InventoryID.EQUIPMENT).getItems();
        } else {
            try {
                items = PaistiSuite.getInstance().clientExecutor.scheduleAndWait(() -> {
                    return PUtils.getClient().getItemContainer(InventoryID.EQUIPMENT).getItems();
                }, "getEquippedItems");
            } catch (Exception e) {
            }
        }

        if (items == null) return equipped;
        for (Item item : items) {
            if (item.getId() == -1 || item.getId() == 0) {
                continue;
            }
            equipped.add(item);
        }
        return equipped;
    }

    public static int getEquipmentCount(int equipmentId) {
        int count = 0;
        List<Item> equipment = legacyGetEquipmentItems();

        for (Item i : equipment) {
            if (i.getId() == equipmentId) {
                count += i.getQuantity();
            }
        }

        return count;
    }
}
