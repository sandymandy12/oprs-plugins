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

package net.runelite.client.plugins.deecat.dcobjectindicators;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("dcobjectindicators")
public interface DCObjectIndicatorsConfig extends Config
{
	@Alpha
	@ConfigItem(
		keyName = "markerColor",
		name = "Marker color",
		position = 0,
		description = "Configures the color of object marker"
	)
	default Color markerColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
			keyName = "altColor",
			name = "Alternate color",
			position = 1,
			description = "Alternate color for marker. (fountain) "
	)
	default Color altColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		keyName = "rememberObjectColors",
		name = "Remember color per object",
		description = "Color objects using the color from time of marking"
	)
	default boolean rememberObjectColors()
	{
		return false;
	}

	@ConfigItem(
			position = 4,
			keyName = "squareOverlay",
			name = "Square Overlay",
			description = "Size of square"
	)
	default int squareSize()
	{
		return 20;
	}

	@ConfigItem(
			position = 5,
			keyName = "clickThresh",
			name = "Click Threshold",
			description = "Threshold between clikcs"
	)
	default int clickThresh()
	{
		return 1200;
	}

	@ConfigItem(
			position = 6,
			keyName = "altClickThresh",
			name = "Alt Click Threshold",
			description = "Alt Threshold between clikcs"
	)
	default int altClickThresh()
	{
		return 1200;
	}

}
