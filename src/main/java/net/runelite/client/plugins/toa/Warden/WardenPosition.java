package net.runelite.client.plugins.toa.Warden;

import lombok.Getter;

public enum WardenPosition
{
		UNDEFINED(""),
		RIGHT("Right"),
		MIDDLE("Mid"),
		LEFT("Left");
		@Getter
	private String text;
	private String last;

	WardenPosition(String side){
		this.text = side;
	}
}
