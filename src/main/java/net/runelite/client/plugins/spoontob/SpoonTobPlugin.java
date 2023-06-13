package net.runelite.client.plugins.spoontob;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Provides;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.MenuEntry;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.spoontob.rooms.Bloat.Bloat;
import net.runelite.client.plugins.spoontob.rooms.Maiden.Maiden;
import net.runelite.client.plugins.spoontob.rooms.Maiden.MaidenRedsOverlay;
import net.runelite.client.plugins.spoontob.rooms.Nylocas.NyloInfo;
import net.runelite.client.plugins.spoontob.rooms.Nylocas.Nylocas;
import net.runelite.client.plugins.spoontob.rooms.Sotetseg.Sotetseg;
import net.runelite.client.plugins.spoontob.rooms.Verzik.Verzik;
import net.runelite.client.plugins.spoontob.rooms.Xarpus.Xarpus;
import net.runelite.client.plugins.spoontob.util.CustomGameObject;
import net.runelite.client.plugins.spoontob.util.RaveUtils;
import net.runelite.client.plugins.spoontob.util.TheatreInputListener;
import net.runelite.client.plugins.spoontob.util.TheatreRegions;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.*;

@PluginDescriptor(
        name = "<html><font color=#86C43F>[RB]</font> Spoontob</html>",
        description = " useful 1 plugin",
        enabledByDefault = false
)
public class SpoonTobPlugin extends Plugin {
    private Room[] rooms = null;
    @Inject
    private EventBus eventBus;
    @Inject
    private Maiden maiden;
    @Inject
    private Bloat bloat;
    @Inject
    private Nylocas nylocas;
    @Inject
    private Sotetseg sotetseg;
    @Inject
    private Xarpus xarpus;
    @Inject
    private Verzik verzik;
    @Inject
    private Client client;
    @Inject
    private TheatreInputListener theatreInputListener;
    @Inject
    private ClientThread clientThread;
    @Inject
    private SpoonTobConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MiscOverlay miscOverlay;
    @Inject
    private SituationalTickOverlay tickOverlay;
    @Inject
    private MaidenRedsOverlay redsOverlay;
    @Inject
    public RaveUtils raveUtils;
    @Inject
    private Hooks hooks;

    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    public Color c;
    private Color rave;
    private final Set<CustomGameObject> customizedGameObjects = new LinkedHashSet();
    private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();
    private String roomCompleteMsg = "";

    public GameObject bankLootChest = null;
    public Color raveBankChestColor = Color.WHITE;
    public Color flowColor = new Color(75, 25, 150, 255);
    private boolean raveRedUp = true;
    private boolean raveGreenUp = true;
    private boolean raveBlueUp = true;

    public ArrayList<Integer> hiddenIndices;

    public HashMap<Player, Integer> situationalTicksList = new HashMap<>();
    public HashMap<Player, Integer> getSituationalTicksList() {return situationalTicksList;}

    public int situationalTicks = 0;


    public static int partySize;

    public void configure(Binder binder) {
        binder.bind(TheatreInputListener.class);
    }

    @Provides
    SpoonTobConfig getConfig(ConfigManager configManager) {
        return (SpoonTobConfig)configManager.getConfig(SpoonTobConfig.class);
    }

    protected void startUp() {
        situationalTicksList.clear();
        overlayManager.add(miscOverlay);
        overlayManager.add(tickOverlay);
        bankLootChest = null;
        roomCompleteMsg = "";
        raveBankChestColor = Color.WHITE;
        if (rooms == null) {
            rooms = new Room[]{ maiden, bloat, nylocas, sotetseg, xarpus, verzik};

            for (Room room : rooms) {
                room.init();
            }
        }

        for (Room room : rooms) {
            room.load();
            eventBus.register(room);
        }

        hooks.registerRenderableDrawListener(drawListener);
        hiddenIndices = new ArrayList<>();
    }

    protected void shutDown() {
        situationalTicksList.clear();
        overlayManager.remove(miscOverlay);
        overlayManager.remove(tickOverlay);
        bankLootChest = null;
        modifyCustomObjList(true, true);
        roomCompleteMsg = "";
        raveBankChestColor = Color.WHITE;

        for (Room room : rooms) {
            eventBus.unregister(room);
            room.unload();
        }

        hooks.unregisterRenderableDrawListener(drawListener);
        clearHiddenNpcs();
        hiddenIndices = null;

        situationalTicks = 0;
    }

