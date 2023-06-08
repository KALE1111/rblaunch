package net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def;

import java.util.Objects;

public final class DistanceInfo {
    private static final int MAX = 7;
    private static final int MIN = 4;
    private final int distance;

    public boolean isCorner() {
        return this.distance >= 7 || this.distance <= 4;
    }

    public boolean shouldModifyCorner(BloatPath path) {
        boolean mod = (path == BloatPath.N_PATH || path == BloatPath.S_PATH) && (this.distance == 8 || this.distance == 3);
        return mod || this.distance == 7 || this.distance == 4;
    }

    public byte getCornerIndex(boolean isClockwise) {
        int cidx = this.distance >= 7 ? 0 : 1;
        if (!isClockwise) {
            cidx ^= 1;
        }

        return (byte)cidx;
    }

    public boolean isSideMin() {
        return this.distance == 6;
    }

    public boolean isSideMax() {
        return this.distance == 5;
    }

    public boolean equals(Object other) {
        if (!(other instanceof DistanceInfo)) {
            return false;
        } else {
            return this.distance == ((DistanceInfo)other).distance;
        }
    }

    public int hashCode() {
        return Objects.hash(this.distance);
    }

    public DistanceInfo(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return this.distance;
    }
}