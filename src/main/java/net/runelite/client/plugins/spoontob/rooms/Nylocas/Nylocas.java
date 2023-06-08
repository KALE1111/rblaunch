package net.runelite.client.plugins.spoontob.rooms.Nylocas;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.util.Text;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.spoontob.Room;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.plugins.spoontob.util.TheatreInputListener;
import net.runelite.client.plugins.spoontob.util.TheatreRegions;
import net.runelite.client.plugins.spoontob.util.WeaponMap;
import net.runelite.client.plugins.spoontob.util.WeaponStyle;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.util.ColorUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.MenuOpened;
import java.awt.Color;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;


import java.util.*;

import static net.runelite.api.NpcID.*;

public class Nylocas extends Room {
    private static final Logger log = LoggerFactory.getLogger(SpoonTobPlugin.class);
    @Inject
    private SkillIconManager skillIconManager;
    @Inject
    private MouseManager mouseManager;
    @Inject
    private TheatreInputListener theatreInputListener;
    @Inject
    private Client client;
    @Inject
    private NylocasOverlay nylocasOverlay;
    @Inject
    public NylocasAliveCounterOverlay nylocasAliveCounterOverlay;
    @Inject
    private NyloTimer nyloTimer;
    @Inject
    private NyloWaveSpawnInfobox waveSpawnInfobox;

    private static final int NPCID_NYLOCAS_PILLAR = 8358;
    private static final int NPCID_NYLOCAS_SM_PILLAR = 10790;
    private static final int NPCID_NYLOCAS_HM_PILLAR = 10811;
    private static final int NYLO_MAP_REGION = 13122;
    private static final int BLOAT_MAP_REGION = 13125;
    private static final String MAGE_NYLO = "Nylocas Hagios";
    private static final String RANGE_NYLO = "Nylocas Toxobolos";
    private static final String MELEE_NYLO = "Nylocas Ischyros";
    private static final String BOSS_NYLO = "Nylocas Vasilias";
    private static final String DEMIBOSS_NYLO = "Nylocas Prinkipas";

    protected static final Set<Integer> NYLO_BOSS_IDS = ImmutableSet.of(
            NYLOCAS_VASILIAS_8355, NYLOCAS_VASILIAS_8356, NYLOCAS_VASILIAS_8357, //Reg
            NYLOCAS_VASILIAS_10787, NYLOCAS_VASILIAS_10788, NYLOCAS_VASILIAS_10789, // SM
            NYLOCAS_VASILIAS_10808, NYLOCAS_VASILIAS_10809, NYLOCAS_VASILIAS_10810 // HM
    );

    protected static final Set<Integer> NYLO_DEMI_BOSS_IDS = ImmutableSet.of(
            NYLOCAS_PRINKIPAS_10804, NYLOCAS_PRINKIPAS_10805, NYLOCAS_PRINKIPAS_10806
    );

    protected static final Set<Integer> MELEE_IDS = ImmutableSet.of(
            NYLOCAS_ISCHYROS_8342, NYLOCAS_ISCHYROS_8345, NYLOCAS_ISCHYROS_8348, NYLOCAS_ISCHYROS_8351, //Reg
            NYLOCAS_ISCHYROS_10774, NYLOCAS_ISCHYROS_10777, NYLOCAS_ISCHYROS_10780, NYLOCAS_ISCHYROS_10783, // SM
            NYLOCAS_ISCHYROS_10791, NYLOCAS_ISCHYROS_10794, NYLOCAS_ISCHYROS_10797, NYLOCAS_ISCHYROS_10800 // HM
    );

    protected static final Set<Integer> RANGE_IDS = ImmutableSet.of(
            NYLOCAS_TOXOBOLOS_8343, NYLOCAS_TOXOBOLOS_8346, NYLOCAS_TOXOBOLOS_8349, NYLOCAS_TOXOBOLOS_8352, //Reg
            NYLOCAS_TOXOBOLOS_10775, NYLOCAS_TOXOBOLOS_10778, NYLOCAS_TOXOBOLOS_10781, NYLOCAS_TOXOBOLOS_10784, // SM
            NYLOCAS_TOXOBOLOS_10792, NYLOCAS_TOXOBOLOS_10795, NYLOCAS_TOXOBOLOS_10798, NYLOCAS_TOXOBOLOS_10801 // HM
    );

    protected static final Set<Integer> MAGIC_IDS = ImmutableSet.of(
            NYLOCAS_HAGIOS, NYLOCAS_HAGIOS_8347, NYLOCAS_HAGIOS_8350, NYLOCAS_HAGIOS_8353, //Reg
            NYLOCAS_HAGIOS_10776, NYLOCAS_HAGIOS_10779, NYLOCAS_HAGIOS_10782, NYLOCAS_HAGIOS_10785, // SM
            NYLOCAS_HAGIOS_10793, NYLOCAS_HAGIOS_10796, NYLOCAS_HAGIOS_10799, NYLOCAS_HAGIOS_10802 // HM
    );

    protected static final Set<Integer> TRIDENT_IDS = ImmutableSet.of(
            ItemID.SANGUINESTI_STAFF, ItemID.HOLY_SANGUINESTI_STAFF, ItemID.TRIDENT_OF_THE_SEAS_E, ItemID.TRIDENT_OF_THE_SEAS,
            ItemID.TRIDENT_OF_THE_SEAS_FULL, ItemID.TRIDENT_OF_THE_SWAMP_E, ItemID.TRIDENT_OF_THE_SWAMP
    );

    @Getter
    @Setter
    private static Runnable wave31Callback = null;
    @Getter
    @Setter
    private static Runnable endOfWavesCallback = null;

    @Getter
    private boolean nyloActive;

    public int nyloWave = 0;
    private int varbit6447 = -1;
    @Getter
    private Instant nyloWaveStart;
    @Getter
    private NyloSelectionManager nyloSelectionManager;

    @Getter
    private HashMap<NPC, Integer> nylocasPillars = new HashMap();
    public ArrayList<NyloInfo> nylocasNpcs = new ArrayList<>();
    @Getter
    private HashSet<NPC> aggressiveNylocas = new HashSet();
    private HashMap<NyloNPC, NPC> currentWave = new HashMap();

