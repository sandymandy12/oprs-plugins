/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.deecat.dcgroundmarkers;

import net.runelite.client.config.*;
import net.runelite.client.plugins.deecat.SquareOverlay;
import net.runelite.client.ui.overlay.OverlayLayer;

import java.awt.*;

@ConfigGroup(DCGroundMarkerConfig.GROUND_MARKER_CONFIG_GROUP)
public interface DCGroundMarkerConfig extends Config
{
	String GROUND_MARKER_CONFIG_GROUP = "dcgroundmarkers";
	String SHOW_IMPORT_EXPORT_KEY_NAME = "showImportExport";
	String SHOW_CLEAR_KEY_NAME = "showClear";
	@ConfigSection(
			name = "Solid Square",
			description = "Solid square modification",
			position = 0,
			closedByDefault = true
	)
	String solidSquare = "solidSquare";

	@ConfigItem(
			keyName = "solidSquareSize",
			name = "Solid Square Sieze",
			description = "solidSquareSize",
			section = solidSquare
	)
	default int solidSquareSize()
	{
		return 20;
	}

	@ConfigItem(
			keyName = "layer",
			name = "Layer",
			description = "The layer of the overlay",
			section = solidSquare
	)
	default OverlayLayer overlayLayer()
	{
		return SquareOverlay.OVERLAY_LAYER2;
	}

	@Alpha
	@ConfigItem(
		keyName = "markerColor",
		name = "Color of the tile",
		description = "Configures the color of marked tile"
	)
	default Color markerColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
			keyName = "isCheckAnimation",
			name = "Checks animation",
			description = "Check to see if player is performing an animation"
	)
	default boolean isCheckAnimation()
	{
		return false;
	}

	@ConfigItem(
		keyName = "rememberTileColors",
		name = "Remember color per tile",
		description = "Color tiles using the color from time of placement"
	)
	default boolean rememberTileColors()
	{
		return false;
	}

	@ConfigItem(
			keyName = "isCheckInteraction",
			name = "Checks for interation",
			description = "Check to see if player is interacting"
	)
	default boolean isCheckInteraction()
	{
		return false;
	}

	@ConfigItem(
		keyName = "drawOnMinimap",
		name = "Draw tiles on minimap",
		description = "Configures whether marked tiles should be drawn on minimap"
	)
	default boolean drawTileOnMinimmap()
	{
		return false;
	}

	@ConfigItem(
		keyName = "maxDistanceFromPlayer",
		name = "maxDistanceFromPlayer",
		description = "maxDistanceFromPlayer"
	)
	default int maxDistanceFromPlayer()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "disableAtDestination",
		name = "disableAtDestination",
		description = "disableAtDestination"
	)
	default boolean disableAtDestination()
	{
		return false;
	}
}
