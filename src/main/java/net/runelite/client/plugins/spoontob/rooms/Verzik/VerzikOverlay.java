package net.runelite.client.plugins.spoontob.rooms.Verzik;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class VerzikOverlay extends RoomOverlay {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0");

    @Inject
    private Verzik verzik;

    @Inject
    private SpoonTobPlugin plugin;

    @Inject
    protected VerzikOverlay(SpoonTobConfig config) {
        super(config);
    }

    public Dimension render(Graphics2D graphics) {
        if (verzik.isVerzikActive() && verzik.getVerzikNPC() != null) {
            int id = verzik.getVerzikNPC().getId();
            if (Verzik.VERZIK_ACTIVE_IDS.contains(id)) {
                if (config.displayGreenBall() != SpoonTobConfig.greenBallMode.OFF || config.displayGreenBallTicks()) {
                    displayProjectiles(graphics);
                }

                if (config.purpleAoe()) {
                    displayPurpleCrabAOE(graphics, verzik.getVerzikNPC());
                }
            }

            if (config.verzikMelee() != SpoonTobConfig.meleeTileMode.OFF) {
                int size = 1;
                NPCComposition composition = verzik.getVerzikNPC().getTransformedComposition();
                if (composition != null)
                    size = composition.getSize();
                LocalPoint lp = LocalPoint.fromWorld(client, verzik.getVerzikNPC().getWorldLocation());
                if (lp != null) {
                    Polygon tilePoly = getCanvasTileAreaPoly(client, lp, size, false);
                    if (tilePoly != null && verzik.verzikPhase == Verzik.Phase.PHASE3) {
                        if (config.verzikMelee() == SpoonTobConfig.meleeTileMode.TANK_NOTIFIER) {
                            if (verzik.getVerzikNPC().getInteracting() == client.getLocalPlayer()) {
                                renderPoly(graphics, config.p3AggroColor(), tilePoly);
                            } else {
                                renderPoly(graphics, config.verzikMeleeColor(), tilePoly);
                            }
                        }else {
                            renderPoly(graphics, config.verzikMeleeColor(), tilePoly);
                        }
                    }
                }
            }

            if(config.verzikTankTarget() && verzik.verzikPhase == Verzik.Phase.PHASE3){
                if(verzik.getVerzikNPC() != null && verzik.getVerzikNPC().getInteracting() != null){
                    Actor actor = verzik.getVerzikNPC().getInteracting();
                    Polygon tilePoly = getCanvasTileAreaPoly(client, actor.getLocalLocation(), 1, false);
                    if (tilePoly != null) {
                        renderPoly(graphics, config.p3AggroColor(), tilePoly);
                    }
                }
            }

            if (config.showVerzikNados() != SpoonTobConfig.nadoMode.OFF && verzik.verzikPhase == Verzik.Phase.PHASE3) {
                if (config.showVerzikNados() == SpoonTobConfig.nadoMode.ALL) {
                    for(NPC nado : client.getNpcs()){
                        if (Verzik.NADO_IDS.contains(nado.getId())){
                            if (config.showVerzikNadoStyle() == SpoonTobConfig.nadoStyle.TILE){
                                renderNpcOverlay(graphics, nado, config.showVerzikNadoColor(), 2, config.showVerzikNadoColor().getAlpha(), config.verzikNadoOpacity());
                            } else if (config.showVerzikNadoStyle() == SpoonTobConfig.nadoStyle.TRUE_LOCATION){
                                renderNpcTLOverlay(graphics, nado, config.showVerzikNadoColor(), 2, config.showVerzikNadoColor().getAlpha(), config.verzikNadoOpacity());
                            }
                        }
                    }
                }else if (config.showVerzikNados() == SpoonTobConfig.nadoMode.PERSONAL && verzik.getPersonalNado() != null) {
                    if(config.showVerzikNadoStyle() == SpoonTobConfig.nadoStyle.TILE){
                        renderNpcOverlay(graphics, verzik.getPersonalNado(), config.showVerzikNadoColor(), 2, config.showVerzikNadoColor().getAlpha(), config.verzikNadoOpacity());
                    }else if(config.showVerzikNadoStyle() == SpoonTobConfig.nadoStyle.TRUE_LOCATION){
                        renderNpcTLOverlay(graphics, verzik.getPersonalNado(), config.showVerzikNadoColor(), 2, config.showVerzikNadoColor().getAlpha(), config.verzikNadoOpacity());
                    }
                }
            }

            if(config.raveNados() != SpoonTobConfig.raveNadoMode.OFF && verzik.verzikPhase == Verzik.Phase.PHASE3){
                int index = 0;
                Color color;
                for (NPC nado : client.getNpcs()) {
                    if (Verzik.NADO_IDS.contains(nado.getId())){
                        if (config.raveNados() == SpoonTobConfig.raveNadoMode.RAVE) {
                            color = plugin.raveUtils.getColor(nado.hashCode(), true);
                        } else {
                            color = plugin.raveUtils.getColor(index * 50, false);
                        }
                        renderTargetOverlay(graphics, nado, color);
                    }
                    index++;
                }
            }

            if (config.showVerzikRangeAttack()){
                for (WorldPoint p : verzik.verzikRangeProjectiles.values()) {
                    LocalPoint point = LocalPoint.fromWorld(client, p);
                    if(point != null) {
                        Polygon poly = Perspective.getCanvasTilePoly(client, point);
                        graphics.setColor(new Color(config.verzikRangeAttacksColor().getRed(), config.verzikRangeAttacksColor().getGreen(), config.verzikRangeAttacksColor().getBlue(), config.verzikRangeAttacksColor().getAlpha()));
                        graphics.drawPolygon(poly);
                        graphics.setColor(new Color(config.verzikRangeAttacksColor().getRed(), config.verzikRangeAttacksColor().getGreen(), config.verzikRangeAttacksColor().getBlue(), config.verzikRangeAttacksFill()));
                        graphics.fillPolygon(poly);
                    }
                }
            }

            if ((config.showVerzikYellows() == SpoonTobConfig.verzikYellowsMode.YELLOW
                    || (config.showVerzikYellows() == SpoonTobConfig.verzikYellowsMode.GROUPS && verzik.yellowGroups.size() == 0)) && verzik.yellowsOut) {
                String text = Integer.toString(verzik.yellowTimer);
                Point point;

                if(config.yellowTicksOnPlayer() && client.getLocalPlayer() != null) {
                    point = Perspective.getCanvasTextLocation(client, graphics, client.getLocalPlayer().getLocalLocation(), text, config.yellowsOffset());

                    if (config.fontStyle()) {
                        renderTextLocation(graphics, text, Color.WHITE, point);
                    } else {
                        renderSteroidsTextLocation(graphics, text, config.yellowsSize(), Font.BOLD, Color.WHITE, point);
                    }
                }

                for (WorldPoint wp : verzik.yellowsList) {
                    LocalPoint lp = LocalPoint.fromWorld(client, wp);
                    drawTile(graphics, wp, Color.YELLOW, 2, 255, 0);

                    if (!config.yellowTicksOnPlayer() && lp != null) {
                        point = Perspective.getCanvasTextLocation(client, graphics, lp, text, 0);
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, text, Color.WHITE, point);
                        } else {
                            renderResizeTextLocation(graphics, text, 12, Font.BOLD, Color.WHITE, point);
                        }
                    }
                }
            }

            if (config.showVerzikRocks() && verzik.verzikPhase == Verzik.Phase.PHASE1) {
                for (GraphicsObject object : client.getGraphicsObjects()) {
                    if (object.getId() == 1436) {
                        LocalPoint lp = object.getLocation();
                        drawTile(graphics, WorldPoint.fromLocal(client, lp), config.showVerzikRocksColor(), 2, 255, 50);
                    }
                }
            }

            if (config.showVerzikAcid() && verzik.verzikPhase == Verzik.Phase.PHASE2 && client.getLocalPlayer() != null) {
                int index = 0;
                for (GameObject object : verzik.acidSpots) {
                    if(client.getLocalPlayer().getWorldLocation().distanceTo(object.getWorldLocation()) <= config.showVerzikAcidDistance()) {
                        LocalPoint lp = object.getLocalLocation();
                        String text = String.valueOf(verzik.acidSpotsTimer.get(index));
                        Point point = Perspective.getCanvasTextLocation(client, graphics, lp, text, 0);
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, text, Color.WHITE, point);
                        } else {
                            renderSteroidsTextLocation(graphics, text, 12, Font.BOLD, Color.WHITE, point);
                        }
                        drawTile(graphics, WorldPoint.fromLocal(client, lp), config.showVerzikAcidColor(), 2, 255, 0);
                    }
                    index++;
                }
            }

            verzik.getVerzikAggros().forEach(k -> {
                if (config.verzikNyloAggroWarning() && k.getInteracting() != null && !k.isDead()) {
                    String targetText = "";

                    if (k.getInteracting().getName() != null) {
                        if (k.getInteracting().getName().equalsIgnoreCase("wayabove") || k.getInteracting().getName().equalsIgnoreCase("oblv way")) {
                            targetText = "Wayaboob";
                        } else if (k.getInteracting().getName().equalsIgnoreCase("flaw less") || k.getInteracting().getName().equalsIgnoreCase("oblv flaw")
                                || k.getInteracting().getName().equalsIgnoreCase("flaaw less")) {
                            targetText = "Glennjamin";
                        } else if (k.getInteracting().getName().equalsIgnoreCase("xelywood")) {
                            targetText = "Femboy";
                        } else if (k.getInteracting().getName().equalsIgnoreCase("afka") || k.getInteracting().getName().equalsIgnoreCase("rattori")
                                || k.getInteracting().getName().equalsIgnoreCase("sadgecry") || k.getInteracting().getName().equalsIgnoreCase("squish that")) {
                            targetText = "thisiswhyudonthavedust";
                        } else if (k.getInteracting().getName().equalsIgnoreCase("messywalcott")) {
                            targetText = "Rat";
                        } else if (k.getInteracting().getName().equalsIgnoreCase("divinesdream") || k.getInteracting().getName().equalsIgnoreCase("divine dream")
                                || k.getInteracting().getName().equalsIgnoreCase("trio tob")) {
                            targetText = "Lil Bitch";
                        } else if (k.getInteracting().getName().equalsIgnoreCase("null god")) {
                            targetText = "Click";
                        } else if (k.getInteracting().getName().equalsIgnoreCase("noobtype")) {
                            targetText = "Sick Invite";
                        } else if (k.getInteracting().getName().equalsIgnoreCase("turbosmurf") || k.getInteracting().getName().equalsIgnoreCase("yukinon fan") ) {
                            targetText = k.getInteracting().getName();

                        } else {
                            targetText = k.getInteracting().getName();
                        }
                    }
                    Point textLocation = k.getCanvasTextLocation(graphics, targetText, 80);
                    if (textLocation != null) {
                        Color color = Color.GREEN;
                        if (k.getInteracting().equals(client.getLocalPlayer()))
                            color = Color.RED;
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, targetText, color, textLocation);
                        } else {
                            renderResizeTextLocation(graphics, targetText, 14, Font.BOLD, color, textLocation);
                        }
                    }
                }

                if (config.verzikNyloExplodeRange() == SpoonTobConfig.VerzikNyloSetting.ALL_CRABS || (config.verzikNyloExplodeRange() == SpoonTobConfig.VerzikNyloSetting.MY_CRABS && client.getLocalPlayer() != null && client.getLocalPlayer().equals(k.getInteracting()))) {
                    int size = 1;
                    int thick_size = 1;
                    NPCComposition composition = k.getTransformedComposition();
                    if (composition != null)
                        size = composition.getSize() + 2 * thick_size;
                    LocalPoint lp = LocalPoint.fromWorld(client, k.getWorldLocation());
                    if (lp != null) {
                        lp = new LocalPoint(lp.getX() - thick_size * 128, lp.getY() - thick_size * 128);
                        Polygon tilePoly = getCanvasTileAreaPoly(client, lp, size, false);
                        if (tilePoly != null)
                            renderPoly(graphics, config.verzikNyloExplodeTileColor(), tilePoly);
                    }
                }
            });

            if (config.redsHp() && verzik.redCrabs.size() > 0 && verzik.verzikPhase == Verzik.Phase.PHASE2) {
                int index = 0;
                for (NPC crab : verzik.redCrabs) {
                    Color textColor = Color.WHITE;
                    String text = "";

                    if (crab.getHealthRatio() > 0 || (verzik.lastRatioList.get(index) != 0 && verzik.lastHealthScaleList.get(index) != 0)) {
                        if (crab.getHealthRatio() > 0) {
                            verzik.lastRatioList.set(index, crab.getHealthRatio());
                            verzik.lastHealthScaleList.set(index, crab.getHealthScale());
                        }
                        float floatRatio = ((float) verzik.lastRatioList.get(index) / (float) verzik.lastHealthScaleList.get(index)) * 100;
                        if (config.oldHpThreshold()) {
                            textColor = plugin.oldHitpointsColor(floatRatio);
                        } else {
                            textColor = plugin.calculateHitpointsColor(floatRatio);
                        }
                        text = Float.toString(floatRatio).substring(0, 4) + "%";
                        Point textLoc = crab.getCanvasTextLocation(graphics, text, 50);
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, text, textColor, textLoc);
                        } else {
                            renderResizeTextLocation(graphics, text, 14, Font.BOLD, textColor, textLoc);
                        }
                    }
                    index++;
                }
            }

            if ((config.showVerzikTicks() || config.showVerzikAttacks() != SpoonTobConfig.verzikAttacksMode.OFF || config.showVerzikTotalTicks())) {
                String text = "";

                if ((((config.showVerzikAttacks() == SpoonTobConfig.verzikAttacksMode.ALL && verzik.getVerzikSpecial() != Verzik.SpecialAttack.WEBS)
                        || (config.showVerzikAttacks() == SpoonTobConfig.verzikAttacksMode.P2 && verzik.verzikPhase == Verzik.Phase.PHASE2))
                        || (config.showVerzikAttacks() == SpoonTobConfig.verzikAttacksMode.REDS && verzik.verzikRedPhase))
                        && ((config.showVerzikAttacks() == SpoonTobConfig.verzikAttacksMode.REDS && verzik.verzikRedPhase) || config.showVerzikAttacks() != SpoonTobConfig.verzikAttacksMode.REDS)) {
                    text = text + "Att " + verzik.getVerzikAttackCount();
                    if (config.showVerzikTicks() || config.showVerzikTotalTicks())
                        text = text + " : ";
                }
                if (config.showVerzikTicks() && verzik.getVerzikSpecial() != Verzik.SpecialAttack.WEBS && verzik.verzikPhase == Verzik.Phase.PHASE1) {
                    text = text + verzik.getVerzikTicksUntilAttack();
                    if (config.showVerzikTotalTicks())
                        text = text + " : ";
                }
                if (config.showVerzikTicks() && verzik.getVerzikSpecial() != Verzik.SpecialAttack.WEBS && verzik.verzikPhase == Verzik.Phase.PHASE2
                        && !verzik.verzikRedPhase) {
                    text = text + verzik.getVerzikTicksUntilAttack();
                    if (config.showVerzikTotalTicks())
                        text = text + " : ";
                }
                if (config.showVerzikTicks() && verzik.getVerzikSpecial() != Verzik.SpecialAttack.WEBS && verzik.verzikPhase == Verzik.Phase.PHASE2
                        && verzik.verzikRedPhase) {
                    text = text + verzik.getVerzikTicksUntilAttack();
                    if (config.showVerzikTotalTicks())
                        text = text + " : ";
                }
                if (config.showVerzikTicks() && verzik.getVerzikSpecial() != Verzik.SpecialAttack.WEBS && verzik.verzikPhase == Verzik.Phase.PHASE3) {
                    text = text + verzik.getVerzikTicksUntilAttack();
                    if (config.showVerzikTotalTicks())
                        text = text + " : ";
                }
                if (config.showVerzikTotalTicks()) {
                    text = text + "(" + verzik.getVerzikTotalTicksUntilAttack() + ")";
                }
                Point canvasPoint = verzik.getVerzikNPC().getCanvasTextLocation(graphics, text, 60);
                if (canvasPoint != null) {
                    Color col = verzik.verzikSpecialWarningColor();
                    if (config.fontStyle()) {
                        renderTextLocation(graphics, text, col, canvasPoint);
                    } else {
                        renderResizeTextLocation(graphics, text, 15, Font.BOLD, col, canvasPoint);
                    }
                }
            }

            if (config.lightningInfobox() != SpoonTobConfig.lightningMode.OFF) {
                if ((config.lightningInfobox() == SpoonTobConfig.lightningMode.OVERLAY || config.lightningInfobox() == SpoonTobConfig.lightningMode.BOTH)
                        && verzik.verzikPhase == Verzik.Phase.PHASE2) {
                    String zapText;
                    if (verzik.lightningAttacks > 0) {
                        zapText = Integer.toString(verzik.lightningAttacks);
                    } else {
                        zapText = "ZAP";
                    }
                    Point canvasPoint = verzik.getVerzikNPC().getCanvasTextLocation(graphics, zapText, 270);
                    if (canvasPoint != null && !verzik.getVerzikNPC().isDead()) {
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, zapText, Color.ORANGE, canvasPoint);
                        } else {
                            renderResizeTextLocation(graphics, zapText, 15, Font.BOLD, Color.ORANGE, canvasPoint);
                        }
                    }
                }
            }

            if (config.lightningAttackTick() && verzik.verzikPhase == Verzik.Phase.PHASE2) {
                for (Projectile p : verzik.getVerzikLightningProjectiles().keySet()) {
                    Player localPlayer = client.getLocalPlayer();
                    if (localPlayer != null && p.getInteracting() == localPlayer) {
                        int ticks = verzik.getVerzikLightningProjectiles().get(p);
                        String tickstring = String.valueOf(ticks);
                        Point point = Perspective.getCanvasTextLocation(client, graphics, localPlayer.getLocalLocation(), tickstring, config.zapOffset());
                        if (point != null) {
                            if (config.fontStyle()) {
                                renderTextLocation(graphics, tickstring, (ticks > 0 ? Color.WHITE : Color.ORANGE), point);
                            } else {
                                renderSteroidsTextLocation(graphics, tickstring, config.zapSize(), Font.BOLD, (ticks > 0 ? Color.WHITE : Color.ORANGE), point);
                            }
                        }
                    }
                }
            }

        }
        return null;
    }

    private void displayProjectiles(Graphics2D graphics) {
        for(Projectile p : client.getProjectiles()){
            Actor interacting = p.getInteracting();
            if (p.getId() == 1598 && interacting != null) {
                if(config.displayGreenBall() != SpoonTobConfig.greenBallMode.OFF) {
                    int size;
                    if (config.displayGreenBall() == SpoonTobConfig.greenBallMode.TILE) {
                        size = 1;
                    } else {
                        size = 3;
                    }
                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, interacting.getLocalLocation(), size);
                    renderPolygon(graphics, tilePoly, Color.GREEN);
                }

                if (config.displayGreenBallTicks()) {
                    String text = String.valueOf(p.getRemainingCycles() / 30);
                    LocalPoint lp = interacting.getLocalLocation();
                    Point point = Perspective.getCanvasTextLocation(client, graphics, lp, text, config.greenBallOffset());
                    Color color = Color.RED;
                    if (config.fontStyle()) {
                        renderTextLocation(graphics, text, color, point);
                    } else {
                        renderSteroidsTextLocation(graphics, text, config.greenBallSize(), Font.BOLD, color, point);
                    }
                }
            }
        }
    }

    private void displayPurpleCrabAOE(Graphics2D graphics, NPC npc) {
        if (config.purpleAoe() && Verzik.P2_IDS.contains(npc.getId()) && verzik.getPurpleCrabProjectile().size() > 0) {
            verzik.getPurpleCrabProjectile().forEach((point, ticks) -> {
                Point textLocation = Perspective.getCanvasTextLocation(client, graphics, point, "#", 0);
                if (config.fontStyle()){
                    renderTextLocation(graphics, Integer.toString(ticks), Color.WHITE, textLocation);
                } else {
                    renderSteroidsTextLocation(graphics, Integer.toString(ticks),13, Font.BOLD, Color.WHITE, textLocation);
                }
                Polygon tileAreaPoly = Perspective.getCanvasTileAreaPoly(client, point, 3);
                renderPolygon(graphics, tileAreaPoly, new Color(106, 61, 255));
            });
        }
    }

    protected void renderPolygon(Graphics2D graphics, @Nullable Shape polygon, @Nonnull Color color) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
            graphics.fill(polygon);
        }
    }

    private void renderTargetOverlay(Graphics2D graphics, NPC actor, Color color){
        Shape objectClickbox = actor.getConvexHull();

        if (objectClickbox != null){
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            graphics.fill(actor.getConvexHull());
        }
    }
}