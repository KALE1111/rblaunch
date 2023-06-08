package net.runelite.client.plugins.spoontob.rooms.Xarpus;

import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;

import java.util.function.Function;

public enum LineOfSight {
    NE_BOX((xarpus) -> new Point[]{new Point(xarpus.getX(), xarpus.getY()), new Point(xarpus.getX(), xarpus.getY() + 8), new Point(xarpus.getX() + 8, xarpus.getY() + 8), new Point(xarpus.getX() + 8, xarpus.getY())}),
    NW_BOX((xarpus) -> new Point[]{new Point(xarpus.getX() - 8, xarpus.getY()), new Point(xarpus.getX() - 8, xarpus.getY() + 8), new Point(xarpus.getX(), xarpus.getY() + 8), new Point(xarpus.getX(), xarpus.getY())}),
    SE_BOX((xarpus) -> new Point[]{new Point(xarpus.getX(), xarpus.getY() - 8), new Point(xarpus.getX(), xarpus.getY()), new Point(xarpus.getX() + 8, xarpus.getY()), new Point(xarpus.getX() + 8, xarpus.getY() - 8)}),
    SW_BOX((xarpus) -> new Point[]{new Point(xarpus.getX() - 8, xarpus.getY() - 8), new Point(xarpus.getX() - 8, xarpus.getY()), new Point(xarpus.getX(), xarpus.getY()), new Point(xarpus.getX(), xarpus.getY() - 8)}),
    NE_MELEE((xarpus) -> new Point[]{new Point(xarpus.getX() + 4, xarpus.getY() + 4), new Point(xarpus.getX(), xarpus.getY() + 4), new Point(xarpus.getX(), xarpus.getY() + 3), new Point(xarpus.getX() + 3, xarpus.getY() + 3), new Point(xarpus.getX() + 3, xarpus.getY()), new Point(xarpus.getX() + 4, xarpus.getY())}),
    NW_MELEE((xarpus) -> new Point[]{new Point(xarpus.getX() - 4, xarpus.getY() + 4), new Point(xarpus.getX() - 4, xarpus.getY()), new Point(xarpus.getX() - 3, xarpus.getY()), new Point(xarpus.getX() - 3, xarpus.getY() + 3), new Point(xarpus.getX(), xarpus.getY() + 3), new Point(xarpus.getX(), xarpus.getY() + 4)}),
    SE_MELEE((xarpus) -> new Point[]{new Point(xarpus.getX() + 4, xarpus.getY() - 4), new Point(xarpus.getX() + 4, xarpus.getY()), new Point(xarpus.getX() + 3, xarpus.getY()), new Point(xarpus.getX() + 3, xarpus.getY() - 3), new Point(xarpus.getX(), xarpus.getY() - 3), new Point(xarpus.getX(), xarpus.getY() - 4)}),
    SW_MELEE((xarpus) -> new Point[]{new Point(xarpus.getX() - 4, xarpus.getY() - 4), new Point(xarpus.getX(), xarpus.getY() - 4), new Point(xarpus.getX(), xarpus.getY() - 3), new Point(xarpus.getX() - 3, xarpus.getY() - 3), new Point(xarpus.getX() - 3, xarpus.getY()), new Point(xarpus.getX() - 4, xarpus.getY())});

    private final Function<WorldPoint, Point[]> func;

    LineOfSight(Function<WorldPoint, Point[]> func) {
        this.func = func;
    }

    public Function<WorldPoint, Point[]> getFunc() {
        return this.func;
    }
}