package net.runelite.client.plugins.toa.Prayer;

import net.runelite.client.plugins.toa.ToaConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.toa.Prayer.WidgetInfoExt;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;
import java.util.Queue;

@Slf4j
public abstract class PrayerOverlay extends Overlay
{
    private static final int TICK_PIXEL_SIZE = 60;
    private static final int BOX_WIDTH = 10;
    private static final int BOX_HEIGHT = 5;

    @Getter(AccessLevel.PROTECTED)
    private final ToaConfig config;
    private final Client client;

    @Inject
    protected PrayerOverlay(final Client client, final ToaConfig config)
    {
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGHEST);
    }

    protected abstract Queue<NextAttack> getAttackQueue();

    protected abstract long getLastTick();

    protected abstract boolean isEnabled();

    @Override
    public Dimension render(Graphics2D graphics)
    {

        final Widget meleePrayerWidget = client.getWidget(WidgetInfoExt.PRAYER_PROTECT_FROM_MELEE.getPackedId());
        final Widget rangePrayerWidget = client.getWidget(WidgetInfoExt.PRAYER_PROTECT_FROM_MISSILES.getPackedId());
        final Widget magicPrayerWidget = client.getWidget(WidgetInfoExt.PRAYER_PROTECT_FROM_MAGIC.getPackedId());

        boolean prayerWidgetHidden = meleePrayerWidget == null
                || rangePrayerWidget == null
                || magicPrayerWidget == null
                || meleePrayerWidget.isHidden()
                || rangePrayerWidget.isHidden()
                || magicPrayerWidget.isHidden();


        if ((config.prayerHelperToa() && isEnabled()) && (!prayerWidgetHidden && config.alwaysShowPrayerHelperToa()))
        {
            renderPrayerIconOverlay(graphics);

            if (config.descendingBoxesToa())
            {
                renderDescendingBoxes(graphics);
            }
        }

        return null;
    }


    private void renderDescendingBoxes(Graphics2D graphics)
    {
        Map<Integer, NextAttack> tickPriorityMap = NextAttack.getTickPriorityMap(getAttackQueue());

        getAttackQueue().forEach(attack -> {
            int tick = attack.getTicksUntil();
            final Color color = tick == 1 ? config.prayerColorDangerToa() : config.prayerColorToa();
            final Widget prayerWidget = client.getWidget(attack.getPrayer().getWidgetInfo().getPackedId());

            if (prayerWidget == null)
            {
                return;
            }

            int baseX = (int) prayerWidget.getBounds().getX();
            baseX += prayerWidget.getBounds().getWidth() / 2;
            baseX -= BOX_WIDTH / 2;

            int baseY = (int) prayerWidget.getBounds().getY() - tick * TICK_PIXEL_SIZE - BOX_HEIGHT;
            baseY += TICK_PIXEL_SIZE - ((getLastTick() + 600 - System.currentTimeMillis()) / 600.0 * TICK_PIXEL_SIZE);

            final Rectangle boxRectangle = new Rectangle(BOX_WIDTH, BOX_HEIGHT);
            boxRectangle.translate(baseX, baseY);

            if (attack.getPrayer().equals(tickPriorityMap.get(attack.getTicksUntil()).getPrayer()) && baseY < (int) prayerWidget.getBounds().getY() - BOX_HEIGHT)
            {
                OverlayUtil.renderPolygon(graphics, boxRectangle, color, color, new BasicStroke(2));
            }
            else if (config.indicateNonPriorityDescendingBoxesToa() && baseY < (int) prayerWidget.getBounds().getY() - BOX_HEIGHT)
            {
                OverlayUtil.renderPolygon(graphics, boxRectangle, color, new Color(0, 0, 0, 0), new BasicStroke(2));
            }
        });
    }

    private void renderPrayerIconOverlay(Graphics2D graphics)
    {
        NextAttack attack = getAttackQueue().peek();
        if (attack == null)
        {
            return;
        }

        if (!client.isPrayerActive(attack.getPrayer().getApiPrayer()))
        {
            final Widget prayerWidget = client.getWidget(attack.getPrayer().getWidgetInfo().getPackedId());
            if (prayerWidget == null)
            {
                return;
            }

            final Rectangle prayerRectangle = new Rectangle((int) prayerWidget.getBounds().getWidth(), (int) prayerWidget.getBounds().getHeight());
            prayerRectangle.translate((int) prayerWidget.getBounds().getX(), (int) prayerWidget.getBounds().getY());

            OverlayUtil.renderPolygon(graphics, prayerRectangle, config.prayerColorDangerToa());
        }
    }
}
