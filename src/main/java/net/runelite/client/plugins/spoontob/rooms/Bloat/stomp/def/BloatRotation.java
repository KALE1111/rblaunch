package net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def;

public enum BloatRotation {
    CLOCKWISE,
    COUNTER_CLOCKWISE,
    UNKNOWN;

    public boolean isClockwise() {
        return this == CLOCKWISE;
    }
}