package net.runelite.client.plugins.toa.Zebak;

import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Prayer.NextAttack;
import net.runelite.client.plugins.toa.Prayer.PrayerBoxOverlay;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

import javax.inject.Inject;
import java.util.Queue;

public class ZebakPrayerBoxOverlay extends PrayerBoxOverlay
{
    private final Zebak plugin;

    @Inject
    protected ZebakPrayerBoxOverlay(Client client, ToaConfig config, Zebak plugin, SpriteManager spriteManager)
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
