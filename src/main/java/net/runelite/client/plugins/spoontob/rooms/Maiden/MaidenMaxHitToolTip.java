package net.runelite.client.plugins.spoontob.rooms.Maiden;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

public class MaidenMaxHitToolTip extends Overlay {
    @Inject
    private  Client client;
    @Inject
    private  TooltipManager tooltipManager;

    @Inject
    private MaidenMaxHitToolTip(Client client, TooltipManager tooltipManager, Maiden maiden, SpoonTobConfig config) {
        this.client = client;
        this.tooltipManager = tooltipManager;
        this.maiden = maiden;
        this.config = config;
    }
    @Inject
    private  Maiden maiden;
    @Inject
    private  SpoonTobConfig config;

    public Dimension render(Graphics2D graphics) {
        if (config.maidenMaxHit() != null && !client.isMenuOpen() && maiden.isMaidenActive()) {
            NPC maidenNpc = maiden.getMaidenNPC();
            Model model = maidenNpc.getModel();
            LocalPoint localPoint = maidenNpc.getLocalLocation();
            if (model != null && localPoint != null) {
                Shape clickbox = Perspective.getClickbox(client, model, maidenNpc.getOrientation(), localPoint.getX(), localPoint.getY(), client.getPlane());
                if (clickbox != null &&
                        clickbox.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())) {
                    int noPrayerMaxHit = (int) Math.floor(maiden.getMaxHit());
                    int prayerMaxHit = noPrayerMaxHit / 2;
                    int elyMaxHit = prayerMaxHit - (int) Math.floor(prayerMaxHit * 0.25D);
                    tooltipManager.add(new Tooltip(ColorUtil.wrapWithColorTag("No Prayer:", new Color(255, 109, 97)) +
                            ColorUtil.wrapWithColorTag(" +" + noPrayerMaxHit, new Color(-7278960)) + "</br>" +
                            ColorUtil.wrapWithColorTag("Prayer:", Color.ORANGE) + ColorUtil.wrapWithColorTag(" +" + prayerMaxHit, new Color(-7278960)) + "</br>" +
                            ColorUtil.wrapWithColorTag("Elysian:", Color.CYAN) + ColorUtil.wrapWithColorTag(" +" + elyMaxHit, new Color(-7278960))));
                }
            }
        }


        return null;
    }
}


