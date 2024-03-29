package net.runelite.client.plugins.ocarial;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.RuneBotApi.SpecialMenuOptionClicked;
import com.google.inject.Inject;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOptionClicked;

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@PluginDescriptor(
        name = "One Click Aerial Fishing",
        description = "Ensure some form of bait is in invent, either worms or fish chunks and a knife. If there's no knife it will just drop the fish.",
        tags = {"one", "click", "tench", "fishing","aerial","arial"},
        enabledByDefault = false
)
public class OneClickAerialFishingPlugin extends Plugin {

    private static final int TENCH_FISHING_SPOT_NPC_ID = 8523;
    private static final List<Integer> LIST_OF_FISH_IDS= Arrays.asList(22826, 22829, 22832,22835);
    private static boolean shouldCut = false;

    @Inject
    private Client client;

    private SpecialMenuOptionClicked currEntry;

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if(event.getMenuOption().equals("<col=00ff00>One Click Aerial Fishing"))
        {
            currEntry = new SpecialMenuOptionClicked(event.getMenuEntry());
            handleClick(currEntry);
            if(!currEntry.getMenuOption().equals("Handled")) {
                EthanApiPlugin.invoke(currEntry.getParam0(), currEntry.getParam1(), currEntry.getMenuAction().getId(), currEntry.getId(), currEntry.getItemId(), currEntry.getMenuOption(), currEntry.getMenuTarget(), 0, 0);
            }
            event.consume();
        }
    }

    @Subscribe
    private void onClientTick(ClientTick event) {
        if (this.client.getLocalPlayer() == null || this.client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        String text = "<col=00ff00>One Click Aerial Fishing";
        MenuEntry[] list = client.getMenuEntries();
        if(list.length == 1){
            MenuEntry newest = list[0];
            newest.setTarget(text);
            newest.setOption(text);
            newest.setIdentifier(MenuAction.UNKNOWN.getId());
            list[0] = newest;
        }
        else {
            MenuEntry newest = list[1];
            newest.setTarget(text);
            newest.setOption(text);
            newest.setIdentifier(MenuAction.UNKNOWN.getId());
            list[1] = newest;
        }
        client.setMenuEntries(list);
    }

    private void handleClick(SpecialMenuOptionClicked event)
    {
        if (getEmptySlots()>0
                && ((getLastInventoryItem(ItemID.KING_WORM)!=null)||(getLastInventoryItem(ItemID.FISH_CHUNKS)!=null)) //if bait exists
                && !shouldCut)
        {
            event.setMenuEntry(catchFishMenuEntry());
            return;
        }
        else
        {
            shouldCut = true;
        }
        //if space in inventory then fish, else cut // add check for bait!

        for (int fish:LIST_OF_FISH_IDS)
        {
            if (getLastInventoryItem(fish)!=null)
            {
                if (getLastInventoryItem(ItemID.KNIFE)==null)
                {
                    MenuEntry next = createMenuEntry(0,MenuAction.WIDGET_TYPE_1,0,0,false);
                    next.setOption("Handled");
                    event.setMenuEntry(next);
                    //MousePackets.queueClickPacket();
                    Widget dropitem = getLastInventoryItem(fish);
                    EthanApiPlugin.invoke(dropitem.getIndex(),9764864,1007,7,dropitem.getItemId(),"Drop",dropitem.getName(),0,0);
                    return;
                }

                MenuEntry next = createMenuEntry(0,MenuAction.WIDGET_TYPE_1,0,0,false);
                next.setOption("Handled");
                event.setMenuEntry(next);

                Widget knife = getLastInventoryItem(ItemID.KNIFE);
                //MousePackets.queueClickPacket();//Should change this to uh shadowclicks probs cba
                //easier to invoke then send selected + looks a teensy bit more realistic imo
                EthanApiPlugin.invoke(knife.getIndex(), 9764864,MenuAction.WIDGET_TARGET.getId(),1,knife.getItemId(),"Use", knife.getName(),0,0);
                MousePackets.queueClickPacket();
                Widget fishWidgete = getLastInventoryItem(fish);
                EthanApiPlugin.invoke(fishWidgete.getIndex(),9764864,MenuAction.WIDGET_TARGET_ON_WIDGET.getId(),1,fishWidgete.getItemId(),"","",0,0);
                return;
            }
        }
        shouldCut=false;
    }

    private Widget getLastInventoryItem(int id) {
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget!=null && !inventoryWidget.isHidden())
        {
            return getLastWidgetItem(inventoryWidget,id);
        }
        return null;
    }

    private Widget getLastWidgetItem(Widget widget,int id) {
        return Arrays.stream(widget.getDynamicChildren())
                .filter(item -> item.getItemId()==id)
                .reduce((first, second) -> second)
                .orElse(null);
    }

    private int getEmptySlots() {
        Widget inventory = client.getWidget(WidgetInfo.INVENTORY.getId());
        Widget bankInventory = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId());

        if (inventory!=null && !inventory.isHidden()
                && inventory.getDynamicChildren()!=null)
        {
            List<Widget> inventoryItems = Arrays.asList(client.getWidget(WidgetInfo.INVENTORY.getId()).getDynamicChildren());
            return (int) inventoryItems.stream().filter(item -> item.getItemId() == 6512).count();
        }

        if (bankInventory!=null && !bankInventory.isHidden()
                && bankInventory.getDynamicChildren()!=null)
        {
            List<Widget> inventoryItems = Arrays.asList(client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId()).getDynamicChildren());
            return (int) inventoryItems.stream().filter(item -> item.getItemId() == 6512).count();
        }
        return -1;
    }

    private MenuEntry useKnifeOnFishMenuEntry(Widget Fish){
        return createMenuEntry(
                0,
                MenuAction.WIDGET_TARGET_ON_WIDGET,
                Fish.getIndex(),
                9764864,
                false);
    }

    private MenuEntry dropFishMenuEntry(Widget Fish){
        return createMenuEntry(
                7,
                MenuAction.CC_OP_LOW_PRIORITY,
                Fish.getIndex(),
                9764864,
                false);
    }

    private MenuEntry catchFishMenuEntry(){
        NPC FishingSpot = getFishingSpot();
        return createMenuEntry(
                FishingSpot.getIndex(),
                MenuAction.NPC_FIRST_OPTION,
                getNPCLocation(FishingSpot).getX(),
                getNPCLocation(FishingSpot).getY(),
                true);
    }

    private NPC getFishingSpot()
    {
        return NPCs.search().withId(TENCH_FISHING_SPOT_NPC_ID).nearestToPlayer().get();
    }

    private Point getNPCLocation(NPC npc)
    {
        return new Point(npc.getLocalLocation().getSceneX(),npc.getLocalLocation().getSceneY());
    }

    public MenuEntry createMenuEntry(int identifier, MenuAction type, int param0, int param1, boolean forceLeftClick) {
        return client.createMenuEntry(0).setOption("").setTarget("").setIdentifier(identifier).setType(type)
                .setParam0(param0).setParam1(param1).setForceLeftClick(forceLeftClick);
    }

    private void printLineNumber() {
        System.out.println("LINE - " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    }
}