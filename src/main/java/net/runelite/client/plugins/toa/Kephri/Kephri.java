package net.runelite.client.plugins.toa.Kephri;

import java.util.LinkedHashSet;
import net.runelite.api.GraphicsObject;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Het.HetOverlay;
import net.runelite.client.plugins.toa.Prayer.NextAttack;
import net.runelite.client.plugins.toa.Room;
import net.runelite.client.plugins.toa.ToaPlugin;
import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Renderable;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.Hooks;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import net.runelite.client.plugins.toa.Warden.WardenDangerTile;
import net.runelite.client.plugins.toa.reflectMeth;

@Slf4j
public class Kephri extends Room {
    @Inject
    private Client client;

    @Inject
    private KephriOverlay kephriOverlay;

    @Inject
    private Hooks hooks;

    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    @Getter
    private boolean kephriActive;

    @Getter
    private int flyTicks;

    @Getter
    private boolean flyActive;

    public static final int SWARM_ID = 11723;

	@Getter
	private LinkedHashSet<KephriDangerTile> KephriDangerTiles = new LinkedHashSet<>();


    @Inject
    protected Kephri(ToaPlugin plugin, ToaConfig config)
	{
		super(plugin, config);
	}

    @Override
    public void init(){
        kephriActive = false;
        flyActive = false;
        flyTicks = 0;
    }

    @Getter(AccessLevel.PACKAGE)
    private final List<GameObject> objects = new ArrayList<>();

    @Override
    public void load()
    {
        overlayManager.add(kephriOverlay);
        hooks.registerRenderableDrawListener(drawListener);
    }

    @Override
    public void unload()
    {
        overlayManager.remove(kephriOverlay);
        hooks.unregisterRenderableDrawListener(drawListener);
		KephriDangerTiles.clear();
    }

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event){
		GraphicsObject object = event.getGraphicsObject();
		if (!inRoomRegion(ToaPlugin.KEPHRI_REGION))
		{
			return;
		}
		switch (object.getId()){
			case 1447:
				KephriDangerTiles.add(new KephriDangerTile(object.getLocation(), 4));
				break;
			case 1446:
				KephriDangerTiles.add(new KephriDangerTile(object.getLocation(), 3));
				break;
			case 2111:
				KephriDangerTiles.add(new KephriDangerTile(object.getLocation(), 2));
				break;
		}
	}

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inRoomRegion(ToaPlugin.KEPHRI_REGION)){
            kephriActive = false;
            return;
        }

		LinkedHashSet<KephriDangerTile> tempTiles = new LinkedHashSet<>();
		for (KephriDangerTile tile : KephriDangerTiles){
			if ((tile.getTicksLeft()-1)>=0){
				tempTiles.add(new KephriDangerTile(tile.getLpoint(), tile.getTicksLeft()-1));
			}
		}

		KephriDangerTiles = tempTiles;
		//log.info(String.valueOf(KephriDangerTiles.size()));


        kephriActive = true;

        if (flyTicks > 0)
            flyTicks--;
        else
            flyActive = false;

        if (client.getLocalPlayer().getGraphic() == 2146 && !flyActive){
            flyActive = true;
            flyTicks = 4;
        }
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI)
    {
        if (renderable instanceof NPC)
        {
            NPC npc = (NPC) renderable;
            if (npc.getId() == SWARM_ID && config.hideUnattackableSwams()){
                int xNpc = npc.getWorldLocation().getRegionX();
                int yNpc = npc.getWorldLocation().getRegionY();
                if ((xNpc >= 28 && xNpc <= 34) && (yNpc >= 29 && yNpc <= 35)) {
                    return false;
                }
            }
        }
        return true;
    }
}
