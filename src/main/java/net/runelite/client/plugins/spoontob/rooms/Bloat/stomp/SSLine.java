package net.runelite.client.plugins.spoontob.rooms.Bloat.stomp;

import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public final class SSLine {
    private final Coordinates coordA;

    private final Coordinates coordB;

    private final int sigX;

    private final int sigY;

    public Coordinates getCoordA() {
        return this.coordA;
    }

    public Coordinates getCoordB() {
        return this.coordB;
    }

    public SSLine(Coordinates coordA, Coordinates coordB, int sigX, int sigY) {
        this.coordA = coordA;
        this.coordB = coordB;
        this.sigX = Integer.signum(sigX);
        this.sigY = Integer.signum(sigY);
    }

    @Nullable
    public Point getTranslatedPointA(@Nonnull Client client) {
        return translate(client, this.coordA, this.sigX, this.sigY);
    }

    @Nullable
    public Point getTranslatedPointB(@Nonnull Client client) {
        return translate(client, this.coordB, this.sigX, this.sigY);
    }

    public SSLine offset(UnaryOperator<Coordinates> offsetFunc) {
        return (offsetFunc == null) ? this : offset(offsetFunc, offsetFunc);
    }

    public SSLine offset(UnaryOperator<Coordinates> offsetFuncA, UnaryOperator<Coordinates> offsetFuncB) {
        return new SSLine(
                (offsetFuncA == null) ? this.coordA : offsetFuncA.apply(this.coordA),
                (offsetFuncB == null) ? this.coordB : offsetFuncB.apply(this.coordB), this.sigX, this.sigY);
    }

    @Nullable
    private static Point translate(@Nonnull Client client, @Nonnull Coordinates coords, int sigX, int sigY) {
        Player player = client.getLocalPlayer();
        if (player == null)
            return null;
        int regionID = player.getWorldLocation().getRegionID();
        int plane = client.getPlane();
        LocalPoint local = LocalPoint.fromWorld(client, WorldPoint.fromRegion(regionID, coords.getX(), coords.getY(), plane));
        if (local == null)
            return null;
        int x = local.getX() + getTileOffset(sigX);
        int y = local.getY() + getTileOffset(sigY);
        return Perspective.localToCanvas(client, x, y, client.getTileHeights()[plane][x >> 7][y >> 7]);
    }

    private static int getTileOffset(int sig) {
        if (sig == 0)
            return 0;
        int off = 64;
        return (sig > 0) ? 64 : -64;
    }

    public boolean equals(Object other) {
        if (!(other instanceof SSLine))
            return false;
        return (this.coordA.equals(((SSLine)other).coordA) && this.coordB
                .equals(((SSLine)other).coordB));
    }

    public int hashCode() {
        return Objects.hash(this.coordA, this.coordB, this.sigX, this.sigY);
    }
}