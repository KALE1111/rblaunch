package net.runelite.client.plugins.toa.Util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@AllArgsConstructor
@Getter
public enum AttackStyle
{
    MAGE("Mage", Color.CYAN, Prayer.PROTECT_FROM_MAGIC),
    RANGE("Range", Color.GREEN, Prayer.PROTECT_FROM_MISSILES),
    MELEE("Melee", Color.RED, Prayer.PROTECT_FROM_MELEE),
    UNKNOWN("Unknown", Color.WHITE, null);

    @Getter
    private final String name;
    @Getter
    private final Color color;
    @Getter
    private final Prayer prayer;
}
