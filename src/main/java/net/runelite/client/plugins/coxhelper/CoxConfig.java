package net.runelite.client.plugins.coxhelper;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("Cox")
public interface CoxConfig extends Config {
   @ConfigSection(
      position = 1,
      name = "Muttadile",
      description = ""
   )
   String muttadileTitle = "Muttadile";
   @ConfigSection(
      position = 3,
      name = "Tekton",
      description = ""
   )
   String tektonTitle = "Tekton";
   @ConfigSection(
      position = 5,
      name = "Guardians",
      description = ""
   )
   String guardiansTitle = "Guardians";
   @ConfigSection(
      position = 7,
      name = "Vanguards",
      description = ""
   )
   String vanguardsTitle = "Vanguards";
   @ConfigSection(
      position = 10,
      name = "Olm",
      description = ""
   )
   String olmTitle = "Olm";
   @ConfigSection(
      position = 17,
      name = "Colors",
      description = ""
   )
   String colors = "Colors";
   @ConfigSection(
      position = 25,
      name = "Text",
      description = ""
   )
   String text = "Text";

   @ConfigItem(
      position = 2,
      keyName = "muttadile2",
      name = "Muttadile Marker",
      description = "Places an overlay around muttadiles showing their melee range.",
      section = "Muttadile"
   )
   default boolean muttadile() {
      return true;
   }

   @ConfigItem(
      position = 4,
      keyName = "tekton3",
      name = "Tekton Marker",
      description = "Places an overlay around Tekton showing his melee range.",
      section = "Tekton"
   )
   default boolean tekton() {
      return true;
   }

   @ConfigItem(
      position = 4,
      keyName = "tektonTickCounter3",
      name = "Tekton Tick Counters",
      description = "Counts down current phase timer, and attack ticks.",
      section = "Tekton"
   )
   default boolean tektonTickCounter() {
      return true;
   }

   @ConfigItem(
      position = 6,
      keyName = "guardians3",
      name = "Guardians Overlay",
      description = "Places an overlay near Guardians showing safespot.",
      section = "Guardians"
   )
   default boolean guardians() {
      return true;
   }

   @ConfigItem(
      position = 6,
      keyName = "guardinTickCounter3",
      name = "Guardians Tick Timing",
      description = "Places an overlay on Guardians showing attack tick timers.",
      section = "Guardians"
   )
   default boolean guardinTickCounter() {
      return true;
   }

   @ConfigItem(
      position = 8,
      keyName = "vangHighlight3",
      name = "Highlight Vanguards",
      description = "Color is based on their attack style.",
      section = "Vanguards"
   )
   default boolean vangHighlight() {
      return true;
   }

   @ConfigItem(
      position = 9,
      keyName = "vangHealth3",
      name = "Show Vanguards Current HP",
      description = "This will create an infobox with vanguards current hp.",
      section = "Vanguards"
   )
   default boolean vangHealth() {
      return true;
   }

   @ConfigItem(
      position = 11,
      keyName = "prayAgainstOlm3",
      name = "Olm Show Prayer",
      description = "Shows what prayer to use during olm.",
      section = "Olm"
   )
   default boolean prayAgainstOlm() {
      return true;
   }

   @Range(
      min = 40,
      max = 100
   )
   @ConfigItem(
      position = 11,
      keyName = "prayAgainstOlmSize2",
      name = "Olm Prayer Size",
      description = "Change the Size of the Olm Infobox.",
      section = "Olm"
   )
   @Units("px")
   default int prayAgainstOlmSize() {
      return 40;
   }

   @ConfigItem(
      position = 12,
      keyName = "timers3",
      name = "Olm Show Burn/Acid Timers",
      description = "Shows tick timers for burns/acids.",
      section = "Olm"
   )
   default boolean timers() {
      return true;
   }

   @ConfigItem(
      position = 13,
      keyName = "tpOverlay3",
      name = "Olm Show Teleport Overlays",
      description = "Shows Overlays for targeted teleports.",
      section = "Olm"
   )
   default boolean tpOverlay() {
      return true;
   }

