package net.runelite.client.plugins.spoontob.rooms.Xarpus;

import com.google.common.collect.ImmutableSet;
import net.runelite.client.plugins.spoontob.Room;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.AlternateSprites;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl.Type;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.util.List;
import java.util.*;

import static net.runelite.api.NpcID.*;

public class Xarpus extends Room {
    private static BufferedImage EXHUMED_COUNT_ICON;
    private static final int GROUNDOBJECT_ID_EXHUMED = 32743;
    protected static final Set<Integer> P0_IDS = ImmutableSet.of(
            XARPUS, XARPUS_10766, XARPUS_10770
    );
    protected static final Set<Integer> P1_IDS = ImmutableSet.of(
            XARPUS_8339, XARPUS_10767, XARPUS_10771
    );
    protected static final Set<Integer> P2_IDS = ImmutableSet.of(
            XARPUS_8340, XARPUS_10768, XARPUS_10772
    );
    protected static final Set<Integer> P3_IDS = ImmutableSet.of(
            XARPUS_8341, XARPUS_10769, XARPUS_10773
    );
    @Inject
    private XarpusOverlay xarpusOverlay;
    @Inject
    private XarpusCounterPanel xarpusPanel;
    @Inject
    private XarpusTimer xarpusTimer;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private Client client;

    int exhumedCount = 0;
    int healCount = 0;

    private boolean xarpusStarted = false;

	private static Clip clip;

    @Getter
    private ExhumedInfobox exhumedCounter;
    private static BufferedImage HEALED_COUNT_ICON;
    @Getter
    private Counter xarpusHealedCounter;

    @Getter
    private boolean xarpusActive;
    @Getter
    public boolean xarpusStare;
    @Getter
    private final Map<Long, Pair<GroundObject, Integer>> xarpusExhumeds = new HashMap<>();
    @Getter
    private int xarpusTicksUntilAttack;
    @Getter
    private NPC xarpusNPC;

    @Getter
    private boolean exhumedSpawned = false;

    @Getter
    private int instanceTimer = 0;
    @Getter
    private boolean isInstanceTimerRunning = false;
    private boolean nextInstance = true;

    @Getter
    private boolean isHM = false;
    @Getter
    private boolean isP3Active = false;

    @Inject
    protected Xarpus(SpoonTobPlugin plugin, SpoonTobConfig config) {
        super(plugin, config);
    }

