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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
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
    public static final int COLOR_COMPONENTS = 1;

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
    public static final int POLYGON = 3;


    private Shape2D shape;
    private int type;
    private Texture texture;
    private Mesh mesh;

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

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGB888);
        pixmap.setColor(getColor());
        this.colorTexture = new Texture(pixmap);
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        super.setColor(r, g, b, a);
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(getColor());
        this.colorTexture = new Texture(pixmap);
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

    private void init() {
        setColor(Color.PINK);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (mesh != null) {

            if(texture == null){
                colorTexture.bind();
            } else {
                texture.bind();
            }

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
    }

    public int getType() {
        return type;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
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

        } else if (type == CIRCLE) {
            Circle circle = (Circle)this.shape;
            int divisions = 20;
            MeshBuilder meshBuilder = new MeshBuilder();
            meshBuilder.begin(VERTEX_ATTRIBUTES);
            meshBuilder.setColor(getColor());
            MeshPart part1 = meshBuilder.part("part1",GL20.GL_TRIANGLES);
            EllipseShapeBuilder.build(meshBuilder,circle.radius,divisions,circle.x,circle.y,0,0,0,1);
            this.mesh = meshBuilder.end();

        } else if (type == RECTANGLE) {
            Rectangle rect = ((Rectangle) shape);

            MeshBuilder meshBuilder = new MeshBuilder();
            meshBuilder.begin(VERTEX_ATTRIBUTES);
            meshBuilder.setColor(getColor());
            MeshPart part1 = meshBuilder.part("part1",GL20.GL_TRIANGLES);

            Vector3 bottomLeft = new Vector3(rect.getX(),rect.getY(),0);
            Vector3 topRight = new Vector3(rect.getX()+rect.getWidth(),rect.getY()+rect.getHeight(),0);
            Vector3 bottomRight = new Vector3(rect.getX()+rect.getWidth(),rect.getY(),0);
            Vector3 topleft = new Vector3(rect.getX(),rect.getY()+rect.getHeight(),0);

            Vector3 normal = new Vector3(0,0,1);
            meshBuilder.rect(bottomLeft,bottomRight,topRight,topleft,normal);
            this.mesh = meshBuilder.end();

        } else if (type == POLYGON) {

            int currentVertex = 0; // increments after every full Vertex
            int numberOfVertices = 0;

            float[] x = null;
            float[] y = null;
            float bitColor = Color.WHITE.toFloatBits();
            float[] u = null;
            float[] v = null;

            float[] vertices = ((Polygon) shape).getVertices();
            numberOfVertices = vertices.length / 2;
            x = new float[numberOfVertices];
            y = new float[numberOfVertices];
            u = new float[numberOfVertices];
            v = new float[numberOfVertices];

            // Origin Vertex For Drawing Triangle_Fan
            x[currentVertex] = 0;
            y[currentVertex] = 0;
            u[currentVertex] = 0;
            v[currentVertex] = 0;
            currentVertex++;

            for (; currentVertex < numberOfVertices; currentVertex++) {
                x[currentVertex] = vertices[currentVertex*2];
                y[currentVertex] = vertices[currentVertex*2+1];
                u[currentVertex] = 0;
                v[currentVertex] = 0;
            }


            float[] meshVertices = new float[numberOfVertices*NUM_COMPONENTS];
            short[] indices = new short[numberOfVertices];

            int i = 0; // Index of meshVertices Array
            for (currentVertex = 0; currentVertex<numberOfVertices;currentVertex++) {
                int meshIndexOfVertex = currentVertex*NUM_COMPONENTS;
                meshVertices[meshIndexOfVertex] = x[currentVertex];
                meshVertices[meshIndexOfVertex+1] = y[currentVertex];
                meshVertices[meshIndexOfVertex+2] = bitColor;
                meshVertices[meshIndexOfVertex+3] = u[currentVertex];
                meshVertices[meshIndexOfVertex+4] = v[currentVertex];

                indices[currentVertex] = (short)currentVertex;
            }

            //Mesh.VertexDataType vertexDataType = (Gdx.gl30 != null) ? Mesh.VertexDataType.VertexBufferObjectWithVAO : Mesh.VertexDataType.VertexArray;
            this.mesh = new Mesh( false, numberOfVertices, numberOfVertices,
                    new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorPacked, COLOR_COMPONENTS, ShaderProgram.COLOR_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, TEXTURE_COMPONENTS, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
            this.mesh.setVertices(meshVertices,0,meshVertices.length);
            this.mesh.setIndices(indices);

        }
    }
}
