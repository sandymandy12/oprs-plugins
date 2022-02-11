package net.runelite.client.plugins.deecat;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.infobox.Timer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;


public class SquareOverlay {
    public static OverlayLayer OVERLAY_LAYER2 = OverlayLayer.ABOVE_WIDGETS;
    public static OverlayLayer OVERLAY_LAYER = OverlayLayer.UNDER_WIDGETS;

    public static void drawCenterSquare(Graphics2D g, int centerX, int centerY, int size, Color color)
    {
        g.setColor(color);
        g.fillRect(centerX - size / 2, centerY - size / 2, size, size);
    }
    public static void drawCenterSquare(Graphics2D g, double centerX, double centerY, int size, Color color)
    {
        drawCenterSquare(g, (int)centerX, (int)centerY, size, color);
    }
    public static void drawCenterSquare(Graphics2D g, Rectangle bounds, int size, Color color)
    {
        drawCenterSquare(g, bounds.getCenterX(), bounds.getCenterY(), size, color);
    }

    public static void drawCenterSquare(Graphics2D g, Rectangle2D bounds, int size, Color color)
    {
        drawCenterSquare(g, bounds.getCenterX(), bounds.getCenterY(), size, color);
    }
    public static void drawCenterSquare(Graphics2D g, Point p, int size, Color color)
    {
        drawCenterSquare(g, p.getX(), p.getY(), size, color);
    }


    public static void drawCenterSquare(Graphics2D g, Actor actor, int size, Color color)
    {
        Point p = actor.getCanvasTextLocation(g, ".", actor.getLogicalHeight() / 2);
        if (p != null)
        drawCenterSquare(g, p.getX(), p.getY(), size, color);
    }

    public static void drawCenterSquare(Graphics2D g, Client client, WorldPoint worldPoint, int size, Color color)
    {
        LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
        if (lp == null) return;
        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) return;
        drawCenterSquare(g, poly, size, color);
    }

    public static void drawOnMinimap(Client client, Graphics2D graphics, WorldPoint point, Color color, int size)
    {
        Player local = client.getLocalPlayer();
        if (local == null) return;
        WorldPoint playerLocation = local.getWorldLocation();
        int distance = point.distanceTo(playerLocation);

        if (distance >= 100 || point.getPlane() != client.getPlane())
        {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
        {
            return;
        }

        Point posOnMinimap = Perspective.localToMinimap(client, lp);
        if (posOnMinimap == null)
        {
            return;
        }

        drawCenterSquare(graphics, posOnMinimap, size, color);
    }


    public static void drawCenterSquare(Graphics2D g, Polygon p, int size, Color color)
    {
        Rectangle r = p.getBounds();
        drawCenterSquare(g, (int)r.getCenterX(), (int)r.getCenterY(), size, color);
    }

    public static void drawTile(Client client, Graphics2D graphics, WorldPoint point, Color color, int size)
    {
        Player local = client.getLocalPlayer();
        if (local == null) return;
        WorldPoint playerLocation = local.getWorldLocation();
        int distance = point.distanceTo(playerLocation);
        if (distance >= 100 || point.getPlane() != client.getPlane())
        {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
        {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
        {
            return;
        }

        drawCenterSquare(graphics, poly, size, color);
    }




    private static HashMap<java.awt.Point, Timer> timerHashMap = new HashMap<>();

}
