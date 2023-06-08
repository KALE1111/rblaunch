package net.runelite.client.plugins.spoontob.rooms.Verzik;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.MenuEntry;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.spoontob.Room;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.plugins.spoontob.util.PoisonStyle;
import net.runelite.client.plugins.spoontob.util.PoisonWeaponMap;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.runelite.api.NpcID.*;

public class Verzik extends Room {
    @Inject
    private Client client;

    @Inject
    private VerzikOverlay verzikOverlay;

    @Inject
    private LightningPanel lightningPanel;

    @Inject
    private PurpleCrabPanel purpleCrabPanel;

    @Inject
    private GreenBallPanel greenBallPanel;

    @Inject
    private YellowGrouperOverlay yellowGroupOverlay;

    @Inject
    private VerzikRedsOverlay redsOverlay;

    private PoisonStyle poisonStyle;
    private boolean skipTickCheck = false;

    private ClientThread clientThread;

    private static final Logger log = LoggerFactory.getLogger(SpoonTobPlugin.class);
    private static final int REGULAR_TORNADO = 8386;
    private static final int SM_TORNADO = 10846;
    private static final int HM_TORNADO = 10863;

    private static final int VERZIK_P1_MAGIC = 8109;
    private static final int VERZIK_P2_REG = 8114;
    private static final int VERZIK_P2_BOUNCE = 8116;
    private static final int VERZIK_ORGASM = 8117;

    private static final int p3_crab_attack_count = 5;
    private static final int p3_web_attack_count = 10;
    private static final int p3_yellow_attack_count = 15;
    private static final int p3_green_attack_count = 20;

    private static final int VERZIK_RANGE_BALL = 1583;
    private static final int VERZIK_LIGHTNING_BALL = 1585;
    private static final int VERZIK_YELLOW_OBJECT = 1595;
    private static final int VERZIK_YELLOW_BALL = 1596;
    private static final int VERZIK_GREEN_BALL = 1598;
    private static final int VERZIK_PURPLE_SPAWN = 1586;
    private static final int HM_ACID = 41747;

    protected static final Set<Integer> BEFORE_START_IDS = ImmutableSet.of(
            VERZIK_VITUR_8369, VERZIK_VITUR_10830, VERZIK_VITUR_10847
    );
    protected static final Set<Integer> P1_IDS = ImmutableSet.of(
            VERZIK_VITUR_8370, VERZIK_VITUR_10831, VERZIK_VITUR_10848
    );
    protected static final Set<Integer> P12_TRANSITION_IDS = ImmutableSet.of(
            VERZIK_VITUR_8371, VERZIK_VITUR_10832, VERZIK_VITUR_10849
    );
    protected static final Set<Integer> P2_IDS = ImmutableSet.of(
            VERZIK_VITUR_8372, VERZIK_VITUR_10833, VERZIK_VITUR_10850
    );
    protected static final Set<Integer> P23_TRANSITION_IDS = ImmutableSet.of(
            VERZIK_VITUR_8373, VERZIK_VITUR_10834, VERZIK_VITUR_10851
    );
    protected static final Set<Integer> P3_IDS = ImmutableSet.of(
            VERZIK_VITUR_8374, VERZIK_VITUR_10835, VERZIK_VITUR_10852
    );
    protected static final Set<Integer> DEAD_IDS = ImmutableSet.of(
            VERZIK_VITUR_8375, VERZIK_VITUR_10836, VERZIK_VITUR_10853
    );
    protected static final Set<Integer> NADO_IDS = ImmutableSet.of(
            REGULAR_TORNADO, SM_TORNADO, HM_TORNADO
    );
    protected static final Set<Integer> VERZIK_ACTIVE_IDS = ImmutableSet.of(
            VERZIK_VITUR_8370, VERZIK_VITUR_8371, VERZIK_VITUR_8372, VERZIK_VITUR_8373, VERZIK_VITUR_8374,
            VERZIK_VITUR_10831, VERZIK_VITUR_10832, VERZIK_VITUR_10833, VERZIK_VITUR_10834, VERZIK_VITUR_10835,
            VERZIK_VITUR_10848, VERZIK_VITUR_10849, VERZIK_VITUR_10850, VERZIK_VITUR_10851, VERZIK_VITUR_10852
    );

