package net.runelite.client.plugins.toa;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.toa.config.FontStyle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;
import net.runelite.client.ui.overlay.components.ComponentOrientation;

import java.awt.*;

@ConfigGroup("toa")
public interface ToaConfig extends Config
{


	@ConfigSection(
		name = "TOA: General settings",
		description = "",
		position = 18,
		closedByDefault = true
	)
	String toaGen = "TOA: General settings";

	@Alpha
	@ConfigItem(
		position = 0,
		keyName = "trueTileColorToa",
		name = "True tile color",
		description = "Sets color of npc highlights",
		section = toaGen
	)
	default Color trueTileColorToa()
	{
		return Color.BLACK;
	}

	@ConfigItem(
		name = "Font style",
		description = "Font style can be bold, plain, or italicized.",
		position = 0,
		keyName = "fontStylePAT",
		section = toaGen
	)
	default FontStyle fontStylePAT()
	{
		return FontStyle.BOLD;
	}

	@Range(max = 20)
	@ConfigItem(
		position = 0,
		keyName = "theatreFontSize",
		name = "Theatre Overlay Font Size",
		description = "Sets the font size for all theatre text overlays.",
		section = toaGen
	)
	default int theatreFontSize()
	{
		return 12;
	}


	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "trueTileFillColorToa",
		name = "True Tile Fill Color",
		description = "Sets the fill color of npc highlights",
		section = toaGen
	)
	default Color trueTileFillColorToa()
	{
		return new Color(0, 255, 255, 20);
	}

	@Alpha
	@ConfigItem(
		position = 3,
		keyName = "dangerTileColorToa",
		name = "Danger tile color",
		description = "Sets color of dangerTiles",
		section = toaGen
	)
	default Color dangerTileColorToa()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "dangerTileFillColorToa",
		name = "Danger Tile Fill Color",
		description = "Sets the fill color of npc highlights",
		section = toaGen
	)
	default Color dangerTileFillColorToa()
	{
		return new Color(255, 0, 0, 20);
	}

	@ConfigItem(
		position = 1000,
		keyName = "toaDebug",
		name = "Toa Debug Info",
		description = "Dev tool to show info about Toa",
		section = toaGen
	)
	default boolean toaDebug()
	{
		return false;
	}

	@ConfigSection(
		name = "TOA: Het path",
		description = "",
		position = 19,
		closedByDefault = true
	)
	String toaHet = "TOA: Het path";

	@ConfigItem(
		name = "Akkha: Wrong prayer",
		description = "Outline the Akkha when incorrectly praying against its current attack style.",
		position = 1,
		keyName = "AkkhaOverlayWrongPrayerOutline",
		section = toaHet
	)
	default boolean AkkhaOverlayWrongPrayerOutline() {
		return true;
	}

	@ConfigItem(
		name = "Akkha: Elements sequence",
		description = "Renders the tick counter sequence of elements during Akkha.",
		position = 2,
		keyName = "AkkhaSequence",
		section = toaHet
	)
	default boolean AkkhaSequence() {
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "orbTrueTile",
		name = "Orb True Tile ",
		description = "Highlights true tile of orbs.",
		section = toaHet
	)
	default boolean orbTrueTile()
	{
		return true;
	}


	@ConfigItem(
		name = "Orbs: Hide orbs",
		description = "Unrender the orbs",
		position = 4,
		keyName = "HideOrbs",
		section = toaHet
	)
	default boolean hideOrbs() {
		return false;
	}

	@ConfigSection(
		name = "TOA: Crondis path",
		description = "",
		position = 20,
		closedByDefault = true
	)
	String toaCrondis = "TOA: Crondis path";
	@ConfigItem(
		position = 0,
		keyName = "waveTrueTile",
		name = "Wave true tile",
		description = "Highlights true tile of wave.",
		section = toaCrondis
	)
	default boolean waveTrueTile()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "boulderTrueTile",
		name = "Highlight Boulder",
		description = "Highlights true tile of boulder.",
		section = toaCrondis
	)
	default boolean boulderTrueTile()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "poisonTileToa",
		name = "Highlight Poison",
		description = "Highlights tile of poison.",
		section = toaCrondis
	)
	default boolean poisonTileToa()
	{
		return true;
	}

	@ConfigSection(
		name = "TOA: Scarabas path",
		description = "",
		position = 21,
		closedByDefault = true
	)
	String toaScarabas= "TOA: Scarabas path";

	@ConfigItem(
		position = 0,
		keyName = "kephriAttackRadius",
		name = "Kephri Attack Radius",
		description = "Highlights tiles of Kephri's attack.",
		section = toaScarabas
	)
	default boolean kephriAttackRadius()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "fliesOnCharacter",
		name = "Flies attack",
		description = "Highlights flies",
		section = toaScarabas
	)
	default boolean fliesOnCharacter()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "hideUnattackableSwams",
		name = "Hide unattackable swarms",
		description = "Hide unattackable swarms",
		section = toaScarabas
	)
	default boolean hideUnattackableSwams()
	{
		return true;
	}

	@ConfigItem(
		position = 10,
		keyName = "scarabasAttackRadius",
		name = "Boulder Attack Radius",
		description = "Highlights tiles of Boulder attack.",
		section = toaScarabas
	)
	default boolean scarabasAttackRadius()
	{
		return true;
	}

	@ConfigItem(
		position = 11,
		keyName = "scarabasPuzzleSolver",
		name = "Solve matching puzzles",
		description = "Solve matching puzzles.",
		section = toaScarabas
	)
	default boolean scarabasPuzzleSolver()
	{
		return true;
	}

	@ConfigSection(
		name = "TOA: Apmeken path",
		description = "",
		position = 22,
		closedByDefault = true
	)
	String toaApmeken= "TOA: Apmeken path";

	@ConfigSection(
		name = "TOA: Wardens",
		description = "",
		position = 31,
		closedByDefault = true
	)
	String toaWardens= "TOA: Wardens";

	@ConfigItem(
		position = 0,
		keyName = "bouldersTrueTileBaba",
		name = "Boulder true tile",
		description = "Highlights true tile of boulders.",
		section = toaApmeken
	)
	default boolean bouldersTrueTileBaba()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "shockwaveRadiusToa",
		name = "Shockwave Radius",
		description = "Highlights the shockwave Radius",
		section = toaApmeken
	)
	default boolean shockwaveRadiusToa()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "boulderDangerToa",
		name = "Falling boulders",
		description = "Highlights the falling boulders",
		section = toaApmeken
	)
	default boolean boulderDangerToa()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "bananasToa",
		name = "Highlight Bananas",
		description = "Highlights the bananas",
		section = toaApmeken
	)
	default boolean bananasToa()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "sacrophagusToa",
		name = "Sacrophagus flame",
		description = "Highlights the sacrophagus flame",
		section = toaApmeken
	)
	default boolean sacrophagusToa()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = "ticksrenderOnBaba",
		name = "Ticks on Baba",
		description = "Renders ticks till next attack on baba.",
		section = toaApmeken
	)
	default boolean babaRenderTicks()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = "MsgtoPub",
		name = "Send call automatically to pub",
		description = "Calls messages out to public chat",
		section = toaApmeken
	)
	default boolean sendToPubChat()
	{
		return true;
	}

	@ConfigSection(
		name = "TOA: Prayer helper",
		description = "",
		position = 30,
		closedByDefault = true
	)
	String toaPrayer = "TOA: Prayer helper";

	@ConfigItem(
		position = 11,
		keyName = "prayerHelperToa",
		name = "Prayer Helper",
		description = "Display prayer indicator in the prayer tab or in the bottom right corner of the screen",
		section = toaPrayer
	)
	default boolean prayerHelperToa()
	{
		return true;
	}

	@ConfigItem(
		position = 13,
		keyName = "descendingBoxesToa",
		name = "Prayer Descending Boxes",
		description = "Draws timing boxes above the prayer icons, as if you were playing Guitar Hero",
		section = toaPrayer
	)
	default boolean descendingBoxesToa()
	{
		return true;
	}

	@ConfigItem(
		position = 14,
		keyName = "alwaysShowPrayerHelperToa",
		name = "Always Show Prayer Helper",
		description = "Render prayer helper at all time, even when other inventory tabs are open.",
		section = toaPrayer
	)
	default boolean alwaysShowPrayerHelperToa()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 15,
		keyName = "prayerColorToa",
		name = "Box Color",
		description = "Color for descending box normal",
		section = toaPrayer
	)
	default Color prayerColorToa()
	{
		return Color.ORANGE;
	}

	@Alpha
	@ConfigItem(
		position = 16,
		keyName = "prayerColorDangerToa",
		name = "Box Color Danger",
		description = "Color for descending box one tick before damage",
		section = toaPrayer
	)
	default Color prayerColorDangerToa()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 17,
		keyName = "indicateNonPriorityDescendingBoxesToa",
		name = "Indicate Non-Priority Boxes",
		description = "Render descending boxes for prayers that are not the priority prayer for that tick",
		section = toaPrayer
	)
	default boolean indicateNonPriorityDescendingBoxesToa()
	{
		return true;
	}

	@ConfigItem(
		position = 12,
		keyName = "prayerInfoboxToa",
		name = "Prayer Infobox",
		description = "Renders a prayer infobox for attacks in ToA",
		section = toaPrayer
	)
	default boolean prayerInfoboxToa()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "zebakPrayerHelper",
		name = "Zebak prayer helper",
		description = "Render prayers during the Zebak fight",
		section = toaPrayer
	)


	default boolean zebakPrayerHelper()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "Wardenballticks",
		name = "Waden Ballticks onplayer",
		description = "Shows ticks till ball hits player",
		section = toaWardens
	)
	default boolean wardenObeliskBallTicks()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "boulderoutline",
		name = "Boulder falling P3 tile outline",
		description = "",
		section = toaWardens
	)
	default Color wardenBoulderOutline()
	{
		return new Color(255, 0, 0, 20);
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "boulderFill",
		name = "Boulder falling P3 tile fill",
		description = "",
		section = toaWardens
	)

	default Color wadernBoulderFill()
	{
		return new Color(255, 0, 0, 20);
	}

	@ConfigSection(
		name = "TOA: Automation",
		description = "",
		position = 18,
		closedByDefault = true
	)

	String toaauto = "TOA: Automation";

	@ConfigItem(
		position = 0,
		keyName = "warning",
		name = "WARNING",
		warning = "THESE PORTIONS OF THE PLUGIN AUTOMATICALLY SEND CLIENT DATA. BEST PRACTICE is USED and Tabs are forced. WILL AUTOMATICALLY DISABLE ON CLIENT REVS and PREVENT itself from being turned on",
		description = "AUTOMATION IS RISKY, if off, nothing else below will work",
		section = toaauto
	)
	default boolean warning()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "PrayerSwitcher",
		name = "autoPraySwapZeb",
		description = "Automatically flicks defensive prayers at zebak",
		section = toaauto
	)

	default boolean zebakprayFlick()
	{
		return false;
	}



	@ConfigItem(
		position = 3,
		keyName = "PrayerSwitcher1",
		name = "autoPraySwapWard",
		description = "Automatically flicks defensive prayers at warden",
		section = toaauto
	)

	default boolean wardenprayFlick() { return false; }



	@Range(
		min = 1,
		max = 4
	)
	@ConfigItem(
		position = 4,
		keyName = "ticks",
		name = "Ticks before attack to pray",
		description = "How many ticks before the attack it will start praying",
		section = toaauto
	)

	default int tickstowait() { return 2; }

	@ConfigItem(
		position = 9,
		keyName = "flickpray",
		name = "FLicks Prayer off",
		description = "WIUll turn pray off/on to save prayers",
		section = toaauto
	)

	default boolean flickPrayer() { return false; }



}
