package net.runelite.client.plugins.toa.Util;

import java.lang.reflect.*;

import net.runelite.api.Client;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPCComposition;


public class NPCCompositionHeadIcon {

    private Client client;
    private NPCComposition npcComposition;
    private HeadIcon headIcon;

    final private String className = "gz";
    final private String fieldName = "aq";

    public NPCCompositionHeadIcon(Client client, NPCComposition npcComposition) {
        this.client = client;
        this.npcComposition = npcComposition;
    }

    public HeadIcon getNPCHeadIcon() {
        try {
            Field headIconSpriteIndexes = client.getClass().getClassLoader().loadClass(className).getDeclaredField(fieldName);
            headIconSpriteIndexes.setAccessible(true);
            short[] headIconSpritesValues = short[].class.cast(headIconSpriteIndexes.get(npcComposition));
            headIconSpriteIndexes.setAccessible(false);
            switch (headIconSpritesValues[0]) {
                case 0:
                    headIcon = HeadIcon.MELEE;
                    break;
                case 1:
                    headIcon = HeadIcon.RANGED;
                    break;
                case 2:
                    headIcon = HeadIcon.MAGIC;
                    break;
                case 3:
                    headIcon = HeadIcon.RETRIBUTION;
                    break;
                case 4:
                    headIcon = HeadIcon.SMITE;
                    break;
                case 5:
                    headIcon = HeadIcon.REDEMPTION;
                    break;
                case 6:
                    headIcon = HeadIcon.RANGE_MAGE;
                    break;
                case 7:
                    headIcon = HeadIcon.RANGE_MELEE;
                    break;
                case 8:
                    headIcon = HeadIcon.MAGE_MELEE;
                    break;
                case 9:
                    headIcon = HeadIcon.RANGE_MAGE_MELEE;
                    break;
                case 10:
                    headIcon = HeadIcon.WRATH;
                    break;
                case 11:
                    headIcon = HeadIcon.SOUL_SPLIT;
                    break;
                case 12:
                    headIcon = HeadIcon.DEFLECT_MELEE;
                    break;
                case 13:
                    headIcon = HeadIcon.DEFLECT_RANGE;
                    break;
                case 14:
                    headIcon = HeadIcon.DEFLECT_MAGE;
                    break;
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return headIcon;
    }
}
