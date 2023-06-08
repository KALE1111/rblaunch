package net.runelite.client.plugins.toa.Scarabas;

import net.runelite.api.Point;
import net.runelite.client.plugins.toa.Kephri.KephriDangerTile;
import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Kephri.Kephri;
import net.runelite.client.plugins.toa.RoomOverlay;
import net.runelite.api.GraphicsObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class ScarabasOverlay extends RoomOverlay {

    private final Scarabas plugin;

    @Inject
    protected ScarabasOverlay(ToaConfig config, ModelOutlineRenderer outliner, Scarabas plugin)
    {
        super(config, outliner);
        this.plugin = plugin;
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.isScarabasActive()){
            if (config.scarabasAttackRadius()){
                pillarAttackRadius(graphics);
            }

            if (config.scarabasPuzzleSolver()){
                drawPuzzleTiles(graphics);
            }
        }

        return null;
    }

    private void pillarAttackRadius(Graphics2D graphics){
        for (GraphicsObject object : client.getGraphicsObjects()) {
            if (object != null) {
                if (object.getId() == 317) {
                    renderDangerTile(graphics, object.getLocation());
                }
            }
        }
    }

    private void drawPuzzleTiles(Graphics2D graphics){
        for (GroundObject object : getGroundObjects()) {
            switch(object.getId()){
                //line
                case 45388:
                case 45365:
                case 45356:
                    renderMatchingTile(graphics, Color.black, object.getWorldLocation(), "A");
                    break;
                //knives
                case 45389:
                case 45366:
                case 45357:
                    renderMatchingTile(graphics, Color.red, object.getWorldLocation(), "B");
                    break;
                //crook
                case 45386:
                case 45367:
                case 45358:
                    renderMatchingTile(graphics, Color.MAGENTA, object.getWorldLocation(), "C");
                    break;
                //diamond
                case 45391:
                case 45368:
                case 45359:
                    renderMatchingTile(graphics, Color.blue, object.getWorldLocation(), "D");
                    break;
                //hand
                case 45392:
                case 45369:
                case 45360:
                    renderMatchingTile(graphics, Color.LIGHT_GRAY, object.getWorldLocation(), "E");
                    break;
                //star
                case 45387:
                case 45370:
                case 45361:
                    renderMatchingTile(graphics, Color.CYAN, object.getWorldLocation(), "F");
                    break;
                //bird
                case 45393:
                case 45371:
                case 45362:
                    renderMatchingTile(graphics, Color.PINK, object.getWorldLocation(), "G");
                    break;
                //wiggle
                case 45394:
                case 45372:
                case 45363:
                    renderMatchingTile(graphics, Color.YELLOW, object.getWorldLocation(), "H");
                    break;
                //boot
                case 45395:
                case 45373:
                case 45364:
                    renderMatchingTile(graphics, Color.GREEN, object.getWorldLocation(), "I");
                    break;
            }
        }
    }



    private void renderMatchingTile(Graphics2D graphics, Color color, WorldPoint wp,String text){
        LocalPoint lp = LocalPoint.fromWorld(client, wp);
        Polygon canvasTilePoly = Perspective.getCanvasTilePoly(client, lp);
        if (canvasTilePoly == null)
        {
            return;
        }
		OverlayUtil.renderPolygon(graphics, canvasTilePoly, color);
		Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, text, 0);
		if (textPoint != null)
		{
			//Add worldpoint to list
			renderTextLocation(graphics, text, Color.WHITE, textPoint);
		}

    }
}
