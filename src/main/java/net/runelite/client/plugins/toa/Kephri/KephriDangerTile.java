package net.runelite.client.plugins.toa.Kephri;

import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.World;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class KephriDangerTile {

    @Getter
    private int ticksLeft;

	@Getter
	private int id;


	@Inject
	private Client client;

    @Getter
	private LocalPoint lpoint;

    public KephriDangerTile(LocalPoint point, int ticksLeft){
        this.ticksLeft = ticksLeft;
        this.lpoint = point;
    }
}
