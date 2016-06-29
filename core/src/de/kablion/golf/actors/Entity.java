package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.kablion.golf.data.CollisionData;
import de.kablion.golf.utils.Collision;
import de.kablion.golf.utils.RepeatablePolygonSprite;

public class Entity extends Actor {

    public static final int NONE = 0;
    public static final int CIRCLE = 1;
    public static final int POLYGON = 2;

    private RepeatablePolygonSprite repeatablePolygonSprite = new RepeatablePolygonSprite();
    private int type = NONE;
    private TextureRegion whiteTextureRegion;

    private float radius = 0;

    public Entity(float[] vertices) {
        super();
        setVertices(vertices);
        init();
    }

    private void init() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTextureRegion = new TextureRegion(new Texture(pixmap));
        setColor(Color.PINK);
        setTextureRegion(null);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        repeatablePolygonSprite.draw((PolygonSpriteBatch) batch);
    }

    @Override
    protected void drawDebugBounds(ShapeRenderer shapes) {

        if (!getDebug()) return;
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(getStage().getDebugColor());
        Rectangle bounds = getBoundingRectangle();
        shapes.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        // Cross on the Origin
        float realX = getX();
        float realY = getY();
        shapes.line(realX - 1, realY - 1, realX + 1, realY + 1);
        shapes.line(realX - 1, realY + 1, realX + 1, realY - 1);

        // draw grid and vertices in grid
        //repeatablePolygonSprite.drawDebug(shapes, getStage().getDebugColor());
    }

    @Override
    public void clear() {
        super.clear();
        if (this.repeatablePolygonSprite.getTextureRegion() != null) {
            this.repeatablePolygonSprite.getTextureRegion().getTexture().dispose();
        }
        if (whiteTextureRegion != null) {
            whiteTextureRegion.getTexture().dispose();
        }
    }

    /**
     * Has to be Overridden for actual logic
     *
     * @param ball the ball to checkCollisionWith
     * @return if collision was detected and handled
     */
    public boolean checkCollisionWithBall(Ball ball) {
        return false;
    }

    public CollisionData checkCollisionWith(Entity actor) {
        CollisionData collisionData = null;
        if (checkBoundsIntersect(this, actor)) {
            if (type == CIRCLE) {
                if (actor.getType() == CIRCLE) {
                    collisionData = Collision.checkCircleCircle((Circle) this.getShape(), (Circle) actor.getShape());
                } else if (actor.getType() == POLYGON) {
                    collisionData = Collision.checkCirclePolygon((Circle) this.getShape(), (Polygon) actor.getShape());
                }
            } else if (type == POLYGON) {
                if (actor.getType() == CIRCLE) {
                    collisionData = Collision.checkPolygonCircle((Polygon) this.getShape(), (Circle) actor.getShape());
                } else if (actor.getType() == POLYGON) {
                    collisionData = Collision.checkPolygonPolygon((Polygon) this.getShape(), (Polygon) actor.getShape());
                }
            }
        }

        return collisionData;
    }

    public boolean checkBoundsIntersect(Entity actor1, Entity actor2) {
        boolean intersect = false;
        Rectangle rect1 = actor1.getBoundingRectangle();
        Rectangle rect2 = actor2.getBoundingRectangle();
        if (rect1.x < rect2.x + rect2.getWidth() && rect1.x + rect1.getWidth() > rect2.x) {
            //intersection along y axis
            if (rect1.y < rect2.y + rect2.getHeight() && rect1.y + rect1.getHeight() > rect2.y) {
                //intersection along x axis
                intersect = true;
            }
        }
        return intersect;
    }

    /**
     *
     * @param vertices {x1,y1,x2,y2,x3,y3...} one vertex = Circle , two vertices = Rectangle , more = Polygon
     */
    public void setVertices(float[] vertices) {

        if (vertices == null || vertices.length < 2) {
            type = NONE;
            this.repeatablePolygonSprite.setVertices(null);

        } else {
            float[] finalVerts;
            if (vertices.length == 2 || vertices.length == 3) {
                // type = CIRCLE;
                type = CIRCLE;

                int i = 0;
                this.radius = Vector2.len(vertices[i++], vertices[i]);

                int divisions = 20;
                if (radius > 50) divisions = 50;
                finalVerts = new float[divisions * 2];
                float radiansPerDivision = (360 / divisions) * MathUtils.degreesToRadians;
                for (int division = 0; division < divisions; division++) {
                    finalVerts[division * 2] = (float) Math.cos(radiansPerDivision * division) * this.radius;
                    finalVerts[division * 2 + 1] = (float) Math.sin(radiansPerDivision * division) * this.radius;
                }

            } else if (vertices.length == 4 || vertices.length == 5) {
                // type = RECTANGLE;
                type = POLYGON;

                int i = 0;
                float x = vertices[i++];
                float y = vertices[i++];
                float width = vertices[i++] - x;
                float height = vertices[i] - y;

                finalVerts = new float[8];
                i = 0;

                float halfWidth = width / 2;
                float halfHeight = height / 2;

                // Bottom Left
                finalVerts[i++] = -halfWidth;
                finalVerts[i++] = -halfHeight;

                // Top Left
                finalVerts[i++] = -halfWidth;
                finalVerts[i++] = halfHeight;

                // Top Right
                finalVerts[i++] = halfWidth;
                finalVerts[i++] = halfHeight;

                // Bottom Right
                finalVerts[i++] = halfWidth;
                finalVerts[i] = -halfHeight;

            } else {
                // type = POLYGON;
                type = POLYGON;

                finalVerts = vertices.clone();

            }
            this.repeatablePolygonSprite.setVertices(finalVerts);
        }
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        setTextureRegion(textureRegion, 0, 0, RepeatablePolygonSprite.WrapType.STRETCH, RepeatablePolygonSprite.WrapType.STRETCH);
    }

    public void setTextureRegion(TextureRegion textureRegion, float width, float height, int wrapTypeX, int wrapTypeY) {
        if (textureRegion != null) {
            this.repeatablePolygonSprite.setTextureRegion(textureRegion, width, height, wrapTypeX, wrapTypeY);
            this.repeatablePolygonSprite.setColor(Color.WHITE);
        } else {
            this.repeatablePolygonSprite.setTextureRegion(whiteTextureRegion, width, height, wrapTypeX, wrapTypeY);
            this.repeatablePolygonSprite.setColor(getColor());
        }
    }

    public void setTextureOffset(float textureOffsetX, float textureOffsetY) {
        this.repeatablePolygonSprite.setTextureOffset(textureOffsetX, textureOffsetY);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        this.repeatablePolygonSprite.setX(x);
        positionChanged();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        this.repeatablePolygonSprite.setY(y);
        positionChanged();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        this.repeatablePolygonSprite.setPosition(x, y);
        positionChanged();
    }

    @Override
    public void setPosition(float x, float y, int alignment) {
        setPosition(x, y);
    }

    @Override
    public void moveBy(float x, float y) {
        setPosition(getX() + x, getY() + y);
    }

    @Override
    public void setOriginX(float originX) {
        super.setOriginX(originX);
        this.repeatablePolygonSprite.setOriginX(originX);
    }

    @Override
    public void setOriginY(float originY) {
        super.setOriginY(originY);
        this.repeatablePolygonSprite.setOriginY(originY);
    }

    @Override
    public void setOrigin(float originX, float originY) {
        super.setOrigin(originX, originY);
        this.repeatablePolygonSprite.setOrigin(originX, originY);
    }

    @Override
    public void setRotation(float degrees) {
        super.setRotation(degrees);
        this.repeatablePolygonSprite.setRotation(degrees);
        rotationChanged();
    }

    @Override
    public void setWidth(float width) {}

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        if (repeatablePolygonSprite.getTextureRegion() == null || repeatablePolygonSprite.getTextureRegion().equals(whiteTextureRegion)) {
            this.repeatablePolygonSprite.setColor(color);
        }
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        super.setColor(r, g, b, a);
        if (repeatablePolygonSprite.getTextureRegion() == null || repeatablePolygonSprite.getTextureRegion().equals(whiteTextureRegion)) {
            this.repeatablePolygonSprite.setColor(getColor());
        }

    }

    @Override
    public void setHeight(float height) {}

    @Override
    public void setSize(float width, float height) {}

    @Override
    public void setBounds(float x, float y, float width, float height) {}

    @Override
    public void setOrigin(int alignment) {}

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
        this.repeatablePolygonSprite.setScaleX(scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
        this.repeatablePolygonSprite.setScaleY(scaleY);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        this.repeatablePolygonSprite.setScale(scaleXY, scaleXY);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        super.setScale(scaleX, scaleY);
        this.repeatablePolygonSprite.setScale(scaleX, scaleY);
    }

    @Override
    public void sizeBy(float size) {}

    @Override
    public void sizeBy(float width, float height) {}

    @Override
    public void scaleBy(float scale) {
        super.scaleBy(scale);
        this.repeatablePolygonSprite.scaleBy(scale);
    }

    @Override
    public void scaleBy(float scaleX, float scaleY) {
        super.scaleBy(scaleX, scaleY);
        this.repeatablePolygonSprite.scaleBy(scaleX, scaleY);
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        super.rotateBy(amountInDegrees);
        this.repeatablePolygonSprite.rotateBy(amountInDegrees);
    }

    @Override
    public float getWidth() {
        return getBoundingRectangle().getWidth();
    }

    @Override
    public float getHeight() {
        return getBoundingRectangle().getHeight();
    }

    @Override
    public float getTop() {
        return getBoundingRectangle().getY() + getBoundingRectangle().getHeight();
    }

    @Override
    public float getRight() {
        return getBoundingRectangle().getX() + getBoundingRectangle().getWidth();
    }

    public int getType() {
        return this.type;
    }

    public float getRadius() {
        return this.radius;
    }

    public Shape2D getShape() {
        if (getType() == NONE) {
            return null;
        } else if (getType() == CIRCLE) {
            return new Circle(getX(), getY(), getRadius());
        } else if (getType() == POLYGON) {
            return getRepeatablePolygonSprite().getPolygon();
        } else {
            throw new NullPointerException("The type of this Entity is not valid");
        }
    }

    public RepeatablePolygonSprite getRepeatablePolygonSprite() {
        return this.repeatablePolygonSprite;
    }

    public Rectangle getBoundingRectangle() {
        return this.repeatablePolygonSprite.getBoundingRectangle();
    }
}
