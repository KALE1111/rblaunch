package net.runelite.client.plugins.toa;


import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public abstract class Room
{
    protected final ToaPlugin plugin;
    protected final ToaConfig config;

    @Inject
    protected OverlayManager overlayManager;

    @Inject
    private Client client;

    @Inject
    protected Room(ToaPlugin plugin, ToaConfig config)
    {
        this.plugin = plugin;
        this.config = config;
    }

    public void setpackMeth(reflectMeth type){

	}

    public void init()
    {
    }

    public void load()
    {
    }

    public void unload()
    {
    }

    public boolean inRoomRegion(Integer roomRegionId)
    {
        return ArrayUtils.contains(client.getMapRegions(), roomRegionId);
    }
}
