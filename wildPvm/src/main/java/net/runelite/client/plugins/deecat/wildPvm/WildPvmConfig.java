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
package net.runelite.client.plugins.deecat.wildPvm;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("wildPvm")
public interface WildPvmConfig extends Config
{
    @ConfigSection(
            name = "Threshold",
            description = "blank",
            position = 0
    )
    String thresh = "thresh";

    @ConfigSection(
            name = "tasks",
            description = "Choose from these tasks",
            position = 1
    )
    String tasks = "tasks";

    @ConfigItem(
            position = 0,
            keyName = "stay",
            name = "Stay",
            description = "Don't leave. It's my fault",
            section = thresh
    )
    default boolean stay()
    {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "minHp",
            name = "Min hp",
            description = "Heal if HP falls below this",
            section = thresh
    )
    default int minHp()
    {
        return 10;
    }

    @ConfigItem(
            position = 2,
            keyName = "timer",
            name = "Loot Timer",
            description = "After each click while looting, the timer resets",
            section = thresh
    )
    default int timer()
    {
        return 2200;
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
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "leave",
            name = "Leave",
            description = "Leave the Dragon's Lair",
            section = tasks
    )
    default boolean leave()
    {
        return true;
    }
}