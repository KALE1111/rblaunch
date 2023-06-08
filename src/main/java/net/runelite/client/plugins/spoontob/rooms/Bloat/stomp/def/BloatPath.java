package net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def;

import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.SSLine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public enum BloatPath implements IPath {
    N_PATH {
        public List<Pair<SSLine[], int[]>> getCornerSafespots(SSLine[][][] bloatGrid) {
            return Arrays.asList(new ImmutablePair<>(IPath.getSECornerLines(bloatGrid), SE_OFFSETS), new ImmutablePair<>(IPath.getSWCornerLines(bloatGrid), SW_OFFSETS));
        }

        public SSLine[] getSideSafespotLines(SSLine[][][] bloatGrid) {
            return IPath.getNorthLines(bloatGrid);
        }

        public boolean areOffsetsNegative() {
            return true;
        }

        public boolean shouldOffsetY() {
            return true;
        }
    },
    E_PATH {
        public List<Pair<SSLine[], int[]>> getCornerSafespots(SSLine[][][] bloatGrid) {
            return Arrays.asList(new ImmutablePair<>(IPath.getSWCornerLines(bloatGrid), SW_OFFSETS), new ImmutablePair<>(IPath.getNWCornerLines(bloatGrid), NW_OFFSETS));
        }

        public SSLine[] getSideSafespotLines(SSLine[][][] bloatGrid) {
            return IPath.getEastLines(bloatGrid);
        }

        public boolean areOffsetsNegative() {
            return true;
        }

        public boolean shouldOffsetX() {
            return true;
        }
    },
    S_PATH {
        public List<Pair<SSLine[], int[]>> getCornerSafespots(SSLine[][][] bloatGrid) {
            return Arrays.asList(new ImmutablePair<>(IPath.getNWCornerLines(bloatGrid), NW_OFFSETS), new ImmutablePair<>(IPath.getNECornerLines(bloatGrid), NE_OFFSETS));
        }

        public SSLine[] getSideSafespotLines(SSLine[][][] bloatGrid) {
            return IPath.getSouthLines(bloatGrid);
        }

        public boolean shouldOffsetY() {
            return true;
        }
    },
    W_PATH {
        public List<Pair<SSLine[], int[]>> getCornerSafespots(SSLine[][][] bloatGrid) {
            return Arrays.asList(new ImmutablePair<>(IPath.getNECornerLines(bloatGrid), NE_OFFSETS), new ImmutablePair<>(IPath.getSECornerLines(bloatGrid), SE_OFFSETS));
        }

        public SSLine[] getSideSafespotLines(SSLine[][][] bloatGrid) {
            return IPath.getWestLines(bloatGrid);
        }

        public boolean shouldOffsetX() {
            return true;
        }
    },
    UNKNOWN;

    BloatPath() {
    }
}