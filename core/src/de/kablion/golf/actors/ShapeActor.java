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
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.Application;
import de.kablion.golf.data.CollisionData;
import de.kablion.golf.utils.RepeatablePolygonSprite;

public class ShapeActor extends Actor {

    //Position attribute - (x, y)
    public static final int POSITION_COMPONENTS = 2;

    //Color attribute - floatBits(r, g, b, a)
    public static final int COLOR_COMPONENTS = 4;

    //Texture attribute - (u, v)
    public static final int TEXTURE_COMPONENTS = 2;

    //Total number of components for all attributes
    public static final int NUM_COMPONENTS = POSITION_COMPONENTS + 1 + TEXTURE_COMPONENTS;

    public static final VertexAttributes VERTEX_ATTRIBUTES = new VertexAttributes(new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.ColorPacked, COLOR_COMPONENTS, ShaderProgram.COLOR_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, TEXTURE_COMPONENTS, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

    public static final int NONE = 0;
    public static final int CIRCLE = 1;
    public static final int RECTANGLE = 2;
    public static final int POLYGON = 3;

    private Shape2D shape;
    private RepeatablePolygonSprite repeatablePolygonSprite;
    private int type;
    private TextureRegion textureRegion;
    private TextureRegion whiteTextureRegion;
    private Mesh mesh;

    private boolean repeatXTexture;
    private boolean repeatYTexture;
    private boolean mirroredXTexture;
    private boolean mirroredYTexture;

    private int wrapTypeX;
    private int wrapTypeY;
    private float textureWidth;
    private float textureHeight;

    private Texture colorTexture;

    private Matrix4 transform = new Matrix4();

    public ShapeActor(Shape2D shape, float positionX, float positionY) {
        super();
        setOrigin(positionX, positionY);
        setShape(shape);
        init();
    }

    public ShapeActor(Array<Vector2> polygonPoints) {
        super();
        setOrigin(0, 0);
        setShape(findShape(polygonPoints, 0, 0));
        init();
    }

    public ShapeActor(Array<Vector2> polygonPoints, float positionX, float positionY) {
        super();
        setOrigin(positionX, positionY);
        setShape(findShape(polygonPoints, positionX, positionY));
        init();
    }

    public ShapeActor() {
        super();
        setShape(null);
        setOrigin(0, 0);
        init();
    }

    private void init() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTextureRegion = new TextureRegion(new Texture(pixmap));
        setColor(Color.PINK);
    }