    private int ticksSinceLastWave = 0;
    @Getter
    public int instanceTimer = 0;
    @Getter
    private boolean isInstanceTimerRunning = false;
    private boolean nextInstance = true;

    private int rangeBoss = 0;
    private int mageBoss = 0;
    private int meleeBoss = 0;
    private int rangeSplits = 0;
    private int mageSplits = 0;
    private int meleeSplits = 0;
    private int preRangeSplits = 0;
    private int preMageSplits = 0;
    private int preMeleeSplits = 0;
    private int postRangeSplits = 0;
    private int postMageSplits = 0;
    private int postMeleeSplits = 0;

    @Getter
    private int bossChangeTicks;
    private int lastBossId;
    @Getter
    private NPC nylocasBoss;
    private boolean nyloBossAlive;

    public int weaponId = 0;

    private static final Set<Point> spawnTiles = ImmutableSet.of(
            new Point(17, 24), new Point(17, 25), new Point(31, 9), new Point(32, 9), new Point(46, 24), new Point(46, 25));

    @Getter
    private final Map<NPC, Integer> splitsMap = new HashMap<>();
    private final Set<NPC> bigNylos = new HashSet<>();

    public boolean showHint;

    public final ArrayList<Color> meleeNyloRaveColors = new ArrayList<Color>();
    public final ArrayList<Color> rangeNyloRaveColors = new ArrayList<Color>();
    public final ArrayList<Color> mageNyloRaveColors = new ArrayList<Color>();

    public String tobMode = "";
    public boolean minibossAlive = false;
    public NPC nyloMiniboss = null;
    public String nyloBossStyle = "";

    public int logTicks = 0;

    public int waveSpawnTicks = 0;
    public boolean stalledWave = false;

    private boolean mirrorMode;
    private boolean setAlive;

    private WeaponStyle weaponStyle;
    private boolean skipTickCheck = false;

    @Inject
    protected Nylocas(SpoonTobPlugin plugin, SpoonTobConfig config) {
        super(plugin, config);
    }

    public void init() {
        InfoBoxComponent box = new InfoBoxComponent();
        box.setImage(skillIconManager.getSkillImage(Skill.ATTACK));
        NyloSelectionBox nyloMeleeOverlay = new NyloSelectionBox(box);
        nyloMeleeOverlay.setSelected(config.getHighlightMeleeNylo());
        box = new InfoBoxComponent();
        box.setImage(skillIconManager.getSkillImage(Skill.MAGIC));
        NyloSelectionBox nyloMageOverlay = new NyloSelectionBox(box);
        nyloMageOverlay.setSelected(config.getHighlightMageNylo());
        box = new InfoBoxComponent();
        box.setImage(skillIconManager.getSkillImage(Skill.RANGED));
        NyloSelectionBox nyloRangeOverlay = new NyloSelectionBox(box);
        nyloRangeOverlay.setSelected(config.getHighlightRangeNylo());
        nyloSelectionManager = new NyloSelectionManager(nyloMeleeOverlay, nyloMageOverlay, nyloRangeOverlay);
        nyloSelectionManager.setHidden(!config.nyloOverlay());
        nylocasAliveCounterOverlay.setHidden(!config.nyloAlivePanel());
        nylocasAliveCounterOverlay.setNyloAlive(0);
        nylocasAliveCounterOverlay.setMaxNyloAlive(12);
        nyloBossAlive = false;
        tobMode = "";
        minibossAlive = false;
        nyloMiniboss = null;
        nyloBossStyle = "";
        waveSpawnTicks = 0;
        stalledWave = false;
    }

    private void startupNyloOverlay() {
        mouseManager.registerMouseListener(theatreInputListener);
        if (nyloSelectionManager != null) {
            overlayManager.add(nyloSelectionManager);
            nyloSelectionManager.setHidden(!config.nyloOverlay());
        }

        if (nylocasAliveCounterOverlay != null) {
            overlayManager.add(nylocasAliveCounterOverlay);
            nylocasAliveCounterOverlay.setHidden(!config.nyloAlivePanel());
        }
    }

    private void shutdownNyloOverlay() {
        mouseManager.unregisterMouseListener(theatreInputListener);
        if (nyloSelectionManager != null) {
            overlayManager.remove(nyloSelectionManager);
            nyloSelectionManager.setHidden(true);
        }

        if (nylocasAliveCounterOverlay != null) {
            overlayManager.remove(nylocasAliveCounterOverlay);
            nylocasAliveCounterOverlay.setHidden(true);
        }
    }

    public void load() {
        overlayManager.add(nylocasOverlay);
        overlayManager.add(nyloTimer);
        overlayManager.add(waveSpawnInfobox);
        bossChangeTicks = -1;
        lastBossId = -1;
        weaponStyle = null;
    }

    public void unload() {
        overlayManager.remove(nylocasOverlay);
        overlayManager.remove(nyloTimer);
        overlayManager.remove(waveSpawnInfobox);
        shutdownNyloOverlay();
        nyloBossAlive = false;
        nyloWaveStart = null;
        nyloActive = false;
        tobMode = "";
        minibossAlive = false;
        nyloBossStyle = "";
        logTicks = 0;
        waveSpawnTicks = 0;
        stalledWave = false;
        weaponStyle = null;
        splitsMap.clear();
        bigNylos.clear();
    }

    private void resetNylo() {
        nyloBossAlive = false;
        nylocasPillars.clear();
        nylocasNpcs.clear();
        aggressiveNylocas.clear();
        setNyloWave(0);
        currentWave.clear();
        bossChangeTicks = -1;
        lastBossId = -1;
        nylocasBoss = null;
        weaponId = 0;
        weaponStyle = null;
        splitsMap.clear();
        bigNylos.clear();

        tobMode = "";
        minibossAlive = false;
        nyloMiniboss = null;
        nyloBossStyle = "";
        logTicks = 0;
        waveSpawnTicks = 0;
        stalledWave = false;
    }

