package net.runelite.client.plugins.spoontob.rooms.Maiden;

import net.runelite.api.Client;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.plugins.spoontob.util.TheatreRegions;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class MaidenRedsOverlay extends RoomOverlay {
    @Inject
    private SpoonTobPlugin plugin;

    @Inject
    private SpoonTobConfig config;

    @Inject
    private Client client;

    @Inject
    private Maiden maiden;

    @Inject
    public MaidenRedsOverlay(Client client, SpoonTobConfig config, SpoonTobPlugin plugin) {
        super(config);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if ((config.redsTL() != SpoonTobConfig.redsTlMode.OFF || config.redsFreezeWarning()) && plugin.enforceRegion()) {
            boolean canFreeze = true;
            boolean n1Spawned = false;
            boolean n2Spawned = false;
            boolean threeSpawned = false;
            for (MaidenCrabInfo mci : maiden.maidenCrabInfoList) {
                if (maiden.maidenPhase == mci.phase) {
                    switch (mci.position) {
                        case "N1":
                            n1Spawned = true;
                            break;
                        case "N2":
                            n2Spawned = true;
                            break;
                        case "N3":
                        case "S3":
                            threeSpawned = true;
                            break;
                    }
                }
            }

            for (MaidenCrabInfo mci : maiden.maidenCrabInfoList) {
                NPCComposition composition = mci.crab.getComposition();
                if(composition != null) {
                    int size = composition.getSize();
                    LocalPoint lp = LocalPoint.fromWorld(client, mci.crab.getWorldLocation());
                    if (lp != null) {
                        lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
                        if (tilePoly != null) {
                            String spawn = mci.position;
                            WorldPoint maidenWp = maiden.getMaidenNPC().getWorldLocation();
                            int maidenX = maidenWp.getX();
                            NPCComposition maidenModel = maiden.getMaidenNPC().getTransformedComposition();
                            if (maidenModel != null) {
                                maidenX += maidenModel.getSize();
                            }
                            WorldPoint healerWp = mci.crab.getWorldLocation();
                            int healerX = healerWp.getX();
                            int deltaX = Math.max(0, healerX - maidenX);

                            if (threeSpawned) {
                                if (n1Spawned && n2Spawned) {
                                    if (spawn.equals("N1")) {
                                        if (deltaX < 4 && mci.frozenTicks > 0) {
                                            canFreeze = false;
                                        }
                                    } else if (spawn.equals("N2")) {
                                        if (deltaX < 3) {
                                            canFreeze = mci.frozenTicks <= 0;
                                        }
                                    }
                                } else if (n1Spawned) {
                                    if (spawn.equals("N1")) {
                                        if (deltaX < 1 && mci.frozenTicks > 0) {
                                            canFreeze = false;
                                        }
                                    }
                                } else if (n2Spawned) {
                                    if (spawn.equals("N2")) {
                                        if (deltaX < 3 && mci.frozenTicks > 0) {
                                            canFreeze = false;
                                        }
                                    }
                                }
                            }

                            if (!canFreeze && (client.getVarbitValue(4070) == 1 && SpoonTobPlugin.partySize > 3) && config.redsFreezeWarning()
                                    && (mci.position.equals("N3") || mci.position.equals("S3")) && mci.phase == maiden.maidenPhase && mci.frozenTicks == -1) {
                                renderPoly(graphics, tilePoly, config.redsFreezeWarningColor(), config.redsFreezeWarningColor().getAlpha(), 50);
                            } else {
                                if ((config.redsTL() == SpoonTobConfig.redsTlMode.MAIDEN || config.redsTL() == SpoonTobConfig.redsTlMode.BOTH) && TheatreRegions.inRegion(client, TheatreRegions.MAIDEN)) {
                                    renderPoly(graphics, tilePoly, config.redsTLColor(), config.redsTLColor().getAlpha(), 0);
                                }
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