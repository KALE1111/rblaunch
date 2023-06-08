package net.runelite.client.plugins.spoontob.rooms.Xarpus;

import net.runelite.api.Client;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class XarpusCounterPanel extends OverlayPanel {
    private final Client client;
    private final SpoonTobPlugin plugin;
    private final SpoonTobConfig config;
    private Xarpus xarpus;

    @Inject
    public XarpusCounterPanel(Client client, SpoonTobPlugin plugin, SpoonTobConfig config, Xarpus xarpus) {
        super(plugin);
        this.client = client;
        this.xarpus = xarpus;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        if (xarpus.isXarpusActive() && xarpus.isExhumedSpawned() && xarpus.getExhumedCounter() != null && Xarpus.P1_IDS.contains(xarpus.getXarpusNPC().getId())
                && config.xarpusExhumedInfo()) {
            if (config.fontStyle()) {
                graphics.setFont(new Font("SansSerif", 0, 11));
            }
            String exhumeds = Integer.toString(xarpus.getExhumedCounter().getCount());
            String healed = Integer.toString(xarpus.healCount);

            panelComponent.getChildren().clear();
            String overlayTitle = "Exhume Counter";
            panelComponent.getChildren().add(TitleComponent.builder().text(overlayTitle).color(Color.GREEN).build());
            panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(overlayTitle) + 30, 0));
            panelComponent.getChildren().add(LineComponent.builder().left("Exhumes: ").right(exhumeds).build());
            panelComponent.getChildren().add(LineComponent.builder().left("Healed: ").right(healed).build());
            return super.render(graphics);
        } else {
            return null;
        }
    }
}