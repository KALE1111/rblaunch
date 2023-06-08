package net.runelite.client.plugins.spoontob.rooms.Sotetseg;

import net.runelite.api.Client;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DeathBallPanel extends OverlayPanel {
    @Inject
    private  SpoonTobPlugin plugin;
    @Inject
    private  SpoonTobConfig config;
    @Inject
    private  Client client;
    @Inject
    private Sotetseg sote;

    @Inject
    public DeathBallPanel(SpoonTobPlugin plugin, SpoonTobConfig config, Client client, Sotetseg sote) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.sote = sote;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if((config.deathballInfobox() == SpoonTobConfig.soteDeathballMode.INFOBOX || config.deathballInfobox() == SpoonTobConfig.soteDeathballMode.BOTH)
                && sote.isSotetsegActive()) {
            Color color = Color.WHITE;
            int attacksLeft = sote.sotetsegAttacksLeft;
            if(attacksLeft > 0) {
                if (attacksLeft == 1) {
                    color = Color.RED;
                }
                this.panelComponent.getChildren().add(TitleComponent.builder()
                        .color(color)
                        .text(Integer.toString(attacksLeft))
                        .build());
            }else {
                BufferedImage img = ImageUtil.loadImageResource(SpoonTobPlugin.class, "NukeSprite.png");
                ImageComponent imgComp = new ImageComponent(img);
                this.panelComponent.getChildren().add(imgComp);
            }
            this.panelComponent.setPreferredSize(new Dimension(24, 24));
        }
        return super.render(graphics);
    }
}
