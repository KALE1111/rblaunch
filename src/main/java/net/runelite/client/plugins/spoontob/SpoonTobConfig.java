package net.runelite.client.plugins.spoontob;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("tobqol v2")
public interface SpoonTobConfig extends Config {
    @ConfigSection(
            name = "Maiden",
            description = "Maiden settings",
            position = 1,
            closedByDefault = true
    )
    String maiden = "maiden";

    @ConfigSection(
            name = "Bloat",
            description = "Bloat settings",
            position = 2,
            closedByDefault = true
    )
    String bloat = "bloat";

    @ConfigSection(
            name = "Nylocas",
            description = "Nylocas settings",
            position = 3,
            closedByDefault = true
    )
    String nylocas = "nylocas";

    @ConfigSection(
            name = "Sotetseg",
            description = "Sotetseg settings",
            position = 4,
            closedByDefault = true
    )
    String sotetseg = "sotetseg";

    @ConfigSection(
            name = "Xarpus",
            description = "Xarpus settings",
            position = 5,
            closedByDefault = true
    )
    String xarpus = "xarpus";

    @ConfigSection(
            name = "Verzik",
            description = "Verzik settings",
            position = 6,
            closedByDefault = true
    )
    String verzik = "verzik";

    @ConfigSection(
            name = "Misc",
            description = "Misc settings",
            position = 7,
            closedByDefault = true
    )
    String misc = "misc";

    @ConfigSection(
            name = "Font Settings",
            description = "Font settings",
            position = 8,
            closedByDefault = true
    )
    String font = "font";

