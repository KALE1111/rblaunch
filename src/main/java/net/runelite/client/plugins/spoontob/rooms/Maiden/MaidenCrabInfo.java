package net.runelite.client.plugins.spoontob.rooms.Maiden;

import net.runelite.api.NPC;

public class MaidenCrabInfo {
    public MaidenCrabInfo(NPC crab, int phase, String position, int hpRatio, int hpScale, int frozenTicks, boolean scuffed) {
        this.crab = crab;
        this.phase = phase;
        this.position = position;
        this.hpRatio = hpRatio;
        this.hpScale = hpScale;
        this.frozenTicks = frozenTicks;
        this.scuffed = scuffed;
    }
    public NPC crab;
    public int phase;
    public String position;
    public int hpRatio;
    public int hpScale;
    public int frozenTicks;
    public boolean scuffed;
}