    private void setNyloWave(int wave) {
        nyloWave = wave;
        nylocasAliveCounterOverlay.setWave(wave);
        if (wave >= 3) {
            isInstanceTimerRunning = false;
        }

        if (wave != 0) {
            switch (tobMode) {
                case "hard":
                    ticksSinceLastWave = ((NylocasWave) NylocasWave.hmWaves.get(wave)).getWaveDelay();
                    break;
                case "story":
                    ticksSinceLastWave = ((NylocasWave) NylocasWave.smWaves.get(wave)).getWaveDelay();
                    break;
                case "normal":
                    ticksSinceLastWave = ((NylocasWave) NylocasWave.waves.get(wave)).getWaveDelay();
                    break;
            }
        }

        if (wave >= 20 && nylocasAliveCounterOverlay.getMaxNyloAlive() != 24) {
            nylocasAliveCounterOverlay.setMaxNyloAlive(24);
        }

        if (wave < 20 && nylocasAliveCounterOverlay.getMaxNyloAlive() != 12) {
            nylocasAliveCounterOverlay.setMaxNyloAlive(12);
        }

        if (wave == 31 && wave31Callback != null) {
            wave31Callback.run();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged change) {
        if (change.getKey().equals("nyloOverlay")) {
            nyloSelectionManager.setHidden(!config.nyloOverlay());
        }else if (change.getKey().equals("nyloAliveCounter")) {
            nylocasAliveCounterOverlay.setHidden(!config.nyloAlivePanel());
        }else if (change.getKey().equals("showLowestPillar") && !config.showLowestPillar()) {
            client.clearHintArrow();
        }else if(change.getKey().equals("hidePillars")){
            plugin.refreshScene();
            if(config.hidePillars() == SpoonTobConfig.hidePillarsMode.PILLARS){
                //removeGameObjectsFromScene(ImmutableSet.of(32862), 0);
            }else if(config.hidePillars() == SpoonTobConfig.hidePillarsMode.CLEAN){
                //removeGameObjectsFromScene(ImmutableSet.of(32862, 32876, 32899), 0);
            }

            if(config.hideEggs()) {
                //removeGameObjectsFromScene(ImmutableSet.of(32939, 32937, 2739, 32865), 0);
            }
        }else if(change.getKey().equals("hideEggs")){
            plugin.refreshScene();
            if(config.hideEggs()) {
                //removeGameObjectsFromScene(ImmutableSet.of(32939, 32937, 2739, 32865), 0);
            }

            if(config.hidePillars() == SpoonTobConfig.hidePillarsMode.PILLARS){
                //removeGameObjectsFromScene(ImmutableSet.of(32862), 0);
            }else if(config.hidePillars() == SpoonTobConfig.hidePillarsMode.CLEAN){
                //removeGameObjectsFromScene(ImmutableSet.of(32862, 32876, 32899), 0);
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        int id = npc.getId();
        switch(npc.getId()) {
            case NYLOCAS_ISCHYROS_8342:
            case NYLOCAS_TOXOBOLOS_8343:
            case NYLOCAS_HAGIOS:
            case NYLOCAS_ISCHYROS_8345:
            case NYLOCAS_TOXOBOLOS_8346:
            case NYLOCAS_HAGIOS_8347:
            case NYLOCAS_ISCHYROS_8348:
            case NYLOCAS_TOXOBOLOS_8349:
            case NYLOCAS_HAGIOS_8350:
            case NYLOCAS_ISCHYROS_8351:
            case NYLOCAS_TOXOBOLOS_8352:
            case NYLOCAS_HAGIOS_8353:
            case NYLOCAS_ISCHYROS_10774: //Story Mode
            case NYLOCAS_TOXOBOLOS_10775:
            case NYLOCAS_HAGIOS_10776:
            case NYLOCAS_ISCHYROS_10777:
            case NYLOCAS_TOXOBOLOS_10778:
            case NYLOCAS_HAGIOS_10779:
            case NYLOCAS_ISCHYROS_10780:
            case NYLOCAS_TOXOBOLOS_10781:
            case NYLOCAS_HAGIOS_10782:
            case NYLOCAS_ISCHYROS_10783:
            case NYLOCAS_TOXOBOLOS_10784:
            case NYLOCAS_HAGIOS_10785:
            case NYLOCAS_ISCHYROS_10791: //Hard Mode
            case NYLOCAS_TOXOBOLOS_10792:
            case NYLOCAS_HAGIOS_10793:
            case NYLOCAS_ISCHYROS_10794:
            case NYLOCAS_TOXOBOLOS_10795:
            case NYLOCAS_HAGIOS_10796:
            case NYLOCAS_ISCHYROS_10797:
            case NYLOCAS_TOXOBOLOS_10798:
            case NYLOCAS_HAGIOS_10799:
            case NYLOCAS_ISCHYROS_10800:
            case NYLOCAS_TOXOBOLOS_10801:
            case NYLOCAS_HAGIOS_10802:
            case NYLOCAS_PRINKIPAS:
            case NYLOCAS_PRINKIPAS_10804:
            case NYLOCAS_PRINKIPAS_10805:
            case NYLOCAS_PRINKIPAS_10806:
                if (nyloActive) {
                    if (npc.getId() == NYLOCAS_PRINKIPAS_10804){
                        minibossAlive = true;
                        nyloMiniboss = npc;
                        bossChangeTicks = 10;
                    } else {
                        nylocasNpcs.add(new NyloInfo(npc));
                    }

                    if (minibossAlive) {
                        nylocasAliveCounterOverlay.setNyloAlive(nylocasNpcs.size() + 3);
                    } else {
                        nylocasAliveCounterOverlay.setNyloAlive(nylocasNpcs.size());
                    }
                    NyloNPC nyloNPC = matchNpc(npc);
                    if (nyloNPC != null) {
                        currentWave.put(nyloNPC, npc);
                        if (currentWave.size() > 2) {
                            matchWave();
                        }
                    }
                }
                setAlive = true;
                break;
            case NYLOCAS_VASILIAS:
            case NYLOCAS_VASILIAS_8355:
            case NYLOCAS_VASILIAS_8356:
            case NYLOCAS_VASILIAS_8357:
            case NYLOCAS_VASILIAS_10786: //Story mode
            case NYLOCAS_VASILIAS_10787:
            case NYLOCAS_VASILIAS_10788:
            case NYLOCAS_VASILIAS_10789:
            case NYLOCAS_VASILIAS_10807: //Hard mode
            case NYLOCAS_VASILIAS_10808:
            case NYLOCAS_VASILIAS_10809:
            case NYLOCAS_VASILIAS_10810:
                showHint = false;
                isInstanceTimerRunning = false;
                nyloBossStyle = "melee";
                client.clearHintArrow();
                nyloBossAlive = true;
                lastBossId = id;
                nylocasBoss = npc;
                meleeBoss = 0;
                mageBoss = 0;
                rangeBoss = 0;
                if (npc.getId() == NYLOCAS_VASILIAS_8355 || npc.getId() == NYLOCAS_VASILIAS_10787 || npc.getId() == NYLOCAS_VASILIAS_10808) {
                    if (npc.getId() == NYLOCAS_VASILIAS_10787) {
                        bossChangeTicks = 15;
                    } else {
                        bossChangeTicks = 10;
                    }
                    meleeBoss++;
                }
                break;
            case NPCID_NYLOCAS_PILLAR:
            case NPCID_NYLOCAS_SM_PILLAR: //Story Mode
            case NPCID_NYLOCAS_HM_PILLAR: //Hard Mode
                nyloActive = true;
                showHint = true;
                if (nylocasPillars.size() > 3) {
                    nylocasPillars.clear();
                }
                if (!nylocasPillars.containsKey(npc)) {
                    nylocasPillars.put(npc, 100);
                }

                if (npc.getId() == NPCID_NYLOCAS_HM_PILLAR){
                    tobMode = "hard";
                } else if (npc.getId() == NPCID_NYLOCAS_SM_PILLAR){
                    tobMode = "story";
                } else {
                    tobMode = "normal";
                }

                mageSplits = 0;
                rangeSplits = 0;
                meleeSplits = 0;
                preRangeSplits = 0;
                preMageSplits = 0;
                preMeleeSplits = 0;
                postRangeSplits = 0;
                postMageSplits = 0;
                postMeleeSplits = 0;
        }

        if (nyloActive) {
            switch (id) {
                case NYLOCAS_ISCHYROS_8345: //Normal mode
                case NYLOCAS_TOXOBOLOS_8346:
                case NYLOCAS_HAGIOS_8347:
                case NYLOCAS_ISCHYROS_10777: //Story mode
                case NYLOCAS_TOXOBOLOS_10778:
                case NYLOCAS_HAGIOS_10779:
                case NYLOCAS_ISCHYROS_10794: //Hard mode
                case NYLOCAS_TOXOBOLOS_10795:
                case NYLOCAS_HAGIOS_10796:
                    bigNylos.add(npc);
                    break;
            }

            WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
            Point spawnLoc = new Point(worldPoint.getRegionX(), worldPoint.getRegionY());
            if (!spawnTiles.contains(spawnLoc)) {
                if (npc.getName() != null) {
                    if (npc.getName().contains("Hagios") && (id == NYLOCAS_HAGIOS || id == NYLOCAS_HAGIOS_10776 || id == NYLOCAS_HAGIOS_10793)) {
                        mageSplits++;
                        if (nyloWave < 20) {
                            preMageSplits++;
                        } else {
                            postMageSplits++;
                        }
                    } else if (npc.getName().contains("Toxobolos") && (id == NYLOCAS_TOXOBOLOS_8343 || id == NYLOCAS_TOXOBOLOS_10775 || id == NYLOCAS_TOXOBOLOS_10792)) {
                        rangeSplits++;
                        if (nyloWave < 20) {
                            preRangeSplits++;
                        } else {
                            postRangeSplits++;
                        }
                    } else if (npc.getName().contains("Ischyros") && (id == NYLOCAS_ISCHYROS_8342 || id == NYLOCAS_ISCHYROS_10774 || id == NYLOCAS_ISCHYROS_10791)) {
                        meleeSplits++;
                        if (nyloWave < 20) {
                            preMeleeSplits++;
                        } else {
                            postMeleeSplits++;
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event){
        NPC npc = event.getNpc();
        int id = npc.getId();
        if (NYLO_BOSS_IDS.contains(id) || NYLO_DEMI_BOSS_IDS.contains(id)) {
            if (id == NYLOCAS_VASILIAS_10787 || id == NYLOCAS_VASILIAS_10788 || id == NYLOCAS_VASILIAS_10789) {
                bossChangeTicks = 16;
            } else {
                bossChangeTicks = 11;
            }
            lastBossId = id;

            if (NYLO_DEMI_BOSS_IDS.contains(id)) {
                nyloMiniboss = npc;
            }
        }

        if (id == NYLOCAS_VASILIAS_8355 || id == NYLOCAS_VASILIAS_10787 || id == NYLOCAS_VASILIAS_10808) {
            meleeBoss++;
            nyloBossStyle = "melee";
        } else if (id == NYLOCAS_VASILIAS_8356 || id == NYLOCAS_VASILIAS_10788 || id == NYLOCAS_VASILIAS_10809) {
            mageBoss++;
            nyloBossStyle = "mage";
        } else if (id == NYLOCAS_VASILIAS_8357 || id == NYLOCAS_VASILIAS_10789 || id == NYLOCAS_VASILIAS_10810) {
            rangeBoss++;
            nyloBossStyle = "range";
        }
    }

    private void matchWave() {
        HashSet<NyloNPC> potentialWave = null;
        Set<NyloNPC> currentWaveKeySet = currentWave.keySet();

        for (int wave = nyloWave + 1; wave <= NylocasWave.MAX_WAVE; wave++) {
            boolean matched = true;
            switch (tobMode) {
                case "hard":
                    potentialWave = ((NylocasWave) NylocasWave.hmWaves.get(wave)).getWaveData();
                    break;
                case "story":
                    potentialWave = ((NylocasWave) NylocasWave.smWaves.get(wave)).getWaveData();
                    break;
                case "normal":
                    potentialWave = ((NylocasWave) NylocasWave.waves.get(wave)).getWaveData();
                    break;
            }

            for (NyloNPC nyloNpc : potentialWave) {
                if (!currentWaveKeySet.contains(nyloNpc)) {
                    matched = false;
                    break;
                }
            }

            if (matched) {
                setNyloWave(wave);
                stalledWave = false;
                if(ticksSinceLastWave > 0) {
                    waveSpawnTicks = ticksSinceLastWave;
                } else {
                    waveSpawnTicks = 4;
                }

                for (NyloNPC nyloNPC : potentialWave) {
                    if (nyloNPC.isAggressive()) {
                        aggressiveNylocas.add(currentWave.get(nyloNPC));
                    }
                }

                currentWave.clear();
                return;
            }
        }
    }

    private NyloNPC matchNpc(NPC npc) {
        WorldPoint p = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
        Point point = new Point(p.getRegionX(), p.getRegionY());
        NylocasSpawnPoint spawnPoint = NylocasSpawnPoint.getLookupMap().get(point);

        if (spawnPoint == null) {
            return null;
        }

        NylocasType nylocasType = NylocasType.getLookupMap().get(npc.getId());

        if (nylocasType == null) {
            return null;
        }

        return new NyloNPC(nylocasType, spawnPoint);
    }


    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        int id = npc.getId();
        switch(id) {
            case NYLOCAS_ISCHYROS_8342:
            case NYLOCAS_TOXOBOLOS_8343:
            case NYLOCAS_HAGIOS:
            case NYLOCAS_ISCHYROS_8345:
            case NYLOCAS_TOXOBOLOS_8346:
            case NYLOCAS_HAGIOS_8347:
            case NYLOCAS_ISCHYROS_8348:
            case NYLOCAS_TOXOBOLOS_8349:
            case NYLOCAS_HAGIOS_8350:
            case NYLOCAS_ISCHYROS_8351:
            case NYLOCAS_TOXOBOLOS_8352:
            case NYLOCAS_HAGIOS_8353:
            case NYLOCAS_ISCHYROS_10774: //Story Mode
            case NYLOCAS_TOXOBOLOS_10775:
            case NYLOCAS_HAGIOS_10776:
            case NYLOCAS_ISCHYROS_10777:
            case NYLOCAS_TOXOBOLOS_10778:
            case NYLOCAS_HAGIOS_10779:
            case NYLOCAS_ISCHYROS_10780:
            case NYLOCAS_TOXOBOLOS_10781:
            case NYLOCAS_HAGIOS_10782:
            case NYLOCAS_ISCHYROS_10783:
            case NYLOCAS_TOXOBOLOS_10784:
            case NYLOCAS_HAGIOS_10785:
            case NYLOCAS_ISCHYROS_10791: //Hard Mode
            case NYLOCAS_TOXOBOLOS_10792:
            case NYLOCAS_HAGIOS_10793:
            case NYLOCAS_ISCHYROS_10794:
            case NYLOCAS_TOXOBOLOS_10795:
            case NYLOCAS_HAGIOS_10796:
            case NYLOCAS_ISCHYROS_10797:
            case NYLOCAS_TOXOBOLOS_10798:
            case NYLOCAS_HAGIOS_10799:
            case NYLOCAS_ISCHYROS_10800:
            case NYLOCAS_TOXOBOLOS_10801:
            case NYLOCAS_HAGIOS_10802:
            case NYLOCAS_PRINKIPAS_10804:
            case NYLOCAS_PRINKIPAS_10805:
            case NYLOCAS_PRINKIPAS_10806:
                if (nylocasNpcs.removeIf(n -> n.nylo != null && n.nylo == npc) || NYLO_DEMI_BOSS_IDS.contains(id)) {
                    if (NYLO_DEMI_BOSS_IDS.contains(id)) {
                        nyloMiniboss = null;
                        minibossAlive = false;
                        bossChangeTicks = -1;
                    }

                    if (minibossAlive) {
                        nylocasAliveCounterOverlay.setNyloAlive(nylocasNpcs.size() + 3);
                    } else {
                        nylocasAliveCounterOverlay.setNyloAlive(nylocasNpcs.size());
                    }
                }

                aggressiveNylocas.remove(npc);
                if (nyloWave == 31 && nylocasNpcs.size() == 0) {
                    if ((config.nyloSplitsMsg() == SpoonTobConfig.nyloSplitsMessage.WAVES || config.nyloSplitsMsg() == SpoonTobConfig.nyloSplitsMessage.BOTH)
                            && config.splitMsgTiming() == SpoonTobConfig.splitsMsgTiming.CLEANUP) {
                        if (config.smallSplitsType() == SpoonTobConfig.smallSplitsMode.CAP || config.smallSplitsType() == SpoonTobConfig.smallSplitsMode.BOTH) {
                            client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Pre-cap splits: <col=00FFFF>" + preMageSplits + "</col> - <col=00FF00>"
                                    + preRangeSplits + "</col> - <col=ff0000>" + preMeleeSplits + "</col> Post-cap splits: <col=00FFFF>" + postMageSplits + "</col> - <col=00FF00>"
                                    + postRangeSplits + "</col> - <col=ff0000>" + postMeleeSplits, null);
                        }
                        if (config.smallSplitsType() == SpoonTobConfig.smallSplitsMode.TOTAL || config.smallSplitsType() == SpoonTobConfig.smallSplitsMode.BOTH) {
                            client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Small splits: <col=00FFFF>" + mageSplits + "</col> - <col=00FF00>"
                                    + rangeSplits + "</col> - <col=ff0000>" + meleeSplits + "</col> ", null);
                        }
                    }
                    if (endOfWavesCallback != null) {
                        endOfWavesCallback.run();
                    }
                }
                setAlive = false;
                break;
            case NYLOCAS_VASILIAS:
            case NYLOCAS_VASILIAS_8355:
            case NYLOCAS_VASILIAS_8356:
            case NYLOCAS_VASILIAS_8357:
            case NYLOCAS_VASILIAS_10786: //Story mode
            case NYLOCAS_VASILIAS_10787:
            case NYLOCAS_VASILIAS_10788:
            case NYLOCAS_VASILIAS_10789:
            case NYLOCAS_VASILIAS_10807: //Hard mode
            case NYLOCAS_VASILIAS_10808:
            case NYLOCAS_VASILIAS_10809:
            case NYLOCAS_VASILIAS_10810:
                nyloBossAlive = false;
                nylocasBoss = null;
                break;
            case NPCID_NYLOCAS_PILLAR:
            case NPCID_NYLOCAS_SM_PILLAR: //Story Mode
            case NPCID_NYLOCAS_HM_PILLAR: //Hard Mode
                if (nylocasPillars.containsKey(npc)) {
                    nylocasPillars.remove(npc);
                }

                if (nylocasPillars.size() < 1) {
                    nyloWaveStart = null;
                    nyloActive = false;
                }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        int[] varps = client.getVarps();
        int newVarbit6447 = client.getVarbitValue(varps, 6447);
        if (isInNyloRegion() && newVarbit6447 != 0 && newVarbit6447 != varbit6447) {
            nyloWaveStart = Instant.now();
            if (nylocasAliveCounterOverlay != null) {
                nylocasAliveCounterOverlay.setNyloWaveStart(nyloWaveStart);
            }
        }

        if (TheatreRegions.inRegion(client, TheatreRegions.NYLOCAS)) {
            nyloActive = client.getVarbitValue(6447) != 0;
        }

        varbit6447 = newVarbit6447;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            if (isInNyloRegion()) {
                startupNyloOverlay();

                if (config.hidePillars() == SpoonTobConfig.hidePillarsMode.PILLARS) {
                    removeGameObjectsFromScene(ImmutableSet.of(32862), 0);
                } else if (config.hidePillars() == SpoonTobConfig.hidePillarsMode.CLEAN) {
                    removeGameObjectsFromScene(ImmutableSet.of(32862, 32876, 32899), 0);
                }

                if (config.hideEggs()) {
                    removeGameObjectsFromScene(ImmutableSet.of(32939, 32937, 2739, 32865), 0);
                }
            } else {
                if (!nyloSelectionManager.isHidden() || !nylocasAliveCounterOverlay.isHidden()) {
                    shutdownNyloOverlay();
                }

                resetNylo();
                isInstanceTimerRunning = false;
            }

            nextInstance = true;
        }

    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (nyloActive) {
            if (skipTickCheck) {
                skipTickCheck = false;
            } else {
                if (client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null) {
                    return;
                }
                int equippedWeapon = ObjectUtils.defaultIfNull(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON), -1);
                weaponStyle = WeaponMap.StyleMap.get(equippedWeapon);
            }

            if (waveSpawnTicks >= 0) {
                waveSpawnTicks--;
                if (waveSpawnTicks < 0 && nylocasAliveCounterOverlay.getNyloAlive() >= nylocasAliveCounterOverlay.getMaxNyloAlive()) {
                    waveSpawnTicks = 3;
                    stalledWave = true;
                }
            }
            meleeNyloRaveColors.clear();
            rangeNyloRaveColors.clear();
            mageNyloRaveColors.clear();

            for (int i = nylocasNpcs.size() - 1; i >= 0; i--)
            {
                NyloInfo ni = nylocasNpcs.get(i);
                ni.ticks--;
                if (ni.ticks < 0 || ni.nylo.isDead() || !ni.alive)
                {
                    nylocasNpcs.remove(ni);
                    continue;
                }

                if (MELEE_IDS.contains(ni.nylo.getId())) {
                    meleeNyloRaveColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
                } else if (RANGE_IDS.contains(ni.nylo.getId())) {
                    rangeNyloRaveColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
                } else if (MAGIC_IDS.contains(ni.nylo.getId())) {
                    mageNyloRaveColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
                }
            }

            for (NPC pillar : nylocasPillars.keySet())
            {
                int healthPercent = pillar.getHealthRatio();
                if (healthPercent > -1)
                {
                    nylocasPillars.replace(pillar, healthPercent);
                }
            }

            boolean foundPillar = false;
            for (NPC npc : this.client.getNpcs()) {
                if (npc.getId() == NPCID_NYLOCAS_PILLAR || npc.getId() == NPCID_NYLOCAS_SM_PILLAR || npc.getId() == NPCID_NYLOCAS_HM_PILLAR) {
                    foundPillar = true;
                    break;
                }
            }
            NPC minNPC = null;
            int minHealth = 100;
            if (foundPillar) {
                for (NPC npc : this.nylocasPillars.keySet()) {
                    int health = (npc.getHealthRatio() > -1) ? npc.getHealthRatio() : this.nylocasPillars.get(npc);
                    this.nylocasPillars.replace(npc, health);
                    if (health < minHealth) {
                        minHealth = health;
                        minNPC = npc;
                    }
                }
                if (minNPC != null && this.config.showLowestPillar() && showHint)
                    this.client.setHintArrow(minNPC);
            } else {
                this.nylocasPillars.clear();
            }

            if ((instanceTimer + 1) % 4 == 1 && nyloWave < NylocasWave.MAX_WAVE && ticksSinceLastWave < 2) {
                if (config.nyloStallMessage() && nylocasAliveCounterOverlay.getNyloAlive() >= nylocasAliveCounterOverlay.getMaxNyloAlive()) {
                    client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Stalled wave: <col=FF0000>" + nyloWave + " </col>Time:<col=FF0000> "
                            + nylocasAliveCounterOverlay.getFormattedTime() + " </col>Nylos alive:<col=FF0000> " + nylocasAliveCounterOverlay.getNyloAlive() + "/"
                            + nylocasAliveCounterOverlay.getMaxNyloAlive(), "", false);
                }
            }

            ticksSinceLastWave = Math.max(0, ticksSinceLastWave - 1);

            if (nylocasBoss != null && nyloBossAlive) {
                bossChangeTicks--;
                if (nylocasBoss.getId() != lastBossId) {
                    lastBossId = nylocasBoss.getId();
                    if (nylocasBoss.getId() == 10787 || nylocasBoss.getId() == 10788 || nylocasBoss.getId() == 10789) {
                        bossChangeTicks = 15;
                    } else {
                        bossChangeTicks = 10;
                    }
                }
            } else if (minibossAlive && nyloMiniboss != null){
                bossChangeTicks--;
            }

            if (!splitsMap.isEmpty())
            {
                splitsMap.values().removeIf((value) -> value <= 1);
                splitsMap.replaceAll((key, value) -> value - 1);
            }
        }

        instanceTimer = (instanceTimer + 1) % 4;
    }

    @Subscribe
    protected void onClientTick(ClientTick event) {
        List<Player> players = client.getPlayers();
        for (Player player : players)
        {
            if (player.getWorldLocation() != null)
            {
                LocalPoint lp = player.getLocalLocation();

                WorldPoint wp = WorldPoint.fromRegion(player.getWorldLocation().getRegionID(), 5, 33, 0);
                LocalPoint lp1 = LocalPoint.fromWorld(client, wp.getX(), wp.getY());
                if (lp1 != null)
                {
                    Point base = new Point(lp1.getSceneX(), lp1.getSceneY());
                    Point point = new Point(lp.getSceneX() - base.getX(), lp.getSceneY() - base.getY());

                    if (isInBloatRegion() && point.getX() == -1 && (point.getY() == -1 || point.getY() == -2 || point.getY() == -3) && nextInstance)
                    {
                        client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nylo instance timer started.", "");
                        instanceTimer = 3;
                        isInstanceTimerRunning = true;
                        nextInstance = false;
                    }
                }
            }
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {
        if (!bigNylos.isEmpty() && event.getActor() instanceof NPC) {
            NPC npc = (NPC) event.getActor();
            int anim = npc.getAnimation();
            if (bigNylos.contains(npc)) {
                if (anim == 8005 || anim == 7991 || anim == 7998) {
                    splitsMap.putIfAbsent(npc, 6);
                    bigNylos.remove(npc);
                }
                if (anim == 8006 || anim == 7992 || anim == 8000) {
                    splitsMap.putIfAbsent(npc, 4);
                    bigNylos.remove(npc);
                }
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event){
        String mes = event.getMessage();
        if (mes.contains("Wave 'The Nylocas'") && mes.contains("complete!<br>Duration: <col=ff0000>")){
            if ((config.nyloSplitsMsg() == SpoonTobConfig.nyloSplitsMessage.WAVES || config.nyloSplitsMsg() == SpoonTobConfig.nyloSplitsMessage.BOTH)
                    && config.splitMsgTiming() == SpoonTobConfig.splitsMsgTiming.FINISHED){
                if (config.smallSplitsType() == SpoonTobConfig.smallSplitsMode.CAP || config.smallSplitsType() == SpoonTobConfig.smallSplitsMode.BOTH){
                    client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Pre-cap splits: <col=00FFFF>" + preMageSplits + "</col> - <col=00FF00>"
                            + preRangeSplits + "</col> - <col=ff0000>" + preMeleeSplits + "</col> Post-cap splits: <col=00FFFF>" + postMageSplits + "</col> - <col=00FF00>"
                            + postRangeSplits + "</col> - <col=ff0000>" + postMeleeSplits, null);
                } if (config.smallSplitsType() == SpoonTobConfig.smallSplitsMode.TOTAL || config.smallSplitsType() == SpoonTobConfig.smallSplitsMode.BOTH)
                    client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Small splits: <col=00FFFF>" + mageSplits + "</col> - <col=00FF00>"
                            + rangeSplits + "</col> - <col=ff0000>" + meleeSplits + "</col> ", null);
            }
            if (config.nyloSplitsMsg() == SpoonTobConfig.nyloSplitsMessage.BOSS || config.nyloSplitsMsg() == SpoonTobConfig.nyloSplitsMessage.BOTH){
                client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Boss phases: <col=00FFFF>" + mageBoss + "</col> - <col=00FF00>"
                        + rangeBoss + "</col> - <col=ff0000>" + meleeBoss + "</col> ", null);
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuOption().equalsIgnoreCase("wield")) {
            WeaponStyle newStyle = WeaponMap.StyleMap.get(event.getItemId());
            if (newStyle != null) {
                skipTickCheck = true;
                weaponStyle = newStyle;
            }
        } else if ((config.wheelchairNylo() == SpoonTobConfig.wheelchairMode.BOSS || config.wheelchairNylo() == SpoonTobConfig.wheelchairMode.BOTH)
                && nylocasBoss != null && event.getMenuTarget().contains(BOSS_NYLO) && event.getMenuOption().equalsIgnoreCase("attack") && weaponStyle != null) {
            switch (weaponStyle) {
                case TRIDENTS:
                case MAGIC:
                    if (nylocasBoss.getId() != NYLOCAS_VASILIAS_8356 && nylocasBoss.getId() != NYLOCAS_VASILIAS_10788
                            && nylocasBoss.getId() != NYLOCAS_VASILIAS_10809) {
                        event.consume();
                    }
                    break;
                case MELEE:
                    if (nylocasBoss.getId() != NYLOCAS_VASILIAS_8355 && nylocasBoss.getId() != NYLOCAS_VASILIAS_10787
                            && nylocasBoss.getId() != NYLOCAS_VASILIAS_10808) {
                        event.consume();
                    }
                    break;
                case RANGE:
                    if (nylocasBoss.getId() != NYLOCAS_VASILIAS_8357 && nylocasBoss.getId() != NYLOCAS_VASILIAS_10789
                            && nylocasBoss.getId() != NYLOCAS_VASILIAS_10810) {
                        event.consume();
                    }
                    break;
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (nyloActive) {
            String target = event.getTarget();
            String option = event.getOption();
            MenuEntry nEntry = event.getMenuEntry();
            MenuEntry nbEntry = event.getMenuEntry();

            if (config.nyloRecolorMenu() && (option.equalsIgnoreCase("attack") || event.getType() == MenuAction.WIDGET_TARGET_ON_NPC.getId())) {
                MenuEntry[] entries = client.getMenuEntries();
                MenuEntry toEdit = entries[entries.length - 1];

                String strippedTarget = Text.removeTags(target);
                int timeAlive;
                String timeAliveString = "";

                NPC npc = client.getCachedNPCs()[toEdit.getIdentifier()];
                if (npc != null && npc.getComposition() != null) {
                    boolean isBig = npc.getComposition().getSize() > 1;
                    for (NyloInfo ni : nylocasNpcs) {
                        if (config.nyloTicksMenu() && ni.nylo == npc) {
                            if (config.nyloTimeAliveCountStyle() == SpoonTobConfig.nylotimealive.COUNTUP) {
                                timeAlive = 52 - ni.ticks;
                                timeAliveString = ColorUtil.prependColorTag(" - " + timeAlive, new Color(255 * timeAlive / 52, 255 * (52 - timeAlive) / 52, 0));
                            } else {
                                timeAlive = ni.ticks;
                                timeAliveString = ColorUtil.prependColorTag(" - " + timeAlive, new Color(255 * (52 - timeAlive) / 52, 255 * timeAlive / 52, 0));
                            }
                            break;
                        }
                    }

                    if (strippedTarget.contains(MAGE_NYLO)) {
                        if (isBig) {
                            toEdit.setTarget(ColorUtil.prependColorTag(strippedTarget, new Color(0, 190, 190)) + timeAliveString);
                        } else {
                            toEdit.setTarget(ColorUtil.prependColorTag(strippedTarget, new Color(0, 255, 255)) + timeAliveString);
                        }
                    } else if (strippedTarget.contains(MELEE_NYLO)) {
                        if (isBig) {
                            toEdit.setTarget(ColorUtil.prependColorTag(strippedTarget, new Color(190, 150, 150)) + timeAliveString);
                        } else {
                            toEdit.setTarget(ColorUtil.prependColorTag(strippedTarget, new Color(255, 188, 188)) + timeAliveString);
                        }
                    } else if (strippedTarget.contains(RANGE_NYLO)) {
                        if (isBig) {
                            toEdit.setTarget(ColorUtil.prependColorTag(strippedTarget, new Color(0, 190, 0)) + timeAliveString);
                        } else {
                            toEdit.setTarget(ColorUtil.prependColorTag(strippedTarget, new Color(0, 255, 0)) + timeAliveString);
                        }
                    }
                    client.setMenuEntries(entries);
                }
            }

            if ((config.wheelchairNylo() == SpoonTobConfig.wheelchairMode.WAVES || config.wheelchairNylo() == SpoonTobConfig.wheelchairMode.BOTH)
                    && option.equalsIgnoreCase("attack") && weaponStyle != null) {
                switch (weaponStyle) {
                    case TRIDENTS:
                        if (target.contains(MELEE_NYLO) || target.contains(RANGE_NYLO)) {
                            nEntry.setDeprioritized(true);
                        }
                        break;
                    case MAGIC:
                        if (config.manualCast()) {
                            if (target.contains(MELEE_NYLO) || target.contains(RANGE_NYLO) || target.contains(MAGE_NYLO)) {
                                nEntry.setDeprioritized(true);
                            }
                        } else {
                            if (target.contains(MELEE_NYLO) || target.contains(RANGE_NYLO)) {
                                nEntry.setDeprioritized(true);
                            }
                        }
                        break;
                    case MELEE:
                        if (target.contains(RANGE_NYLO) || target.contains(MAGE_NYLO)) {
                            nEntry.setDeprioritized(true);
                        }
                        break;
                    case RANGE:
                        if (target.contains(MELEE_NYLO) || target.contains(MAGE_NYLO)) {
                            nEntry.setDeprioritized(true);
                        }
                        break;
                    case CHINS:
                        if (!config.ignoreChins() && (target.contains(MELEE_NYLO) || target.contains(MAGE_NYLO))) {
                            nEntry.setDeprioritized(true);
                        }
                        break;
                }
            }

            if ((config.wheelchairNylo() == SpoonTobConfig.wheelchairMode.BOSS || config.wheelchairNylo() == SpoonTobConfig.wheelchairMode.BOTH)
                    && nyloMiniboss != null && target.contains(DEMIBOSS_NYLO) && option.equalsIgnoreCase("attack") && weaponStyle != null) {
                switch (weaponStyle) {
                    case TRIDENTS:
                    case MAGIC:
                        if (nyloMiniboss.getId() != NYLOCAS_PRINKIPAS_10805) {
                            nbEntry.setDeprioritized(true);
                        }
                        break;
                    case MELEE:
                        if (nyloMiniboss.getId() != NYLOCAS_PRINKIPAS_10804) {
                            nbEntry.setDeprioritized(true);
                        }
                        break;
                    case RANGE:
                        if (nyloMiniboss.getId() != NYLOCAS_PRINKIPAS_10806) {
                            nbEntry.setDeprioritized(true);
                        }
                        break;
                }
            }
        }
    }

    static String stripColor(String str) {
        return str.replaceAll("(<col=[0-9a-f]+>|</col>)", "");
    }

    @Subscribe
    public void onMenuOpened(MenuOpened menu) {
        if (config.nyloRecolorMenu() && nyloActive && !nyloBossAlive) {
            client.setMenuEntries(Arrays.stream(menu.getMenuEntries()).filter((s) -> !s.getOption().equals("Examine")).toArray(MenuEntry[]::new));
        }
    }

    public void removeGameObjectsFromScene(Set<Integer> objectIDs, int plane) {
        Scene scene = client.getScene();
        Tile[][] tiles = scene.getTiles()[plane];
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                Tile tile = tiles[x][y];
                if (tile != null) {
                    if (objectIDs != null) {
                        Arrays.stream(tile.getGameObjects()).filter(obj -> (obj != null && objectIDs.contains(obj.getId()))).findFirst().ifPresent(scene::removeGameObject);
                    }
                }
            }
        }
    }

    boolean isInNyloRegion() {
        return client.isInInstancedRegion() && client.getMapRegions().length > 0 && client.getMapRegions()[0] == NYLO_MAP_REGION;
    }

    private boolean isInBloatRegion() {
        return client.isInInstancedRegion() && client.getMapRegions().length > 0 && client.getMapRegions()[0] == BLOAT_MAP_REGION;
    }
}