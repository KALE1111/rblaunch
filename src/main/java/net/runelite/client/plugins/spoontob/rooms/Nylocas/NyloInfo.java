package net.runelite.client.plugins.spoontob.rooms.Nylocas;

import net.runelite.api.NPC;

public class NyloInfo {
    public NyloInfo(NPC nylo) {
        this.nylo = nylo;
        this.ticks = 52;
        this.alive = true;
    }
    public NPC nylo;
    public int ticks;
    public boolean alive;
}