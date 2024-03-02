package net.runelite.client.plugins.TitheFarm.tasks;

import com.example.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.TitheFarm.TithePlugin;
import net.runelite.client.plugins.TitheFarm.utils.Task;
import net.runelite.client.plugins.TitheFarm.utils.Time;
import net.runelite.api.TileObject;

import javax.inject.Inject;

import static net.runelite.client.plugins.TitheFarm.utils.Helpers.*;

public class WaterPlant extends Task {
    @Inject
    private TithePlugin plugin;
    TileObject waterPatch;

    @Override
    public String getStatus() {
        return "Watering plant";
    }

    @Override
    public boolean validate() {
        waterPatch = getAtWithAction(plugin.patches.get(0), "Water");
        if (waterPatch != null){
            if (plugin.getConfig().debug()){
                EthanApiPlugin.sendClientMessage("Water plant is visible");
            }
            return true;
        }

        return false;
    }

    @Override
    public void execute() {
        waterPatch = getAtWithAction(plugin.patches.get(0), "Water");
        if(waterPatch != null){
            interact(waterPatch, "Water");
            if (plugin.getConfig().debug()){
                EthanApiPlugin.sendClientMessage("Watered seed at" + waterPatch.getWorldLocation());
            }
            Time.sleepTicksUntil(() -> getAtWithAction(plugin.patches.get(0), "Water") == null, 10);
            Time.sleepTicks(nextInt(0, plugin.getConfig().maxTick()));
            plugin.patches.remove(0);
        }
    }
}
