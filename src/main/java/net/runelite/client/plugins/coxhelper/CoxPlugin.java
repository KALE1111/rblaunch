package net.runelite.client.plugins.coxhelper;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
   name = "CoX Helper",
   enabledByDefault = false,
   description = "All-in-one plugin for Chambers of Xeric",
   tags = {"CoX", "chamber", "xeric", "helper"}
)
public class CoxPlugin extends Plugin {
   private static final Logger log = LoggerFactory.getLogger(CoxPlugin.class);
   private static final int ANIMATION_ID_G1 = 430;
   private static final Pattern TP_REGEX = Pattern.compile("You have been paired with <col=ff0000>(.*)</col>! The magical power will enact soon...");
   private final Map<NPC, NPCContainer> npcContainers = new HashMap();
   private Projectile previousProjectile = null;
   private boolean firstProjectile = true;
   @Inject
   private Client client;
   @Inject
   private ChatMessageManager chatMessageManager;
   @Inject
   private CoxOverlay coxOverlay;
   @Inject
   private CoxInfoBox coxInfoBox;
   @Inject
   private CoxDebugBox coxDebugBox;
   @Inject
   private CoxConfig config;
   @Inject
   private OverlayManager overlayManager;
   @Inject
   private EventBus eventBus;
   @Inject
   private Olm olm;
   private int vanguards;
   private boolean tektonActive;
   private int tektonAttackTicks;
   public static final int TEKTON_ANVIL = 7475;
   public static final int TEKTON_AUTO1 = 7482;
   public static final int TEKTON_AUTO2 = 7483;
   public static final int TEKTON_AUTO3 = 7484;
   public static final int TEKTON_FAST_AUTO1 = 7478;
   public static final int TEKTON_FAST_AUTO2 = 7488;
   public static final int TEKTON_ENRAGE_AUTO1 = 7492;
   public static final int TEKTON_ENRAGE_AUTO2 = 7493;
   public static final int TEKTON_ENRAGE_AUTO3 = 7494;

   @Provides
   CoxConfig getConfig(ConfigManager configManager) {
      return (CoxConfig)configManager.getConfig(CoxConfig.class);
   }

   protected void startUp() {
      this.overlayManager.add(this.coxOverlay);
      this.overlayManager.add(this.coxInfoBox);
      this.overlayManager.add(this.coxDebugBox);
      this.olm.hardRest();
   }

   protected void shutDown() {
      this.overlayManager.remove(this.coxOverlay);
      this.overlayManager.remove(this.coxInfoBox);
      this.overlayManager.remove(this.coxDebugBox);
   }

