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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Collection;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.deecat.SquareOverlay;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class DCGroundMarkerOverlay extends Overlay
{
	private static final int MAX_DRAW_DISTANCE = 32;

	private final Client client;
	private final DCGroundMarkerConfig config;
	private final DCGroundMarkerPlugin plugin;

	@Inject
	private DCGroundMarkerOverlay(Client client, DCGroundMarkerConfig config, DCGroundMarkerPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(SquareOverlay.OVERLAY_LAYER2);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Collection<ColorTileMarker> points = plugin.getPoints();
		for (final ColorTileMarker point : points)
		{
			WorldPoint worldPoint = point.getWorldPoint();
			if (worldPoint.getPlane() != client.getPlane())
			{
				continue;
			}

			Color tileColor = point.getColor();
			if (tileColor == null || !config.rememberTileColors())
			{
				// If this is an old tile which has no color, or rememberTileColors is off, use marker color
				tileColor = config.markerColor();
			}

			LocalPoint lp = LocalPoint.fromWorld(client,worldPoint);
			Point p = Perspective.localToCanvas(client,lp,worldPoint.getPlane());
			String text = worldPoint.toString();

			//WorldPoint localWp = client.getLocalPlayer().getWorldLocation();
			//int text = localWp.distanceTo(WorldPoint.fromLocal(client,lp));
			OverlayUtil.renderTextLocation(graphics,p,""+text,tileColor);
			//drawTile(graphics, worldPoint, tileColor);
		}

		return null;
	}

	private void drawTile(Graphics2D graphics, WorldPoint point, Color color)
	{
		Player local = client.getLocalPlayer();
		if (local == null) return;
		WorldPoint playerLocation = local.getWorldLocation();
		int distance = point.distanceTo(playerLocation);
		if (distance >= MAX_DRAW_DISTANCE && config.maxDistanceFromPlayer() == 0)
		{
			return;
		}
		if (distance >= 100)
		{
			return;
		}

		if (distance == 0 && config.disableAtDestination())
		{
			return;
		}

		LocalPoint lp;
		if (config.maxDistanceFromPlayer() > 0)
		{
			lp = LocalPoint.fromWorld(client, inbetweenTile(playerLocation, point, Math.min(distance, config.maxDistanceFromPlayer())));
		}
		else
		{
			lp = LocalPoint.fromWorld(client, point);
		}
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}

		if (config.solidSquareSize() > 0)
		{
			drawCenterSquare(graphics, poly, config.solidSquareSize(), color);
		}
		else
		{
			OverlayUtil.renderPolygon(graphics, poly, color);
		}
	}

	/**
	 *
	 * @param a origin
	 * @param a dest
	 * @param distance
	 * @return tile "distance" tiles away from origin in direction of destination
	 */
	public static WorldPoint inbetweenTile(WorldPoint a, WorldPoint b, double distance)
	{
		double deltaX = b.getX() - a.getX();
		double deltaY = b.getY() - a.getY();
		double angle = Math.abs(Math.atan(deltaY / deltaX));
		double xOffset = Math.cos(angle) * distance * (deltaX / Math.abs(deltaX));
		double yOffset = Math.sin(angle) * distance * (deltaY / Math.abs(deltaY));
		int newX = (int)Math.round(xOffset + a.getX());
		int newY = (int)Math.round(yOffset + a.getY());
		return new WorldPoint(newX, newY, a.getPlane());
	}


	private void drawCenterSquare(Graphics2D g, int centerX, int centerY, int size, Color color)
	{
		g.setColor(color);
		g.fillRect(centerX - size / 2, centerY - size / 2, size, size);
	}
	private void drawCenterSquare(Graphics2D g, double centerX, double centerY, int size, Color color)
	{
		drawCenterSquare(g, (int)centerX, (int)centerY, size, color);
	}

	private void drawCenterSquare(Graphics2D g, Polygon p, int size, Color color)
	{
		Rectangle r = p.getBounds();
		drawCenterSquare(g, (int)r.getCenterX(), (int)r.getCenterY(), size, color);
	}
}
