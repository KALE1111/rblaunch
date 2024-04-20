package net.runelite.client.plugins.toa.Warden;

import java.util.LinkedHashSet;

import com.example.InteractionApi.PrayerInteraction;
import net.runelite.api.*;
import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Prayer.NextAttack;
import net.runelite.client.plugins.toa.Room;
import net.runelite.client.plugins.toa.ToaPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.eventbus.Subscribe;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import net.runelite.client.plugins.toa.reflectMeth;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;


@Slf4j
public class Warden extends Room {
	@Inject
	private Client client;

	@Inject
	private WardenOverlay WardenOverlay;

	@Inject
	private WardenOverlayBox WardenOverlayBox;

	@Inject
	private WardenPrayerOverlay WardenPrayerOverlay;

	@Inject
	private WardenPrayerBoxOverlay WardenPrayerBoxOverlay;



	@Getter
	private WardenPosition currentSide = WardenPosition.UNDEFINED;

	@Inject
	protected Warden(ToaPlugin plugin, ToaConfig config)
	{
		super(plugin, config);
	}


	@Getter(AccessLevel.PUBLIC)
	Queue<NextAttack> nextAttackQueue = new PriorityQueue<>();

	@Getter(AccessLevel.PACKAGE)
	private long lastTick;

	@Override
	public void init(){


	}

	private int timeTillNext = 0;
	private int lastAnim;

	private NextAttack lastattack = null;

	private NPC wardenp2;
	@Getter
	private NPC wardenp3;
	private NPC zebakGhost;

	@Getter(AccessLevel.PACKAGE)
	private final List<GameObject> objects = new ArrayList<>();

	@Getter
	private LinkedHashSet<WardenDangerTile> WardenDangerTiles = new LinkedHashSet<>();

	public reflectMeth packet;

	@Override
	public void setpackMeth(reflectMeth type){
		this.packet = type;
	}

	@Override
	public void load()
	{
		overlayManager.add(WardenOverlay);
		overlayManager.add(WardenOverlayBox);
		overlayManager.add(WardenPrayerOverlay);
		overlayManager.add(WardenPrayerBoxOverlay);
	}

	@Override
	public void unload()
	{
		overlayManager.remove(WardenOverlay);
		overlayManager.remove(WardenOverlayBox);
		overlayManager.remove(WardenPrayerOverlay);
		overlayManager.remove(WardenPrayerBoxOverlay);
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event){
		GraphicsObject object = event.getGraphicsObject();
		if (!inRoomRegion(ToaPlugin.WARDEN_P1_REGION) || !inRoomRegion(ToaPlugin.WARDEN_P2_REGION))
		{
			return;
		}
		switch (object.getId()){
			case 2251:
				WardenDangerTiles.add(new WardenDangerTile(object.getLocation(), 5, object.getId()));
				break;
			case 2250:
				//log.info("here");
				WardenDangerTiles.add(new WardenDangerTile(object.getLocation(), 6, object.getId()));
				break;
			case 1446:
			case 1447:
				//log.info("here");
				WardenDangerTiles.add(new WardenDangerTile(object.getLocation(), 4, object.getId()));
				break;
		}
	}