    @Getter
    private NPC verzikNPC;
    @Getter
    private boolean verzikActive;
    @Getter
    private Map<NPC, Pair<Integer, Integer>> verzikReds = new HashMap();
    @Getter
    private HashSet<NPC> verzikAggros = new HashSet();
    @Getter
    private int verzikTicksUntilAttack = -1;
    @Getter
    private int verzikTotalTicksUntilAttack = 0;
    @Getter
    private boolean verzikEnraged = false;
    private boolean verzikFirstEnraged = false;
    @Getter
    private int verzikAttackCount;
    @Getter
    protected Phase verzikPhase;
    private boolean verzikTickPaused = true;
    protected boolean verzikRedPhase = false;
    @Getter
    private SpecialAttack verzikSpecial;
    private int verzikLastAnimation;

    @Getter
    public final Map<Projectile, WorldPoint> verzikRangeProjectiles = new HashMap();

    @Getter
    private final Map<LocalPoint, Integer> purpleCrabProjectile = new HashMap();
    @Getter
    private NPC purpleCrabNpc = null;
    public int purpleAttacksLeft = 0;

    public ArrayList<Integer> WEAPONS = new ArrayList<>(Arrays.asList(12926, 12006, 12899, 22292, 5698));
    public ArrayList<Integer> SERPS = new ArrayList<>(Arrays.asList(12931, 13197, 13199));
    public int weaponId = 0;
    public int helmId = 0;

    public boolean yellowsOut;
    public int yellowTimer;
    public int hmYellowSpotNum;

    public ArrayList<NPC> redCrabs = new ArrayList<>();
    public ArrayList<Integer> lastRatioList = new ArrayList<>();
    public ArrayList<Integer> lastHealthScaleList = new ArrayList<>();

    public ArrayList<GameObject> acidSpots = new ArrayList<>();
    public ArrayList<Integer> acidSpotsTimer = new ArrayList<>();

    public int lightningAttacks;
    public int lightningAttacksDelay;
    @Getter
    private Map<Projectile, Integer> verzikLightningProjectiles = new HashMap<>();

    public ArrayList<ArrayList<WorldPoint>> yellowGroups;
    private ArrayList<WorldPoint> yellows;
    public ArrayList<WorldPoint> yellowsList;

    @Getter
    private NPC personalNado = null;
    public ArrayList<TornadoTracker> nadoList;
    private WorldPoint prevPlayerWp;
    private int nadosOut;
    private int personalNadoRespawn = 0;

    private ArrayList<String> partyMembersNames = new ArrayList<>();

    public int greenBallBounces = 0;
    public boolean greenBallOut = false;
    public int greenBallDelay = 0;

    List<GameObject> pillarsPendingRemoval;
    public List<WorldPoint> pillarLocations;

    @Inject
    private Verzik(SpoonTobPlugin plugin, SpoonTobConfig config) {
        super(plugin, config);
        verzikSpecial = SpecialAttack.NONE;
        verzikLastAnimation = -1;

        purpleCrabProjectile.clear();
        purpleCrabNpc = null;
        purpleAttacksLeft = 0;
        weaponId = 0;
        helmId = 0;
        poisonStyle = null;

        yellowsOut = false;
        yellowTimer = 14;
        hmYellowSpotNum = 1;

        lightningAttacks = 4;
        lightningAttacksDelay = 0;
        yellowGroups = new ArrayList<>();
        yellows = new ArrayList<>();
        yellowsList = new ArrayList<>();
        nadosOut = 0;
        nadoList = new ArrayList<>();
        personalNadoRespawn = 0;
        greenBallBounces = 0;
        greenBallOut = false;
        greenBallDelay = 0;
        pillarsPendingRemoval = new ArrayList<>();
        pillarLocations = new ArrayList<>();
    }

    public void load() {
        overlayManager.add(verzikOverlay);
        overlayManager.add(lightningPanel);
        overlayManager.add(yellowGroupOverlay);
        overlayManager.add(greenBallPanel);
        overlayManager.add(purpleCrabPanel);
        overlayManager.add(redsOverlay);
        poisonStyle = null;
    }

