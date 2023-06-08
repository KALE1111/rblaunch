package net.runelite.client.plugins.toa.Warden;

import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Prayer.NextAttack;
import net.runelite.client.plugins.toa.Prayer.PrayerOverlay;
import net.runelite.api.Client;

import javax.inject.Inject;
import java.util.Queue;

public class WardenPrayerOverlay extends PrayerOverlay
{
    private final Warden plugin;

    @Inject
    protected WardenPrayerOverlay(Client client, ToaConfig config, Warden plugin)
    {
        super(client, config);
        this.plugin = plugin;
    }

    @Override
    protected Queue<NextAttack> getAttackQueue()
    {
        return plugin.getNextAttackQueue();
    }

    @Override
    protected long getLastTick()
    {
        return plugin.getLastTick();
    }

    @Override
    protected boolean isEnabled()
    {
        return getConfig().zebakPrayerHelper();
    }
}
