package net.runelite.client.plugins.spoontob.rooms.Nylocas;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class NylocasAliveCounterOverlay extends OverlayPanel {
    private static final String prefix = "Nylocas alive: ";
    private final PanelComponent panelComponent = new PanelComponent();
    private LineComponent waveComponent;
    private SpoonTobPlugin plugin;
    private SpoonTobConfig config;

    @Setter
    private Instant nyloWaveStart;

    @Getter
    private int nyloAlive = 0;

    @Getter
    private int maxNyloAlive = 12;

    @Getter
    private int wave = 0;

    @Setter
    @Getter
    private boolean hidden = false;


    @Inject
    private NylocasAliveCounterOverlay(SpoonTobPlugin plugin, SpoonTobConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.HIGH);
        refreshPanel();
    }

    public void setNyloAlive(int aliveCount) {
        nyloAlive = aliveCount;
        refreshPanel();
    }

    public void setMaxNyloAlive(int maxAliveCount) {
        maxNyloAlive = maxAliveCount;
        refreshPanel();
    }

    public void setWave(int wave) {
        this.wave = wave;
        refreshPanel();
    }

    private void refreshPanel() {
        LineComponent lineComponent = LineComponent.builder()
                .left("Alive: ")
                .right(nyloAlive + "/" + maxNyloAlive)
                .build();
        if (nyloAlive >= maxNyloAlive) {
            lineComponent.setRightColor(Color.ORANGE);
        } else {
            lineComponent.setRightColor(Color.GREEN);
        }

        waveComponent = LineComponent.builder()
                .left("Wave: " + this.wave)
                .build();
        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(waveComponent);
        panelComponent.getChildren().add(lineComponent);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.nyloAlivePanel() && !isHidden()) {
            this.panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("Alive: 24/24") + 10, 0));
            return panelComponent.render(graphics);
        } else {
            return null;
        }
    }

    public String getFormattedTime() {
        Duration duration = Duration.between(this.nyloWaveStart, Instant.now());
        LocalTime localTime = LocalTime.ofSecondOfDay(duration.getSeconds());
        return localTime.format(DateTimeFormatter.ofPattern("mm:ss"));
    }
}