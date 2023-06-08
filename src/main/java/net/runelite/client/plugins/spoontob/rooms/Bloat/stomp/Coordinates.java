package net.runelite.client.plugins.spoontob.rooms.Bloat.stomp;

import java.util.Objects;

public final class Coordinates {
    private final int x;
    private final int y;

    public Coordinates dx(int dx) {
        return new Coordinates(this.x + dx, this.y);
    }

    public Coordinates dy(int dy) {
        return new Coordinates(this.x, this.y + dy);
    }

    public int distanceTo(Coordinates other) {
        return (int)Math.hypot((double)(this.x - other.x), (double)(this.y - other.y));
    }

    public boolean equals(Object other) {
        if (!(other instanceof Coordinates)) {
            return false;
        } else {
            return this.x == ((Coordinates)other).x && this.y == ((Coordinates)other).y;
        }
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}