/*
 * Copyright (c) 2021, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.deecat.magic;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MagicConfig.GROUP)
public interface MagicConfig extends Config
{
	String GROUP = "dcmagic";

	@ConfigItem(
			keyName = "highAlch",
			name = "High Alch",
			description = "High Level alchemy helper.",
			position = 0
	)
	default boolean highAlch()
	{
		return false;
	}
	@ConfigItem(
		keyName = "alchSlot",
		name = "Alch inventory slot",
		description = "Inventory slot with alchable",
		position = 1
	)
	default int alchSlot()
{
	return 3;
}

	@ConfigItem(
			keyName = "enchant	",
			name = "Enchant",
			description = "Enchant.",
			position = 2
	)
	default boolean enchant()
	{
		return false;
	}

	@ConfigItem(
			keyName = "altSpell",
			name = "Alt spell",
			description = "Alt spell.",
			position = 3
	)
	default boolean alt()
	{
		return false;
	}

	@ConfigItem(
			keyName = "spell",
			name = "Spell",
			description = "chooose spell.",
			position = 4
	)
	default Spells spell()
	{
		return Spells.ENCHANT_LVL1;
	}
}
