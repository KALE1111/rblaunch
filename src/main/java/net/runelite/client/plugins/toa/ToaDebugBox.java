package net.runelite.client.plugins.toa;

import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Util.Table.TableAlignment;
import net.runelite.client.plugins.toa.Util.Table.TableComponent;
import net.runelite.client.plugins.toa.Zebak.Zebak;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class ToaDebugBox extends Overlay
{
    private final Client client;
	private final Zebak zebak;
    private final ToaPlugin plugin;
    private final ToaConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    ToaDebugBox(Client client, ToaPlugin plugin, ToaConfig config, Zebak zebak)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.zebak = zebak;
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.setPriority(OverlayPriority.HIGH);
        this.panelComponent.setPreferredSize(new Dimension(270, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!this.config.toaDebug() || !plugin.inRoomRegion())
        {
            return null;
        }



        this.panelComponent.getChildren().clear();
        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);

        tableComponent.addRow("ticks", String.valueOf(client.getTickCount()));

        this.panelComponent.getChildren().add(tableComponent);

        return this.panelComponent.render(graphics);
    }
}
