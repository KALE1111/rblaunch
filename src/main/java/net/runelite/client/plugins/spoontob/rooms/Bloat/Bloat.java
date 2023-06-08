package net.runelite.client.plugins.spoontob.rooms.Bloat;

import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.BloatDown;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.*;
import net.runelite.api.events.*;
import net.runelite.client.util.Text;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.spoontob.Room;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.plugins.spoontob.rooms.Bloat.stomp.def.BloatChunk;
import net.runelite.client.plugins.spoontob.util.TheatreRegions;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;

public class Bloat extends Room {
    private ClientThread clientThread;

    @Inject
    private BloatOverlay bloatOverlay;

    @Inject
    private Client client;

    protected static final int ROOM_STATE_VARBIT = 6447;
    protected static final Set<Integer> BLOAT_IDS = ImmutableSet.of(NpcID.PESTILENT_BLOAT, NpcID.PESTILENT_BLOAT_10812, NpcID.PESTILENT_BLOAT_10813);
    protected int lastVarp6447 = 0;
    public int bloatVar = 0;

    @Getter
    private boolean bloatActive;
    @Getter
    private NPC bloatNPC;
    @Getter
    private int bloatDownCount = 0;
    @Getter
    private int bloatUpTimer = 0;
    @Getter
    private int bloatState = 0;
    @Getter
    private BloatDown bloatDown = null;

    @Getter
    private final HashMap<WorldPoint, Integer> bloathands;

    public static final Set<Integer> topOfTankObjectIDs = ImmutableSet.of(32958, 32962, 32964, 32965, 33062);
    public static final Set<Integer> tankObjectIDs = ImmutableSet.of(32957, 32955, 32959, 32960, 32964, 33084);
    public static final Set<Integer> ceilingChainsObjectIDs = ImmutableSet.of(32949, 32950, 32951, 32952, 32953, 32954, 32970);

    public int handTicks = 4;
    public boolean handsFalling = false;

    private static Clip clip;
    private LocalPoint bloatPrevLoc = null;
    private String bloatDirection = "";

    @Inject
    protected Bloat(SpoonTobPlugin plugin, SpoonTobConfig config) {
        super(plugin, config);
        bloathands = new HashMap();
    }

