package net.runelite.client.plugins.spoontob.rooms.Verzik;

import net.runelite.api.Point;
import java.util.ArrayList;

public class YellowGroup {
    public Point a;
    public Point b;
    public Point c;

    public YellowGroup(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public YellowGroup(ArrayList<Point> points) {
        this.a = points.get(0);
        this.b = points.get(1);
        this.c = points.get(2);
    }
}