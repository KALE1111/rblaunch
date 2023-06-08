package net.runelite.client.plugins.toa.Akkha;

import net.runelite.client.plugins.toa.Util.AttackStyle;
import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Room;
import net.runelite.client.plugins.toa.ToaPlugin;
import net.runelite.client.plugins.toa.Util.Prayer;
import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.Hooks;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import net.runelite.client.plugins.toa.reflectMeth;
import net.runelite.client.plugins.toa.reflectPackMeth;

public class Akkha extends Room {

    public static final int ORB_ID = 11804;

    @Inject
    private Client client;

    @Inject
    private Hooks hooks;

    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    @Inject
    private AkkhaOverlay akkhaOverlay;

    @Getter
    private boolean akkhaActive;

    @Getter
	private NPC lastInteracted;

    @Getter
	public NPC AkkhaNPC;

    @Getter
	public int AkkhaTicks = -1;



    @Getter
    private int memorizeElementsLength;

    private Prayer currentPray;

    @Getter
    private LinkedHashSet<MemorizingTile> memorizingSequence = new LinkedHashSet<>();

    @Inject
    protected Akkha(ToaPlugin plugin, ToaConfig config)
	{
		super(plugin, config);
    }

    @Override
    public void init(){
        memorizeElementsLength = 4;
        memorizingSequence = new LinkedHashSet<>();
    }

	public reflectMeth packet;

	@Override
	public void setpackMeth(reflectMeth type){
		this.packet = type;
	}

    @Override
    public void load()
    {
		currentPray = null;
        overlayManager.add(akkhaOverlay);
        hooks.registerRenderableDrawListener(drawListener);
    }

    @Override
    public void unload()
    {
    	currentPray = null;
        overlayManager.remove(akkhaOverlay);
        hooks.unregisterRenderableDrawListener(drawListener);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inRoomRegion(ToaPlugin.AKKHA_REGION)){
            akkhaActive = false;
            return;
        }
        //automation goes here
		if(packet != null)
		{


					for (NPC npc : client.getNpcs())
					{
						switch (npc.getId())
						{
							case 11792:
								handlePray(Prayer.PROTECT_FROM_MAGIC,npc, net.runelite.api.Prayer.PROTECT_FROM_MAGIC);//mage
								//packet.toggleNormalPrayer(Prayer.RIGOUR);
								//if(config.AkkhaSwitchItems()){plugin.itemSwitch(config.rangeItems());}
								AkkhaNPC = npc;
								break;
							case 11790:

								handlePray(Prayer.PROTECT_FROM_MELEE,npc,net.runelite.api.Prayer.PROTECT_FROM_MELEE);//melee
								//if(config.AkkhaSwitchItems()){plugin.itemSwitch(config.mageItems());}
								//packet.toggleNormalPrayer(Prayer.AUGURY);
								AkkhaNPC = npc;
								break;
							case 11791:

								handlePray(Prayer.PROTECT_FROM_MISSILES,npc,net.runelite.api.Prayer.PROTECT_FROM_MISSILES);//range
								//if(config.AkkhaSwitchItems()){plugin.itemSwitch(config.meleeItems());}
								//packet.toggleNormalPrayer(Prayer.PIETY);
								AkkhaNPC = npc;
								break;
						}
					}
			}

        akkhaActive = true;

        LinkedHashSet<MemorizingTile> tempTiles = new LinkedHashSet<>();
        for (MemorizingTile tile : memorizingSequence){
            if ((tile.getTicksLeft()-1)>0){
                tempTiles.add(new MemorizingTile(tile.getPoint(), tile.getTicksLeft()-1));
            }
        }
        memorizingSequence = tempTiles;
		if (this.AkkhaNPC != null){

			AkkhaTicks = Math.max(0, AkkhaTicks - 1);
			if (AkkhaNPC.getAnimation() > -1 && AkkhaTicks < 3 ){
				AkkhaTicks = 6;
			}


			//log.info("tick Happened");
		}
    }

    private void handlePray(Prayer utilstyle, NPC currNPC, net.runelite.api.Prayer styles)
	{
		if ((packet.packetsInstalled) && (false))//config.warning() && config.AkkhaprayFlick() && plugin.revs))
		{
			if (currNPC == AkkhaNPC || AkkhaNPC == null)
			{
				if (!client.isPrayerActive(styles) && AkkhaTicks <= 2)
				{
					packet.toggleNormalPrayer(utilstyle.getWidgetInfo().getPackedId());
					//currentPray = style;
				}
				else if (AkkhaTicks >= 2 && client.isPrayerActive(styles) && AkkhaTicks < 6)
				{
					packet.toggleNormalPrayer(utilstyle.getWidgetInfo().getPackedId());
				}
			}
			else
			{
				switch (AkkhaNPC.getId())
				{
					case 11792:

						handlePray(Prayer.PROTECT_FROM_MAGIC,net.runelite.api.Prayer.PROTECT_FROM_MAGIC);//mage

						break;
					case 11790:

						handlePray(Prayer.PROTECT_FROM_MELEE,net.runelite.api.Prayer.PROTECT_FROM_MELEE);//melee

						break;
					case 11791:

						handlePray(Prayer.PROTECT_FROM_MISSILES,net.runelite.api.Prayer.PROTECT_FROM_MISSILES);//range

						break;
				}
			}
		}
	}

	private void handlePray(Prayer style, net.runelite.api.Prayer styles){
		if (!client.isPrayerActive(styles) && AkkhaTicks <= 0 )
		{
			packet.toggleNormalPrayer(style.getWidgetInfo().getPackedId());
			//currentPray = style;
		}
		else if (AkkhaTicks >= 0 && client.isPrayerActive(styles) && AkkhaTicks < 5)
		{
			packet.toggleNormalPrayer(style.getWidgetInfo().getPackedId());
		}
	}

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == 14378){
            if (event.getValue() >= 4){
                memorizeElementsLength = 6;
            } else if (event.getValue() >= 2){
                memorizeElementsLength = 5;
            } else {
                memorizeElementsLength = 4;
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event){
        GameObject object = event.getGameObject();
        switch (object.getId()){
            case 45869:
            case 45871:
            case 45870:
            case 45868:
                memorizingSequence.add(new MemorizingTile(object.getWorldLocation(), 2*memorizeElementsLength-1));
                break;
        }
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI)
    {
        if (renderable instanceof NPC)
        {
            NPC npc = (NPC) renderable;
            if (npc.getId() == ORB_ID && config.hideOrbs()){
                return false;
            }
        }
        return true;
    }

}
