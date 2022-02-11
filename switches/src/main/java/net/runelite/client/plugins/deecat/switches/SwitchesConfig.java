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
package net.runelite.client.plugins.deecat.switches;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("Switches")
public interface SwitchesConfig extends Config
{
	@ConfigSection(
		name = "Threshold",
		description = "blank",
		position = 0
	)
    String thresh = "thresh";

	@ConfigItem(
			position = 2,
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
			position = 6,
			keyName = "swapTimer",
			name = "Swaps",
			description = ""
	)
	default int swapTimer()
	{
		return 300;
	}

		@ConfigItem(
				position = 0,
				keyName = "thresholdNotification",
				name = "Threshold Notifications",
				description = "Sends a notification when your special attack counter exceeds the threshold"
		)
		default boolean thresholdNotification()
		{
			return false;
		}
	@ConfigItem(
			position = 0,
			keyName = "teleOut",
			name = "Teleport",
			description = "live to see another day"
	)
	default boolean teleOut()
	{
		return true;
	}

	@ConfigItem(
			position = 0,
			keyName = "swapping",
			name = "Swap",
			description = "Going to swap or nah"
	)
	default boolean swapping()
	{
		return true;
	}

	@ConfigItem(
			position = 0,
			keyName = "autoAttack",
			name = "Attack",
			description = "for the kill"
	)
	default boolean autoAttack()
	{
		return false;
	}

		@ConfigItem(
				position = 1,
				keyName = "dragonWarhammerThreshold",
				name = "Dragon Warhammer",
				description = "Threshold for Dragon Warhammer (0 to disable)"
		)
		default int dragonWarhammerThreshold()
		{
			return 0;
		}

	@ConfigItem(
			position = 5,
			keyName = "darkBowThreshold",
			name = "Dark bow",
			description = "Threshold for Dark bow (0 to disable)"
	)
	default int darkBowThreshold()
	{
		return 549;
	}

	@ConfigItem(
			position = 6,
			keyName = "minSpec",
			name = "Min Special",
			description = "Threshold for use spec above this"
	)
	default int minSpec()
	{
		return 500;
	}

	}