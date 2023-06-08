 package net.runelite.client.plugins.spoontob;
 
 import java.awt.BasicStroke;
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.Graphics2D;
 import java.awt.Polygon;
 import java.awt.Shape;
 import javax.inject.Inject;

 import net.runelite.client.plugins.spoontob.rooms.Maiden.Maiden;
 import net.runelite.client.plugins.spoontob.rooms.Verzik.Verzik;
 import net.runelite.client.plugins.spoontob.util.TheatreRegions;
 import net.runelite.api.Client;
 import net.runelite.api.NPC;
 import net.runelite.api.NPCComposition;
 import net.runelite.api.Perspective;
 import net.runelite.api.coords.LocalPoint;
 import net.runelite.client.ui.overlay.Overlay;
 import net.runelite.client.ui.overlay.OverlayLayer;
 import net.runelite.client.ui.overlay.OverlayPosition;
 import net.runelite.client.ui.overlay.OverlayPriority;
 
 public class RedsOverlay
   extends Overlay
 {
   @Inject
   private SpoonTobPlugin plugin;
   @Inject
   private SpoonTobConfig config;
   @Inject
   private Client client;
   @Inject
   private Maiden maiden;
   @Inject
   private Verzik verzik;
   
   @Inject
   public RedsOverlay(Client client, SpoonTobConfig config, SpoonTobPlugin plugin, Maiden maiden, Verzik verzik) {
     client = client;
     config = config;
     plugin = plugin;
     maiden = maiden;
     verzik = verzik;
     setPosition(OverlayPosition.DYNAMIC);
     setPriority(OverlayPriority.HIGH);
     setLayer(OverlayLayer.ABOVE_SCENE);
   }
   
   public Dimension render(Graphics2D graphics) {
     if (config.redsTL() != SpoonTobConfig.redsTlMode.OFF && plugin.enforceRegion()) {
       for (NPC reds : client.getNpcs()) {
         if (reds.getName() != null && reds.getName().equalsIgnoreCase("nylocas matomenos")) {
           NPCComposition composition = reds.getComposition();
           int size = composition.getSize();
           LocalPoint lp = LocalPoint.fromWorld(client, reds.getWorldLocation());
           if (lp != null) {
             lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
             Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
             if (tilePoly != null && (((
               config.redsTL() == SpoonTobConfig.redsTlMode.MAIDEN || config.redsTL() == SpoonTobConfig.redsTlMode.BOTH) && TheatreRegions.inRegion(client, TheatreRegions.MAIDEN)) || ((config
               .redsTL() == SpoonTobConfig.redsTlMode.VERZIK || config.redsTL() == SpoonTobConfig.redsTlMode.BOTH) && TheatreRegions.inRegion(client, TheatreRegions.VERZIK)))) {
               renderPoly(graphics, tilePoly, config.redsTLColor(), config.redsTLColor().getAlpha(), 0);
             }
           } 
         } 
       } 
     }
     
     return null;
   }
   
   private void renderPoly(Graphics2D graphics, Shape polygon, Color color, int outlineOpacity, int fillOpacity) {
     if (polygon != null) {
       graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineOpacity));
       graphics.setStroke(new BasicStroke(1.0F));
       graphics.draw(polygon);
       graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillOpacity));
       graphics.fill(polygon);
     } 
   }
 }


