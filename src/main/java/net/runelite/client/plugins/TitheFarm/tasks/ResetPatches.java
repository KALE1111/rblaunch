package net.runelite.client.plugins.TitheFarm.tasks;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import net.runelite.client.plugins.TitheFarm.TithePlugin;
import net.runelite.client.plugins.TitheFarm.utils.Task;
import net.runelite.client.plugins.TitheFarm.utils.Time;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

import static net.runelite.client.plugins.TitheFarm.utils.Helpers.*;
public class ResetPatches extends Task {
    @Inject
    private TithePlugin plugin;
    @Override
    public String getStatus() {
        return "Resetting patches";
    }

    @Override
    public boolean validate() {
        if (plugin.patches.isEmpty()){
            if (plugin.getConfig().debug())
                EthanApiPlugin.sendClientMessage("patches is empty");
            return true;
        }
        if(plugin.getConfig().debug()){
            EthanApiPlugin.sendClientMessage("patches is not empty");
        }
        return false;
    }

    @Override
    public void execute() {
        plugin.setupPatches();
        WorldPoint location = plugin.patches.get(0).dx(nextInt(-3, -2)).dy(nextInt(-2, 2));
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(location);
        Time.sleepTicksUntil(() -> plugin.client.getLocalPlayer().getWorldLocation().distanceTo(location) <= 6, 20);
    }
}
