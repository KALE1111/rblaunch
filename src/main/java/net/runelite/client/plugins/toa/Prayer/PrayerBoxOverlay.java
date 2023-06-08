package net.runelite.client.plugins.toa.Prayer;

import net.runelite.client.plugins.toa.ToaConfig;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.Queue;

import static net.runelite.client.ui.overlay.components.ComponentConstants.STANDARD_BACKGROUND_COLOR;

public abstract class PrayerBoxOverlay extends Overlay {

    public static final int IMAGE_SIZE = 36;

    private static final String INFO_BOX_TEXT_PADDING = "        ";
    private static final Dimension INFO_BOX_DIMENSION = new Dimension(40, 40);

    private static final PanelComponent panelComponent = new PanelComponent();
    private static final InfoBoxComponent prayerComponent = new InfoBoxComponent();


    static
    {
        panelComponent.setOrientation(ComponentOrientation.VERTICAL);
        panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
        panelComponent.setPreferredSize(new Dimension(40, 0));

        prayerComponent.setPreferredSize(INFO_BOX_DIMENSION);
    }


    @Getter(AccessLevel.PROTECTED)
    private final ToaConfig config;
    private final Client client;
    private final SpriteManager spriteManager;

    @Inject
    protected PrayerBoxOverlay(final Client client, final ToaConfig config, final SpriteManager spriteManager)
    {
        this.client = client;
        this.config = config;
        this.spriteManager = spriteManager;

        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    protected abstract Queue<NextAttack> getAttackQueue();

    protected abstract long getLastTick();

    protected abstract boolean isEnabled();

    @Override
    public Dimension render(Graphics2D graphics)
    {
        clearPanelComponent();

        if (config.prayerHelperToa() && isEnabled())
        {
            updatePrayerComponent();
        }

        return panelComponent.render(graphics);
    }

    private void clearPanelComponent()
    {
        final List<LayoutableRenderableEntity> children = panelComponent.getChildren();

        if (!children.isEmpty())
        {
            children.clear();
        }
    }

    private void updatePrayerComponent()
    {
        NextAttack attack = getAttackQueue().peek();

        if (attack != null){
            prayerComponent.setBackgroundColor(!client.isPrayerActive(attack.getPrayer().getApiPrayer()) ? config.dangerTileColorToa() : STANDARD_BACKGROUND_COLOR);
            prayerComponent.setImage(attack.getImage(spriteManager));
            panelComponent.getChildren().add(prayerComponent);
        }
    }
}
