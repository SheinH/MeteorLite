package meteor.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import meteor.plugins.paistisuite.api.PUtils;
import meteor.plugins.paistisuite.api.PWidgets;
import meteor.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper;
import meteor.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import meteor.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DoomsToggle {

    private static final int STRONGHOLD_TOGGLE = 579, WILDERNESS_TOGGLE = 475, SHANTY_TOGGLE = 565, WATERBIRTH = 574, MORT_MYRE = 580, LUMBRIDGE_SWAMP = 572,
            OBSERVATORY_TOGGLE = 560;

    private static final int[] GENERAL_CASES = {STRONGHOLD_TOGGLE, WILDERNESS_TOGGLE, SHANTY_TOGGLE, WATERBIRTH, MORT_MYRE, LUMBRIDGE_SWAMP, OBSERVATORY_TOGGLE};

    private static DoomsToggle instance;

    private static DoomsToggle getInstance(){
        return instance != null ? instance : (instance = new DoomsToggle());
    }

    private static Client client = PUtils.getClient();


    public static void handleToggle(){
        for (int generalCase : GENERAL_CASES){
            handle(generalCase, "Yes", "Enter Wilderness","Enter the swamp.","I'll be fine without a tinderbox.",
                    "Proceed regardless");
        }
    }

    public static boolean handle(int parentInterface, String... option){
        if (!PWidgets.isSubstantiated(parentInterface)) return false;
        log.info("Handling Interface: " + parentInterface);
        Optional<RSInterface> optional = InterfaceHelper.getAllChildren(parentInterface).stream().filter(rsInterface -> {
            List<String> actions = rsInterface.getActions();
            return !actions.isEmpty() && Arrays.stream(option).anyMatch(s -> actions.stream().anyMatch(s1 -> s1.equals(s)));
        }).findAny();
        optional.ifPresent(rsInterface -> rsInterface.interact(option));
        WaitFor.milliseconds(500, 1500);
        return true;
    }
}