package net.runelite.client.plugins.toa.Warden;

import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.World;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class WardenDangerTile {

	@Getter
	private int ticksLeft;

	@Getter
	private int id;

	@Inject
	private Client client;

	@Getter
	private LocalPoint lpoint;

	public WardenDangerTile(LocalPoint point, int ticksLeftm, int ID){
		this.ticksLeft = ticksLeftm;
		this.lpoint = point;
		this.id = ID;
	}
}
