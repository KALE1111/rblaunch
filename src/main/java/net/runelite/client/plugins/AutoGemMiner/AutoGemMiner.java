package net.runelite.client.plugins.AutoGemMiner;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.PathingTesting.PathingTesting;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.Optional;


@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> Gem Miner</html>",
        description = "",
        enabledByDefault = false,
        tags = {"bn", "plugin"}
)

@Slf4j
public class AutoGemMiner extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoGemMinerOverlay overlay;
    @Inject
    AutoGemMinerConfig config;
    public GemMinerState currentState = GemMinerState.IDLE;
    private Instant startTime;
    int gemCounter;
    public int freeSlots;
    public int previousFreeSlots;
    public int timeout;
    boolean startup;
    WorldPoint BankTile = new WorldPoint(2851, 2955, 0);
    WorldPoint GemMine = new WorldPoint(2820, 2997, 0);
    boolean shouldMine = false;
    @Provides
    AutoGemMinerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoGemMinerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
        startup = true;
        gemCounter = 0;
        freeSlots = 0;
        timeout = 0;
        previousFreeSlots = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startup = false;
        startTime = null;
        currentState = GemMinerState.IDLE;
        freeSlots = 0;
        previousFreeSlots = 0;
        timeout = 0;
    }

    public GemMinerState currentState() {
        return currentState;
    }

    public int getGemCounter() {
        return gemCounter;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if(timeout > 0) {
            timeout--;
            return;
        }

        freeSlots = Inventory.getEmptySlots();
        handleCounter();
        handleState();

        switch (currentState) {
            case WALKING_TO_MINE:
                walkToMine();
                break;
            case MINING_GEMS:
                mineGems();
                break;
            case WALKING_TO_BANK:
                walkToBank();
                break;
            case OPENING_BANK:
                openBank();
                break;
            case DEPOSIT_GEMS:
                depositGems();
                break;
            case CLOSE_BANK:
                closeBank();
                break;
            case IDLE:
                Idle();
                break;
        }

    }

    private void handleState() {
        if (startup) {
            currentState = GemMinerState.STARTING;
            startup = false;
        }
        if (isAtShiloVillage() && inventoryIsEmpty() && !isAtGemMine())
            currentState = GemMinerState.WALKING_TO_MINE;
        if (!inventoryIsFull() && shouldMine)
            currentState = GemMinerState.MINING_GEMS;
        if (inventoryIsFull() && shouldMine)
            currentState = GemMinerState.WALKING_TO_BANK;
        if (isAtBank() && inventoryIsFull())
            currentState = GemMinerState.OPENING_BANK;
        if (Bank.isOpen() && inventoryIsFull())
            currentState = GemMinerState.DEPOSIT_GEMS;
        if (isAtBank() && Bank.isOpen() && inventoryIsEmpty())
            currentState = GemMinerState.CLOSE_BANK;
    }

    public void handleCounter() {
        if (freeSlots < previousFreeSlots) {
            gemCounter++;
        }
        previousFreeSlots = freeSlots;
    }

    public void walkToMine() {
        WorldPoint gemMine = new WorldPoint(2820, 2997, 0);
        PathingTesting.walkTo(gemMine);
    }

    public void mineGems() {
        if (!isMining()) {
            Optional<TileObject> gemRocks = TileObjects.search().withName("Gem rocks").first();
            if (gemRocks.isPresent() && !EthanApiPlugin.isMoving() && !client.getLocalPlayer().isInteracting()) {
                TileObjectInteraction.interact(gemRocks.get(), "Mine");
            }
        }

    }

    public void walkToBank() {
        shouldMine = false;
        WorldPoint bankLocation = new WorldPoint(2851, 2955, 0);
        PathingTesting.walkTo(bankLocation);
    }

    public void openBank() {
        NPCs.search().withName("Banker").nearestToPlayer().ifPresent(bankNpc -> {
            NPCInteraction.interact(bankNpc, "Bank");
            timeout += 2;
        });
    }


    public void depositGems() {
        if (Bank.isOpen()) {
            timeout += 2;
            Widget depositInventory = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
            if (depositInventory != null) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(depositInventory, "Deposit inventory");
            }
        }
    }

    public void closeBank() {
        timeout += 2;
        MousePackets.queueClickPacket();
        client.runScript((29));
    }

    public void Idle() {
    }

    public boolean isAtShiloVillage() {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == 11310;
    }

    public boolean inventoryIsEmpty() {
        return Inventory.getEmptySlots() == 28;
    }

    public boolean inventoryIsFull() {
        return Inventory.full();
    }

    public boolean isAtBank() {
        return client.getLocalPlayer().getWorldLocation().equals(BankTile);
    }

    public boolean isMining() {
        return client.getLocalPlayer().getAnimation() == AnimationID.MINING_RUNE_PICKAXE;
    }

    public boolean isAtGemMine() {
        if(client.getLocalPlayer().getWorldLocation().equals(GemMine)) {
            shouldMine = true;
        }
        return client.getLocalPlayer().getWorldLocation().equals(GemMine);
    }
}


