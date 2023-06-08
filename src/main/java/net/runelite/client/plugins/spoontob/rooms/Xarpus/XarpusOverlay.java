package net.runelite.client.plugins.spoontob.rooms.Xarpus;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class XarpusOverlay extends RoomOverlay {
    @Inject
    private Xarpus xarpus;

    @Inject
    private SpoonTobPlugin plugin;

    PanelComponent panelComponent = new PanelComponent();

    @Inject
    protected XarpusOverlay(SpoonTobConfig config) {
        super(config);
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.entryInstanceTimer() == SpoonTobConfig.instancerTimerMode.OVERHEAD && xarpus.isInstanceTimerRunning() && !xarpus.isExhumedSpawned()
                && xarpus.isInXarpusRegion()) {
            Player player = client.getLocalPlayer();
            if (player != null) {
                Point point = player.getCanvasTextLocation(graphics, "#", player.getLogicalHeight() + 60);
                if (point != null) {
                    renderTextLocation(graphics, String.valueOf(xarpus.getInstanceTimer()), Color.CYAN, point);
                }
            }
        }

        if (xarpus.isXarpusActive()) {
            NPC boss = xarpus.getXarpusNPC();
            boolean showp2 = config.xarpusTicks() && Xarpus.P2_IDS.contains(boss.getId());
            boolean p3exception = xarpus.isHM() && xarpus.isXarpusStare() && xarpus.isP3Active();
            boolean showp3 = config.xarpusTicks() && Xarpus.P3_IDS.contains(boss.getId()) && !p3exception;
            if (showp2 || showp3) {
                int tick = xarpus.getXarpusTicksUntilAttack();
                String ticksLeftStr = String.valueOf(tick);
                Point canvasPoint = boss.getCanvasTextLocation(graphics, ticksLeftStr, 130);
                if (config.fontStyle()) {
                    renderTextLocation(graphics, ticksLeftStr, Color.WHITE, canvasPoint);
                } else {
                    renderResizeTextLocation(graphics, ticksLeftStr, 14, Font.BOLD, Color.WHITE, canvasPoint);
                }
            }

            if (Xarpus.P1_IDS.contains(boss.getId())) {
                if (!xarpus.getXarpusExhumeds().isEmpty()) {
                    Collection<Pair<GroundObject, Integer>> exhumeds = xarpus.getXarpusExhumeds().values();
                    exhumeds.forEach((p) -> {
                        GroundObject o = p.getLeft();
                        int ticks = p.getRight();
                        String text = String.valueOf(ticks);
                        int maxSafeTicks = 8;
                        int minSafeTicks = 1;
                        if (xarpus.isHM()) {
                            maxSafeTicks = 6;
                        }

                        if (config.xarpusExhumed() == SpoonTobConfig.exhumedMode.TILE || config.xarpusExhumed() == SpoonTobConfig.exhumedMode.BOTH) {
                            Polygon poly = o.getCanvasTilePoly();
                            if (poly != null) {
                                Color color = new Color(0, 255, 0, 130);
                                if (config.exhumedStepOffWarning() == SpoonTobConfig.stepOffMode.TILE || config.exhumedStepOffWarning() == SpoonTobConfig.stepOffMode.BOTH) {
                                    if (ticks <= minSafeTicks || ticks >= maxSafeTicks) {
                                        color = new Color(0, 255, 0, 130);
                                    } else {
                                        color = new Color(255, 0, 0, 130);
                                    }
                                }
                                graphics.setColor(color);
                                graphics.setStroke(new BasicStroke(1));
                                graphics.draw(poly);
                            }
                        }

                        if (config.xarpusExhumed() == SpoonTobConfig.exhumedMode.BOTH || config.xarpusExhumed() == SpoonTobConfig.exhumedMode.TICKS) {

                            Point textLocation = o.getCanvasTextLocation(graphics, text, 0);
                            if (textLocation != null) {
                                Color color = Color.WHITE;

                                if (config.exhumedStepOffWarning() == SpoonTobConfig.stepOffMode.TICKS || config.exhumedStepOffWarning() == SpoonTobConfig.stepOffMode.BOTH) {
                                    color = Color.RED;
                                    if (ticks <= minSafeTicks || ticks >= maxSafeTicks) {
                                        color = Color.GREEN;
                                    }
                                }

                                if (config.fontStyle()) {
                                    renderTextLocation(graphics, text, color, textLocation);
                                } else {
                                    renderResizeTextLocation(graphics, text, 12, Font.BOLD, color, textLocation);
                                }
                            }
                        }
                    });
                }
            }


            if (config.xarpusLos() != SpoonTobConfig.losMode.OFF) {
                renderLineOfSightPolygon(graphics);
            }

            if (config.exhumedOnXarpus() && xarpus.isExhumedSpawned() && Xarpus.P1_IDS.contains(xarpus.getXarpusNPC().getId()) && xarpus.getExhumedCounter() != null) {
                String xarpusText = (xarpus.getExhumedCounter().getCount() == 0) ? "NOW!" : (String.valueOf(xarpus.getExhumedCounter().getCount()));
                if (xarpusText.length() >= 1) {
                    Point canvasPoint = xarpus.getXarpusNPC().getCanvasTextLocation(graphics, xarpusText, 320);

                    if (canvasPoint != null && !xarpus.getXarpusNPC().isDead()) {
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, xarpusText, Color.ORANGE, canvasPoint);
                        } else {
                            renderResizeTextLocation(graphics, xarpusText, 14, Font.BOLD, Color.ORANGE, canvasPoint);
                        }
                    }
                }
            }
        }
        return null;
    }

    public void renderLineOfSightPolygon(Graphics2D graphics) {
        NPC xarpusNpc = xarpus.getXarpusNPC();
        if (!xarpusNpc.isDead() && Xarpus.P2_IDS.contains(xarpusNpc.getId()) && xarpus.xarpusStare) {
            Direction dir = Direction.getPreciseDirection(xarpusNpc.getOrientation());
            if (dir != null) {
                WorldPoint wp = WorldPoint.fromLocal(client, xarpusNpc.getLocalLocation());
                boolean mt = config.xarpusLos() == SpoonTobConfig.losMode.MELEE;
                Point[] points;
                switch(dir) {
                    case NORTHEAST:
                        points = mt ? LineOfSight.NE_MELEE.getFunc().apply(wp) : LineOfSight.NE_BOX.getFunc().apply(wp);
                        break;
                    case NORTHWEST:
                        points = mt ? LineOfSight.NW_MELEE.getFunc().apply(wp) : LineOfSight.NW_BOX.getFunc().apply(wp);
                        break;
                    case SOUTHEAST:
                        points = mt ? LineOfSight.SE_MELEE.getFunc().apply(wp) : LineOfSight.SE_BOX.getFunc().apply(wp);
                        break;
                    case SOUTHWEST:
                        points = mt ? LineOfSight.SW_MELEE.getFunc().apply(wp) : LineOfSight.SW_BOX.getFunc().apply(wp);
                        break;
                    default:
                        return;
                }

                Polygon poly = new Polygon();
                Arrays.stream(points, 0, points.length).map((worldPoint) -> {
                    LocalPoint lp = LocalPoint.fromWorld(client, worldPoint.getX(), worldPoint.getY());
                    int x = lp.getX();
                    int y = lp.getY();
                    switch(dir) {
                        case NORTHEAST:
                            return Perspective.localToCanvas(client, new LocalPoint(x - 64, y - 64), client.getPlane());
                        case NORTHWEST:
                            return Perspective.localToCanvas(client, new LocalPoint(x + 64, y - 64), client.getPlane());
                        case SOUTHEAST:
                            return Perspective.localToCanvas(client, new LocalPoint(x - 64, y + 64), client.getPlane());
                        case SOUTHWEST:
                            return Perspective.localToCanvas(client, new LocalPoint(x + 64, y + 64), client.getPlane());
                        default:
                            return null;
                    }
                }).filter(Objects::nonNull).forEach((localToCanvas) -> {
                    poly.addPoint(localToCanvas.getX(), localToCanvas.getY());
                });

                Color color;
                if(config.raveLos()) {
                    color = plugin.raveUtils.getColor(poly.hashCode(), true);
                    renderXarpusPolygon(graphics, poly, color);
                } else {
                    renderXarpusPolygon(graphics, poly, config.xarpusLosColor());
                }
            }
        }
    }

    protected void renderXarpusPolygon(Graphics2D graphics, @Nullable Shape polygon, @Nonnull Color color) {
        if (polygon != null) {
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), config.xarpusLosColor().getAlpha()));
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), config.xarpusLosFill()));
            graphics.fill(polygon);
        }
    }
}