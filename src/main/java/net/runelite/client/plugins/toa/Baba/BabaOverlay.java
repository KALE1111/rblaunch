package net.runelite.client.plugins.toa.Baba;

import net.runelite.api.Point;
import net.runelite.client.plugins.toa.Kephri.KephriDangerTile;
import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Akkha.Akkha;
import net.runelite.client.plugins.toa.RoomOverlay;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.toa.Warden.WardenDangerTile;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.apache.commons.lang3.ArrayUtils;
//import org.graalvm.compiler.nodes.calc.ObjectEqualsNode;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class BabaOverlay extends RoomOverlay {

    @Inject
    private Baba baba;

    @Inject
    protected BabaOverlay(ToaConfig config, ModelOutlineRenderer outliner)
    {
        super(config, outliner);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (baba.isBabaActive()){
            if (config.bouldersTrueTileBaba()) {
                renderBoulders(graphics);
            }

            if (config.bananasToa()){
                renderBananas(graphics);
            }

            if (config.boulderDangerToa()){
				renderElementsSequence(graphics);
            }

            if (config.shockwaveRadiusToa()){
                renderShockwave(graphics);
            }

            if (config.sacrophagusToa()){
                renderSacrophagus(graphics);
            }

            if (config.babaRenderTicks()){
            	renderBabaTicks(graphics);
			}
        }

        return null;
    }

    private void renderSacrophagus(Graphics2D graphics){
        for (Projectile p: client.getProjectiles()) {
            if (p.getId() == Baba.SACROPHAGUS_FLAME_ID){
                renderDangerTile(graphics,p.getTarget());
            }
        }
    }

    private void renderShockwave(Graphics2D graphics){
        Color colorFill = config.dangerTileFillColorToa();
        int alpha = 20;

        final List<int[][]> allEdges = new ArrayList<>();
        int edgeSizeSquared = 0;
        int shockwaveSetting = 0;

        List<LocalPoint> lpsFast = new ArrayList<>();

        for (GraphicsObject object : client.getGraphicsObjects()){
            if (object != null) {
                if (object.getId() == 1448 || object.getId() == 2111 || object.getId() == 1446 || object.getId() == 1447) {
                    if (shockwaveSetting == 0)
                        shockwaveSetting = 1;

                    if (object.getId() == 2111)
                        lpsFast.add(object.getLocation());

                    final Polygon tilePoly = Perspective.getCanvasTilePoly(client, object.getLocation());

                    if (tilePoly == null)
                    {
                        continue;
                    }

                    renderAreaTilePolygon(graphics, tilePoly, colorFill, alpha);

                    final int[][] edge1 = new int[][]{{tilePoly.xpoints[0], tilePoly.ypoints[0]}, {tilePoly.xpoints[1], tilePoly.ypoints[1]}};
                    edgeSizeSquared += Math.pow(tilePoly.xpoints[0] - tilePoly.xpoints[1], 2) + Math.pow(tilePoly.ypoints[0] - tilePoly.ypoints[1], 2);
                    allEdges.add(edge1);
                    final int[][] edge2 = new int[][]{{tilePoly.xpoints[1], tilePoly.ypoints[1]}, {tilePoly.xpoints[2], tilePoly.ypoints[2]}};
                    edgeSizeSquared += Math.pow(tilePoly.xpoints[1] - tilePoly.xpoints[2], 2) + Math.pow(tilePoly.ypoints[1] - tilePoly.ypoints[2], 2);
                    allEdges.add(edge2);
                    final int[][] edge3 = new int[][]{{tilePoly.xpoints[2], tilePoly.ypoints[2]}, {tilePoly.xpoints[3], tilePoly.ypoints[3]}};
                    edgeSizeSquared += Math.pow(tilePoly.xpoints[2] - tilePoly.xpoints[3], 2) + Math.pow(tilePoly.ypoints[2] - tilePoly.ypoints[3], 2);
                    allEdges.add(edge3);
                    final int[][] edge4 = new int[][]{{tilePoly.xpoints[3], tilePoly.ypoints[3]}, {tilePoly.xpoints[0], tilePoly.ypoints[0]}};
                    edgeSizeSquared += Math.pow(tilePoly.xpoints[3] - tilePoly.xpoints[0], 2) + Math.pow(tilePoly.ypoints[3] - tilePoly.ypoints[0], 2);
                    allEdges.add(edge4);

                }

                if (object.getId() == 1448){
                    shockwaveSetting = 2;
                    for ( LocalPoint lp : getSlowShockwaveLocalPoints(object.getLocation())) {
                        final Polygon tilePoly = Perspective.getCanvasTilePoly(client, lp);

                        if (tilePoly == null)
                        {
                            continue;
                        }

                        renderAreaTilePolygon(graphics, tilePoly, colorFill, alpha);

                        final int[][] edge1 = new int[][]{{tilePoly.xpoints[0], tilePoly.ypoints[0]}, {tilePoly.xpoints[1], tilePoly.ypoints[1]}};
                        edgeSizeSquared += Math.pow(tilePoly.xpoints[0] - tilePoly.xpoints[1], 2) + Math.pow(tilePoly.ypoints[0] - tilePoly.ypoints[1], 2);
                        allEdges.add(edge1);
                        final int[][] edge2 = new int[][]{{tilePoly.xpoints[1], tilePoly.ypoints[1]}, {tilePoly.xpoints[2], tilePoly.ypoints[2]}};
                        edgeSizeSquared += Math.pow(tilePoly.xpoints[1] - tilePoly.xpoints[2], 2) + Math.pow(tilePoly.ypoints[1] - tilePoly.ypoints[2], 2);
                        allEdges.add(edge2);
                        final int[][] edge3 = new int[][]{{tilePoly.xpoints[2], tilePoly.ypoints[2]}, {tilePoly.xpoints[3], tilePoly.ypoints[3]}};
                        edgeSizeSquared += Math.pow(tilePoly.xpoints[2] - tilePoly.xpoints[3], 2) + Math.pow(tilePoly.ypoints[2] - tilePoly.ypoints[3], 2);
                        allEdges.add(edge3);
                        final int[][] edge4 = new int[][]{{tilePoly.xpoints[3], tilePoly.ypoints[3]}, {tilePoly.xpoints[0], tilePoly.ypoints[0]}};
                        edgeSizeSquared += Math.pow(tilePoly.xpoints[3] - tilePoly.xpoints[0], 2) + Math.pow(tilePoly.ypoints[3] - tilePoly.ypoints[0], 2);
                        allEdges.add(edge4);
                    }
                }
            }
        }

        if (shockwaveSetting == 1){
            int xHigh = 0, xLow = 10000, yHigh = 0, yLow = 10000;
            for (LocalPoint lp : lpsFast){
                if (lp.getX() > xHigh)
                    xHigh = lp.getX();
                if (lp.getX() < xLow)
                    xLow = lp.getX();

                if (lp.getY() > yHigh)
                    yHigh = lp.getY();
                if (lp.getY() < yLow)
                    yLow = lp.getY();
            }

            LocalPoint center = new LocalPoint(xLow+((xHigh - xLow)/2), yLow + ((yHigh-yLow)/2));

            for ( LocalPoint lp : getFastShockwaveLocalPoints(center)) {
                final Polygon tilePoly = Perspective.getCanvasTilePoly(client, lp);

                if (tilePoly == null)
                {
                    continue;
                }

                renderAreaTilePolygon(graphics, tilePoly, colorFill, alpha);

                final int[][] edge1 = new int[][]{{tilePoly.xpoints[0], tilePoly.ypoints[0]}, {tilePoly.xpoints[1], tilePoly.ypoints[1]}};
                edgeSizeSquared += Math.pow(tilePoly.xpoints[0] - tilePoly.xpoints[1], 2) + Math.pow(tilePoly.ypoints[0] - tilePoly.ypoints[1], 2);
                allEdges.add(edge1);
                final int[][] edge2 = new int[][]{{tilePoly.xpoints[1], tilePoly.ypoints[1]}, {tilePoly.xpoints[2], tilePoly.ypoints[2]}};
                edgeSizeSquared += Math.pow(tilePoly.xpoints[1] - tilePoly.xpoints[2], 2) + Math.pow(tilePoly.ypoints[1] - tilePoly.ypoints[2], 2);
                allEdges.add(edge2);
                final int[][] edge3 = new int[][]{{tilePoly.xpoints[2], tilePoly.ypoints[2]}, {tilePoly.xpoints[3], tilePoly.ypoints[3]}};
                edgeSizeSquared += Math.pow(tilePoly.xpoints[2] - tilePoly.xpoints[3], 2) + Math.pow(tilePoly.ypoints[2] - tilePoly.ypoints[3], 2);
                allEdges.add(edge3);
                final int[][] edge4 = new int[][]{{tilePoly.xpoints[3], tilePoly.ypoints[3]}, {tilePoly.xpoints[0], tilePoly.ypoints[0]}};
                edgeSizeSquared += Math.pow(tilePoly.xpoints[3] - tilePoly.xpoints[0], 2) + Math.pow(tilePoly.ypoints[3] - tilePoly.ypoints[0], 2);
                allEdges.add(edge4);
            }
        }


        if (allEdges.size() <= 0)
        {
            return;
        }

        edgeSizeSquared /= allEdges.size();

        //Find and indicate unique edges
        final int toleranceSquared = (int) Math.ceil(edgeSizeSquared / 6);

        for (int i = 0; i < allEdges.size(); i++)
        {
            int[][] baseEdge = allEdges.get(i);

            boolean duplicate = false;

            for (int j = 0; j < allEdges.size(); j++)
            {
                if (i == j)
                {
                    continue;
                }

                int[][] checkEdge = allEdges.get(j);

                if (edgeEqualsEdge(baseEdge, checkEdge, toleranceSquared))
                {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate)
            {
                renderFullLine(graphics, baseEdge, Color.RED);
            }
        }
    }


    private List<LocalPoint> getSlowShockwaveLocalPoints(LocalPoint center){
        List<LocalPoint> lps = new ArrayList<>();

        for (int x = 1; x < 3; x++){
            for (int y = 1; y < 3; y++){
                lps.add(new LocalPoint(center.getX()+(x*128), center.getY()+(y*128)));
                lps.add(new LocalPoint(center.getX()-(x*128), center.getY()+(y*128)));
                lps.add(new LocalPoint(center.getX()+(x*128), center.getY()-(y*128)));
                lps.add(new LocalPoint(center.getX()-(x*128), center.getY()-(y*128)));
            }
        }
        return lps;
    }

    private List<LocalPoint> getFastShockwaveLocalPoints(LocalPoint center){
        List<LocalPoint> lps = new ArrayList<>();

        for (int x = 1; x < 3; x++){
            for (int y = 1; y < 3; y++){
                lps.add(new LocalPoint(center.getX()+(x*128), center.getY()+(y*128)));
                lps.add(new LocalPoint(center.getX()-(x*128), center.getY()+(y*128)));
                lps.add(new LocalPoint(center.getX()+(x*128), center.getY()-(y*128)));
                lps.add(new LocalPoint(center.getX()-(x*128), center.getY()-(y*128)));
            }
        }

        lps.add(new LocalPoint(center.getX()+(3*128), center.getY()));
        lps.add(new LocalPoint(center.getX()-(3*128), center.getY()));
        lps.add(new LocalPoint(center.getX(), center.getY()+(3*128)));
        lps.add(new LocalPoint(center.getX(), center.getY()-(3*128)));
        return lps;
    }

    private void renderBananas(Graphics2D graphics){
        for (GameObject object : getGameObjects()) {
            if (object != null) {
                if (object.getId() == Baba.BANANA_ID) {
                    renderDangerTile(graphics, object.getWorldLocation());
                }
            }
        }
    }

    private void renderFallingBoulders(Graphics2D graphics) {
        for (GraphicsObject object : client.getGraphicsObjects()) {
            if (object != null) {
                if (object.getId() == Baba.FALLING_BOULDER_ID) {
                    renderDangerTile(graphics, object.getLocation());
                }
            }
        }
    }

    private void renderBoulders(Graphics2D graphics){
        for (NPC npc : client.getNpcs())
        {
            NPCComposition npcComposition = npc.getTransformedComposition();

            if (npcComposition != null && npc.getId() == Baba.BOULDER_ID){
                renderTrueTile(graphics, npc, npcComposition.getSize());
            }
            else if (npcComposition != null && npc.getId() == Baba.BOULDER_BROKEN_ID){
                renderTrueTile(graphics, npc, npcComposition.getSize());
                OverlayUtil.renderPolygon(graphics, npc.getConvexHull(), Color.RED,new BasicStroke(3));
            }
        }
    }

    private void renderBabaTicks(Graphics2D graphics){
    	if(baba.getBabaNPC() != null){
    		String text = "";
    	text = text + baba.babaTicksUntilAttack;
		Point canvasPoint = baba.getBabaNPC().getCanvasTextLocation(graphics, text, 60);
		if (canvasPoint != null) {
				renderTextLocation(graphics, text, Color.white, canvasPoint);
			}
    	}

	}

	private void renderElementsSequence(Graphics2D graphics){
		ArrayList<LocalPoint> localPoints = new ArrayList<LocalPoint>();

		for (BabaDangerTile tile : this.baba.getBabaDangerTiles())
		{
			//Check if tick counter is drawn

			Color color = Color.WHITE;
			if (localPoints.size() < 1) color = Color.RED;
			renderTile(graphics, tile.getLpoint(), config.dangerTileColorToa(), 1, config.dangerTileFillColorToa());
			LocalPoint lp = tile.getLpoint();
			if (lp != null) {
				Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(tile.getTicksLeft()), 0);
				if (textPoint != null) {
					//Add worldpoint to list
					localPoints.add(tile.getLpoint());
					OverlayUtil.renderTextLocation(graphics,textPoint, String.valueOf(tile.getTicksLeft()), Color.WHITE);
				}
			}

		}
	}
	public void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final double borderWidth, final Color fillColor)
	{
		if (dest == null)
		{
			return;
		}

		final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

		if (poly == null)
		{
			return;
		}

		OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
	}

}
