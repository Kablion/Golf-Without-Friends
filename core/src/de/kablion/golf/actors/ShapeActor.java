package de.kablion.golf.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.data.CollisionData;
import de.kablion.golf.utils.RepeatablePolygonSprite;

public class ShapeActor extends Actor {

    public static final int NONE = 0;
    public static final int CIRCLE = 1;
    public static final int RECTANGLE = 2;
    public static final int POLYGON = 3;

    private Shape2D shape;
    private RepeatablePolygonSprite repeatablePolygonSprite = new RepeatablePolygonSprite();
    private int type;
    private TextureRegion whiteTextureRegion;

    public ShapeActor(Shape2D shape, float positionX, float positionY) {
        super();
        init();
        setPosition(positionX, positionY);
        setShape(shape);
    }

    public ShapeActor(Array<Vector2> polygonPoints) {
        super();
        init();
        setShape(findShape(polygonPoints));
    }

    public ShapeActor(Array<Vector2> polygonPoints, float positionX, float positionY) {
        super();
        init();
        setPosition(positionX, positionY);
        setShape(findShape(polygonPoints));
    }

    public ShapeActor() {
        super();
        init();
        setShape(null);
    }

    private void init() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTextureRegion = new TextureRegion(new Texture(pixmap));
        setColor(Color.PINK);
        setTextureRegion(null);
    }

    public static Shape2D findShape(Array<Vector2> polygonPoints) {
        Shape2D result;

        if (polygonPoints == null | polygonPoints.size == 0) {
            return null;

        } else if (polygonPoints.size == 1) {
            // type = CIRCLE;
            result = new Circle(0, 0, polygonPoints.get(0).len());

        } else if (polygonPoints.size == 2) {
            // type = RECTANGLE;
            result = new Rectangle();
            float x = polygonPoints.get(0).x;
            float y = polygonPoints.get(0).y;
            float width = polygonPoints.get(1).x - polygonPoints.get(0).x;
            float height = polygonPoints.get(1).y - polygonPoints.get(0).y;
            result = new Rectangle(x, y, width, height);

        } else {
            // type = POLYGON;
            float[] vertices = new float[polygonPoints.size * 2];
            for (int i = 0; i < polygonPoints.size; i++) {
                vertices[i * 2] = polygonPoints.get(i).x;
                vertices[i * 2 + 1] = polygonPoints.get(i).y;
            }
            result = new Polygon(vertices);


        }

        return result;
    }

    private static int findShapeType(Shape2D shape) {
        int result;

        if (shape == null) {
            result = NONE;
        } else if (shape instanceof Circle) {
            result = CIRCLE;
        } else if (shape instanceof Rectangle) {
            result = RECTANGLE;
        } else if (shape instanceof Polygon) {
            result = POLYGON;
        } else {
            result = NONE;
        }

        return result;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        repeatablePolygonSprite.draw((PolygonSpriteBatch) batch);
    }

    @Override
    protected void drawDebugBounds(ShapeRenderer shapes) {

        if (!getDebug()) return;
        /*shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(getStage().getDebugColor());
        shapes.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), 0);
        // Cross on the Origin
        shapes.line(getOriginX() - 1, getOriginY() - 1, getOriginX() + 1, getOriginY() + 1);
        shapes.line(getOriginX() - 1, getOriginY() + 1, getOriginX() + 1, getOriginY() - 1);*/

        // draw grid and vertices in grid
        repeatablePolygonSprite.drawDebug(shapes, getStage().getDebugColor());
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

    public void setShape(Shape2D shape) {
        this.shape = shape;
        this.type = findShapeType(this.shape);
        updateVertices();
    }

    private void updateVertices() {

        if (type == NONE) {
            this.repeatablePolygonSprite.setVertices(null);
        } else {
            float[] vertices = null;

            if (type == CIRCLE) { ////////////////////////////////// Circle

                Circle circle = (Circle) this.shape;
                int divisions = 20;
                if (circle.radius > 50) divisions = 50;
                vertices = new float[divisions * 2];
                float radiansPerDivision = (360 / divisions) * MathUtils.degreesToRadians;
                for (int division = 0; division < divisions; division++) {
                    vertices[division * 2] = (float) Math.cos(radiansPerDivision * division) * circle.radius;
                    vertices[division * 2 + 1] = (float) Math.sin(radiansPerDivision * division) * circle.radius;
                }

                /*MeshBuilder meshBuilder = new MeshBuilder();
                meshBuilder.begin(new VertexAttributes(new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)));
                MeshPart part1 = meshBuilder.part("part1", GL20.GL_TRIANGLES);
                EllipseShapeBuilder.build(meshBuilder, circle.radius, divisions, 0, 0, 0, 0, 0, 1);

                Mesh mesh = meshBuilder.end();
                vertices = new float[mesh.getNumVertices()*2];
                mesh.getVertices(vertices);

                // remove first (center)
                float[] tempVerts = new float[vertices.length - 2];
                System.arraycopy(vertices,2,tempVerts,0,vertices.length-2);
                vertices = tempVerts;*/

            } else if (type == RECTANGLE) {  ////////////////////////////////// Rectangle
                Rectangle rect = ((Rectangle) this.shape);

                vertices = new float[8];
                int i = 0;

                float halfWidth = rect.getWidth() / 2;
                float halfHeight = rect.getHeight() / 2;

                // Bottom Left
                vertices[i++] = -halfWidth;
                vertices[i++] = -halfHeight;

                // Top Left
                vertices[i++] = -halfWidth;
                vertices[i++] = halfHeight;

                // Top Right
                vertices[i++] = halfWidth;
                vertices[i++] = halfHeight;

                // Bottom Right
                vertices[i++] = halfWidth;
                vertices[i++] = -halfHeight;

            } else if (type == POLYGON) {  ////////////////////////////////// Polygon

                Polygon polygon = (Polygon) this.shape;

                vertices = polygon.getVertices();

            }
            if (vertices != null) {
                this.repeatablePolygonSprite.setVertices(vertices);
            }

        }
    }

    public CollisionData checkCollisionWith(ShapeActor actor) {
        CollisionData collisionData = null;
        if (checkBoundsIntersect(this, actor)) {
            if (type == CIRCLE) {
                if (actor.getType() == CIRCLE) {
                    collisionData = checkCollisionCircleCircle(this, actor);
                } else if (actor.getType() == RECTANGLE) {
                    collisionData = checkCollisionCircleRectangle(this, actor);
                } else if (actor.getType() == POLYGON) {
                    collisionData = checkCollisionCirclePolygon(this, actor);
                }
            } else if (type == RECTANGLE) {
                // First only the ball (type==CIRCLE) detection is needed
            } else if (type == POLYGON) {
                // First only the ball (type==CIRCLE) detection is needed
            }
        }

        return collisionData;
    }

    public boolean checkBoundsIntersect(ShapeActor actor1, ShapeActor actor2) {
        boolean intersect = true;
        Rectangle rect1 = actor1.getBoundingRectangle();
        Rectangle rect2 = actor2.getBoundingRectangle();
        if (rect2.getX() + rect2.getWidth() < rect1.getX() | rect1.getX() + rect1.getWidth() < rect2.getX()) {
            if (rect2.getY() + rect2.getHeight() < rect1.getY() | rect1.getY() + rect1.getHeight() < rect2.getY()) {
                // separated
                intersect = false;
            }
        }
        return intersect;
    }

    public static CollisionData checkCollisionCircleCircle(ShapeActor main, ShapeActor second) {
        CollisionData collisionData = null;
        Vector2 mOrigin = new Vector2(main.getOriginX(), main.getOriginY());
        Vector2 sOrigin = new Vector2(second.getOriginX(), second.getOriginY());
        if (mOrigin.dst(sOrigin) < main.getWidth() / 2 + second.getWidth() / 2) {
            collisionData = new CollisionData();
            collisionData.normalVector.set(new Vector2(mOrigin.x - sOrigin.x, mOrigin.y - sOrigin.y));
            float sumRadius = (main.getWidth() / 2 + second.getWidth() / 2);
            float distance = mOrigin.dst(sOrigin);
            collisionData.overlapDistance = sumRadius - distance;
        }
        return collisionData;
    }

    public static CollisionData checkCollisionCircleRectangle(ShapeActor main, ShapeActor second) {
        CollisionData collisionData = null;

        Vector2 normal = new Vector2();

        int iMinOlap = 0;
        float minOverLap = 1000000;

        Circle circle = (Circle) main.getShape();

        //Polygon polygon = (Polygon) second.getShape();
        Rectangle rect = (Rectangle) second.getShape();

        Vector2 polygonOrigin = new Vector2();
        rect.getCenter(polygonOrigin);

        float halfWidth = rect.getWidth() / 2;
        float halfHeight = rect.getHeight() / 2;

        float[] vertices = new float[8];
        // bottom left
        vertices[0] = -halfWidth;
        vertices[1] = -halfHeight;

        // top left
        vertices[2] = -halfWidth;
        vertices[3] = +halfHeight;

        // top right
        vertices[4] = +halfWidth;
        vertices[5] = +halfHeight;

        // bottom right
        vertices[6] = +halfWidth;
        vertices[7] = -halfHeight;

        Vector2 circlePosition = new Vector2(circle.x, circle.y);

        return checkCollisionCirclePolygon(vertices, polygonOrigin, second.getRotation(), circlePosition, circle.radius);
    }

    public static CollisionData checkCollisionCirclePolygon(ShapeActor main, ShapeActor second) {
        CollisionData collisionData = null;
        Vector2 normal = new Vector2();

        int iMinOlap = 0;
        float minOverLap = 1000000;

        Circle circle = (Circle) main.getShape();

        Polygon polygon = (Polygon) second.getShape();
        float[] vertices = polygon.getVertices();
        Vector2 polygonOrigin = new Vector2(polygon.getOriginX(), polygon.getOriginY());
        Vector2 circlePosition = new Vector2(circle.x, circle.y);

        return checkCollisionCirclePolygon(vertices, polygonOrigin, second.getRotation(), circlePosition, circle.radius);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static CollisionData checkCollisionCirclePolygon(float[] vertices, Vector2 polygonOrigin, float rotation, Vector2 circlePosition, float circleRadius) {
        CollisionData collisionData = null;
        Vector2 normal = new Vector2();

        int iMinOlap = 0;
        float minOverLap = 1000000;

        Vector2 circlePFromPOrigin = new Vector2(circlePosition.x - polygonOrigin.x, circlePosition.y - polygonOrigin.y);

        if (rotation != 0) {
            circlePFromPOrigin.rotate(-rotation);
        }

        Vector2 edgeNormal = new Vector2();

        for (int i = 0; i < vertices.length; i += 2) {
            // Loop through every edge of the Polygon

            if (i == -1) {
                // First Projection along the vector from anker to ballCenter
                edgeNormal.set(circlePFromPOrigin);
            } else if (i + 2 == vertices.length) {
                // Edge from last to first Vertex
                edgeNormal.set(vertices[0] - vertices[i], vertices[1] - vertices[i + 1]);
                // set to left normal
                edgeNormal.rotate90(1);
            } else {
                // edge from current to next Vertex
                edgeNormal.set(vertices[i + 2] - vertices[i], vertices[i + 3] - vertices[i + 1]);
                // set to left normal
                edgeNormal.rotate90(1);
            }
            edgeNormal.setLength(1);

            //setting min max for the polygon on the edgeNormal
            float polygonMin = Vector2.dot(vertices[0], vertices[1], edgeNormal.x, edgeNormal.y);
            float polygonMax = Vector2.dot(vertices[0], vertices[1], edgeNormal.x, edgeNormal.y);

            float projectedCircleCenter = Vector2.dot(circlePFromPOrigin.x, circlePFromPOrigin.y, edgeNormal.x, edgeNormal.y);
            float circleMin = projectedCircleCenter - circleRadius;
            float circleMax = projectedCircleCenter + circleRadius;

            for (int j = 2; j < vertices.length; j += 2) {
                // Loop to get Min and Max Vertices
                float currentProjection = Vector2.dot(vertices[j], vertices[j + 1], edgeNormal.x, edgeNormal.y);

                if (currentProjection > polygonMax) {
                    polygonMax = currentProjection;
                }

                if (currentProjection < polygonMin) {
                    polygonMin = currentProjection;
                }
            }

            float overlapDistance;
            boolean isSeparated = polygonMax < circleMin | circleMax < polygonMin;
            if (isSeparated) {
                return null;
            } else {
                if (polygonMax - circleMin < circleMax - polygonMin) {
                    overlapDistance = polygonMax - circleMin;
                } else {
                    overlapDistance = circleMax - polygonMin;
                }
                if (overlapDistance < minOverLap) {
                    minOverLap = overlapDistance;
                    iMinOlap = i;
                } else if (overlapDistance == minOverLap) {
                    float projectedOld = Vector2.dot(vertices[iMinOlap], vertices[iMinOlap + 1], edgeNormal.x, edgeNormal.y);
                    float projectedNew = Vector2.dot(vertices[i], vertices[i + 1], edgeNormal.x, edgeNormal.y);
                    if (Math.abs(projectedOld - projectedCircleCenter) > Math.abs(projectedNew - projectedCircleCenter)) {
                        // Distance Edge to ball
                        // the new Edge is closer
                        iMinOlap = i;
                    }
                }
            }
        }

        if (iMinOlap == -1) {
            // find nearest Corner
        }


        if (iMinOlap + 2 == vertices.length) {

            Vector2 projectOn = new Vector2(vertices[0] - vertices[iMinOlap], vertices[1] - vertices[iMinOlap + 1]);
            projectOn.setLength(1);
            float projectedMin = Vector2.dot(vertices[iMinOlap], vertices[iMinOlap + 1], projectOn.x, projectOn.y);
            float projectedMax = Vector2.dot(vertices[0], vertices[1], projectOn.x, projectOn.y);
            float projectedCircle = Vector2.dot(circlePFromPOrigin.x, circlePFromPOrigin.y, projectOn.x, projectOn.y);
            if (projectedCircle < projectedMin) {
                // Corner
                normal.set(circlePFromPOrigin.x - vertices[iMinOlap], circlePFromPOrigin.y - vertices[iMinOlap + 1]);

            } else if (projectedCircle > projectedMax) {
                // Corner
                normal.set(circlePFromPOrigin.x - vertices[0], circlePFromPOrigin.y - vertices[1]);
                iMinOlap = 0;

            } else {
                // Edge
                normal.set(vertices[0] - vertices[iMinOlap], vertices[1] - vertices[iMinOlap + 1]);
                // left normal
                normal.rotate90(1);
            }
        } else {
            Vector2 projectOn = new Vector2(vertices[iMinOlap + 2] - vertices[iMinOlap], vertices[iMinOlap + 3] - vertices[iMinOlap + 1]);
            projectOn.setLength(1);
            float projectedMin = Vector2.dot(vertices[iMinOlap], vertices[iMinOlap + 1], projectOn.x, projectOn.y);
            float projectedMax = Vector2.dot(vertices[iMinOlap + 2], vertices[iMinOlap + 3], projectOn.x, projectOn.y);
            float projectedCircle = Vector2.dot(circlePFromPOrigin.x, circlePFromPOrigin.y, projectOn.x, projectOn.y);

            if (projectedCircle < projectedMin) {
                // Corner
                normal.set(circlePFromPOrigin.x - vertices[iMinOlap], circlePFromPOrigin.y - vertices[iMinOlap + 1]);

            } else if (projectedCircle > projectedMax) {
                // Corner
                normal.set(circlePFromPOrigin.x - vertices[iMinOlap + 2], circlePFromPOrigin.y - vertices[iMinOlap + 3]);
                iMinOlap++;

            } else {
                // Edge
                normal.set(vertices[iMinOlap + 2] - vertices[iMinOlap], vertices[iMinOlap + 3] - vertices[iMinOlap + 1]);
                // left normal
                normal.rotate90(1);
                //normal.set(normal.y,-normal.x);
            }
        }
        normal.setLength(1);

        if (rotation != 0) {
            normal.rotate(rotation);
        }

        collisionData = new CollisionData();
        collisionData.normalVector = normal;
        collisionData.overlapDistance = minOverLap;

        return collisionData;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        this.repeatablePolygonSprite.setX(x);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        this.repeatablePolygonSprite.setY(y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        this.repeatablePolygonSprite.setPosition(x, y);
    }

    @Override
    public void setPosition(float x, float y, int alignment) {
        setPosition(x, y);
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
    }

    @Override
    public void setWidth(float width) {
        return;
    }

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
    public void setHeight(float height) {
        return;
    }

    @Override
    public void setSize(float width, float height) {
        return;
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        return;
    }

    @Override
    public void setOrigin(int alignment) {
        return;
    }

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
    public void sizeBy(float size) {
        return;
    }

    @Override
    public void sizeBy(float width, float height) {
        return;
    }

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

    public Shape2D getShape() {
        return this.shape;
    }

    public int getType() {
        return type;
    }

    public RepeatablePolygonSprite getRepeatablePolygonSprite() {
        return this.repeatablePolygonSprite;
    }

    public Rectangle getBoundingRectangle() {
        return this.repeatablePolygonSprite.getBoundingRectangle();
    }
}
