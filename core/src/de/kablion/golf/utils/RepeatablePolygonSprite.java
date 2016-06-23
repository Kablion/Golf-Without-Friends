package de.kablion.golf.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

/**
 * Renders polygon filled with a repeating TextureRegion with specified density
 * Without causing an additional flush or render call
 *
 * @author Avetis Zakharyan (concept and first version)
 * @author Kablion (rewrite)
 *         <p/>
 *         todo:
 *         rotation
 *         scale
 */
public class RepeatablePolygonSprite {

    public final static int DEFAULT_WRAP_TYPE = WrapType.REPEAT;

    private final Color color = new Color(Color.WHITE);

    private int wrapTypeX = DEFAULT_WRAP_TYPE;
    private int wrapTypeY = DEFAULT_WRAP_TYPE;

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
    private Vector2 gridOffset = new Vector2();
    private float gridWidth, gridHeight;
    private Vector2 buildOffset = new Vector2();

    private float x = 0, y = 0;
    private float originX = 0, originY = 0;
    private float scaleX = 1f, scaleY = 1f;
    private float rotation = 0;
    private Rectangle bounds = new Rectangle();

    /**
     * calculates the grid and the parts in relation to the texture Origin
     */
    private void prepareVertices() {
        parts.clear();
        if (originalVertices == null) return;
        if (textureRegion == null) return;
        float[] vertices = originalVertices.clone();

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

        polygon.setVertices(offset(vertices));

        boundRect = polygon.getBoundingRectangle();

        cols = (int) (Math.ceil(boundRect.getWidth() / gridWidth));
        if (boundRect.getX() + boundRect.getWidth() > (cols + gridOffset.x) * gridWidth) cols++;
        rows = (int) Math.ceil(boundRect.getHeight() / gridHeight);
        if (boundRect.getY() + boundRect.getHeight() > (rows + gridOffset.y) * gridHeight) rows++;

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                float[] verts = new float[8];
                idx = 0;
                int offsettedCol = col + (int) gridOffset.x;
                int offsettedRow = row + (int) gridOffset.y;

                verts[idx++] = offsettedCol * gridWidth;
                verts[idx++] = offsettedRow * gridHeight;

                verts[idx++] = (offsettedCol) * gridWidth;
                verts[idx++] = (offsettedRow + 1) * gridHeight;

                verts[idx++] = (offsettedCol + 1) * gridWidth;
                verts[idx++] = (offsettedRow + 1) * gridHeight;

                verts[idx++] = (offsettedCol + 1) * gridWidth;
                verts[idx] = (offsettedRow) * gridHeight;

                tmpPoly.setVertices(verts);

                Intersector.intersectPolygons(polygon, tmpPoly, intersectionPoly);
                verts = intersectionPoly.getVertices();
                if (verts.length > 0) {
                    parts.add(snapToGrid(verts, offsettedRow, offsettedCol));
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
        if (textureRegion == null) return;
        if (originalVertices == null) return;
        for (int i = 0; i < parts.size; i++) {
            float verts[] = parts.get(i);
            if (verts == null) continue;

            float[] fullVerts = new float[5 * verts.length / 2];
            int idx = 0;

            int col = i / rows;
            int row = i % rows;
            int offsettedCol = col + (int) gridOffset.x;
            int offsettedRow = row + (int) gridOffset.y;

            for (int j = 0; j < verts.length; j += 2) {
                fullVerts[idx++] = verts[j] + buildOffset.x + textureOffset.x + x + originX;
                fullVerts[idx++] = verts[j + 1] + buildOffset.y + textureOffset.y + y + originY;

                fullVerts[idx++] = color.toFloatBits();

                float inGridX = verts[j] - offsettedCol * gridWidth;
                float inGridY = verts[j + 1] - offsettedRow * gridHeight;
                float u = inGridX / gridWidth;
                float v = inGridY / gridHeight;
                if (u > 1.0f) u = 1.0f;
                if (v > 1.0f) v = 1.0f;
                if (u < 0.0f) u = 0.0f;
                if (v < 0.0f) v = 0.0f;
                // (col & 1 == 0) == true : col is even
                if (wrapTypeX == WrapType.REPEAT_MIRRORED & (col & 1) == 0) u = 1 - u;
                if (wrapTypeY == WrapType.REPEAT_MIRRORED & (row & 1) == 0) v = 1 - v;

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
    private float[] snapToGrid(float[] vertices, int row, int col) {
        float[] resultVerts = vertices.clone();
        for (int i = 0; i < resultVerts.length; i += 2) {
            float inGridX = resultVerts[i] - col * gridWidth;
            float inGridY = resultVerts[i + 1] - row * gridHeight;
            float inGridXFraction = inGridX / gridWidth;
            float inGridYFraction = inGridY / gridHeight;
            if (inGridXFraction != 1 & inGridXFraction > 0.99f) {
                resultVerts[i] = (col + 1) * gridWidth;
            } else if (inGridXFraction != 0 & inGridXFraction < 0.01f) {
                resultVerts[i] = col * gridWidth;
            }
            if (inGridYFraction != 1 & inGridYFraction > 0.99f) {
                resultVerts[i + 1] = (row + 1) * gridHeight;
            } else if (inGridYFraction != 0 & inGridYFraction < 0.01f) {
                resultVerts[i + 1] = row * gridHeight;
            }
        }

        return resultVerts;
    }

    /**
     * Offsets polygon to 0 - textureOffset coordinate for ease of calculations, later this is put back on final render
     *
     * @param vertices vertices to offset
     * @return offsetted vertices
     */
    private float[] offset(float[] vertices) {
        float[] result = vertices.clone();

        buildOffset.x = bounds.x - x - originX;
        buildOffset.y = bounds.y - y - originY;

        for (int i = 0; i < result.length; i += 2) {
            result[i] -= (buildOffset.x + textureOffset.x);
            result[i + 1] -= (buildOffset.y + textureOffset.y);
        }

        gridOffset.x = (int) Math.floor(-(textureOffset.x / gridWidth));
        gridOffset.y = (int) Math.floor(-(textureOffset.y / gridHeight));

        return result;
    }

    public void draw(PolygonSpriteBatch batch) {
        if (dirtyGrid || parts.size == 0) {
            prepareVertices();
        }
        if (dirtyAttributes || vertices.size == 0) {
            buildVertices();
        }

        for (int i = 0; i < vertices.size; i++) {
            batch.draw(textureRegion.getTexture(), vertices.get(i), 0, vertices.get(i).length, indices.get(i), 0, indices.get(i).length);
        }
    }

    public void drawDebug(ShapeRenderer shapes, Color color) {
        if (dirtyGrid) {
            prepareVertices();
        }
        if (dirtyAttributes) {
            buildVertices();
        }

        // draw grid
        for (int i = 0; i < vertices.size; i++) {

            // draw vertices in grid
            shapes.setColor(color);
            float[] curVerts = vertices.get(i);
            short[] curIndices = this.indices.get(i);
            for (int j = 0; j < curIndices.length; j += 3) {
                float x1 = curVerts[curIndices[j] * 5];
                float y1 = curVerts[curIndices[j] * 5 + 1];
                float x2 = curVerts[curIndices[j + 1] * 5];
                float y2 = curVerts[curIndices[j + 1] * 5 + 1];
                float x3 = curVerts[curIndices[j + 2] * 5];
                float y3 = curVerts[curIndices[j + 2] * 5 + 1];
                shapes.triangle(x1, y1, x2, y2, x3, y3);
            }


            int col = i / rows;
            int row = i % rows;
            int offsettedCol = col + (int) gridOffset.x;
            int offsettedRow = row + (int) gridOffset.y;

            // draw grid
            float cellX = offsettedCol * gridWidth + buildOffset.x + textureOffset.x + x + originX;
            float cellY = offsettedRow * gridHeight + buildOffset.y + textureOffset.y + y + originY;
            shapes.set(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.BLACK);
            shapes.rect(cellX, cellY, gridWidth, gridHeight);

            //draw cross on grid 0/0
            shapes.setColor(Color.RED);
            shapes.line(buildOffset.x + textureOffset.x + x + originX - 2, buildOffset.y + textureOffset.y + y + originY - 2,
                    buildOffset.x + textureOffset.x + x + originX + 2, buildOffset.y + textureOffset.y + y + originY + 2);
            shapes.line(buildOffset.x + textureOffset.x + x + originX - 2, buildOffset.y + textureOffset.y + y + originY + 2,
                    buildOffset.x + textureOffset.x + x + originX + 2, buildOffset.y + textureOffset.y + y + originY - 2);
        }
    }

    /**
     * Sets the outline vertices of the polygon
     *
     * @param vertices - vertices of polygon relative to the origin
     */
    public void setVertices(float[] vertices) {
        this.originalVertices = vertices;

        if (vertices != null) {
            Polygon polygon = new Polygon(vertices);
            polygon.translate(x + originX, y + originY);
            this.bounds = polygon.getBoundingRectangle();
        }
        dirtyGrid = true;
    }

    /**
     * Sets the texture region, the size of repeating grid is equal to region size
     *
     * @param textureRegion - texture region mapped on the polygon
     * @param wrapTypeX     - WrapType how the texture region is drawn along the X-Axis
     * @param wrapTypeY     - WrapType how the texture region is drawn along the Y-Axis
     */
    public void setTextureRegion(TextureRegion textureRegion, int wrapTypeX, int wrapTypeY) {
        setTextureRegion(textureRegion);
        setWrapTypeX(wrapTypeX);
        setWrapTypeY(wrapTypeY);
    }

    /**
     * Sets the texture region, the size of repeating grid is equal to region size
     *
     * @param textureRegion - texture region mapped on the polygon
     * @param textureWidth  - width of the repeating region
     * @param textureHeight - height of the repeating region
     */
    public void setTextureRegion(TextureRegion textureRegion, float textureWidth, float textureHeight) {
        setTextureRegion(textureRegion);
        setTextureSize(textureWidth, textureHeight);
    }

    /**
     * Sets the texture region, the size of repeating grid is equal to region size
     *
     * @param textureRegion - texture region mapped on the polygon
     * @param textureWidth  - width of the repeating region
     * @param textureHeight - height of the repeating region
     * @param wrapTypeX     - WrapType how the texture region is drawn along the X-Axis
     * @param wrapTypeY     - WrapType how the texture region is drawn along the Y-Axis
     */
    public void setTextureRegion(TextureRegion textureRegion, float textureWidth, float textureHeight, int wrapTypeX, int wrapTypeY) {
        setTextureRegion(textureRegion);
        setTextureSize(textureWidth, textureHeight);
        setWrapTypeX(wrapTypeX);
        setWrapTypeY(wrapTypeY);
    }

    /**
     * Sets the texture region, the size of repeating grid is equal to region size
     *
     * @param textureRegion - texture region mapped on the polygon
     */
    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
        if (this.textureWidth == 0) this.textureWidth = textureRegion.getRegionWidth();
        if (this.textureHeight == 0) this.textureHeight = textureRegion.getRegionHeight();
        dirtyGrid = true;
    }

    /**
     * Sets the position of the texture where 0 is the bottom left corner of the bounding rectangle
     */
    public void setTextureOffset(float x, float y) {
        this.textureOffset.set(x, y);
        dirtyGrid = true;
    }

    /** Sets the to be drawn width and height of the texture */
    public void setTextureSize(float width, float height) {
        this.textureWidth = width;
        this.textureHeight = height;
        dirtyGrid = true;
    }

    /** Sets the sprite's position in the world */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        dirtyAttributes = true;
    }

    /**
     * Sets the sprite's x position in the world
     */
    public void setX(float x) {
        this.x = x;
        dirtyAttributes = true;
    }

    /**
     * Sets the sprite's y position in the world
     */
    public void setY(float y) {
        this.y = y;
        dirtyAttributes = true;
    }

    /**
     * Sets the sprite's position in the world
     */
    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
        dirtyAttributes = true;
    }

    /** Sets the origin in relation to the sprite's position for scaling and rotation. */
    public void setOrigin(float x, float y) {
        this.originX = x;
        this.originY = y;
        dirtyAttributes = true;
    }

    /**
     * Sets the origin x in relation to the sprite's position for scaling and rotation.
     */
    public void setOriginX(float x) {
        this.originX = x;
        dirtyAttributes = true;
    }

    /**
     * Sets the origin y in relation to the sprite's position for scaling and rotation.
     */
    public void setOriginY(float y) {
        this.originY = y;
        dirtyAttributes = true;
    }

    /** Sets the scale along both axises where 1 = normal Size */
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        dirtyAttributes = true;
    }

