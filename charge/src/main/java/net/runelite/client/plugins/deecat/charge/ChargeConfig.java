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
package net.runelite.client.plugins.deecat.charge;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("charge")
public interface ChargeConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "blank",
            name = "blank",
            description = "blank"
    )
    default boolean blank()
    {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "traverse",
            name = "Traverse trail",
            description = "If active then will start traversing"
    )
    default boolean traverse()
    {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "minEnergy",
            name = "Minimum energy %",
            description = "If energy is below this then will use pot"
    )
    default int minEnergy()
    {
        return 30;
    }

    @ConfigSection(
            name = "Threshold",
            description = "List of thresholds",
            position = 0
    )
    String thresh = "thresh";


    @ConfigItem(
            position = 1,
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
            position = 2,
            keyName = "clickThresh",
            name = "Clicks",
            description = "Only click after this",
            section = thresh
    )
    default int clickThresh()
    {
        return 1200;
    }

}