package net.runelite.client.plugins.toa.Zebak;

import net.runelite.client.plugins.toa.ToaConfig;
import net.runelite.client.plugins.toa.Util.Prayer;
import net.runelite.client.plugins.toa.Prayer.NextAttack;
import net.runelite.client.plugins.toa.Room;
import net.runelite.client.plugins.toa.ToaPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import net.runelite.client.plugins.toa.reflectMeth;
import net.runelite.client.plugins.toa.reflectPackMeth;

@Slf4j
public class Zebak extends Room {
    @Inject
    private Client client;

    @Inject
    private ZebakOverlay zebakOverlay;

    @Inject
    private ZebakPrayerOverlay zebakPrayerOverlay;

    @Inject
    private ZebakPrayerBoxOverlay zebakPrayerBoxOverlay;

	public reflectMeth packet;



    @Getter
    private boolean zebakActive;

    private int pathLevel;

    @Inject
    protected Zebak(ToaPlugin plugin, ToaConfig config)
	{
		super(plugin, config);
	}

    public static final int ZEBAK_ID = 11730;
    public static final int ZEBAK_P2_ID = 11732;
    public static final int ZEBAK_BLOOD_BARRAGE = 377;
    public static final int ZEBAK_ATTACK_ANIMATION = 9624;
    public static final int ZEBAK_ATTACK_P2_ANIMATION = 9626;
    public static final int RANGED_ATTACK_P1 = 2178;
    public static final int RANGED_ATTACK_P1_QUICK = 2179;
    public static final int RANGED_ATTACK_P2 = 2187;
    public static final int MAGIC_ATTACK_P1 = 2176;
    public static final int MAGIC_ATTACK_P1_QUICK = 2177;
    public static final int MAGIC_ATTACK_P2 = 2181;

    @Getter(AccessLevel.PUBLIC)
    Queue<NextAttack> nextAttackQueue = new PriorityQueue<>();

    @Getter(AccessLevel.PACKAGE)
    private long lastTick;

    @Override
    public void init(){
        canAttackTicks = 0;
		tickInterval = 7;
        pathLevel = 0;
    }

    private int tickInterval;
    private int canAttackTicks;

    private NextAttack lastattack = null;

    @Getter(AccessLevel.PACKAGE)
    private final List<GameObject> objects = new ArrayList<>();

    @Override
    public void load()
    {
		lastattack = null;
        overlayManager.add(zebakOverlay);
        overlayManager.add(zebakPrayerOverlay);
        overlayManager.add(zebakPrayerBoxOverlay);
    }
	@Override
	public void setpackMeth(reflectMeth type){
		this.packet = type;
	}

    @Override
    public void unload()
    {
		lastattack = null;
        overlayManager.remove(zebakOverlay);
        overlayManager.remove(zebakPrayerOverlay);
        overlayManager.remove(zebakPrayerBoxOverlay);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inRoomRegion(ToaPlugin.ZEBAK_REGION)){
            zebakActive = false;
            return;
        }
        zebakActive = true;
        //Update attack queue
        lastTick = System.currentTimeMillis();
        NextAttack.updateNextPrayerQueue(getNextAttackQueue());

        //Automation Happens Here
		if(packet != null)
		{
			if ((packet.packetsInstalled && nextAttackQueue.peek() != null) && (false))//config.warning() && config.zebakprayFlick() && plugin.revs))
			{
				NextAttack attack = nextAttackQueue.peek();
				if ((attack.getTicksUntil() <= 3 && !client.isPrayerActive(attack.getPrayer().getApiPrayer())) && client.getLocalPlayer().getAnimation() != 5538)
				{// if 2t till next attack and not prayer correctly
					String str = "";
					if (attack.getPrayer() == Prayer.PROTECT_FROM_MELEE)
					{
						str = attack.getPrayer().getApiPrayer().name();
						lastattack = attack;
						packet.toggleNormalPrayer(Prayer.PROTECT_FROM_MELEE.getWidgetInfo().getPackedId());
					}
					if (attack.getPrayer() == Prayer.PROTECT_FROM_MAGIC)
					{
						str = attack.getPrayer().getApiPrayer().name();
						lastattack = attack;
						packet.toggleNormalPrayer(Prayer.PROTECT_FROM_MAGIC.getWidgetInfo().getPackedId());

					}
					if (attack.getPrayer() == Prayer.PROTECT_FROM_MISSILES)
					{
						str = attack.getPrayer().getApiPrayer().name();
						lastattack = attack;
						packet.toggleNormalPrayer(Prayer.PROTECT_FROM_MISSILES.getWidgetInfo().getPackedId());
					}
					log.info(str);
				}
				else if ((attack.getTicksUntil() > 3 && client.isPrayerActive(lastattack.getPrayer().getApiPrayer()))&& false)
				{//turns prayer off for flicking
					String str = "";
					if (lastattack.getPrayer() == Prayer.PROTECT_FROM_MELEE)
					{
						str = attack.getPrayer().getApiPrayer().name();
						packet.toggleNormalPrayer(Prayer.PROTECT_FROM_MELEE.getWidgetInfo().getPackedId());
					}
					if (lastattack.getPrayer() == Prayer.PROTECT_FROM_MAGIC)
					{
						str = attack.getPrayer().getApiPrayer().name();
						packet.toggleNormalPrayer(Prayer.PROTECT_FROM_MAGIC.getWidgetInfo().getPackedId());

					}
					if (lastattack.getPrayer() == Prayer.PROTECT_FROM_MISSILES)
					{
						str = attack.getPrayer().getApiPrayer().name();
						packet.toggleNormalPrayer(Prayer.PROTECT_FROM_MISSILES.getWidgetInfo().getPackedId());
					}
					log.info(str);
				}
			}
		}

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

        zebakCheckForNewHit();
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectC) {
        if (graphicsObjectC.getGraphicsObject().getId() == ZEBAK_BLOOD_BARRAGE && zebakActive){
            nextAttackQueue.add(new NextAttack(4, Prayer.PROTECT_FROM_MAGIC, 2));
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == 14376){
            pathLevel = event.getVarbitId();
        }
    }


    private void zebakCheckForNewHit(){
        NPC zebak = null;

        for (NPC npc : client.getNpcs()){
            if (npc != null){
                if (npc.getId() == ZEBAK_ID || npc.getId() == ZEBAK_P2_ID){
                    zebak = npc;
                }
            }
        }

        //Check if Zebak exists
        if (zebak == null)
            return;

        //Possible new hit
        if ((zebak.getAnimation() == ZEBAK_ATTACK_ANIMATION || zebak.getAnimation() == ZEBAK_ATTACK_P2_ANIMATION) && canAttackTicks == 0){
            for (Projectile p : client.getProjectiles()) {
                //Check if new projectile spawned and 4 ticks or more
                if (p.getRemainingCycles() > 90) {
                    switch(p.getId()){
                        case MAGIC_ATTACK_P1:
                        case MAGIC_ATTACK_P1_QUICK:
                            nextAttackQueue.add(new NextAttack(7, Prayer.PROTECT_FROM_MAGIC, 1 ));
                            canAttackTicks = tickInterval;
                            break;
                        case RANGED_ATTACK_P1:
                        case RANGED_ATTACK_P1_QUICK:
                            nextAttackQueue.add(new NextAttack(7, Prayer.PROTECT_FROM_MISSILES, 1 ));
                            canAttackTicks = tickInterval;
                            break;
                    }
                }
            }
        }
    }
}
