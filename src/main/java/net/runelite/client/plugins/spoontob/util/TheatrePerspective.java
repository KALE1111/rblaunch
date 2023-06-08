package net.runelite.client.plugins.spoontob.util;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.Point;
import net.runelite.api.SpritePixels;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.geometry.SimplePolygon;
import net.runelite.api.model.Jarvis;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class TheatrePerspective {
    public static final double UNIT = 0.0030679615757712823D;

    public static final int LOCAL_COORD_BITS = 7;

    public static final int LOCAL_TILE_SIZE = 128;

    public static final int LOCAL_HALF_TILE_SIZE = 64;

    public static final int SCENE_SIZE = 104;

    public static final int[] SINE = new int[2048];

    public static final int[] COSINE = new int[2048];

    static {
        for (int i = 0; i < 2048; i++) {
            SINE[i] = (int)(65536.0D * Math.sin(i * 0.0030679615757712823D));
            COSINE[i] = (int)(65536.0D * Math.cos(i * 0.0030679615757712823D));
        }
    }

    @Nullable
    public static Point localToCanvas(@Nonnull Client client, @Nonnull LocalPoint point, int plane) {
        return localToCanvas(client, point, plane, 0);
    }

    @Nullable
    public static Point localToCanvas(@Nonnull Client client, @Nonnull LocalPoint point, int plane, int zOffset) {
        int tileHeight = getTileHeight(client, point, plane);
        return localToCanvas(client, point.getX(), point.getY(), tileHeight - zOffset);
    }

    public static Point localToCanvas(@Nonnull Client client, int x, int y, int z) {
        if (x >= 128 && y >= 128 && x <= 13056 && y <= 13056) {
            x -= client.getCameraX();
            y -= client.getCameraY();
            z -= client.getCameraZ();
            int cameraPitch = client.getCameraPitch();
            int cameraYaw = client.getCameraYaw();
            int pitchSin = SINE[cameraPitch];
            int pitchCos = COSINE[cameraPitch];
            int yawSin = SINE[cameraYaw];
            int yawCos = COSINE[cameraYaw];
            int var8 = yawCos * x + y * yawSin >> 16;
            y = yawCos * y - yawSin * x >> 16;
            x = var8;
            var8 = pitchCos * z - y * pitchSin >> 16;
            y = z * pitchSin + y * pitchCos >> 16;
            if (y >= 50) {
                int pointX = client.getViewportWidth() / 2 + x * client.getScale() / y;
                int pointY = client.getViewportHeight() / 2 + var8 * client.getScale() / y;
                return new Point(pointX + client
                        .getViewportXOffset(), pointY + client
                        .getViewportYOffset());
            }
        }
        return null;
    }

    public static void modelToCanvas(Client client, int end, int x3dCenter, int y3dCenter, int z3dCenter, int rotate, int[] x3d, int[] y3d, int[] z3d, int[] x2d, int[] y2d) {
        int cameraPitch = client.getCameraPitch();
        int cameraYaw = client.getCameraYaw();
        int pitchSin = SINE[cameraPitch];
        int pitchCos = COSINE[cameraPitch];
        int yawSin = SINE[cameraYaw];
        int yawCos = COSINE[cameraYaw];
        int rotateSin = SINE[rotate];
        int rotateCos = COSINE[rotate];
        int cx = x3dCenter - client.getCameraX();
        int cy = y3dCenter - client.getCameraY();
        int cz = z3dCenter - client.getCameraZ();
        int viewportXMiddle = client.getViewportWidth() / 2;
        int viewportYMiddle = client.getViewportHeight() / 2;
        int viewportXOffset = client.getViewportXOffset();
        int viewportYOffset = client.getViewportYOffset();
        int zoom3d = client.getScale();
        for (int i = 0; i < end; i++) {
            int viewX, viewY, x = x3d[i];
            int y = y3d[i];
            int z = z3d[i];
            if (rotate != 0) {
                int x0 = x;
                x = x0 * rotateCos + y * rotateSin >> 16;
                y = y * rotateCos - x0 * rotateSin >> 16;
            }
            x += cx;
            y += cy;
            z += cz;
            int x1 = x * yawCos + y * yawSin >> 16;
            int y1 = y * yawCos - x * yawSin >> 16;
            int y2 = z * pitchCos - y1 * pitchSin >> 16;
            int z1 = y1 * pitchCos + z * pitchSin >> 16;
            if (z1 < 50) {
                viewX = Integer.MIN_VALUE;
                viewY = Integer.MIN_VALUE;
            } else {
                viewX = viewportXMiddle + x1 * zoom3d / z1 + viewportXOffset;
                viewY = viewportYMiddle + y2 * zoom3d / z1 + viewportYOffset;
            }
            x2d[i] = viewX;
            y2d[i] = viewY;
        }
    }

    @Nullable
    public static Point localToMinimap(@Nonnull Client client, @Nonnull LocalPoint point) {
        return localToMinimap(client, point, 6400);
    }

    @Nullable
    public static Point localToMinimap(@Nonnull Client client, @Nonnull LocalPoint point, int distance) {
        LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
        int x = point.getX() / 32 - localLocation.getX() / 32;
        int y = point.getY() / 32 - localLocation.getY() / 32;
        int dist = x * x + y * y;
        if (dist < distance) {
            Widget minimapDrawWidget;
            if (client.isResized()) {
                if (client.getVarbitValue(Varbits.SIDE_PANELS) == 1) {
                    minimapDrawWidget = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_DRAW_AREA);
                } else {
                    minimapDrawWidget = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_STONES_DRAW_AREA);
                }
            } else {
                minimapDrawWidget = client.getWidget(WidgetInfo.FIXED_VIEWPORT_MINIMAP_DRAW_AREA);
            }
            if (minimapDrawWidget == null || minimapDrawWidget.isHidden())
                return null;
            int angle = client.getMapAngle() & 0x7FF;
            int sin = SINE[angle];
            int cos = COSINE[angle];
            int xx = y * sin + cos * x >> 16;
            int yy = sin * x - y * cos >> 16;
            Point loc = minimapDrawWidget.getCanvasLocation();
            int miniMapX = loc.getX() + xx + minimapDrawWidget.getWidth() / 2;
            int miniMapY = minimapDrawWidget.getHeight() / 2 + loc.getY() + yy;
            return new Point(miniMapX, miniMapY);
        }
        return null;
    }

    public static int getTileHeight(@Nonnull Client client, @Nonnull LocalPoint point, int plane) {
        int sceneX = point.getSceneX();
        int sceneY = point.getSceneY();
        if (sceneX >= 0 && sceneY >= 0 && sceneX < 104 && sceneY < 104) {
            byte[][][] tileSettings = client.getTileSettings();
            int[][][] tileHeights = client.getTileHeights();
            int z1 = plane;
            if (plane < 3 && (tileSettings[1][sceneX][sceneY] & 0x2) == 2)
                z1 = plane + 1;
            int x = point.getX() & 0x7F;
            int y = point.getY() & 0x7F;
            int var8 = x * tileHeights[z1][sceneX + 1][sceneY] + (128 - x) * tileHeights[z1][sceneX][sceneY] >> 7;
            int var9 = tileHeights[z1][sceneX][sceneY + 1] * (128 - x) + x * tileHeights[z1][sceneX + 1][sceneY + 1] >> 7;
            return (128 - y) * var8 + y * var9 >> 7;
        }
        return 0;
    }

    private static int getHeight(@Nonnull Client client, int localX, int localY, int plane) {
        int sceneX = localX >> 7;
        int sceneY = localY >> 7;
        if (sceneX >= 0 && sceneY >= 0 && sceneX < 104 && sceneY < 104) {
            int[][][] tileHeights = client.getTileHeights();
            int x = localX & 0x7F;
            int y = localY & 0x7F;
            int var8 = x * tileHeights[plane][sceneX + 1][sceneY] + (128 - x) * tileHeights[plane][sceneX][sceneY] >> 7;
            int var9 = tileHeights[plane][sceneX][sceneY + 1] * (128 - x) + x * tileHeights[plane][sceneX + 1][sceneY + 1] >> 7;
            return (128 - y) * var8 + y * var9 >> 7;
        }
        return 0;
    }

    public static Polygon getLine(@Nonnull Client client, @Nonnull LocalPoint localLocation, String orientation) {
        Polygon poly = new Polygon();
        int plane = client.getPlane();
        int swX = localLocation.getX() - 64;
        int swY = localLocation.getY() - 64;
        int neX = localLocation.getX() + 64;
        int neY = localLocation.getY() + 64;
        int seX = swX;
        int seY = neY;
        int nwX = neX;
        int nwY = swY;
        byte[][][] tileSettings = client.getTileSettings();
        int sceneX = localLocation.getSceneX();
        int sceneY = localLocation.getSceneY();
        if (sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
            return null;
        int tilePlane = plane;
        if (plane < 3 && (tileSettings[1][sceneX][sceneY] & 0x2) == 2)
            tilePlane = plane + 1;
        int swHeight = getHeight(client, swX, swY, tilePlane);
        int nwHeight = getHeight(client, nwX, nwY, tilePlane);
        int neHeight = getHeight(client, neX, neY, tilePlane);
        int seHeight = getHeight(client, seX, seY, tilePlane);
        Point p1 = localToCanvas(client, swX, swY, swHeight);
        Point p2 = localToCanvas(client, nwX, nwY, nwHeight);
        Point p3 = localToCanvas(client, neX, neY, neHeight);
        Point p4 = localToCanvas(client, seX, seY, seHeight);
        Point p5 = localToCanvas(client, localLocation.getX(), localLocation.getY() - 64, swHeight);
        Point p6 = localToCanvas(client, localLocation.getX(), localLocation.getY(), swHeight);
        Point p7 = localToCanvas(client, localLocation.getX(), localLocation.getY() + 64, swHeight);
        Point p8 = localToCanvas(client, localLocation.getX() + 64, localLocation.getY(), swHeight);
        Point p9 = localToCanvas(client, localLocation.getX() - 64, localLocation.getY(), swHeight);
        Point p10 = localToCanvas(client, localLocation.getX() - 44, localLocation.getY() + 32, swHeight);
        Point p11 = localToCanvas(client, localLocation.getX() - 20, localLocation.getY() + 32, swHeight);
        Point p12 = localToCanvas(client, localLocation.getX(), localLocation.getY(), swHeight);
        Point p13 = localToCanvas(client, localLocation.getX(), localLocation.getY() - 64, swHeight);
        Point p14 = localToCanvas(client, localLocation.getX() + 44, localLocation.getY() + 32, swHeight);
        Point p15 = localToCanvas(client, localLocation.getX() + 20, localLocation.getY() + 32, swHeight);
        Point p16 = localToCanvas(client, localLocation.getX(), localLocation.getY(), swHeight);
        Point p17 = localToCanvas(client, localLocation.getX(), localLocation.getY() - 64, swHeight);
        if (p1 == null || p2 == null || p3 == null || p4 == null)
            return null;
        if (orientation == "swnw") {
            poly.addPoint(p1.getX(), p1.getY());
            poly.addPoint(p4.getX(), p4.getY());
        } else if (orientation == "sene") {
            poly.addPoint(p2.getX(), p2.getY());
            poly.addPoint(p3.getX(), p3.getY());
        } else if (orientation == "swse") {
            poly.addPoint(p1.getX(), p1.getY());
            poly.addPoint(p2.getX(), p2.getY());
        } else if (orientation == "nwne") {
            poly.addPoint(p4.getX(), p4.getY());
            poly.addPoint(p3.getX(), p3.getY());
        } else if (orientation == "westMiddle") {
            poly.addPoint(p5.getX(), p5.getY());
            poly.addPoint(p6.getX(), p6.getY());
        } else if (orientation == "kUp") {
            poly.addPoint(p5.getX(), p5.getY());
            poly.addPoint(p4.getX(), p4.getY());
        } else if (orientation == "kDown") {
            poly.addPoint(p5.getX(), p5.getY());
            poly.addPoint(p3.getX(), p3.getY());
        } else if (orientation == "E") {
            poly.addPoint(p5.getX(), p5.getY());
            poly.addPoint(p7.getX(), p7.getY());
        } else if (orientation == "I") {
            poly.addPoint(p8.getX(), p8.getY());
            poly.addPoint(p9.getX(), p9.getY());
        } else if (orientation == "B1") {
            poly.addPoint(p1.getX(), p1.getY());
            poly.addPoint(p9.getX(), p9.getY());
        } else if (orientation == "B2") {
            poly.addPoint(p9.getX(), p9.getY());
            poly.addPoint(p10.getX(), p10.getY());
        } else if (orientation == "B3") {
            poly.addPoint(p10.getX(), p10.getY());
            poly.addPoint(p11.getX(), p11.getY());
        } else if (orientation == "B4") {
            poly.addPoint(p11.getX(), p11.getY());
            poly.addPoint(p12.getX(), p12.getY());
        } else if (orientation == "B5") {
            poly.addPoint(p12.getX(), p12.getY());
            poly.addPoint(p13.getX(), p13.getY());
        } else if (orientation == "B6") {
            poly.addPoint(p2.getX(), p2.getY());
            poly.addPoint(p8.getX(), p8.getY());
        } else if (orientation == "B7") {
            poly.addPoint(p8.getX(), p8.getY());
            poly.addPoint(p14.getX(), p14.getY());
        } else if (orientation == "B8") {
            poly.addPoint(p14.getX(), p14.getY());
            poly.addPoint(p15.getX(), p15.getY());
        } else if (orientation == "B9") {
            poly.addPoint(p15.getX(), p15.getY());
            poly.addPoint(p16.getX(), p16.getY());
        } else if (orientation == "B10") {
            poly.addPoint(p16.getX(), p16.getY());
            poly.addPoint(p17.getX(), p17.getY());
        }
        return poly;
    }

    public static Polygon getCanvasTilePoly(@Nonnull Client client, @Nonnull LocalPoint localLocation) {
        return getCanvasTileAreaPoly(client, localLocation, 1);
    }

    public static Polygon getCanvasTilePoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int zOffset) {
        return getCanvasTileAreaPoly(client, localLocation, 1, 1, client.getPlane(), zOffset);
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size) {
        return getCanvasTileAreaPoly(client, localLocation, size, 0, true);
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size, int borderOffset) {
        return getCanvasTileAreaPoly(client, localLocation, size, borderOffset, true);
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size, boolean centered) {
        return getCanvasTileAreaPoly(client, localLocation, size, 0, centered);
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size, int borderOffset, boolean centered) {
        int swX, swY, neX, neY, plane = client.getPlane();
        if (centered) {
            swX = localLocation.getX() - size * (128 + borderOffset) / 2;
            swY = localLocation.getY() - size * (128 + borderOffset) / 2;
            neX = localLocation.getX() + size * (128 + borderOffset) / 2;
            neY = localLocation.getY() + size * (128 + borderOffset) / 2;
        } else {
            swX = localLocation.getX() - (128 + borderOffset) / 2;
            swY = localLocation.getY() - (128 + borderOffset) / 2;
            neX = localLocation.getX() - (128 + borderOffset) / 2 + size * (128 + borderOffset);
            neY = localLocation.getY() - (128 + borderOffset) / 2 + size * (128 + borderOffset);
        }
        int seX = swX;
        int seY = neY;
        int nwX = neX;
        int nwY = swY;
        byte[][][] tileSettings = client.getTileSettings();
        int sceneX = localLocation.getSceneX();
        int sceneY = localLocation.getSceneY();
        if (sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
            return null;
        int tilePlane = plane;
        if (plane < 3 && (tileSettings[1][sceneX][sceneY] & 0x2) == 2)
            tilePlane = plane + 1;
        int swHeight = getHeight(client, swX, swY, tilePlane);
        int nwHeight = getHeight(client, nwX, nwY, tilePlane);
        int neHeight = getHeight(client, neX, neY, tilePlane);
        int seHeight = getHeight(client, seX, seY, tilePlane);
        Point p1 = localToCanvas(client, swX, swY, swHeight);
        Point p2 = localToCanvas(client, nwX, nwY, nwHeight);
        Point p3 = localToCanvas(client, neX, neY, neHeight);
        Point p4 = localToCanvas(client, seX, seY, seHeight);
        if (p1 == null || p2 == null || p3 == null || p4 == null)
            return null;
        Polygon poly = new Polygon();
        poly.addPoint(p1.getX(), p1.getY());
        poly.addPoint(p2.getX(), p2.getY());
        poly.addPoint(p3.getX(), p3.getY());
        poly.addPoint(p4.getX(), p4.getY());
        return poly;
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int sizeX, int sizeY, int zOffset) {
        int plane = client.getPlane();
        int swX = localLocation.getX() - sizeX * 128 / 2;
        int swY = localLocation.getY() - sizeY * 128 / 2;
        int neX = localLocation.getX() + sizeX * 128 / 2;
        int neY = localLocation.getY() + sizeY * 128 / 2;
        int seX = swX;
        int seY = neY;
        int nwX = neX;
        int nwY = swY;
        byte[][][] tileSettings = client.getTileSettings();
        int sceneX = localLocation.getSceneX();
        int sceneY = localLocation.getSceneY();
        if (sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
            return null;
        int tilePlane = plane;
        if (plane < 3 && (tileSettings[1][sceneX][sceneY] & 0x2) == 2)
            tilePlane = plane + 1;
        int swHeight = getHeight(client, swX, swY, tilePlane) - zOffset;
        int nwHeight = getHeight(client, nwX, nwY, tilePlane) - zOffset;
        int neHeight = getHeight(client, neX, neY, tilePlane) - zOffset;
        int seHeight = getHeight(client, seX, seY, tilePlane) - zOffset;
        Point p1 = localToCanvas(client, swX, swY, swHeight);
        Point p2 = localToCanvas(client, nwX, nwY, nwHeight);
        Point p3 = localToCanvas(client, neX, neY, neHeight);
        Point p4 = localToCanvas(client, seX, seY, seHeight);
        if (p1 == null || p2 == null || p3 == null || p4 == null)
            return null;
        Polygon poly = new Polygon();
        poly.addPoint(p1.getX(), p1.getY());
        poly.addPoint(p2.getX(), p2.getY());
        poly.addPoint(p3.getX(), p3.getY());
        poly.addPoint(p4.getX(), p4.getY());
        return poly;
    }

    public static Polygon getCanvasTileAreaPoly(@Nonnull Client client, @Nonnull LocalPoint localLocation, int sizeX, int sizeY, int plane, int zOffset) {
        int swX = localLocation.getX() - sizeX * 128 / 2;
        int swY = localLocation.getY() - sizeY * 128 / 2;
        int neX = localLocation.getX() + sizeX * 128 / 2;
        int neY = localLocation.getY() + sizeY * 128 / 2;
        int seX = swX;
        int seY = neY;
        int nwX = neX;
        int nwY = swY;
        byte[][][] tileSettings = client.getTileSettings();
        int sceneX = localLocation.getSceneX();
        int sceneY = localLocation.getSceneY();
        if (sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
            return null;
        int tilePlane = plane;
        if (plane < 3 && (tileSettings[1][sceneX][sceneY] & 0x2) == 2)
            tilePlane = plane + 1;
        int swHeight = getHeight(client, swX, swY, tilePlane) - zOffset;
        int nwHeight = getHeight(client, nwX, nwY, tilePlane) - zOffset;
        int neHeight = getHeight(client, neX, neY, tilePlane) - zOffset;
        int seHeight = getHeight(client, seX, seY, tilePlane) - zOffset;
        Point p1 = localToCanvas(client, swX, swY, swHeight);
        Point p2 = localToCanvas(client, nwX, nwY, nwHeight);
        Point p3 = localToCanvas(client, neX, neY, neHeight);
        Point p4 = localToCanvas(client, seX, seY, seHeight);
        if (p1 == null || p2 == null || p3 == null || p4 == null)
            return null;
        Polygon poly = new Polygon();
        poly.addPoint(p1.getX(), p1.getY());
        poly.addPoint(p2.getX(), p2.getY());
        poly.addPoint(p3.getX(), p3.getY());
        poly.addPoint(p4.getX(), p4.getY());
        return poly;
    }

    public static Polygon getCanvasTileAreaPolyDiag(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size) {
        int plane = client.getPlane();
        int swX = localLocation.getX() - size * 128 / 2;
        int swY = localLocation.getY() - size * 128 / 2;
        int neX = localLocation.getX() + size * 128 / 2;
        int neY = localLocation.getY() + size * 128 / 2;
        int seX = swX;
        int seY = neY;
        int nwX = neX;
        int nwY = swY;
        byte[][][] tileSettings = client.getTileSettings();
        int sceneX = localLocation.getSceneX();
        int sceneY = localLocation.getSceneY();
        if (sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
            return null;
        int tilePlane = plane;
        if (plane < 3 && (tileSettings[1][sceneX][sceneY] & 0x2) == 2)
            tilePlane = plane + 1;
        int swHeight = getHeight(client, swX, swY, tilePlane);
        int nwHeight = getHeight(client, nwX, nwY, tilePlane);
        int neHeight = getHeight(client, neX, neY, tilePlane);
        int seHeight = getHeight(client, seX, seY, tilePlane);
        Point p1 = localToCanvas(client, swX, swY, swHeight);
        Point p2 = localToCanvas(client, nwX, nwY, nwHeight);
        Point p3 = localToCanvas(client, neX, neY, neHeight);
        Point p4 = localToCanvas(client, seX, seY, seHeight);
        if (p1 == null || p2 == null || p3 == null || p4 == null)
            return null;
        Polygon poly = new Polygon();
        poly.addPoint(p1.getX(), p1.getY());
        poly.addPoint(p3.getX(), p3.getY());
        return poly;
    }

    public static Polygon getCanvasTileAreaPolyDiag2(@Nonnull Client client, @Nonnull LocalPoint localLocation, int size) {
        int plane = client.getPlane();
        int swX = localLocation.getX() - size * 128 / 2;
        int swY = localLocation.getY() - size * 128 / 2;
        int neX = localLocation.getX() + size * 128 / 2;
        int neY = localLocation.getY() + size * 128 / 2;
        int seX = swX;
        int seY = neY;
        int nwX = neX;
        int nwY = swY;
        byte[][][] tileSettings = client.getTileSettings();
        int sceneX = localLocation.getSceneX();
        int sceneY = localLocation.getSceneY();
        if (sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
            return null;
        int tilePlane = plane;
        if (plane < 3 && (tileSettings[1][sceneX][sceneY] & 0x2) == 2)
            tilePlane = plane + 1;
        int swHeight = getHeight(client, swX, swY, tilePlane);
        int nwHeight = getHeight(client, nwX, nwY, tilePlane);
        int neHeight = getHeight(client, neX, neY, tilePlane);
        int seHeight = getHeight(client, seX, seY, tilePlane);
        Point p1 = localToCanvas(client, swX, swY, swHeight);
        Point p2 = localToCanvas(client, nwX, nwY, nwHeight);
        Point p3 = localToCanvas(client, neX, neY, neHeight);
        Point p4 = localToCanvas(client, seX, seY, seHeight);
        if (p1 == null || p2 == null || p3 == null || p4 == null)
            return null;
        Polygon poly = new Polygon();
        poly.addPoint(p2.getX(), p2.getY());
        poly.addPoint(p4.getX(), p4.getY());
        return poly;
    }

    public static Point getCanvasTextLocation(@Nonnull Client client, @Nonnull Graphics2D graphics, @Nonnull LocalPoint localLocation, @Nullable String text, int zOffset) {
        if (text == null)
            return null;
        int plane = client.getPlane();
        Point p = localToCanvas(client, localLocation, plane, zOffset);
        if (p == null)
            return null;
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(text, graphics);
        int xOffset = p.getX() - (int)(bounds.getWidth() / 2.0D);
        return new Point(xOffset, p.getY());
    }

    public static Point getCanvasImageLocation(@Nonnull Client client, @Nonnull LocalPoint localLocation, @Nonnull BufferedImage image, int zOffset) {
        int plane = client.getPlane();
        Point p = localToCanvas(client, localLocation, plane, zOffset);
        if (p == null)
            return null;
        int xOffset = p.getX() - image.getWidth() / 2;
        int yOffset = p.getY() - image.getHeight() / 2;
        return new Point(xOffset, yOffset);
    }

    public static Point getMiniMapImageLocation(@Nonnull Client client, @Nonnull LocalPoint localLocation, @Nonnull BufferedImage image) {
        Point p = localToMinimap(client, localLocation);
        if (p == null)
            return null;
        int xOffset = p.getX() - image.getWidth() / 2;
        int yOffset = p.getY() - image.getHeight() / 2;
        return new Point(xOffset, yOffset);
    }

    public static Point getCanvasSpriteLocation(@Nonnull Client client, @Nonnull LocalPoint localLocation, @Nonnull SpritePixels sprite, int zOffset) {
        int plane = client.getPlane();
        Point p = localToCanvas(client, localLocation, plane, zOffset);
        if (p == null)
            return null;
        int xOffset = p.getX() - sprite.getWidth() / 2;
        int yOffset = p.getY() - sprite.getHeight() / 2;
        return new Point(xOffset, yOffset);
    }

    private static SimplePolygon calculateAABB(Client client, Model m, int jauOrient, int x, int y, int z) {
        int ex = m.getAABB(0).getExtremeX();
        if (ex == -1) {
            m.calculateBoundsCylinder();
            m.calculateExtreme(0);
            ex = m.getAABB(0).getExtremeX();
        }
        int x1 = m.getAABB(0).getCenterX();
        int y1 = m.getAABB(0).getCenterZ();
        int z1 = m.getAABB(0).getCenterY();
        int ey = m.getAABB(0).getExtremeZ();
        int ez = m.getAABB(0).getExtremeY();
        int x2 = x1 + ex;
        int y2 = y1 + ey;
        int z2 = z1 + ez;
        x1 -= ex;
        y1 -= ey;
        z1 -= ez;
        int[] xa = { x1, x2, x1, x2, x1, x2, x1, x2 };
        int[] ya = { y1, y1, y2, y2, y1, y1, y2, y2 };
        int[] za = { z1, z1, z1, z1, z2, z2, z2, z2 };
        int[] x2d = new int[8];
        int[] y2d = new int[8];
        modelToCanvas(client, 8, x, y, z, jauOrient, xa, ya, za, x2d, y2d);
        return Jarvis.convexHull(x2d, y2d);
    }

    public static Point getCanvasTextMiniMapLocation(@Nonnull Client client, @Nonnull Graphics2D graphics, @Nonnull LocalPoint localLocation, @Nonnull String text) {
        Point p = localToMinimap(client, localLocation);
        if (p == null)
            return null;
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(text, graphics);
        int xOffset = p.getX() - (int)(bounds.getWidth() / 2.0D);
        int yOffset = p.getY() - (int)(bounds.getHeight() / 2.0D) + fm.getAscent();
        return new Point(xOffset, yOffset);
    }
}
