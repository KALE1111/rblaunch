package net.runelite.client.plugins.spoontob.rooms.Bloat;

import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.BloatSafespot;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.SSLine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;

public class BloatOverlay extends RoomOverlay {
    @Inject
    private Bloat bloat;

    @Inject
    private SpoonTobPlugin plugin;

    @Inject
    protected BloatOverlay(SpoonTobConfig config) {
        super(config);
    }

    public Dimension render(Graphics2D graphics) {
        if(bloat.isBloatActive()) {
            if (config.bloatIndicator() != SpoonTobConfig.BloatIndicatorMode.OFF) {
                if (config.bloatIndicator() == SpoonTobConfig.BloatIndicatorMode.TILE) {
                    renderNpcPoly(graphics, bloat.getBloatStateColor(), bloat.getBloatTilePoly(), 3, bloat.getBloatStateColor().getAlpha());
                } else if (config.bloatIndicator() == SpoonTobConfig.BloatIndicatorMode.TRUE_LOCATION) {
                    renderNpcTLOverlay(graphics, bloat.getBloatNPC(), bloat.getBloatStateColor(), 3, bloat.getBloatStateColor().getAlpha(), 0);
                }
            }

            if (config.showBloatHands() != SpoonTobConfig.bloatHandsMode.OFF || config.bloatHandsTicks()) {
                int index = 0;
                Color color = config.bloatHandColor();
                for (WorldPoint point : bloat.getBloathands().keySet()) {
                    if (config.showBloatHands() == SpoonTobConfig.bloatHandsMode.RAVE) {
                        color = plugin.raveUtils.getColor(bloat.getBloathands().hashCode(), true);
                    } else if (config.showBloatHands() == SpoonTobConfig.bloatHandsMode.RAVEST) {
                        color = plugin.raveUtils.getColor(index * 50, false);
                    }
                    drawTile(graphics, point, color, 1, config.bloatHandColor().getAlpha(), config.bloatColorFill());

                    if(config.bloatHandsTicks() && bloat.handsFalling){
                        String text = String.valueOf(bloat.handTicks);
                        LocalPoint lp = LocalPoint.fromWorld(client, point);
                        if(lp != null) {
                            Point p = Perspective.getCanvasTextLocation(client, graphics, lp, text, 0);
                            if (config.fontStyle()) {
                                renderTextLocation(graphics, text, Color.WHITE, p);
                            } else {
                                renderSteroidsTextLocation(graphics, text, 12, Font.BOLD, Color.WHITE, p);
                            }
                        }
                    }
                    index++;
                }
            }

            if (bloat.bloatVar == 1) {
                if (config.bloatUpTimer() && bloat != null) {
                    Point canvasPoint = bloat.getBloatNPC().getCanvasTextLocation(graphics, String.valueOf(bloat.getBloatUpTimer()), 60);
                    if (bloat.getBloatState() != 1 && bloat.getBloatState() != 4) {
                        String str = String.valueOf(33 - bloat.getBloatDownCount());
                        if (bloat.getBloatDownCount() >= 26) {
                            if (config.fontStyle()) {
                                renderTextLocation(graphics, str, Color.RED, canvasPoint);
                            } else {
                                renderResizeTextLocation(graphics, str, 15, Font.BOLD, Color.RED, canvasPoint);
                            }
                        } else {
                            if (config.fontStyle()) {
                                renderTextLocation(graphics, str, Color.WHITE, canvasPoint);
                            } else {
                                renderResizeTextLocation(graphics, str, 15, Font.BOLD, Color.WHITE, canvasPoint);
                            }
                        }
                    } else {
                        Color col = bloat.getBloatUpTimer() > 37 ? Color.RED : Color.WHITE;
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, String.valueOf(bloat.getBloatUpTimer()), col, canvasPoint);
                        } else {
                            //int secondConversion = (int)((double)bloat.getBloatUpTimer() * 0.6D);
                            //renderTextLocation(graphics, bloat.getBloatUpTimer() + "( " + secondConversion + " )", 15, 1, col, canvasPoint);
                            renderResizeTextLocation(graphics, String.valueOf(bloat.getBloatUpTimer()), 15, Font.BOLD, col, canvasPoint);
                        }
                    }
                }
            } else if (bloat.bloatVar == 0) {
                if (config.bloatEntryTimer() && bloat != null) {
                    Point canvasPoint = bloat.getBloatNPC().getCanvasTextLocation(graphics, String.valueOf(bloat.getBloatUpTimer()), 60);
                    Color col = Color.WHITE;
                    if (config.fontStyle()) {
                        renderTextLocation(graphics, String.valueOf(bloat.getBloatUpTimer()), col, canvasPoint);
                    } else {
                        renderResizeTextLocation(graphics, String.valueOf(bloat.getBloatUpTimer()), 15, Font.BOLD, col, canvasPoint);
                    }
                }
            }

            if (bloat != null) {
                if ((bloat.getBloatState() == 2 || bloat.getBloatState() == 3) && config.bloatStompMode() != SpoonTobConfig.bloatStompMode.OFF) {
                    renderStompSafespots(graphics);
                }
            }
        }
        return null;
    }

    private void renderStompSafespots(Graphics2D graphics) {
        if (bloat.getBloatDown() != null) {
            BloatSafespot safespot = bloat.getBloatDown().getBloatSafespot();
            safespot.getSafespotLines().forEach((line) -> {
                Color color = config.bloatStompColor();
                if (config.bloatStompMode() == SpoonTobConfig.bloatStompMode.RAVE){
                    color = plugin.raveUtils.getColor(line.hashCode(), true);
                }
                drawLine(graphics, line, color, config.bloatStompWidth());
            });
        }
    }

    protected void drawLine(Graphics2D graphics, @Nullable SSLine safespotLine, @Nonnull Color lineColor, int lineStroke) {
        if (safespotLine != null) {
            Point pointA = safespotLine.getTranslatedPointA(client);
            Point pointB = safespotLine.getTranslatedPointB(client);
            if (pointA != null && pointB != null) {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setStroke(new BasicStroke((float)lineStroke));
                graphics.setColor(lineColor);
                graphics.drawLine(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
            }
        }
    }
}