   @ConfigItem(
      position = 14,
      keyName = "olmTick3",
      name = "Olm Tick Counter",
      description = "Show Tick Counter on Olm",
      section = "Olm"
   )
   default boolean olmTick() {
      return true;
   }

   @ConfigItem(
      position = 15,
      keyName = "olmDebug3",
      name = "Olm Debug Info",
      description = "Dev tool to show info about olm",
      section = "Olm"
   )
   default boolean olmDebug() {
      return false;
   }

   @ConfigItem(
      position = 16,
      keyName = "olmPShowPhase3",
      name = "Olm Phase Type",
      description = "Will highlight olm depending on which phase type is active. Red=Flame Green=Acid Purple=Crystal",
      section = "Olm"
   )
   default boolean olmPShowPhase() {
      return false;
   }

   @ConfigItem(
      position = 18,
      keyName = "muttaColor3",
      name = "Muttadile Tile Color",
      description = "Change hit area tile color for muttadiles",
      section = "Colors"
   )
   default Color muttaColor() {
      return new Color(0, 255, 99);
   }

   @ConfigItem(
      position = 19,
      keyName = "guardColor3",
      name = "Guardians Tile Color",
      description = "Change safespot area tile color for Guardians",
      section = "Colors"
   )
   default Color guardColor() {
      return new Color(0, 255, 99);
   }

   @ConfigItem(
      position = 20,
      keyName = "tektonColor3",
      name = "Tekton Tile Color",
      description = "Change hit area tile color for Tekton",
      section = "Colors"
   )
   default Color tektonColor() {
      return new Color(193, 255, 245);
   }

   @ConfigItem(
      position = 21,
      keyName = "burnColor3",
      name = "Burn Victim Color",
      description = "Changes tile color for burn victim.",
      section = "Colors"
   )
   default Color burnColor() {
      return new Color(255, 100, 0);
   }

   @ConfigItem(
      position = 22,
      keyName = "acidColor3",
      name = "Acid Victim Color",
      description = "Changes tile color for acid victim.",
      section = "Colors"
   )
   default Color acidColor() {
      return new Color(69, 241, 44);
   }

   @ConfigItem(
      position = 23,
      keyName = "tpColor3",
      name = "Teleport Target Color",
      description = "Changes tile color for teleport target.",
      section = "Colors"
   )
   default Color tpColor() {
      return new Color(193, 255, 245);
   }

   @ConfigItem(
      position = 24,
      keyName = "olmSpecialColor3",
      name = "Olm Special Color",
      description = "Changes color of a special on Olm's tick counter",
      section = "Colors"
   )
   default Color olmSpecialColor() {
      return new Color(89, 255, 0);
   }

   @ConfigItem(
      position = 26,
      keyName = "fontStyle3",
      name = "Font Style",
      description = "Bold/Italics/Plain",
      section = "Text"
   )
   default FontStyle fontStyle() {
      return FontStyle.BOLD;
   }

   @Range(
      min = 9,
      max = 20
   )
   @ConfigItem(
      position = 27,
      keyName = "textSize3",
      name = "Text Size",
      description = "Text Size for Timers.",
      section = "Text"
   )
   @Units("pt")
   default int textSize() {
      return 14;
   }

   @ConfigItem(
      position = 28,
      keyName = "shadows3",
      name = "Shadows",
      description = "Adds Shadows to text.",
      section = "Text"
   )
   default boolean shadows() {
      return true;
   }

   public static enum FontStyle {
      BOLD("Bold", 1),
      ITALIC("Italic", 2),
      PLAIN("Plain", 0);

      private final String name;
      private final int font;

      public String toString() {
         return this.getName();
      }

      public String getName() {
         return this.name;
      }

      public int getFont() {
         return this.font;
      }

      private FontStyle(String name, int font) {
         this.name = name;
         this.font = font;
      }
   }
}
