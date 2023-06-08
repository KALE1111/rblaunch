package net.runelite.client.plugins.spoontob.rooms.Verzik;

import net.runelite.api.Client;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class GreenBallPanel extends OverlayPanel {
    private SpoonTobPlugin plugin;

    private SpoonTobConfig config;

    private Client client;

    private Verzik verzik;

    @Inject
    public GreenBallPanel(SpoonTobPlugin plugin, SpoonTobConfig config, Client client, Verzik verzik) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.verzik = verzik;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if(config.greenBouncePanel() != SpoonTobConfig.greenBouncePanelMode.OFF && this.verzik.isVerzikActive() && this.verzik.getVerzikPhase() == Verzik.Phase.PHASE3 && this.verzik.greenBallOut && verzik.getVerzikNPC() != null) {
            String leftText = "";
            String rightText = "";
            Color rightColor = Color.WHITE;
            if(config.greenBouncePanel() == SpoonTobConfig.greenBouncePanelMode.BOUNCES){
                leftText = "Bounces:";
                rightText = Integer.toString(verzik.greenBallBounces);
                this.panelComponent.setPreferredSize(new Dimension(95, 24));
            }else if(config.greenBouncePanel() == SpoonTobConfig.greenBouncePanelMode.DAMAGE){
                leftText = "Damage:";
                if(verzik.getVerzikNPC().getId() == 10852){
                    rightText = Double.toString(99 - ((verzik.greenBallBounces * .25) * 99));
                }else{
                    if(verzik.greenBallBounces == 0){
                        rightText = Integer.toString(74);
                    }else {
                        rightText = Double.toString(74 - ((verzik.greenBallBounces * .25) * 74));
                    }
                }
                this.panelComponent.setPreferredSize(new Dimension(90, 24));
            }else {
                leftText = "Bounces(Dmg):";
                if(verzik.getVerzikNPC().getId() == 10852){
                    rightText = verzik.greenBallBounces + "(" + Math.floor(99 - ((verzik.greenBallBounces * .25) * 99)) + ")";
                }else{
                    if(verzik.greenBallBounces == 0){
                        rightText = verzik.greenBallBounces + "(74)";
                    }else {
                        rightText = verzik.greenBallBounces + "(" + Math.floor(74 - ((verzik.greenBallBounces * .25) * 74)) + ")";
                    }
                }
                this.panelComponent.setPreferredSize(new Dimension(130, 24));
            }

            if(verzik.greenBallBounces == 0 && verzik.getVerzikNPC().getId() == 10852) {
                rightColor = Color.RED;
                rightText = "Death";
            }

            this.panelComponent.getChildren().add(LineComponent.builder()
                    .left(leftText)
                    .rightColor(rightColor)
                    .right(rightText)
                    .build());
        }
        return super.render(graphics);
    }
}