    /**
     * Sets the scale along the x axis where 1 = normal Size
     */
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
        dirtyAttributes = true;
    }

    /**
     * Sets the scale along the y axis where 1 = normal Size
     */
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
        dirtyAttributes = true;
    }

    /**
     * Adds the specified scale to the current scale.
     */
    public void scaleBy(float scaleXY) {
        this.scaleX += scaleXY;
        this.scaleY += scaleXY;
        dirtyAttributes = true;
    }

    /**
     * Adds the specified scale to the current scale.
     */
    public void scaleBy(float scaleX, float scaleY) {
        this.scaleX += scaleX;
        this.scaleY += scaleY;
        dirtyAttributes = true;
    }

    public void setRotation(float degree) {
        this.rotation = degree;
        dirtyAttributes = true;
    }

    /**
     * Adds the specified rotation to the current rotation.
     */
    public void rotateBy(float amountInDegrees) {
        this.rotation += amountInDegrees;
        dirtyAttributes = true;
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

    /**
     * Returns the packed vertices, colors, and texture coordinates for this sprite.
     */
    public Array<float[]> getVertices() {
        return vertices;
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    public static class WrapType {
        /**
         * The Type how a texture is drawn over the Polygon along one axis
         */

        public static final int STRETCH = 0;
        public static final int REPEAT = 1;
        public static final int REPEAT_MIRRORED = 2;

        public static void validate(int type) {
            if (!((type == STRETCH) | (type == REPEAT) | (type == REPEAT_MIRRORED))) {
                throw new IllegalArgumentException("The given type is not valid: " + type);
            }
        }
    }
}
