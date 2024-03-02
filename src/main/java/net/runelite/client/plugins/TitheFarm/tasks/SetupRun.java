package net.runelite.client.plugins.TitheFarm.tasks;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import net.runelite.client.plugins.TitheFarm.TithePlugin;
import net.runelite.client.plugins.TitheFarm.utils.Task;
import net.runelite.client.plugins.TitheFarm.utils.Time;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;

import static com.example.EthanApiPlugin.EthanApiPlugin.isMoving;
import static net.runelite.client.plugins.TitheFarm.utils.Helpers.isIdle;
import static net.runelite.client.plugins.TitheFarm.utils.Helpers.nextInt;

public class SetupRun extends Task {
    @Inject
    private TithePlugin plugin;

    @Override
    public String getStatus() {
        return "Setting up run";
    }

    @Override
    public boolean validate() {
        if(isMoving()){
            return false;
        }

        TileObject patch = getAt(plugin.patches.get(0));
        if (patch == null)
            return false;

        return patch.getId() == ObjectID.TITHE_PATCH;
    }

    @Override
    public void execute() {
        TileObject patch = getAt(plugin.patches.get(0));
        if(patch == null || patch.getId() != ObjectID.TITHE_PATCH)
            return;

        Widget seed = get(plugin.seed);
        if (seed == null)
            return;

        useOn(seed, patch);
        if (plugin.getConfig().debug()){
            EthanApiPlugin.sendClientMessage("Planted seed at" + patch.getWorldLocation());
        }
        Time.sleepTicksUntil(() -> isIdle(), 10);
        Time.sleepTicks(nextInt(0, plugin.getConfig().maxTick()));
    }

    public TileObject getAt(WorldPoint worldPoint){
        return TileObjects.search().atLocation(worldPoint).nearestToPlayer().orElse(null);
    }

    public boolean useOn(Widget item, TileObject tileObject){
        if (item == null || tileObject == null)
            return false;

        MousePackets.queueClickPacket();
        MousePackets.queueClickPacket();
        ObjectPackets.queueWidgetOnTileObject(item, tileObject);
        return true;
    }

    public Widget get(int itemId){
        return Inventory.search().withId(itemId).first().orElse(null);
    }
}
