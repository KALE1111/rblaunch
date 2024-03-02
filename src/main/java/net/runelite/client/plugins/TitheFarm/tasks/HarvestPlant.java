package net.runelite.client.plugins.TitheFarm.tasks;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import net.runelite.client.plugins.TitheFarm.utils.Task;
import net.runelite.client.plugins.TitheFarm.utils.Time;
import net.runelite.client.plugins.TitheFarm.TithePlugin;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

import static net.runelite.client.plugins.TitheFarm.utils.Helpers.nextInt;

public class HarvestPlant extends Task {
    @Inject
    private TithePlugin plugin;
    TileObject harvestPatch;
    @Override
    public String getStatus() {
        return "Harvesting plant";
    }

    @Override
    public boolean validate() {
        harvestPatch = getAtWithAction(plugin.patches.get(0), "Harvest");
        if (harvestPatch != null){
            if (plugin.getConfig().debug()){
                EthanApiPlugin.sendClientMessage("Harvest plant is visible");
            }
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        harvestPatch = getAtWithAction(plugin.patches.get(0), "Harvest");
        if(harvestPatch != null){
            interact(harvestPatch, "Harvest");
            if (plugin.getConfig().debug()){
                EthanApiPlugin.sendClientMessage("Harvest plant at" + harvestPatch.getWorldLocation());
            }
            Time.sleepTicksUntil(() -> getAtWithAction(plugin.patches.get(0), "Harvest") == null, 10);

            Time.sleepTicks(nextInt(0, plugin.getConfig().maxTick()));

            plugin.patches.remove(0);
            if (plugin.patches.isEmpty()){
                plugin.harvested = true;
            }
        }
    }

    public TileObject getAtWithAction(WorldPoint worldPoint, String action){
        return TileObjects.search().atLocation(worldPoint).withAction(action).nearestToPlayer().orElse(null);
    }

    public boolean interact(TileObject tileObject, String... action){
        if (tileObject == null)
            return false;

        TileObjectInteraction.interact(tileObject, action);
        return true;
    }
}
