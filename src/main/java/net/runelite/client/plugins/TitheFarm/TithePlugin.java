package net.runelite.client.plugins.TitheFarm;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.plugins.TitheFarm.tasks.*;
import net.runelite.client.plugins.TitheFarm.utils.Activity;
import net.runelite.client.plugins.TitheFarm.utils.Helpers;
import net.runelite.client.plugins.TitheFarm.utils.LoggableExecutor;
import net.runelite.client.plugins.TitheFarm.utils.Task;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.TitheFarm.utils.Helpers.TITHE_PATCHES;
import static net.runelite.client.plugins.TitheFarm.utils.Helpers.nextInt;

@PluginDescriptor(
        name = "<html><font color=#86C43F>[RB]</font> Tithe Farm</html>",
        description = "RuneBot's Tithe Farm",
        enabledByDefault = false,
        tags = {"rb", "RB"}
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
public class TithePlugin extends Plugin {

    private final int GOLOVANOVA_SEED = 13423;
    public int seed;
    public int runs = 0;
    @Inject
    public Client client;

    @Inject
    private TitheOverlay overlay;
    @Inject
    OverlayManager overlayManager;

    @Inject
    @Getter
    private TitheConfig config;
    @Provides
    TitheConfig provideConfig(ConfigManager configManager){
        return configManager.getConfig(TitheConfig.class);
    }
    public List<WorldPoint> patches = new ArrayList<>();
    Instant timer;
    public boolean harvested = false;
    public WorldPoint startPoint;
    int xpGained = 0;
    private final Map<Skill, Integer> previousSkillExpTable = new EnumMap<>(Skill.class);
    private int previousExpGained;

    protected final List<Task> tasks = new ArrayList<>();
    @Getter
    private Activity currentActivity;
    private Activity previousActivity;
    ScheduledExecutorService executor;
    protected int lastActionTick = 0;
    @Getter
    private boolean running;
    private ScheduledFuture<?> current;
    private ScheduledFuture<?> next;
    private TitheConfig.Route route;
    @Subscribe
    private void onGameTick(GameTick event) {
        if (!running) {
            return;
        }

        if (config.debug()){
            EthanApiPlugin.sendClientMessage("Size of patches: " + patches.size());
            EthanApiPlugin.sendClientMessage("Is patch empty: " + patches.isEmpty());
        }

        try {
            if (config.debug()){
                EthanApiPlugin.sendClientMessage("Current Activity" + currentActivity.getName());
            }
            if (current == null) {
                current = schedule(this::tick);
            } else {
                if (current.isDone()) {
                    if (next == null) {
                        current = schedule(this::tick);
                    } else {
                        current = next;
                        next = null;
                    }
                } else {
                    if (next == null) {
                        next = schedule(this::tick);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setActivity(Activity activity) {
        if (activity == Activity.IDLE && currentActivity != Activity.IDLE) {
            previousActivity = currentActivity;
        }
        currentActivity = activity;
        if (activity != Activity.IDLE) {
            lastActionTick = client.getTickCount();
        }
    }
    public final boolean isCurrentActivity(Activity activity) {
        return currentActivity == activity;
    }
    public final boolean wasPreviousActivity(Activity activity) {
        return previousActivity == activity;
    }
    @Override
    protected void startUp() {
        executor = new LoggableExecutor(1);
        overlayManager.add(overlay);
        running = true;
        timer = Instant.now();
        previousActivity = Activity.IDLE;
        currentActivity = Activity.IDLE;
        startPoint = client.getLocalPlayer().getWorldLocation();
        route = config.route();
        setupPatches();
        whatSeed();

        //Add tasks here
        addTask(RefillWater.class);
        addTask(ResetPatches.class);
        addTask(DropBucket.class);
        addTask(SetupRun.class);
        addTask(WaterPlant.class);
        addTask(HarvestPlant.class);
    }

    public void setupPatches(){
        WorldPoint loc = startPoint;
        switch (route){
            case SIXTEEN:
                first_SIXTEEN_patches(loc);
                break;
            case TWENTY:
                first_TWENTY_patches(loc);
                break;
        }
    }

    private void first_TWENTY_patches(WorldPoint loc){
        //North patches
        patches.add(loc.dx(10).dy(12));
        patches.add(loc.dx(5).dy(12));

        patches.add(loc.dx(10).dy(9));
        patches.add(loc.dx(5).dy(9));

        patches.add(loc.dx(10).dy(6));
        patches.add(loc.dx(5).dy(6));

        patches.add(loc.dx(10).dy(3));
        patches.add(loc.dx(5).dy(3));

        List<WorldPoint> south_patch_3 = TITHE_PATCHES(loc, 5, 4, false, false);
        List<WorldPoint> south_patch_4 = TITHE_PATCHES(loc, 10, 4, false, true);
        List<WorldPoint> north_patch_5 = TITHE_PATCHES(loc, 15, 4, true, true);

        if (south_patch_3.isEmpty() || south_patch_4.isEmpty() || north_patch_5.isEmpty()){
            EthanApiPlugin.sendClientMessage("Error setting up patches");
            return;
        }

        patches.addAll(south_patch_3);
        patches.addAll(south_patch_4);
        patches.addAll(north_patch_5);
    }

    private void first_SIXTEEN_patches(WorldPoint loc){
        //North patches
        patches.add(loc.dx(10).dy(12));
        patches.add(loc.dx(5).dy(12));

        patches.add(loc.dx(10).dy(9));
        patches.add(loc.dx(5).dy(9));

        patches.add(loc.dx(10).dy(6));
        patches.add(loc.dx(5).dy(6));

        patches.add(loc.dx(10).dy(3));
        patches.add(loc.dx(5).dy(3));

        //South patches
        patches.add(loc.dx(10).dy(-3));
        patches.add(loc.dx(5).dy(-3));

        patches.add(loc.dx(10).dy(-6));
        patches.add(loc.dx(5).dy(-6));

        patches.add(loc.dx(10).dy(-9));
        patches.add(loc.dx(5).dy(-9));

        patches.add(loc.dx(10).dy(-12));
        patches.add(loc.dx(5).dy(-12));
    }

    @Override
    protected void shutDown() {
        running = false;
        for (Task task : tasks) {
            Helpers.getEventBus().unregister(task);
        }

        tasks.clear();
        patches.clear();
        current = null;
        next = null;
        startPoint = null;
        harvested = false;
        runs = 0;
        previousActivity = Activity.IDLE;
        currentActivity = Activity.IDLE;
        executor.shutdownNow();
        overlayManager.remove(overlay);
        timer = null;

    }
    protected final void addTask(Task task) {
        Helpers.getEventBus().register(task);
        tasks.add(task);
    }

    protected final <T extends Task> void addTask(Class<T> type) {
        addTask(injector.getInstance(type));
    }
    protected void tick() {
        for (Task t : tasks) {
            if (t.validate()) {
                if (config.debug())
                    EthanApiPlugin.sendClientMessage(t.getStatus());
                setActivity(t.getActivity());
                t.execute();
                break;
            }
        }
    }
    protected ScheduledFuture<?> schedule(Runnable runnable) {
        final int minDelay = Math.min(250, 300);
        final int maxDelay = Math.max(250, 300);

        return executor.schedule(
                runnable,
                nextInt(minDelay, maxDelay), TimeUnit.MILLISECONDS
        );
    }

    @Subscribe
    private void onStatChanged(StatChanged statChanged) {

        final Skill skill = statChanged.getSkill();
        final int xp = statChanged.getXp();

        Integer previous = previousSkillExpTable.put(skill, xp);
        if (previous != null) {
            previousExpGained = xp - previous;
        }

        if (statChanged.getSkill() == Skill.FARMING) {
            xpGained += previousExpGained;
            if (config.debug()){
                EthanApiPlugin.sendClientMessage("XP Gained: " + xpGained);
            }
        }
    }

    private void whatSeed(){
        final int farmingLevel = client.getBoostedSkillLevel(Skill.FARMING);
        if(farmingLevel >= 34 && farmingLevel < 54){
            seed = GOLOVANOVA_SEED;
        }
        if (farmingLevel >= 54 && farmingLevel < 74){
            seed = ItemID.BOLOGANO_SEED;
        }
        if (farmingLevel >= 74){
            seed = ItemID.LOGAVANO_SEED;
        }
    }
}
