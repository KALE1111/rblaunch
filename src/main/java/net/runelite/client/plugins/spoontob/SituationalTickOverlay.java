package net.runelite.client.plugins.spoontob;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;

public class SituationalTickOverlay extends RoomOverlay {
    @Inject
    private Client client;
    @Inject
    private SpoonTobConfig config;
    @Inject
    private SpoonTobPlugin plugin;

    @Inject
    protected SituationalTickOverlay(SpoonTobConfig config) {
        super(config);
    }

    public Dimension render(Graphics2D graphics) {
        if(plugin.enforceRegion() && config.situationalTicks()) {
            Player p = client.getLocalPlayer();
            if (p != null) {
                if (isInBloatRegion(client)) {
                    Integer tick = plugin.situationalTicksList.get(p);
                    if (tick != null) {
                        Point canvasPoint = client.getLocalPlayer().getCanvasTextLocation(graphics, String.valueOf(tick), config.situationalTicksOffset());
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, String.valueOf(tick), (tick == 1) ? Color.GREEN : Color.WHITE, canvasPoint);
                        } else {
                            renderSteroidsTextLocation(graphics, String.valueOf(tick), config.situationalTicksSize(), Font.BOLD, (tick == 1) ? Color.GREEN : Color.WHITE, canvasPoint);
                        }
                    }
                } else if (isInXarpRegion(client)) {
                    for (Player p2 : plugin.getSituationalTicksList().keySet()) {
                        int tick = plugin.getSituationalTicksList().get(p2);
                        Point canvasPoint = p2.getCanvasTextLocation(graphics, String.valueOf(tick), config.situationalTicksOffset());
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, String.valueOf(tick), (tick == 1) ? Color.GREEN : Color.WHITE, canvasPoint);
                        } else {
                            renderSteroidsTextLocation(graphics, String.valueOf(tick), config.situationalTicksSize(), Font.BOLD, (tick == 1) ? Color.GREEN : Color.WHITE, canvasPoint);
                        }
                    }
                }
            }
        }
        return null;
    }


    private static boolean isInBloatRegion(Client client) {
        return (client.getMapRegions() != null && (client.getMapRegions()).length > 0 && Arrays.stream(client.getMapRegions()).anyMatch(s -> (s == 13125)));
    }

    private static boolean isInXarpRegion(Client client) {
        return (client.getMapRegions() != null && (client.getMapRegions()).length > 0 && Arrays.stream(client.getMapRegions()).anyMatch(s -> (s == 12612)));
    }

    protected void renderTextLocation(Graphics2D graphics, String txtString, Color fontColor, Point canvasPoint) {
        if (canvasPoint != null) {
            Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
            Point canvasCenterPoint_shadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }
}