   @Subscribe
   private void onChatMessage(ChatMessage event) {
      if (this.inRaid()) {
         if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            Matcher tpMatcher = TP_REGEX.matcher(event.getMessage());
            if (tpMatcher.matches()) {
               Iterator var3 = this.client.getPlayers().iterator();

               while(var3.hasNext()) {
                  Player player = (Player)var3.next();
                  String rawPlayerName = player.getName();
                  if (rawPlayerName != null) {
                     String fixedPlayerName = Text.sanitize(rawPlayerName);
                     if (fixedPlayerName.equals(Text.sanitize(tpMatcher.group(1)))) {
                        this.olm.getVictims().add(new Victim(player, Victim.Type.TELEPORT));
                     }
                  }
               }
            }

            String var7 = Text.standardize(event.getMessageNode().getValue());
            byte var8 = -1;
            switch(var7.hashCode()) {
            case -2094114081:
               if (var7.equals("the great olm fires a sphere of magical power your way.")) {
                  var8 = 6;
               }
               break;
            case -2064703836:
               if (var7.equals("the great olm rises with the power of acid.")) {
                  var8 = 0;
               }
               break;
            case -1272924347:
               if (var7.equals("the great olm rises with the power of crystal.")) {
                  var8 = 1;
               }
               break;
            case -525042198:
               if (var7.equals("the great olm fires a sphere of aggression your way.")) {
                  var8 = 4;
               }
               break;
            case -127344230:
               if (var7.equals("the great olm fires a sphere of accuracy and dexterity your way. your prayers have been sapped.")) {
                  var8 = 7;
               }
               break;
            case -103006838:
               if (var7.equals("the great olm fires a sphere of accuracy and dexterity your way.")) {
                  var8 = 8;
               }
               break;
            case 569920040:
               if (var7.equals("the great olm rises with the power of flame.")) {
                  var8 = 2;
               }
               break;
            case 1486918970:
               if (var7.equals("the great olm fires a sphere of aggression your way. your prayers have been sapped.")) {
                  var8 = 3;
               }
               break;
            case 1947752869:
               if (var7.equals("the great olm fires a sphere of magical power your way. your prayers have been sapped.")) {
                  var8 = 5;
               }
            }

            switch(var8) {
            case 0:
               this.olm.setPhaseType(Olm.PhaseType.ACID);
               break;
            case 1:
               this.olm.setPhaseType(Olm.PhaseType.CRYSTAL);
               break;
            case 2:
               this.olm.setPhaseType(Olm.PhaseType.FLAME);
               break;
            case 3:
            case 4:
               this.olm.setPrayer(Prayer.PROTECT_FROM_MELEE);
               break;
            case 5:
            case 6:
               this.olm.setPrayer(Prayer.PROTECT_FROM_MAGIC);
               break;
            case 7:
            case 8:
               this.olm.setPrayer(Prayer.PROTECT_FROM_MISSILES);
            }
         }

      }
   }

   @Subscribe
   private void onProjectileMoved(ProjectileMoved event) {
      if (this.inRaid()) {
         Projectile projectile = event.getProjectile();
         if (this.firstProjectile) {
            this.previousProjectile = projectile;
         }

         if (this.previousProjectile.getStartCycle() != projectile.getStartCycle() || this.firstProjectile) {
            switch(projectile.getId()) {
            case 1339:
               this.olm.setPrayer(Prayer.PROTECT_FROM_MAGIC);
               break;
            case 1340:
               this.olm.setPrayer(Prayer.PROTECT_FROM_MISSILES);
               break;
            case 1354:
               Actor actor = projectile.getInteracting();
               if (actor instanceof Player) {
                  this.olm.getVictims().add(new Victim((Player)actor, Victim.Type.ACID));
               }
            }

            if (this.firstProjectile) {
               this.firstProjectile = false;
            }
         }

      }
   }

   @Subscribe
   private void onGraphicChanged(GraphicChanged event) {
      if (this.inRaid()) {
         if (event.getActor() instanceof Player) {
            Player player = (Player)event.getActor();
            if (player.getGraphic() == 1351) {
               this.olm.getVictims().add(new Victim(player, Victim.Type.BURN));
            }

         }
      }
   }

   @Subscribe
   private void onNpcSpawned(NpcSpawned event) {
      if (this.inRaid()) {
         NPC npc = event.getNpc();
         switch(npc.getId()) {
         case 7525:
         case 7526:
         case 7527:
         case 7528:
         case 7529:
            ++this.vanguards;
            this.npcContainers.put(npc, new NPCContainer(npc));
         case 7530:
         case 7531:
         case 7532:
         case 7533:
         case 7534:
         case 7535:
         case 7536:
         case 7537:
         case 7538:
         case 7539:
         case 7546:
         case 7547:
         case 7548:
         case 7549:
         case 7550:
         case 7551:
         case 7552:
         case 7553:
         case 7554:
         case 7555:
         case 7556:
         case 7557:
         case 7558:
         case 7559:
         case 7560:
         case 7564:
         case 7565:
         case 7566:
         case 7567:
         case 7568:
         default:
            break;
         case 7540:
         case 7541:
         case 7542:
         case 7543:
         case 7544:
         case 7545:
            this.npcContainers.put(npc, new NPCContainer(npc));
            this.tektonAttackTicks = 27;
            break;
         case 7561:
         case 7562:
         case 7563:
         case 7569:
         case 7570:
            this.npcContainers.put(npc, new NPCContainer(npc));
         }

      }
   }

   @Subscribe
   private void onNpcDespawned(NpcDespawned event) {
      if (this.inRaid()) {
         NPC npc = event.getNpc();
         switch(npc.getId()) {
         case 7525:
         case 7526:
         case 7527:
         case 7528:
         case 7529:
            if (this.npcContainers.remove(event.getNpc()) != null && !this.npcContainers.isEmpty()) {
               this.npcContainers.remove(event.getNpc());
            }

            --this.vanguards;
         case 7530:
         case 7531:
         case 7532:
         case 7533:
         case 7534:
         case 7535:
         case 7536:
         case 7537:
         case 7538:
         case 7539:
         case 7546:
         case 7547:
         case 7548:
         case 7549:
         case 7550:
         case 7551:
         case 7552:
         case 7553:
         case 7554:
         case 7555:
         case 7556:
         case 7557:
         case 7558:
         case 7559:
         case 7560:
         case 7564:
         case 7565:
         case 7566:
         case 7567:
         case 7568:
         default:
            break;
         case 7540:
         case 7541:
         case 7542:
         case 7543:
         case 7544:
         case 7545:
         case 7561:
         case 7562:
         case 7563:
         case 7569:
         case 7570:
         case 7571:
         case 7572:
            if (this.npcContainers.remove(event.getNpc()) != null && !this.npcContainers.isEmpty()) {
               this.npcContainers.remove(event.getNpc());
            }
         }

      }
   }

   @Subscribe
   private void onGameTick(GameTick event) {
      if (!this.inRaid()) {
         this.olm.hardRest();
      } else {
         this.handleNpcs();
         if (this.olm.isActive()) {
            this.olm.update();
         }

      }
   }

   private void handleNpcs() {
      Iterator var1 = this.getNpcContainers().values().iterator();

      while(var1.hasNext()) {
         NPCContainer npcs = (NPCContainer)var1.next();
         switch(npcs.getNpc().getId()) {
         case 7527:
            if (npcs.getAttackStyle() == NPCContainer.Attackstyle.UNKNOWN) {
               npcs.setAttackStyle(NPCContainer.Attackstyle.MELEE);
            }
            break;
         case 7528:
            if (npcs.getAttackStyle() == NPCContainer.Attackstyle.UNKNOWN) {
               npcs.setAttackStyle(NPCContainer.Attackstyle.RANGE);
            }
            break;
         case 7529:
            if (npcs.getAttackStyle() == NPCContainer.Attackstyle.UNKNOWN) {
               npcs.setAttackStyle(NPCContainer.Attackstyle.MAGE);
            }
         case 7530:
         case 7531:
         case 7532:
         case 7533:
         case 7534:
         case 7535:
         case 7536:
         case 7537:
         case 7538:
         case 7539:
         case 7546:
         case 7547:
         case 7548:
         case 7549:
         case 7550:
         case 7551:
         case 7552:
         case 7553:
         case 7554:
         case 7555:
         case 7556:
         case 7557:
         case 7558:
         case 7559:
         case 7560:
         case 7561:
         case 7562:
         case 7563:
         case 7564:
         case 7565:
         case 7566:
         case 7567:
         case 7568:
         default:
            break;
         case 7540:
         case 7541:
         case 7542:
         case 7543:
         case 7544:
         case 7545:
            npcs.setTicksUntilAttack(npcs.getTicksUntilAttack() - 1);
            npcs.setAttackStyle(NPCContainer.Attackstyle.MELEE);
            switch(npcs.getNpc().getAnimation()) {
            case 7475:
               this.tektonActive = false;
               this.tektonAttackTicks = 47;
               if (npcs.getTicksUntilAttack() < 1) {
                  npcs.setTicksUntilAttack(15);
               }
            case 7476:
            case 7477:
            case 7479:
            case 7480:
            case 7481:
            case 7485:
            case 7486:
            case 7487:
            case 7489:
            case 7490:
            case 7491:
            default:
               continue;
            case 7478:
            case 7488:
               this.tektonActive = true;
               if (npcs.getTicksUntilAttack() < 1) {
                  npcs.setTicksUntilAttack(3);
               }
               continue;
            case 7482:
            case 7483:
            case 7484:
            case 7492:
            case 7493:
            case 7494:
               this.tektonActive = true;
               if (npcs.getTicksUntilAttack() < 1) {
                  npcs.setTicksUntilAttack(4);
               }
               continue;
            }
         case 7569:
         case 7570:
         case 7571:
         case 7572:
            npcs.setTicksUntilAttack(npcs.getTicksUntilAttack() - 1);
            npcs.setAttackStyle(NPCContainer.Attackstyle.MELEE);
            if (npcs.getNpc().getAnimation() == 430 && npcs.getTicksUntilAttack() < 1) {
               npcs.setTicksUntilAttack(5);
            }
         }
      }

      if (this.tektonActive && this.tektonAttackTicks > 0) {
         --this.tektonAttackTicks;
      }

   }

   boolean inRaid() {
      return this.client.getVarbitValue(5432) == 1;
   }

   @Subscribe
   public void onGameObjectSpawned(GameObjectSpawned event) {
      if (event.getGameObject() != null) {
         int id = event.getGameObject().getId();
         switch(id) {
         case 29880:
         case 29881:
            if (this.olm.getHead() == null) {
               this.olm.startPhase();
            }

            this.olm.setHead(event.getGameObject());
         case 29882:
         default:
            break;
         case 29883:
         case 29884:
            this.olm.setHand(event.getGameObject());
         }

      }
   }

   @Subscribe
   public void onGameObjectDespawned(GameObjectDespawned event) {
      if (event.getGameObject() != null) {
         int id = event.getGameObject().getId();
         if (id == 29881) {
            this.olm.setHead((GameObject)null);
         }

      }
   }

   Map<NPC, NPCContainer> getNpcContainers() {
      return this.npcContainers;
   }

   Olm getOlm() {
      return this.olm;
   }

   int getVanguards() {
      return this.vanguards;
   }

   boolean isTektonActive() {
      return this.tektonActive;
   }

   int getTektonAttackTicks() {
      return this.tektonAttackTicks;
   }

   public Projectile getPreviousProjectile() {
      return this.previousProjectile;
   }

   public void setPreviousProjectile(Projectile previousProjectile) {
      this.previousProjectile = previousProjectile;
   }

   public boolean isFirstProjectile() {
      return this.firstProjectile;
   }

   public void setFirstProjectile(boolean firstProjectile) {
      this.firstProjectile = firstProjectile;
   }
}
