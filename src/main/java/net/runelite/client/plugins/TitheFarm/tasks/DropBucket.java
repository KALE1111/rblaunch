package net.runelite.client.plugins.TitheFarm.tasks;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import net.runelite.client.plugins.TitheFarm.utils.Task;
import net.runelite.client.plugins.TitheFarm.utils.Time;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

public class DropBucket extends Task {

    @Override
    public String getStatus() {
        return "Dropping bucket";
    }

    @Override
    public boolean validate() {
        if (Inventory.search().withId(ItemID.GRICOLLERS_FERTILISER).first().isPresent())
            return true;
        return false;
    }

    @Override
    public void execute() {
        Widget fertiliser = Inventory.search().withId(ItemID.GRICOLLERS_FERTILISER).first().orElse(null);
        if (fertiliser != null) {
            drop(fertiliser, "Drop");
        }
        Time.sleepTicksUntil(() -> Inventory.search().withId(ItemID.GRICOLLERS_FERTILISER).result().size() == 0, 10);
    }

    public void drop(Widget item, String... action){
        if (item != null){
            InventoryInteraction.useItem(item, action);
        }
    }
}
