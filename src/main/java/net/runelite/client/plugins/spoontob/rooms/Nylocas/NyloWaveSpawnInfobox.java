package net.runelite.client.plugins.spoontob.rooms.Nylocas;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.plugins.spoontob.util.TheatreRegions;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class NyloWaveSpawnInfobox extends OverlayPanel {
    private final Client client;
    private final SpoonTobPlugin plugin;
    private final SpoonTobConfig config;
    private Nylocas nylo;

    @Inject
    public NyloWaveSpawnInfobox(Client client, SpoonTobPlugin plugin, SpoonTobConfig config, Nylocas nylo) {
        super(plugin);
        this.client = client;
        this.nylo = nylo;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Theatre xarpus overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if((config.waveSpawnTimer() == SpoonTobConfig.waveSpawnTimerMode.INFOBOX || config.waveSpawnTimer() == SpoonTobConfig.waveSpawnTimerMode.BOTH)
                && TheatreRegions.inRegion(client, TheatreRegions.NYLOCAS) && nylo.isNyloActive() && nylo.nyloWave < 31 && nylo.waveSpawnTicks > -1) {
            panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("Next Wave:   ") + 20, 0));

            if(nylo.stalledWave) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Next Wave: ")
                        .rightColor(Color.RED)
                        .right(String.valueOf(nylo.waveSpawnTicks))
                        .build());
            } else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Next Wave: ")
                        .right(String.valueOf(nylo.waveSpawnTicks))
                        .build());
            }
        }
        return super.render(graphics);
    }
}