    //------------------------------------------------------------//
    // Maiden
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "MaidenTc1",
            name = "Show Maiden Tick Counter",
            description = "Show a Tick timer on the boss indicating time until next attack",
            section = maiden
    )
    default boolean maidenTickCounter() {return false;}

    @ConfigItem(
            position = 1,
            keyName = "leaked1",
            name = "Show Leaked Message",
            description = "For all those shit freezers out there",
            section = maiden
    )
    default boolean leakedMessage() { return false; }

    @ConfigItem(
            position = 2,
            keyName = "maidenProc1",
            name = "Maiden Proc Threshold",
            description = "Shows a rough estimate on the damage needed to proc next phase",
            section = maiden
    )
    default boolean maidenProcThreshold()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            name = "Maiden Max Hit (Tooltip)",
            keyName = "mMHit",
            description = "When hovering over Maiden's clickbox it will display her max hits for:<br>No Prayer<br>Prayer<br>Elysian Spirit Shield",
            section = maiden
    )
    default MaidenMaxHitTTMode maidenMaxHit() {
        return MaidenMaxHitTTMode.OFF;
    }

    @ConfigItem(
            position = 4,
            name = "Maiden Max Hit (Overlay)",
            keyName = "mMHO",
            description = "Overlay that will display her max hits for:<br>No Prayer<br>Prayer<br>Elysian Spirit Shield",
            section = maiden
    )
    default MaidenMaxHit maidenMaxHitOverlay() {
        return MaidenMaxHit.OFF;
    }

    @ConfigItem(
            position = 5,
            name = "<html><p style=\"color:#25C54F\">—————— Nylocas Matomenos</p></html>",
            keyName = "maiden div",
            description = "",
            section = maiden
    )
    void maidenDivider1();

    @ConfigItem(
            position = 6,
            keyName = "MaidencolNylos1",
            name = "Recolor HP Nylo Menu",
            description = "Recolor nylos in right click menu based on their HP and adds the HP % next to the name.",
            section = maiden
    )
    default boolean maidenRecolourNylos() {
        return false;
    }

    @ConfigItem(
            position = 7,
            keyName = "maidenFrzTimer1",
            name = "Nylo Freeze Timers",
            description = "Displays how long each Nylocas Matomenos is frozen for",
            section = maiden
    )
    default maidenFreezeTimerMode maidenFreezeTimer() { return maidenFreezeTimerMode.OFF; }

    @ConfigItem(
            position = 8,
            keyName = "CrabsDistance332",
            name = "Show Crabs Distance",
            description = "You really need me to explain this?",
            section = maiden
    )
    default boolean showMaidenCrabsDistance() { return false; }

    @ConfigItem(
            position = 9,
            keyName = "singleLineD332",
            name = "Single Line Crabs Distance",
            description = "Makes the crabs distance and hp % a single line",
            section = maiden
    )
    default boolean singleLineDistance() { return false; }

    @ConfigItem(
            position = 10,
            keyName = "showFD",
            name = "Show Distance When Frozen",
            description = "Shows the distance overlay on the crabs when they are frozen",
            section = maiden

    )
    default boolean showFrozenDistance() { return false; }

    @ConfigItem(
            position = 11,
            keyName = "distanceCol112",
            name = "Crabs Distance Color",
            description = "You really need me to explain this?",
            section = maiden

    )
    default Color distanceColor() { return Color.WHITE; }

    @ConfigItem(
            position = 12,
            keyName = "showMaidenCrabHp221",
            name = "Show Crabs HP",
            description = "Crab HP show do",
            section = maiden
    )
    default boolean showMaidenCrabHp() { return false; }


    @ConfigItem(
            position = 13,
            keyName = "maidenScuffed21",
            name = "Scuffed Crab Outline",
            description = "Spawn right you little shit",
            section = maiden
    )
    default boolean maidenScuffedCrab() { return false; }

    @ConfigItem(
            position = 14,
            keyName = "maidenScufCCol",
            name = "Scuffed Crab Color",
            description = "Sets the color for Scuffed Crab Outline",
            section = maiden

    )
    default Color maidenScuffedCrabColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            position = 15,
            keyName = "hpcrab12",
            name = "Prioritize Highest Hp Crab",
            description = "Swaps menu entries so the highest HP crab is left click",
            section = maiden
    )
    default boolean maidenCrabHpPriority() {return false;}

    @ConfigItem(
            position = 16,
            name = "Reds Freeze Warning",
            keyName = "redwarn1",
            description = "Highlights the N3 and S3 crabs when the north mager cannot freeze them in time <br> Must be on Ancient spellbook and be 4 or 5 man scale",
            section = maiden
    )
    default boolean redsFreezeWarning() {
        return false;
    }

    @ConfigItem(
            position = 17,
            name = "Reds Freeze Warning Color",
            keyName = "redfrzwarncol2",
            description = "Color of the N3 crabs when the north mager cannot freeze them in time",
            section = maiden

    )
    default Color redsFreezeWarningColor() {
        return Color.RED;
    }

    @ConfigItem(
            position = 18,
            name = "<html><p style=\"color:#25C54F\">—————— Blood Spawns</p></html>",
            keyName = "maiden div2",
            description = "",
            section = maiden
    )
    void maidenDivider2();

    @ConfigItem(
            position = 19,
            keyName = "mBl2",
            name = "Show Maiden Blood Toss",
            description = "Displays the tile location where tossed blood will land.",
            section = maiden
    )
    default maidenBloodSplatMode maidenBlood() {
        return maidenBloodSplatMode.COLOR;
    }

    @Alpha
    @ConfigItem(
            position = 20,
            keyName = "bTossCol332",
            name = "Blood Toss Color",
            description = "Colors the tile where blood will land",
            section = maiden
    )
    default Color bloodTossColour() {
        return new Color(0, 255, 255, 150);
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 21,
            keyName = "bTossFill",
            name = "Blood Toss Opacity",
            description = "Changes the opacity of the blood toss highlight",
            section = maiden
    )
    default int bloodTossFill() {
        return 10;
    }

    @ConfigItem(
            position = 22,
            keyName = "bloodTossT221",
            name = "Blood Toss Ticks",
            description = "Show the ticks until the blood splat lands",
            section = maiden
    )
    default boolean bloodTossTicks() {
        return false;
    }

    @ConfigItem(
            position = 23,
            keyName = "mSpawn66",
            name = "Show Blood Spawns True Tile",
            description = "Show the tiles that blood spawns will travel to.",
            section = maiden
    )
    default boolean maidenSpawns() {
        return false;
    }

    @ConfigItem(
            position = 24,
            keyName = "mSpTrail22",
            name = "Show Blood Spawns Trailing Tile",
            description = "Shows the trailing tile of the blood spawns location.",
            section = maiden

    )
    default boolean maidenSpawnsTrail() {
        return false;
    }

    @ConfigItem(
            position = 25,
            keyName = "bSpawnCol22",
            name = "Blood Spawns Color",
            description = "Color of the tiles that blood spawns will travel to.",
            section = maiden
    )
    default Color bloodSpawnsColor() {
        return new Color(0, 150, 200);
    }

    @ConfigItem(
            position = 26,
            keyName = "rMB",
            name = "Remove Blood Spawns",
            description = "Yup... cast, attack, both",
            section = maiden
    )
    default maidenBloodsMode removeMaidenBloods() { return maidenBloodsMode.BOTH; }

    @ConfigItem(
            position = 27,
            keyName = "bSpFrzT",
            name = "Blood Spawn Freeze Timer",
            description = "y freeze?",
            section = maiden
    )
    default boolean bloodSpawnFreezeTimer() { return false; }

    //------------------------------------------------------------//
    // Bloat
    //------------------------------------------------------------//
    @ConfigItem(
            position = 1,
            keyName = "bloatI33",
            name = "Bloat Status",
            description = "Display Bloat's status (asleep, awake, enrage) using color codes.",
            section = bloat
    )
    default BloatIndicatorMode bloatIndicator() {
        return BloatIndicatorMode.TILE;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "bIColorUPp33",
            name = "Bloat Up",
            description = "Select a color for when Bloat is UP.",
            section = bloat
    )
    default Color bloatIndicatorColorUP() {return new Color(223, 109, 255, 150);}

    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "bIndiCoTHRESH221",
            name = "Bloat Down Warning",
            description = "Select a color for when Bloat UP and goes over 37 ticks, which allows you to know when he can go down.",
            section = bloat
    )
    default Color bloatIndicatorColorTHRESH() {return new Color(255, 200, 0, 150);}

    @Alpha
    @ConfigItem(
            position = 4,
            keyName = "bIndiColDOWN221",
            name = "Bloat Down",
            description = "Select a color for when Bloat is DOWN.",
            section = bloat
    )
    default Color bloatIndicatorColorDOWN() {return new Color(0, 255, 0, 150);}

    @Alpha
    @ConfigItem(
            position = 5,
            keyName = "bIndiColWARN131",
            name = "Bloat Stomp Warning",
            description = "Select a color for when Bloat is DOWN and about to get UP.",
            section = bloat
    )
    default Color bloatIndicatorColorWARN() {return new Color(255, 0, 0, 150);}

    @ConfigItem(
            position = 6,
            keyName = "bUpT331",
            name = "Bloat Timer",
            description = "Show the estimated time when Bloat will stop moving.",
            section = bloat
    )
    default boolean bloatUpTimer() {
        return false;
    }

    @ConfigItem(
            position = 7,
            keyName = "bEntT",
            name = "Bloat Entry Timer",
            description = "Shows the ticks since entering the Bloat region. Disappears once you start Bloat",
            section = bloat
    )
    default boolean bloatEntryTimer() {
        return false;
    }

    @ConfigItem(
            position = 8,
            keyName = "bRevNoti221",
            name = "Bloat Turn",
            description = "Plays a sound to let you know when bloat changes direction",
            section = bloat
    )
    default bloatTurnMode bloatReverseNotifier() {
        return bloatTurnMode.OFF;
    }

    @Range(max = 100)
    @ConfigItem(
            position = 9,
            keyName = "revVol2",
            name = "Turn Volume",
            description = "Cha cha real smooth",
            section = bloat
    )
    default int reverseVolume() {
        return 50;
    }

    @ConfigItem(
            position = 10,
            name = "<html><p style=\"color:#25C54F\">—————— Hands</p></html>",
            keyName = "b div1",
            description = "",
            section = bloat
    )
    void bloatDivider1();

    @ConfigItem(
            position = 11,
            keyName = "sBH",
            name = "Show Bloat Hands",
            description = "Highlights the falling hands inside Bloat.",
            section = bloat
    )
    default bloatHandsMode showBloatHands() {
        return bloatHandsMode.COLOR;
    }

    @ConfigItem(
            position = 12,
            keyName = "bHT",
            name = "Bloat Hands Ticks",
            description = "Shows the ticks till the hands hit the ground",
            section = bloat
    )
    default boolean bloatHandsTicks() {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 13,
            keyName = "bC",
            name = "Hands Color",
            description = "Bloat Hands Color",
            section = bloat
    )
    default Color bloatHandColor() {
        return new Color(106, 61, 255, 255);
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 14,
            keyName = "bCF",
            name = "Hands Color Opacity",
            description = "Changes the opacity of the bloat hands highlight",
            section = bloat
    )
    default int bloatColorFill() {
        return 10;
    }

    @ConfigItem(
            position = 15,
            name = "<html><p style=\"color:#25C54F\">—————— Misc</p></html>",
            keyName = "b d3",
            description = "",
            section = bloat
    )
    void bloatDivider2();

    @ConfigItem(
            position = 16,
            keyName = "hideAnnoyAssObj",
            name = "Hide Objects",
            description = "Hides annoying objects in the bloat room",
            section = bloat
    )
    default annoyingObjectHideMode hideAnnoyingAssObjects() {
        return annoyingObjectHideMode.CHAINS;
    }

    @ConfigItem(
            position = 17,
            keyName = "bSM33",
            name = "Stomp Safespots",
            description = "Shows lines for where you should go to flinch bloat stomps",
            section = bloat
    )
    default bloatStompMode bloatStompMode() {
        return bloatStompMode.COLOR;
    }

    @ConfigItem(
            position = 18,
            keyName = "bSC3",
            name = "Stomp Color",
            description = "Color of the stomp lines",
            section = bloat
    )
    @Alpha
    default Color bloatStompColor() {
        return new Color(0, 255, 0, 100);
    }

    @ConfigItem(
            position = 19,
            keyName = "blStW4",
            name = "Stomp Width",
            description = "Girth",
            section = bloat
    )
    @Range(max = 3, min = 1)
    default int bloatStompWidth() {
        return 1;
    }

    //------------------------------------------------------------//
    // Nylo
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "wcW2",
            name = "Wheelchair Nylos",
            description = "Removes attack options on wrong style nylos when weapons are equipped",
            section = nylocas
    )
    default wheelchairMode wheelchairNylo() {
        return wheelchairMode.BOTH;
    }

    @ConfigItem(
            position = 1,
            keyName = "ignCh44",
            name = "Wheelchair - Ignore Chins",
            description = "Ignores wheelchair settings if you equip chins (aka lets you attack the wrong styles with chins)",
            section = nylocas
    )
    default boolean ignoreChins() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "manC3",
            name = "Wheelchair - Manual Cast Wands",
            description = "Only lets you manually cast spells on all nylos when a wand or staff is equipped. Ignores tridents/sangs",
            section = nylocas
    )
    default boolean manualCast() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "shoPCng22",
            name = "Show Boss Phase Change",
            description = "Shows how long until the boss changes phases. Both includes demiboss",
            section = nylocas
    )
    default nyloBossPhaseChange showPhaseChange() {
        return nyloBossPhaseChange.BOSS;
    }

    @ConfigItem(
            position = 3,
            name = "<html><p style=\"color:#25C54F\">——————</p></html>",
            keyName = "n dm3",
            description = "",
            section = nylocas
    )
    void nyloDivider1();

    @Range(max = 52)
    @ConfigItem(
            position = 4,
            keyName = "nylExplDispT",
            name = "Display Last Ticks",
            description = "Displays the last 'x' amount of ticks for a Nylocas. (ex: to see the last 10 ticks, you set it to 10).",
            section = nylocas
    )
    default int nyloExplosionDisplayTicks()
    {
        return 52;
    }

    @ConfigItem(
            position = 5,
            keyName = "nylTilCS",
            name = "Nylocas Tick Time Alive Style",
            description = "Count up or Count down options on the tick time alive.",
            section = nylocas
    )
    default nylotimealive nyloTimeAliveCountStyle() { return nylotimealive.COUNTUP;}

    @ConfigItem(
            position = 6,
            keyName = "sholocasExpl",
            name = "Explosion Warning",
            description = "Displays ticks until explosion, a yellow tile, or both.",
            section = nylocas
    )
    default ExplosionWarning showNylocasExplosions() {
        return ExplosionWarning.OFF;
    }

    @ConfigItem(
            position = 7,
            keyName = "loExplTyp",
            name = "Explosion Mode",
            description = "Display nylo explosion as either tile or explosion radius",
            section = nylocas
    )
    default nyloExplosionType nyloExplosionType() {
        return nyloExplosionType.TILE;
    }

    @ConfigItem(
            position = 8,
            keyName = "loRecM",
            name = "Nylocas Recolor Menu",
            description = "Recolors the right click menu to the color of the nylos. Bigs are darker.",
            section = nylocas
    )
    default boolean nyloRecolorMenu() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "loTMen",
            name = "Time Alive Right Click Menu",
            description = "Displays how many ticks the Nylos have left/been alive for in the right click menu <br> Must have Nylocas Recolour Menu on",
            section = nylocas

    )
    default boolean nyloTicksMenu() { return false; }

    @ConfigItem(
            position = 10,
            name = "<html><p style=\"color:#25C54F\">——————</p></html>",
            keyName = "nlo divi",
            description = "",
            section = nylocas
    )
    void nyloDivider2();

    @ConfigItem(
            position = 11,
            keyName = "nloOlay",
            name = "Nylocas Role Overlay",
            description = "Display the interactive overlay allowing you to choose which nylocas to highlight",
            section = nylocas
    )
    default boolean nyloOverlay() {
        return false;
    }

    @Range(min = 0, max = 3)
    @ConfigItem(
            position = 12,
            keyName = "nloTilWid",
            name = "Nylocas Tile Width",
            description = "girth",
            section = nylocas

    )
    default double nyloTileWidth()
    {
        return 1;
    }

    @ConfigItem(
            position = 13,
            keyName = "nloAggrOlay",
            name = "Highlight Aggressive Nylocas",
            description = "Highlight nylocas that are aggressive.",
            section = nylocas
    )
    default aggroStyle nyloAggressiveOverlay() { return aggroStyle.TILE; }

    @ConfigItem(
            position = 14,
            name = "<html><p style=\"color:#25C54F\">—————— Waves</p></html>",
            keyName = "nlo div2",
            description = "",
            section = nylocas
    )
    void nyloDivider3();

    @ConfigItem(
            position = 15,
            keyName = "nloAliCount",
            name = "Nylocas Alive Display",
            description = "Show how many nylocas are alive in the room.",
            section = nylocas
    )
    default boolean nyloAlivePanel() {
        return false;
    }

    @ConfigItem(
            position = 16,
            keyName = "wavSpT",
            name = "Wave Spawn Timer",
            description = "Timer for when them niglets finna pull up",
            section = nylocas
    )
    default waveSpawnTimerMode waveSpawnTimer() {
        return waveSpawnTimerMode.OFF;
    }

    @ConfigItem(
            position = 17,
            keyName = "wavSpTimCol",
            name = "Wave Timer Color",
            description = "Sets color of Wave Spawn Timer overlay",
            section = nylocas
    )
    default Color waveSpawnTimerColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            position = 20,
            keyName = "sBSplit",
            name = "Show Big Splits",
            description = "Marks where a big nylo died and how long until littles spawn",
            section = nylocas
    )
    default boolean showBigSplits() {
        return true;
    }

    @ConfigItem(
            position = 21,
            keyName = "bColor",
            name = "Big Splits Color",
            description = "Big Splits Color",
            section = nylocas

    )
    default Color bigsColor() {
        return Color.GRAY;
    }

    @ConfigItem(
            position = 22,
            name = "<html><p style=\"color:#25C54F\">—————— Chat Messages</p></html>",
            keyName = "nlo divi4",
            description = "",
            section = nylocas
    )
    void nyloDivider4();

    @ConfigItem(
            position = 23,
            keyName = "nloSM5",
            name = "Nylo Stall Chat Message",
            description = "Display a message in chatbox when a wave stalls.",
            section = nylocas
    )
    default boolean nyloStallMessage() {
        return false;
    }

    @ConfigItem(
            position = 24,
            keyName = "nloSMsg3",
            name = "Nylo Splits Message",
            description = "Shows how many of each boss phase and/or how many small splits you got",
            section = nylocas
    )
    default nyloSplitsMessage nyloSplitsMsg() {
        return nyloSplitsMessage.BOSS;
    }

    @ConfigItem(
            position = 25,
            keyName = "sptMsgTim4",
            name = "Waves Message Timing",
            description = "Shows when to display how many small nylos you got from splits",
            section = nylocas
    )
    default splitsMsgTiming splitMsgTiming() {
        return splitsMsgTiming.FINISHED;
    }

    @ConfigItem(
            position = 26,
            keyName = "sST",
            name = "Small Splits Type",
            description = "Caps = Pre  + Post cap splits, Total is just the total splits throughout the waves, both you don't need an explanation",
            section = nylocas
    )
    default smallSplitsMode smallSplitsType() {
        return smallSplitsMode.TOTAL;
    }

    @ConfigItem(
            position = 29,
            name = "<html><p style=\"color:#25C54F\">—————— Pillars</p></html>",
            keyName = "nlo div5",
            description = "",
            section = nylocas
    )
    void nyloDivider5();

    @ConfigItem(
            position = 30,
            keyName = "nloPill3",
            name = "Show Nylocas Pillar Health",
            description = "Show the health bars of the Nylocas pillars.",
            section = nylocas
    )
    default boolean nyloPillars() {
        return false;
    }

    @ConfigItem(
            position = 31,
            keyName = "showLowP2",
            name = "Show Lowest Pillar Health",
            description = "Puts a hint arrow on the Nylocas pillar with the lowest health.",
            section = nylocas
    )
    default boolean showLowestPillar() {
        return true;
    }

    @ConfigItem(
            position = 32,
            keyName = "hidePill8",
            name = "Hide Pillars",
            description = "Removes the pillars in Nylo and the walls as well if set to clean",
            section = nylocas
    )
    default hidePillarsMode hidePillars() {
        return hidePillarsMode.OFF;
    }

    @ConfigItem(
            position = 33,
            keyName = "hideEggs3",
            name = "Hide Eggs",
            description = "You're an idiot. Nobody's allergic to eggs",
            section = nylocas
    )
    default boolean hideEggs() {
        return false;
    }

    //------------------------------------------------------------//
    // Sote
    //------------------------------------------------------------//
    @ConfigItem(
            position = 1,
            keyName = "sotetmaz13",
            name = "Sotetseg Maze",
            description = "Display tiles indicating the correct path of the sotetseg maze.",
            section = sotetseg
    )
    default boolean sotetsegMaze() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "showSoAT",
            name = "Show Sotetseg Attack Ticks",
            description = "Ticks until Sotetseg attacks again.",
            section = sotetseg
    )
    default boolean showSotetsegAttackTicks() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "SoteAtt3",
            name = "Show Sotetseg Orb",
            description = "Highlight the attacks that Sotetseg throws at you.",
            section = sotetseg
    )
    default soteOrbMode sotetsegShowOrbs() {
        return soteOrbMode.OFF;
    }

    @Range(min = 1, max = 50)
    @ConfigItem(
            position = 4,
            keyName = "sotHSizz",
            name = "Sotetseg Orb Hat Size",
            description = "Changes the size of the hats",
            section = sotetseg
    )
    default int soteHatSize() {
        return 16;
    }

    @ConfigItem(
            position = 5,
            name = "<html><p style=\"color:#25C54F\">—————— Death Ball</p></html>",
            keyName = "sot divi2",
            description = "",
            section = sotetseg
    )
    void soteDivider1();

    @ConfigItem(
            position = 6,
            keyName = "SoetAtcks13",
            name = "Show Sotetseg Nuke",
            description = "Highlight the big AoE dragonball-z deathball mfkin thing.",
            section = sotetseg
    )
    default soteDeathballOverlayMode sotetsegShowNuke() {
        return soteDeathballOverlayMode.TICKS;
    }

    @ConfigItem(
            position = 7,
            keyName = "deathTOPlay",
            name = "Death Ball Ticks on Player",
            description = "Displays the death ball ticks on the targeted player instead of on the death ball",
            section = sotetseg
    )
    default boolean deathTicksOnPlayer() {
        return false;
    }

    @ConfigItem(
            position = 8,
            keyName = "SotetAttaSnds3",
            name = "Sotetseg Nuke Sound",
            description = "Ear rape.",
            section = sotetseg
    )
    default boolean sotetsegAttacksSound() {
        return false;
    }

    @Range(max = 100)
    @ConfigItem(
            position = 9,
            keyName = "SotetAtcksSndsVol3",
            name = "Nuke Volume",
            description = "Set this to 100 or you're a pussy.",
            section = sotetseg
    )
    default int sotetsegAttacksSoundVolume() {
        return 80;
    }

    @ConfigItem(
            position = 10,
            keyName = "dDB3",
            name = "Show Death Ball Target",
            description = "Shows who has the death ball",
            section = sotetseg
    )
    default boolean displayDeathBall() {
        return false;
    }

    @ConfigItem(
            position = 11,
            keyName = "dispDBCol33",
            name = "Death Ball Target Color",
            description = "Sets color of the death ball target tile",
            section = sotetseg

    )
    default Color displayDeathBallColor()
    {
        return new Color(188, 74, 74);
    }

    @ConfigItem(
            position = 12,
            keyName = "dballInfo22",
            name = "Attacks Until Death Ball",
            description = "Shows an infobox with the attacks left until death ball",
            section = sotetseg
    )
    default soteDeathballMode deathballInfobox() {
        return soteDeathballMode.OFF;
    }

    @ConfigItem(
            position = 13,
            keyName = "dthbSL22",
            name = "Single Line Text",
            description = "Makes the attacks until deathball and ticks until attack a single line",
            section = sotetseg
    )
    default boolean deathballSingleLine() {
        return false;
    }

    //------------------------------------------------------------//
    // Xarpus
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "xarT3",
            name = "Xarpus Ticks",
            description = "Count down the ticks until xarpus attacks next",
            section = xarpus
    )
    default boolean xarpusTicks() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "xarLos22",
            name = "Xarpus Line of Sight",
            description = "No attack here",
            section = xarpus
    )
    default losMode xarpusLos() {
        return losMode.OFF;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "xLosCol",
            name = "Line of Sight Color",
            description = "What sorta fuckin description u need u moron",
            section = xarpus
    )
    default Color xarpusLosColor() {
        return new Color(255, 0, 0, 255);
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 3,
            keyName = "xarLosFil3",
            name = "Line of Sight Opacity",
            description = "Changes the opacity of the Xarpus Line of Sight highlight",
            section = xarpus
    )
    default int xarpusLosFill() {
        return 20;
    }

    @ConfigItem(
            position = 4,
            name = "<html><p style=\"color:#25C54F\">—————— Exhumeds</p></html>",
            keyName = "xar divi2",
            description = "",
            section = xarpus
    )
    void xarpusDivider1();

    @ConfigItem(
            position = 5,
            keyName = "xarExhu2",
            name = "Xarpus Exhumed",
            description = "Highlight the exhumed tiles that spawn on the ground.",
            section = xarpus
    )
    default exhumedMode xarpusExhumed() {
        return exhumedMode.BOTH;
    }

    @Alpha
    @ConfigItem(
            position = 6,
            keyName = "exhuStOffWarn6",
            name = "Exhumed Step Off Warning",
            description = "Changes the color of exhumed ticks and/or highlights when they are not active.",
            section = xarpus
    )
    default stepOffMode exhumedStepOffWarning() {
        return stepOffMode.OFF;
    }

    @ConfigItem(
            position = 7,
            keyName = "xarpExhuInf",
            name = "Show Xarpus Exhumed Panel",
            description = "Show a small info panel indicating how many exhumes remaining and total healed.",
            section = xarpus
    )
    default boolean xarpusExhumedInfo() {
        return false;
    }

    @ConfigItem(
            position = 8,
            keyName = "exhuOnXar7",
            name = "Show Exhumed Count on Xarpus",
            description = "Displays the number of exhumeds left on Xarpus",
            section = xarpus
    )
    default boolean exhumedOnXarpus() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "exhuIB3",
            name = "Show Exhumed Count Infobox",
            description = "Displays the number of exhumeds left in an infobox",
            section = xarpus
    )
    default boolean exhumedIB() {
        return false;
    }

    @ConfigItem(
            position = 10,
            keyName = "xarHealC3",
            name = "Show Healing Count Infobox",
            description = "Displays the healing done from exhumeds",
            section = xarpus
    )
    default boolean xarpusHealingCount()
    {
        return true;
    }

    @ConfigItem(
            position = 11,
            name = "<html><p style=\"color:#25C54F\">—————— Audio</p></html>",
            keyName = "xar divi2",
            description = "",
            section = xarpus
    )
    void xarpusDivider2();

    @ConfigItem(
            position = 12,
            keyName = "muteXar2",
            name = "Mute HM Earrape",
            description = "Fuck that noise",
            section = xarpus
    )
    default boolean muteXarpusHmEarrape() {
        return false;
    }

    @ConfigItem(
            position = 13,
            keyName = "sweeshhsh",
            name = "Sheeeesh",
            description = "Why not?",
            section = xarpus
    )
    default boolean sheesh() {
        return false;
    }

    @Range(max = 100)
    @ConfigItem(
            position = 14,
            keyName = "swheeeshiVolume",
            name = "Sheesh Volume",
            description = "Muted hard mode earrape.... then I added this",
            section = xarpus

    )
    default int sheeshVolume() {
        return 50;
    }

    //------------------------------------------------------------//
    // Verzik
    //------------------------------------------------------------//
    @ConfigItem(
            position = 1,
            keyName = "sVT",
            name = "Show Verzik Ticks",
            description = "Count down the ticks until Verzik attacks.",
            section = verzik
    )
    default boolean showVerzikTicks() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "sVA",
            name = "Show Attack Counter",
            description = "Counts how many attacks Verzik has done",
            section = verzik
    )
    default verzikAttacksMode showVerzikAttacks() {
        return verzikAttacksMode.REDS;
    }

    @ConfigItem(
            position = 3,
            keyName = "sVTT",
            name = "Show Total Tick Counter",
            description = "Display the total tick counter on Verzik.",
            section = verzik
    )
    default boolean showVerzikTotalTicks() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "VML",
            name = "P3 True Location",
            description = "Draws a true location tile around verzik during p3 <br> Purple until tornadoes spawn. Orange after tornadoes spawn. Tank changes color if YOU are the tank.",
            section = verzik
    )
    default meleeTileMode verzikMelee() {
        return meleeTileMode.NORMAL;
    }

    @Alpha
    @ConfigItem(
            position = 5,
            keyName = "vMC",
            name = "P3 Tile Color",
            description = "Sets color of P3 True Location",
            section = verzik
    )
    default Color verzikMeleeColor() {
        return new Color(106, 61, 255);
    }

    @Alpha
    @ConfigItem(
            position = 6,
            keyName = "p33AggCol",
            name = "P3 Tile Aggro Color",
            description = "Changes the color the tile will be if you are chosen as the tank <br> Must have 'Tank Notifier' selected",
            section = verzik
    )
    default Color p3AggroColor() {
        return Color.RED;
    }

    @ConfigItem(
            position = 7,
            keyName = "vTT",
            name = "Verzik Tank Target",
            description = "Highlight the tile of the player who is tanking. Color set by aggro color",
            section = verzik
    )
    default boolean verzikTankTarget() {
        return false;
    }

    @ConfigItem(
            position = 8,
            keyName = "mVeSs",
            name = "Mute Verzik Sounds",
            description = "Woooooo.... more sounds to mute",
            section = verzik
    )
    default boolean muteVerzikSounds() {
        return false;
    }

    @ConfigItem(
            position = 9,
            name = "<html><p style=\"color:#25C54F\">—————— Nylocas</p></html>",
            keyName = "ver divi8",
            description = "",
            section = verzik
    )
    void verzikDivider1();

    @ConfigItem(
            position = 10,
            keyName = "VNAgg",
            name = "Show Crab Targets",
            description = "Show a text overlay on crab spawns that are aggressive to you.",
            section = verzik
    )
    default boolean verzikNyloAggroWarning() {
        return true;
    }

    @ConfigItem(
            position = 11,
            keyName = "VNExpl",
            name = "Crab Tile",
            description = "Show crab explosion range with a tile indicator.",
            section = verzik
    )
    default VerzikNyloSetting verzikNyloExplodeRange() {
        return VerzikNyloSetting.MY_CRABS;
    }

    @ConfigItem(
            position = 12,
            keyName = "VNloExpCol",
            name = "Crab Tile Color",
            description = "Color of the tile for the exploding range.",
            section = verzik
    )
    default Color verzikNyloExplodeTileColor() {
        return Color.RED;
    }

    @ConfigItem(
            position = 13,
            keyName = "hpr3",
            name = "Show Red Crab Hp",
            description = "Shows the hp % of red crabs during P2 verzik",
            section = verzik
    )
    default boolean redsHp() {
        return false;
    }

    @ConfigItem(
            position = 14,
            keyName = "purp lurk",
            name = "Show Purple AoE",
            description = "Where the purple is gonna land",
            section = verzik
    )
    default boolean purpleAoe() { return false; }

    @ConfigItem(
            position = 15,
            keyName = "hid purp",
            name = "Hide Attack Purple",
            description = "Removes clickbox on purple crab spawn at Verzik when not wearing poison weapons/serps",
            section = verzik
    )
    default boolean hidePurple() {
        return false;
    }

    @ConfigItem(
            position = 16,
            keyName = "pCInfbo",
            name = "Attacks Until Purple Crab",
            description = "Shows an infobox with the attacks left until purple crab can spawn",
            section = verzik
    )
    default boolean purpleCrabInfobox() {
        return false;
    }

    @ConfigItem(
            position = 17,
            name = "<html><p style=\"color:#25C54F\">—————— Yellows</p></html>",
            keyName = "verz divi2",
            description = "",
            section = verzik
    )
    void verzikDivider2();

    @ConfigItem(
            position = 18,
            keyName = "shwVerYel",
            name = "Show Yellows Tick",
            description = "<u>Count down the ticks until Verzik yellow's damage tick.</u>"
                    + "<br> Thank you to Caps Lock13 for contributing to the 'groups' option",
            section = verzik
    )
    default verzikYellowsMode showVerzikYellows() {
        return verzikYellowsMode.OFF;
    }

    @ConfigItem(
            position = 19,
            keyName = "yelTOnP",
            name = "Yellows Ticks on Player",
            description = "Displays the yellows ticks on the local player instead of on the yellows",
            section = verzik
    )
    default boolean yellowTicksOnPlayer() {
        return false;
    }

    @ConfigItem(
            position = 20,
            keyName = "hAtYell",
            name = "Hide Attack Yellows",
            description = "Hides attack option on Verzik during yellows",
            section = verzik
    )
    default boolean hideAttackYellows() {
        return false;
    }

    @ConfigItem(
            position = 21,
            name = "<html><p style=\"color:#25C54F\">—————— Tornadoes</p></html>",
            keyName = "ver3 divi",
            description = "",
            section = verzik
    )
    void verzikDivider3();

    @ConfigItem(
            position = 22,
            keyName = "shoVeNdo",
            name = "Show Tornadoes",
            description = "Highlights all or only your personal tornado",
            section = verzik
    )
    default nadoMode showVerzikNados() {
        return nadoMode.OFF;
    }

    @ConfigItem(
            position = 23,
            keyName = "sVNdoSyl",
            name = "Tornado Style",
            description = "Sets the type of highlight for Show Tornadoes",
            section = verzik
    )
    default nadoStyle showVerzikNadoStyle() {
        return nadoStyle.TRUE_LOCATION;
    }

    @Alpha
    @ConfigItem(
            position = 24,
            keyName = "sVNdoCol",
            name = "Verzik Nado Color",
            description = "Color for the tornadoes",
            section = verzik
    )
    default Color showVerzikNadoColor() { return Color.RED; }

    @Alpha
    @ConfigItem(
            position = 25,
            keyName = "vNdoOpaci",
            name = "Verzik Nado Opacity",
            description = "opacity for the tornadoes",
            section = verzik
    )
    default int verzikNadoOpacity() { return 0; }

    @ConfigItem(
            position = 26,
            keyName = "hid0therNdos",
            name = "Hide Other Tornadoes",
            description = "Hides any tornado not following you",
            section = verzik
    )
    default boolean hideOtherNados() { return false; }

    @ConfigItem(
            position = 27,
            name = "<html><p style=\"color:#25C54F\">—————— Projectiles</p></html>",
            keyName = "v d 3",
            description = "",
            section = verzik
    )
    void verzikDivider4();

    @ConfigItem(
            position = 28,
            keyName = "sVRngAtt",
            name = "Show Verzik Range Attacks",
            description = "Shows the tile in which a ranged attack on P2 will land.",
            section = verzik
    )
    default boolean showVerzikRangeAttack() { return false; }

    @Alpha
    @ConfigItem(
            position = 29,
            keyName = "vRngeAttCol",
            name = "Verzik Range Attacks Color",
            description = "Color for the garlic balls",
            section = verzik

    )
    default Color verzikRangeAttacksColor() { return new Color(106, 61, 255, 255); }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 31,
            keyName = "vRgeAttaFil",
            name = "Verzik Range Attacks Opacity",
            description = "Changes the opacity of the Xarpus Line of Sight highlight",
            section = verzik

    )
    default int verzikRangeAttacksFill() {
        return 20;
    }

    @ConfigItem(
            position = 32,
            keyName = "lightInfobox",
            name = "Attacks Until Lightning",
            description = "Shows the attacks left until lightning",
            section = verzik
    )
    default lightningMode lightningInfobox() {
        return lightningMode.OFF;
    }

    @ConfigItem(
            position = 33,
            keyName = "lightningAT",
            name = "Lightning Attack Tick",
            description = "Displays the number of ticks before a lightning ball hits you.",
            section = verzik
    )
    default boolean lightningAttackTick() { return false; }

    @ConfigItem(
            position = 34,
            keyName = "dispGrB",
            name = "Show Green Ball",
            description = "Highlights whoever the green ball is on",
            section = verzik
    )
    default greenBallMode displayGreenBall() { return greenBallMode.OFF; }

    @ConfigItem(
            position = 35,
            keyName = "dispGrBT",
            name = "Show Green Ball Ticks",
            description = "Shows ticks on the person who has green ball on them",
            section = verzik
    )
    default boolean displayGreenBallTicks() { return false; }

    @ConfigItem(
            position = 36,
            keyName = "gBPan",
            name = "Green Bounce/Dmg Counter",
            description = "Infobox to display how many times you have bounced the green ball",
            section = verzik
    )
    default greenBouncePanelMode greenBouncePanel() { return greenBouncePanelMode.OFF; }

    @ConfigItem(
            position = 37,
            name = "<html><p style=\"color:#25C54F\">—————— Hard Mode</p></html>",
            keyName = "v di32",
            description = "",
            section = verzik
    )
    void verzikDivider5();

    @ConfigItem(
            position = 38,
            keyName = "sVRock",
            name = "Show HM Verzik Rocks",
            description = "Shows the tile the rocks will land on in P1 of Hard mode",
            section = verzik
    )
    default boolean showVerzikRocks() { return false; }

    @Alpha
    @ConfigItem(
            position = 39,
            keyName = "sVRockCol",
            name = "Verzik Rock Color",
            description = "Color for the rocks in P1",
            section = verzik
    )
    default Color showVerzikRocksColor() { return new Color(106, 61, 255); }

    @ConfigItem(
            position = 40,
            keyName = "sVAcid",
            name = "Show HM Acid",
            description = "Shows the tile the acid from hard mode Verzik range attacks is on",
            section = verzik
    )
    default boolean showVerzikAcid() { return false; }

    @Range(min = 1)
    @ConfigItem(
            position = 41,
            keyName = "sVADist",
            name = "Acid Render Distance",
            description = "Only highlights acid within a certain distance",
            section = verzik
    )
    default int showVerzikAcidDistance() { return 5; }

    @Alpha
    @ConfigItem(
            position = 42,
            keyName = "sVACol",
            name = "Verzik Acid Color",
            description = "Color for the acid from range attacks in P2",
            section = verzik

    )
    default Color showVerzikAcidColor() { return Color.GREEN; }

    //------------------------------------------------------------//
    // Misc
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "rct3",
            name = "Remove Cast ToB",
            description = "Removes cast on players and thralls in Theatre of Blood",
            section = misc
    )
    default boolean removeCastToB() { return false; }

    @ConfigItem(
            position = 1,
            keyName = "eIT",
            name = "Tick Entry Timer",
            description = "Show the instance timer indicating when you should enter the Nylo and Xarpus rooms for perfect spawn.",
            section = misc
    )
    default instancerTimerMode entryInstanceTimer() {
        return instancerTimerMode.OVERHEAD;
    }

    @ConfigItem(
            position = 2,
            keyName = "remoFlag",
            name = "Left Click Bank Loot",
            description = "Removes the 'Force Right Click' flag from the [Bank-all] option inside the Monumental Chest in the Loot Room",
            section = misc
    )
    default boolean removeFRCFlag() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "rTL4",
            name = "Red Crabs True Tile",
            description = "Shows the true tile for red crabs",
            section = misc
    )
    default redsTlMode redsTL() {return redsTlMode.OFF;}

    @ConfigItem(
            position = 5,
            keyName = "rTLCol",
            name = "Red Crabs True Tile Color",
            description = "Color for the reds true tile",
            section = misc
    )
    default Color redsTLColor() {return new Color(207, 138, 253, 255);}

    @ConfigItem(
            position = 6,
            keyName = "recolBar",
            name = "Recolor Barriers",
            description = "Recolors all the barriers inside the raid",
            section = misc
    )
    default barrierMode recolorBarriers() {
        return barrierMode.COLOR;
    }

    @ConfigItem(
            position = 7,
            keyName = "barrisCol",
            name = "Barriers Color",
            description = "Sets the color of barriers",
            section = misc
    )
    @Alpha
    default Color barriersColor() {
        return new Color(106, 61, 255, 255);
    }

    @ConfigItem(
            position = 8,
            keyName = "sTBuy3",
            name = "Swap value with buy 1",
            description = "Swap value and buy 1 on tob chest items",
            section = misc
    )
    default boolean swapTobBuys() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "situaT",
            name = "Situational Ticks",
            description = "Displays ticks till next attack on players with certain weapons <br> " +
                    "Local player in Bloat and all players in Xarpus",
            section = misc
    )
    default boolean situationalTicks() {return false;}

    @ConfigItem(
            position = 10,
            keyName = "stR4",
            name = "Stamina Requirement",
            description = "Doesn't let you go to the next room if you don't have a stamina potion",
            section = misc
    )
    default stamReqMode stamReq() {
        return stamReqMode.OFF;
    }

    @ConfigItem(
            position = 11,
            keyName = "oldhp4",
            name = "Old HP Colors",
            description = "Changes HP overlays from a gradual change to set colors <br>" +
                    "Works for maiden reds, verzik reds, and nylo pillars",
            section = misc
    )
    default boolean oldHpThreshold() {return false;}

    @ConfigItem(
            position = 66,
            keyName = "vTCrysHelp",
            name = "Remove Use Teleport Crystal",
            description = "Removes use option for verzik's teleport crystals on anything other than players",
            section = misc
    )
    default boolean verzikTeleportCrystalHelper() {return false;}

    @ConfigItem(
            position = 67,
            keyName = "lRemi",
            name = "Loot Reminder",
            description = "Dont be a chest victim",
            section = misc
    )
    default lootReminderMode lootReminder() {
        return lootReminderMode.OFF;
    }

    @Alpha
    @ConfigItem(
            position = 68,
            keyName = "lootRemiCol",
            name = "Reminder Color",
            description = "Sets color of the chest highlight from loot reminder",
            section = misc
    )
    default Color lootReminderColor() {return new Color(106, 61, 255, 100);}

    @ConfigItem(
            position = 89,
            name = "<html><p style=\"color:#25C54F\">—————— Rave</p></html>",
            keyName = "misc1 divi1",
            description = "",
            section = misc
    )
    void miscDivider1();

    @ConfigItem(
            position = 90,
            keyName = "fbl",
            name = "Fuck Bluelite",
            description = "Fuck Bluelite",
            section = misc
    )
    default boolean fuckBluelite() {
        return false;
    }

    @ConfigItem(
            keyName = "raveN",
            name = "Rave Nylos",
            description = "Fucking crab rave",
            section = misc,
            position = 99
    )
    default boolean raveNylo() {
        return false;
    }

    @ConfigItem(
            keyName = "raveNad1",
            name = "Rave Nados",
            description = "Just incase you cant fucking see it",
            section = misc,
            position = 99
    )
    default raveNadoMode raveNados() {
        return raveNadoMode.OFF;
    }

    @ConfigItem(
            position = 99,
            keyName = "rh1",
            name = "Rave Hats",
            description = "Hats = $400<br>Rave Hats = my fucking sanity",
            section = misc
    )
    default raveHatsMode raveHats() {
        return raveHatsMode.OFF;
    }

    @ConfigItem(
            position = 99,
            keyName = "rl1",
            name = "Rave Xarpus Line of Sight",
            description = "No attack here... rave",
            section = misc
    )
    default boolean raveLos() { return false; }

    @ConfigItem(
            position = 100,
            keyName = "rSpeed5",
            name = "Rave Speed",
            description = "Sets the speed the overlays rave at",
            section = misc
    )
    @Units(Units.MILLISECONDS)
    default int raveSpeed() { return 6000; }

    @ConfigItem(
            keyName = "hlm4",
            name = "",
            description = "",
            hidden = true
    )
    default boolean getHighlightMeleeNylo() {
        return false;
    }

    @ConfigItem(
            keyName = "hlm4",
            name = "",
            description = "",
            hidden = true
    )
    void setHighlightMeleeNylo(boolean var1);

    @ConfigItem(
            keyName = "high2lightMa",
            name = "",
            description = "",
            hidden = true
    )
    default boolean getHighlightMageNylo() {
        return false;
    }

    @ConfigItem(
            keyName = "high2lightMa",
            name = "",
            description = "",
            hidden = true
    )
    void setHighlightMageNylo(boolean var1);

    @ConfigItem(
            keyName = "hig1lightRe",
            name = "",
            description = "",
            hidden = true
    )
    default boolean getHighlightRangeNylo() {
        return false;
    }

    @ConfigItem(
            keyName = "hig1lightRe",
            name = "",
            description = "",
            hidden = true
    )
    void setHighlightRangeNylo(boolean var1);

    //------------------------------------------------------------//
    // Font
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "fontSyl1",
            name = "Runelite Font",
            description = "Replaces the default font with whatever you have the dynamic font set to",
            section = font
    )
    default boolean fontStyle() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "resizeFon22",
            name = "Allow Resizing Font",
            description = "Lets you resize font for overlays in Tob. Resizes ALL overlays when turned on",
            section = font
    )
    default boolean resizeFont() {
        return false;
    }

    @Range(max = 30)
    @ConfigItem(
            position = 2,
            keyName = "tFontSiz3",
            name = "Overlay Font Size",
            description = "Sets the font size for all Tob overlays. Must have 'Allow resizing font' on",
            section = font
    )
    default int tobFontSize()
    {
        return 12;
    }

    @ConfigItem(
            position = 3,
            keyName = "fontWegh11",
            name = "Font Weight",
            description = "Bold/Italics/Plain.",
            section = font
    )
    default FontStyle fontWeight()
    {
        return FontStyle.BOLD;
    }

    @ConfigItem(
            position = 4,
            name = "<html><p style=\"color:#25C54F\">——————</p></html>",
            keyName = "fntdiide22",
            description = "",
            section = font
    )
    void fontDivider1();

    @ConfigItem(
            position = 5,
            keyName = "deble",
            name = "Death Ball Font Size",
            description = "Font size for the death ball ticks - must have on player selected",
            section = font

    )
    default int deathballSize() {return 14;}

    @Range(min = -60)
    @ConfigItem(
            position = 6,
            keyName = "deballOffset",
            name = "Death Ball Font Offset",
            description = "Offset for the death ball ticks - must have on player selected",
            section = font

    )
    default int deathballOffset() {return 0;}

    @ConfigItem(
            position = 7,
            name = "<html><p style=\"color:#25C54F\">——————</p></html>",
            keyName = "font11ivide22",
            description = "",
            section = font
    )
    void fontDivider2();

    @ConfigItem(
            position = 8,
            keyName = "ySiz1",
            name = "Yellows Font Size",
            description = "Font size for the yellows ticks - must have on player selected",
            section = font

    )
    default int yellowsSize() {return 14;}

    @Range(min = -60)
    @ConfigItem(
            position = 9,
            keyName = "yelsOffse11",
            name = "Yellows Font Offset",
            description = "Offset for the yellows ticks - must have on player selected",
            section = font

    )
    default int yellowsOffset() {return 0;}

    @ConfigItem(
            position = 10,
            name = "<html><p style=\"color:#25C54F\">——————</p></html>",
            keyName = "font dide11",
            description = "",
            section = font
    )
    void fontDivider3();

    @ConfigItem(
            position = 11,
            keyName = "zapS",
            name = "Zap Font Size",
            description = "Font size for the zap ticks - must have on player selected",
            section = font

    )
    default int zapSize() {return 14;}

    @Range(min = -60)
    @ConfigItem(
            position = 12,
            keyName = "zapOset",
            name = "Zap Font Offset",
            description = "Offset for the zap ticks - must have on player selected",
            section = font

    )
    default int zapOffset() {return 0;}

    @ConfigItem(
            position = 13,
            name = "<html><p style=\"color:#25C54F\">——————</p></html>",
            keyName = "font divid1",
            description = "",
            section = font
    )
    void fontDivider4();

    @ConfigItem(
            position = 14,
            keyName = "gBalle",
            name = "Green Ball Font Size",
            description = "Font size for the green ball ticks - must have on player selected",
            section = font
    )
    default int greenBallSize() {return 15;}

    @Range(min = -60)
    @ConfigItem(
            position = 15,
            keyName = "setgrnoff",
            name = "Green Ball Font Offset",
            description = "Offset for the green ball ticks - must have on player selected",
            section = font
    )
    default int greenBallOffset() {return 0;}

    @ConfigItem(
            position = 16,
            name = "<html><p style=\"color:#25C54F\">——————</p></html>",
            keyName = "fntivide33",
            description = "",
            section = font
    )
    void fontDivider5();

    @ConfigItem(
            position = 17,
            keyName = "situaTS",
            name = "Sit. Ticks Font Size",
            description = "Font size for the situational ticks - must have on player selected",
            section = font
    )
    default int situationalTicksSize() {return 14;}

    @Range(min = -60)
    @ConfigItem(
            position = 18,
            keyName = "situatTOffset",
            name = "Sit. Ticks Font Offset",
            description = "Offset for the situational ticks - must have on player selected",
            section = font
    )
    default int situationalTicksOffset() {return 60;}

    //------------------------------------------------------------//
    // Maiden enums
    //------------------------------------------------------------//
    enum maidenBloodSplatMode {
        OFF, COLOR, RAVE, RAVEST
    }

    enum maidenFreezeTimerMode {
        OFF, TICKS, TILE
    }

    enum maidenBloodsMode{
        OFF, CAST, ATTACK, BOTH
    }

    enum MaidenMaxHitTTMode {
        OFF("Off"),
        REGULAR("Regular"),
        ELY("Elysian"),
        BOTH("Both");

        private final String name;

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        MaidenMaxHitTTMode(String name) {
            this.name = name;
        }
    }

    enum MaidenMaxHit {
        OFF("Off"),
        REGULAR("Regular"),
        ELY("Elysian"),
        BOTH("Both");

        private final String name;

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        MaidenMaxHit(String name) {
            this.name = name;
        }
    }

    //------------------------------------------------------------//
    // Bloat enums
    //------------------------------------------------------------//
    enum BloatIndicatorMode {
        OFF, TILE, TRUE_LOCATION
    }

    enum bloatHandsMode{
        OFF, COLOR, RAVE, RAVEST
    }

    enum bloatStompMode {
        OFF, COLOR, RAVE
    }

    enum bloatTurnMode {
        OFF, SOUND, CHA_CHA
    }

    enum annoyingObjectHideMode{
        OFF, CHAINS, TANK, BOTH
    }

    //------------------------------------------------------------//
    // Nylo enums
    //------------------------------------------------------------//
    enum wheelchairMode {
        OFF, WAVES, BOSS, BOTH
    }

    enum nyloBossPhaseChange {
        OFF, BOSS, BOTH
    }

    enum nyloExplosionType {
        TILE, EXPLOSION
    }

    enum ExplosionWarning {
        OFF, TILE, TICKS, BOTH
    }

    enum nylotimealive {
        COUNTUP, COUNTDOWN
    }

    enum aggroStyle {
        OFF, HULL, TILE
    }

    enum nyloSplitsMessage {
        OFF, WAVES, BOSS, BOTH
    }

    enum splitsMsgTiming {
        CLEANUP, FINISHED
    }

    enum smallSplitsMode {
        CAP, TOTAL, BOTH
    }

    enum waveSpawnTimerMode {
        OFF, INFOBOX, OVERLAY, BOTH
    }

    enum hidePillarsMode {
        OFF, PILLARS, CLEAN
    }

    enum waveTimerMode {
        WR5("2:45"),
        WR4("2:48"),
        FIVES("2:50"),
        FOURS("2:52"),
        TRIO("2:55"),
        WR2("3:09"),
        WR2_1("3:12"),
        DUO("3:14");

        private final String name;

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        waveTimerMode(String name) {
            this.name = name;
        }
    }

    //------------------------------------------------------------//
    // Sote enums
    //------------------------------------------------------------//
    enum soteOrbMode {
        OFF, TICKS, HATS, BOTH
    }

    enum soteDeathballMode {
        OFF, INFOBOX, OVERLAY, BOTH
    }

    enum soteDeathballOverlayMode {
        OFF, TICKS, NUKE, BOTH
    }

    enum raveHatsMode {
        OFF, RAVE, EPILEPSY, TURBO
    }

    //------------------------------------------------------------//
    // Xarpus enums
    //------------------------------------------------------------//
    enum exhumedMode {
        OFF, TILE, TICKS, BOTH
    }

    enum stepOffMode {
        OFF, TILE, TICKS, BOTH
    }

    enum meleeTileMode {
        OFF, NORMAL, TANK_NOTIFIER
    }

    enum losMode {
        OFF, MELEE, QUADRANT
    }

    //------------------------------------------------------------//
    // Verzik enums
    //------------------------------------------------------------//
    enum verzikAttacksMode {
        OFF, REDS, P2, ALL
    }

    enum VerzikNyloSetting {
        OFF, MY_CRABS, ALL_CRABS
    }

    enum nadoMode {
        OFF, ALL, PERSONAL
    }

    enum nadoStyle {
        TILE, TRUE_LOCATION
    }

    enum verzikYellowsMode {
        OFF, YELLOW, GROUPS
    }

    enum greenBouncePanelMode {
        OFF, BOUNCES, DAMAGE, BOTH
    }

    enum greenBallMode{
        OFF, TILE, AREA
    }

    enum lightningMode {
        OFF, INFOBOX, OVERLAY, BOTH
    }

    //------------------------------------------------------------//
    // Misc enums
    //------------------------------------------------------------//
    enum redsTlMode {
        OFF, MAIDEN, VERZIK, BOTH
    }

    enum barrierMode {
        OFF, COLOR, RAVE
    }

    enum instancerTimerMode {
        OFF, OVERHEAD, OVERLAY
    }

    enum stamReqMode {
        OFF, NYLO, XARPUS, BOTH
    }

    enum lootReminderMode {
        OFF, DUMB, DUMBER, DUMBEST, DUMBEREST
    }

    enum raveNadoMode {
        OFF, RAVE, RAVEST
    }

    @Getter(AccessLevel.PACKAGE)
    @AllArgsConstructor
    enum FontStyle
    {
        BOLD("Bold", Font.BOLD),
        ITALIC("Italic", Font.ITALIC),
        PLAIN("Plain", Font.PLAIN);

        private final String name;
        private final int font;

        @Override
        public String toString()
        {
            return getName();
        }
    }
}