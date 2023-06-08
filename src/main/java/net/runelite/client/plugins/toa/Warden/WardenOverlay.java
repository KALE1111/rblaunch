package net.runelite.client.plugins.toa.Warden;

import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.toa.Akkha.MemorizingTile;
import net.runelite.client.plugins.toa.Baba.Baba;
import net.runelite.client.plugins.toa.Kephri.KephriDangerTile;
import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.RoomOverlay;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class WardenOverlay extends RoomOverlay {
    private int[] poisonIds = new int[]{45570, 45571, 45572, 45573, 45574, 45575, 45576};

    private final Warden plugin;

    @Inject
    protected WardenOverlay(ToaConfig config, ModelOutlineRenderer outliner, Warden plugin) {
        super(config, outliner);
        this.plugin = plugin;
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        //TODO MAKE THIS WORK if (!plugin.isZebakActive()){ return null; }
        if(config.wardenObeliskBallTicks()) {
        	wardenBallTicks(graphics);
		}

		renderElementsSequence(graphics);

        return null;
    }

	private void renderElementsSequence(Graphics2D graphics){
		ArrayList<LocalPoint> localPoints = new ArrayList<LocalPoint>();

		for (WardenDangerTile tile : this.plugin.getWardenDangerTiles())
		{
			//Check if tick counter is drawn

			Color color = Color.WHITE;
			if (localPoints.size() < 1) color = Color.RED;

			if(tile.getId() == 2251 || tile.getId() == 2250){
				renderColorTile(graphics,tile.getLpoint(), config.wardenBoulderOutline(), config.wadernBoulderFill());
			}
			else
			{
				renderDangerTile(graphics, tile.getLpoint());
			}
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





	private void wardenBallTicks(Graphics2D graphics) {
		for (Projectile p : this.client.getProjectiles()) {
			int id = p.getId();
			Point point = Perspective.localToCanvas(this.client, new LocalPoint((int)p.getX(), (int)p.getY()), 0, Perspective.getTileHeight(this.client, new LocalPoint((int)p.getX(), (int)p.getY()), p.getFloor()) - (int)p.getZ());
			String ticks = String.valueOf(p.getRemainingCycles() / 30);
			if (point != null && (
				id == 2238 || id == 2237) &&
				p.getInteracting() == this.client.getLocalPlayer())
				renderTextLocation(graphics, ticks, Color.white, point);
		}
	}

	private void renderFallingWardenBoulders(Graphics2D graphics) {
		for (GraphicsObject object : client.getGraphicsObjects()) {
			if (object != null) {
				if (object.getId() == 2250) {
					renderDangerTile(graphics, object.getLocation());
				}
			}
		}
	}





    }

