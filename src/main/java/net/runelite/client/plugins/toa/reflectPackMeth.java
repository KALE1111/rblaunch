package net.runelite.client.plugins.toa;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;

@Singleton
@Slf4j
public class reflectPackMeth implements reflectMeth
{
	@Inject
	private PluginManager pluginManager;

	private Object widginstance = null;

	private Object mouseinstance = null;

	private Object npcinstance = null;

	private Object apiInstance = null;

	private HashMap<String, Method> packMethods = null;

	public boolean packetsInstalled = true;

	public void queueWidgetActionPacket(int actionFieldNo, int widgetID, int itemID, int ChildID) {
		performReflection("queuewidgetactionpacket4", widginstance, actionFieldNo,widgetID,itemID,ChildID );
	}

	public void queueClickPacket() {
		performReflection("queueclickpacket0", mouseinstance);
	}

	public void togglePrayer() {
		performReflection("toggleprayer1", apiInstance);
	}

	public void toggleNormalPrayer(int style) {
		performReflection("togglenormalprayer1", apiInstance, style);
	}

	public void toggleNormalPrayers(List<Integer> style) {
		performReflection("togglenormalprayers1", apiInstance, style);
	}

	public void WidgetonWidget(Widget src, Widget dst) {
		performReflection("queuewidgetonwidget2", widginstance, src,dst);
	}

	public void QueueNpcAction(NPC npc, String... actionList) {
		performReflection("queuenpcaction2", npcinstance, npc,actionList);
	}

	public void WidgetonWidgetint(int sourceWidgetId, int sourceSlot, int sourceItemId, int destinationWidgetId, int destinationSlot, int destinationItemId){
		performReflection("queuewidgetonwidget6", widginstance, sourceWidgetId, sourceSlot, sourceItemId, destinationWidgetId, destinationSlot, destinationItemId);
	}

	public void equipItems(List<Integer> items) {
		performReflection("equipitems1", apiInstance, items);
	}



	public Object performReflection(String methodName,Object instance, Object... args) {
		System.out.println(packMethods);
		if (checkReflection() && packMethods.containsKey(methodName = methodName.toLowerCase()))
			try {
				return packMethods.get(methodName).invoke(instance, args);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}

		return null;
	}


	public boolean checkReflection() {
		if (!this.packetsInstalled)
			return false;
		if (this.packMethods != null && this.widginstance != null && this.mouseinstance != null && this.apiInstance != null)
			return true;
		this.packMethods = new HashMap<>();

		for (Plugin p : pluginManager.getPlugins()) {
			if (p.getClass().getSimpleName().toLowerCase().equals("packetutilspluginsm"))
			{
				for (Field f : p.getClass().getDeclaredFields())
				{
					if (f.getName().toLowerCase().equals("widgetpacket")) {
						f.setAccessible(true);
						try {
							widginstance = f.get(p);
							for (Method m : widginstance.getClass().getDeclaredMethods())
							{
								m.setAccessible(true);
								packMethods.put(m.getName().toLowerCase()+m.getParameterCount(), m);
							}
							//return true;
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						//return false;
					}
				}
			}
		}
		for (Plugin p : pluginManager.getPlugins()) {
			if (p.getClass().getSimpleName().toLowerCase().equals("packetutilspluginsm"))
			{
				for (Field f : p.getClass().getDeclaredFields())
				{
					if (f.getName().toLowerCase().equals("mousepackets")) {
						f.setAccessible(true);
						try {
							mouseinstance = f.get(p);
							for (Method m : mouseinstance.getClass().getDeclaredMethods())
							{
								m.setAccessible(true);
								packMethods.put(m.getName().toLowerCase()+m.getParameterCount(), m);
							}
							//return true;
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						//return false;
					}
				}
			}
		}
		for (Plugin p : pluginManager.getPlugins()) {
			if (p.getClass().getSimpleName().toLowerCase().equals("packetutilspluginsm"))
			{
				for (Field f : p.getClass().getDeclaredFields())
				{
					if (f.getName().toLowerCase().equals("npcpackets")) {
						f.setAccessible(true);
						try {
							npcinstance = f.get(p);
							for (Method m : npcinstance.getClass().getDeclaredMethods())
							{
								m.setAccessible(true);
								packMethods.put(m.getName().toLowerCase()+m.getParameterCount(), m);
							}
							//return true;
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						//return false;
					}
				}
			}
		}

		for (Plugin p : pluginManager.getPlugins()) {
			if (p.getClass().getSimpleName().toLowerCase().equals("ethanapipluginsm"))
			{
				apiInstance = p;
				for (Method m : apiInstance.getClass().getDeclaredMethods())
				{
					m.setAccessible(true);
					packMethods.put(m.getName().toLowerCase()+m.getParameterCount(), m);
				}
				return true;
			}
		}

		this.packetsInstalled = false;
		return false;
	}
}