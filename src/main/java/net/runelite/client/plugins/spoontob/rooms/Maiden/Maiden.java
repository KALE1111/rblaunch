package net.runelite.client.plugins.spoontob.rooms.Maiden;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.spoontob.Room;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.runelite.api.NpcID.*;

public class Maiden extends Room {
    @Inject
    private Client client;

    @Inject
    private MaidenOverlay maidenOverlay;

    @Inject
    private ThresholdOverlay thresholdOverlay;

    @Inject
    private MaidenMaxHitOverlay maidenMaxHitOverlay;

    @Inject
    private MaidenRedsOverlay redsOverlay;

    private static final int BLOOD_SPLAT_GRAPHIC = 1579;
    private static final int BLOOD_SPLAT_PROJECTILE = 1578;

    @Getter
    private boolean maidenActive;

    @Getter
    private NPC maidenNPC;

    @Getter
    private List<NPC> maidenSpawns = new ArrayList<>();

    @Getter
    private Map<NPC, Pair<Integer, Integer>> maidenReds = new HashMap<>();

    @Getter
    private List<WorldPoint> maidenBloodSplatters = new ArrayList<>();

    @Getter
    private ArrayList<MaidenBloodSplatInfo> maidenBloodSplatterProj = new ArrayList<>();

    @Getter
    public ArrayList<Color> maidenBloodSplattersColors = new ArrayList<>();

    @Getter
    private List<WorldPoint> maidenBloodSpawnLocations = new ArrayList<>();

    @Getter
    private List<WorldPoint> maidenBloodSpawnTrailingLocations = new ArrayList<>();

    @Getter
    private int newMaidenHp = -1;
    @Getter
    private int newMaidenThresholdHp = -1;
    @Getter
    private short realMaidenHp = -1;
    @Getter
    private short thresholdHp = -1;
    @Getter
    private double maxHit;
    private short timesMaidenHealed = 0;
    private short amountMaidenHealed = 0;
    public final DecimalFormat df1 = new DecimalFormat("#0.0");
    private final Consumer<Double> setThreshold = (percent) -> {
        thresholdHp = (short)((int)Math.floor((double)getMaidenBaseHpIndex() * percent));
    };

    public int ticksUntilAttack = 0;
    public int maidenAttSpd = 10;
    public int lastAnimationID = -1;

    private static final Set<MenuAction> NPC_MENU_ACTIONS = ImmutableSet.of(MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION, MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION, MenuAction.NPC_FIFTH_OPTION, MenuAction.WIDGET_TARGET_ON_NPC, MenuAction.ITEM_USE_ON_NPC);;
    public Color c;

    public int nyloSpawnDelay = 2;
    public int maidenPhase = 70;
    public ArrayList<MaidenCrabInfo> maidenCrabInfoList = new ArrayList<MaidenCrabInfo>();
    public Map<NPC, Integer> frozenBloodSpawns = new HashMap<>();
    public int crabTicksSinceSpawn = 0;

    @Inject
    protected Maiden(SpoonTobPlugin plugin, SpoonTobConfig config) {
        super(plugin, config);
        maxHit = 36.5D;
    }

    public void load() {
        overlayManager.add(maidenOverlay);
        overlayManager.add(thresholdOverlay);
        overlayManager.add(maidenMaxHitOverlay);
        overlayManager.add(redsOverlay);
    }

    public void unload() {
        overlayManager.remove(maidenOverlay);
        overlayManager.remove(thresholdOverlay);
        overlayManager.remove(maidenMaxHitOverlay);
        overlayManager.remove(redsOverlay);

        maidenActive = false;
        maidenBloodSplatters.clear();
        maidenBloodSplattersColors.clear();
        maidenSpawns.clear();
        maidenBloodSpawnLocations.clear();
        maidenBloodSpawnTrailingLocations.clear();

        newMaidenHp = -1;
        newMaidenThresholdHp = -1;
        timesMaidenHealed = 0;
        amountMaidenHealed = 0;
        realMaidenHp = -1;
        thresholdHp = -1;
        maxHit = 36.5D;
    }