    public void unload() {
        overlayManager.remove(verzikOverlay);
        overlayManager.remove(lightningPanel);
        overlayManager.remove(yellowGroupOverlay);
        overlayManager.remove(greenBallPanel);
        overlayManager.remove(purpleCrabPanel);
        overlayManager.remove(redsOverlay);
        verzikCleanup();
        plugin.clearHiddenNpcs();
        poisonStyle = null;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        switch(npc.getId()) {
            case VERZIK_VITUR_8369:
            case VERZIK_VITUR_8371:
            case VERZIK_VITUR_8373:
            case VERZIK_VITUR_8375:
            case VERZIK_VITUR_10830: //Story mode
            case VERZIK_VITUR_10832:
            case VERZIK_VITUR_10834:
            case VERZIK_VITUR_10836:
            case VERZIK_VITUR_10847: //Hard mode
            case VERZIK_VITUR_10849:
            case VERZIK_VITUR_10851:
            case VERZIK_VITUR_10853:
                verzikSpawn(npc);
                break;
            case VERZIK_VITUR_8370:
            case VERZIK_VITUR_10831: //Story Mode
            case VERZIK_VITUR_10848: //Hard Mode
                verzikPhase = Phase.PHASE1;
                verzikSpawn(npc);
                break;
            case VERZIK_VITUR_8372:
            case VERZIK_VITUR_10833: //Story Mode
            case VERZIK_VITUR_10850: //Hard Mode
                verzikPhase = Phase.PHASE2;
                verzikSpawn(npc);
                lightningAttacks = 4;
                break;
            case VERZIK_VITUR_8374:
            case VERZIK_VITUR_10835: //Story Mode
            case VERZIK_VITUR_10852: //Hard Mode
                verzikPhase = Phase.PHASE3;
                verzikSpawn(npc);
                break;
            case WEB:
            case WEB_10837: //Story Mode
            case WEB_10854: //Hard Mode
                if (verzikNPC != null && verzikNPC.getInteracting() == null) {
                    verzikSpecial = SpecialAttack.WEBS;
                }
                break;
            case NYLOCAS_ISCHYROS_8381:
            case NYLOCAS_TOXOBOLOS_8382:
            case NYLOCAS_HAGIOS_8383:
            case NYLOCAS_ISCHYROS_10841: //Story mode
            case NYLOCAS_TOXOBOLOS_10842:
            case NYLOCAS_HAGIOS_10843:
            case NYLOCAS_ISCHYROS_10858: //Hard mode
            case NYLOCAS_TOXOBOLOS_10859:
            case NYLOCAS_HAGIOS_10860:
                verzikAggros.add(npc);
                break;
            case NYLOCAS_MATOMENOS_8385:
            case NYLOCAS_MATOMENOS_10845: //Story Mode
            case NYLOCAS_MATOMENOS_10862: //Hard Mode
                verzikReds.putIfAbsent(npc, new MutablePair(npc.getHealthRatio(), npc.getHealthScale()));
                break;
            case REGULAR_TORNADO:
            case SM_TORNADO: //Story Mode
            case HM_TORNADO: //Hard Mode
                if (personalNado == null && personalNadoRespawn == 0){
                    nadoList.add(new TornadoTracker(npc));
                }

                if (!verzikEnraged) {
                    verzikEnraged = true;
                    verzikFirstEnraged = true;
                }
                nadosOut++;
        }
        if (npc.getName() != null && npc.getName().equals("Nylocas Athanatos")){
            purpleCrabNpc = npc;
        }

        if (npc.getName() != null && npc.getName().equals("Nylocas Matomenos")){
            redCrabs.add(npc);
            lastRatioList.add(0);
            lastHealthScaleList.add(0);
        }
    }

