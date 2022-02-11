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

import java.awt.*;
import java.time.Instant;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.deecat.FountainTrail;
import net.runelite.client.plugins.deecat.Obelisk;
import net.runelite.client.plugins.deecat.SquareOverlay;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class DCObjectIndicatorsOverlay extends Overlay
{
	private final Client client;
	private final DCObjectIndicatorsConfig config;
	private final DCObjectIndicatorsPlugin plugin;

	@Inject
	private DCObjectIndicatorsOverlay(Client client, DCObjectIndicatorsConfig config, DCObjectIndicatorsPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		int clickThresh = config.clickThresh();

		if(plugin.ignoreObject()){
			return null;
		}

		WorldPoint localWp = client.getLocalPlayer().getWorldLocation();
		Obelisk obelisk = plugin.closestObelisk();
		for (ColorTileObject colorTileObject : plugin.getObjects())
		{
			TileObject object = colorTileObject.getTileObject();
			Color color = colorTileObject.getColor();

			if (object.getId() == 39656){
				if(localWp.getX() > FountainTrail.SAFE.getWorldPoint().getX()){
					continue;
				}
			}

			if (object.getPlane() != client.getPlane())
			{
				continue;
			}

			if (color == null || !config.rememberObjectColors())
			{
				// Fallback to the current config if the object is marked before the addition of multiple colors
				color = config.markerColor();
			}

			final Shape polygon;
			Shape polygon2 = null;

			if (object instanceof GameObject)
			{
				polygon = ((GameObject) object).getConvexHull();
			}
			else if (object instanceof WallObject)
			{
				polygon = ((WallObject) object).getConvexHull();
				polygon2 = ((WallObject) object).getConvexHull2();
			}
			else if (object instanceof DecorativeObject)
			{
				polygon = ((DecorativeObject) object).getConvexHull();
				polygon2 = ((DecorativeObject) object).getConvexHull2();
			}
			else if (object instanceof GroundObject)
			{
				polygon = ((GroundObject) object).getConvexHull();
			}
			else
			{
				polygon = object.getCanvasTilePoly();
			}

			if (polygon != null)
			{

				long clickElapsed = Instant.now().toEpochMilli() - client.getMouseLastPressedMillis();

				String text = "";

				if (object.getId() == 26782){ //Fountain of rune


					if (clickElapsed <= config.altClickThresh() || localWp.distanceTo(object.getWorldLocation()) >= 4){
						continue;
					}
					color = config.altColor();

					SquareOverlay.drawCenterSquare(graphics, polygon.getBounds(), config.squareSize(), color);
					OverlayUtil.renderTextLocation(graphics,object.getCanvasLocation(),text,color);
				}
				else if (localWp.distanceTo(object.getWorldLocation()) <= 2)
				{
					SquareOverlay.drawCenterSquare(graphics, polygon.getBounds(), config.squareSize(), color);
					OverlayUtil.renderTextLocation(graphics,object.getCanvasLocation(),text,color);
				}

				//OverlayUtil.renderPolygon(graphics, polygon, color);
			}

			if (polygon2 != null)
			{

				OverlayUtil.renderPolygon(graphics, polygon2, color);
			}
		}

		return null;
	}
}