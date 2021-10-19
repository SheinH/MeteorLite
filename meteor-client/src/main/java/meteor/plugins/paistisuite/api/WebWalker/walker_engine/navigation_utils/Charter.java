package meteor.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.queries.NPCQuery;
import meteor.plugins.paistisuite.api.types.Filters;
import meteor.plugins.paistisuite.api.PPlayer;
import meteor.plugins.paistisuite.api.PUtils;
import meteor.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.InteractionHelper;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.NPCInteraction;
import meteor.plugins.paistisuite.api.WebWalker.wrappers.RSArea;
import meteor.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import meteor.plugins.paistisuite.api.WebWalker.wrappers.RSTile;

import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
public class Charter{
    private static final int CHARTER_INTERFACE_MASTER = 72;
    private static Charter instance;

    private static Charter getInstance(){
        return instance != null ? instance : (instance = new Charter());
    }

    public static boolean to(LocationProperty locationProperty){
        if (locationProperty == null){
            return false;
        }
        if (!openCharterMenu()){
            log.info("Failed to open charter menu.");
            return false;
        }
        HashMap<LocationProperty, Location> charterLocations = getCharterLocations();
        Location location = charterLocations.get(locationProperty);

        if (location == null){
            log.info("Location: " + locationProperty + " is not available. " + charterLocations.keySet());
            return false;
        }
        if (!location.click()){
            log.info("Failed to click charter location.");
            return false;
        }
        if (!NPCInteraction.waitForConversationWindow()){
            log.info("Confirmation dialogue did not appear.");
        }
        NPCInteraction.handleConversation("Ok", "Okay");
        return WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static boolean openCharterMenu() {
        boolean widgetValid = PUtils.getClient().getWidget(CHARTER_INTERFACE_MASTER, 0) != null;
        if (widgetValid) return true;
        NPC charterNpc = new NPCQuery()
                .filter(Filters.NPCs.actionsContains("Charter"))
                .result(PUtils.getClient())
                .nearestTo(PPlayer.get());

        return InteractionHelper.click(charterNpc, "Charter",
                () -> PUtils.getClient().getWidget(CHARTER_INTERFACE_MASTER, 0) != null ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
    }

    private static HashMap<LocationProperty, Location> getCharterLocations(){
        return PUtils.clientOnly(() -> {
            HashMap<LocationProperty, Location> locations = new HashMap<>();
            InterfaceHelper.getAllChildren(CHARTER_INTERFACE_MASTER).stream().filter(
                    rsInterface -> rsInterface != null
                            && rsInterface.getWidget() != null
                            && rsInterface.getWidget().getFontId() == 495
                            && rsInterface.getWidget().getTextShadowed()
                            && !rsInterface.getWidget().isHidden()
            )
                    .collect(Collectors.toList())
                    .forEach(rsInterface -> {
                                locations.put(
                                        LocationProperty.stringToLocation(rsInterface.getText()), new Location(rsInterface));
                            }
                    );


            return locations;
        }, "getCharterLocations");
    }

    public enum LocationProperty {
        PORT_TYRAS ("Port Tyras", null),
        PORT_PHASMATYS ("Port Phasmatys", new RSTile(3702, 3503, 0)),
        CATHERBY ("Catherby", new RSTile(2796, 3414, 0)),
        SHIPYARD ("Shipyard", null),
        KARAMJA ("Musa Point", new RSTile(2956, 3146, 0)),
        BRIMHAVEN ("Brimhaven", new RSTile(2760, 3237, 0)),
        PORT_KHAZARD ("Port Khazard", new RSTile(2674, 3149, 0)),
        PORT_SARIM ("Port Sarim", new RSTile(3041, 3193, 0)),
        MOS_LE_HARMLESS ("Mos le'Harmless", null),
        CRANDOR ("Crandor", null);

        private String name;
        private RSArea area;

        LocationProperty(String name, RSTile center){
            this.name = name;
            if (center != null) {
                this.area = new RSArea(center, 15);
            }
        }

        public boolean valid(RSTile tile) {
            return area != null && tile != null && area.contains(tile);
        }

        public static LocationProperty stringToLocation(String name){
            for (LocationProperty locationProperty : values()){
                if (name.equals(locationProperty.name)){
                    return locationProperty;
                }
            }
            return null;
        }

        public static LocationProperty getLocation(RSTile tile){
            for (LocationProperty locationProperty : values()){
                if (locationProperty.valid(tile)){
                    return locationProperty;
                }
            }
            return null;
        }

        @Override
        public String toString(){
            return name;
        }
    }


    public static class Location {

        private String name;
        private RSInterface rsInterface;

        private Location(RSInterface rsInterface){
            this.name = rsInterface.getText();
            this.rsInterface = rsInterface;
        }

        public String getName() {
            return name;
        }

        public RSInterface getRsInterface() {
            return rsInterface;
        }

        public boolean click(){
            return rsInterface.interact();
        }


        @Override
        public String toString(){
            return name;
        }
    }

}