package net.runelite.client.plugins.spoontob.rooms.Bloat.stomp;

import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def.BloatPath;
import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def.BloatRotation;
import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def.DistanceInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class BloatSafespot {
    public BloatSafespot(Pair<BloatPath, BloatRotation> pair, Supplier<Integer> distance) {
        this.bloatPath = pair.getLeft();
        this.clockRotation = pair.getRight();
        this.distanceInfo = new DistanceInfo(distance.get());
    }

    public List<SSLine> getSafespotLines() {
        return this.distanceInfo.isCorner() ? getCornerSafespots() : getSideSafespots();
    }

    public List<SSLine> getCornerSafespots() {
        if (this.bloatPath == BloatPath.UNKNOWN)
            return Collections.emptyList();
        Pair<SSLine[], int[]> lop = this.bloatPath.getCornerSafespots(grid).get(this.distanceInfo.getCornerIndex(this.clockRotation.isClockwise()));
        SSLine[] safespotLines = lop.getLeft();
        if (!this.distanceInfo.shouldModifyCorner(this.bloatPath))
            return Arrays.asList(safespotLines);
        byte bit = (byte)((this.bloatPath == BloatPath.N_PATH || this.bloatPath == BloatPath.S_PATH) ? 1 : 0);
        boolean isCol = (bit == 0);
        int[] offsets = lop.getRight();
        return Arrays.asList(safespotLines[bit]
                .offset(c -> c.dx(isCol ? offsets[0] : offsets[2]).dy(isCol ? offsets[1] : offsets[3])), safespotLines[(3 + bit) % 2]);
    }

    public List<SSLine> getSideSafespots() {
        if (this.bloatPath == BloatPath.UNKNOWN)
            return Collections.emptyList();
        SSLine[] safespotLines = this.bloatPath.getSideSafespotLines(grid);
        if (!this.clockRotation.isClockwise())
            ArrayUtils.reverse(safespotLines);
        List<Integer> offsets = this.bloatPath.getSideOffsets(this.distanceInfo.isSideMin());
        return Arrays.asList(safespotLines[0]
                .offset(c -> c.dx(this.bloatPath.shouldOffsetX() ? offsets.get(0) : 0).dy(this.bloatPath.shouldOffsetY() ? offsets.get(0) : 0)), safespotLines[1]
                .offset(c -> c.dx(this.bloatPath.shouldOffsetX() ? offsets.get(1) : 0).dy(this.bloatPath.shouldOffsetY() ? offsets.get(1) : 0)));
    }

    public static final SSLine[][][] grid = new SSLine[][][] { { { new SSLine(new Coordinates(29, 39), new Coordinates(29, 34), -1, 1), new SSLine(new Coordinates(29, 29), new Coordinates(29, 24), -1, -1) }, { new SSLine(new Coordinates(34, 39), new Coordinates(34, 34), 1, 1), new SSLine(new Coordinates(34, 29), new Coordinates(34, 24), 1, -1) } }, { { new SSLine(new Coordinates(24, 34), new Coordinates(29, 34), -1, 1), new SSLine(new Coordinates(34, 34), new Coordinates(39, 34), 1, 1) }, { new SSLine(new Coordinates(28, 29), new Coordinates(23, 29), 1, -1), new SSLine(new Coordinates(34, 29), new Coordinates(39, 29), 1, -1) } } };

    public final BloatPath bloatPath;

    public final BloatRotation clockRotation;

    public final DistanceInfo distanceInfo;
}