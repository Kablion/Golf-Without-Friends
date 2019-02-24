package de.kablion.golf.utils;

import com.badlogic.gdx.math.MathUtils;

public class ShapeUtils {
    public static float[] buildCircle(float radius, int divisions, OriginX originX, OriginY originY) {
        float[] verts = new float[divisions * 2];
        float radiansPerDivision = (360f / divisions) * MathUtils.degreesToRadians;
        for (int division = 0; division < divisions; division++) {
            verts[division * 2] = (float) Math.cos(radiansPerDivision * division) * radius;
            verts[division * 2 + 1] = (float) Math.sin(radiansPerDivision * division) * radius;
        }
        return alignVertices(verts,originX,originY,radius,radius);
    }

    /**
     *
     * @param verts Verts where the origin is centered. the input array will be aligned so the original vertices will be removed
     * @return the same verts which was given, just aligned
     */
    private static float[] alignVertices(float[] verts, OriginX originX, OriginY originY, float halfWidth, float halfHeight) {

        switch (originX) {
            case LEFT: {
                for(int i = 0; i+1<verts.length; i+=2) {
                    verts[i] += halfWidth;
                }
                break;
            }
            case CENTER: {
                break;
            }
            case RIGHT: {
                for(int i = 0; i+1<verts.length; i+=2) {
                    verts[i] -= halfWidth;
                }
                break;
            }
        }

        switch (originY) {
            case BOTTOM: {
                for(int i = 0; i+1<verts.length; i+=2) {
                    verts[i+1] += halfHeight;
                }
                break;
            }
            case CENTER: {
                break;
            }
            case TOP: {
                for(int i = 0; i+1<verts.length; i+=2) {
                    verts[i+1] -= halfHeight;
                }
                break;
            }
        }

        return verts;
    }

    public static float[] buildRectangle(float width, float height, OriginX originX, OriginY originY) {
        float[] verts = new float[8];
        int i = 0;

        float halfWidth = width / 2;
        float halfHeight = height / 2;

        // Bottom Left
        verts[i++] = -halfWidth;
        verts[i++] = -halfHeight;

        // Top Left
        verts[i++] = -halfWidth;
        verts[i++] = halfHeight;

        // Top Right
        verts[i++] = halfWidth;
        verts[i++] = halfHeight;

        // Bottom Right
        verts[i++] = halfWidth;
        verts[i] = -halfHeight;

        return alignVertices(verts,originX,originY,halfWidth,halfHeight);
    }

    public static void translateVerts(float[] verts, float x, float y) {
        for(int i=0; i<verts.length;i+=2) {
            verts[i] += x;
            verts[i+1] += y;
        }

    }

    public enum OriginX {
        LEFT,CENTER,RIGHT
    }

    public enum OriginY {
        BOTTOM,CENTER,TOP
    }
}
