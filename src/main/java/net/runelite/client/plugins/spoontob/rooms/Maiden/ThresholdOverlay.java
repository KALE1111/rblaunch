package net.runelite.client.plugins.spoontob.rooms.Maiden;

import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class ThresholdOverlay extends OverlayPanel {

    private final Maiden maiden;
    private final SpoonTobConfig config;

    @Inject
    private ThresholdOverlay(Maiden maiden, SpoonTobConfig config) {
        this.maiden = maiden;
        this.config = config;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        panelComponent.setPreferredSize(new Dimension(100, 0));
    }

    public Dimension render(Graphics2D graphics) {
        if (config.maidenProcThreshold() && maiden.getMaidenNPC() != null && maiden.getMaidenNPC().getId() != 8363) {
            if (maiden.getRealMaidenHp() >= maiden.getThresholdHp()) {
                String maidenThresholdStr = Integer.toString(maiden.getRealMaidenHp() - maiden.getThresholdHp());
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .left("DMG Left:")
                        .leftColor(Color.WHITE)
                        .right(maidenThresholdStr)
                        .rightColor(Color.GREEN)
                        .build());
            }

            return super.render(graphics);
        } else {
            return null;
        }
    }
}