package net.runelite.client.plugins.spoontob.util;

import net.runelite.api.GameObject;
import net.runelite.api.Model;

import java.awt.*;
import java.util.Arrays;

public class CustomGameObject {
    private final GameObject obj;
    private final int id;
    private int[] oldColors1;
    private int[] oldColors2;
    private int[] oldColors3;

    public CustomGameObject(GameObject obj, int id) {
        this.obj = obj;
        this.id = id;
    }

    public void setFaceColorValues(Color color) {
        Model model = this.obj.getRenderable().getModel();
        if (model != null && color != null) {
            int[] colors1 = model.getFaceColors1();
            int[] colors2 = model.getFaceColors2();
            int[] colors3 = model.getFaceColors3();
            if (this.isFaceColorsNullOrEmpty(this.oldColors1, this.oldColors2, this.oldColors3)) {
                this.oldColors1 = colors1.clone();
                this.oldColors2 = colors2.clone();
                this.oldColors3 = colors3.clone();
            }

            this.replaceFaceColors123(color, colors1, colors2, colors3);
        }
    }

    private void replaceFaceColors123(Color color, int[]... args) {
        int rs2 = ColorsUtil.RGBtoRS2HSB(color.getRed(), color.getGreen(), color.getBlue());
        int[][] var4 = args;
        int var5 = args.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            int[] faceColors = var4[var6];
            if (faceColors.length > 0) {
                Arrays.fill(faceColors, rs2);
            }
        }

    }

    public void restore() {
        Model model = this.obj.getRenderable().getModel();
        if (model != null && !this.isFaceColorsNullOrEmpty(this.oldColors1, this.oldColors2, this.oldColors3)) {
            System.arraycopy(this.oldColors1, 0, model.getFaceColors1(), 0, this.oldColors1.length);
            System.arraycopy(this.oldColors2, 0, model.getFaceColors2(), 0, this.oldColors2.length);
            System.arraycopy(this.oldColors3, 0, model.getFaceColors3(), 0, this.oldColors3.length);
            this.oldColors1 = null;
            this.oldColors2 = null;
            this.oldColors3 = null;
        }
    }

    private boolean isFaceColorsNullOrEmpty(int[]... args) {
        boolean flag = false;
        int[][] var3 = args;
        int var4 = args.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int[] faceColors = var3[var5];
            if (faceColors == null || faceColors.length <= 0) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CustomGameObject)) {
            return false;
        } else {
            CustomGameObject other = (CustomGameObject)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$obj = this.getObj();
                Object other$obj = other.getObj();
                if (this$obj == null) {
                    if (other$obj != null) {
                        return false;
                    }
                } else if (!this$obj.equals(other$obj)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof CustomGameObject;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $obj = getObj();
        return result * 59 + (($obj == null) ? 43 : $obj.hashCode());
    }

    public GameObject getObj() {
        return this.obj;
    }

    public int getId() {
        return this.id;
    }

    public int[] getOldColors1() {
        return this.oldColors1;
    }

    public int[] getOldColors2() {
        return this.oldColors2;
    }

    public int[] getOldColors3() {
        return this.oldColors3;
    }
}