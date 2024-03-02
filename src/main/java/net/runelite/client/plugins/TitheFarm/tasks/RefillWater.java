package net.runelite.client.plugins.TitheFarm.tasks;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import net.runelite.client.plugins.TitheFarm.utils.Task;
import net.runelite.client.plugins.TitheFarm.utils.Time;
import net.runelite.client.plugins.TitheFarm.TithePlugin;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;

import static net.runelite.client.plugins.TitheFarm.utils.Helpers.nextInt;

public class RefillWater extends Task {
    @Inject
    private TithePlugin plugin;
    @Override
    public String getStatus() {
        return "Refilling water cans";
    }

    @Override
    public boolean validate() {
        if (plugin.harvested){
            if (plugin.getConfig().debug()){
                EthanApiPlugin.sendClientMessage("Refilling water cans");
            }
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        TileObject barrel = getAt(plugin.startPoint.dx(3).dy(-1), 5598);
        if (barrel == null)
            return;

        Widget can = null;
        if (contains(ItemID.GRICOLLERS_CAN)){
            can = get(ItemID.GRICOLLERS_CAN);
        }else {
            can = get(5331); //Empty water can
        }
        if (can == null)
            return;

        useOn(can, barrel);
        if (contains(ItemID.GRICOLLERS_CAN)){
            Time.sleepTicks(nextInt(4,5));
        }else {
            Time.sleepTicksUntil(() -> !contains(5331), 40);
        }
        Time.sleepTicks(nextInt(0,plugin.getConfig().maxTick()));

        //TODO Deposit harvested seeds

        plugin.harvested = false;
    }

    public static boolean contains(int itemId){
        return Inventory.search().withId(itemId).result().size() > 0;
    }

    public boolean useOn(Widget item, TileObject tileObject){
        if (item == null || tileObject == null)
            return false;

        MousePackets.queueClickPacket();
        MousePackets.queueClickPacket();
        ObjectPackets.queueWidgetOnTileObject(item, tileObject);
        return true;
    }

    public TileObject getAt(WorldPoint worldPoint, int objectId){
        return TileObjects.search().withId(objectId).atLocation(worldPoint).nearestToPlayer().orElse(null);
    }

    public Widget get(int itemId){
        return Inventory.search().withId(itemId).first().orElse(null);
    }
}
