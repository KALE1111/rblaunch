package net.runelite.client.plugins.socketba;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
//import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.socketba.jacob.GroundItem;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.TextComponent;

public class AzEasyScapeOverlay extends Overlay {
	private final TextComponent textComponent = new TextComponent();

	private final AzEasyScapeConfig config;

	private final AzEasyScapePlugin plugin;

	private final Client client;

	private String eggColour;

	public String getEggColour() {
		return this.eggColour;
	}

	public void setEggColour(String eggColour) {
		this.eggColour = eggColour;
	}

	@Inject
	private AzEasyScapeOverlay(Client client, AzEasyScapePlugin plugin, AzEasyScapeConfig config) {
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "B.A. overlay"));
	}

	public Dimension render(Graphics2D graphics) {
		if (this.client.getGameState() == GameState.LOGGED_IN) {
			Player player = this.client.getLocalPlayer();
			if (player == null || this.plugin.getInGameBit() != 1)
				return null;
			if (this.config.truetile()) {
				WorldPoint playerPos = this.client.getLocalPlayer().getWorldLocation();
				if (playerPos != null) {
					LocalPoint playerPosLocal = LocalPoint.fromWorld(this.client, this.client.getLocalPlayer().getWorldLocation());
					if (playerPosLocal != null) {
						Polygon poly = Perspective.getCanvasTilePoly(this.client, playerPosLocal);
						if (poly != null)
							OverlayUtil.renderPolygon(graphics, poly, this.config.trueCOl());
					}
				}
			}
			if (this.config.runnerStack()) {
				List<List<Actor>> stackList = this.plugin.getStacks();
				if (stackList != null)
					for (List<Actor> actorArrayList : stackList) {
						try {
							Actor actorToRender = actorArrayList.get(0);
							String text = "" + actorArrayList.size();
							OverlayUtil.renderTextLocation(graphics, actorToRender.getCanvasTextLocation(graphics, text, 40), text, Color.WHITE);
						} catch (Exception exception) {}
					}
			}
			if (this.plugin.getBaRole() != 2)
				return null;
			Collection<GroundItem> groundItemList = this.plugin.getCollectedGroundItems().values();
			LocalPoint localLocation = player.getLocalLocation();
			Iterator<GroundItem> var5 = groundItemList.iterator();
			while (var5.hasNext()) {
				GroundItem item = var5.next();
				if ((this.eggColour != null && item.getName().toLowerCase().equals(this.eggColour + " egg")) || item.getName().toLowerCase().contains("yellow egg")) {
					Color colorEgg = Color.YELLOW;
					String var8 = this.eggColour;
					byte var9 = -1;
					if (item.getName().toLowerCase().contains("yellow egg")) {
						var9 = 3;
					} else {
						switch (var8.hashCode()) {
							case 112785:
								if (var8.equals("red"))
									var9 = 0;
								break;
							case 3027034:
								if (var8.equals("blue"))
									var9 = 2;
								break;
							case 98619139:
								if (var8.equals("green"))
									var9 = 1;
								break;
						}
					}
					switch (var9) {
						case 0:
							colorEgg = Color.RED;
							break;
						case 1:
							colorEgg = Color.GREEN;
							break;
						case 2:
							colorEgg = Color.CYAN.darker();
							break;
						case 3:
							colorEgg = Color.YELLOW;
							break;
					}
					if (colorEgg == null)
						colorEgg = Color.YELLOW;
					LocalPoint groundPoint = LocalPoint.fromWorld(this.client, item.getLocation());
					if (groundPoint != null && localLocation.distanceTo(groundPoint) <= 2500) {
						Polygon poly = Perspective.getCanvasTilePoly(this.client, groundPoint);
						if (poly != null)
							OverlayUtil.renderPolygon(graphics, poly, colorEgg);
						net.runelite.api.Point textPoint = Perspective.getCanvasTextLocation(this.client, graphics, groundPoint, "" + item.getQuantity(), item.getHeight() + 2);
						if (textPoint != null && item != null) {
							this.textComponent.setText(item.getQuantity() + "");
							this.textComponent.setColor(colorEgg);
							this.textComponent.setPosition(new Point(textPoint.getX(), textPoint.getY()));
							this.textComponent.render(graphics);
						}
					}
				}
			}
			return null;
		}
		return null;
	}

	public int idxOf(NPC n) {
		for (int i = 0; i < this.plugin.getHealers().size(); i++) {
			if (this.plugin.getHealers().get(i) == n)
				return i + 1;
		}
		return -1;
	}
}
