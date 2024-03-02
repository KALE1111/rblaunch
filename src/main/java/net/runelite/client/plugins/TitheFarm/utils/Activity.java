package net.runelite.client.plugins.TitheFarm.utils;

import lombok.Value;

@Value
public class Activity {

    public static final Activity IDLE = new Activity("Idle");


    String name;

}
