package net.runelite.client.plugins.spoontob.rooms.Xarpus;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class XarpusTimer extends OverlayPanel {
    private final Client client;
    private final SpoonTobPlugin plugin;
    private final SpoonTobConfig config;
    private Xarpus xarpus;

    @Inject
    public XarpusTimer(Client client, SpoonTobPlugin plugin, SpoonTobConfig config, Xarpus xarpus) {
        super(plugin);
        this.client = client;
        this.xarpus = xarpus;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Theatre xarpus overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (config.entryInstanceTimer() == SpoonTobConfig.instancerTimerMode.OVERLAY && xarpus.isInstanceTimerRunning() && !xarpus.isExhumedSpawned()
                && xarpus.isInXarpusRegion()) {
            panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("Tick:   ") + 10, 0));
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Tick: ")
                    .right(String.valueOf(xarpus.getInstanceTimer()))
                    .build());
        }
        return super.render(graphics);
    }
}