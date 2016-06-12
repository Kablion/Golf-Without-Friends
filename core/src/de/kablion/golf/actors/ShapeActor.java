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
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class ShapeActor extends Actor {

    //Position attribute - (x, y)
    public static final int POSITION_COMPONENTS = 2;

    //Color attribute - floatBits(r, g, b, a)
    public static final int COLOR_COMPONENTS = 4;

    //Texture attribute - (u, v)
    public static final int TEXTURE_COMPONENTS = 2;

    //Total number of components for all attributes
    public static final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS + TEXTURE_COMPONENTS;

    public static final VertexAttributes VERTEX_ATTRIBUTES = new VertexAttributes(new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.ColorPacked, COLOR_COMPONENTS, ShaderProgram.COLOR_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, TEXTURE_COMPONENTS, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

    public static final int NONE = 0;
    public static final int CIRCLE = 1;
    public static final int RECTANGLE = 2;
    public static final int TRIANGLE = 3;
    public static final int POLYGON = 4;


    private Shape2D shape;
    private int type;
    private Texture texture;
    private Mesh mesh;

    private boolean repeatTexture;
    private float textureWidth;
    private float textureHeight;

    private Texture colorTexture;

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
            ((Polygon) result).setPosition(positionX, positionY);

        } else {
            // type = POLYGON;
            float[] vertices = new float[polygonPoints.size * 2];
            for (int i = 0; i < polygonPoints.size; i++) {
                vertices[i * 2] = polygonPoints.get(i).x;
                vertices[i * 2 + 1] = polygonPoints.get(i).y;
            }
            result = new Polygon(vertices);
            ((Polygon) result).setPosition(positionX, positionY);


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
            if (((Polygon) shape).getVertices().length == 6) {
                result = TRIANGLE;
            } else {
                result = POLYGON;
            }
        } else {
            result = NONE;
        }

        return result;
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(getColor());
        pixmap.fill();
        this.colorTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        super.setColor(r, g, b, a);
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(getColor());
        pixmap.fill();
        this.colorTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void init() {
        setColor(Color.PINK);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Gdx.gl.glEnable(GL20.GL_BLEND);

        if (mesh != null) {

            if (texture == null) {
                colorTexture.bind();
            } else {
                texture.bind();
            }

            Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT);
            Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT);

            //render the mesh
            mesh.render(batch.getShader(), GL20.GL_TRIANGLES, 0, mesh.getNumIndices());

        }
    }

    @Override
    public void clear() {
        super.clear();
        if (mesh != null) {
            mesh.dispose();
        }
        if (texture != null) {
            texture.dispose();
        }
        if (colorTexture != null) {
            colorTexture.dispose();
        }
    }

    public int getType() {
        return type;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        this.repeatTexture = false;
    }

    public void setTexture(Texture texture, float width, float height) {
        this.texture = texture;
        this.repeatTexture = true;
        this.textureWidth = width;
        this.textureHeight = height;
        updateMesh();
    }

    public void setShape(Shape2D shape) {
        this.shape = shape;
        type = findShapeType(this.shape);
        updateMesh();
        if (mesh != null) {
            //setSize(mesh.calculateBoundingBox().getWidth(),mesh.calculateBoundingBox().getHeight());
            BoundingBox boundingBox = mesh.calculateBoundingBox();
            float left = getOriginX() - boundingBox.getWidth() / 2;
            float bottom = getOriginY() - boundingBox.getHeight() / 2;
            setBounds(left, bottom, boundingBox.getWidth(), boundingBox.getHeight());
        }
    }

    private void updateMesh() {
        this.mesh = null;


        if (type == NONE) {

        } else {
            MeshBuilder meshBuilder = new MeshBuilder();
            meshBuilder.begin(VERTEX_ATTRIBUTES);
            meshBuilder.setColor(Color.WHITE);

            if (repeatTexture) {
                //meshBuilder.setUVRange(0, 0, polygon.getBoundingRectangle().getWidth() / textureWidth, polygon.getBoundingRectangle().getHeight() / textureHeight);
                meshBuilder.setUVRange(0, 0, 1, 1);
            } else {
                meshBuilder.setUVRange(1, 1, 0, 0);
            }
            if (type == CIRCLE) { ////////////////////////////////// Circle
                Circle circle = (Circle) this.shape;
                int divisions = 20;
                MeshPart part1 = meshBuilder.part("part1", GL20.GL_TRIANGLES);
                if (repeatTexture) {
                    meshBuilder.setUVRange(circle.radius * 2 / textureWidth, circle.radius * 2 / textureHeight, 0, 0);
                } else {
                    meshBuilder.setUVRange(1, 1, 0, 0);
                }

                EllipseShapeBuilder.build(meshBuilder, circle.radius, divisions, circle.x, circle.y, 0, 0, 0, 1);

            } else if (type == RECTANGLE) {  ////////////////////////////////// Rectangle
                Rectangle rect = ((Rectangle) this.shape);
                MeshPart part1 = meshBuilder.part("part1", GL20.GL_TRIANGLES);

                Vector3 bottomLeft = new Vector3(rect.getX(), rect.getY(), 0);
                Vector3 topRight = new Vector3(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), 0);
                Vector3 bottomRight = new Vector3(rect.getX() + rect.getWidth(), rect.getY(), 0);
                Vector3 topleft = new Vector3(rect.getX(), rect.getY() + rect.getHeight(), 0);

                Vector3 normal = new Vector3(0, 0, 1);

                if (repeatTexture) {
                    meshBuilder.setUVRange(0, 0, rect.getWidth() / textureWidth, rect.getHeight() / textureHeight);
                } else {
                    meshBuilder.setUVRange(0, 0, 1, 1);
                }

                meshBuilder.rect(bottomLeft, bottomRight, topRight, topleft, normal);

            } else if (type == TRIANGLE) { ////////////////////////////////// Triangle
                Polygon polygon = (Polygon) this.shape;

                float[] vertices = polygon.getVertices();

                Vector3 origin = new Vector3(getOriginX(), getOriginY(), 0);
                MeshPart part = meshBuilder.part("part1", GL20.GL_TRIANGLES);
                Vector3 p1 = new Vector3(vertices[0] + getOriginX(), vertices[1] + getOriginY(), 0);
                Vector3 p2 = new Vector3(vertices[2] + getOriginX(), vertices[3] + getOriginY(), 0);
                Vector3 p3 = new Vector3(vertices[4] + getOriginX(), vertices[5] + getOriginY(), 0);
                meshBuilder.setUVRange(0, 0, 1, 1);
                meshBuilder.triangle(p1, p2, p3);
                meshBuilder.setUVRange(0, 0, 1, 1);

            } else if (type == POLYGON) {  ////////////////////////////////// Polygon

                Polygon polygon = (Polygon) this.shape;

                float[] vertices = polygon.getVertices();

                if (repeatTexture) {
                    //meshBuilder.setUVRange(0, 0, polygon.getBoundingRectangle().getWidth() / textureWidth, polygon.getBoundingRectangle().getHeight() / textureHeight);
                    meshBuilder.setUVRange(1, 1, 0, 0);
                } else {
                    meshBuilder.setUVRange(1, 1, 0, 0);
                }

                Vector3 origin = new Vector3(getOriginX(), getOriginY(), 0);
                for (int i = 0; i < vertices.length; i += 2) {
                    MeshPart part = meshBuilder.part("part" + i / 2, GL20.GL_TRIANGLES);
                    Vector3 p1 = new Vector3(vertices[i] + origin.x, vertices[i + 1] + origin.y, 0);
                    Vector3 p2;
                    if (i + 2 < vertices.length) {
                        p2 = new Vector3(vertices[i + 2] + origin.x, vertices[i + 3] + origin.y, 0);
                    } else {
                        p2 = new Vector3(vertices[0] + origin.x, vertices[1] + origin.y, 0);
                    }
                    meshBuilder.triangle(origin, p1, p2);
                }

            }

            this.mesh = meshBuilder.end();
        }
    }
}
