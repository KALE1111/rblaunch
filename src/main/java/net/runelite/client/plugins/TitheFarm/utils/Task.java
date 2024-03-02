package net.runelite.client.plugins.TitheFarm.utils;

import net.runelite.client.plugins.TitheFarm.utils.Activity;

public abstract class Task {

    public Activity getActivity() {
        return Activity.IDLE;
    }

    public abstract String getStatus();

    public abstract boolean validate();

    public abstract void execute();
}
