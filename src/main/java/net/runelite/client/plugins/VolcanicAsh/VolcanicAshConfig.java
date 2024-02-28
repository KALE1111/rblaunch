package net.runelite.client.plugins.VolcanicAsh;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.VolcanicAsh.UiLayoutOption;

@ConfigGroup("autoVolcanicAshConfig")
public interface VolcanicAshConfig extends Config {

    @ConfigSection(
            name = "Instructions",
            description = "Instructions on how to use the plugin",
            position = 0
    )
    String instructionsSection = "instructionsSection";

    @ConfigItem(
            position = 1,
            keyName = "random",
            name = "Plugin Instructions",
            description = "Detailed instructions on how to use the plugin",
            section = instructionsSection
    )
    default String instructionsText() {
        return "Equip a pickaxe and start at the Volcano in Verdant Valley on Fossil Island. The plugin will automatically mine the volcanic ash.";
    }

    @ConfigSection(
            name = "UI Settings",
            description = "Settings related to the user interface",
            position = 4
    )
    String uiSettings = "uiSettings";

    @ConfigItem(
            keyName = "uiLayout",
            name = "UI Layout",
            description = "Select the UI layout for the overlay",
            section = uiSettings,
            position = 5
    )
    default UiLayoutOption uiLayout() {
        return UiLayoutOption.FULL;

    }
}