	@Subscribe
	public void onGameTick(GameTick event) {
		if (!inRoomRegion(ToaPlugin.WARDEN_P1_REGION) || !inRoomRegion(ToaPlugin.WARDEN_P2_REGION)){
			wardenp2 = null;//if no Npcs were found/set
			wardenp3 = null;
			zebakGhost = null;
			return;
		}//TODO make this work?

		LinkedHashSet<WardenDangerTile> tempTiles = new LinkedHashSet<>();
		for (WardenDangerTile tile : WardenDangerTiles){
			if ((tile.getTicksLeft()-1)>=0){
				tempTiles.add(new WardenDangerTile(tile.getLpoint(), tile.getTicksLeft()-1, tile.getId()));
			}
		}

		WardenDangerTiles = tempTiles;

		if (client.getNpcs() != null)
		{
			for (NPC wardens : client.getNpcs())
			{
				if (wardens.getId() == 11753 || wardens.getId() == 11754 || wardens.getId() == 11757 || wardens.getId() == 11756)
				{
					this.wardenp2 = wardens;
				}
				else if ((wardens.getId() == 11762 && wardenp3 == null)|| (wardens.getId() == 11763 && wardenp3 == null)){
					this.wardenp3 = wardens;
				}
				else if(wardens.getId() == 11774){
					this.zebakGhost = wardens;
				}
			}
		}
		if(wardenp3 != null && wardenp3.getAnimation() == 9685){
			//wardenp3 = null;//TODO fix this a better way
		}

		//log.info("p3 {}",wardenp3);
		//log.info("p2 {}",wardenp2);
		//log.info("ze {}",zebakGhost);

		//log.info(String.valueOf(currentSide.getText()));



		if (this.wardenp2 != null || this.wardenp3 != null){
			lastTick = System.currentTimeMillis();
			//log.info("here");
			NextAttack.updateNextPrayerQueue(getNextAttackQueue());
			if(zebakGhost != null)
			{
				WardenCheckForNewHit(true, true,false);
			}
			else if(this.wardenp3 != null){
				//log.info("her2");
				this.wardenp2 = null;
				WardenCheckForNewHit(false, true, false);
			}
			else{
				WardenCheckForNewHit(false, false, true);
			}
		}


		//Automation Happens Here
		if(true)
		{
			if ((nextAttackQueue.peek() != null) && (config.warning() && config.wardenprayFlick() && plugin.revs))
			{
				NextAttack attack = nextAttackQueue.peek();
				if (attack.getTicksUntil() <= config.tickstowait() && !client.isPrayerActive(attack.getPrayer().getApiPrayer())&& client.getLocalPlayer().getAnimation() != 5538)
				{// if 2t till next attack and not prayer correctly
					String str = "";
					if (attack.getPrayer() == com.example.RuneBotApi.Prayer.PROTECT_FROM_MELEE)
					{
						str = attack.getPrayer().name();
						lastattack = attack;
						PrayerInteraction.flickPrayers(Prayer.PROTECT_FROM_MELEE);
					}
					if (attack.getPrayer() == com.example.RuneBotApi.Prayer.PROTECT_FROM_MAGIC)
					{
						str = attack.getPrayer().name();
						lastattack = attack;
						PrayerInteraction.flickPrayers(Prayer.PROTECT_FROM_MAGIC);

					}
					if (attack.getPrayer() == com.example.RuneBotApi.Prayer.PROTECT_FROM_MISSILES)
					{
						str = attack.getPrayer().name();
						lastattack = attack;
						PrayerInteraction.flickPrayers(Prayer.PROTECT_FROM_MISSILES);
					}
					log.info(str);
				}
				else if ((attack.getTicksUntil() > config.tickstowait() && client.isPrayerActive(lastattack.getPrayer().getApiPrayer())) && config.flickPrayer())
				{//turns prayer off for flicking
					String str = "";
					if (lastattack.getPrayer() == com.example.RuneBotApi.Prayer.PROTECT_FROM_MELEE)
					{
						str = attack.getPrayer().name();
						PrayerInteraction.flickPrayers(Prayer.PROTECT_FROM_MELEE);
					}
					if (lastattack.getPrayer() == com.example.RuneBotApi.Prayer.PROTECT_FROM_MAGIC)
					{
						str = attack.getPrayer().name();
						PrayerInteraction.flickPrayers(Prayer.PROTECT_FROM_MAGIC);

					}
					if (lastattack.getPrayer() == com.example.RuneBotApi.Prayer.PROTECT_FROM_MISSILES)
					{
						str = attack.getPrayer().name();
						PrayerInteraction.flickPrayers(Prayer.PROTECT_FROM_MISSILES);
					}
					log.info(str);
				}
			}
		}


		//wardenActive = true;
		//Update attack queue
		/*
        lastTick = System.currentTimeMillis();
        NextAttack.updateNextPrayerQueue(getNextAttackQueue());

        for (NPC npc : client.getNpcs()){
            if (npc != null){
                int decrease = (int)Math.floor(pathLevel/2);
                if (decrease > 2)
                    decrease = 2;
                if (npc.getId() == ZEBAK_ID){
                    tickInterval = 7 - decrease;
                } else if (npc.getId() == ZEBAK_P2_ID){
                    tickInterval = 4 - decrease;
                }
            }
        }

        if (canAttackTicks > 0)
            canAttackTicks = canAttackTicks - 1;
*/
		//;
	}





