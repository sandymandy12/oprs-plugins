package net.runelite.client.plugins.deecat.slayer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Direction {

    STAY("Stay"),
    NORTH("North"),
    SOUTH("South"),
    EAST("East"),
    WEST("West"),
    NORTHEAST("NorthEast"),
    NORTHWEST("NorthWest"),
    SOUTHEAST("SouthEast"),
    SOUTHWEST("SouthWest");

    private final String name;
}
