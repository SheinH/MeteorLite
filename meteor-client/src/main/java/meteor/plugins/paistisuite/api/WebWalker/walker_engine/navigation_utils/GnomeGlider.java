package meteor.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.queries.NPCQuery;
import meteor.plugins.paistisuite.api.PPlayer;
import meteor.plugins.paistisuite.api.PUtils;
import meteor.plugins.paistisuite.api.PWidgets;
import meteor.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.InteractionHelper;
import meteor.plugins.paistisuite.api.types.Filters;
import meteor.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import meteor.plugins.paistisuite.api.WebWalker.wrappers.RSTile;

import java.util.Arrays;
import java.util.List;


@Slf4j
public class GnomeGlider {

    private static final int GNOME_GLIDER_MASTER_INTERFACE = 138;
    public enum Location {
        TA_QUIR_PRIW ("Ta Quir Priw", 2465, 3501, 3),
        GANDIUS ("Gandius", 2970, 2972, 0),
        LEMANTO_ANDRA ("Lemanto Andra", 3321, 3430, 0),
        KAR_HEWO ("Kar-Hewo", 3284, 3211, 0),
        SINDARPOS ("Sindarpos", 2850, 3498, 0)
        ;

        private int x, y, z;
        private String name;
        Location(String name, int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public RSTile getRSTile(){
            return new RSTile(x, y, z);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }

    public static boolean to(Location location) {
        boolean interfaceValid = PWidgets.isValid(GNOME_GLIDER_MASTER_INTERFACE, 0);


        // Interface is open or lets try to open it
        if (!interfaceValid){
            log.info("Trying to open interface");

            NPC gliderNpc = new NPCQuery()
                    .filter(Filters.NPCs.actionsContains("Glider"))
                    .result(PUtils.getClient())
                    .nearestTo(PPlayer.get());

            if (gliderNpc != null){
                log.info("Found npc");
            }

            if (!InteractionHelper.click(gliderNpc, "Glider",
                            () -> PWidgets.isValid(GNOME_GLIDER_MASTER_INTERFACE, 0) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                return false;
            }
        }


        RSInterface option = InterfaceHelper.getAllChildren(GNOME_GLIDER_MASTER_INTERFACE).stream().filter(rsInterface -> {
            List<String> actions = rsInterface.getActions();
            return actions != null && actions.stream().anyMatch(s -> s.contains(location.getName()));
        }).findAny().orElse(null);

        if (option == null){
            return false;
        }

        if (!option.interact()){
            return false;
        }

        if (WaitFor.condition(PUtils.random(5400, 6500), () -> location.getRSTile().toWorldPoint().distanceToHypotenuse(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(2000, 3500);
            return true;
        }
        return false;
    }

}