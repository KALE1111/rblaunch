package net.runelite.client.plugins.socketba.jacob;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

public class CycleCounter extends Counter {
	public CycleCounter(BufferedImage img, Plugin plugin, int tick) {
		super(img, plugin, tick);
	}

	public Color getTextColor() {
		return (getCount() == 9) ? Color.GREEN : Color.WHITE;
	}
}
