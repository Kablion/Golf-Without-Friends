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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GeometryUtils;
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
            Vector3 bottomLeft = new Vector3();
            boundingBox.getCorner000(bottomLeft);
            setBounds(bottomLeft.x, bottomLeft.y, boundingBox.getWidth(), boundingBox.getHeight());
        }
    }

    private float[] getUVRange(Rectangle boundingRect) {
        float minU, minV, maxU, maxV;
        minU = 0;
        minV = 0;
        if (repeatTexture) {
            maxU = boundingRect.getWidth() / textureWidth;
            maxV = boundingRect.getHeight() / textureHeight;
        } else {
            maxU = 1;
            maxV = 1;
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


        if (type == NONE) {

        } else {
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

                EllipseShapeBuilder.build(meshBuilder, circle.radius, divisions, circle.x, circle.y, 0, 0, 0, 1);

            } else if (type == RECTANGLE) {  ////////////////////////////////// Rectangle
                Rectangle rect = ((Rectangle) this.shape);


                MeshPart part1 = meshBuilder.part("part1", GL20.GL_TRIANGLES);
                Vector3 bottomLeft = new Vector3(rect.getX(), rect.getY(), 0);
                Vector3 topRight = new Vector3(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), 0);
                Vector3 bottomRight = new Vector3(rect.getX() + rect.getWidth(), rect.getY(), 0);
                Vector3 topleft = new Vector3(rect.getX(), rect.getY() + rect.getHeight(), 0);
                Vector3 normal = new Vector3(0, 0, 1);

                float[] uvRange = getUVRange(rect);
                meshBuilder.setUVRange(uvRange[0], uvRange[1], uvRange[2], uvRange[3]);

                meshBuilder.rect(bottomLeft, bottomRight, topRight, topleft, normal);

            } else if (type == TRIANGLE | type == POLYGON) {  ////////////////////////////////// Polygon

                Polygon polygon = (Polygon) this.shape;

                Vector3 originP = new Vector3(getOriginX(), getOriginY(), 0);
                float[] vertices = getUnprojectedVertices(polygon.getVertices(), originP);
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
        }
    }
}
