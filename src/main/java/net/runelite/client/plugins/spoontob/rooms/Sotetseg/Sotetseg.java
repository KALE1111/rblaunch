package net.runelite.client.plugins.spoontob.rooms.Sotetseg;

import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.plugins.zulrahoverlay.ZulrahPlugin;
import net.runelite.client.util.Text;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.spoontob.Room;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl.Type;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.util.*;

import static net.runelite.api.NpcID.*;

public class Sotetseg extends Room {
    private static final Logger log = LoggerFactory.getLogger(Sotetseg.class);
    @Inject
    private Client client;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private SotetsegOverlay sotetsegOverlay;
    @Inject
    private DeathBallPanel deathBallPanel;
    @Inject
    private SkillIconManager iconManager;

    static final int SOTETSEG_MAGE_ORB = 1606;
    static final int SOTETSEG_RANGE_ORB = 1607;
    static final int SOTETSEG_BIG_AOE_ORB = 1604;

    private static final int GROUNDOBJECT_ID_REDMAZE = 33035;
    private static final int GROUNDOBJECT_ID_BLACKMAZE = 33034;
    private static final int GROUNDOBJECT_ID_GREYMAZE = 33033;

    private static final int OVERWORLD_REGION_ID = 13123;
    private static final int UNDERWORLD_REGION_ID = 13379;
    @Getter
    private static final Point swMazeSquareOverWorld = new Point(9, 22);
    @Getter
    private static final Point swMazeSquareUnderWorld = new Point(42, 31);

    private boolean bigOrbPresent = false;
    private static Clip clip;
    static BufferedImage TACTICAL_NUKE_OVERHEAD;
    private static BufferedImage TACTICAL_NUKE_SHEET;
    private static BufferedImage TACTICAL_NUKE_SHEET_BLANK;
    private String currentTopic = null;

    @Getter
    private boolean sotetsegActive;
    public NPC sotetsegNPC;

    private int overWorldRegionID = -1;
    @Getter
    private boolean wasInUnderWorld = false;
    @Getter
    private LinkedHashSet<Point> redTiles = new LinkedHashSet();
    @Getter
    private HashSet<Point> greenTiles = new HashSet();

    @Getter
    public byte sotetsegTicks = -1;
    public boolean ballOutNigga = false;
    public int turboHatWidth = 0;
    public int turboHatHeight = 0;

    public int sotetsegAttacksLeft = 10;

    private boolean offTick = false;

    public int mageHatNum = 0;
    public int rangeHatNum = 0;
    public BufferedImage mageIcon;
    public BufferedImage rangeIcon;

    @Inject
    protected Sotetseg(SpoonTobPlugin plugin, SpoonTobConfig config) {
        super(plugin, config);
    }

