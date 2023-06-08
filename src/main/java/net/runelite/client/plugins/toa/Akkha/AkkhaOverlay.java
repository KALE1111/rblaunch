package net.runelite.client.plugins.toa.Akkha;

import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Util.AttackStyle;
import net.runelite.client.plugins.toa.RoomOverlay;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AkkhaOverlay extends RoomOverlay {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");

    @Inject
    private Akkha akkha;

    @Inject
    private Client client;

    @Inject
    protected AkkhaOverlay(ToaConfig config, ModelOutlineRenderer outliner)
    {
        super(config, outliner);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics){
        if (akkha.isAkkhaActive()){
            if (config.AkkhaSequence()){
                renderElementsSequence(graphics);
            }

            if (config.AkkhaOverlayWrongPrayerOutline())
            {
                renderAkkhaWrongPrayerOutline();
            }

            if (config.orbTrueTile()){
                renderOrbs(graphics);
            }

			renderAkkhaTicks(graphics);
        }

        return null;
    }

    private void renderElementsSequence(Graphics2D graphics){
        ArrayList<WorldPoint> worldPoints = new ArrayList<WorldPoint>();
        for (MemorizingTile tile : this.akkha.getMemorizingSequence())
        {
            //Check if tick counter is drawn
            if (!worldPoints.contains(tile.getPoint())) {
                Color color = Color.WHITE;
                if (worldPoints.size() < 1) color = Color.RED;

                drawTile(graphics, tile.getPoint(), color, 1, 255, 0);
                LocalPoint lp = LocalPoint.fromWorld(client, tile.getPoint());
                if (lp != null) {
                    Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(tile.getTicksLeft()), 0);
                    if (textPoint != null) {
                        //Add worldpoint to list
                        worldPoints.add(tile.getPoint());
                        renderTextLocation(graphics, String.valueOf(tile.getTicksLeft()), Color.WHITE, textPoint);
                    }
                }
            }
        }
    }

    private void renderAkkhaWrongPrayerOutline()
    {
        for (NPC npc : client.getNpcs()){
            switch(npc.getId()) {
                case 11792:
                    OutlineNPC(npc, AttackStyle.MAGE);
                    break;
                case 11790:
                    OutlineNPC(npc, AttackStyle.MELEE);
                    break;
                case 11791:
                    OutlineNPC(npc, AttackStyle.RANGE);
                    break;
            }
        }
    }

    private void OutlineNPC(NPC npc, AttackStyle attackStyle){
        if (attackStyle == null || attackStyle == AttackStyle.UNKNOWN){
            return;
        }

        if (client.isPrayerActive(attackStyle.getPrayer().getApiPrayer()))
        {
            return;
        }

        outliner.drawOutline(npc, 2, attackStyle.getColor(),
                    0);
    }

	private void renderAkkhaTicks(Graphics2D graphics){
		if(akkha.getAkkhaNPC() != null){
			String text = "";
			text = text + akkha.AkkhaTicks;
			Point canvasPoint = akkha.getAkkhaNPC().getCanvasTextLocation(graphics, text, 60);
			if (canvasPoint != null) {
				renderTextLocation(graphics, text, Color.white, canvasPoint);
			}
		}

	}


    private void renderOrbs(Graphics2D graphics){
        for (NPC npc : client.getNpcs())
        {
            NPCComposition npcComposition = npc.getTransformedComposition();

            if (npcComposition != null && npc.getId() == Akkha.ORB_ID){
                int size = npcComposition.getSize();
                if (config.orbTrueTile()){
                    renderTrueTile(graphics, npc, size);
                }
            }
        }
    }
}
