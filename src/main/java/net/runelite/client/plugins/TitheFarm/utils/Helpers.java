package net.runelite.client.plugins.TitheFarm.utils;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Helpers {

    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static EventBus eventBus = RuneLite.getInjector().getInstance(EventBus.class);

    public static EventBus getEventBus() {
        return eventBus;
    }
    public static synchronized int nextInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
    public static List<WorldPoint> TITHE_PATCHES(WorldPoint loc, int startX, int patches, boolean north, boolean reverse) {
        List<WorldPoint> newPatches = new ArrayList<>();
        int y = 0;
        if (north) {
            y = 12;
            for (int i = 0; i < patches; i++) {
                newPatches.add(loc.dx(startX).dy(y));
                y -= 3;
            }
        } else {
            y = -3;
            for (int i = 0; i < patches; i++) {
                newPatches.add(loc.dx(startX).dy(y));
                y -= 3;
            }
        }

        if (reverse)
            Collections.reverse(newPatches);

        return newPatches;
    }
    public static boolean interact(TileObject tileObject, String... action){
        if (tileObject == null)
            return false;

        TileObjectInteraction.interact(tileObject, action);
        return true;
    }
    public static boolean isIdle(){
        return client.getLocalPlayer().getAnimation() == -1;
    }

    public static TileObject getAtWithAction(WorldPoint worldPoint, String action){
        return TileObjects.search().atLocation(worldPoint).withAction(action).nearestToPlayer().orElse(null);
    }public static Widget get(int itemId){
        return Inventory.search().withId(itemId).first().orElse(null);
    }

}
