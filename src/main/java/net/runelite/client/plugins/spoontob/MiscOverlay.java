package net.runelite.client.plugins.spoontob;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class MiscOverlay extends Overlay {
    private final Client client;
    private final SpoonTobPlugin plugin;
    private final SpoonTobConfig config;

    @Inject
    private MiscOverlay(final Client client, final SpoonTobPlugin plugin, final SpoonTobConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.lootReminder() != SpoonTobConfig.lootReminderMode.OFF && plugin.bankLootChest != null && this.client.getLocalPlayer() != null) {
            Color raveColor = plugin.raveUtils.getColor(plugin.bankLootChest.hashCode(), true);
            if(plugin.bankLootChest.getRenderable().getModel().getModelHeight() == 119) {
                if(config.lootReminder() == SpoonTobConfig.lootReminderMode.DUMBER || config.lootReminder() == SpoonTobConfig.lootReminderMode.DUMBEST
                        || config.lootReminder() == SpoonTobConfig.lootReminderMode.DUMBEREST) {
                    if (!this.client.hasHintArrow()) {
                        this.client.setHintArrow(plugin.bankLootChest.getWorldLocation());
                    }

                    if (config.lootReminder() == SpoonTobConfig.lootReminderMode.DUMBEST) {
                        graphics.setColor(new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), config.lootReminderColor().getAlpha()));
                    } else if (config.lootReminder() == SpoonTobConfig.lootReminderMode.DUMBEREST){
                        graphics.setColor(new Color(plugin.raveBankChestColor.getRed(), plugin.raveBankChestColor.getBlue(), plugin.raveBankChestColor.getGreen(), config.lootReminderColor().getAlpha()));
                    } else {
                        graphics.setColor(config.lootReminderColor());
                    }

                    if(plugin.bankLootChest.getConvexHull() != null) {
                        graphics.fill(plugin.bankLootChest.getConvexHull());
                    }
                }else if(config.lootReminder() == SpoonTobConfig.lootReminderMode.DUMB){
                    graphics.setColor(config.lootReminderColor());
                    if(plugin.bankLootChest.getConvexHull() != null) {
                        graphics.fill(plugin.bankLootChest.getConvexHull());
                    }
                }
            }else {
                plugin.bankLootChest = null;
                this.client.clearHintArrow();
            }
        }
        return null;
    }
}