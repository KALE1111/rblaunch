package net.runelite.client.plugins.spoontob.rooms.Verzik;

import net.runelite.api.coords.WorldPoint;

public class PlayerShell {
    public PlayerShell(WorldPoint location, String name) {
        this.name = name;
        this.location = location;
    }
    public WorldPoint location;
    public String name;
}