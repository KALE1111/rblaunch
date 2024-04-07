package net.runelite.client.plugins.toa;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.toa.Akkha.Akkha;
import net.runelite.client.plugins.toa.Apmeken.Apmeken;
import net.runelite.client.plugins.toa.Baba.Baba;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.toa.Het.Het;
import net.runelite.client.plugins.toa.Kephri.Kephri;
import net.runelite.client.plugins.toa.Scarabas.Scarabas;
import net.runelite.client.plugins.toa.Warden.Warden;
import net.runelite.client.plugins.toa.Zebak.Zebak;
import lombok.AccessLevel;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.ui.ColorScheme;
import org.apache.commons.lang3.ArrayUtils;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.google.inject.Provides;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;

@PluginDescriptor(
	name = "<html><font color=#86C43F>[RB]</font> Tombs of Amascut</html>",
	enabledByDefault = false,
	description = "Overlays for Tombs",
	tags = {"bosses", "combat", "overlay", "pve", "pvm", "tombs", "RB"}
)

@Slf4j
@Singleton
public class ToaPlugin extends Plugin {

    public static final Integer AKKHA_REGION = 14676;
    public static final Integer CRONDIS_REGION = 15698;
    public static final Integer ZEBAK_REGION = 15700;
    public static final Integer SCABARAS_REGION = 14162;
    public static final Integer NEXUS_REGION = 14160;
    public static final Integer KEPHRI_REGION = 14164;
    public static final Integer APMEKEN_REGION = 15186;
    public static final Integer BABA_REGION = 15188;
    public static final Integer HET_REGION = 14674;
    public static final Integer WARDEN_P1_REGION = 15184;
    public static final Integer WARDEN_P2_REGION = 15696;

    private int[] regionIds = null;

    @Inject
    private Akkha akkha;

    @Inject
    private Het het;

    @Inject
    private Zebak zebak;

    @Inject
    private Kephri kephri;

    @Inject
    private Scarabas scarabas;

    @Inject
    private Baba baba;

    @Inject
    private Apmeken apmeken;

	@Inject
	private ToaConfig config;

	@Inject
	private Warden warden;

	@Inject
	private OverlayManager overlayManager;

    @Inject
    @Getter(AccessLevel.NONE)
    private ToaDebugBox toaDebugBox;

    @Inject
	private reflectPackMeth packetSM;

    @Inject
	private reflectPackMethETHAN packetETHAN;

    private reflectMeth packet;

    @Inject
    private Client client;

    private Room[] rooms = null;

    public boolean revs = false;


	@Provides
	ToaConfig provideConfig(ConfigManager configManager) {
		return (ToaConfig)configManager.getConfig(ToaConfig.class);
	}

    @Override
    public void startUp() {

        revs = true;
		if(client.getRevision() != 221){
			revs = false;
			JCheckBox checkbox = new JCheckBox();
			checkbox.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
			final int result = JOptionPane.showOptionDialog(checkbox, "REVS OUTDATED: Overlays Only For TOA Plugin",
				"REVS OUTDATED", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
				null, new String[]{"I Understand", "What?"}, "No");
		}


        if (rooms == null)
        {
            rooms = new Room[]{akkha, het, zebak, kephri, scarabas, baba, apmeken, warden};


            for (Room room : rooms)
            {
                log.info("Checking room");
                room.init();
                room.setpackMeth(packet);
            }
        }

        for (Room room : rooms)
        {
            room.load();
        }

        if (regionIds == null){
            regionIds = new int[]{AKKHA_REGION,CRONDIS_REGION, ZEBAK_REGION, SCABARAS_REGION, NEXUS_REGION, KEPHRI_REGION, APMEKEN_REGION, BABA_REGION, HET_REGION,  WARDEN_P1_REGION, WARDEN_P2_REGION};
        }
    }

    @Override
    public void shutDown() {
        this.overlayManager.remove(this.toaDebugBox);

        for (Room room : rooms)
        {
            room.unload();
        }
        //List<Prayer> test = new ArrayList<>();
		//test.add(0, Prayer.PROTECT_FROM_MELEE);
		//packet.toggleNormalPrayer(Prayer.PROTECT_FROM_MISSILES.getWidgetInfo().getPackedId());
        //packet.toggleNormalPrayers(test);
		//packet.toggleNormalPrayer(Prayer.PROTECT_FROM_MISSILES.getWidgetInfo().getPackedId());
		packet = null;
	}

	public void itemSwitch(String itemListStr){
		List<Integer> item = new ArrayList<Integer>();
		if(itemListStr != ""){
			String[] items = itemListStr.split(",");
			for(String itemStr:items){
				item.add(Integer.parseInt(itemStr));
			}
		}
		packet.equipItems(item);
	}

	@Subscribe
    public void onGameTick(GameTick event) {
        akkha.onGameTick(event);
        zebak.onGameTick(event);
        kephri.onGameTick(event);
        scarabas.onGameTick(event);
        baba.onGameTick(event);
        warden.onGameTick(event);

    }

	@Subscribe
    public void onVarbitChanged(final VarbitChanged event){
        akkha.onVarbitChanged(event);
        zebak.onVarbitChanged(event);
    }


	@Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObject)
    {
    	akkha.onGameObjectSpawned(gameObject);
    }

	@Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectC) {
        zebak.onGraphicsObjectCreated(graphicsObjectC);
        warden.onGraphicsObjectCreated(graphicsObjectC);
        kephri.onGraphicsObjectCreated(graphicsObjectC);
        baba.onGraphicsObjectCreated(graphicsObjectC);

    }

	@Subscribe
    public void onChatMessage(final ChatMessage event) {
        apmeken.onChatMessage(event);
    }

    public boolean inRoomRegion()
    {
        for (int regionId : regionIds) {
            if (ArrayUtils.contains(client.getMapRegions(), regionId))
                return true;
        }
        return false;
    }


}