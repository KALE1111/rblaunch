package net.runelite.client.plugins.toa.Util;

import com.google.common.base.Strings;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.VarClientInt;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.annotation.Nullable;

@Slf4j
public class GUIOverlayUtil
{
    private static final int MAX_DRAW_DISTANCE = 32;

    public static Rectangle renderPrayerOverlay(Graphics2D graphics, Client client, Prayer prayer, Color color)
    {
        Widget widget = client.getWidget(prayer.getWidgetInfo().getPackedId());

        if (widget == null || client.getVarcIntValue(VarClientInt.INVENTORY_TAB) != InterfaceTab.PRAYER.getId())
        {
            return null;
        }

        Rectangle bounds = widget.getBounds();
        OverlayUtil.renderPolygon(graphics, rectangleToPolygon(bounds), color);
        return bounds;
    }

    private static Polygon rectangleToPolygon(Rectangle rect)
    {
        int[] xpoints = {rect.x, rect.x + rect.width, rect.x + rect.width, rect.x};
        int[] ypoints = {rect.y, rect.y, rect.y + rect.height, rect.y + rect.height};

        return new Polygon(xpoints, ypoints, 4);
    }

    public static void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint, boolean shadows, int yOffset)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final Point canvasCenterPoint = new Point(
                    canvasPoint.getX(),
                    canvasPoint.getY() + yOffset);
            final Point canvasCenterPoint_shadow = new Point(
                    canvasPoint.getX() + 1,
                    canvasPoint.getY() + 1 + yOffset);
            if (shadows)
            {
                renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            }
            renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }


    public static void renderTextLocation(Graphics2D graphics, Point txtLoc, String text, Color color)
    {
        if (Strings.isNullOrEmpty(text))
        {
            return;
        }

        int x = txtLoc.getX();
        int y = txtLoc.getY();

        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);

        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }

    public static void renderTextLocation(Graphics2D graphics, Point txtLoc, String text, Color color, Supplier outline)
    {
        if (txtLoc != null && !Strings.isNullOrEmpty(text))
        {
            int x = txtLoc.getX();
            int y = txtLoc.getY();
            graphics.setColor(Color.BLACK);
            if ((Boolean) outline.get())
            {
                graphics.drawString(text, x, y + 1);
                graphics.drawString(text, x, y - 1);
                graphics.drawString(text, x + 1, y);
                graphics.drawString(text, x - 1, y);
            }
            else
            {
                graphics.drawString(text, x + 1, y + 1);
            }

            graphics.setColor(color);
            graphics.drawString(text, x, y);
        }
    }


    public static List<WorldPoint> getHitSquares(WorldPoint npcLoc, int npcSize, int thickness, boolean includeUnder)
    {
        List<WorldPoint> little = new WorldArea(npcLoc, npcSize, npcSize).toWorldPointList();
        List<WorldPoint> big = new WorldArea(npcLoc.getX() - thickness, npcLoc.getY() - thickness, npcSize + (thickness * 2), npcSize + (thickness * 2), npcLoc.getPlane()).toWorldPointList();
        if (!includeUnder)
        {
            big.removeIf(little::contains);
        }
        return big;
    }

    public static void drawTiles(Graphics2D graphics, Client client, WorldPoint point, WorldPoint playerPoint, Color color, int strokeWidth, int outlineAlpha, int fillAlpha)
    {
        if (point.distanceTo(playerPoint) >= 32)
        {
            return;
        }
        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
        {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
        {
            return;
        }
        drawStrokeAndFillPoly(graphics, color, strokeWidth, outlineAlpha, fillAlpha, poly);
    }

    public static void drawStrokeAndFillPoly(Graphics2D graphics, Color color, int strokeWidth, int outlineAlpha, int fillAlpha, Polygon poly)
    {
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
        graphics.fill(poly);
    }

    public static void renderPolygon(Graphics2D graphics, Shape polygon, Color color, int outlineStroke, int fillAlpha)
    {
        if (polygon != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke((float) outlineStroke));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
            graphics.fill(polygon);
        }
    }

    public static void renderWidgetPolygon(Graphics2D graphics, Widget widget, Color color, boolean outlineOnly, boolean flash)
    {
        if (widget != null)
        {
            Rectangle widgetBounds = widget.getBounds();
            if (widgetBounds != null)
            {
                if (flash)
                {
                    graphics.setComposite(AlphaComposite.getInstance(3, getAlphaTime()));
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                }

                if (outlineOnly)
                {
                    graphics.setColor(color);
                    graphics.setStroke(new BasicStroke(1.0F));
                    graphics.draw(widgetBounds);
                }
                else
                {
                    graphics.setColor(color);
                    graphics.fill(widgetBounds);
                }

            }
        }
    }

    public static void renderFilledPolygon(Graphics2D graphics, Shape poly, Color color)
    {
        graphics.setColor(color);
        final Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(2));
        graphics.draw(poly);
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }

    public static void drawTile(Graphics2D graphics, WorldPoint point, Client client, Color color, @Nullable String label, Stroke borderStroke)
    {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (point.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE)
        {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
        {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly != null)
        {
            OverlayUtil.renderPolygon(graphics, poly, color, borderStroke);
        }

        if (!Strings.isNullOrEmpty(label))
        {
            Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
            if (canvasTextLocation != null)
            {
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
            }
        }
    }

    public static float getAlphaTime()
    {
        return (float) Math.abs(System.currentTimeMillis() % 2000L - 1000L) / 1000.0F;
    }
}