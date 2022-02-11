package net.runelite.client.plugins.deecat.charge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public
enum Obelisk
{
    LVL13("13", new WorldPoint(3156,3620,0),new WorldPoint(3158,3618,0),14829),
    LVL19("19", new WorldPoint(3227,3667,0),new WorldPoint(3225,3665,0),14830),
    LVL27("27", new WorldPoint(3035,3732,0),new WorldPoint(3033,3730,0),14827),
    LVL35("35", new WorldPoint(3106,3794,0),new WorldPoint(3104,3792,0),14828),
    LVL44("44", new WorldPoint(2980,3866,0),new WorldPoint(2982,3864,0),14826),
    LVL50("50", new WorldPoint(3307,3916,0),new WorldPoint(3305,3914,0),14831);

    private static final Map<String, Obelisk> by_level = new HashMap<>();
    private static final Map<WorldPoint, Obelisk> by_center = new HashMap<>();
    private static final Map<WorldPoint, Obelisk> by_pillar = new HashMap<>();
    private static final Map<Integer, Obelisk> by_objectId = new HashMap<>();


    private final String level;
    private final WorldPoint center;
    private final WorldPoint pillar;
    private final int objectID;

    static {
        for (Obelisk e: values()) {
            by_level.put(e.level, e);
            by_center.put(e.center, e);
            by_pillar.put(e.pillar, e);
            by_objectId.put(e.objectID, e);

        }
    }

    public static Obelisk valueOfLevel(String level) {
        return by_level.get(level);
    }

    public static Obelisk valueOfObjectId(int objectID) {
        return by_objectId.get(objectID);
    }

    public static Obelisk valueOfCenter(String center) {
        return by_center.get(center);
    }
    public static Obelisk valueOfPillar(String pillar) {
        return by_pillar.get(pillar);
    }
}