    public void refreshScene() { clientThread.invokeLater(() -> client.setGameState(GameState.LOADING)); }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (config.removeFRCFlag() && client.getGameState() == GameState.LOGGED_IN && !client.isMenuOpen()) {
            if (TheatreRegions.inRegion(client, TheatreRegions.LOOT_ROOM) || isLootingNonLootRoomChest()) {
                MenuEntry[] entries = client.getMenuEntries();

                for (MenuEntry entry : entries) {
                    if (entry.getOption().equals("Bank-all")) {
                        entry.setForceLeftClick(true);
                        break;
                    }
                }
                client.setMenuEntries(entries);
            }
        }

        if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
            return;
        MenuEntry[] menuEntries = client.getMenuEntries();
        int idx = 0;
        optionIndexes.clear();
        for (MenuEntry entry : menuEntries) {
            String option = Text.removeTags(entry.getOption()).toLowerCase();
            optionIndexes.put(option, idx++);
        }
        idx = 0;
        for (MenuEntry entry : menuEntries) {
            swapMenuEntry(idx++, entry);
        }

        flowColor();
    }

    private void swapMenuEntry(int index, MenuEntry menuEntry) {
        int eventId = menuEntry.getIdentifier();
        String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
        String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
        String[] FreezeSpells = {"ice barrage", "ice burst", "ice blitz", "ice rush", "entangle", "snare", "bind", "blood barrage", "blood barrage", "smoke barrage"};
        String[] LunarSpells = {"energy transfer", "heal other", "vengeance other"};
        MenuEntry[] newEntries = client.getMenuEntries();
        if (config.removeCastToB() && enforceRegion()) {
            for (String spell : FreezeSpells) {
                if (target.startsWith(spell + " ->") &&
                        (menuEntry.getType().getId() != 8 || target.contains("greater skeletal thrall") || target.contains("greater zombified  thrall") || target.contains("greater ghostly thrall"))) {
                    delete(menuEntry, newEntries);
                    return;
                }
            }
        }

        if (option.equals("value") && config.swapTobBuys() && enforceRegion()) {
            swap("buy-1", option, target, index);
        }
    }

    private void swap(String optionA, String optionB, String target, int index) {
        swap(optionA, optionB, target, index, true);
    }

    private void swap(String optionA, String optionB, String target, int index, boolean strict) {
        MenuEntry[] menuEntries = client.getMenuEntries();
        int thisIndex = findIndex(menuEntries, index, optionB, target, strict);
        int optionIdx = findIndex(menuEntries, thisIndex, optionA, target, strict);
        if (thisIndex >= 0 && optionIdx >= 0) {
            swap(optionIndexes, menuEntries, optionIdx, thisIndex);
        }

    }

    private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict) {
        if (strict) {
            List<Integer> indexes = optionIndexes.get(option);

            for(int i = indexes.size() - 1; i >= 0; --i) {
                int idx = indexes.get(i);
                MenuEntry entry = entries[idx];
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (idx <= limit && entryTarget.equals(target)) {
                    return idx;
                }
            }
        } else {
            for(int i = limit; i >= 0; --i) {
                MenuEntry entry = entries[i];
                String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void swap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2) {
        MenuEntry entry = entries[index1];
        entries[index1] = entries[index2];
        entries[index2] = entry;
        client.setMenuEntries(entries);
        optionIndexes.clear();
        int idx = 0;

        for (MenuEntry menuEntry : entries) {
            String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
            optionIndexes.put(option, idx++);
        }

    }

    private void delete(MenuEntry entry, MenuEntry[] newEntries) {
        for (int i = newEntries.length - 1; i >= 0; i--) {
            if (newEntries[i].equals(entry))
                newEntries = (MenuEntry[])ArrayUtils.remove((Object[])newEntries, i);
        }
        client.setMenuEntries(newEntries);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if(config.verzikTeleportCrystalHelper() && Text.removeTags(event.getTarget()).contains("Verzik's crystal shard -> ") && event.getOption().equalsIgnoreCase("use")){
            boolean keepEntry = false;
            for (Player player : client.getPlayers()){
                if (player.getName() != null && event.getTarget().replaceAll("[^A-Za-z0-9]", " ").contains(player.getName())) {
                    keepEntry = true;
                }
            }

            if(!keepEntry){
                MenuEntry[] entries = client.getMenuEntries();
                MenuEntry[] newEntries = new MenuEntry[entries.length - 1];
                System.arraycopy(entries, 0, newEntries, 0, newEntries.length);
                client.setMenuEntries(newEntries);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (enforceRegion()) {
            partySize = 0;
            for (int i = 330; i < 335; i++) {
                if (client.getVarcStrValue(i) != null && !client.getVarcStrValue(i).equals("")) {
                    partySize++;
                }
            }
        }


        raveBankChestColor = Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F);
        if (enforceRegion()) {
            Random random = new Random();
            float hue = random.nextFloat();
            rave = Color.getHSBColor(hue, 0.9F, 1.0F);

            modifyCustomObjList(false, false);

            ArrayList<Player> toRemove0 = new ArrayList<>();
            for (Player n : situationalTicksList.keySet())
            {
                int i = situationalTicksList.get(n);
                if (i - 1 == 0)
                {
                    toRemove0.add(n);
                    continue;
                }
                situationalTicksList.put(n, i - 1);
            }
            for (Player n : toRemove0)
                situationalTicksList.remove(n);

        } else {
            if (config.recolorBarriers() != SpoonTobConfig.barrierMode.OFF) {
                if (!client.isInInstancedRegion()) {
                    Player you = client.getLocalPlayer();
                    if (you != null) {
                        WorldPoint wp = you.getWorldLocation();
                        if (wp.getRegionID() == 14642) {
                            modifyCustomObjList(true, true);
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("spoontob")) {
            if (event.getKey().equals("recolorBarriers") || event.getKey().equals("barriersColor")) {
                modifyCustomObjList(true, false);
                modifyCustomObjList(false, false);
            } else if(event.getKey().equals("lootReminder")){
                if (config.lootReminder() == SpoonTobConfig.lootReminderMode.OFF || config.lootReminder() == SpoonTobConfig.lootReminderMode.DUMB) {
                    if (client.hasHintArrow()) {
                        client.clearHintArrow();
                    }
                }
            }
        }
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject obj = event.getGameObject();
        int id = obj.getId();
        if (id == 32755 || id == 33028) {
            customizedGameObjects.add(new CustomGameObject(obj, id));
            modifyCustomObjList(false, false);
        } else if (id == 41437) {
            bankLootChest = obj;
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event) {
        if (event.getGameObject().getId() == 41437) {
            bankLootChest = null;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (config.lootReminder() != SpoonTobConfig.lootReminderMode.OFF && bankLootChest != null && client.getLocalPlayer() != null) {
            if (client.isInInstancedRegion()){
                if (WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() != 14642) {
                    bankLootChest = null;
                    if (client.hasHintArrow()) {
                        client.clearHintArrow();
                    }
                }
            } else {
                if (client.getLocalPlayer().getWorldLocation().getRegionID() != 14642) {
                    bankLootChest = null;
                    if (client.hasHintArrow()) {
                        client.clearHintArrow();
                    }
                }
            }
        }
    }

    private void modifyCustomObjList(boolean restore, boolean clear) {
        if (!customizedGameObjects.isEmpty()) {
            if (restore) {
                List<CustomGameObject> objs = new ArrayList(customizedGameObjects);
                Lists.reverse(objs).forEach(CustomGameObject::restore);
                if (clear) {
                    customizedGameObjects.clear();
                }
            } else if (config.recolorBarriers() == SpoonTobConfig.barrierMode.COLOR) {
                customizedGameObjects.forEach((o) -> o.setFaceColorValues(config.barriersColor()));
            } else if (config.recolorBarriers() == SpoonTobConfig.barrierMode.RAVE) {
                customizedGameObjects.forEach((o) -> o.setFaceColorValues(rave));
            }
        }
    }

    private boolean isLootingNonLootRoomChest() {
        if (client.isInInstancedRegion()) {
            return false;
        } else {
            Player you = client.getLocalPlayer();
            if (you == null) {
                return false;
            } else {
                WorldPoint wp = you.getWorldLocation();
                if (wp.getRegionID() != 14642) {
                    return false;
                } else {
                    Widget widget = client.getWidget(1507328);
                    return widget != null && !widget.isHidden();
                }
            }
        }
    }

    public boolean crossedLine(int region, Point start, Point end, boolean vertical) {
        if (inRegion(region))
            for (Player p : client.getPlayers()) {
                WorldPoint wp = p.getWorldLocation();
                if (vertical) {
                    for (int j = start.getY(); j < end.getY() + 1; j++) {
                        if (wp.getRegionY() == j && wp.getRegionX() == start.getX())
                            return true;
                    }
                    continue;
                }
                for (int i = start.getX(); i < end.getX() + 1; i++) {
                    if (wp.getRegionX() == i && wp.getRegionY() == start.getY())
                        return true;
                }
            }
        return false;
    }

    public boolean enforceRegion() {
        return inRegion(12611, 12612, 12613, 12687, 13122, 13123, 13125, 13379);
    }

    public boolean inRegion(int... regions) {
        if (client.getMapRegions() != null)
            for (int i : client.getMapRegions()) {
                for (int j : regions) {
                    if (i == j)
                        return true;
                }
            }
        return false;
    }

    public void setHiddenNpc(NPC npc, boolean hidden) {
        if (hidden) {
            hiddenIndices.add(npc.getIndex());
        } else {
            if (hiddenIndices.contains(npc.getIndex()))
            {
                hiddenIndices.remove((Integer) npc.getIndex());
            }
        }
    }

    public void clearHiddenNpcs() {
        hiddenIndices.clear();
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI)
    {
        if (renderable instanceof NPC)
        {
            NPC npc = (NPC) renderable;
            return !hiddenIndices.contains(npc.getIndex());
        }

        return true;
    }

    private void SocketDeathIntegration(int passedIndex) {
        for (NyloInfo ni : nylocas.nylocasNpcs) {
            if (passedIndex == ni.nylo.getIndex()) {
                ni.alive = false;
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if(event.getActor() instanceof Player && event.getActor() != null) {
            Player player = (Player) event.getActor();
            int anim = player.getAnimation();
            int hammerBop = 401;
            int godBop = 7045;
            int clawSpec = 7514;
            int clawBop = 393;
            int whip = 1658;
            int blade = 390;
            int rapier = 8145;
            int chalyBop = 440;
            int chalySpec = 1203;
            int scy = 8056;
            int bggsSpec = 7643;
            int bggsSpec2 = 7642;
            int hammerSpec = 1378;
            int trident = 1167;
            int surge = 7855;
            int ticks = 0;
            if (anim == scy)
                ticks = 5;
            if (anim == clawBop || anim == whip || anim == clawSpec || anim == trident || anim == surge || anim == blade || anim == rapier)
                ticks = 4;
            if (anim == chalySpec || anim == chalyBop)
                ticks = 7;
            if (anim == hammerSpec || anim == hammerBop || anim == bggsSpec || anim == bggsSpec2 || anim == godBop || anim == 7516)
                ticks = 6;
            if (ticks != 0) {
                if(client.getLocalPlayer() != null && player == client.getLocalPlayer()) {
                    situationalTicks = ticks;
                }
                situationalTicksList.put(player, ticks + 1);
            }
        }
    }

    public Color calculateHitpointsColor(double hpPercent) {
        hpPercent = Math.max(Math.min(100.0D, hpPercent), 0.0D);
        double rMod = 130.0D * hpPercent / 100.0D;
        double gMod = 235.0D * hpPercent / 100.0D;
        double bMod = 125.0D * hpPercent / 100.0D;
        int r = (int)Math.min(255.0D, 255.0D - rMod);
        int g = Math.min(255, (int)(0.0D + gMod));
        int b = Math.min(255, (int)(0.0D + bMod));
        return new Color(r, g, b);
    }

    public Color oldHitpointsColor(double hpPercent) {
        hpPercent = Math.max(Math.min(100.0D, hpPercent), 0.0D);
        double rMod = 0;
        double gMod = 0;
        double bMod = 0;
        if (hpPercent >= 75.0D) {
            rMod = 0;
            gMod = 255;
            bMod = 0;
        } else if (hpPercent < 75.0D && hpPercent >= 50.0D) {
            rMod = 255;
            gMod = 255;
            bMod = 0;
        } else if (hpPercent < 50.0D && hpPercent >= 30.0D) {
            rMod = 220;
            gMod = 200;
            bMod = 0;
        } else if (hpPercent < 30.0D) {
            rMod = 255;
            gMod = 102;
            bMod = 102;
        }
        int r = (int) rMod;
        int g = (int)(gMod);
        int b = (int)(bMod);
        return new Color(r, g, b);
    }

    public void flowColor () {
        int red = flowColor.getRed();
        red += raveRedUp ? 1 : -1;
        if(red == 255 || red == 0) {
            raveRedUp = !raveRedUp;
        }
        int green = flowColor.getGreen();
        green += raveGreenUp ? 1 : -1;
        if(green == 255 || green == 0) {
            raveGreenUp = !raveGreenUp;
        }
        int blue = flowColor.getBlue();
        blue += raveBlueUp ? 1 : -1;
        if(blue == 255 || blue == 0) {
            raveBlueUp = !raveBlueUp;
        }
        flowColor = new Color(red, green, blue, 255);
    }
}
    