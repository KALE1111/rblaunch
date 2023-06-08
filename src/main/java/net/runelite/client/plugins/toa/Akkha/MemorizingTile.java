package net.runelite.client.plugins.toa.Akkha;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.World;
import net.runelite.api.coords.WorldPoint;

public class MemorizingTile {

    @Getter
    private int ticksLeft;

    @Getter
    private WorldPoint point;

    public MemorizingTile(WorldPoint point, int ticksLeft){
        this.ticksLeft = ticksLeft;
        this.point = point;
    }
}
