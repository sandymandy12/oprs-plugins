package net.runelite.client.plugins.deecat;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.Instant.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;


public class Utility {

    public static void sleep(long ms){
        long now = now().toEpochMilli();
        long elapsed = 0;
        while (elapsed < ms){
            if (elapsed >= ms) {break;}
            else { elapsed = now().toEpochMilli() - now;};
        }
    }

    public static Point randomPoint(Rectangle bounds){
        int w = 1;//Math.toIntExact(Math.round(bounds.width * 0.4));
        int h = 1;//Math.toIntExact(Math.round(bounds.height * 0.4));

        int x = bounds.x + ThreadLocalRandom.current().nextInt(w, bounds.width - w);
        int y = bounds.y + ThreadLocalRandom.current().nextInt(h, bounds.height - h);
        Point p = new Point(x,y);
        return p;
    }
    public static Point randomPoint(Rectangle bounds, float skew){
        int w = 1 ;//Math.toIntExact(Math.round(bounds.width * 0.4));
        int h = 1 ;//Math.toIntExact(Math.round(bounds.height * 0.4));

        int midX = (bounds.width / 2);
        int midY = (bounds.height / 2);

        int randW = ThreadLocalRandom.current().nextInt(w, bounds.width - w);
        int randH = ThreadLocalRandom.current().nextInt(h, bounds.height - h);

        float skewW = (midX - randW) * skew / 100;
        float skewH = (midY - randH) * skew / 100;

        int x = (int) (bounds.x + midX - skewW);
        int y = (int) (bounds.y + midY - skewH);

        return new Point(x,y);
    }

    public static int rand(int start, int end){
        return ThreadLocalRandom.current().nextInt(start,end);
    }
    public static int rand(long start, long end){
        int s = Math.toIntExact(start);
        int e = Math.toIntExact(end);
        return ThreadLocalRandom.current().nextInt(s,e);
    }
}
