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
package net.runelite.client.plugins.deecat.blueDragon;

import net.runelite.client.config.*;

@ConfigGroup("blueDragon")
public interface BlueDragonConfig extends Config
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
            keyName = "blank",
            name = "blank",
            description = "blank",
            section = thresh
    )
    default boolean blank()
    {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "blank",
            name = "blank",
            description = "blank",
            section = thresh
    )
    default boolean blank1()
    {
        return false;
    }


    @ConfigItem(
            position = 3,
            keyName = "maxHp",
            name = "Max hp",
            description = "",
            section = thresh
    )
    default int maxHp()
    {
        return 60;
    }

    @ConfigItem(
            position = 3,
            keyName = "minHp",
            name = "Min hp",
            description = "",
            section = thresh
    )
    default int minHp()
    {
        return 30;
    }

    @ConfigItem(
            position = 3,
            keyName = "timer",
            name = "Timer",
            description = "",
            section = thresh
    )
    default int timer()
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
        return 1200;
    }

    @ConfigItem(
            position = 3,
            keyName = "activate",
            name = "activate",
            description = "bluedragon",
            section = tasks
    )
    default boolean activate()
    {
        return true;
    }
}