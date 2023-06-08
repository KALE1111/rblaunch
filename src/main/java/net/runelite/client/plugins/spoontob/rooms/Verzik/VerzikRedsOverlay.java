package net.runelite.client.plugins.spoontob.rooms.Verzik;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.plugins.spoontob.util.TheatreRegions;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class VerzikRedsOverlay extends RoomOverlay {
    @Inject
    private SpoonTobPlugin plugin;

    @Inject
    private SpoonTobConfig config;

    @Inject
    private Client client;

    @Inject
    private Verzik verzik;

    @Inject
    public VerzikRedsOverlay(Client client, SpoonTobConfig config, SpoonTobPlugin plugin) {
        super(config);
        this.client = client;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if ((config.redsTL() != SpoonTobConfig.redsTlMode.OFF || config.redsFreezeWarning()) && plugin.enforceRegion()) {
            for (NPC reds : client.getNpcs()) {
                if (reds.getName() != null && reds.getName().equalsIgnoreCase("nylocas matomenos")) {
                    NPCComposition composition = reds.getComposition();
                    int size = composition.getSize();
                    LocalPoint lp = LocalPoint.fromWorld(client, reds.getWorldLocation());
                    if (lp != null) {
                        lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
                        if (tilePoly != null) {
                            if ((config.redsTL() == SpoonTobConfig.redsTlMode.VERZIK || config.redsTL() == SpoonTobConfig.redsTlMode.BOTH)
                                    && TheatreRegions.inRegion(client, TheatreRegions.VERZIK)) {
                                renderPoly(graphics, tilePoly, config.redsTLColor(), config.redsTLColor().getAlpha(), 0);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void renderPoly(Graphics2D graphics, Shape polygon, Color color, int outlineOpacity, int fillOpacity) {
        if (polygon != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineOpacity));
            graphics.setStroke(new BasicStroke((float) 1));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillOpacity));
            graphics.fill(polygon);
        }
    }
}