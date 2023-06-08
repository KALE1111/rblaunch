package net.runelite.client.plugins.spoontob.rooms.Bloat.stomp;

import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def.BloatChunk;
import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def.BloatPath;
import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def.BloatRotation;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.runelite.api.Client;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public final class BloatDown {
    private static final Logger log = LoggerFactory.getLogger(BloatDown.class);
    private final WorldPoint destination;
    private final BloatSafespot bloatSafespot;
    private static final Table<BloatChunk, Direction, Pair<BloatPath, BloatRotation>> chunkTable = HashBasedTable.create();

    public BloatDown(@Nonnull Client client, @Nonnull WorldPoint sw, @Nonnull Direction dir, @Nonnull BloatChunk chunk) {
        this.destination = getFinalDestination(client, sw, () -> {
            int[] offsets = new int[2];
            switch(dir) {
                case NORTH:
                    offsets[1] = 1;
                    break;
                case EAST:
                    offsets[0] = 1;
                    break;
                case SOUTH:
                    offsets[1] = -1;
                    break;
                case WEST:
                    offsets[0] = -1;
            }

            return offsets;
        });
        this.bloatSafespot = new BloatSafespot(chunkTable.get(chunk, dir), () -> {
            int off = dir != Direction.NORTH && dir != Direction.EAST ? 0 : 4;
            return Math.max(sw.distanceTo2D(this.destination) - off, 0);
        });
    }

    private static WorldPoint getFinalDestination(Client client, WorldPoint start, Supplier<int[]> offsetsSupplier) {
        int[] offsets = offsetsSupplier.get();
        WorldPoint wl = start;

        for(WorldArea wa = new WorldArea(start, 1, 1); wa.canTravelInDirection(client, offsets[0], offsets[1]); wa = new WorldArea(wl, 1, 1)) {
            wl = wa.toWorldPoint().dx(offsets[0]).dy(offsets[1]);
        }

        return wl;
    }

    public WorldPoint getDestination() {
        return this.destination;
    }

    public BloatSafespot getBloatSafespot() {
        return this.bloatSafespot;
    }

    static {
        log.info("Populating the Chunk HashTable...");
        chunkTable.put(BloatChunk.NW, Direction.NORTH, new ImmutablePair<>(BloatPath.W_PATH, BloatRotation.CLOCKWISE));
        chunkTable.put(BloatChunk.NW, Direction.EAST, new ImmutablePair<>(BloatPath.N_PATH, BloatRotation.CLOCKWISE));
        chunkTable.put(BloatChunk.NW, Direction.SOUTH, new ImmutablePair<>(BloatPath.W_PATH, BloatRotation.COUNTER_CLOCKWISE));
        chunkTable.put(BloatChunk.NW, Direction.WEST, new ImmutablePair<>(BloatPath.N_PATH, BloatRotation.COUNTER_CLOCKWISE));
        chunkTable.put(BloatChunk.NE, Direction.NORTH, new ImmutablePair<>(BloatPath.E_PATH, BloatRotation.COUNTER_CLOCKWISE));
        chunkTable.put(BloatChunk.NE, Direction.EAST, new ImmutablePair<>(BloatPath.N_PATH, BloatRotation.CLOCKWISE));
        chunkTable.put(BloatChunk.NE, Direction.SOUTH, new ImmutablePair<>(BloatPath.E_PATH, BloatRotation.CLOCKWISE));
        chunkTable.put(BloatChunk.NE, Direction.WEST, new ImmutablePair<>(BloatPath.N_PATH, BloatRotation.COUNTER_CLOCKWISE));
        chunkTable.put(BloatChunk.SW, Direction.NORTH, new ImmutablePair<>(BloatPath.W_PATH, BloatRotation.CLOCKWISE));
        chunkTable.put(BloatChunk.SW, Direction.EAST, new ImmutablePair<>(BloatPath.S_PATH, BloatRotation.COUNTER_CLOCKWISE));
        chunkTable.put(BloatChunk.SW, Direction.SOUTH, new ImmutablePair<>(BloatPath.W_PATH, BloatRotation.COUNTER_CLOCKWISE));
        chunkTable.put(BloatChunk.SW, Direction.WEST, new ImmutablePair<>(BloatPath.S_PATH, BloatRotation.CLOCKWISE));
        chunkTable.put(BloatChunk.SE, Direction.NORTH, new ImmutablePair<>(BloatPath.E_PATH, BloatRotation.COUNTER_CLOCKWISE));
        chunkTable.put(BloatChunk.SE, Direction.EAST, new ImmutablePair<>(BloatPath.S_PATH, BloatRotation.COUNTER_CLOCKWISE));
        chunkTable.put(BloatChunk.SE, Direction.SOUTH, new ImmutablePair<>(BloatPath.E_PATH, BloatRotation.CLOCKWISE));
        chunkTable.put(BloatChunk.SE, Direction.WEST, new ImmutablePair<>(BloatPath.S_PATH, BloatRotation.CLOCKWISE));
        chunkTable.put(BloatChunk.UNKNOWN, Direction.NORTH, new ImmutablePair<>(BloatPath.UNKNOWN, BloatRotation.UNKNOWN));
        chunkTable.put(BloatChunk.UNKNOWN, Direction.EAST, new ImmutablePair<>(BloatPath.UNKNOWN, BloatRotation.UNKNOWN));
        chunkTable.put(BloatChunk.UNKNOWN, Direction.SOUTH, new ImmutablePair<>(BloatPath.UNKNOWN, BloatRotation.UNKNOWN));
        chunkTable.put(BloatChunk.UNKNOWN, Direction.WEST, new ImmutablePair<>(BloatPath.UNKNOWN, BloatRotation.UNKNOWN));
    }
}