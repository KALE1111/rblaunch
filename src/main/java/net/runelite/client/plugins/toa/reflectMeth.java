package net.runelite.client.plugins.toa;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.Plugin;

public interface reflectMeth
{
	public Object widginstance = null;

	public Object mouseinstance = null;

	public Object npcinstance = null;

	public Object apiInstance = null;

	public HashMap<String, Method> packMethods = null;

	public boolean packetsInstalled = true;

	public void queueWidgetActionPacket(int actionFieldNo, int widgetID, int itemID, int ChildID);


	public void queueClickPacket();

	public void togglePrayer();

	public void toggleNormalPrayer(int style);

	public void toggleNormalPrayers(List<Integer> style);

	public void WidgetonWidget(Widget src, Widget dst);

	public void QueueNpcAction(NPC npc, String... actionList);

	public void WidgetonWidgetint(int sourceWidgetId, int sourceSlot, int sourceItemId, int destinationWidgetId, int destinationSlot, int destinationItemId);

	public void equipItems(List<Integer> items);



	public Object performReflection(String methodName,Object instance, Object... args);


	public boolean checkReflection();
}

