package net.runelite.client.plugins.spoontob.rooms.Verzik;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
@Slf4j
public class YellowGrouperOverlay extends RoomOverlay {
    @Inject
    private Client client;
    @Inject
    private Verzik verzik;
    @Inject
    private SpoonTobConfig config;

    @Inject
    protected YellowGrouperOverlay(SpoonTobConfig config) {
        super(config);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(verzik.yellowGroups.size() > 0 && config.showVerzikYellows() == SpoonTobConfig.verzikYellowsMode.GROUPS && verzik.yellowsOut && verzik.getVerzikNPC().getId() == 10852) {
            int group = 0;
            String text = String.valueOf(verzik.yellowTimer);

            if(config.yellowTicksOnPlayer() && client.getLocalPlayer() != null) {
                Point point = Perspective.getCanvasTextLocation(client, graphics, client.getLocalPlayer().getLocalLocation(), "#", config.yellowsOffset());
                if (config.fontStyle()) {
                    renderTextLocation(graphics, text, Color.WHITE, point);
                } else {
                    renderSteroidsTextLocation(graphics, text, config.yellowsSize(), Font.BOLD, Color.WHITE, point);
                }
            }

            for (ArrayList<WorldPoint> list : verzik.yellowGroups) {
                for (WorldPoint next : list) {
                    final LocalPoint localPoint = LocalPoint.fromWorld(client, next);
                    if (localPoint != null) {
                        Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                        if (poly == null)
                            continue;

                        Color color = Color.BLACK;
                        graphics.setColor(color);

                        Stroke originalStroke = graphics.getStroke();
                        graphics.setStroke(new BasicStroke(2));
                        graphics.draw(poly);
                        Color fill;
                        switch (group) {
                            case 0:
                                fill = Color.RED;
                                break;
                            case 1:
                                fill = Color.BLUE;
                                break;
                            case 2:
                                fill = Color.GREEN;
                                break;
                            case 3:
                                fill = Color.MAGENTA;
                                break;
                            case 4:
                                fill = Color.ORANGE;
                                break;
                            default:
                                fill = new Color(250, 50, 100);
                        }
                        Color realFill = new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 130);
                        graphics.setColor(realFill);
                        graphics.fill(poly);
                        graphics.setStroke(originalStroke);

                        if (!config.yellowTicksOnPlayer()) {
                            Point point = Perspective.getCanvasTextLocation(client, graphics, localPoint, text, 0);
                            if (config.fontStyle()) {
                                renderTextLocation(graphics, text, Color.WHITE, point);
                            } else {
                                renderResizeTextLocation(graphics, text, 12, Font.BOLD, Color.WHITE, point);
                            }
                        }
                    }
                }
                group++;
            }
        }
        return null;
    }
}