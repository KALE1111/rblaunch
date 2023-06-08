package net.runelite.client.plugins.toa.Scarabas;

import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Kephri.KephriOverlay;
import net.runelite.client.plugins.toa.Room;
import net.runelite.client.plugins.toa.ToaPlugin;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.runelite.client.plugins.toa.reflectMeth;

public class Scarabas extends Room {
    @Inject
    private Client client;

    @Inject
    private ScarabasOverlay scarabasOverlay;

    @Getter
    private boolean scarabasActive;

    @Inject
    protected Scarabas(ToaPlugin plugin, ToaConfig config)
	{
		super(plugin, config);
	}

    @Override
    public void init(){
        scarabasActive = false;
    }

    @Getter(AccessLevel.PACKAGE)
    private final List<GameObject> objects = new ArrayList<>();

    @Override
    public void load()
    {
        overlayManager.add(scarabasOverlay);
    }

    @Override
    public void unload()
    {
        overlayManager.remove(scarabasOverlay);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inRoomRegion(ToaPlugin.SCABARAS_REGION)){
            scarabasActive = false;
            return;
        }
        scarabasActive = true;
    }
}
