package net.runelite.client.plugins.toa;



public interface PluginLifecycleComponent
{

    default boolean isEnabled(ToaConfig config, RaidState raidState)
    {
        return true;
    }

    void startUp();

    void shutDown();

}
