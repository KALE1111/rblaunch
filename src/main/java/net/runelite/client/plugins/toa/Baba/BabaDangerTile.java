package net.runelite.client.plugins.toa.Baba;

import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.World;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class BabaDangerTile {

    @Getter
    private int ticksLeft;

    @Inject
	private Client client;

    @Getter
	private LocalPoint lpoint;

    public BabaDangerTile(LocalPoint point, int ticksLeft){
        this.ticksLeft = ticksLeft;
        this.lpoint = point;
    }
}
