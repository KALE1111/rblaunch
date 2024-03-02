package net.runelite.client.plugins.TitheFarm;

import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

public class TitheOverlay extends OverlayPanel {

    private final TithePlugin plugin;
    private final TitheConfig config;
    String timeFormat;

    @Inject
    private TitheOverlay(final TithePlugin plugin, final TitheConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Tithe farmer overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.timer == null || !config.enableUI()) {
            return null;
        }

        panelComponent.setPreferredSize(new Dimension(200, 100));

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("RB Tithe Farmer")
                .color(ColorUtil.fromHex("#40C4FF"))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Time:")
                .right(formatSession(plugin.timer))
                .build());


        panelComponent.getChildren().add(LineComponent.builder()
                .left("Runs:")
                .right(plugin.runs + " (p/H)")
                .build());


        return super.render(graphics);
    }

    public static String formatSession(Instant timer) {
        Duration duration = Duration.between(timer, Instant.now());
        String timeFormat = (duration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
        String formatted = formatDuration(duration.toMillis(), timeFormat);
        return formatted;
    }
}
