package net.runelite.client.plugins.spoontob.rooms.Maiden;

import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;

public class MaidenBloodSplatInfo {
    public MaidenBloodSplatInfo(Projectile projectile, LocalPoint lp) {
        this.projectile = projectile;
        this.lp = lp;
    }
    public Projectile projectile;
    public LocalPoint lp;
}