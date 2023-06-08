package net.runelite.client.plugins.toa;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Inject;

import net.runelite.client.plugins.toa.Util.GUIOverlayUtil;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import static net.runelite.api.Constants.TILE_FLAG_BRIDGE;

public abstract class RoomOverlay extends Overlay
{
    @Inject
    protected Client client;
    protected final ToaConfig config;
    protected final ModelOutlineRenderer outliner;
	private boolean clearChildren = true;
	protected final PanelComponent panelComponent = new PanelComponent(); public PanelComponent getPanelComponent() { return this.panelComponent; }

    @Inject
    protected RoomOverlay(ToaConfig config, ModelOutlineRenderer outliner)
    {
        this.config = config;
        this.outliner = outliner;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    protected void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha)
    {
        GUIOverlayUtil.drawTiles(graphics, client, point, client.getLocalPlayer().getWorldLocation(), color, strokeWidth, outlineAlpha, fillAlpha);
    }

	protected void renderTextLocation(Graphics2D graphics, String txtString, Color fontColor, Point canvasPoint) {
		if (canvasPoint != null) {
			Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
			Point canvasCenterPoint_shadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
		}
	}


    protected void renderPoly(Graphics2D graphics, Color outlineColor, Color fillColor, Shape polygon, double width, boolean antiAlias)
    {
        if (polygon != null)
        {
            if (antiAlias)
            {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            else
            {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            graphics.setColor(outlineColor);
            graphics.setStroke(new BasicStroke((float) width));
            graphics.draw(polygon);
            graphics.setColor(fillColor);
            graphics.fill(polygon);
        }
    }

    protected void renderDangerTile(Graphics2D graphics, WorldPoint point){
        Color outlineColor = config.dangerTileColorToa();
        Color fillColor = config.dangerTileFillColorToa();
        int size = 1;

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp != null)
        {
            lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
            if (tilePoly != null)
            {
                renderPoly(graphics, outlineColor, fillColor, tilePoly, 2, true);
            }
        }
    }

    protected void renderDangerTile(Graphics2D graphics, LocalPoint lp){
        Color outlineColor = config.dangerTileColorToa();
        Color fillColor = config.dangerTileFillColorToa();
        int size = 1;

        if (lp != null)
        {
            lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
            if (tilePoly != null)
            {
                renderPoly(graphics, outlineColor, fillColor, tilePoly, 2, true);
            }
        }
    }

	protected void renderColorTile(Graphics2D graphics, LocalPoint lp, Color outline, Color fill){
		Color outlineColor = outline;
		Color fillColor = fill;
		int size = 1;

		if (lp != null)
		{
			lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
			Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
			if (tilePoly != null)
			{
				renderPoly(graphics, outlineColor, fillColor, tilePoly, 2, true);
			}
		}
	}




    protected void renderTrueTile(Graphics2D graphics, NPC npc, int size){
        Color outlineColor = config.trueTileColorToa();
        Color fillColor = config.trueTileFillColorToa();

        LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldLocation());
        if (lp != null)
        {
            lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
            if (tilePoly != null)
            {
                renderPoly(graphics, outlineColor, fillColor, tilePoly, 2, true);
            }
        }
    }

    protected void renderTrueTile(Graphics2D graphics, NPC npc, int size, Color color){
        Color outlineColor = color;
        Color fillColor = config.trueTileFillColorToa();

        LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldLocation());
        if (lp != null)
        {
            lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
            if (tilePoly != null)
            {
                renderPoly(graphics, outlineColor, fillColor, tilePoly, 2, true);
            }
        }
    }

    protected Collection<GameObject> getGameObjects(){
        Collection<GameObject> objects = new ArrayList<>();

        Tile[][] tiles = client.getScene().getTiles()[client.getPlane()];
        for (Tile[] lineOfTiles : tiles)
        {
            for (Tile tile : lineOfTiles)
            {
                if (tile != null)
                {
                    GameObject[] gameObjects = tile.getGameObjects();
                    if (gameObjects != null)
                    {
                        objects.addAll(Arrays.asList(gameObjects));
                    }
                }
            }
        }

        return objects;
    }

    protected Collection<GroundObject> getGroundObjects(){
        Collection<GroundObject> objects = new ArrayList<>();

        Tile[][] tiles = client.getScene().getTiles()[client.getPlane()];
        for (Tile[] lineOfTiles : tiles)
        {
            for (Tile tile : lineOfTiles)
            {
                if (tile != null)
                {
                    GroundObject groundObject = tile.getGroundObject();
                    if (groundObject != null)
                    {
                        objects.add(groundObject);
                    }
                }
            }
        }

        return objects;
    }

    protected void renderAreaTilePolygon(Graphics2D graphics, Shape poly, Color color,int alpha)
    {
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        graphics.fill(poly);
    }


    protected boolean edgeEqualsEdge(int[][] edge1, int[][] edge2, int toleranceSquared)
    {
        return (pointEqualsPoint(edge1[0], edge2[0], toleranceSquared) && pointEqualsPoint(edge1[1], edge2[1], toleranceSquared))
                || (pointEqualsPoint(edge1[0], edge2[1], toleranceSquared) && pointEqualsPoint(edge1[1], edge2[0], toleranceSquared));
    }

    protected boolean pointEqualsPoint(int[] point1, int[] point2, int toleranceSquared)
    {
        double distanceSquared = Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2);

        return distanceSquared <= toleranceSquared;
    }

    protected void renderFullLine(Graphics2D graphics, int[][] line, Color color)
    {
        graphics.setColor(color);
        final Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(2));
        graphics.drawLine(line[0][0], line[0][1], line[1][0], line[1][1]);
        graphics.setStroke(originalStroke);
    }
}