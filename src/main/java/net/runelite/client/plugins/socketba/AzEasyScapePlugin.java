package net.runelite.client.plugins.socketba;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.Varbits;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socketba.jacob.CycleCounter;
import net.runelite.client.plugins.socketba.jacob.GroundItem;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.HotkeyListener;
import org.apache.commons.lang3.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Plugin originally provided to SkylerMiner to fix from one in the ba scene. I believe originally made by AZscape
//SkylerMiner has done current fixes for left click healing, socket ba + shift click os calls
//plugin needs a major refractor/rewrite for newest api + better menuentry usage but that'll be on y'all

@PluginDescriptor(name = "<html><font color=#86C43F>[RB]</font> Ba Helper</html>", description = "", tags = {"walk under", "detached cam", "ba"}, enabledByDefault = false)
public class AzEasyScapePlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(AzEasyScapePlugin.class);

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private KeyManager keyManager;

	private boolean walkHerehotKeyPressed = false;

	private int skillifwait = 0;
	private int skillifwait1 = 0;
	private int skillifwait2 = 0;
	private int skillifwait3 = 0;
	private int skillifwait4 = 0;
	private int skillifwait5 = 0;

	private boolean lcon = false;

	private boolean socketMsgRecieved = false;

	private boolean shiftOsKeyPressed = false;

	private int baRole = 0;

	private String HealCall = "";
	private String AttackerCall = "";
	private String CollectorCall = "";
	private String DefenderCall = "";
	private String lastCall = "";

	private int menuentyr =0;

	@Inject
	private ItemManager itemManager;

	public int getBaRole() {
		return this.baRole;
	}

	@Inject
	private EventBus eventBus;

	private final Map<GroundItem.GroundItemKey, GroundItem> collectedGroundItems = new LinkedHashMap<>();

	@Inject
	private AzEasyScapeOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	public Map<GroundItem.GroundItemKey, GroundItem> getCollectedGroundItems() {
		return this.collectedGroundItems;
	}

	private int inGameBit = 0;

	int tickNum;

	public int getInGameBit() {
		return this.inGameBit;
	}

	int pastCall = 0;

	private CycleCounter counter;

	@Inject
	private InfoBoxManager infoBoxManager;

	private ArrayList<NPC> healers = new ArrayList<>();

	@Inject
	private AzEasyScapeConfig config;

	public ArrayList<NPC> getHealers() {
		return this.healers;
	}

	@Provides

	AzEasyScapeConfig provideConfig(ConfigManager configManager) {
		return (AzEasyScapeConfig)configManager.getConfig(AzEasyScapeConfig.class);
	}

	public AzEasyScapeConfig getConfig() {
		return this.config;
	}

	private final HotkeyListener hotkeyListener = new HotkeyListener(() -> this.config.walkUnderHotkey()) {
		public void keyPressed(KeyEvent event) {
			if (AzEasyScapePlugin.this.config.walkUnderHotkey().matches(event) && AzEasyScapePlugin.this.config.walkUnderHotkey() != Keybind.NOT_SET) {
				AzEasyScapePlugin.this.setWalkHerehotKeyPressed(true);
				int i = event.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					event.consume();
			}
			if (AzEasyScapePlugin.this.config.shiftOsHotkey().matches(event) && AzEasyScapePlugin.this.config.shiftOsHotkey() != Keybind.NOT_SET) {
				AzEasyScapePlugin.this.shiftOsKeyPressed = true;
				int i = event.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					event.consume();
			}
			if (AzEasyScapePlugin.this.config.lcHotkey().matches(event) && AzEasyScapePlugin.this.config.lcHotkey() != Keybind.NOT_SET) {
				AzEasyScapePlugin.this.lcon = true;
				int i = event.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					event.consume();
			}

			if (AzEasyScapePlugin.this.config.camKey().matches(event) && AzEasyScapePlugin.this.config.camKey() != Keybind.NOT_SET) {
				if (AzEasyScapePlugin.this.client.getOculusOrbState() == 0) {
					AzEasyScapePlugin.this.client.setOculusOrbState(1);
					AzEasyScapePlugin.this.client.setOculusOrbNormalSpeed(36);
				} else {
					AzEasyScapePlugin.this.client.setOculusOrbState(0);
					AzEasyScapePlugin.this.client.setOculusOrbNormalSpeed(12);
				}
				int i = event.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					event.consume();
			}
			if (AzEasyScapePlugin.this.getConfig().debug() &&
				AzEasyScapePlugin.this.client.getLocalPlayer() != null)
				AzEasyScapePlugin.this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "[az] Debug key pressed: " + event.getKeyCode(), "");
		}

		public void keyTyped(KeyEvent event) {
			if (AzEasyScapePlugin.this.config.walkUnderHotkey().matches(event) && AzEasyScapePlugin.this.config.walkUnderHotkey() != Keybind.NOT_SET) {
				int i = event.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					event.consume();
			}
			if (AzEasyScapePlugin.this.config.shiftOsHotkey().matches(event) && AzEasyScapePlugin.this.config.shiftOsHotkey() != Keybind.NOT_SET) {
				int i = event.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					event.consume();
			}

			if (AzEasyScapePlugin.this.config.lcHotkey().matches(event) && AzEasyScapePlugin.this.config.lcHotkey() != Keybind.NOT_SET) {
				AzEasyScapePlugin.this.lcon = true;
				int i = event.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					event.consume();
			}
			if (AzEasyScapePlugin.this.config.camKey().matches(event) && AzEasyScapePlugin.this.config.camKey() != Keybind.NOT_SET) {
				int i = event.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					event.consume();
			}
		}

		public void keyReleased(KeyEvent e) {
			if (AzEasyScapePlugin.this.config.walkUnderHotkey().matches(e) && AzEasyScapePlugin.this.config.walkUnderHotkey() != Keybind.NOT_SET) {
				AzEasyScapePlugin.this.setWalkHerehotKeyPressed(false);
				int i = e.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					e.consume();
			}

			if (AzEasyScapePlugin.this.config.shiftOsHotkey().matches(e) && AzEasyScapePlugin.this.config.shiftOsHotkey() != Keybind.NOT_SET) {
				AzEasyScapePlugin.this.shiftOsKeyPressed = false;
				int i = e.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					e.consume();
			}

			if (AzEasyScapePlugin.this.config.lcHotkey().matches(e) && AzEasyScapePlugin.this.config.lcHotkey() != Keybind.NOT_SET) {
				AzEasyScapePlugin.this.lcon = false;
				int i = e.getKeyCode();
				int[] safe = { 16, 18, 157 };
				if (!ArrayUtils.contains(safe, i))
					e.consume();
			}
		}
	};

	private long rollOverCheck;

	public void startUp() {
		this.overlayManager.add(this.overlay);
		this.keyManager.registerKeyListener((KeyListener)this.hotkeyListener);
		HealCall = "";
		AttackerCall = "";
		CollectorCall = "";
		DefenderCall = "";
	}
	private void resetcalls(){
		HealCall = "";
		AttackerCall = "";
		CollectorCall = "";
		DefenderCall = "";
	}

	public void shutDown() {
		this.overlayManager.remove(this.overlay);
		this.keyManager.unregisterKeyListener((KeyListener)this.hotkeyListener);
		removeCounter();
		this.collectedGroundItems.clear();
		this.baRole = 0;
		this.inGameBit = 0;
		HealCall = "";
		AttackerCall = "";
		CollectorCall = "";
		DefenderCall = "";

		 walkHerehotKeyPressed = false;

		 skillifwait = 0;
		 skillifwait1 = 0;
		 skillifwait2 = 0;
		 skillifwait3 = 0;
		 skillifwait4 = 0;
		 skillifwait5 = 0;

		 lcon = false;

		 socketMsgRecieved = false;

		 shiftOsKeyPressed = false;

		 baRole = 0;
	}

	@Subscribe
	public void onFocusChanged(FocusChanged event) {
		if (!event.isFocused()) {
			this.walkHerehotKeyPressed = false;
			this.shiftOsKeyPressed = false;
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned e) {
		NPC npc = e.getNpc();
		if (npc != null && !this.healers.contains(npc) && npc.getName() != null && npc.getName().toLowerCase().contains("penance heal"))
			this.healers.add(npc);
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned) {
		TileItem item = itemSpawned.getItem();
		Tile tile = itemSpawned.getTile();
		GroundItem groundItem = buildGroundItem(tile, item);
		GroundItem.GroundItemKey groundItemKey = new GroundItem.GroundItemKey(item.getId(), tile.getWorldLocation());
		GroundItem existing = this.collectedGroundItems.putIfAbsent(groundItemKey, groundItem);
		if (existing != null)
			existing.setQuantity(existing.getQuantity() + groundItem.getQuantity());
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned) {
		TileItem item = itemDespawned.getItem();
		Tile tile = itemDespawned.getTile();
		GroundItem.GroundItemKey groundItemKey = new GroundItem.GroundItemKey(item.getId(), tile.getWorldLocation());
		GroundItem groundItem = this.collectedGroundItems.get(groundItemKey);
		if (groundItem != null)
			if (groundItem.getQuantity() <= item.getQuantity()) {
				this.collectedGroundItems.remove(groundItemKey);
			} else {
				groundItem.setQuantity(groundItem.getQuantity() - item.getQuantity());
			}
	}

	private GroundItem buildGroundItem(Tile tile, TileItem item) {
		int itemId = item.getId();
		ItemComposition itemComposition = this.itemManager.getItemComposition(itemId);
		int realItemId = (itemComposition.getNote() != -1) ? itemComposition.getLinkedNoteId() : itemId;
		GroundItem groundItem = GroundItem.builder().id(itemId).location(tile.getWorldLocation()).itemId(realItemId).quantity(item.getQuantity()).name(itemComposition.getName()).height(tile.getItemLayer().getHeight()).build();
		return groundItem;
	}

	private void addCounter() {
		if (this.config.defTimer() && this.counter == null) {
			int itemSpriteId = 10551;
			AsyncBufferedImage asyncBufferedImage = this.itemManager.getImage(itemSpriteId);
			this.counter = new CycleCounter((BufferedImage)asyncBufferedImage, this, this.tickNum);
			this.infoBoxManager.addInfoBox((InfoBox)this.counter);
		}
	}

	private void removeCounter() {
		if (this.counter != null) {
			this.infoBoxManager.removeInfoBox((InfoBox)this.counter);
			this.counter = null;
		}
	}

	private boolean entryMatchHorn(MenuEntry ent, String baW, boolean disp) {
		String entry = ent.getOption().toLowerCase();
		String ba = baW.toLowerCase();
		String[] matches = {
			"red", "green", "blue", "worm", "tofu", "meat", "cracker", "accurate", "aggres", "contr",
			"defen", "examine" };
		if ((!entry.contains("tell-") && !disp) || (!entry.contains("take-") && disp))
			return false;
		if (this.config.debug())
			this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Attemping match: ba: " + ba + ", ent:" + entry + ", type: " + ent.getType(), "");
		for (String m : matches) {
			if (ba.contains(m) && entry.contains(m))
				return true;
		}
		return false;
	}

	public int idxOf(NPC n) {
		if (n == null)
			return -1;
		for (int i = 0; i < getHealers().size(); i++) {
			if (getHealers().get(i) == n)
				return i + 1;
		}
		return -1;
	}

	@Subscribe
	public void onClientTick(ClientTick event) {


		if(config.lcAllwaysOn() || lcon)
		{
			fixHealer();
		}

			skillifwait5 = skillifwait5 +1;
			if (skillifwait5 >= 30){
				skillifwait5 = 0;
				if(getBaWidget() != null) {
					if (!getBaWidget().getText().equals(lastCall) || lastCall.equals("")) {
						lastCall = getBaWidget().getText();
						resetcalls();
					}
				}
			}


		if (this.config.collhelp()) {
			skillifwait = skillifwait +1;
			if (skillifwait >= 30){
				skillifwait = 0;
				setColourEgg();
			}
		}
		if (this.config.spoof()) {
			skillifwait1 = skillifwait1 +1;
			if (skillifwait1 >= 30){
				skillifwait1 = 0;
				this.client.setGameState(GameState.CONNECTION_LOST);
			}
		}
		skillifwait2 = skillifwait2 +1;
		if (skillifwait2 >= 30){
			skillifwait2 = 0;
			Widget callWidget = getBaWidget();
			if (callWidget != null) {
				if (callWidget.getTextColor() != this.pastCall && callWidget.getTextColor() == 16316664)
					this.tickNum = 0;
				this.pastCall = callWidget.getTextColor();
			}
		}


		if (config.socketba()) {
			skillifwait3 = skillifwait3 +1;
			if (skillifwait3 >= 30 || socketMsgRecieved)
			{
				skillifwait3 = 0;
				socketMsgRecieved = false;
				//log.info("attempting socket send, if nothing underneath then no current call(probbaly not in game)");
				if (getBaWidget().getText() != null)
				{
					skillifwait3 = 0;
					JSONObject data = new JSONObject();
					data.put("role", String.valueOf(baRole));
					data.put("call", getBaWidget().getText());
					JSONObject payload = new JSONObject();
					payload.put("socketba", data);
					this.eventBus.post(new SocketBroadcastPacket(payload));
					//log.info("Sent role {}, with call {} to socket client", baRole, getBaWidget().getText());
					skillifwait3 = 0;
				}
			}
		}
		if (getBaWidget() != null && config.socketba()) {
			skillifwait4 = skillifwait4 +1;
			if (skillifwait4 >= 30)
			{
				//log.info("attempting socket send, if nothing underneath then no current call(probbaly not in game)");
				//getBaListenWidget().setText("Testing");
				skillifwait4 = 0;
				if (baRole == 4)
				{
					if (!DefenderCall.equals(""))
					{
						getBaListenWidget().setText(DefenderCall);
					}
				}
				if (baRole == 1)
				{
					if (!CollectorCall.equals(""))
					{
						Widget listenwidg = client.getWidget(485, 7);
						Widget listenwid1 = client.getWidget(485, 8);
						String[] splitcall = CollectorCall.split("/");
						listenwidg.setText(splitcall[0] + "/");
						listenwid1.setText(splitcall[1] + "/" + splitcall[2]);
					}
				}
				if (baRole == 2)
				{
					if (!AttackerCall.equals(""))
					{
						Widget listenwidg = client.getWidget(486, 7);
						listenwidg.setText(AttackerCall);
					}
				}

				if (baRole == 3)
				{
					if (!HealCall.equals(""))
					{
						//log.info("Set Heal call widget");
						getBaListenWidget().setText(HealCall);
					}
				}
					//log.info("Sent role {}, with call {} to socket client", baRole, getBaWidget().getText());

			}
		}



		if (this.client.getGameState() == GameState.LOGGED_IN && !this.client.isMenuOpen() && (this.walkHerehotKeyPressed || rollOver())) {
			addRollOver();
			MenuEntry[] entries = this.client.getMenuEntries();
			int entryIndex = -1;
			for (int i = 0; i < entries.length; i++) {
				MenuEntry entry = entries[i];
				int j = entry.getType().getId();
				if (j >= 2000)
					j -= 2000;
				if (j == MenuAction.WALK.getId())
					entryIndex = i;
			}
			if (entryIndex < 0){
				return;}
			MenuEntry[] var8 = entries;
			int var10 = entries.length;
			for (int opId = 0; opId < var10; opId++) {
				MenuEntry menuEntry = var8[opId];
				if (menuEntry.getType().getId() < MenuAction.WALK.getId())
					menuEntry.setType(MenuAction.of(menuEntry.getType().getId() + 2000));
			}
			MenuEntry first = entries[entries.length - 1];
			entries[entries.length - 1] = entries[entryIndex];
			entries[entryIndex] = first;
			this.client.setMenuEntries(entries);
		}
		fixHorn();
		//fixHealer();
/*		if (this.client.getGameState() == GameState.LOGGED_IN && this.inGameBit == 1 && this.baRole == 4) {
			List<MenuEntry> smallNyloEntries = new ArrayList<>();
			List<MenuEntry> restEntries = new ArrayList<>();
			for (MenuEntry ent : this.client.getMenuEntries()) {
				String entTarget = ent.getTarget().toLowerCase();
				String entOption = ent.getOption();
				int entId = ent.getIdentifier();
				NPC entNpc = this.client.getCachedNPCs()[entId];
				int healerIdx = idxOf(entNpc);
				if (entTarget.contains("penance healer") && entTarget.contains("->") && entTarget
					.contains("poisoned ")) {
					smallNyloEntries.add(ent);
				} else if (!entTarget.contains("penance cave") && (
					!this.config.wheygay() || !entTarget.contains("->") ||
						!entTarget.contains("poisoned ") || entTarget.endsWith("egg") || entTarget.contains("penance heal"))) {
					restEntries.add(ent);
				}
			}
			List<MenuEntry> newEntries = (List<MenuEntry>)Stream.concat(restEntries.stream(), smallNyloEntries.stream()).collect(Collectors.toList());
			this.client.setMenuEntries(newEntries.<MenuEntry>toArray(new MenuEntry[0]));
		}*/
		if (this.client.getGameState() == GameState.LOGGED_IN && this.config.collhelp() && this.baRole == 2 && this.inGameBit == 1 &&
			(this.client.getWidget(486, this.config.listenWidget()).getText() != null || !CollectorCall.equals(""))) {

			String[] currentCall = this.client.getWidget(486, this.config.listenWidget()).getText().split(" ");
			if(CollectorCall.equals("")) {
			}

			if (currentCall[0] != null) {
				List<MenuEntry> smallNyloEntries = new ArrayList<>();
				List<MenuEntry> restEntries = new ArrayList<>();
				List<MenuEntry> trshEntries = new ArrayList<>();
				for (MenuEntry ent : this.client.getMenuEntries()) {
					String entTarget = ent.getTarget().toLowerCase();
					String entOption = ent.getOption();
					if (entTarget.contains(" egg")) {
						if (entTarget.contains("yellow egg") && entOption.equals("Take")) {
							ent.setTarget(ColorUtil.prependColorTag("Yellow egg", Color.YELLOW));
							smallNyloEntries.add(ent);
						} else if (entTarget.contains(currentCall[0].toLowerCase()) && entOption.equals("Take")) {
							if (entTarget.toLowerCase().contains("red")) {
								ent.setTarget(ColorUtil.prependColorTag("Red egg", Color.RED));
							} else if (entTarget.toLowerCase().contains("blue")) {
								ent.setTarget(ColorUtil.prependColorTag("Blue egg", Color.CYAN.darker()));
							} else if (entTarget.toLowerCase().contains("green")) {
								ent.setTarget(ColorUtil.prependColorTag("Green egg", Color.GREEN));
							}
							smallNyloEntries.add(ent);
						} else if (!entOption.startsWith("Take") && !entOption.startsWith("Examine")) {
							restEntries.add(ent);
						} else if (!entOption.startsWith("Examine") && (!entOption.contains("Attack") || !entTarget.contains("penance queen"))) {
							if (entTarget.toLowerCase().contains("red")) {
								ent.setTarget(ColorUtil.prependColorTag("Red egg", Color.RED));
							} else if (entTarget.toLowerCase().contains("blue")) {
								ent.setTarget(ColorUtil.prependColorTag("Blue egg", Color.CYAN.darker()));
							} else if (entTarget.toLowerCase().contains("green")) {
								ent.setTarget(ColorUtil.prependColorTag("Green egg", Color.GREEN));
							}
							trshEntries.add(ent);
						}
					} else {
						restEntries.add(ent);
					}
				}
				List<MenuEntry> newEntries = (List<MenuEntry>)Stream.concat(trshEntries.stream(), ((List)Stream.concat(restEntries.stream(), smallNyloEntries.stream()).collect(Collectors.toList())).stream()).collect(Collectors.toList());
				this.client.setMenuEntries(newEntries.<MenuEntry>toArray(new MenuEntry[0]));
			}
		}

		if (this.client.getGameState() == GameState.LOGGED_IN && this.inGameBit == 1) {
			List<MenuEntry> smallNyloEntries = new ArrayList<>();
			List<MenuEntry> restEntries = new ArrayList<>();
			MenuEntry[] oldEntries = this.client.getMenuEntries();
			MenuEntry[] sortedEntries = new MenuEntry[oldEntries.length];
			for (MenuEntry ent : oldEntries) {
				String entTarget = stripColor(ent.getTarget().toLowerCase());
				String entOption = ent.getOption();
				boolean defFood = (this.baRole == 3 && this.config.removeBait() && (entTarget.startsWith("crackers") || entTarget.startsWith("worms") || entTarget.startsWith("tofu")));
				if (this.config.disp()) {
					if (this.baRole == 1 && (entTarget.contains("healer item machine") || entTarget.contains("defender item machine") || entTarget.contains("collector convert"))) {
						restEntries.add(ent);
						continue;
					}
					if (this.baRole == 2 && (entTarget.contains("healer item machine") || entTarget.contains("defender item machine") || entTarget.contains("attacker item machine"))) {
						restEntries.add(ent);
						continue;
					}
					if (this.baRole == 3 && (entTarget.contains("healer item machine") || entTarget.contains("attacker item machine") || entTarget.contains("collector convert"))) {
						restEntries.add(ent);
						continue;
					}
					if (this.baRole == 4 && (entTarget.contains("attacker item machine") || entTarget.contains("defender item machine") || entTarget.contains("collector convert"))) {
						restEntries.add(ent);
						continue;
					}
				}
				if (this.baRole == 2 || (!entTarget.contains("green egg") &&
					!entTarget.contains("red egg") &&
					!entTarget.contains("blue egg")))
					if (this.baRole == 1 || !entOption.toLowerCase().contains("attack") || (!entTarget.contains("penance fighter") &&
						!entTarget.contains("penance ranger")))
						if (defFood) {
							restEntries.add(ent);
						} else {
							smallNyloEntries.add(ent);
						}
				continue;
			}
			List<MenuEntry> newEntries = (List<MenuEntry>)Stream.concat(restEntries.stream(), smallNyloEntries.stream()).collect(Collectors.toList());
			this.client.setMenuEntries(newEntries.<MenuEntry>toArray(new MenuEntry[0]));
		}
		fixOsShift();
	}

	private Widget getBaWidget() {
		if (this.client.getWidget(487, this.config.tocallWidget()) != null)
			return this.client.getWidget(487, this.config.tocallWidget());
		if (this.client.getWidget(485, this.config.rolesprite()) != null)
			return this.client.getWidget(485, this.config.rolesprite());
		if (this.client.getWidget(486, this.config.tocallWidget()) != null)
			return this.client.getWidget(486, this.config.tocallWidget());
		return (this.client.getWidget(488, this.config.tocallWidget()) != null) ? this.client.getWidget(488, this.config.tocallWidget()) : null;
	}
	private Widget getBaListenWidget() {
		if (this.client.getWidget(487, this.config.listenWidget()) != null)
			return this.client.getWidget(487, this.config.listenWidget());
		if (this.client.getWidget(485, this.config.rolesprite()) != null)
			return this.client.getWidget(485, this.config.rolesprite());
		if (this.client.getWidget(486, this.config.listenWidget()) != null)
			return this.client.getWidget(486, this.config.listenWidget());
		return (this.client.getWidget(488, this.config.listenWidget()) != null) ? this.client.getWidget(488, this.config.listenWidget()) : null;
	}

	public boolean rollOver() {
		if (System.currentTimeMillis() > this.rollOverCheck)
			return false;
		return true;
	}

	private void addRollOver() {
		if (rollOver())
			return;
		this.rollOverCheck = System.currentTimeMillis() + 300L;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {
		int inGame = this.client.getVar(Varbits.IN_GAME_BA);
		if (this.inGameBit != inGame)
			if (this.inGameBit == 1) {
				this.pastCall = 0;
				removeCounter();
			} else {
				addCounter();
			}
		this.inGameBit = inGame;
		if (this.inGameBit != 1) {
			this.collectedGroundItems.clear();
			this.healers = new ArrayList<>();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!this.config.defTimer())
			removeCounter();
	}
	@Subscribe
	public void onSocketReceivePacket(SocketReceivePacket event) {
		//log.info("recieved packet");
		if (this.client.getLocalPlayer() != null  && getBaListenWidget() != null)
			try {
				JSONObject payload = event.getPayload();
				if (payload.has("socketba"))
				{
					JSONObject data = payload.getJSONObject("socketba");
					//log.info("REcieved socketba packet");
					if (data.getString("role").equals("1"))
					{
						this.AttackerCall = data.getString("call");
						//log.info("Set attacker call");
						socketMsgRecieved = true;
					}
					else if (data.getString("role").equals("3"))
					{
						this.DefenderCall = data.getString("call");
						//log.info("Set Defender call");
						//log.info(DefenderCall);
						socketMsgRecieved = true;
					}
					else if (data.getString("role").equals("4"))
					{
						this.HealCall = data.getString("call");
						//log.info("Set Healer call");
						//log.info(HealCall);
						socketMsgRecieved = true;
					}
					else if (data.getString("role").equals("2"))
					{
						this.CollectorCall = data.getString("call");
						//log.info("Set Collector call");
						socketMsgRecieved = true;

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}


	


	@Subscribe
	public void onGameTick(GameTick r) {


	}

	private void setColourEgg() {
		if (this.config.collhelp())
			if (this.client.getWidget(486, this.config.listenWidget()) != null) {
				String eggC = this.client.getWidget(486, this.config.listenWidget()).getText().split(" ")[0].toLowerCase();
				if (!eggC.equals("red") && !eggC.equals("green") && !eggC.equals("blue")) {
					this.overlay.setEggColour((String)null);
				} else {
					this.overlay.setEggColour(eggC);
				}
			} else {
				this.overlay.setEggColour((String)null);
			}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e) {
		if (e.getMenuAction().getId() == 7 && e.getMenuOption().toLowerCase().contains("meat")) {
			this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "set meat back to 1001", "");
			//TODO figure out a fix here e.set(1001);
		}
		if(config.lcAllwaysOn() || lcon) {
				if (e.getMenuEntry().getParam0() == 6969){
					{
						e.consume();
						//System.out.println("Found");
						Optional<NPC> healer = NPCs.search().nameContains("Penance Healer").filter(npc -> npc.getIndex() == e.getId()).first();
						System.out.println(healer.get());
						if (healer.isPresent()) {
							System.out.println("Found idx");
							MousePackets.queueClickPacket();
							Widget listenWid = this.client.getWidget(488, this.config.listenWidget());
							String bacall = listenWid.getText().toLowerCase();
							List<Widget> food = Inventory.search().nameContains(bacall.split(" ")[1]).result();
							if (food.size() >0) {
								MousePackets.queueClickPacket();
								NPCPackets.queueWidgetOnNPC(healer.get(),food.get(food.size()-1));
							}

						}
					}
				}


		}

	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		menuentyr = 0;
		if (this.client.getGameState() == GameState.LOGGED_IN && (this.walkHerehotKeyPressed || rollOver())) {

			addRollOver();
			boolean hasWalkHere = false;
			MenuEntry[] var3 = this.client.getMenuEntries();
			int var4 = var3.length;
			for (int var5 = 0; var5 < var4; var5++) {
				MenuEntry menuEntry = var3[var5];
				int opId = menuEntry.getType().getId();
				if (opId >= 2000)
					opId -= 2000;
				menuentyr = ((opId == MenuAction.WALK.getId()) ? 1 : 0);
			}
			if (menuentyr == 0)

			if (event.getType() < MenuAction.WALK.getId())
				deprioritizeEntry(event.getIdentifier(), event.getType());
		}
		fixHorn();
		fixOsShift();
	}

	private void fixOsShift()
	{
		//log.info("Made it here");
		if (this.client.getGameState() == GameState.LOGGED_IN && getBaWidget() != null && baRole == 4)
		{
			List<MenuEntry> prioritisedEntries = new ArrayList<>();
			List<MenuEntry> restEntries = new ArrayList<>();
			Widget listenWid = this.client.getWidget(488, this.config.listenWidget());
			String calledFood = HealCall;
			if(HealCall.equals("")){
				calledFood = listenWid.getText();
			}
			//log.info("IDentified {} as call",listenWid.getText());
			if (listenWid != null)
			{
				boolean meat = listenWid.getText().toLowerCase().contains("meat");
				for (MenuEntry ent : this.client.getMenuEntries())
				{


					String entOption = ent.getOption();
					if (ent.getTarget().toLowerCase().contains("healer item machine") &&
						entryMatchHorn(ent, listenWid.getText(), true) && this.shiftOsKeyPressed)
					{

						ent.setDeprioritized(false);
						prioritisedEntries.add(ent);
					}
					else
					{
						if ((meat && ent.getTarget().toLowerCase().contains("healer item machine") && this.shiftOsKeyPressed && ent.getType().getId() < 2000) || (meat && ent.getType().getId() == 23))
						{

							ent.setDeprioritized(true);

						}
						restEntries.add(ent);
					}
					List<MenuEntry> newEntries = (List<MenuEntry>) Stream.concat(restEntries.stream(), prioritisedEntries.stream()).collect(Collectors.toList());
					this.client.setMenuEntries(newEntries.<MenuEntry>toArray(new MenuEntry[0]));
				}
			}
		}
	}

	private void specialSwap(int actionId, int opId) {
		MenuEntry[] menuEntries = this.client.getMenuEntries();
		for (int i = menuEntries.length - 1; i >= 0; i--) {
			MenuEntry entry = menuEntries[i];
			if (entry.getType().getId() == actionId && entry.getIdentifier() == opId) {
				entry.setType(MenuAction.of(MenuAction.CC_OP.getId()));
				menuEntries[i] = menuEntries[menuEntries.length - 1];
				menuEntries[menuEntries.length - 1] = entry;
				this.client.setMenuEntries(menuEntries);
				break;
			}
		}
	}

	private void fixHorn() {
		if (this.client.getGameState() == GameState.LOGGED_IN && getBaWidget() != null && this.config.horn()) {
			List<MenuEntry> smallNyloEntries = new ArrayList<>();
			List<MenuEntry> restEntries = new ArrayList<>();
			String baW = getBaWidget().getText();
			if (baW != null) {
				for (MenuEntry ent : this.client.getMenuEntries()) {
					String entTarget = ent.getTarget().toLowerCase();
					String entOption = ent.getOption();
					if (entryMatchHorn(ent, baW, false)) {
						smallNyloEntries.add(ent);
					} else {
						restEntries.add(ent);
					}
				}
				List<MenuEntry> newEntries = (List<MenuEntry>)Stream.concat(restEntries.stream(), smallNyloEntries.stream()).collect(Collectors.toList());
				this.client.setMenuEntries(newEntries.<MenuEntry>toArray(new MenuEntry[0]));
			}
		}
	}

	private boolean canAdd(MenuEntry npc){

		return npc.getTarget().contains("Penance Healer") && npc.getOption().equals("Examine");
	}

	private void fixHealer() {
		if (true) {
			//System.out.println("testing");
			List<MenuEntry> curr = Arrays.stream(client.getMenuEntries()).filter(e -> e.getTarget().contains("FOOD")).collect(Collectors.toList());
			//System.out.println(curr);
			if(curr.size() > 0){
				System.out.println("wipe");
				client.setMenuEntries((MenuEntry[]) curr.stream().toArray());
			}

			List<MenuEntry> smallNyloEntries = new ArrayList<>();
			List<MenuEntry> restEntries = new ArrayList<>();
			/*String baW = getBaWidget().getText();
			Widget listenWid = this.client.getWidget(488, this.config.listenWidget());
			String bacall = listenWid.getText().toLowerCase();*/
			ArrayList<MenuEntry> healMenus = new ArrayList<>();
			ArrayList<NPC> healers = new ArrayList<>();
			//System.out.println("Here1");
			Arrays.stream(client.getMenuEntries()).filter(npc -> canAdd(npc)).forEach(x -> healers.add(x.getNpc()));
			//Arrays.stream(client.getMenuEntries()).filter(npc -> npc.getTarget().contains("Penance Healer")).forEach(x -> System.out.println(x.getNpc()));

			for(NPC healer : healers){
				//System.out.println("Here");
				MenuEntry menu = client.createMenuEntry(-1);
				menu.setTarget(String.format("<col=ff9040>%s</col><col=ffffff> -> <col=ffff00>Penance Healer","FOOD"));
				menu.setOption("Use");
				menu.setType(MenuAction.WIDGET_TARGET_ON_NPC);
				menu.setIdentifier(healer.getIndex());
				menu.setParam0(6969);
				menu.setParam1(0);
				healMenus.add(menu);
				eventBus.post(new MenuEntryAdded(menu));
				//System.out.println(menu);
			}

				//MenuEntry newW = Arrays.stream(client.getMenuEntries()).filter(npc -> npc.getTarget().equals("Penance Healer")).findFirst().get();


				for (MenuEntry ent : this.client.getMenuEntries()) {

					String entTarget = ent.getTarget().toLowerCase();
					String entOption = ent.getOption();
					//System.out.println(ent == newW);
					if (ent.getTarget().equals("Penance Healer") && ent.getOption().contains("->")) {
						smallNyloEntries.add(ent);
					} else {
						restEntries.add(ent);
					}
				}
				List<MenuEntry> newEntries = (List<MenuEntry>)Stream.concat(restEntries.stream(), smallNyloEntries.stream()).collect(Collectors.toList());
				this.client.setMenuEntries(newEntries.<MenuEntry>toArray(new MenuEntry[0]));
		}
	}





	static String stripColor(String str) {
		return str.replaceAll("(<col=[0-9a-f]+>|</col>)", "");
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event) {
		resetcalls();
		switch (event.getGroupId()) {
			case 485:
				this.baRole = 1;
				break;
			case 486:
				this.baRole = 2;
				break;
			case 487:
				this.baRole = 3;
				break;
			case 488:
				this.baRole = 4;
				break;
		}
	}

	private void deprioritizeEntry(int id, int op_id) {
		MenuEntry[] menuEntries = this.client.getMenuEntries();
		for (int i = menuEntries.length - 1; i >= 0; i--) {
			MenuEntry entry = menuEntries[i];
			if (entry.getType().getId() == op_id && entry.getIdentifier() == id) {
				entry.setType(MenuAction.of(op_id + 2000));
				menuEntries[i] = menuEntries[menuEntries.length - 1];
				menuEntries[menuEntries.length - 1] = entry;
				this.client.setMenuEntries(menuEntries);
				break;
			}
		}
	}

	public void setWalkHerehotKeyPressed(boolean walkHerehotKeyPressed) {
		this.walkHerehotKeyPressed = walkHerehotKeyPressed;
	}

	public List<List<Actor>> getStacks() {
		List<List<Actor>> outerArrayList = new ArrayList<>();
		List<Actor> pileList = new ArrayList<>();
		for (NPC npc : this.client.getNpcs()) {
			if (npc != null && !npc.isDead() && npc.getName() != null) {
				String n = npc.getName().toLowerCase();
				if ((this.baRole == 1 || this.baRole == 2) && (n.contains("penance fighter") || n.contains("penance ranger"))) {
					pileList.add(npc);
					continue;
				}
				if (this.baRole == 3 && n.contains("penance runner")) {
					pileList.add(npc);
					continue;
				}
				if (this.baRole == 4 && n.contains("penance healer"))
					pileList.add(npc);
			}
		}
		if (pileList.size() == 0)
			return null;
		for (Actor actor : pileList) {
			ArrayList<Actor> potentialStackArrayList = new ArrayList<>();
			for (Actor actorToCompareTo : pileList) {
				if (!potentialStackArrayList.contains(actorToCompareTo) && actor.getWorldLocation().distanceTo(actorToCompareTo.getWorldLocation()) == 0)
					potentialStackArrayList.add(actorToCompareTo);
			}
			if (potentialStackArrayList.size() >= ((this.baRole == 3) ? 1 : 2))
				outerArrayList.add(potentialStackArrayList);
		}
		return (outerArrayList.size() != 0) ? outerArrayList : null;
	}
}
