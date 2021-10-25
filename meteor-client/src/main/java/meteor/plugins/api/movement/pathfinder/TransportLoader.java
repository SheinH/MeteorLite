package meteor.plugins.api.movement.pathfinder;

import meteor.plugins.api.entities.NPCs;
import meteor.plugins.api.entities.Players;
import meteor.plugins.api.entities.TileObjects;
import meteor.plugins.api.game.Skills;
import meteor.plugins.api.game.Vars;
import meteor.plugins.api.game.Worlds;
import meteor.plugins.api.items.Inventory;
import meteor.plugins.api.movement.Movement;
import meteor.plugins.api.movement.Reachable;
import meteor.plugins.api.widgets.Dialog;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class TransportLoader {
    private static final int RFD_VARBIT = 1850;
    private static final int BUILD_DELAY_SECONDS = 5;
    private static Instant lastBuild = Instant.now().minusSeconds(6);
    private static List<Transport> LAST_TRANSPORT_LIST = Collections.emptyList();

    private static final WorldArea MLM = new WorldArea(3714, 5633, 60, 62, 0);

    public static List<Transport> buildTransports() {
        if (lastBuild.plusSeconds(BUILD_DELAY_SECONDS).isAfter(Instant.now())) {
            return List.copyOf(LAST_TRANSPORT_LIST);
        }

        lastBuild = Instant.now();
        List<Transport> transports = new ArrayList<>();
        try {
            InputStream txt = TransportLoader.class.getResourceAsStream("/transports.txt");
            String[] lines = new String(txt.readAllBytes()).split("\n");
            for (String l : lines) {
                String line = l.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                transports.add(parseTransportLine(line));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        int gold = Inventory.getFirst(995) != null ? Inventory.getFirst(995).getQuantity() : 0;
        if (gold >= 10) {
            transports.add(objectTransport(
                    new WorldPoint(3267, 3228, 0),
                    new WorldPoint(3268, 3228, 0),
                    2883,
                    "Pay-toll(10gp)")
            );
            transports.add(objectTransport(
                    new WorldPoint(3268, 3228, 0),
                    new WorldPoint(3267, 3228, 0),
                    2883,
                    "Pay-toll(10gp)")
            );
            transports.add(objectTransport(
                    new WorldPoint(3267, 3227, 0),
                    new WorldPoint(3268, 3227, 0),
                    2882,
                    "Pay-toll(10gp)")
            );
            transports.add(objectTransport(
                    new WorldPoint(3268, 3227, 0),
                    new WorldPoint(3267, 3227, 0),
                    2882,
                    "Pay-toll(10gp)")
            );
        }

        // Lumbridge castle dining room, ignore if RFD is in progress.
        if (Vars.getBit(RFD_VARBIT) == -1) {
            transports.add(objectTransport(new WorldPoint(3213, 3221, 0), new WorldPoint(3212, 3221, 0), 12349, "Open"));
            transports.add(objectTransport(new WorldPoint(3212, 3221, 0), new WorldPoint(3213, 3221, 0), 12349, "Open"));
            transports.add(objectTransport(new WorldPoint(3213, 3222, 0), new WorldPoint(3212, 3222, 0), 12350, "Open"));
            transports.add(objectTransport(new WorldPoint(3212, 3222, 0), new WorldPoint(3213, 3222, 0), 12350, "Open"));
            transports.add(objectTransport(new WorldPoint(3207, 3218, 0), new WorldPoint(3207, 3217, 0), 12348, "Open"));
            transports.add(objectTransport(new WorldPoint(3207, 3217, 0), new WorldPoint(3207, 3218, 0), 12348, "Open"));
        }

        if (Worlds.inMembersWorld()) {
            // Edgeville
            if (Skills.getBoostedLevel(Skill.AGILITY) >= 21) {
                transports.add(objectTransport(
                        new WorldPoint(3142, 3513, 0),
                        new WorldPoint(3137, 3516, 0),
                        16530,
                        "Climb-into")
                );
                transports.add(objectTransport(
                        new WorldPoint(3137, 3516, 0),
                        new WorldPoint(3142, 3513, 0),
                        16529,
                        "Climb-into")
                );
            }

            // Glarial's tomb
            transports.add(itemUseTransport(new WorldPoint(2557, 3444, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
            transports.add(itemUseTransport(new WorldPoint(2557, 3445, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
            transports.add(itemUseTransport(new WorldPoint(2558, 3443, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
            transports.add(itemUseTransport(new WorldPoint(2559, 3443, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
            transports.add(itemUseTransport(new WorldPoint(2560, 3444, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
            transports.add(itemUseTransport(new WorldPoint(2560, 3445, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
            transports.add(itemUseTransport(new WorldPoint(2558, 3446, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
            transports.add(itemUseTransport(new WorldPoint(2559, 3446, 0), new WorldPoint(2555, 9844, 0), 294, 1992));

            // Waterfall Island
            transports.add(itemUseTransport(new WorldPoint(2512, 3476, 0), new WorldPoint(2513, 3468, 0), 954, 1996));
            transports.add(itemUseTransport(new WorldPoint(2512, 3466, 0), new WorldPoint(2511, 3463, 0), 954, 2020));

            // Crabclaw island
            if (gold >= 10_000) {
                transports.add(npcTransport(new WorldPoint(1782, 3458, 0), new WorldPoint(1778, 3417, 0), 7483, "Travel"));
            }

            transports.add(npcTransport(new WorldPoint(1779, 3418, 0), new WorldPoint(1784, 3458, 0), 7484, "Travel"));

            // Port sarim
            if (Vars.getBit(4897) == 0) {
                if (Vars.getBit(8063) >= 7) {
                    transports.add(npcDialogTransport(new WorldPoint(3054, 3245, 0),
                            new WorldPoint(1824, 3691, 0),
                            8484,
                            "Can you take me to Great Kourend?"));
                } else {
                    transports.add(npcDialogTransport(new WorldPoint(3054, 3245, 0),
                            new WorldPoint(1824, 3691, 0),
                            8484,
                            "That's great, can you take me there please?"));
                }
            } else {
                transports.add(npcTransport(new WorldPoint(3054, 3245, 0),
                        new WorldPoint(1824, 3695, 1),
                        10724,
                        "Port Piscarilius"));
            }

            // Paterdomus
            transports.add(trapDoorTransport(new WorldPoint(3405, 3506, 0), new WorldPoint(3405, 9906, 0), 1579, 1581));
            transports.add(trapDoorTransport(new WorldPoint(3423, 3485, 0), new WorldPoint(3440, 9887, 0), 3432, 3433));
            transports.add(trapDoorTransport(new WorldPoint(3422, 3484, 0), new WorldPoint(3440, 9887, 0), 3432, 3433));

            // Port Piscarilius
            transports.add(npcTransport(new WorldPoint(1824, 3691, 0), new WorldPoint(3055, 3242, 1), 10727, "Port Sarim"));

            // Gnome stronghold
            transports.add(objectDialogTransport(new WorldPoint(2461, 3382, 0),
                    new WorldPoint(2461, 3385, 0),
                    190,
                    "Open",
                    "Sorry, I'm a bit busy."));

            // Tree Gnome Village
            if (Vars.getVarp(111) > 0) {
                transports.add(npcTransport(new WorldPoint(2504, 3192, 0), new WorldPoint(2515, 3159, 0), 4968, "Follow"));
                transports.add(npcTransport(new WorldPoint(2515, 3159, 0), new WorldPoint(2504, 3192, 0), 4968, "Follow"));
            }
        }

        // Entrana
        transports.add(npcTransport(new WorldPoint(3041, 3237, 0), new WorldPoint(2834, 3331, 1), 1166, "Take-boat"));
        transports.add(npcTransport(new WorldPoint(2834, 3335, 0), new WorldPoint(3048, 3231, 1), 1170, "Take-boat"));
        transports.add(npcDialogTransport(new WorldPoint(2821, 3374, 0),
                new WorldPoint(2822, 9774, 0),
                1164,
                "Well that is a risk I will have to take."));

        // Motherload Mine
        if (MLM.contains(Players.getLocal())) {
            transports.addAll(motherloadMineTransport(new WorldPoint(3726, 5643, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3726, 5654, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3727, 5652, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3727, 5683, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3728, 5651, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3728, 5688, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3731, 5683, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3733, 5680, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3745, 5689, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3748, 5684, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3748, 5689, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3755, 5640, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3756, 5639, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3757, 5677, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3759, 5690, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3762, 5652, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3762, 5668, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3765, 5688, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3766, 5639, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3766, 5647, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3768, 5674, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3768, 5679, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3769, 5642, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3769, 5658, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3769, 5680, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3770, 5659, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3771, 5638, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3762, 5687, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3766, 5670, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3719, 5664, 0)));
            transports.addAll(motherloadMineTransport(new WorldPoint(3720, 5665, 0)));
        }

        return List.copyOf(LAST_TRANSPORT_LIST = transports);
    }

    public static Transport parseTransportLine(String line) {
        String[] split = line.split(" ");
        return objectTransport(
                new WorldPoint(
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]),
                        Integer.parseInt(split[2])
                ),
                new WorldPoint(
                        Integer.parseInt(split[3]),
                        Integer.parseInt(split[4]),
                        Integer.parseInt(split[5])
                ),
                Integer.parseInt(split[split.length - 1]), split[6]
        );
    }

    public static Transport trapDoorTransport(
            WorldPoint source,
            WorldPoint destination,
            int closedId,
            int openedId
    ) {
        return new Transport(source, destination, Integer.MAX_VALUE, 0, () -> {
            TileObject openedTrapdoor = TileObjects.getFirstSurrounding(source, 5, openedId);
            if (openedTrapdoor != null) {
                openedTrapdoor.interact(0);
                return;
            }

            TileObject closedTrapDoor = TileObjects.getFirstSurrounding(source, 5, closedId);
            if (closedTrapDoor != null) {
                closedTrapDoor.interact(0);
            }
        });
    }

    public static Transport itemUseTransport(
            WorldPoint source,
            WorldPoint destination,
            int itemId,
            int objId
    ) {
        return new Transport(source, destination, Integer.MAX_VALUE, 0, () -> {
            Item item = Inventory.getFirst(itemId);
            if (item == null) {
                return;
            }

            TileObject transport = TileObjects.getFirstSurrounding(source, 5, objId);
            if (transport != null) {
                item.useOn(transport);
            }
        });
    }

    public static Transport npcTransport(
            WorldPoint source,
            WorldPoint destination,
            int npcId,
            String action
    ) {
        return new Transport(source, destination, 10, 0, () -> {
            NPC npc = NPCs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= 10 && x.getId() == npcId);
            if (npc != null) {
                npc.interact(action);
            }
        });
    }

    public static Transport npcDialogTransport(
            WorldPoint source,
            WorldPoint destination,
            int npcId,
            String... chatOptions
    ) {
        return new Transport(source, destination, 10, 0, () -> {
            if (Dialog.isViewingOptions()) {
                if (Dialog.canContinue()) {
                    Dialog.continueSpace();
                    return;
                }

                if (Dialog.chooseOption(chatOptions)) {
                    return;
                }

                return;
            }

            NPC npc = NPCs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= 10 && x.getId() == npcId);
            if (npc != null) {
                npc.interact(0);
            }
        });
    }

    public static List<Transport> motherloadMineTransport(
            WorldPoint rockfall
    ) {
        return Arrays.stream(Direction.values()).map(dir -> {
            WorldPoint neighbor = Reachable.getNeighbour(dir, rockfall);
            if (Reachable.isWalkable(neighbor)) {
                WorldPoint dest = null;
                switch (dir) {
                    case NORTH -> dest = rockfall.dy(-1);
                    case SOUTH -> dest = rockfall.dy(1);
                    case WEST -> dest = rockfall.dx(1);
                    case EAST -> dest = rockfall.dx(-1);
                }
                if (dest != null) {
                    WorldPoint finalDest = dest;
                    return new Transport(neighbor, finalDest, Integer.MAX_VALUE, 0, () -> {
                        TileObjects.getAt(rockfall, x -> x.getName().equalsIgnoreCase("Rockfall")).stream()
                                .findFirst()
                                .ifPresentOrElse(obj -> obj.interact("Mine"), () -> Movement.walk(finalDest));
                    });
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static Transport objectTransport(
            WorldPoint source,
            WorldPoint destination,
            int objId,
            String action
    ) {
        return new Transport(source, destination, Integer.MAX_VALUE, 0, () ->
                TileObjects.getSurrounding(source, 5, x -> x.getId() == objId).stream()
                        .findFirst()
                        .ifPresent(obj -> obj.interact(action)));
    }

    public static Transport objectDialogTransport(
            WorldPoint source,
            WorldPoint destination,
            int objId,
            String action,
            String... chatOptions
    ) {
        return new Transport(source, destination, Integer.MAX_VALUE, 0, () -> {
            if (Dialog.isViewingOptions()) {
                if (Dialog.canContinue()) {
                    Dialog.continueSpace();
                    return;
                }

                if (Dialog.chooseOption(chatOptions)) {
                    return;
                }

                return;
            }

            TileObject transport = TileObjects.getFirstSurrounding(source, 5, objId);
            if (transport != null) {
                transport.interact(action);
            }
        });
    }
}