    public void init() {
        TACTICAL_NUKE_SHEET = ImageUtil.loadImageResource(SpoonTobPlugin.class, "nuke_spritesheet.png");
        TACTICAL_NUKE_OVERHEAD = ImageUtil.loadImageResource(SpoonTobPlugin.class, "Tactical_Nuke_Care_Package_Icon_MW2.png");
        TACTICAL_NUKE_SHEET_BLANK = new BufferedImage(TACTICAL_NUKE_SHEET.getWidth(), TACTICAL_NUKE_SHEET.getHeight(), TACTICAL_NUKE_SHEET.getType());
        Graphics2D graphics = TACTICAL_NUKE_SHEET_BLANK.createGraphics();
        graphics.setColor(new Color(0, 0, 0, 0));
        graphics.fillRect(0, 0, TACTICAL_NUKE_SHEET.getWidth(), TACTICAL_NUKE_SHEET.getHeight());
        graphics.dispose();

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonTobPlugin.class.getResourceAsStream("mw2_tactical_nuke.wav")));
            AudioFormat format = stream.getFormat();
            Info info = new Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            FloatControl control = (FloatControl)clip.getControl(Type.MASTER_GAIN);
            if (control != null) {
                control.setValue(20.0F * (float)Math.log10((float)config.sotetsegAttacksSoundVolume() / 100.0F));
            }
        } catch (Exception var6) {
            clip = null;
        }

    }

    public void load() {
        overlayManager.add(sotetsegOverlay);
        overlayManager.add(deathBallPanel);
        loadImages(config.soteHatSize());
    }

    public void unload() {
        overlayManager.remove(sotetsegOverlay);
        overlayManager.remove(deathBallPanel);
    }

    private void loadImages(int imageSize) {
        mageIcon = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.MAGIC, true), imageSize, imageSize);
        rangeIcon = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.RANGED, true), imageSize, imageSize);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged change) {
        if (change.getGroup().equals("spoontob")) {
            FloatControl control;
            if (change.getKey().equals("SotetsegAttacksSoundsVolume") && clip != null && (control = (FloatControl) clip.getControl(Type.MASTER_GAIN)) != null) {
                control.setValue(20.0F * (float) Math.log10((double) ((float) config.sotetsegAttacksSoundVolume() / 100.0F)));
            } else if (change.getKey().equals("soteHatSize")) {
                loadImages(config.soteHatSize());
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        switch(npc.getId()) {
            case SOTETSEG:
            case SOTETSEG_8388:
            case SOTETSEG_10864: //Story mode
            case SOTETSEG_10865:
            case SOTETSEG_10867: //Hard Mode
            case SOTETSEG_10868:
                sotetsegNPC = npc;
                if(!sotetsegActive) {
                    sotetsegActive = true;
                    sotetsegAttacksLeft = 10;
                }
                break;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        switch(npc.getId()) {
            case SOTETSEG:
            case SOTETSEG_8388:
            case SOTETSEG_10864: //Story mode
            case SOTETSEG_10865:
            case SOTETSEG_10867: //Hard Mode
            case SOTETSEG_10868:
                if (client.getPlane() != 3) {
                    sotetsegActive = false;
                    sotetsegNPC = null;
                    sotetsegTicks = -1;
                }
                if (npc.isDead()){
                    sotetsegAttacksLeft = 10;
                }
                break;
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved e)
    {
        if (sotetsegActive) {
            Projectile p = e.getProjectile();
            if (client.getGameCycle() < p.getStartCycle()) {
                switch (p.getId())
                {
                    case SOTETSEG_BIG_AOE_ORB:
                        sotetsegTicks = 11;
                        sotetsegAttacksLeft = 10;
                        break;
                    case SOTETSEG_MAGE_ORB:
                        WorldPoint soteWp = WorldPoint.fromLocal(client, sotetsegNPC.getLocalLocation());
                        WorldPoint projWp = WorldPoint.fromLocal(client, p.getX1(), p.getY1(), client.getPlane());
                        if (sotetsegNPC.getAnimation() == 8139 && projWp.equals(soteWp)) {
                            sotetsegAttacksLeft--;
                        }
                }
            }
        }
    }


    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();
        if (actor instanceof NPC) {
            if (actor == sotetsegNPC) {
                int animation = event.getActor().getAnimation();
                if (animation == 8138 || animation == 8139) {
                    sotetsegTicks = 6;
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (sotetsegActive) {
            int rng = new Random().nextInt(8) + 1;
            if(mageHatNum == rng){
                if(mageHatNum == 8){
                    mageHatNum = 7;
                }else {
                    mageHatNum++;
                }
            }else{
                mageHatNum = rng;
            }

            if(rangeHatNum == rng){
                if(rangeHatNum == 8){
                    rangeHatNum = 7;
                }else {
                    rangeHatNum++;
                }
            }else{
                rangeHatNum = rng;
            }
            turboHatWidth = new Random().nextInt(35) + 10;
            turboHatHeight = new Random().nextInt(25) + 10;

            if (sotetsegTicks >= 0) {
                --sotetsegTicks;
            }

            if (sotetsegNPC != null && (sotetsegNPC.getId() == 8388 || sotetsegNPC.getId() == 10865 || sotetsegNPC.getId() == 10868)) {
                if (!redTiles.isEmpty())
                {
                    redTiles.clear();
                    offTick = false;
                }

                if (!greenTiles.isEmpty())
                {
                    greenTiles.clear();
                }

                if (isInOverWorld())
                {
                    wasInUnderWorld = false;
                    if (client.getLocalPlayer() != null && client.getLocalPlayer().getWorldLocation() != null)
                    {
                        overWorldRegionID = client.getLocalPlayer().getWorldLocation().getRegionID();
                    }
                }
            }

            if (config.sotetsegShowNuke() != SpoonTobConfig.soteDeathballOverlayMode.OFF)
            {
                boolean foundBigOrb = false;
                for (Projectile p : client.getProjectiles())
                {
                    if (p.getId() == SOTETSEG_BIG_AOE_ORB)
                    {
                        foundBigOrb = true;
                        if (!bigOrbPresent && clip != null && config.sotetsegAttacksSound()) {
                            clip.setFramePosition(0);
                            clip.start();
                        }
                        break;
                    }
                }
                bigOrbPresent = foundBigOrb;
            }

            if (!bigOrbPresent)
            {
                ballOutNigga = false;
            }

            if (bigOrbPresent && !ballOutNigga)
            {
                sotetsegTicks = 10;
                ballOutNigga = true;
            }
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        if (sotetsegActive) {
            GroundObject o = event.getGroundObject();

            if (o.getId() == GROUNDOBJECT_ID_REDMAZE) {
                Tile t = event.getTile();
                WorldPoint p = WorldPoint.fromLocal(client, t.getLocalLocation());
                Point point = new Point(p.getRegionX(), p.getRegionY());
                if (isInOverWorld()) {
                    redTiles.add(new Point(point.getX() - swMazeSquareOverWorld.getX(), point.getY() - swMazeSquareOverWorld.getY()));
                }
                if (isInUnderWorld()) {
                    redTiles.add(new Point(point.getX() - swMazeSquareUnderWorld.getX(), point.getY() - swMazeSquareUnderWorld.getY()));
                    wasInUnderWorld = true;
                }
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (!isInOverWorld())
            return;
        if (client.getItemContainer(InventoryID.INVENTORY) == null)
            return;
        String target = Text.removeTags(event.getTarget()).toLowerCase();
        MenuEntry[] entries = client.getMenuEntries();
        if (((config.stamReq() == SpoonTobConfig.stamReqMode.XARPUS || config.stamReq() == SpoonTobConfig.stamReqMode.BOTH) && config.stamReq() != SpoonTobConfig.stamReqMode.OFF)
                && target.contains("formidable passage") && !client.getItemContainer(InventoryID.INVENTORY).contains(12625))
            client.setMenuEntries(Arrays.copyOf(entries, entries.length - 1));
    }

    WorldPoint worldPointFromMazePoint(Point mazePoint)
    {
        if (overWorldRegionID == -1 && client.getLocalPlayer() != null)
        {
            return WorldPoint.fromRegion(
                    client.getLocalPlayer().getWorldLocation().getRegionID(), mazePoint.getX() + Sotetseg.getSwMazeSquareOverWorld().getX(),
                    mazePoint.getY() + Sotetseg.getSwMazeSquareOverWorld().getY(), 0);
        }
        return WorldPoint.fromRegion(
                overWorldRegionID, mazePoint.getX() + Sotetseg.getSwMazeSquareOverWorld().getX(),
                mazePoint.getY() + Sotetseg.getSwMazeSquareOverWorld().getY(), 0);
    }


    private boolean isInOverWorld() {
        return client.getMapRegions().length > 0 && client.getMapRegions()[0] == OVERWORLD_REGION_ID;
    }

    private boolean isInUnderWorld() {
        return client.getMapRegions().length > 0 && client.getMapRegions()[0] == UNDERWORLD_REGION_ID;
    }
}
    