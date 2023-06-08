package net.runelite.client.plugins.toa.Kephri;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Akkha.Akkha;
import net.runelite.client.plugins.toa.RoomOverlay;
import net.runelite.client.plugins.toa.Warden.WardenDangerTile;
import net.runelite.client.plugins.toa.Zebak.Zebak;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class KephriOverlay extends RoomOverlay {

    private final Kephri plugin;

    @Inject
    protected KephriOverlay(ToaConfig config, ModelOutlineRenderer outliner, Kephri plugin)
    {
        super(config, outliner);
        this.plugin = plugin;
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.isKephriActive()){
            if (config.kephriAttackRadius()){
				renderElementsSequence(graphics);
            }

            if (config.fliesOnCharacter()){
                fliesOnCharacter(graphics);
            }


            if (config.hideUnattackableSwams()){
                renderSwarm(graphics);
            }
        }

        return null;
    }

    private void fliesOnCharacter(Graphics2D graphics){
        if (plugin.isFlyActive()){
            outliner.drawOutline(client.getLocalPlayer(), 2, Color.GREEN,
                    0);

            LocalPoint lp = LocalPoint.fromWorld(client, client.getLocalPlayer().getWorldLocation());
            if (lp != null) {
                Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(plugin.getFlyTicks()), 0);
                if (textPoint != null) {
                    renderTextLocation(graphics, String.valueOf(plugin.getFlyTicks()), Color.GREEN, textPoint);
                }
            }
        }
    }

    private void renderSwarm(Graphics2D graphics){
        for (NPC npc : client.getNpcs())
        {
            NPCComposition npcComposition = npc.getTransformedComposition();
            if (npcComposition != null && npc.getId() == Kephri.SWARM_ID){
                int xNpc = npc.getWorldLocation().getRegionX();
                int yNpc = npc.getWorldLocation().getRegionY();
                if ((xNpc < 28 || xNpc > 34) || (yNpc < 29 || yNpc > 35)) {
                    renderTrueTile(graphics, npc, npcComposition.getSize());
                }
            }
        }
    }

    private void kephriAttackRadius(Graphics2D graphics){
        final List<int[][]> allEdges = new ArrayList<>();
        int edgeSizeSquared = 0;

        for (GraphicsObject object : client.getGraphicsObjects()) {
            if (object != null) {
                if (object.getId() == 1447) {
                    Color colorFill = new Color(255, 0, 0);
                    int alpha = 30;
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

	private void renderElementsSequence(Graphics2D graphics){
		ArrayList<LocalPoint> localPoints = new ArrayList<LocalPoint>();

		for (KephriDangerTile tile : this.plugin.getKephriDangerTiles())
		{
			//Check if tick counter is drawn

				Color color = Color.WHITE;
				if (localPoints.size() < 1) color = Color.RED;

				renderDangerTile(graphics, tile.getLpoint());
				LocalPoint lp = tile.getLpoint();
				if (lp != null) {
					Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(tile.getTicksLeft()), 0);
					if (textPoint != null) {
						//Add worldpoint to list
						localPoints.add(tile.getLpoint());
						renderTextLocation(graphics, String.valueOf(tile.getTicksLeft()), Color.WHITE, textPoint);
					}
				}

		}
	}


}
