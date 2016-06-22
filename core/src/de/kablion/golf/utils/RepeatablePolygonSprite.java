package de.kablion.golf.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

/**
 * Renders polygon filled with a repeating TextureRegion with specified density
 * Without causing an additional flush or render call
 *
 * @author Avetis Zakharyan (concept)
 * @author Kablion (rewrite)
 *         <p>
 *         todo:
 *         updatePolygon
 *         draw
 *         getBoundingRectangle
 *         getVertices
 *         to be aware of TextureRegion changes
 */
public class RepeatablePolygonSprite {

    public static class WrapType {
        /*
         * The Type how a texture is drawn over the Polygon along one axis
         */

        public static final int STRETCH = 0;
        public static final int FILL = 1;
        public static final int REPEAT = 2;
        public static final int REPEAT_MIRRORED = 3;

        public static void validate(int type) {
            if (!((type == STRETCH) | (type == FILL) | (type == REPEAT) | (type == REPEAT_MIRRORED))) {
                throw new IllegalArgumentException("The given type is not valid: " + type);
            }
        }
    }

    private int wrapTypeX;
    private int wrapTypeY;

    private TextureRegion textureRegion;

    private Vector2 textureOffset = new Vector2();
    private float textureWidth = 0;
    private float textureHeight = 0;

    private boolean dirtyGrid = true;
    private boolean dirtyAttributes = true;

    private float[] originalVertices = null;

    private Array<float[]> parts = new Array<float[]>();

    private Array<float[]> vertices = new Array<float[]>();
    private Array<short[]> indices = new Array<short[]>();

    private int cols, rows;
    private float gridWidth, gridHeight;
    private Vector2 currentOffset = new Vector2();

    private float x = 0, y = 0; // Position in the world
    private float originX = 0, originY = 0; // Origin relative to the Position (normaly 0,0)
    private float scaleX = 1f, scaleY = 1f;
    private float rotation = 0;
    private Rectangle bounds = new Rectangle();
    private final Color color = new Color(Color.WHITE);

