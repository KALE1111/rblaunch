package net.runelite.client.plugins.toa.Prayer;

import net.runelite.client.plugins.toa.Util.Prayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class NextAttack implements Comparable<NextAttack> {
    @Getter(AccessLevel.PUBLIC)
    private int ticksUntil;

    @Getter(AccessLevel.PUBLIC)
    private final Prayer prayer;

    @Getter(AccessLevel.PUBLIC)
    private final int priority;

    private final int SPRITE_ID;

    public NextAttack(int ticksUntil, Prayer prayer, int priority) {
        this.ticksUntil = ticksUntil;
        this.prayer = prayer;
        this.priority = priority;

        if (Prayer.PROTECT_FROM_MAGIC == prayer)
            SPRITE_ID = 127;
        else if (Prayer.PROTECT_FROM_MISSILES == prayer)
            SPRITE_ID = 128;
        else if (Prayer.PROTECT_FROM_MELEE == prayer)
            SPRITE_ID = 129;
        else
            SPRITE_ID = 132;
    }

    public NextAttack(int ticksUntil, Prayer prayer) {
        this(ticksUntil, prayer, 0);
    }

    public void decrementTicks() {
        if (ticksUntil > 0) {
            ticksUntil -= 1;
        }
    }

    public boolean shouldRemove() {
        return ticksUntil == 0;
    }

    @Override
    public int compareTo(@NonNull NextAttack o) {
        return Comparator.comparing(NextAttack::getTicksUntil).thenComparing(NextAttack::getPriority).compare(this, o);
    }

    @Getter(AccessLevel.NONE)
    private BufferedImage image;

    public BufferedImage getImage(final SpriteManager spriteManager) {
        if (image == null) {
            final BufferedImage tmp = spriteManager.getSprite(SPRITE_ID, 0);
            image = tmp == null ? null : ImageUtil.resizeImage(tmp, PrayerBoxOverlay.IMAGE_SIZE, PrayerBoxOverlay.IMAGE_SIZE);
        }

        return image;
    }

    public static void updateNextPrayerQueue(Queue<NextAttack> queue)
    {
        queue.forEach(NextAttack::decrementTicks);
        queue.removeIf(NextAttack::shouldRemove);
    }

    // Map ticks until to prayer
    public static Map<Integer, NextAttack> getTickPriorityMap(Queue<NextAttack> queue)
    {
        Map<Integer, NextAttack> map = new HashMap<>();

        queue.forEach(attack -> {
            if (!map.containsKey(attack.getTicksUntil()))
            {
                map.put(attack.getTicksUntil(), attack);
            }

            if (attack.getPriority() < map.get(attack.getTicksUntil()).getPriority())
            {
                map.put(attack.getTicksUntil(), attack);
            }
        });

        return map;
    }
}