	private void WardenCheckForNewHit(boolean ZebakSpawned, boolean phase3, boolean phase2){

		if(ZebakSpawned){
			if (this.zebakGhost.getAnimation() == 9626){
				for (Projectile p : client.getProjectiles()) {
					//Check if new projectile spawned and 4 ticks or more
					//log.info(String.valueOf(p.getRemainingCycles()));
					if (p.getRemainingCycles() > 110) {
						switch(p.getId()){
							case 2177:
							case 2176:
								nextAttackQueue.add(new NextAttack(7, com.example.RuneBotApi.Prayer.PROTECT_FROM_MAGIC, 1 ));
								break;
							case 2179:
							case 2178:
								nextAttackQueue.add(new NextAttack(7, com.example.RuneBotApi.Prayer.PROTECT_FROM_MISSILES, 1 ));
								break;
						}
					}
				}
			}
		}
		if(phase3)
		{
			if(timeTillNext == 0 || lastAnim != wardenp3.getAnimation())
			{
				if(lastAnim != wardenp3.getAnimation()){
					timeTillNext = 1;
					lastAnim = wardenp3.getAnimation();
				}
				//tracks side in p3
				else if (wardenp3.getAnimation() == 9677 || wardenp3.getAnimation() == 9676)
				{
					this.currentSide = WardenPosition.MIDDLE;
				}
				else if (wardenp3.getAnimation() == 9679 || wardenp3.getAnimation() == 9678)
				{
					this.currentSide = WardenPosition.RIGHT;
				}
				else if (wardenp3.getAnimation() == 9675 || wardenp3.getAnimation() == 9674)
				{
					this.currentSide = WardenPosition.LEFT;
				}
			}
			timeTillNext--;
		}

		//P2 Logic for prayers
		if(wardenp2 != null)
		{
			if ((wardenp2.getAnimation() == 9660 || wardenp2.getAnimation() == 9661))
			{
				//log.info("Warden Attacked");
				for (Projectile p : client.getProjectiles())
				{
					//Check if new projectile spawned and 4 ticks or more
					//log.info(String.valueOf(p.getId()));
					if (p.getRemainingCycles() > 80)
					{
						switch (p.getId())
						{

							case 2204://special melee
								if (p.getRemainingCycles() > 110)
								{
									nextAttackQueue.add(new NextAttack(4, com.example.RuneBotApi.Prayer.PROTECT_FROM_MELEE, 1));
								}
								break;
							case 2206://special range
								if (p.getRemainingCycles() > 110)
								{
									//log.info("added special {}", p.getRemainingCycles());
									nextAttackQueue.add(new NextAttack(4, com.example.RuneBotApi.Prayer.PROTECT_FROM_MISSILES, 1));
								}

								break;
							case 2208://special mage\
								if (p.getRemainingCycles() > 110)
								{
									//log.info("added special {}", p.getRemainingCycles());
									nextAttackQueue.add(new NextAttack(4, com.example.RuneBotApi.Prayer.PROTECT_FROM_MAGIC, 1));
								}

								break;
							case 2224://normal mage
								nextAttackQueue.add(new NextAttack(3, com.example.RuneBotApi.Prayer.PROTECT_FROM_MAGIC, 1));
								break;
							case 2241://normal range
								nextAttackQueue.add(new NextAttack(3, com.example.RuneBotApi.Prayer.PROTECT_FROM_MISSILES, 1));
								break;
						}
					}

				}
			}
		}
	}
}