    /**
     * calculates the grid and the parts in relation to the texture Origin
     */
    private void prepareVertices() {
        float[] vertices = originalVertices.clone();
        vertices = offset(vertices);

        Polygon polygon = new Polygon(vertices);
        Polygon tmpPoly = new Polygon();
        Polygon intersectionPoly = new Polygon();
        EarClippingTriangulator triangulator = new EarClippingTriangulator();

        int idx;

        Rectangle boundRect = polygon.getBoundingRectangle();

        if (wrapTypeX == WrapType.STRETCH) {
            gridWidth = boundRect.getWidth();
        } else {
            gridWidth = textureWidth;
        }
        if (wrapTypeY == WrapType.STRETCH) {
            gridHeight = boundRect.getHeight();
        } else {
            gridHeight = textureHeight;
        }
        cols = (int) (Math.ceil(boundRect.getWidth() / gridWidth));
        rows = (int) Math.ceil(boundRect.getHeight() / gridHeight);

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                float[] verts = new float[8];
                idx = 0;
                verts[idx++] = col * gridWidth - textureOffset.x;
                verts[idx++] = row * gridHeight - textureOffset.y;
                verts[idx++] = (col) * gridWidth - textureOffset.x;
                verts[idx++] = (row + 1) * gridHeight - textureOffset.y;
                verts[idx++] = (col + 1) * gridWidth - textureOffset.x;
                verts[idx++] = (row + 1) * gridHeight - textureOffset.y;
                verts[idx++] = (col + 1) * gridWidth - textureOffset.x;
                verts[idx] = (row) * gridHeight - textureOffset.y;
                tmpPoly.setVertices(verts);

                Intersector.intersectPolygons(polygon, tmpPoly, intersectionPoly);
                verts = intersectionPoly.getVertices();
                if (verts.length > 0) {
                    parts.add(snapToGrid(verts));
                    ShortArray arr = triangulator.computeTriangles(verts);
                    indices.add(arr.toArray());
                } else {
                    // adding null for key consistancy, needed to get col/row from key
                    // the other alternative is to make parts - IntMap<FloatArray>
                    parts.add(null);
                }
            }
        }
        dirtyGrid = false;
        dirtyAttributes = true;
    }

    /**
     * Builds final vertices with vertex attributes like coordinates, color and region u/v
     */
    private void buildVertices() {
        vertices.clear();
        for (int i = 0; i < parts.size; i++) {
            float verts[] = parts.get(i);
            if (verts == null) continue;

            float[] fullVerts = new float[5 * verts.length / 2];
            int idx = 0;

            int col = i / rows;
            int row = i % rows;

            for (int j = 0; j < verts.length; j += 2) {
                fullVerts[idx++] = verts[j] + currentOffset.x + textureOffset.x + x + originX;
                fullVerts[idx++] = verts[j + 1] + currentOffset.y + textureOffset.y + y + originX;

                fullVerts[idx++] = color.toFloatBits();

                float u = (verts[j] % gridWidth) / gridWidth;
                float v = (verts[j + 1] % gridHeight) / gridHeight;
                if (verts[j] == col * gridWidth + textureOffset.x) u = 0f;
                if (verts[j] == (col + 1) * gridWidth + textureOffset.x) u = 1f;
                if (verts[j + 1] == row * gridHeight + textureOffset.y) v = 0f;
                if (verts[j + 1] == (row + 1) * gridHeight + textureOffset.y) v = 1f;
                u = textureRegion.getU() + (textureRegion.getU2() - textureRegion.getU()) * u;
                v = textureRegion.getV() + (textureRegion.getV2() - textureRegion.getV()) * v;
                fullVerts[idx++] = u;
                fullVerts[idx++] = v;
            }
            vertices.add(fullVerts);
        }
        dirtyAttributes = false;
    }


    /**
     * This is a garbage, due to Intersector returning values slightly different then the grid values
     * Snapping exactly to grid is important, so that during bulidVertices method, it can be figured out
     * if points are on the wall of it's own grid box or not, to set u/v properly.
     * Any other implementations are welcome
     */
    private float[] snapToGrid(float[] vertices) {
        for (int i = 0; i < vertices.length; i += 2) {
            float numX = (vertices[i] / gridWidth) % 1;
            float numY = (vertices[i + 1] / gridHeight) % 1;
            if (numX > 0.99f || numX < 0.01f) {
                vertices[i] = gridWidth * Math.round(vertices[i] / gridWidth);
            }
            if (numY > 0.99f || numY < 0.01f) {
                vertices[i + 1] = gridHeight * Math.round(vertices[i + 1] / gridHeight);
            }
        }

        return vertices;
    }


    /**
     * Offsets polygon to 0 - textureOffset coordinate for ease of calculations, later this is put back on final render
     *
     * @param vertices vertices to offset
     * @return offsetted vertices
     */
    private float[] offset(float[] vertices) {
        float[] result = vertices.clone();
        currentOffset.set(result[0], result[1]);
        for (int i = 0; i < result.length - 1; i += 2) {
            if (currentOffset.x > result[i]) {
                currentOffset.x = result[i];
            }
            if (currentOffset.y > result[i + 1]) {
                currentOffset.y = result[i + 1];
            }
        }
        for (int i = 0; i < result.length; i += 2) {
            result[i] -= currentOffset.x - textureOffset.x;
            result[i + 1] -= currentOffset.y - textureOffset.y;
        }

        return result;
    }

    public void draw(PolygonSpriteBatch batch) {
        if (dirtyGrid) {
            prepareVertices();
        }
        if (dirtyAttributes) {
            buildVertices();
        }

        for (int i = 0; i < vertices.size; i++) {
            batch.draw(textureRegion.getTexture(), vertices.get(i), 0, vertices.get(i).length, indices.get(i), 0, indices.get(i).length);
        }
    }

    public void drawDebug(ShapeRenderer shapes) {
        if (dirtyGrid) {
            prepareVertices();
        }
        if (dirtyAttributes) {
            buildVertices();
        }

        for (int col = 0; col < cols; col++) {

            for (int row = 0; row < rows; row++) {

                float[] verts = new float[8];
                int idx = 0;
                verts[idx++] = col * gridWidth - textureOffset.x;
                verts[idx++] = row * gridHeight - textureOffset.y;
                verts[idx++] = (col) * gridWidth - textureOffset.x;
                verts[idx++] = (row + 1) * gridHeight - textureOffset.y;
                verts[idx++] = (col + 1) * gridWidth - textureOffset.x;
                verts[idx++] = (row + 1) * gridHeight - textureOffset.y;
                verts[idx++] = (col + 1) * gridWidth - textureOffset.x;
                verts[idx] = (row) * gridHeight - textureOffset.y;

                float cellX = col * gridWidth + currentOffset.x + x + originX - textureOffset.x;
                float cellY = row * gridHeight + currentOffset.y + y + originY - textureOffset.y;

                shapes.set(ShapeRenderer.ShapeType.Line);
                shapes.setColor(Color.BLACK);
                shapes.rect(cellX, cellY, gridWidth, gridHeight);
            }

        }
    }

    /**
     * Sets the outline vertices of the polygon
     *
     * @param vertices - vertices of polygon relative to the origin
     */
    public void setVertices(float[] vertices) {
        this.originalVertices = vertices;
        dirtyGrid = true;
    }

    /**
     * Sets the texture region, the size of repeating grid is equal to region size
     *
     * @param textureRegion - texture region mapped on the polygon
     * @param wrapTypeX     - type how the texture region is drawn along the X-Axis
     * @param wrapTypeY     - type how the texture region is drawn along the Y-Axis
     */
    public void setTextureRegion(TextureRegion textureRegion, int wrapTypeX, int wrapTypeY) {
        this.textureRegion = textureRegion;
        this.textureWidth = textureRegion.getRegionWidth();
        this.textureHeight = textureRegion.getRegionHeight();
        setWrapTypeX(wrapTypeX);
        setWrapTypeY(wrapTypeY);
        dirtyGrid = true;
    }

    /**
     * sets the position of the texture relative to the origin
     */
    public void setTextureOffset(float x, float y) {
        this.textureOffset.set(x, y);
        dirtyGrid = true;
    }

    /**
     * sets the position of the texture relative to the origin
     */
    public void setTextureSize(float width, float height) {
        this.textureWidth = width;
        this.textureHeight = height;
        dirtyGrid = true;
    }

    /**
     * Sets the type how the texture region is drawn along the X-Axis
     *
     * @param wrapType - a type of WrapType
     */
    public void setWrapTypeX(int wrapType) {
        WrapType.validate(wrapType);
        this.wrapTypeX = wrapType;
        dirtyGrid = true;
    }

    /**
     * Sets the type how the texture region is drawn along the Y-Axis
     *
     * @param wrapType - a type of WrapType
     */
    public void setWrapTypeY(int wrapType) {
        WrapType.validate(wrapType);
        this.wrapTypeY = wrapType;
        dirtyGrid = true;
    }

    /**
     * @param color - Tint color to be applied to entire polygon
     */
    public void setColor(Color color) {
        this.color.set(color);
        dirtyAttributes = true;
    }

    /**
     * sets the Position in the world
     *
     * @param x - x-coordinate
     * @param y - y-coordinate
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        dirtyAttributes = true;
    }

    /**
     * sets the Origin relative to the Position
     *
     * @param x - x-coordinate
     * @param y - y-coordinate
     */
    public void setOrigin(float x, float y) {
        this.originX = x;
        this.originY = y;
        dirtyAttributes = true;
    }

    /**
     * @param degree - angle in degree counter-clockwise
     */
    public void setRotation(float degree) {
        this.rotation = degree;
        dirtyAttributes = true;
    }

    /**
     * sets the scale along both axises where 1 = normal Size
     */
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        dirtyAttributes = true;
    }

    public int getWrapTypeX() {
        return wrapTypeX;
    }

    public int getWrapTypeY() {
        return wrapTypeY;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public float getTextureOffsetX() {
        return textureOffset.x;
    }

    public float getTextureOffsetY() {
        return textureOffset.y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getRotation() {
        return rotation;
    }

    public Color getColor() {
        return color;
    }
}
