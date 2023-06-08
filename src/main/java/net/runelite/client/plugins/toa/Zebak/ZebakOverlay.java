package net.runelite.client.plugins.toa.Zebak;

import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.RoomOverlay;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;


public class ZebakOverlay extends RoomOverlay {
    private int[] poisonIds = new int[]{45570, 45571, 45572, 45573, 45574, 45575, 45576};

    private final Zebak plugin;

    @Inject
    protected ZebakOverlay(ToaConfig config, ModelOutlineRenderer outliner, Zebak plugin) {
        super(config, outliner);
        this.plugin = plugin;
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isZebakActive()){
            return null;
        }

        renderTrueTile(graphics);
        renderPoisonTile(graphics);

        return null;
    }

    private void renderPoisonTile(Graphics2D graphics){
        if (config.poisonTileToa()) {
            for (GameObject object : getGameObjects()) {
                if (object != null) {
                    if (ArrayUtils.contains(poisonIds, object.getId())) {
                        renderDangerTile(graphics, object.getWorldLocation());
                    }
                }
            }
        }
    }

    private void renderTrueTile(Graphics2D graphics){
        for (NPC npc : client.getNpcs())
        {
            NPCComposition npcComposition = npc.getTransformedComposition();

            //Wave
            if (npcComposition != null && npc.getId() == 11738 && config.waveTrueTile()){
                int disToPlayer = Math.abs(npc.getWorldLocation().getY() - client.getLocalPlayer().getWorldLocation().getY());

                if (disToPlayer == 4){
                    renderTrueTile(graphics, npc, npcComposition.getSize(), Color.GREEN);
                } else if (disToPlayer == 5){
                    renderTrueTile(graphics, npc, npcComposition.getSize(), Color.ORANGE);
                } else {
                    renderTrueTile(graphics, npc, npcComposition.getSize(), Color.RED);
                }
            }

            //Boulder
            if (npcComposition != null && (npc.getId() == 11737 || npc.getId() == 11735 || npc.getId() == 11736)){
                if (config.boulderTrueTile()) {
                    renderTrueTile(graphics, npc, npcComposition.getSize());
                }
            }
        }
    }
}
