package net.runelite.client.plugins.spoontob.rooms.Sotetseg;

import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Random;

public class SotetsegOverlay extends RoomOverlay {
    @Inject
    private Sotetseg sotetseg;
    @Inject
    private SkillIconManager iconManager;

    @Inject
    protected SotetsegOverlay(SpoonTobConfig config) {
        super(config);
    }

    public Dimension render(Graphics2D graphics) {
        if (this.sotetseg.isSotetsegActive()) {
            this.displaySotetsegCounters(graphics);

            if (config.sotetsegMaze()) {
                int counter = 1;
                for (Point p : sotetseg.getRedTiles())
                {
                    WorldPoint wp = sotetseg.worldPointFromMazePoint(p);
                    drawTile(graphics, wp, Color.GREEN, 1, 255, 0);
                    LocalPoint lp = LocalPoint.fromWorld(client, wp);
                    if (lp != null && !sotetseg.isWasInUnderWorld())
                    {
                        Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(counter), 0);
                        if (textPoint != null)
                        {
                            renderTextLocation(graphics, String.valueOf(counter), Color.GREEN, textPoint);
                        }
                    }
                    counter++;
                }

                for (Point p : sotetseg.getGreenTiles())
                {
                    WorldPoint wp = sotetseg.worldPointFromMazePoint(p);
                    drawTile(graphics, wp, Color.GREEN, 1, 255, 0);
                }
            }

            if (config.sotetsegShowOrbs() != SpoonTobConfig.soteOrbMode.OFF || config.sotetsegShowNuke() != SpoonTobConfig.soteDeathballOverlayMode.OFF) {
                for (Projectile p : client.getProjectiles()) {
                    int id = p.getId();
                    Point point = Perspective.localToCanvas(client, new LocalPoint((int)p.getX(), (int)p.getY()), 0, Perspective.getTileHeight(client, new LocalPoint((int)p.getX(), (int)p.getY()), p.getFloor()) - (int)p.getZ());
                    String ticks = String.valueOf(p.getRemainingCycles() / 30);

                    if (point != null) {
                        if (config.sotetsegShowOrbs() == SpoonTobConfig.soteOrbMode.HATS || config.sotetsegShowOrbs() == SpoonTobConfig.soteOrbMode.BOTH) {
                            BufferedImage icon;

                            if (id == Sotetseg.SOTETSEG_MAGE_ORB && p.getInteracting() == client.getLocalPlayer()) {
                                if (config.raveHats() == SpoonTobConfig.raveHatsMode.RAVE || config.raveHats() == SpoonTobConfig.raveHatsMode.TURBO) {
                                    icon = ImageUtil.loadImageResource(SpoonTobPlugin.class, "magic" + sotetseg.mageHatNum + ".png");
                                } else if (config.raveHats() == SpoonTobConfig.raveHatsMode.EPILEPSY) {
                                    icon = ImageUtil.loadImageResource(SpoonTobPlugin.class, "magic" + (new Random().nextInt(8) + 1) + ".png");
                                } else {
                                    icon = sotetseg.mageIcon;
                                }

                                Point iconlocation = new Point(point.getX() - icon.getWidth() / 2, point.getY() - 30);

                                if (config.raveHats() == SpoonTobConfig.raveHatsMode.TURBO) {
                                    graphics.drawImage(icon, iconlocation.getX(), iconlocation.getY(), sotetseg.turboHatWidth, sotetseg.turboHatHeight, null);
                                } else {
                                    OverlayUtil.renderImageLocation(graphics, iconlocation, icon);
                                }

                                if (p.getInteracting() == client.getLocalPlayer()) {
                                    OverlayUtil.renderImageLocation(graphics, iconlocation, icon);
                                }
                            }

                            if (id == Sotetseg.SOTETSEG_RANGE_ORB && p.getInteracting() == client.getLocalPlayer()) {
                                if (config.raveHats() == SpoonTobConfig.raveHatsMode.RAVE || config.raveHats() == SpoonTobConfig.raveHatsMode.TURBO) {
                                    icon = ImageUtil.loadImageResource(SpoonTobPlugin.class, "ranged" + sotetseg.rangeHatNum + ".png");
                                } else if (config.raveHats() == SpoonTobConfig.raveHatsMode.EPILEPSY) {
                                    icon = ImageUtil.loadImageResource(SpoonTobPlugin.class, "ranged" + (new Random().nextInt(8) + 1) + ".png");
                                } else {
                                    icon = sotetseg.rangeIcon;
                                }

                                Point iconlocation = new Point(point.getX() - icon.getWidth() / 2, point.getY() - 30);

                                if (config.raveHats() == SpoonTobConfig.raveHatsMode.TURBO) {
                                    graphics.drawImage(icon, iconlocation.getX(), iconlocation.getY(), sotetseg.turboHatWidth, sotetseg.turboHatHeight, null);
                                } else {
                                    OverlayUtil.renderImageLocation(graphics, iconlocation, icon);
                                }

                                if (p.getInteracting() == client.getLocalPlayer()) {
                                    OverlayUtil.renderImageLocation(graphics, iconlocation, icon);
                                }

                            }
                        }

                        if ((p.getInteracting() == client.getLocalPlayer()) && (id == Sotetseg.SOTETSEG_MAGE_ORB || id == Sotetseg.SOTETSEG_RANGE_ORB)
                                && (config.sotetsegShowOrbs() == SpoonTobConfig.soteOrbMode.TICKS || config.sotetsegShowOrbs() == SpoonTobConfig.soteOrbMode.BOTH)) {
                            if (config.fontStyle()) {
                                renderTextLocation(graphics, ticks, (id == Sotetseg.SOTETSEG_MAGE_ORB ? Color.CYAN : Color.GREEN), point);
                            } else {
                                renderSteroidsTextLocation(graphics, ticks, 17, Font.BOLD, (id == Sotetseg.SOTETSEG_MAGE_ORB ? Color.CYAN : Color.GREEN), point);
                            }
                        }

                        if (id == Sotetseg.SOTETSEG_BIG_AOE_ORB && (config.sotetsegShowNuke() == SpoonTobConfig.soteDeathballOverlayMode.TICKS
                                || config.sotetsegShowNuke() == SpoonTobConfig.soteDeathballOverlayMode.BOTH)) {
                            Color color = Color.ORANGE;
                            if (config.deathTicksOnPlayer()) {
                                point = Perspective.getCanvasTextLocation(client, graphics, p.getInteracting().getLocalLocation(), ticks, config.deathballOffset());

                                if (config.fontStyle()) {
                                    renderTextLocation(graphics, ticks, Color.WHITE, point);
                                } else {
                                    renderSteroidsTextLocation(graphics, ticks, config.deathballSize(), Font.BOLD, color, point);
                                }
                            } else {
                                if (config.fontStyle()) {
                                    renderTextLocation(graphics, ticks, color, point);
                                } else {
                                    renderSteroidsTextLocation(graphics, ticks, 20, Font.BOLD, color, point);
                                }
                            }
                            if (config.displayDeathBall()) {
                                renderPoly(graphics, config.displayDeathBallColor(), p.getInteracting().getCanvasTilePoly());
                            }
                            Point imagelocation = new Point(point.getX() - Sotetseg.TACTICAL_NUKE_OVERHEAD.getWidth() / 2, point.getY() - 60);
                            if (config.sotetsegShowNuke() == SpoonTobConfig.soteDeathballOverlayMode.NUKE || config.sotetsegShowNuke() == SpoonTobConfig.soteDeathballOverlayMode.BOTH) {
                                OverlayUtil.renderImageLocation(graphics, imagelocation, Sotetseg.TACTICAL_NUKE_OVERHEAD);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void displaySotetsegCounters(Graphics2D graphics) {
        if (this.sotetseg.sotetsegTicks > 0 && sotetseg.sotetsegNPC != null) {
            String text = "";
            String yuriText = "";
            if(config.deathballInfobox() == SpoonTobConfig.soteDeathballMode.OVERLAY || config.deathballInfobox() == SpoonTobConfig.soteDeathballMode.BOTH){
                if (config.deathballSingleLine()) {
                    if (sotetseg.sotetsegAttacksLeft == 0) {
                        text += "Nuke";
                    } else {
                        text += sotetseg.sotetsegAttacksLeft;
                    }
                } else {
                    if (sotetseg.sotetsegAttacksLeft == 0) {
                        yuriText = "Nuke";
                    } else {
                        yuriText = String.valueOf(sotetseg.sotetsegAttacksLeft);
                    }
                }
            }

            if(config.showSotetsegAttackTicks()){
                if(text.equals("")){
                    text += this.getSotetsegTicksString();
                }else {
                    text += " : " + this.getSotetsegTicksString();
                }
            }

            Point textLocation = sotetseg.sotetsegNPC.getCanvasTextLocation(graphics, text, 50);
            if (config.fontStyle()) {
                this.renderTextLocation(graphics, text, Color.WHITE, textLocation);
                if(!config.deathballSingleLine() && !yuriText.equals("")) {
                    Point yuriTextLocation = sotetseg.sotetsegNPC.getCanvasTextLocation(graphics, yuriText, 200);
                    renderTextLocation(graphics, yuriText, Color.ORANGE, yuriTextLocation);
                }
            } else {
                renderResizeTextLocation(graphics, text, 14, Font.BOLD, Color.WHITE, textLocation);
                if(!config.deathballSingleLine() && !yuriText.equals("")) {
                    Point yuriTextLocation = sotetseg.sotetsegNPC.getCanvasTextLocation(graphics, yuriText, 200);
                    renderResizeTextLocation(graphics, yuriText, 14, Font.BOLD, Color.ORANGE, yuriTextLocation);
                }
            }
        }
    }

    private String getSotetsegTicksString() {
        return Byte.toString(this.sotetseg.getSotetsegTicks());
    }

    public static BufferedImage fadeImage(Image img, float fade, float target) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage bi = new BufferedImage(w, h, 2);
        Graphics2D g = bi.createGraphics();
        g.drawImage(img, 0, 0, null);
        float offset = target * (1.0F - fade);
        float[] scales = new float[]{fade, fade, fade, 1.0F};
        float[] offsets = new float[]{offset, offset, offset, 0.0F};
        RescaleOp rop = new RescaleOp(scales, offsets, (RenderingHints)null);
        g.drawImage(bi, rop, 0, 0);
        g.dispose();
        return bi;
    }

    public static void renderPolygon(Graphics2D graphics, Shape poly, Color color, Color color2, int width) {
        graphics.setColor(color);
        Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke((float)width));
        graphics.draw(poly);
        graphics.setColor(color2);
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }
}