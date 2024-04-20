package net.runelite.client.plugins.VolcanicAsh;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;


import javax.inject.Inject;
import java.time.Instant;

import static net.runelite.client.plugins.VolcanicAsh.VolcanicStates.*;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=#86C43F>[RB]</font> Volcanic Ash</html>",
        enabledByDefault = false
)
public class VolcanicAshPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    VolcanicAshOverlay overlay;
    @Inject
    OverlayManager overlayManager;
    private int timeout = 0;
    private Instant startTime;
    public VolcanicStates currentState = STARTING;
    @Inject
    public VolcanicAshConfig config;
    private final int runOrbWidgetId = WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getPackedId();


    @Override
    public void startUp() throws Exception {
        startTime = Instant.now();
        overlayManager.add(overlay);
        System.out.println("Auto Volcanic Ash started!");
    }

    @Override
    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startTime = null;
        System.out.println("Auto Volcanic Ash stopped");
    }

    public VolcanicStates getCurrentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (timeout > 0) {
            timeout--;
            return;
        }

        handleRunEnergy();

        if (Inventory.search().nameContains("pickaxe").first().isPresent() || Equipment.search().nameContains("pickaxe").first().isPresent()) {
            if (!isMining() && !isPlayerAnimating() && !EthanApiPlugin.isMoving()) {
                TileObjects.search().withId(30985).nearestToPlayer().ifPresent(ash -> {
                    TileObjectInteraction.interact(ash, "Mine");
                    currentState = MINING;
                    System.out.println("Mining volcanic ash");
                });
                if (Inventory.full()) {
                    Inventory.search().withId(ItemID.SODA_ASH).result().forEach(soda -> {
                        InventoryInteraction.useItem(soda, "Drop");
                        currentState = DROPPING;
                    });
                }
            }
        }
        else {
            System.out.println("No pickaxe found");
            EthanApiPlugin.sendClientMessage("No pickaxe found. Stopping plugin.");
            EthanApiPlugin.stopPlugin(this);
        }

    }

    @Provides
    public VolcanicAshConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(VolcanicAshConfig.class);
    }

    private void handleRunEnergy() {
        if (client.getVarpValue(173) == 0 && client.getEnergy() >= 1000) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, runOrbWidgetId, -1, -1);
        }
    }

    private boolean isPlayerAnimating() {
        return client.getLocalPlayer().getAnimation() != AnimationID.IDLE;
    }

    private boolean isMining() {
        return client.getLocalPlayer().getAnimation() == 7139;
    }
}

