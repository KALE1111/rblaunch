package net.runelite.client.plugins.TitheFarm;

import net.runelite.client.config.*;

@ConfigGroup("AIOTitheFarm")
public interface TitheConfig extends Config {


    @ConfigSection(
            name = "Instructions",
            description = "Click me to get instructions!",
            position = 1
    )
    String instructionsTitle = "instructionsTitle";

    @ConfigSection(
            name = "Configuration",
            description = "",
            position = 2,
            closedByDefault = true
    )
    String configurationTitle = "configurationTitle";

    @ConfigSection(
            name = "Tick Delay",
            description = "",
            position = 99,
            closedByDefault = true
    )
    String configurationUITitle = "configurationUITitle";

    @ConfigSection(
            name = "Debug",
            description = "don't check this unless you want your chatbox spammed (for debugging)",
            position = 100,
            closedByDefault = true
    )
    String debugTitle = "debugTitle";


    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "",
            position = 1,
            section = "instructionsTitle"
    )
    default String instructions(){
        return "Make sure you have Seed dibber, Spade, 8 Watering Cans. Also make sure you have proper seeds for your level. Start the plugin just after you walk in the door, so walk in the door and DO NOT MOVE, then turn plugin on.";
    }

    /*

     **Configuration section**

     */

    @ConfigItem(
            keyName = "Routes",
            name = "Routes",
            description = "Routes",
            position = 0,
            section = "configurationTitle"
    )
    default Route route(){
        return Route.SIXTEEN;
    }

    @ConfigItem(
            keyName = "enableUI",
            name = "Enable UI",
            description = "Check this to turn on the Interface",
            position = 1,
            section = "configurationTitle"
    )
    default boolean enableUI(){
        return true;
    }

    @Range(
            min = 1,
            max = 1000000000
    )
    @ConfigItem(
            keyName = "DelayTick",
            name = "Delay Ticks",
            description = "Maximum ticks to wait between actions",
            position = 3,
            section = "configurationTitle"
    )
    default int maxTick() {
        return 8;
    }

    /*

     **Configuration UI section**

     */

    @ConfigItem(
            keyName = "enableTickDelay",
            name = "Display Tick Delay",
            description = "Display Tick Delay",
            position = 3,
            section = "configurationUITitle"
    )
    default boolean enableTickDelay() {
        return true;
    }

    /*

     **Debug section**

     */

    @ConfigItem(
            keyName = "debug",
            name = "Enable debug",
            description = "if you check this, you will have your chatbox spammed!",
            position = -1,
            section = "debugTitle"
    )
    default boolean debug(){
        return false;
    }

    enum Route {
        SIXTEEN,
        TWENTY,
    }

}
