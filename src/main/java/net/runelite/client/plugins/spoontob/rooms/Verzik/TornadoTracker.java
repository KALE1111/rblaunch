package net.runelite.client.plugins.spoontob.rooms.Verzik;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@Getter(AccessLevel.PACKAGE)
public class TornadoTracker {
    @Getter(AccessLevel.PACKAGE)
    private NPC npc;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private WorldPoint prevLoc;

    TornadoTracker(final NPC npc){
        this.npc = npc;
        prevLoc = null;
    }

    public int checkMovement(WorldPoint playerWp, WorldPoint nadoWp){
        if (prevLoc == null || nadoWp == null || prevLoc.distanceTo(nadoWp) == 0){
            return -1;
        }
        return playerWp.distanceTo(nadoWp) - playerWp.distanceTo(prevLoc);
    }
}