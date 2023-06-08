package net.runelite.client.plugins.toa.Util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PrayerHighlightMode
{
    WIDGET("Widget"),
    BOX("Box"),
    BOTH("Both"),
    NONE("None");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}

