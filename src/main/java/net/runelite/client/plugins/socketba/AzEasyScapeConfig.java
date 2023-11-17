package net.runelite.client.plugins.socketba;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("Awalkunder")
public interface AzEasyScapeConfig extends Config {
	@ConfigItem(position = 1, keyName = "camwalkunder", name = "Detached cam hotkey", description = "")
	default Keybind camKey() {
		return Keybind.NOT_SET;
	}

	@ConfigItem(position = 2, keyName = "hotkeywalkunder", name = "Walk here hotkey(Don't Enable)", description = "")
	default Keybind walkUnderHotkey() {
		return Keybind.NOT_SET;
	}

	@ConfigItem(position = 3, keyName = "debugwalkunder", name = "Debug key pressed(Don't Enable)", description = "")
	default boolean debug() {
		return false;
	}

	@ConfigItem(position = 6, keyName = "baitdwalkunder", name = "Demote bait for d(Don't Enable)", description = "")
	default boolean removeBait() {
		return false;
	}


	@ConfigItem(position = 7, keyName = "dispwalkunder", name = "Easy dispenser", description = "remove take from disp if not that role")
	default boolean disp() {
		return false;
	}

	@ConfigItem(position = 8, keyName = "hornclickwalkunder", name = "Easy horn", description = "")
	default boolean horn() {
		return false;
	}

	@ConfigItem(position = 9, keyName = "collhelpwalkunder", name = "Easy coll(Don't Enable)", description = "")
	default boolean collhelp() {
		return false;
	}

	@ConfigItem(position = 10, keyName = "deftimepwalkunder", name = "Ba tick counter", description = "")
	default boolean defTimer() {
		return false;
	}

	@ConfigItem(position = 11, keyName = "truetilepwalkunder", name = "Ba true tile", description = "")
	default boolean truetile() {
		return false;
	}

	@Alpha
	@ConfigItem(position = 12, keyName = "truetilepwalkundercolor", name = "True tile color", description = "")
	default Color trueCOl() {
		return Color.gray;
	}

	@ConfigItem(position = 13, keyName = "runnstackwalkunder", name = "Indicate stacked penance", description = "")
	default boolean runnerStack() {
		return false;
	}

	@ConfigItem(position = 13, keyName = "hide all not food->healer", name = "Hide non food->healer", description = "hide all not food->healer")
	default boolean wheygay() {
		return false;
	}

	@ConfigItem(position = 14, keyName = "listenwidget1", name = "ba listen widget (7)", description = "", hidden = true)
	default int listenWidget() {
		return 7;
	}

	@ConfigItem(position = 15, keyName = "tocallwidget1", name = "ba tocall widget (9)", description = "", hidden = true)
	default int tocallWidget() {
		return 9;
	}

	@ConfigItem(position = 16, keyName = "barolesprite1", name = "ba role sprite (10)", description = "", hidden = true)
	default int rolesprite() {
		return 10;
	}

	@ConfigItem(position = 17, keyName = "hotkeyshiftos", name = "Shift os hotkey", description = "")
	default Keybind shiftOsHotkey() {
		return Keybind.NOT_SET;
	}

	@ConfigItem(position = 18, keyName = "spoofwalkunder", name = "<html><font color=#ff0000>Dc spoofer", description = "Will set you gamestate to dc")
	default boolean spoof() {
		return false;
	}
	@ConfigItem(position = 19, keyName = "socketba", name = "<html><font color=#ff0000>SOCKETTTTTSSS", description = "Please ask for usage instructions")
	default boolean socketba() {
		return false;
	}
}
