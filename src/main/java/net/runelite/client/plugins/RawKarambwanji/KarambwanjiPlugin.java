package net.runelite.client.plugins.RawKarambwanji;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.NPCInteraction;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=#86C43F>[RB]</font> Karambwanji</html>",
        enabledByDefault = false
)
public class KarambwanjiPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    OverlayManager overlayManager;
    @Inject
    KarambwanjiOverlay overlay;
    @Inject
    KarambwanjiConfig config;
    private int timeout = 0;
    private Instant startTime;

    public Instant getStartTime() {
        return startTime;
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startTime = null;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (Inventory.search().withId(ItemID.SMALL_FISHING_NET).first().isPresent() && !client.getLocalPlayer().isInteracting())
        {
            NPCs.search().withName("Fishing spot").first().ifPresent(npc -> {
                NPCInteraction.interact(npc, "Net");
            });
        }
        else {
            if (!Inventory.search().withId(ItemID.SMALL_FISHING_NET).first().isPresent() || NPCs.search().withName("Fishing spot").first().isEmpty()) {
                EthanApiPlugin.stopPlugin(this);
            }
        }
    }

    @Provides
    KarambwanjiConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(KarambwanjiConfig.class);
    }
}
