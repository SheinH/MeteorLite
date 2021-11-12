package dev.hoot.api.entities;

import dev.hoot.api.game.Game;
import dev.hoot.api.game.GameThread;
import net.runelite.api.NPC;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NPCs extends Entities<NPC> {
    private static final NPCs NPCS = new NPCs();

    @Override
    protected List<NPC> all(Predicate<? super NPC> filter) {
        List<NPC> out = new ArrayList<>();
        List<NPC> npcs = Game.getClient().getNpcs();
        List<NPC> uncached = npcs.stream()
                .filter(x -> x.isTransformRequired() && !x.isDefinitionCached())
                .collect(Collectors.toList());
        if (!uncached.isEmpty()) {
            GameThread.invokeLater(() -> {
                for (NPC npc : uncached) {
                    npc.getName(); // Transform and cache it by calling getName
                }

                return true;
            });
        }

        for (NPC npc : npcs) {
            if (filter.test(npc)) {
                out.add(npc);
            }
        }

        return out;
    }

    public static List<NPC> getAll(Predicate<NPC> filter) {
        return NPCS.all(filter);
    }

    public static List<NPC> getAll(int... ids) {
        return NPCS.all(ids);
    }

    public static List<NPC> getAll(String... names) {
        return NPCS.all(names);
    }

    public static NPC getNearest(Predicate<NPC> filter) {
        return NPCS.nearest(filter);
    }

    public static NPC getNearest(int... ids) {
        return NPCS.nearest(ids);
    }

    public static NPC getNearest(String... names) {
        return NPCS.nearest(names);
    }
}
