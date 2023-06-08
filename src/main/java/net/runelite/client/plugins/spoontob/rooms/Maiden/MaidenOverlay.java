package net.runelite.client.plugins.spoontob.rooms.Maiden;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.plugins.spoontob.util.TheatrePerspective;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class MaidenOverlay extends RoomOverlay {
    @Inject
    private Maiden maiden;

    @Inject
    private SpoonTobPlugin plugin;

    @Inject
    private Client client;

    protected static final BiFunction<Integer, Integer, Color> rgbMod;

    private final  ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    protected MaidenOverlay(SpoonTobConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        super(config);
        this.modelOutlineRenderer = modelOutlineRenderer;
    }

    static {
        rgbMod = ((max, current) -> new Color(255 * (max - current) / max, 255 * current / max, 0));
    }

    public Dimension render(Graphics2D graphics) {
        if (maiden.isMaidenActive() && maiden.getMaidenNPC() != null) {
            if (config.fuckBluelite()) {
                LocalPoint lp = null;
                if (maiden != null)
                    lp = maiden.getMaidenNPC().getLocalLocation();
                if (lp != null) {
                    LocalPoint lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() - 960);
                    List<Polygon> polyList = new ArrayList<>();
                    Polygon poly = TheatrePerspective.getLine(client, lp2, "swse");
                    Polygon poly2 = TheatrePerspective.getLine(client, lp2, "westMiddle");
                    Polygon poly3 = TheatrePerspective.getLine(client, lp2, "swnw");
                    polyList.add(poly);
                    polyList.add(poly2);
                    polyList.add(poly3);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() - 800);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "sene");
                    poly3 = TheatrePerspective.getLine(client, lp2, "nwne");
                    polyList.add(poly);
                    polyList.add(poly2);
                    polyList.add(poly3);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() - 640);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "sene");
                    poly3 = TheatrePerspective.getLine(client, lp2, "swnw");
                    polyList.add(poly);
                    polyList.add(poly2);
                    polyList.add(poly3);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() - 480);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "kUp");
                    poly3 = TheatrePerspective.getLine(client, lp2, "kDown");
                    polyList.add(poly);
                    polyList.add(poly2);
                    polyList.add(poly3);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() - 192);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "B1");
                    poly3 = TheatrePerspective.getLine(client, lp2, "B2");
                    Polygon poly4 = TheatrePerspective.getLine(client, lp2, "B3");
                    Polygon poly5 = TheatrePerspective.getLine(client, lp2, "B4");
                    Polygon poly6 = TheatrePerspective.getLine(client, lp2, "B5");
                    Polygon poly7 = TheatrePerspective.getLine(client, lp2, "B6");
                    Polygon poly8 = TheatrePerspective.getLine(client, lp2, "B7");
                    Polygon poly9 = TheatrePerspective.getLine(client, lp2, "B8");
                    Polygon poly10 = TheatrePerspective.getLine(client, lp2, "B9");
                    Polygon poly11 = TheatrePerspective.getLine(client, lp2, "B10");
                    polyList.add(poly);
                    polyList.add(poly2);
                    polyList.add(poly3);
                    polyList.add(poly4);
                    polyList.add(poly5);
                    polyList.add(poly6);
                    polyList.add(poly7);
                    polyList.add(poly8);
                    polyList.add(poly9);
                    polyList.add(poly10);
                    polyList.add(poly11);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() - 32);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "sene");
                    polyList.add(poly);
                    polyList.add(poly2);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() + 128);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "sene");
                    poly3 = TheatrePerspective.getLine(client, lp2, "nwne");
                    polyList.add(poly);
                    polyList.add(poly2);
                    polyList.add(poly3);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() + 288);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "sene");
                    poly3 = TheatrePerspective.getLine(client, lp2, "swnw");
                    poly4 = TheatrePerspective.getLine(client, lp2, "E");
                    polyList.add(poly);
                    polyList.add(poly2);
                    polyList.add(poly3);
                    polyList.add(poly4);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() + 448);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "sene");
                    polyList.add(poly);
                    polyList.add(poly2);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() + 608);
                    poly = TheatrePerspective.getLine(client, lp2, "I");
                    polyList.add(poly);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() + 768);
                    poly = TheatrePerspective.getLine(client, lp2, "I");
                    poly2 = TheatrePerspective.getLine(client, lp2, "swnw");
                    polyList.add(poly);
                    polyList.add(poly2);
                    lp2 = new LocalPoint(lp.getX() + 1280, lp.getY() + 928);
                    poly = TheatrePerspective.getLine(client, lp2, "swse");
                    poly2 = TheatrePerspective.getLine(client, lp2, "sene");
                    poly3 = TheatrePerspective.getLine(client, lp2, "swnw");
                    poly4 = TheatrePerspective.getLine(client, lp2, "E");
                    polyList.add(poly);
                    polyList.add(poly2);
                    polyList.add(poly3);
                    polyList.add(poly4);
                    graphics.setColor(maiden.c);
                    graphics.setStroke(new BasicStroke(3.0F));
                    for (Polygon p : polyList)
                        graphics.draw(p);
                }
            }

            if (config.maidenBlood() != SpoonTobConfig.maidenBloodSplatMode.OFF) {
                for (int i = 0; i < maiden.getMaidenBloodSplatters().size(); i++) {
                    WorldPoint wp = maiden.getMaidenBloodSplatters().get(i);
                    Color color = config.bloodTossColour();
                    if (config.maidenBlood() == SpoonTobConfig.maidenBloodSplatMode.RAVEST) {
                        color = plugin.raveUtils.getColor(i * 50, false);
                    } else if (config.maidenBlood() == SpoonTobConfig.maidenBloodSplatMode.RAVE) {
                        color = plugin.raveUtils.getColor(maiden.getMaidenBloodSplatters().hashCode(), true);
                    }
                    drawTile(graphics, wp, color, 1, config.bloodTossColour().getAlpha(), config.bloodTossFill());
                }
            }

            if(config.bloodTossTicks()) {
                for (int i = 0; i < maiden.getMaidenBloodSplatterProj().size(); i++) {
                    String text = String.valueOf(maiden.getMaidenBloodSplatterProj().get(i).projectile.getRemainingCycles() / 30);
                    Point canvasPoint = Perspective.getCanvasTextLocation(client, graphics, maiden.getMaidenBloodSplatterProj().get(i).lp, text, 0);
                    if (canvasPoint != null) {
                        Color col = Color.WHITE;
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, text, col, canvasPoint);
                        } else {
                            renderSteroidsTextLocation(graphics, text, 14, Font.BOLD, col, canvasPoint);
                        }
                    }
                }
            }

            if (config.maidenSpawns()) {
                for (WorldPoint point : maiden.getMaidenBloodSpawnLocations()) {
                    drawTile(graphics, point, config.bloodSpawnsColor(), 2, 180, 20);
                }
                if (config.maidenSpawnsTrail()) {
                    for (WorldPoint point : maiden.getMaidenBloodSpawnTrailingLocations()) {
                        drawTile(graphics, point, config.bloodSpawnsColor(), 1, 120, 10);
                    }
                }
            }

            if (config.maidenTickCounter() && !maiden.getMaidenNPC().isDead()) {
                String text = String.valueOf(maiden.ticksUntilAttack);
                Point canvasPoint = maiden.getMaidenNPC().getCanvasTextLocation(graphics, text, 30);
                if (canvasPoint != null) {
                    Color col = maiden.maidenSpecialWarningColor();
                    if (config.fontStyle()) {
                        renderTextLocation(graphics, text, col, canvasPoint);
                    } else {
                        renderResizeTextLocation(graphics, text, 14, Font.BOLD, col, canvasPoint);
                    }
                }
            }

            if ((config.maidenFreezeTimer() == SpoonTobConfig.maidenFreezeTimerMode.TILE || config.maidenScuffedCrab()) && maiden.maidenCrabInfoList.size() > 0) {
                int maidenX = 0;
                if (maiden.getMaidenNPC() != null) {
                    WorldPoint maidenWp = maiden.getMaidenNPC().getWorldLocation();
                    maidenX = maidenWp.getX();
                    NPCComposition maidenModel = maiden.getMaidenNPC().getTransformedComposition();
                    if (maidenModel != null) {
                        maidenX += maidenModel.getSize();
                    }
                }

                for (MaidenCrabInfo mci : maiden.maidenCrabInfoList) {
                    if (!mci.crab.isDead()) {
                        int healerX = mci.crab.getWorldLocation().getX();
                        int deltaX = Math.max(0, healerX - maidenX);
                        if (deltaX > 0) {
                            NPCComposition npcComposition = mci.crab.getTransformedComposition();
                            if (npcComposition != null) {
                                int size = npcComposition.getSize();
                                LocalPoint lp = mci.crab.getLocalLocation();
                                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
                                if (mci.frozenTicks != -1) {
                                    renderPoly(graphics, rgbMod.apply(32, mci.frozenTicks), tilePoly);
                                } else if(config.maidenScuffedCrab() && mci.scuffed && maiden.crabTicksSinceSpawn > 0) {
                                    modelOutlineRenderer.drawOutline(mci.crab, 2, config.maidenScuffedCrabColor(), 4);
                                }
                            }
                        }
                    }
                }
            }

            if ((config.showMaidenCrabsDistance() || config.showMaidenCrabHp() || config.maidenFreezeTimer() == SpoonTobConfig.maidenFreezeTimerMode.TICKS)
                    && maiden.maidenCrabInfoList.size() > 0) {
                renderCrabInfo(graphics);
            }

            if(config.bloodSpawnFreezeTimer() && maiden.frozenBloodSpawns.size() > 0){
                maiden.frozenBloodSpawns.forEach((npc, ticks) -> {
                    if(ticks >= 0) {
                        String text = String.valueOf(ticks);
                        Point canvasPoint = npc.getCanvasTextLocation(graphics, text, 30);
                        if (canvasPoint != null) {
                            Color col = Color.WHITE;
                            if (config.fontStyle()) {
                                renderTextLocation(graphics, text, col, canvasPoint);
                            } else {
                                renderSteroidsTextLocation(graphics, text, 14, Font.BOLD, col, canvasPoint);
                            }
                        }
                    }
                });
            }
        }
        return null;
    }

    private void renderCrabInfo(Graphics2D graphics) {
        ArrayList<NPC> prevCrabs = new ArrayList<>();
        for (MaidenCrabInfo mci : maiden.maidenCrabInfoList) {
            String text = "";
            String distanceLine = "";
            Color distanceColor = config.distanceColor();
            Color color = Color.GREEN;
            if (mci.hpRatio != 0) {
                if (config.showMaidenCrabHp()) {
                    double crabHealthPcent = ((double) mci.hpRatio / (double) mci.hpScale) * 100.0D;
                    if (config.oldHpThreshold()) {
                        color = plugin.oldHitpointsColor(crabHealthPcent);
                    } else {
                        color = plugin.calculateHitpointsColor(crabHealthPcent);
                    }
                    String crabHp = String.valueOf(crabHealthPcent);
                    text = crabHp.substring(0, crabHp.indexOf(".")) + "%";
                }

                if (config.maidenFreezeTimer() == SpoonTobConfig.maidenFreezeTimerMode.TICKS && mci.frozenTicks >= 0) {
                    if (!text.equals("")) {
                        text += " : " + mci.frozenTicks;
                    } else {
                        text = String.valueOf(mci.frozenTicks);
                    }
                }

                if (config.showMaidenCrabsDistance()) {
                    WorldPoint maidenWp = maiden.getMaidenNPC().getWorldLocation();
                    int maidenX = maidenWp.getX();
                    NPCComposition maidenModel = maiden.getMaidenNPC().getTransformedComposition();
                    if (maidenModel != null) {
                        maidenX += maidenModel.getSize();
                    }
                    WorldPoint healerWp = mci.crab.getWorldLocation();
                    int healerX = healerWp.getX();
                    int deltaX = Math.max(0, healerX - maidenX);
                    if (config.singleLineDistance()) {
                        if (mci.frozenTicks == -1) {
                            if (!text.equals("")) {
                                text += " : " + deltaX;
                            } else {
                                color = Color.WHITE;
                                text = Integer.toString(deltaX);
                            }
                        }
                    } else {
                        if (config.showFrozenDistance() || mci.frozenTicks == -1) {
                            distanceLine = Integer.toString(deltaX);
                        }
                    }
                }

                int offsetTimes = 0;
                NPC firstFreeze = null;
                for (NPC crab : prevCrabs) {
                    LocalPoint lp = crab.getLocalLocation();
                    if (lp.getX() == mci.crab.getLocalLocation().getX() && lp.getY() == mci.crab.getLocalLocation().getY()) {
                        offsetTimes++;
                        if (firstFreeze == null){
                            firstFreeze = crab;
                        }
                    }
                }

                Point drawPoint;
                if (offsetTimes != 0){
                    drawPoint = firstFreeze.getCanvasTextLocation(graphics, text, 0);
                    if (drawPoint != null) {
                        int x = drawPoint.getX();
                        int y = drawPoint.getY() - (15 * offsetTimes);
                        drawPoint = new Point(x, y);
                    }
                } else {
                    drawPoint = mci.crab.getCanvasTextLocation(graphics, text, 0);
                }

                if (drawPoint != null) {
                    if (config.fontStyle()) {
                        renderTextLocation(graphics, text, color, drawPoint);
                        if (!distanceLine.equals("")) {
                            if(text.contains(":")) {
                                drawPoint = new Point(drawPoint.getX() + 15, drawPoint.getY() - 10);
                            }else {
                                drawPoint = new Point(drawPoint.getX() + 5, drawPoint.getY() - 10);
                            }
                            renderTextLocation(graphics, distanceLine, distanceColor, drawPoint);
                        }
                    } else {
                        renderResizeTextLocation(graphics, text, 11, Font.BOLD, color, drawPoint);
                        if (!distanceLine.equals("")) {
                            if(text.contains(":")) {
                                drawPoint = new Point(drawPoint.getX() + 15, drawPoint.getY() - 10);
                            } else {
                                drawPoint = new Point(drawPoint.getX() + 5, drawPoint.getY() - 10);
                            }
                            renderResizeTextLocation(graphics, distanceLine, 11, Font.BOLD, distanceColor, drawPoint);
                        }
                    }
                }
                prevCrabs.add(mci.crab);
            }
        }
    }
}