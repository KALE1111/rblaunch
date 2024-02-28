package net.runelite.client.plugins.RawKarambwanji;

import net.runelite.api.ItemID;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autoKarambwanjiConfig")
public interface KarambwanjiConfig extends Config {

    @ConfigSection(
            name = "Instructions",
            description = "Instructions on how to use the plugin",
            position = 0
    )
    String instructionsSection = "instructionsSection";

    @ConfigItem(
            position = 1,
            keyName = "instructionsText",
            name = "Plugin Instructions",
            description = "Detailed instructions on how to use the plugin",
            section = instructionsSection
    )
    default String instructionsText() {
        return "Fishes raw Karambwanji at the fishing spot. Requires a small fishing net and for you to be near the fishing area.";
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
