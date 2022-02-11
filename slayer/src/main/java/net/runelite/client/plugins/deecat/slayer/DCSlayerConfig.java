/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.deecat.slayer;

import net.runelite.client.config.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("dcslayer")
public interface DCSlayerConfig extends Config
{
    @ConfigSection(
            name = "Threshold",
            description = "blank",
            closedByDefault = true,
            position = 0
    )
    String thresh = "thresh";

    @ConfigSection(
            name = "Tasks",
            description = "Choose from these tasks",
            closedByDefault = true,
            position = 1
    )
    String tasks = "tasks";

    @ConfigSection(
            name = "Hotkeys",
            description = "Choose from these tasks",
            position = 1
    )
    String hotkeys = "hotkeys";

    @ConfigSection(
            name = "Health",
            description = "Health related options",
            closedByDefault = true,
            position = 1
    )
    String health = "health";

    @ConfigItem(
            position = 0,
            keyName = "stay",
            name = "boolean",
            description = "Don't leave. It's my fault",
            section = thresh
    )
    default boolean stay()
    {
        return true;
    }

    @ConfigItem(
            position = 0,
            keyName = "heal",
            name = "Heal",
            description = "Heal/Pot up, because.. why not",
            section = health
    )
    default boolean heal()
    {
        return true;
    }
    @ConfigItem(
            position = 0,
            keyName = "flick",
            name = "Flick",
            description = "flick",
            section = health
    )
    default boolean flick()
    {
        return true;
    }

    @ConfigItem(
            position = 0,
            keyName = "minHp",
            name = "Restore HP",
            description = "Don't let my hp fall below this",
            section = health
    )
    default int minHp()
    {
        return 30;
    }

    @ConfigItem(
            position = 2,
            keyName = "maxHp",
            name = "Eject HP",
            description = "Aight imma head out",
            section = health
    )
    default int ejectHp()
    {
        return 20;
    }

    @ConfigItem(
            position = 1,
            keyName = "minPrayer",
            name = "Restore Prayer",
            description = "Pot up if prayer falls below this",
            section = health
    )
    default int minPrayer()
    {
        return 30;
    }

    @ConfigItem(
            position = 3,
            keyName = "ejectPray",
            name = "Eject Prayer",
            description = "Aight imma head out",
            section = health
    )
    default int ejectPray()
    {
        return 1;
    }

    @ConfigItem(
            position = 2,
            keyName = "hopThresh",
            name = "Hop Thresh",
            description = "Limit how often to hop",
            section = thresh
    )
    default int hopThresh()
    {
        return 1200;
    }

    @ConfigItem(
            position = 4,
            keyName = "clickThresh",
            name = "Clicks",
            description = "Only click after this",
            section = thresh
    )
    default int clickThresh()
    {
        return 600;
    }

    @ConfigItem(
            position = 3,
            keyName = "traverseIsle",
            name = "Traverse",
            description = "Traverse to Lava Isle",
            section = tasks
    )
    default boolean traverse()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "run",
            name = "Run",
            description = "Run in any direction",
            section = tasks
    )
    default Direction run()
    {
        return Direction.STAY;
    }


    @ConfigItem(
            keyName = "npcs",
            name = "NPC's",
            description = "Npc's for task",
            position = 7
    )
    default String npcs() { return ""; }

    @ConfigItem(
            keyName = "safespots",
            name = "Safespot",
            description = "Safe sport",
            position = 7
    )
    default String safeSpot() { return "0,0,0"; }

    @ConfigItem(
            keyName = "safeSpotting",
            name = "Safespotting",
            description = "moving to safespot",
            position = 7
    )
    default boolean safeSpotting() { return false; }

    @ConfigItem(
            keyName = "ignore",
            name = "Ignore",
            description = "Ignore these names",
            position = 8
    )
    default String ignore() { return ""; }

    @ConfigItem(
            keyName = "gearSet1",
            name = "Gear Set 1",
            description = "itemID separated with commas ",
            position = 8
    )
    default String gearSet1() { return ""; }
    @ConfigItem(
            keyName = "gearSet2",
            name = "Gear Set 2",
            description = "Gear swap 2",
            position = 8
    )
    default String gearSet2() { return ""; }
    @ConfigItem(
            keyName = "gearSet3",
            name = "Gear Set 3",
            description = "Gear swap 3",
            position = 8
    )
    default String gearSet3() { return ""; }

    @ConfigItem(
            position = 3,
            keyName = "blastOff",
            name = "Blast off",
            description = "In case someone shows up to spoil the party"
    )
    default boolean blastOff()
    {
        return true;
    }
    @ConfigItem(
            position = 4,
            keyName = "blastOptions",
            name = "Teleport",
            description = "Select the way you would like to tele out"
    )
    default BlastOff teleOptions()
    {
        return BlastOff.ALWAYS;
    }
    @ConfigItem(
        position = 5,
        keyName = "hopOptions",
        name = "Hop",
        description = "Select when to blast out"
)
    default BlastOff hopOptions()
{
    return BlastOff.ALWAYS;
}

    @ConfigItem(
            position = 3,
            keyName = "appendDefault",
            name = "Teleport Lock",
            description = "Enable this for auto tele below lvl 30"
    )
    default boolean appendDefault()
    {
        return true;
    }

    @ConfigItem(
            keyName = "telekey",
            name = "Teleport",
            description = "Tele key",
            position = 0,
            section = hotkeys
    )
    default Keybind teleKey()
    {
        return new Keybind(KeyEvent.VK_OPEN_BRACKET, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    }

    @ConfigItem(
            keyName = "girdUp",
            name = "Gear 2 swap",
            description = "Gear 2 hotkey",
            position = 2,
            section = hotkeys
    )
    default Keybind gearSwap2()
    {
        return new Keybind(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    }

    @ConfigItem(
            keyName = "gearSwap",
            name = "Gear Swap",
            description = "Gear setup 2 hotkey",
            position = 1,
            section = hotkeys
    )
    default Keybind gearSwap()
    {
        return new Keybind(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    }

    @ConfigItem(
            keyName = "gear3Hotkey",
            name = "Gear 3",
            description = "Gear 3 hotkey",
            position = 2,
            section = hotkeys
    )
    default Keybind gearSwap3()
    {
        return new Keybind(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    }

    @ConfigItem(
            keyName = "hopKey",
            name = "Default hop key",
            description = "This key needs to match world hopper 'previous' key [set to '\\']",
            position = 3,
            section = hotkeys
    )
    default String hopKey() { return "BACK_SLASH"; }

}