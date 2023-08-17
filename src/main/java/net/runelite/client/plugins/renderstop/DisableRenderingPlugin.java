package net.runelite.client.plugins.renderstop;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;


import javax.inject.Inject;

//Credit To Illume
@PluginDescriptor(
        name = "<html><font color=#86C43F>[RB]</font> Disable Rendering</html>",
        enabledByDefault = false,
        description = "Disable rendering to improve performance",
        tags = {"RuneBot", "performance", "disable", "rendering", "freeze"}
)
@Slf4j
public class DisableRenderingPlugin extends Plugin {

    @Inject
    private Client client;

    private DrawCallbacks originalDrawCallbacks;

    @Override
    protected void startUp() {
        if (client != null) {
            originalDrawCallbacks = client.getDrawCallbacks();
            client.setDrawCallbacks(new DisableRenderCallbacks());
        }
    }

    @Override
    protected void shutDown() {
        client.setDrawCallbacks(originalDrawCallbacks);
        originalDrawCallbacks = null;
    }
}