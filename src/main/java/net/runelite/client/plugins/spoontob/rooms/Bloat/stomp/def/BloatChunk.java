package net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def;

import java.util.EnumSet;

public enum BloatChunk {
    NW("Northwest", 411, 556),
    NE("Northeast", 412, 556),
    SW("Southwest", 411, 555),
    SE("Southeast", 412, 555),
    UNKNOWN("Unknown", 0, 0);

    private final String name;
    private final int zone;

    BloatChunk(String name, int x, int y) {
        this.name = name;
        this.zone = (x & 1023) << 14 | (y & 1023) << 3;
    }

    public static BloatChunk getOccupiedChunk(int chunk) {
        return chunk == -1 ? UNKNOWN : EnumSet.allOf(BloatChunk.class).stream().filter((c) -> c.zone == chunk).findFirst().orElse(UNKNOWN);
    }

    public String toString() {
        return this.name;
    }
}