package net.runelite.client.plugins.toa.Warden;

import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Prayer.NextAttack;
import net.runelite.client.plugins.toa.Prayer.PrayerBoxOverlay;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

import javax.inject.Inject;
import java.util.Queue;
import net.runelite.client.plugins.toa.Warden.Warden;

public class WardenPrayerBoxOverlay extends PrayerBoxOverlay
{
    private final Warden plugin;

    @Inject
    protected WardenPrayerBoxOverlay(Client client, ToaConfig config, Warden plugin, SpriteManager spriteManager)
    {
        super(client, config, spriteManager);
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
