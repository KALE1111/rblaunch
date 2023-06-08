package net.runelite.client.plugins.spoontob.rooms.Maiden;

import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class MaidenMaxHitOverlay extends OverlayPanel {
    private final Maiden maiden;
    private final SpoonTobConfig config;

    @Inject
    private MaidenMaxHitOverlay(Maiden maiden, SpoonTobConfig config) {
        this.maiden = maiden;
        this.config = config;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        panelComponent.setPreferredSize(new Dimension(85, 0));
    }

    public Dimension render(Graphics2D graphics) {
        if (config.maidenMaxHitOverlay() != SpoonTobConfig.MaidenMaxHit.OFF && maiden.getMaidenNPC() != null) {
            int noPrayerMaxHit = (int)Math.floor(maiden.getMaxHit());
            int prayerMaxHit = noPrayerMaxHit / 2;
            int elyMaxHit = prayerMaxHit - (int)Math.floor((double)prayerMaxHit * 0.25D);
            LineComponent reg = LineComponent.builder().left("Max Hit:").leftColor(Color.WHITE).right(Integer.toString(prayerMaxHit)).rightColor(Color.GREEN).build();
            LineComponent ely = LineComponent.builder().left("Ely Max Hit:").leftColor(Color.WHITE).right(Integer.toString(elyMaxHit)).rightColor(Color.GREEN).build();
            switch(config.maidenMaxHitOverlay()) {
                case REGULAR:
                    panelComponent.getChildren().add(reg);
                    break;
                case ELY:
                    panelComponent.getChildren().add(ely);
                    break;
                case BOTH:
                    panelComponent.getChildren().add(reg);
                    panelComponent.getChildren().add(ely);
                    break;
                default:
                    throw new IllegalStateException("Invalid 'maidenMaxHit' config state -> state: " + config.maidenMaxHitOverlay().getName());
            }

            return super.render(graphics);
        } else {
            return null;
        }
    }
}