    public void load() {
        overlayManager.add(bloatOverlay);
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonTobPlugin.class.getResourceAsStream("reverse.wav")));
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (control != null) {
                control.setValue((float)(config.reverseVolume() / 2 - 45));
            }
        } catch (Exception var6) {
            clip = null;
        }
    }

    public void unload() {
        overlayManager.remove(bloatOverlay);
        bloatDownCount = 0;
        bloatState = 0;
        bloatUpTimer = 0;
        bloatDown = null;
        handTicks = 4;
        handsFalling = false;
        bloatPrevLoc = null;
        bloatDirection = "";
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        if (BLOAT_IDS.contains(npc.getId())) {
            bloatActive = true;
            bloatNPC = npc;
            bloatUpTimer = 0;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        if (BLOAT_IDS.contains(npc.getId())) {
            bloatActive = false;
            bloatNPC = null;
            bloatUpTimer = 0;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        if (client.getGameState() != GameState.LOGGED_IN || event.getActor() != bloatNPC)
        {
            return;
        }

        bloatUpTimer = 0;
    }

    @Subscribe
    protected void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectC) {
        if (bloatActive) {
            GraphicsObject graphicsObject = graphicsObjectC.getGraphicsObject();
            if (graphicsObject.getId() >= 1560 && graphicsObject.getId() <= 1590) {
                WorldPoint point = WorldPoint.fromLocal(client, graphicsObject.getLocation());
                if (!bloathands.containsKey(point)) {
                    bloathands.put(point, 4);

                    if(!handsFalling) {
                        handsFalling = true;
                    }
                }
            }
        }

    }

    @Subscribe
    protected void onVarbitChanged(VarbitChanged event) {
        if (isInRegion()) {
            int varp6447 = client.getVarbitValue(client.getVarps(), ROOM_STATE_VARBIT);
            if (varp6447 != lastVarp6447 && varp6447 > 0) {
                bloatUpTimer = 0;
                bloatVar = 1;
            }
            lastVarp6447 = varp6447;

            if (client.getVarbitValue(ROOM_STATE_VARBIT) == 0){
                bloatVar = 0;
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("spoontob")) {
            if (event.getKey().equals("hideAnnoyingAssObjects")) {
                if (TheatreRegions.inRegion(client, TheatreRegions.BLOAT)) {
                    plugin.refreshScene();
                    if (config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.TANK
                            || config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.BOTH) {
                        removeGameObjectsFromScene(tankObjectIDs, client.getPlane());
                        removeGameObjectsFromScene(topOfTankObjectIDs, 1);
                        nullTopOfTankTiles();
                    }

                    if (config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.CHAINS
                            || config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.BOTH) {
                        removeGameObjectsFromScene(ceilingChainsObjectIDs, 1);
                    }
                }
            }else if (event.getKey().equals("reverseVolume")) {
                if(clip != null) {
                    FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float)(config.reverseVolume() / 2 - 45));
                    }
                }
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if(event.getGameState() == GameState.LOGGED_IN) {
            if (TheatreRegions.inRegion(client, TheatreRegions.BLOAT)) {
                if (config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.CHAINS || config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.BOTH) {
                    removeGameObjectsFromScene(ceilingChainsObjectIDs, 1);
                }
                if (config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.TANK || config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.BOTH) {
                    removeGameObjectsFromScene(tankObjectIDs, client.getPlane());
                    removeGameObjectsFromScene(topOfTankObjectIDs, 1);
                    nullTopOfTankTiles();
                }
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event){
        if (TheatreRegions.inRegion(client, TheatreRegions.BLOAT)) {
            if (config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.CHAINS
                    || config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.BOTH) {
                removeGameObjectsFromScene(ceilingChainsObjectIDs, 1);
            }
            if (config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.TANK
                    || config.hideAnnoyingAssObjects() == SpoonTobConfig.annoyingObjectHideMode.BOTH) {
                removeGameObjectsFromScene(tankObjectIDs, client.getPlane());
                removeGameObjectsFromScene(topOfTankObjectIDs, 1);
                nullTopOfTankTiles();
            }
        }
    }

    public void refreshScene() { clientThread.invokeLater(() -> client.setGameState(GameState.LOADING)); }

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

    private void nullTopOfTankTiles() {
        List<WorldPoint> wpl = (new WorldArea(3293, 4445, 6, 6, 1)).toWorldPointList();
        wpl.forEach((wp) -> {
            Collection<WorldPoint> wpi = WorldPoint.toLocalInstance(client, wp);
            wpi.forEach(this::nullThisTile);
        });
    }

    public void nullThisTile(WorldPoint tile) {
        int plane = tile.getPlane();
        int sceneX = tile.getX() - client.getBaseX();
        int sceneY = tile.getY() - client.getBaseY();
        if (plane <= 3 && plane >= 0 && sceneX <= 103 && sceneX >= 0 && sceneY <= 103 && sceneY >= 0) {
            client.getScene().getTiles()[plane][sceneX][sceneY] = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (bloatActive) {
            if(handsFalling){
                handTicks--;
                if(handTicks <= 0){
                    handTicks = 4;
                    handsFalling = false;
                }
            }
            bloatDownCount++;
            bloatUpTimer++;

            bloathands.values().removeIf(v -> v <= 0);
            bloathands.replaceAll((k, v) -> v - 1);

            if (bloatNPC.getAnimation() == -1) // 1 = UP, 2 = DOWN, 3 = WARN, 4 = PAST THRESHOLD;
            {
                bloatDownCount = 0;
                if (bloatNPC.getHealthScale() == 0)
                {
                    bloatState = 2;
                }
                else if (bloatUpTimer >= 38)
                {
                    bloatState = 4;
                }
                else
                {
                    bloatState = 1;
                }
            }
            else
            {
                if (bloatUpTimer >= 38)
                {
                    bloatState = 4;
                }
                else if (25 < bloatDownCount && bloatDownCount < 35)
                {
                    bloatState = 3;
                }
                else if (bloatDownCount < 26)
                {
                    bloatState = 2;
                }
                else if (bloatNPC.getModelHeight() == 568)
                {
                    bloatState = 2;
                }
                else
                {
                    if (bloatUpTimer >= 38)
                    {
                        bloatState = 4;
                    }
                    else
                    {
                        bloatState = 1;
                    }
                }
            }

            if (bloatNPC != null)
            {
                if (bloatNPC.getAnimation() == -1 && bloatDown != null)
                {
                    //log.debug("Nulling the old 'Bloat Down'");
                    bloatDown = null;
                }
                else if (bloatNPC.getAnimation() != -1 && bloatDown == null && !bloatNPC.isDead())
                {
                    //log.debug("Building a new 'Bloat Down'");
                    WorldPoint sw = bloatNPC.getWorldLocation();
                    Direction dir = (new Angle(bloatNPC.getOrientation())).getNearestDirection();
                    Supplier<BloatChunk> chunk = () -> {
                        LocalPoint lp = LocalPoint.fromWorld(client, sw);
                        if (lp != null && client.isInInstancedRegion())
                        {
                            int zone = client.getInstanceTemplateChunks()[0][lp.getSceneX() >> 3][lp.getSceneY() >> 3];
                            return BloatChunk.getOccupiedChunk(zone);
                        }
                        else
                        {
                            return BloatChunk.UNKNOWN;
                        }
                    };
                    bloatDown = new BloatDown(client, sw, dir, chunk.get());
                }
            }

            if(bloatActive && bloatNPC != null && config.bloatReverseNotifier() != SpoonTobConfig.bloatTurnMode.OFF){
                LocalPoint lp = LocalPoint.fromWorld(client, bloatNPC.getWorldLocation());
                if (bloatPrevLoc != null && lp != null) {
                    boolean changed = false;
                    if (lp.getX() > bloatPrevLoc.getX()) {
                        if (bloatDirection.equals("W")) {
                            changed = true;
                        }
                        bloatDirection = "E";
                    } else if (lp.getX() < bloatPrevLoc.getX()) {
                        if (bloatDirection.equals("E")) {
                            changed = true;
                        }
                        bloatDirection = "W";
                    } else if (lp.getY() > bloatPrevLoc.getY()) {
                        if (bloatDirection.equals("S")) {
                            changed = true;
                        }
                        bloatDirection = "N";
                    } else if (lp.getY() < bloatPrevLoc.getY()) {
                        if (bloatDirection.equals("N")) {
                            changed = true;
                        }
                        bloatDirection = "S";
                    }

                    if (changed) {
                        if (config.bloatReverseNotifier() == SpoonTobConfig.bloatTurnMode.SOUND) {
                            client.playSoundEffect(98, config.reverseVolume());
                        } else {
                            clip.setFramePosition(0);
                            clip.start();
                        }
                    }
                }
                bloatPrevLoc = lp;
            }
        }
    }

    Polygon getBloatTilePoly()
    {
        if (bloatNPC == null)
        {
            return null;
        }

        int size = 1;
        NPCComposition composition = bloatNPC.getTransformedComposition();
        if (composition != null)
        {
            size = composition.getSize();
        }

        LocalPoint lp;

        switch (bloatState)
        {
            case 1:
            case 4:
                lp = bloatNPC.getLocalLocation();

                if (lp == null)
                {
                    return null;
                }

                return RoomOverlay.getCanvasTileAreaPoly(client, lp, size, true);
            case 2:
            case 3:
                lp = LocalPoint.fromWorld(client, bloatNPC.getWorldLocation());

                if (lp == null)
                {
                    return null;
                }

                return RoomOverlay.getCanvasTileAreaPoly(client, lp, size, false);
        }

        return null;
    }

    Color getBloatStateColor()
    {
        Color col = config.bloatIndicatorColorUP();
        switch (bloatState)
        {
            case 2:
                col = config.bloatIndicatorColorDOWN();
                break;
            case 3:
                col = config.bloatIndicatorColorWARN();
                break;
            case 4:
                col = config.bloatIndicatorColorTHRESH();
                break;
        }
        return col;
    }

    private boolean isInRegion() {
        return client.getMapRegions() != null && client.getMapRegions().length > 0 && Arrays.stream(client.getMapRegions()).anyMatch((s) -> s == 13125);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (!isInRegion())
            return;
        if (client.getItemContainer(InventoryID.INVENTORY) == null)
            return;
        String target = Text.removeTags(event.getTarget()).toLowerCase();
        MenuEntry[] entries = client.getMenuEntries();
        if ((config.stamReq() == SpoonTobConfig.stamReqMode.NYLO || config.stamReq() == SpoonTobConfig.stamReqMode.BOTH)
                && target.contains("formidable passage") && !client.getItemContainer(InventoryID.INVENTORY).contains(12625))
            client.setMenuEntries(Arrays.copyOf(entries, entries.length - 1));
    }
}