    public void init() {
		try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonTobPlugin.class.getResourceAsStream("sheesh.wav")));
            AudioFormat format = stream.getFormat();
            Info info = new Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            FloatControl control = (FloatControl)clip.getControl(Type.MASTER_GAIN);
            if (control != null) {
                control.setValue((float)(config.sheeshVolume() / 2 - 45));
            }
        } catch (Exception var6) {
            clip = null;
        }
        EXHUMED_COUNT_ICON = ImageUtil.resizeCanvas(ImageUtil.loadImageResource(AlternateSprites.class, AlternateSprites.POISON_HEART), 26, 26);
        HEALED_COUNT_ICON = ImageUtil.resizeCanvas(ImageUtil.loadImageResource(SpoonTobPlugin.class, "healsplat.png"), 26, 26);
    }

    public void load() {
        overlayManager.add(xarpusOverlay);
        overlayManager.add(xarpusTimer);
        overlayManager.add(xarpusPanel);
    }

    public void unload() {
        overlayManager.remove(xarpusOverlay);
        overlayManager.remove(xarpusPanel);
        overlayManager.remove(xarpusTimer);
        xarpusStarted = false;
        healCount = 0;

        infoBoxManager.removeInfoBox(exhumedCounter);
        exhumedCounter = null;

        infoBoxManager.removeInfoBox(xarpusHealedCounter);
        xarpusHealedCounter = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("spoontob")) {
            if (event.getKey().equals("sheeshVolume")) {
                if(clip != null) {
                    FloatControl control = (FloatControl)clip.getControl(Type.MASTER_GAIN);
			        if (control != null) {
				        control.setValue((float)(config.sheeshVolume() / 2 - 45));
                    }
                }
            }
            if (event.getKey().equals("exhumedIB")) {
                if (config.exhumedIB()) {
                    infoBoxManager.addInfoBox(exhumedCounter);
                } else {
                    infoBoxManager.removeInfoBox(exhumedCounter);
                }
            }

            if (event.getKey().equals("xarpusHealingCount")) {
                if (config.xarpusHealingCount()) {
                    infoBoxManager.addInfoBox(xarpusHealedCounter);
                } else {
                    infoBoxManager.removeInfoBox(xarpusHealedCounter);
                }
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        isHM = false;
        isP3Active = false;
        NPC npc = npcSpawned.getNpc();
        switch (npc.getId())
        {
            case XARPUS_10770:
            case XARPUS_10771:
            case XARPUS_10772:
            case XARPUS_10773:
                isHM = true;
            case XARPUS:
            case XARPUS_8339:
            case XARPUS_8340:
            case XARPUS_8341:
            case XARPUS_10766:
            case XARPUS_10767:
            case XARPUS_10768:
            case XARPUS_10769:
                xarpusActive = true;
                xarpusNPC = npc;
                xarpusStare = false;
                xarpusTicksUntilAttack = 9;
                healCount = 0;
                exhumedSpawned = false;
                break;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        switch(npc.getId()) {
            case XARPUS:
            case XARPUS_8339:
            case XARPUS_8340:
            case XARPUS_8341:
            case XARPUS_10766: //Story mode
            case XARPUS_10767:
            case XARPUS_10768:
            case XARPUS_10769:
            case XARPUS_10770: //Hard mode
            case XARPUS_10771:
            case XARPUS_10772:
            case XARPUS_10773:
                xarpusActive = false;
                xarpusNPC = null;
                xarpusStare = false;
                xarpusTicksUntilAttack = 9;
                xarpusExhumeds.clear();
                xarpusStarted = false;
                isInstanceTimerRunning = false;
                healCount = 0;
                exhumedSpawned = false;
                infoBoxManager.removeInfoBox(exhumedCounter);
                exhumedCounter = null;
                exhumedCount = -1;
                removeCounter();
                break;
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        if (xarpusActive) {
            if (P2_IDS.contains(npc.getId()) || P3_IDS.contains(npc.getId())) {
                infoBoxManager.removeInfoBox(exhumedCounter);
                exhumedCounter = null;
            }
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        if (xarpusActive)
        {
            GroundObject o = event.getGroundObject();
            if (o.getId() == GROUNDOBJECT_ID_EXHUMED)
            {
                long hash = o.getHash();
                if (xarpusExhumeds.containsKey(hash))
                {
                    return;
                }
                exhumedSpawned = true;

                if (exhumedCounter == null)
                {
                    switch (SpoonTobPlugin.partySize)
                    {
                        case 5:
                            exhumedCount = isHM ? 24 : 18;
                            break;
                        case 4:
                            exhumedCount = isHM ? 20 : 15;
                            break;
                        case 3:
                            exhumedCount = isHM ? 16 : 12;
                            break;
                        case 2:
                            exhumedCount = isHM ? 13 : 9;
                            break;
                        default:
                            exhumedCount = isHM ? 9 : 7;
                    }

                    exhumedCounter = new ExhumedInfobox(EXHUMED_COUNT_ICON, plugin, exhumedCount - 1);
                    if (config.exhumedIB())
                    {
                        infoBoxManager.addInfoBox(exhumedCounter);
                        exhumedCounter.setTooltip(ColorUtil.wrapWithColorTag(exhumedCounter.getCount() > 0 ? "Exhumeds Left: "
                                + exhumedCounter.getCount() : "NOW", exhumedCounter.getCount() <= 1 ? Color.RED : Color.WHITE));
                    }
                }
                else
                {

                    exhumedCounter.setCount(exhumedCounter.getCount() - 1);
                    exhumedCounter.setTooltip(ColorUtil.wrapWithColorTag(exhumedCounter.getCount() > 0 ? "Exhumeds Left: "
                            + exhumedCounter.getCount() : "NOW", exhumedCounter.getCount() <= 1 ? Color.RED : Color.WHITE));
                }

                xarpusExhumeds.put(hash, Pair.of(o, isHM ? 9 : 11));
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (xarpusActive) {
            if (!xarpusExhumeds.isEmpty())
            {
                xarpusExhumeds.replaceAll((k, v) -> Pair.of(v.getLeft(), v.getRight() - 1));
                xarpusExhumeds.values().removeIf((p) -> p.getRight() <= 0);
            }

            if (xarpusNPC.getOverheadText() != null && !xarpusStare) {
                xarpusStare = true;
                xarpusTicksUntilAttack = 9;
            }

            if (xarpusStare) {
                xarpusTicksUntilAttack--;
                if (xarpusTicksUntilAttack <= 0) {
                    xarpusTicksUntilAttack = 8;
                    isP3Active = true;
                }

                infoBoxManager.removeInfoBox(exhumedCounter);

            } else if (P2_IDS.contains(xarpusNPC.getId())) {
                xarpusTicksUntilAttack--;

                if (xarpusTicksUntilAttack <= 0)
                {
                    xarpusTicksUntilAttack = 4;
                }
            }
        }

        instanceTimer = (instanceTimer + 1) % 4;
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if (event.getActor() != null && event.getActor().getName() != null) {
            if (event.getActor().getName().toLowerCase().contains("xarpus") && event.getHitsplat().getHitsplatType() == HitsplatID.HEAL) {
                healCount += event.getHitsplat().getAmount();
                addCounter();
                updateCounter();
            }

        }
    }

    private void updateCounter()
    {
        if (xarpusHealedCounter != null)
        {
            xarpusHealedCounter.setCount(healCount);
        }
    }

    private void addCounter()
    {
        if (config.xarpusHealingCount() && xarpusHealedCounter == null)
        {
            xarpusHealedCounter = new Counter(HEALED_COUNT_ICON, plugin, healCount);
            xarpusHealedCounter.setTooltip("Xarpus Heals");
            infoBoxManager.addInfoBox(xarpusHealedCounter);
        }
    }

    private void removeCounter()
    {
        if (xarpusHealedCounter != null)
        {
            infoBoxManager.removeInfoBox(xarpusHealedCounter);
            healCount = 0;
            xarpusHealedCounter = null;
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if ((client.getVarbitValue(Varbits.MULTICOMBAT_AREA) == 1 || client.getVarbitValue(client.getVarps(), 6447) == 2) && !xarpusStarted && isInstanceTimerRunning) {
            isInstanceTimerRunning = false;
            xarpusStarted = true;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            nextInstance = true;
        }
    }

    @Subscribe
    protected void onClientTick(ClientTick event) {
        if (client.getLocalPlayer() == null)
        {
            return;
        }
        List<Player> players = client.getPlayers();
        for (Player player : players)
        {
            if (player.getWorldLocation() != null)
            {
                WorldPoint wpPlayer = player.getWorldLocation();
                LocalPoint lpPlayer = LocalPoint.fromWorld(client, wpPlayer.getX(), wpPlayer.getY());

                if (lpPlayer == null)
                {
                    continue;
                }
                WorldPoint wpChest = WorldPoint.fromRegion(player.getWorldLocation().getRegionID(),17,5, player.getWorldLocation().getPlane());
                LocalPoint lpChest = LocalPoint.fromWorld(client, wpChest.getX(), wpChest.getY());
                if (lpChest != null)
                {
                    Point point = new Point(lpChest.getSceneX() - lpPlayer.getSceneX(), lpChest.getSceneY() - lpPlayer.getSceneY());

                    if (isInSotetsegRegion() && point.getY() == 1 && (point.getX() == 1 || point.getX() == 2 || point.getX() == 3) && nextInstance)
                    {
                        client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Xarpus instance timer started", "", false);
                        instanceTimer = 2;
                        isInstanceTimerRunning = true;
                        nextInstance = false;
                    }
                }
            }
        }
    }

    @Subscribe
    public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event) {
		if(xarpusActive && xarpusNPC != null){
			if(event.getSoundId() == 4005 && (getXarpusNPC().getId() >= 10770 && getXarpusNPC().getId() <= 10773) && config.muteXarpusHmEarrape()){
				event.consume();
			}else if(event.getSoundId() == 4007 && config.sheesh()){
				event.consume();
			}
		}
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event) {
        if(event.getActor() instanceof NPC && config.sheesh() && xarpusActive){
            NPC npc = (NPC) event.getActor();
            if(npc.getId() == xarpusNPC.getId()) {
                event.getActor().setOverheadText("Sheeeeeesh!");
				clip.setFramePosition(0);
                clip.start();
            }
        }
    }

    public boolean isInXarpusRegion() {
        return client.getMapRegions() != null && client.getMapRegions().length > 0 && Arrays.stream(client.getMapRegions()).anyMatch((s) -> s == 12612);
    }

    protected boolean isInSotetsegRegion() {
        return client.getMapRegions() != null && client.getMapRegions().length > 0 && Arrays.stream(client.getMapRegions()).anyMatch((s) -> s == 13123 || s == 13379);
    }

    @ToString
    public static class ExhumedInfobox extends InfoBox {
        @Getter
        @Setter
        private int count;

        public ExhumedInfobox(BufferedImage image, Plugin plugin, int count) {
            super(image, plugin);
            this.count = count;
        }

        @Override
        public String getText() {
            return Integer.toString(getCount());
        }

        @Override
        public Color getTextColor() {
            if (count <= 1) {
                return Color.RED;
            } else {
                return Color.WHITE;
            }
        }
    }
}