    @Subscribe
    public void onNpcChanged (NpcChanged event) {
        int id = event.getNpc().getId();
        if (DEAD_IDS.contains(id)) {
            verzikCleanup();
        } else if (P1_IDS.contains(id)) {
            partyMembersNames.clear();
            for (int i = 330; i < 335; i++) {
                if (client.getVarcStrValue(i) != null && !client.getVarcStrValue(i).equals("")) {
                    partyMembersNames.add(client.getVarcStrValue(i).replaceAll("[^A-Za-z0-9_-]", " ").trim());
                }
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        switch(npc.getId()) {
            case VERZIK_VITUR_8369:
            case VERZIK_VITUR_8370:
            case VERZIK_VITUR_8371:
            case VERZIK_VITUR_8372:
            case VERZIK_VITUR_8373:
            case VERZIK_VITUR_8374:
            case VERZIK_VITUR_8375:
            case VERZIK_VITUR_10830: //Story mode
            case VERZIK_VITUR_10831:
            case VERZIK_VITUR_10832:
            case VERZIK_VITUR_10833:
            case VERZIK_VITUR_10834:
            case VERZIK_VITUR_10835:
            case VERZIK_VITUR_10836:
            case VERZIK_VITUR_10847: //Hard mode
            case VERZIK_VITUR_10848:
            case VERZIK_VITUR_10849:
            case VERZIK_VITUR_10850:
            case VERZIK_VITUR_10851:
            case VERZIK_VITUR_10852:
            case VERZIK_VITUR_10853:
                verzikCleanup();
            case NYLOCAS_ISCHYROS_8381:
            case NYLOCAS_TOXOBOLOS_8382:
            case NYLOCAS_HAGIOS_8383:
            case NYLOCAS_ISCHYROS_10841: //Story mode
            case NYLOCAS_TOXOBOLOS_10842:
            case NYLOCAS_HAGIOS_10843:
            case NYLOCAS_ISCHYROS_10858: //Hard mode
            case NYLOCAS_TOXOBOLOS_10859:
            case NYLOCAS_HAGIOS_10860:
                verzikAggros.remove(npc);
                break;
            case NYLOCAS_MATOMENOS_8385:
            case NYLOCAS_MATOMENOS_10845: //Story Mode
            case NYLOCAS_MATOMENOS_10862: //Hard Mode
                verzikReds.remove(npc);
                break;
            case REGULAR_TORNADO:
            case SM_TORNADO: //Story Mode
            case HM_TORNADO: //Hard Mode
                if (personalNado == npc){
                    personalNado = null;
                    personalNadoRespawn = 18;
                }
                nadoList.removeIf(tt -> tt.getNpc() == npc);
                nadosOut--;
                if (plugin.hiddenIndices.contains(npc.getIndex())) {
                    plugin.setHiddenNpc(npc, false);
                }
        }
        if (npc.getName() != null && npc.getName().equals("Nylocas Athanatos")){
            purpleCrabNpc = null;
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        int id = event.getProjectile().getId();
        Projectile p = event.getProjectile();
        int ticks = (int) Math.round(p.getRemainingCycles() / 30D);

        if (id == VERZIK_RANGE_BALL) {
            WorldPoint wp = WorldPoint.fromLocal(client, event.getPosition());
            verzikRangeProjectiles.put(event.getProjectile(), wp);
            if (lightningAttacksDelay == 0) {
                lightningAttacks--;
                lightningAttacksDelay = 4;
            }
        } else if (id == VERZIK_PURPLE_SPAWN) {
            purpleCrabProjectile.put(event.getPosition(), ticks);
            purpleAttacksLeft = 21;
        } else if (id == VERZIK_LIGHTNING_BALL && lightningAttacks < 2) {
            lightningAttacks = 4;
            if (ticks > 0) {
                verzikLightningProjectiles.putIfAbsent(p, ticks);
            }
        } else if (id == VERZIK_GREEN_BALL){
            if (!greenBallOut){
                greenBallOut = true;
            }

            if (p.getRemainingCycles() == 0){
                greenBallOut = false;
                greenBallBounces++;
                greenBallDelay = 3;
            }
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event){
        if (event.getGraphicsObject().getId() == VERZIK_YELLOW_OBJECT && !yellowsOut) {
            WorldPoint wp = WorldPoint.fromLocal(client, event.getGraphicsObject().getLocation());
            if (!yellows.contains(wp)) {
                yellows.add(wp);
            }

            if (!yellowsList.contains(wp)) {
                yellowsList.add(wp);
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event){
        if (config.showVerzikAcid() && event.getGameObject().getId() == HM_ACID) {
            int index = acidSpots.indexOf(event.getGameObject());
            acidSpots.remove(event.getGameObject());
            if (index != -1) {
                acidSpotsTimer.remove(index);
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("spoontob")) {
            if (event.getKey().equals("hideOtherNados")) {
                if (!config.hideOtherNados()){
                    plugin.clearHiddenNpcs();
                }
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (verzikActive) {
            Actor actor = event.getActor();
            if (actor instanceof NPC) {
                NPC npc = (NPC)actor;
                if (npc.getName() != null && npc.getName().equals("Nylocas Matomenos") && npc.getAnimation() == 8097){
                    int index = redCrabs.indexOf(npc);
                    if (index != -1) {
                        lastRatioList.remove(index);
                        lastHealthScaleList.remove(index);
                    }
                    redCrabs.remove(npc);
                }
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (verzikNPC != null) {
            String target = event.getTarget();
            Player player = client.getLocalPlayer();
            PlayerComposition playerComp = player != null ? player.getPlayerComposition() : null;
            MenuEntry pvEntry = event.getMenuEntry();

            if (config.hidePurple() && P2_IDS.contains(verzikNPC.getId())
                    && target.contains("Nylocas Athanatos") && event.getType() == MenuAction.NPC_SECOND_OPTION.getId() && poisonStyle != null) {
                switch (poisonStyle) {
                    case NOT:
                        if (playerComp != null && !SERPS.contains(playerComp.getEquipmentId(KitType.HEAD))) {
                            pvEntry.setDeprioritized(true);
                        }
                        break;
                    case POISON:
                        break;
                }
            } else if (P3_IDS.contains(verzikNPC.getId())) {
                if (config.hideAttackYellows() && verzikSpecial == SpecialAttack.YELLOWS && verzikTicksUntilAttack > 8) {
                    if (target.contains("Verzik Vitur") && event.getType() == MenuAction.NPC_SECOND_OPTION.getId()) {
                        pvEntry.setDeprioritized(true);
                    }

                }
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        Player player = client.getLocalPlayer();
        PlayerComposition playerComp = player != null ? player.getPlayerComposition() : null;

        if (event.getMenuOption().equalsIgnoreCase("wield")) {
            PoisonStyle newStyle = PoisonWeaponMap.PoisonType.get(event.getItemId());
            if (newStyle != null) {
                skipTickCheck = true;
                poisonStyle = newStyle;
            }
        } else if (config.hidePurple() && verzikNPC != null && P2_IDS.contains(verzikNPC.getId())
                && event.getMenuTarget().contains("Nylocas Athanatos") && event.getMenuAction() == MenuAction.NPC_SECOND_OPTION && poisonStyle != null) {
            switch (poisonStyle) {
                case NOT:
                    if (playerComp != null && !SERPS.contains(playerComp.getEquipmentId(KitType.HEAD))) {
                    }
                    break;
                case POISON:
                    break;
            }
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if (verzikNPC != null && event.getActor() instanceof Player && event.getActor().getName() != null){
            partyMembersNames.remove(event.getActor().getName());
        }
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged event) {
        if (event.getActor() != null  && event.getActor().getName() != null && event.getActor() instanceof Player && verzikPhase == Phase.PHASE3){
            Actor actor = event.getActor();
            if (actor.getGraphic() == 1602 && actor.getName().equals(client.getLocalPlayer().getName())){
                personalNado = null;
                personalNadoRespawn = 18;
                if (nadoList.size() == 1){
                    nadoList.clear();
                }
            }
        }
    }

    @Subscribe
    public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event){
        if (event.getSource() != null && event.getSource().getName() != null && verzikNPC != null && config.muteVerzikSounds()) {
            if (event.getSoundId() == 3991 || event.getSoundId() == 3987){
                event.consume();
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (verzikActive) {
            if (skipTickCheck) {
                skipTickCheck = false;
            } else {
                if (client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null) {
                    return;
                }
                int equippedWeapon = ObjectUtils.defaultIfNull(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON), -1);
                int equippedHelm = ObjectUtils.defaultIfNull(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.HEAD), -1);
                poisonStyle = PoisonWeaponMap.PoisonType.get(equippedWeapon);
                helmId = equippedHelm;
            }

            if (lightningAttacksDelay > 0){
                lightningAttacksDelay--;
            }

            if (personalNadoRespawn > 0){
                personalNadoRespawn--;
            }

            if (greenBallDelay > 0){
                greenBallDelay--;
                if (greenBallDelay == 0 && !greenBallOut){
                    greenBallBounces = 0;
                }
            }

            if (acidSpots.size() > 0 && acidSpotsTimer.size() > 0){
                for(int i=0; i<acidSpotsTimer.size(); i++){
                    acidSpotsTimer.set(i, acidSpotsTimer.get(i) - 1);
                }
            }

            if (!verzikRangeProjectiles.isEmpty()) {
                verzikRangeProjectiles.keySet().removeIf((projectile) -> projectile.getRemainingCycles() < 1);
            }

            if (verzikPhase == Phase.PHASE3) {
                if (yellowsList.size() > 0){
                    if (!yellowsOut) {
                        if (verzikNPC.getId() == VERZIK_VITUR_10852) {
                            yellowGroups = findYellows(yellows);
                        }
                        yellowsOut = true;
                    }else {
                        yellowTimer--;
                        if (yellowTimer <= 0) {
                            if (verzikNPC.getId() == VERZIK_VITUR_10852) {
                                if (hmYellowSpotNum < 3) {
                                    yellowTimer = 3;
                                    hmYellowSpotNum++;

                                    for(Player p : client.getPlayers()){
                                        if (p.getName() != null && partyMembersNames.contains(p.getName())){
                                            WorldPoint wp = WorldPoint.fromLocal(client, p.getLocalLocation());
                                            int index = 0;

                                            for(ArrayList<WorldPoint> yg : yellowGroups){
                                                if (yg.contains(wp)){
                                                    yellowGroups.get(index).remove(wp);
                                                    break;
                                                }else {
                                                    boolean exitLoop = false;
                                                    for(int i = yg.size() - 1; i>=0; i--){
                                                        if (yg.get(i).distanceTo(wp) <= 1) {
                                                            yellowGroups.get(index).remove(i);
                                                            exitLoop = true;
                                                            break;
                                                        }
                                                    }

                                                    if (exitLoop){
                                                        break;
                                                    }
                                                }
                                                index++;
                                            }

                                            if (yellowsList.contains(wp)){
                                                yellowsList.remove(wp);
                                            }else {
                                                for(int i = yellowsList.size() - 1; i>=0; i--){
                                                    if (yellowsList.get(i).distanceTo(wp) <= 1){
                                                        yellowsList.remove(i);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    yellowsOut = false;
                                    yellowTimer = 14;
                                    hmYellowSpotNum = 1;
                                    yellows.clear();
                                    yellowsList.clear();
                                    yellowGroups.clear();
                                }
                            } else {
                                yellowsOut = false;
                                yellowTimer = 14;
                                yellows.clear();
                                yellowsList.clear();
                                yellowGroups.clear();
                            }
                        }
                    }
                }
            }

            if (verzikPhase == Phase.PHASE3 && nadoList.size() > 0 && client.getLocalPlayer() != null){
                boolean recalc = false;
                for (int i = nadoList.size() - 1; i>=0; i--){
                    if (nadoList.get(i).checkMovement(prevPlayerWp, nadoList.get(i).getNpc().getWorldLocation()) != -1){
                        nadoList.remove(i);
                        if (nadoList.size() == 0) {
                            for (NPC npc : client.getNpcs()) {
                                if (NADO_IDS.contains(npc.getId()))
                                    nadoList.add(new TornadoTracker(npc));
                                personalNado = null;
                                recalc = true;

                                if (plugin.hiddenIndices.contains(npc.getIndex())) {
                                    plugin.setHiddenNpc(npc, false);
                                }
                            }
                        }
                    } else {
                        nadoList.get(i).setPrevLoc(nadoList.get(i).getNpc().getWorldLocation());
                    }
                }

                if (nadoList.size() == 1 && personalNado == null && !recalc){
                    personalNado = nadoList.get(0).getNpc();
                }
                prevPlayerWp = client.getLocalPlayer().getWorldLocation();
            }

            Function<Integer, Integer> adjust_for_enrage = (i) -> isVerzikEnraged() ? i - 2 : i;

            if (verzikTickPaused) {
                switch (verzikNPC.getId()) {
                    case VERZIK_VITUR_8370:
                    case VERZIK_VITUR_10831:
                    case VERZIK_VITUR_10848:
                        verzikPhase = Phase.PHASE1;
                        verzikAttackCount = 0;
                        verzikTicksUntilAttack = 18;
                        verzikTickPaused = false;
                        break;
                    case VERZIK_VITUR_8372:
                    case VERZIK_VITUR_10832:
                    case VERZIK_VITUR_10850:
                        verzikPhase = Phase.PHASE2;
                        verzikAttackCount = 0;
                        verzikTicksUntilAttack = 3;
                        verzikTickPaused = false;
                        purpleAttacksLeft = 21;
                        break;
                    case VERZIK_VITUR_8374:
                    case VERZIK_VITUR_10835:
                    case VERZIK_VITUR_10852:
                        verzikPhase = Phase.PHASE3;
                        verzikAttackCount = 0;
                        verzikTicksUntilAttack = 6;
                        verzikTickPaused = false;
                        break;
                }
            }
            else if (verzikSpecial == SpecialAttack.WEBS)
            {
                verzikTotalTicksUntilAttack++;

                if (verzikNPC.getInteracting() != null)
                {
                    verzikSpecial = SpecialAttack.WEB_COOLDOWN;
                    verzikAttackCount = 10;
                    verzikTicksUntilAttack = 10;
                    verzikFirstEnraged = false;
                }
            } else {
                verzikTicksUntilAttack = Math.max(0, verzikTicksUntilAttack - 1);
                verzikTotalTicksUntilAttack++;

                int animationID = verzikNPC.getAnimation();

                if (animationID > -1 && verzikPhase == Phase.PHASE1 && verzikTicksUntilAttack < 5 && animationID != verzikLastAnimation)
                {
                    if (animationID == VERZIK_P1_MAGIC)
                    {
                        verzikTicksUntilAttack = 14;
                        verzikAttackCount++;
                    }
                }

                if (animationID > -1 && verzikPhase == Phase.PHASE2 && verzikTicksUntilAttack < 3 && animationID != verzikLastAnimation)
                {
                    switch (animationID)
                    {
                        case VERZIK_P2_REG:
                        case VERZIK_P2_BOUNCE:
                            verzikTicksUntilAttack = 4;
                            verzikAttackCount++;
                            purpleAttacksLeft--;
                            if (verzikAttackCount == 7 && verzikRedPhase)
                            {
                                verzikTicksUntilAttack = 8;
                            }
                            break;
                        case VERZIK_ORGASM:
                            verzikRedPhase = true;
                            verzikAttackCount = 0;
                            verzikTicksUntilAttack = 12;
                            break;
                    }
                }

                verzikLastAnimation = animationID;
                if (verzikPhase == Phase.PHASE3) {
                    verzikAttackCount = verzikAttackCount % p3_green_attack_count;

                    if (verzikTicksUntilAttack <= 0) {
                        verzikAttackCount++;

                        // First 9 Attacks, Including Crabs
                        if (verzikAttackCount < p3_web_attack_count) {
                            verzikSpecial = SpecialAttack.NONE;
                            verzikTicksUntilAttack = adjust_for_enrage.apply(7);
                        }
                        // Between Webs and Yellows
                        else if (verzikAttackCount < p3_yellow_attack_count) {
                            verzikSpecial = SpecialAttack.NONE;
                            verzikTicksUntilAttack = adjust_for_enrage.apply(7);
                        }
                        // Yellows - Can't Attack
                        else if (verzikAttackCount < p3_yellow_attack_count + 1) {
                            verzikSpecial = SpecialAttack.YELLOWS;
                            if (verzikNPC.getId() == 10852) {
                                verzikTicksUntilAttack = 27;
                            } else {
                                verzikTicksUntilAttack = 21;
                            }
                        }
                        // Between Yellows and Green Ball
                        else if (verzikAttackCount < p3_green_attack_count) {
                            verzikSpecial = SpecialAttack.NONE;
                            verzikTicksUntilAttack = adjust_for_enrage.apply(7);
                        }
                        // Ready for Green Ball
                        else if (verzikAttackCount < p3_green_attack_count + 1) {
                            verzikSpecial = SpecialAttack.GREEN;
                            // 12 During Purples?
                            verzikTicksUntilAttack = 12;
                        } else {
                            verzikSpecial = SpecialAttack.NONE;
                            verzikTicksUntilAttack = adjust_for_enrage.apply(7);
                        }
                    }

                    if (verzikFirstEnraged) {
                        verzikFirstEnraged = false;
                        if (verzikSpecial != SpecialAttack.YELLOWS || verzikTicksUntilAttack <= 7) {
                            verzikTicksUntilAttack = 5;
                        }
                    }
                }
            }
            if (purpleCrabProjectile.size() > 0) {
                purpleCrabProjectile.values().removeIf(valueIsZero);
                purpleCrabProjectile.replaceAll(updateTicks);
            }

            if (verzikPhase == Phase.PHASE2) {
                for (Iterator<Projectile> it = verzikLightningProjectiles.keySet().iterator(); it.hasNext(); ) {
                    Projectile key = it.next();
                    verzikLightningProjectiles.replace(key, verzikLightningProjectiles.get(key) - 1);
                    if (verzikLightningProjectiles.get(key) < 0) {
                        it.remove();
                    }
                }
            }
        }
    }

    Color verzikSpecialWarningColor() {
        Color col = Color.WHITE;

        if (verzikPhase != Phase.PHASE3) {
            return col;
        }

        switch (verzikAttackCount) {
            case Verzik.p3_crab_attack_count - 1:
                col = Color.MAGENTA;
                break;
            case Verzik.p3_web_attack_count - 1:
                col = Color.ORANGE;
                break;
            case Verzik.p3_yellow_attack_count - 1:
                col = Color.YELLOW;
                break;
            case Verzik.p3_green_attack_count - 1:
                col = Color.GREEN;
                break;
        }

        return col;
    }

    private void verzikSpawn(NPC npc) {
        verzikEnraged = false;
        verzikRedPhase = false;
        verzikFirstEnraged = false;
        verzikTicksUntilAttack = 0;
        verzikAttackCount = 0;
        verzikNPC = npc;
        verzikActive = true;
        verzikTickPaused = true;
        verzikSpecial = SpecialAttack.NONE;
        verzikTotalTicksUntilAttack = 0;
        verzikLastAnimation = -1;
    }

    private void verzikCleanup() {
        verzikAggros.clear();
        verzikReds.clear();
        verzikEnraged = false;
        verzikFirstEnraged = false;
        verzikRedPhase = false;
        verzikActive = false;
        yellowsList.clear();
        yellowGroups.clear();
        yellowsOut = false;
        yellowTimer = 14;
        hmYellowSpotNum = 1;
        nadoList.clear();
        prevPlayerWp = null;
        personalNado = null;
        nadosOut = 0;
        verzikNPC = null;
        verzikPhase = null;
        verzikTickPaused = true;
        verzikSpecial = SpecialAttack.NONE;
        verzikTotalTicksUntilAttack = 0;
        verzikLastAnimation = -1;

        redCrabs.clear();
        lastRatioList.clear();
        lastHealthScaleList.clear();
        acidSpots.clear();
        acidSpotsTimer.clear();
        lightningAttacks = 4;
        lightningAttacksDelay = 0;
        greenBallBounces = 0;
        greenBallOut = false;
        greenBallDelay = 0;
        pillarsPendingRemoval = new ArrayList<>();
        pillarLocations = new ArrayList<>();
    }

    enum SpecialAttack {
        WEB_COOLDOWN,
        WEBS,
        YELLOWS,
        GREEN,
        NONE;
    }

    public enum Phase {
        PHASE1,
        PHASE2,
        PHASE3;
    }

    public static final Predicate<Integer> valueIsZero = (v) -> v <= 0;
    public static final BiFunction<Object, Integer, Integer> updateTicks = (k, v) -> v - 1;

    public WorldPoint getNearestPoint(WorldPoint corner, ArrayList<WorldPoint> points) {
        double minDistance = Integer.MAX_VALUE;
        WorldPoint point = new WorldPoint(corner.getX(), corner.getY(), corner.getPlane());
        for (WorldPoint p : points) {
            double distance = distanceBetween(p, corner);
            if (distance < minDistance) {
                minDistance = distance;
                point = p;
            }
        }
        return point;
    }

    public int isSetSpawn(WorldPoint p) {
        if (p.getRegionX() == 7 && p.getRegionY() == 11) {
            return 1;
        }
        else if (p.getRegionX() == 16 && p.getRegionY() == 17) {
            return 2;
        }
        else if (p.getRegionX() == 25 && p.getRegionY() == 11) {
            return 3;
        }
        else if (p.getRegionX() == 7 && p.getRegionY() == 23) {
            return 4;
        }
        else if (p.getRegionX() == 25 && p.getRegionY() == 23) {
            return 5;
        }
        else {
            return -1;
        }
    }

    public WorldPoint getNextValidPoint(ArrayList<WorldPoint> points) {
        for(WorldPoint p : points) {
            if (isSetSpawn(p) != -1) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<ArrayList<WorldPoint>> findYellows(ArrayList<WorldPoint> points) {
        ArrayList<ArrayList<WorldPoint>> groups = new ArrayList<>();
        while(points.size() > 0) {
            ArrayList<WorldPoint> group = new ArrayList<>();
            WorldPoint initial = getNextValidPoint(points);
            group.add(initial);
            points.remove(initial);
            WorldPoint second = getNearestPoint(initial, points);
            group.add(second);
            points.remove(second);
            WorldPoint third = getNearestPoint(initial, points);
            group.add(third);
            points.remove(third);
            groups.add(group);
        }
        return groups;
    }

    public double distanceBetween(WorldPoint a, WorldPoint b) {
        return Math.sqrt(Math.pow(a.getRegionX()-b.getRegionX(), 2) + Math.pow(a.getRegionY()-b.getRegionY(), 2));
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (config.hideOtherNados()) {
            for (NPC npc : client.getNpcs()) {
                if (npc != null && NADO_IDS.contains(npc.getId())) {
                    if (personalNado != null && personalNado.getIndex() != npc.getIndex()) {
                        if (!plugin.hiddenIndices.contains(npc.getIndex())) {
                            plugin.setHiddenNpc(npc, true);
                        }
                    }
                }
            }
        }
    }
}