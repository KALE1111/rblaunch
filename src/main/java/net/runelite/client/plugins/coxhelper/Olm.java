package net.runelite.client.plugins.coxhelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.DynamicObject;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Prayer;
import net.runelite.api.coords.WorldPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Olm {
   private static final Logger log = LoggerFactory.getLogger(Olm.class);
   public static final int HEAD_GAMEOBJECT_RISING = 29880;
   public static final int HEAD_GAMEOBJECT_READY = 29881;
   public static final int LEFT_HAND_GAMEOBJECT_RISING = 29883;
   public static final int LEFT_HAND_GAMEOBJECT_READY = 29884;
   public static final int RIGHT_HAND_GAMEOBJECT_RISING = 29886;
   public static final int RIGHT_HAND_GAMEOBJECT_READY = 29887;
   private final Client client;
   private final CoxPlugin plugin;
   private final CoxConfig config;
   private final List<WorldPoint> healPools = new ArrayList();
   private final List<WorldPoint> portals = new ArrayList();
   private final Set<Victim> victims = new HashSet();
   private int portalTicks = 10;
   private boolean active = false;
   private boolean firstPhase = false;
   private boolean finalPhase = false;
   private PhaseType phaseType;
   private GameObject hand;
   private OlmAnimation handAnimation;
   private GameObject head;
   private OlmAnimation headAnimation;
   private int ticksUntilNextAttack;
   private int attackCycle;
   private int specialCycle;
   private boolean crippled;
   private int crippleTicks;
   private Prayer prayer;
   private long lastPrayTime;

   @Inject
   private Olm(Client client, CoxPlugin plugin, CoxConfig config) {
      this.phaseType = PhaseType.UNKNOWN;
      this.hand = null;
      this.handAnimation = OlmAnimation.UNKNOWN;
      this.head = null;
      this.headAnimation = OlmAnimation.UNKNOWN;
      this.ticksUntilNextAttack = -1;
      this.attackCycle = 1;
      this.specialCycle = 1;
      this.crippled = false;
      this.crippleTicks = 45;
      this.prayer = null;
      this.lastPrayTime = 0L;
      this.client = client;
      this.plugin = plugin;
      this.config = config;
   }

   public void startPhase() {
      this.firstPhase = !this.active;
      this.active = true;
      this.ticksUntilNextAttack = -1;
      this.attackCycle = 1;
      this.specialCycle = 1;
      this.crippled = false;
      this.crippleTicks = 45;
      this.prayer = null;
      this.lastPrayTime = 0L;
      this.headAnimation = OlmAnimation.UNKNOWN;
      this.handAnimation = OlmAnimation.UNKNOWN;
   }

   public void hardRest() {
      this.active = false;
      this.firstPhase = false;
      this.finalPhase = false;
      this.phaseType = PhaseType.UNKNOWN;
      this.hand = null;
      this.head = null;
      this.headAnimation = OlmAnimation.UNKNOWN;
      this.handAnimation = OlmAnimation.UNKNOWN;
      this.ticksUntilNextAttack = -1;
      this.attackCycle = 1;
      this.specialCycle = 1;
      this.healPools.clear();
      this.portals.clear();
      this.portalTicks = 10;
      this.victims.clear();
      this.crippled = false;
      this.crippleTicks = 45;
      this.prayer = null;
      this.lastPrayTime = 0L;
   }

   void setPrayer(Prayer pray) {
      this.prayer = pray;
      this.lastPrayTime = System.currentTimeMillis();
   }

   void cripple() {
      this.crippled = true;
      this.crippleTicks = 45;
   }

   void uncripple() {
      this.crippled = false;
      this.crippleTicks = 45;
   }

   public void update() {
      this.updateVictims();
      this.updateCrippleSticks();
      this.updateSpecials();
      this.incrementTickCycle();
      this.headAnimations();
      this.handAnimations();
   }

   public void incrementTickCycle() {
      if (this.ticksUntilNextAttack == 1) {
         this.ticksUntilNextAttack = 4;
         this.incrementAttackCycle();
      } else if (this.ticksUntilNextAttack != -1) {
         --this.ticksUntilNextAttack;
      }

   }

   public void incrementAttackCycle() {
      if (this.attackCycle == 4) {
         this.attackCycle = 1;
         this.incrementSpecialCycle();
      } else {
         ++this.attackCycle;
      }

   }

   public void incrementSpecialCycle() {
      if ((this.specialCycle != 3 || this.finalPhase) && this.specialCycle != 4) {
         ++this.specialCycle;
      } else {
         this.specialCycle = 1;
      }

   }

   public void specialSync(OlmAnimation currentAnimation) {
      this.ticksUntilNextAttack = 4;
      this.attackCycle = 1;
      switch(currentAnimation) {
      case LEFT_HAND_CRYSTALS1:
      case LEFT_HAND_CRYSTALS2:
         this.specialCycle = 2;
         break;
      case LEFT_HAND_LIGHTNING1:
      case LEFT_HAND_LIGHTNING2:
         this.specialCycle = 3;
         break;
      case LEFT_HAND_PORTALS1:
      case LEFT_HAND_PORTALS2:
         this.specialCycle = this.finalPhase ? 4 : 1;
         break;
      case LEFT_HAND_HEAL1:
      case LEFT_HAND_HEAL2:
         this.specialCycle = 1;
      }

   }

   void updateCrippleSticks() {
      if (this.crippled) {
         --this.crippleTicks;
         if (this.crippleTicks <= 0) {
            this.crippled = false;
            this.crippleTicks = 45;
         }

      }
   }

   void updateVictims() {
      if (this.victims.size() > 0) {
         this.victims.forEach(Victim::updateTicks);
         this.victims.removeIf((victim) -> {
            return victim.getTicks() <= 0;
         });
      }

   }

   void updateSpecials() {
      this.healPools.clear();
      this.portals.clear();
      this.client.clearHintArrow();
      Iterator var1 = this.client.getGraphicsObjects().iterator();

      while(var1.hasNext()) {
         GraphicsObject o = (GraphicsObject)var1.next();
         if (o.getId() == 1359) {
            this.portals.add(WorldPoint.fromLocal(this.client, o.getLocation()));
         }

         if (o.getId() == 1363) {
            this.healPools.add(WorldPoint.fromLocal(this.client, o.getLocation()));
         }

         if (!this.portals.isEmpty()) {
            --this.portalTicks;
            if (this.portalTicks <= 0) {
               this.client.clearHintArrow();
               this.portalTicks = 10;
            }
         }
      }

   }

   private void headAnimations() {
      if (this.head != null && this.head.getRenderable() != null) {
         OlmAnimation currentAnimation = OlmAnimation.fromId(((DynamicObject)this.head.getRenderable()).getAnimation().getId());
         if (currentAnimation != this.headAnimation) {
            switch(currentAnimation) {
            case HEAD_RISING_2:
            case HEAD_ENRAGED_RISING_2:
               this.ticksUntilNextAttack = this.firstPhase ? 6 : 8;
               this.attackCycle = 1;
               this.specialCycle = 1;
               break;
            case HEAD_ENRAGED_LEFT:
            case HEAD_ENRAGED_MIDDLE:
            case HEAD_ENRAGED_RIGHT:
               this.finalPhase = true;
            }

            this.headAnimation = currentAnimation;
         }
      }
   }

   private void handAnimations() {
      if (this.hand != null && this.hand.getRenderable() != null) {
         OlmAnimation currentAnimation = OlmAnimation.fromId(((DynamicObject)this.hand.getRenderable()).getAnimation().getId());
         if (currentAnimation != this.handAnimation) {
            switch(currentAnimation) {
            case LEFT_HAND_CRYSTALS1:
            case LEFT_HAND_CRYSTALS2:
            case LEFT_HAND_LIGHTNING1:
            case LEFT_HAND_LIGHTNING2:
            case LEFT_HAND_PORTALS1:
            case LEFT_HAND_PORTALS2:
            case LEFT_HAND_HEAL1:
            case LEFT_HAND_HEAL2:
               this.specialSync(currentAnimation);
            case HEAD_RISING_2:
            case HEAD_ENRAGED_RISING_2:
            case HEAD_ENRAGED_LEFT:
            case HEAD_ENRAGED_MIDDLE:
            case HEAD_ENRAGED_RIGHT:
            default:
               break;
            case LEFT_HAND_CRIPPLING:
               this.cripple();
               break;
            case LEFT_HAND_UNCRIPPLING1:
            case LEFT_HAND_UNCRIPPLING2:
               this.uncripple();
            }

            this.handAnimation = currentAnimation;
         }
      }
   }

   public Client getClient() {
      return this.client;
   }

   public CoxPlugin getPlugin() {
      return this.plugin;
   }

   public CoxConfig getConfig() {
      return this.config;
   }

   public List<WorldPoint> getHealPools() {
      return this.healPools;
   }

   public List<WorldPoint> getPortals() {
      return this.portals;
   }

   public Set<Victim> getVictims() {
      return this.victims;
   }

   public int getPortalTicks() {
      return this.portalTicks;
   }

   public boolean isActive() {
      return this.active;
   }

   public boolean isFirstPhase() {
      return this.firstPhase;
   }

   public boolean isFinalPhase() {
      return this.finalPhase;
   }

   public PhaseType getPhaseType() {
      return this.phaseType;
   }

   public GameObject getHand() {
      return this.hand;
   }

   public OlmAnimation getHandAnimation() {
      return this.handAnimation;
   }

   public GameObject getHead() {
      return this.head;
   }

   public OlmAnimation getHeadAnimation() {
      return this.headAnimation;
   }

   public int getTicksUntilNextAttack() {
      return this.ticksUntilNextAttack;
   }

   public int getAttackCycle() {
      return this.attackCycle;
   }

   public int getSpecialCycle() {
      return this.specialCycle;
   }

   public boolean isCrippled() {
      return this.crippled;
   }

   public int getCrippleTicks() {
      return this.crippleTicks;
   }

   public Prayer getPrayer() {
      return this.prayer;
   }

   public long getLastPrayTime() {
      return this.lastPrayTime;
   }

   public void setPortalTicks(int portalTicks) {
      this.portalTicks = portalTicks;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public void setFirstPhase(boolean firstPhase) {
      this.firstPhase = firstPhase;
   }

   public void setFinalPhase(boolean finalPhase) {
      this.finalPhase = finalPhase;
   }

   public void setPhaseType(PhaseType phaseType) {
      this.phaseType = phaseType;
   }

   public void setHand(GameObject hand) {
      this.hand = hand;
   }

   public void setHandAnimation(OlmAnimation handAnimation) {
      this.handAnimation = handAnimation;
   }

   public void setHead(GameObject head) {
      this.head = head;
   }

   public void setHeadAnimation(OlmAnimation headAnimation) {
      this.headAnimation = headAnimation;
   }

   public void setTicksUntilNextAttack(int ticksUntilNextAttack) {
      this.ticksUntilNextAttack = ticksUntilNextAttack;
   }

   public void setAttackCycle(int attackCycle) {
      this.attackCycle = attackCycle;
   }

   public void setSpecialCycle(int specialCycle) {
      this.specialCycle = specialCycle;
   }

   public void setCrippled(boolean crippled) {
      this.crippled = crippled;
   }

   public void setCrippleTicks(int crippleTicks) {
      this.crippleTicks = crippleTicks;
   }

   public void setLastPrayTime(long lastPrayTime) {
      this.lastPrayTime = lastPrayTime;
   }

   public static enum PhaseType {
      FLAME,
      ACID,
      CRYSTAL,
      UNKNOWN;
   }
}