    void updateMaidenMaxHit()
    {
        maxHit += 3.5D;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        String name = npc.getName();
        switch (npc.getId()) {
            case THE_MAIDEN_OF_SUGADINTI: //normal mode
            case THE_MAIDEN_OF_SUGADINTI_8361:
            case THE_MAIDEN_OF_SUGADINTI_8362:
            case THE_MAIDEN_OF_SUGADINTI_8363:
            case THE_MAIDEN_OF_SUGADINTI_8364:
            case THE_MAIDEN_OF_SUGADINTI_8365:
            case THE_MAIDEN_OF_SUGADINTI_10814: //story mode
            case THE_MAIDEN_OF_SUGADINTI_10815:
            case THE_MAIDEN_OF_SUGADINTI_10816:
            case THE_MAIDEN_OF_SUGADINTI_10817:
            case THE_MAIDEN_OF_SUGADINTI_10818:
            case THE_MAIDEN_OF_SUGADINTI_10819:
            case THE_MAIDEN_OF_SUGADINTI_10822: //hard mode
            case THE_MAIDEN_OF_SUGADINTI_10823:
            case THE_MAIDEN_OF_SUGADINTI_10824:
            case THE_MAIDEN_OF_SUGADINTI_10825:
            case THE_MAIDEN_OF_SUGADINTI_10826:
            case THE_MAIDEN_OF_SUGADINTI_10827:
                maidenActive = true;
                maidenNPC = npc;
                if((maidenNPC.getHealthRatio() == -1 && maidenNPC.getHealthScale() == -1 && (maidenNPC.getId() != 10822 && maidenNPC.getId() != 8360 && maidenNPC.getId() != 10814))
                        || (maidenNPC.getHealthRatio() != maidenNPC.getHealthScale())){
                    ticksUntilAttack = -1;
                }else {
                    ticksUntilAttack = 10;
                }
                maidenAttSpd = 10;
                maidenCrabInfoList.clear();
                if (realMaidenHp < 0) {
                    realMaidenHp = getMaidenBaseHpIndex();
                }

                setThreshold.accept(0.7D);
                break;
            case BLOOD_SPAWN: //normal mode
            case BLOOD_SPAWN_10821: //story mode
            case BLOOD_SPAWN_10829: //hard mode
                maidenSpawns.add(npc);
        }

        if (name != null && name.equalsIgnoreCase("Nylocas Matomenos") && maidenActive && maidenNPC != null) {
            crabTicksSinceSpawn = 8;
            nyloSpawnDelay = 2;
            String position = "??";
            boolean scuffed = false;
            int x = npc.getWorldLocation().getRegionX();
            int y = npc.getWorldLocation().getRegionY();

            if (x == 21 && y == 40) {
                position = "N1";
            }else if (x == 22 && y == 41) {
                position = "N1";
                scuffed = true;
            }else if (x == 25 && y == 40) {
                position = "N2";
            }else if (x == 26 && y == 41) {
                position = "N2";
                scuffed = true;
            }else if (x == 29 && y == 40) {
                position = "N3";
            }else if (x == 30 && y == 41) {
                position = "N3";
                scuffed = true;
            }else if (x == 33 && y == 40) {
                position = "N4";
            }else if (x == 34 && y == 41) {
                position = "N4";
                scuffed = true;
            }else if (x == 33 && y == 38) {
                position = "N4";
            }else if (x == 34 && y == 39) {
                position = "N4";
                scuffed = true;
            }else if (x == 21 && y == 20) {
                position = "S1";
            }else if (x == 22 && y == 19) {
                position = "S1";
                scuffed = true;
            }else if (x == 25 && y == 20) {
                position = "S2";
            }else if (x == 26 && y == 19) {
                position = "S2";
                scuffed = true;
            }else if (x == 29 && y == 20) {
                position = "S3";
            }else if (x == 30 && y == 19) {
                position = "S3";
                scuffed = true;
            }else if (x == 33 && y == 20) {
                position = "S4";
            }else if (x == 34 && y == 19) {
                position = "S4";
                scuffed = true;
            }else if (x == 33 && y == 22) {
                position = "S4";
            }else if (x == 34 && y == 20) {
                position = "S4";
                scuffed = true;
            }

            for(NPC n : client.getNpcs()) {
                if (n.getId() == 8361 || n.getId() == 10814 || n.getId() == 10823) {
                    maidenPhase = 70;
                    break;
                } else if (n.getId() == 8362 || n.getId() == 10815 || n.getId() == 10824) {
                    maidenPhase = 50;
                    break;
                } else if (n.getId() == 8363 || n.getId() == 10816 || n.getId() == 10825) {
                    maidenPhase = 30;
                    break;
                }
            }
            maidenCrabInfoList.add(new MaidenCrabInfo(npc, maidenPhase, position, -1, -1, -1, scuffed));
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        switch(npc.getId()) {
            case THE_MAIDEN_OF_SUGADINTI: //normal mode
            case THE_MAIDEN_OF_SUGADINTI_8361:
            case THE_MAIDEN_OF_SUGADINTI_8362:
            case THE_MAIDEN_OF_SUGADINTI_8363:
            case THE_MAIDEN_OF_SUGADINTI_8364:
            case THE_MAIDEN_OF_SUGADINTI_8365:
            case THE_MAIDEN_OF_SUGADINTI_10814: //story mode
            case THE_MAIDEN_OF_SUGADINTI_10815:
            case THE_MAIDEN_OF_SUGADINTI_10816:
            case THE_MAIDEN_OF_SUGADINTI_10817:
            case THE_MAIDEN_OF_SUGADINTI_10818:
            case THE_MAIDEN_OF_SUGADINTI_10819:
            case THE_MAIDEN_OF_SUGADINTI_10822: //hard mode
            case THE_MAIDEN_OF_SUGADINTI_10823:
            case THE_MAIDEN_OF_SUGADINTI_10824:
            case THE_MAIDEN_OF_SUGADINTI_10825:
            case THE_MAIDEN_OF_SUGADINTI_10826:
            case THE_MAIDEN_OF_SUGADINTI_10827:
                ticksUntilAttack = 0;
                maidenAttSpd = 10;
                maidenActive = false;
                maidenSpawns.clear();
                maidenNPC = null;
                maidenPhase = 70;
                newMaidenHp = -1;
                newMaidenThresholdHp = -1;
                timesMaidenHealed = 0;
                amountMaidenHealed = 0;
                realMaidenHp = -1;
                thresholdHp = -1;
                maxHit = 36.5D;
                break;
            case BLOOD_SPAWN:  //normal mode
            case BLOOD_SPAWN_10821: //story mode
            case BLOOD_SPAWN_10829: //hard mode
                maidenSpawns.remove(npc);
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        if(maidenActive && maidenNPC != null) {
            NPC npc = event.getNpc();
            int id = npc.getId();
            switch (id) {
                case THE_MAIDEN_OF_SUGADINTI: //normal mode
                case THE_MAIDEN_OF_SUGADINTI_8361:
                case THE_MAIDEN_OF_SUGADINTI_8362:
                case THE_MAIDEN_OF_SUGADINTI_8363:
                case THE_MAIDEN_OF_SUGADINTI_8364:
                case THE_MAIDEN_OF_SUGADINTI_8365:
                case THE_MAIDEN_OF_SUGADINTI_10814: //story mode
                case THE_MAIDEN_OF_SUGADINTI_10815:
                case THE_MAIDEN_OF_SUGADINTI_10816:
                case THE_MAIDEN_OF_SUGADINTI_10817:
                case THE_MAIDEN_OF_SUGADINTI_10818:
                case THE_MAIDEN_OF_SUGADINTI_10819:
                case THE_MAIDEN_OF_SUGADINTI_10822: //hard mode
                case THE_MAIDEN_OF_SUGADINTI_10823:
                case THE_MAIDEN_OF_SUGADINTI_10824:
                case THE_MAIDEN_OF_SUGADINTI_10825:
                case THE_MAIDEN_OF_SUGADINTI_10826:
                case THE_MAIDEN_OF_SUGADINTI_10827:
                    if (id == THE_MAIDEN_OF_SUGADINTI_8361 || id == THE_MAIDEN_OF_SUGADINTI_10815 || id == THE_MAIDEN_OF_SUGADINTI_10823) {
                        maidenPhase = 70;
                    } else if (id == THE_MAIDEN_OF_SUGADINTI_8362 || id == THE_MAIDEN_OF_SUGADINTI_10816 || id == THE_MAIDEN_OF_SUGADINTI_10824) {
                        maidenPhase = 50;
                    } else if (id == THE_MAIDEN_OF_SUGADINTI_8363 || id == THE_MAIDEN_OF_SUGADINTI_10817 || id == THE_MAIDEN_OF_SUGADINTI_10825) {
                        maidenPhase = 30;
                    }
            }

            if (npc.getId() == THE_MAIDEN_OF_SUGADINTI_8361) {
                setThreshold.accept(0.5D);
            }
            if (npc.getId() == THE_MAIDEN_OF_SUGADINTI_8362) {
                setThreshold.accept(0.3D);
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if(maidenActive && maidenNPC != null) {
            MenuEntry mbEntry = event.getMenuEntry();
            int type = event.getType();
            if (type >= 2000) {
                type -= 2000;
            }

            MenuAction menuAction = MenuAction.of(type);
            if (NPC_MENU_ACTIONS.contains(menuAction) && event.getTarget().contains("Matomenos") && maidenCrabInfoList.size() > 0) {
                NPC npc = client.getCachedNPCs()[event.getIdentifier()];
                for (MaidenCrabInfo mci : maidenCrabInfoList){
                    if (mci.crab == npc){
                        double crabHealthPcent = ((double) mci.hpRatio / (double) mci.hpScale) * 100.0D;
                        if (config.maidenRecolourNylos()) {
                            Color color;
                            MenuEntry[] menuEntries = client.getMenuEntries();
                            MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
                            if (config.oldHpThreshold()) {
                                color = plugin.oldHitpointsColor(crabHealthPcent);
                            } else {
                                color = plugin.calculateHitpointsColor(crabHealthPcent);
                            }
                            String crabHp = Double.toString(crabHealthPcent);
                            if (crabHp.contains(".")) {
                                crabHp = crabHp.substring(0, crabHp.indexOf(".") + 2);
                            }
                            String target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget() + " - " + crabHp + "%"), color);
                            menuEntry.setTarget(target);
                            client.setMenuEntries(menuEntries);
                        }
                        break;
                    }
                }
            }

            if (event.getTarget().contains("Blood spawn") && event.getType() == MenuAction.NPC_SECOND_OPTION.getId()
                    && (config.removeMaidenBloods() == SpoonTobConfig.maidenBloodsMode.ATTACK || config.removeMaidenBloods() == SpoonTobConfig.maidenBloodsMode.BOTH)) {
                NPC npc = client.getCachedNPCs()[event.getIdentifier()];
                if (npc != null) {
                    mbEntry.setDeprioritized(true);
                }
            } else if (event.getTarget().contains("Blood spawn") && event.getTarget().contains("Ice B") && event.getType() == MenuAction.WIDGET_TARGET_ON_NPC.getId()
                    && (config.removeMaidenBloods() == SpoonTobConfig.maidenBloodsMode.CAST || config.removeMaidenBloods() == SpoonTobConfig.maidenBloodsMode.BOTH)) {
                NPC npc = client.getCachedNPCs()[event.getIdentifier()];
                if (npc != null) {
                    mbEntry.setDeprioritized(true);
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Random rand = new Random();
        float r = rand.nextFloat() / 2.0F;
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        c = new Color(r, g, b);
        if (maidenActive) {
            if (maidenNPC != null) {
                --ticksUntilAttack;
            }

            if(crabTicksSinceSpawn > 0) {
                crabTicksSinceSpawn--;
            }

            Iterator<NPC> it = frozenBloodSpawns.keySet().iterator();
            while(it.hasNext()) {
                NPC npc = it.next();
                frozenBloodSpawns.replace(npc, frozenBloodSpawns.get(npc) - 1);
                if (frozenBloodSpawns.get(npc) < -5) {
                    it.remove();
                }
            }
            maidenBloodSplatters.clear();
            maidenBloodSplattersColors.clear();
            for(GraphicsObject obj : client.getGraphicsObjects()){
                if(obj.getId() == BLOOD_SPLAT_GRAPHIC) {
                    maidenBloodSplatters.add(WorldPoint.fromLocal(client, obj.getLocation()));
                    maidenBloodSplattersColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
                }
            }
            maidenBloodSpawnTrailingLocations.clear();
            maidenBloodSpawnTrailingLocations.addAll(maidenBloodSpawnLocations);
            maidenBloodSpawnLocations.clear();
            maidenSpawns.forEach((s) -> maidenBloodSpawnLocations.add(s.getWorldLocation()));

            if (maidenCrabInfoList.size() > 0) {
                if (nyloSpawnDelay == 0) {
                    for (MaidenCrabInfo mci : maidenCrabInfoList) {
                        if (mci.frozenTicks != -1) {
                            mci.frozenTicks--;
                        }
                    }
                } else {
                    nyloSpawnDelay--;
                }
            }

            if(maidenBloodSplatterProj.size() > 0){
                for(int i=maidenBloodSplatterProj.size()-1; i>=0; i--){
                    if(maidenBloodSplatterProj.get(i).projectile.getRemainingCycles() / 30 <= 0){
                        maidenBloodSplatterProj.remove(i);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onClientTick (ClientTick event)
    {

        if (maidenNPC != null && maidenActive)
        {
            int highestHP = 0;
            int npcIndex = 0;
            MenuEntry highestHpEntry = null;

            //update HP's
            maidenCrabInfoList.forEach(mci ->
            {
                if (maidenCrabInfoList.size() > 0 && nyloSpawnDelay == 0 && mci.crab.getHealthRatio() >= 0)
                {
                    mci.hpRatio = mci.crab.getHealthRatio();
                    mci.hpScale = mci.crab.getHealthScale();
                }
            });

            //gather the attack and cast entries for the red crabs you are currently hovering
            MenuEntry[] npcEntries = Arrays.stream(client.getMenuEntries())
                    .filter(menuEntry ->
                            menuEntry.getTarget().contains("Nylocas Matomenos") && (menuEntry.getOption().contains("Attack") || menuEntry.getOption().contains("Cast")))
                    .toArray(MenuEntry[]::new);

            if (npcEntries.length > 1 && config.maidenCrabHpPriority())
            {

                //compile a list of crabs which your cursor is hovering over.
                List<MaidenCrabInfo> clickableCrabs = maidenCrabInfoList.stream()
                        .filter(mci -> Arrays.stream(npcEntries)
                                .anyMatch(menuEntry -> menuEntry.getIdentifier() == mci.crab.getIndex()))
                        .collect(Collectors.toList());

                //calc the highest HP
                for (MaidenCrabInfo mci : clickableCrabs)
                {
                    if (mci.hpRatio > highestHP || mci.hpRatio == -1)
                    {
                        highestHP = mci.hpRatio;
                        npcIndex = mci.crab.getIndex();
                    }
                }

                //sort for highest hpEntry
                for (MenuEntry menuEntry : npcEntries)
                {
                    if (menuEntry.getIdentifier() == npcIndex)
                    {
                        highestHpEntry = menuEntry;
                    }
                }

                //rebuild and set all other entries prioritised
                if (highestHpEntry != null)
                {
                    MenuEntry[] newEntries = client.getMenuEntries();
                    int index = Arrays.asList(client.getMenuEntries()).indexOf(highestHpEntry);
                    for (int i = 0; i < newEntries.length; i++)
                    {
                        if (i != index)
                        {
                            newEntries[i] = newEntries[i].setDeprioritized(true);
                        }
                    }
                    //set the new entries
                    client.setMenuEntries(newEntries);
                }
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();
        if(actor.getName() != null && maidenNPC != null && maidenActive){
            if (actor.getName().equals("Nylocas Matomenos") && actor.getAnimation() == 8097) {
                NPC npc = (NPC) actor;
                for(int i=maidenCrabInfoList.size()-1; i>=0; i--){
                    MaidenCrabInfo mci = maidenCrabInfoList.get(i);
                    if(npc == mci.crab){
                        NPCComposition nComp = maidenNPC.getComposition();
                        int distance = npc.getWorldLocation().getX() - (maidenNPC.getWorldLocation().getX() + nComp.getSize());
                        if((distance == -1 || distance == 0)  && (npc.getHealthRatio() > 0 || npc.getHealthRatio() == -1)){
                            double crabHealthPcent = ((double) mci.hpRatio / (double) mci.hpScale) * 100.0D;
                            String crabHp = String.valueOf(crabHealthPcent);
                            crabHp = crabHp.substring(0, crabHp.indexOf(".") + 2) + "%";

                            if(config.leakedMessage()) {
                                String msg = "[<col=ff0000>" + mci.phase + "s</col>] The <col=ff0000>" + mci.position + "</col> crab leaked with <col=ff0000>" + crabHp;
                                client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", msg, "");
                            }
                            if (distance == 0) {
                                updateMaidenMaxHit();
                            }
                        }
                        maidenCrabInfoList.remove(i);
                        break;
                    }
                }
            }else if(actor.getName().equals("The Maiden of Sugadinti")) {
                if(actor.getAnimation() == 8091 || actor.getAnimation() == 8092) {
                    if(ticksUntilAttack > 1 && maidenNPC.getId() >= 10822){
                        maidenAttSpd -= (ticksUntilAttack - 1);
                        if(maidenAttSpd < 3){
                            maidenAttSpd = 3;
                        }
                    }
                    ticksUntilAttack = maidenAttSpd + 1;
                }
            }
        }
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged event) {
        if (maidenActive && maidenNPC != null && event.getActor() instanceof NPC) {
            NPC npc = (NPC) event.getActor();
            int ticks = 0;
            if (npc.getGraphic() == GraphicID.ICE_BARRAGE) {
                ticks = 33;
            } else if (npc.getGraphic() == GraphicID.ICE_BLITZ) {
                ticks = 25;
            } else if (npc.getGraphic() == GraphicID.ICE_BURST) {
                ticks = 16;
            } else if (npc.getGraphic() == GraphicID.ICE_RUSH) {
                ticks = 8;
            } else if (npc.getGraphic() == GraphicID.ENTANGLE) {
                ticks = 24;
            } else if (npc.getGraphic() == GraphicID.SNARE) {
                ticks = 16;
            } else if (npc.getGraphic() == GraphicID.BIND) {
                ticks = 8;
            }

            if (npc.getName() != null && ticks > 0) {
                if (config.bloodSpawnFreezeTimer() && npc.getName().equalsIgnoreCase("blood spawn")) {
                    if (!frozenBloodSpawns.containsKey(npc)) {
                        frozenBloodSpawns.put(npc, ticks);
                    }
                } else if (npc.getName().equalsIgnoreCase("nylocas matomenos")) {
                    for (MaidenCrabInfo mci : maidenCrabInfoList) {
                        if (mci.crab == npc && mci.frozenTicks == -1) {
                            mci.frozenTicks = ticks;
                            break;
                        }
                    }
                }
            }
        }
    }



    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if(event.getActor() instanceof NPC && frozenBloodSpawns.containsKey((NPC)event.getActor())){
            frozenBloodSpawns.remove((NPC)event.getActor());
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        if(event.getProjectile().getId() == BLOOD_SPLAT_PROJECTILE){
            maidenBloodSplatterProj.add(new MaidenBloodSplatInfo(event.getProjectile(), event.getPosition()));
        }
    }

    Color maidenSpecialWarningColor() {
        Color col = Color.GREEN;
        if (maidenNPC == null || maidenNPC.getInteracting() == null ||
                maidenNPC.getInteracting().getName() == null || client.getLocalPlayer() == null) {
            return col;
        }

        if (maidenNPC.getInteracting().getName().equals(client.getLocalPlayer().getName())) {
            return Color.ORANGE;
        }

        return col;
    }

    private short getMaidenBaseHpIndex()
    {
        switch(SpoonTobPlugin.partySize)
        {
            case 4:
                return 3062;
            case 5:
                return 3500;
            default:
                return 2625;
        }
    }
}