    public static Shape2D findShape(Array<Vector2> polygonPoints, float positionX, float positionY) {
        Shape2D result;

        if (polygonPoints == null | polygonPoints.size == 0) {
            return null;

        } else if (polygonPoints.size == 1) {
            // type = CIRCLE;
            result = new Circle(positionX, positionY, polygonPoints.get(0).len());

        } else if (polygonPoints.size == 2) {
            // type = RECTANGLE;
            result = new Rectangle();
            float x = polygonPoints.get(0).x + positionX;
            float y = polygonPoints.get(0).y + positionY;
            float width = polygonPoints.get(1).x - polygonPoints.get(0).x;
            float height = polygonPoints.get(1).y - polygonPoints.get(0).y;
            result = new Rectangle(x, y, width, height);

        } else if (polygonPoints.size == 3) {
            // type = TRIANGLE;
            float[] vertices = new float[polygonPoints.size * 2];
            for (int i = 0; i < polygonPoints.size; i++) {
                vertices[i * 2] = polygonPoints.get(i).x;
                vertices[i * 2 + 1] = polygonPoints.get(i).y;
            }
            result = new Polygon(vertices);
            ((Polygon) result).setOrigin(positionX, positionY);

        } else {
            // type = POLYGON;
            float[] vertices = new float[polygonPoints.size * 2];
            for (int i = 0; i < polygonPoints.size; i++) {
                vertices[i * 2] = polygonPoints.get(i).x;
                vertices[i * 2 + 1] = polygonPoints.get(i).y;
            }
            result = new Polygon(vertices);
            ((Polygon) result).setOrigin(positionX, positionY);


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

        /*Gdx.gl.glEnable(GL20.GL_BLEND);

        if (mesh != null & isVisible()) {


            Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT);
            Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT);
            if (mirroredXTexture) {
                Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_MIRRORED_REPEAT);
            }
            if (mirroredYTexture) {
                Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_MIRRORED_REPEAT);
            }

            //render the mesh
            mesh.render(batch.getShader(), GL20.GL_TRIANGLES, 0, mesh.getNumIndices());

        }*/
        if (repeatablePolygonSprite != null) {
            repeatablePolygonSprite.draw((PolygonSpriteBatch) batch);
        }
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

        if (repeatablePolygonSprite == null) return;
        repeatablePolygonSprite.drawDebug(shapes);
    }

    @Override
    public void clear() {
        super.clear();
        if (mesh != null) {
            mesh.dispose();
        }
        if (textureRegion != null) {
            textureRegion.getTexture().dispose();
        }
        if (whiteTextureRegion != null) {
            whiteTextureRegion.getTexture().dispose();
        }
    }

    public int getType() {
        return type;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        setTextureRegion(textureRegion, 0, 0, RepeatablePolygonSprite.WrapType.STRETCH, RepeatablePolygonSprite.WrapType.STRETCH);
    }

    public void setTextureRegion(TextureRegion textureRegion, float width, float height, int wrapTypeX, int wrapTypeY) {
        this.textureRegion = textureRegion;
        if (width != 0) {
            this.repeatXTexture = true;
        }
        if (height != 0) {
            this.repeatYTexture = true;
        }
        this.textureWidth = width;
        this.textureHeight = height;
        this.wrapTypeX = wrapTypeX;
        this.wrapTypeY = wrapTypeY;
        updateSprite();
    }

    public void setShape(Shape2D shape) {
        this.shape = shape;
        type = findShapeType(this.shape);
        updateSprite();
        updateTransformation(0, 0, 0);
    }

    public void updateBounds() {
        if (mesh != null) {
            //setSize(mesh.calculateBoundingBox().getWidth(),mesh.calculateBoundingBox().getHeight());
            BoundingBox boundingBox = mesh.calculateBoundingBox();
            Rectangle boundingRect = calculateMeshBounds(this.mesh);
            setBounds(boundingRect.x, boundingRect.y, boundingRect.getWidth(), boundingRect.getHeight());
        }
    }

    private static Rectangle calculateMeshBounds(Mesh mesh) {
        Rectangle rect = new Rectangle();
        float[] vertices = new float[mesh.getNumVertices() * NUM_COMPONENTS];
        mesh.getVertices(vertices);
        float minX = vertices[0];
        float minY = vertices[1];
        float maxX = vertices[0];
        float maxY = vertices[1];

        for (int i = 5; i < vertices.length; i += NUM_COMPONENTS) {
            float x = vertices[i];
            float y = vertices[i + 1];
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        rect.set(minX, minY, maxX - minX, maxY - minY);
        return rect;
    }

    private float[] getUVRange(Rectangle boundingRect) {
        float minU, minV, maxU, maxV;
        minU = 0;
        minV = 0;
        maxU = 1;
        maxV = 1;
        if (repeatXTexture) {
            minU = boundingRect.getWidth() - (boundingRect.getWidth() / textureWidth) / 2;
            maxU = boundingRect.getWidth() + (boundingRect.getWidth() / textureWidth) / 2;
        }
        if (repeatYTexture) {
            minV = boundingRect.getHeight() - (boundingRect.getHeight() / textureHeight) / 2;
            maxV = boundingRect.getHeight() + (boundingRect.getHeight() / textureHeight) / 2;
        }
        return new float[]{minU, minV, maxU, maxV};
    }

    private float[] getUVVertices(float[] xyVertices, Rectangle boundingRect) {
        float[] uv = new float[xyVertices.length];
        int MINU = 0, MINV = 1, MAXU = 2, MAXV = 3;

        float[] uvRange = getUVRange(boundingRect);


        float uRangeWidth = uvRange[MAXU] - uvRange[MINU];
        float vRangeWidth = uvRange[MAXV] - uvRange[MINV];

        for (int i = 0; i < xyVertices.length; i += 2) {
            // U
            float inPercent = (boundingRect.x - xyVertices[i]) / boundingRect.getWidth();
            uv[i] = uRangeWidth * inPercent + uvRange[MINU];

            // V
            inPercent = (boundingRect.y - xyVertices[i + 1]) / boundingRect.getHeight();
            uv[i + 1] = vRangeWidth * inPercent + uvRange[MINV];
        }

        return uv;
    }

    private static float[] getUnprojectedVertices(float[] projVertices, Vector3 origin) {
        float[] unprojVertices = new float[projVertices.length];

        for (int i = 0; i < projVertices.length; i += 2) {
            unprojVertices[i] = projVertices[i] + origin.x;
            unprojVertices[i + 1] = projVertices[i + 1] + origin.y;
        }
        return unprojVertices;
    }


    private void updateMesh() {
        this.mesh = null;
        repeatablePolygonSprite = null;

        if (type == NONE) {
        } else {
            repeatablePolygonSprite = new RepeatablePolygonSprite();
            MeshBuilder meshBuilder = new MeshBuilder();
            meshBuilder.begin(VERTEX_ATTRIBUTES);
            meshBuilder.setColor(Color.WHITE);

            if (type == CIRCLE) { ////////////////////////////////// Circle
                Circle circle = (Circle) this.shape;
                int divisions = 20;
                if (circle.radius > 50) divisions = 50;
                MeshPart part1 = meshBuilder.part("part1", GL20.GL_TRIANGLES);

                float[] uvRange = getUVRange(new Rectangle(circle.x - circle.radius,
                        circle.y - circle.radius,
                        circle.radius * 2,
                        circle.radius * 2));
                meshBuilder.setUVRange(uvRange[2], uvRange[3], uvRange[0], uvRange[1]);

                //EllipseShapeBuilder.build(meshBuilder, circle.radius, divisions, circle.x, circle.y, 0, 0, 0, 1);
                EllipseShapeBuilder.build(meshBuilder, circle.radius, divisions, 0, 0, 0, 0, 0, 1);

            } else if (type == RECTANGLE) {  ////////////////////////////////// Rectangle
                Rectangle rect = ((Rectangle) this.shape);

                MeshPart part1 = meshBuilder.part("part1", GL20.GL_TRIANGLES);
                /*Vector3 bottomLeft = new Vector3(rect.getX(), rect.getY(), 0);
                Vector3 topRight = new Vector3(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), 0);
                Vector3 bottomRight = new Vector3(rect.getX() + rect.getWidth(), rect.getY(), 0);
                Vector3 topleft = new Vector3(rect.getX(), rect.getY() + rect.getHeight(), 0);*/
                Vector3 bottomLeft = new Vector3(-rect.getWidth() / 2, -rect.getHeight() / 2, 0);
                Vector3 topRight = new Vector3(rect.getWidth() / 2, rect.getHeight() / 2, 0);
                Vector3 bottomRight = new Vector3(rect.getWidth() / 2, -rect.getHeight() / 2, 0);
                Vector3 topleft = new Vector3(-rect.getWidth() / 2, rect.getHeight() / 2, 0);
                Vector3 normal = new Vector3(0, 0, 1);

                float[] uvRange = getUVRange(rect);
                meshBuilder.setUVRange(uvRange[0], uvRange[1], uvRange[2], uvRange[3]);

                meshBuilder.rect(bottomLeft, bottomRight, topRight, topleft, normal);

            } else if (type == POLYGON) {  ////////////////////////////////// Polygon

                Polygon polygon = (Polygon) this.shape;

                Vector3 originP = new Vector3(getOriginX(), getOriginY(), 0);
                float[] vertices = polygon.getVertices();
                float[] uv = getUVVertices(vertices, polygon.getBoundingRectangle());


                Vector2 centerP = new Vector2();
                centerP = GeometryUtils.polygonCentroid(vertices, 0, vertices.length, centerP);
                //getCentroid(vertices);
                float[] centerUV = getUVVertices(new float[]{centerP.x, centerP.y}, polygon.getBoundingRectangle());
                VertexInfo centerV = new VertexInfo();
                centerV.setPos(new Vector3(centerP, 0));
                centerV.setUV(centerUV[0], centerUV[1]);

                for (int i = 0; i < vertices.length; i += 2) {
                    MeshPart part = meshBuilder.part("part" + i / 2, GL20.GL_TRIANGLES);
                    Vector3 p1 = new Vector3(vertices[i], vertices[i + 1], 0);
                    VertexInfo v1 = new VertexInfo();
                    v1.setPos(p1);
                    v1.setUV(uv[i], uv[i + 1]);
                    Vector3 p2;
                    VertexInfo v2 = new VertexInfo();
                    if (i + 2 < vertices.length) {
                        p2 = new Vector3(vertices[i + 2], vertices[i + 3], 0);
                        v2.setUV(uv[i + 2], uv[i + 3]);
                    } else {
                        p2 = new Vector3(vertices[0], vertices[1], 0);
                        v2.setUV(uv[0], uv[1]);
                    }
                    v2.setPos(p2);
                    meshBuilder.triangle(centerV, v1, v2);
                    //meshBuilder.triangle(origin, p1, p2);
                }

            }

            this.mesh = meshBuilder.end();

            // transforming the mesh
            /*this.transform.idt();
            if (getRotation() != 0) {
                // rotating
                this.transform = new Matrix4();
                this.transform.rotate(0, 0, 1, getRotation());
                this.mesh.transform(transform);
            }
            // translate from 0,0 to to Origin
            this.transform = new Matrix4();
            this.transform.translate(getOriginX(), getOriginY(), 0);
            this.mesh.transform(transform);*/

        }
    }

    private void updateSprite() {

        if (type == NONE) {
            repeatablePolygonSprite = null;
        } else {
            float[] vertices = null;

            if (type == CIRCLE) { ////////////////////////////////// Circle
                MeshBuilder meshBuilder = new MeshBuilder();
                meshBuilder.begin(new VertexAttributes(new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE)));

                Circle circle = (Circle) this.shape;
                int divisions = 20;
                //if (circle.radius > 50) divisions = 50;
                MeshPart part1 = meshBuilder.part("part1", GL20.GL_TRIANGLES);

                //EllipseShapeBuilder.build(meshBuilder, circle.radius, divisions, circle.x, circle.y, 0, 0, 0, 1);
                EllipseShapeBuilder.build(meshBuilder, circle.radius, divisions, 0, 0, 0, 0, 0, 1);

                Mesh mesh = meshBuilder.end();
                vertices = new float[mesh.getNumVertices()];
                mesh.getVertices(vertices);

                // remove first (center)
                float[] tempVerts = new float[vertices.length - 2];
                for (int i = 0; i < tempVerts.length; i++) {
                    tempVerts[i] = vertices[i + 2];
                }
                vertices = tempVerts;

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
                this.repeatablePolygonSprite = new RepeatablePolygonSprite();
                this.repeatablePolygonSprite.setVertices(vertices);
                if (this.textureRegion != null) {
                    this.repeatablePolygonSprite.setTextureRegion(textureRegion, wrapTypeX, wrapTypeY);
                    this.repeatablePolygonSprite.setColor(Color.WHITE);
                } else {
                    this.repeatablePolygonSprite.setTextureRegion(whiteTextureRegion, RepeatablePolygonSprite.WrapType.STRETCH, RepeatablePolygonSprite.WrapType.STRETCH);
                    this.repeatablePolygonSprite.setColor(this.getColor());
                }
                this.repeatablePolygonSprite.setPosition(getX(), getY());
                this.repeatablePolygonSprite.setOrigin(getOriginX(), getOriginY());
                this.repeatablePolygonSprite.setTextureSize(textureWidth, textureHeight);
                //this.repeatablePolygonSprite.setTextureOffset(50,50);
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
        if (actor2.getX() + actor2.getWidth() < actor1.getX() | actor1.getX() + actor1.getWidth() < actor2.getX()) {
            if (actor2.getY() + actor2.getHeight() < actor1.getY() | actor1.getY() + actor1.getHeight() < actor2.getY()) {
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
        setOriginX(x);
    }

    @Override
    public void setY(float y) {
        setOriginY(y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setOrigin(x, y);
    }

    @Override
    public void setPosition(float x, float y, int alignment) {
        setOrigin(alignment);
        setOrigin(x, y);
    }

    @Override
    public void setOriginX(float originX) {
        float oldX = getOriginX();
        super.setOriginX(originX);
        updateTransformation(oldX, getOriginY(), getRotation());
    }

    @Override
    public void setOriginY(float originY) {
        float oldY = getOriginY();
        super.setOriginY(originY);
        updateTransformation(getOriginX(), oldY, getRotation());
    }

    @Override
    public void setOrigin(float originX, float originY) {
        float oldX = getOriginX();
        float oldY = getOriginY();
        super.setOrigin(originX, originY);
        updateTransformation(oldX, oldY, getRotation());
    }

    @Override
    public void setRotation(float degrees) {
        float oldDegrees = getRotation();
        super.setRotation(degrees);
        updateTransformation(getOriginX(), getOriginY(), oldDegrees);
    }

    public void updateTransformation(float oldOriginX, float oldOriginY, float oldDegrees) {
        if (mesh != null) {
            //translate to 0,0
            this.transform = new Matrix4();
            this.transform.translate(-oldOriginX, -oldOriginY, 0);
            this.mesh.transform(this.transform);
            // rotate to 0 Rotation
            this.transform = new Matrix4();
            this.transform.rotate(0, 0, 1, -oldDegrees);
            this.mesh.transform(this.transform);
            // rotate with new Rotation
            this.transform = new Matrix4();
            this.transform.rotate(0, 0, 1, getRotation());
            this.mesh.transform(this.transform);
            // translate back to Origin
            this.transform = new Matrix4();
            this.transform.translate(getOriginX(), getOriginY(), 0);
            this.mesh.transform(this.transform);
        }
        if (type == CIRCLE) {
            Circle circle = (Circle) getShape();
            circle.x = getOriginX();
            circle.y = getOriginY();
        } else if (type == RECTANGLE) {
            Rectangle rect = (Rectangle) getShape();
            rect.setCenter(getOriginX(), getOriginY());
        } else if (type == POLYGON) {
            Polygon polygon = (Polygon) getShape();
            polygon.setOrigin(getOriginX(), getOriginY());
        }
        updateBounds();
    }

    public Shape2D getShape() {
        return this.shape;
    }
}
