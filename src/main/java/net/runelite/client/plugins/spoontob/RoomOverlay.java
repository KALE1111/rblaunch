package net.runelite.client.plugins.spoontob;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.*;

public abstract class RoomOverlay extends Overlay {
    protected final SpoonTobConfig config;
    @Inject
    protected Client client;

    @Inject
    protected RoomOverlay(SpoonTobConfig config) {
        this.config = config;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(OverlayPriority.HIGH);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    protected void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha) {
        WorldPoint playerLocation = this.client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) < 32) {
            LocalPoint lp = LocalPoint.fromWorld(this.client, point);
            if (lp != null) {
                Polygon poly = Perspective.getCanvasTilePoly(this.client, lp);
                if (poly != null) {
                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
                    graphics.setStroke(new BasicStroke((float)strokeWidth));
                    graphics.draw(poly);
                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
                    graphics.fill(poly);
                }
            }
        }
    }

    protected void drawTL(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha, NPC npc) {
        WorldPoint playerLocation = this.client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) < 32) {
            int size = 1;
            NPCComposition composition = npc.getTransformedComposition();
            if (composition != null)
                size = composition.getSize();
            LocalPoint lp = LocalPoint.fromWorld(this.client, point);
            if (lp != null) {
                lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                if (tilePoly != null) {
                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
                    graphics.setStroke(new BasicStroke((float)strokeWidth));
                    graphics.draw(tilePoly);
                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
                    graphics.fill(tilePoly);
                }
            }
        }
    }

    protected void renderNpcOverlay(Graphics2D graphics, NPC actor, Color color, int outlineWidth, int outlineAlpha, int fillAlpha) {
        int size = 1;
        NPCComposition composition = actor.getTransformedComposition();
        if (composition != null) {
            size = composition.getSize();
        }

        LocalPoint lp = actor.getLocalLocation();
        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);

        if (tilePoly != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
            graphics.setStroke(new BasicStroke((float)outlineWidth));
            graphics.draw(tilePoly);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
            graphics.fill(tilePoly);
        }
    }

    protected void renderNpcTLOverlay(Graphics2D graphics, NPC actor, Color color, int outlineWidth, int outlineAlpha, int fillAlpha) {
        int size = 1;
        NPCComposition composition = actor.getTransformedComposition();
        if (composition != null) {
            size = composition.getSize();
        }
        LocalPoint lp = LocalPoint.fromWorld(this.client, actor.getWorldLocation());
        if (lp != null) {
            lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
            graphics.setStroke(new BasicStroke((float)outlineWidth));
            graphics.draw(tilePoly);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
            graphics.fill(tilePoly);
        }
    }

    protected void renderTextLocation(Graphics2D graphics, String txtString, Color fontColor, Point canvasPoint) {
        if (canvasPoint != null) {
            Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
            Point canvasCenterPoint_shadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    protected void renderSteroidsTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint) {
        graphics.setFont(new Font(FontManager.getRunescapeSmallFont().toString(), fontStyle, fontSize));
        if (canvasPoint != null) {
            Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
            Point canvasCenterPointShadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPointShadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    protected void renderResizeTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint) {
        if (config.resizeFont()) {
            graphics.setFont(new Font(FontManager.getRunescapeSmallFont().toString(), config.fontWeight().getFont(), config.tobFontSize()));
        } else {
            graphics.setFont(new Font(FontManager.getRunescapeSmallFont().toString(), fontStyle, fontSize));
        }
        if (canvasPoint != null) {
            Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
            Point canvasCenterPointShadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPointShadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    protected void renderPoly(Graphics2D graphics, Color color, Polygon polygon) {
        this.renderPoly(graphics, color, polygon, 2);
    }

    protected void renderPoly(Graphics2D graphics, Color color, Polygon polygon, double width) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke((float)width));
            graphics.draw(polygon);
        }
    }

    protected void renderNpcPoly(Graphics2D graphics, Color color, Polygon polygon, double width, int alpha) {
        if (polygon != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            graphics.setStroke(new BasicStroke((float)width));
            graphics.draw(polygon);
        }
    }

    /**
     * Returns a polygon representing an area.
     *
     * @param client the game client
     * @param localLocation the center location of the AoE
     * @param size the size of the area (ie. 3x3 AoE evaluates to size 3)
     * @return a polygon representing the tiles in the area
     */
    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size)
    {
        return getCanvasTileAreaPoly(client, localLocation, size,0,  true);
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size, int borderOffset)
    {
        return getCanvasTileAreaPoly(client, localLocation, size,borderOffset,  true);
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size, boolean centered)
    {
        return getCanvasTileAreaPoly(client, localLocation, size,0,  centered);
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size, int borderOffset, boolean centered) {
        int plane = client.getPlane();
        int swX;
        int swY;
        int neX;
        int neY;
        if (centered) {
            swX = localLocation.getX() - size * (128 + borderOffset) / 2;
            swY = localLocation.getY() - size * (128 + borderOffset) / 2;
            neX = localLocation.getX() + size * (128 + borderOffset) / 2;
            neY = localLocation.getY() + size * (128 + borderOffset) / 2;
        } else {
            swX = localLocation.getX() - (128 + borderOffset) / 2;
            swY = localLocation.getY() - (128 + borderOffset) / 2;
            neX = localLocation.getX() - (128 + borderOffset) / 2 + size * (128 + borderOffset);
            neY = localLocation.getY() - (128 + borderOffset) / 2 + size * (128 + borderOffset);
        }

        byte[][][] tileSettings = client.getTileSettings();
        int sceneX = localLocation.getSceneX();
        int sceneY = localLocation.getSceneY();
        if (sceneX >= 0 && sceneY >= 0 && sceneX < 104 && sceneY < 104) {
            int tilePlane = plane;
            if (plane < 3 && (tileSettings[1][sceneX][sceneY] & 2) == 2) {
                tilePlane = plane + 1;
            }

            int swHeight = getHeight(client, swX, swY, tilePlane);
            int nwHeight = getHeight(client, neX, swY, tilePlane);
            int neHeight = getHeight(client, neX, neY, tilePlane);
            int seHeight = getHeight(client, swX, neY, tilePlane);
            Point p1 = Perspective.localToCanvas(client, swX, swY, swHeight);
            Point p2 = Perspective.localToCanvas(client, neX, swY, nwHeight);
            Point p3 = Perspective.localToCanvas(client, neX, neY, neHeight);
            Point p4 = Perspective.localToCanvas(client, swX, neY, seHeight);
            if (p1 != null && p2 != null && p3 != null && p4 != null) {
                Polygon poly = new Polygon();
                poly.addPoint(p1.getX(), p1.getY());
                poly.addPoint(p2.getX(), p2.getY());
                poly.addPoint(p3.getX(), p3.getY());
                poly.addPoint(p4.getX(), p4.getY());
                return poly;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static int getHeight(@Nonnull Client client, int localX, int localY, int plane) {
        int sceneX = localX >> 7;
        int sceneY = localY >> 7;
        if (sceneX >= 0 && sceneY >= 0 && sceneX < 104 && sceneY < 104) {
            int[][][] tileHeights = client.getTileHeights();
            int x = localX & 127;
            int y = localY & 127;
            int var8 = x * tileHeights[plane][sceneX + 1][sceneY] + (128 - x) * tileHeights[plane][sceneX][sceneY] >> 7;
            int var9 = tileHeights[plane][sceneX][sceneY + 1] * (128 - x) + x * tileHeights[plane][sceneX + 1][sceneY + 1] >> 7;
            return (128 - y) * var8 + y * var9 >> 7;
        } else {
            return 0;
        }
    }
}