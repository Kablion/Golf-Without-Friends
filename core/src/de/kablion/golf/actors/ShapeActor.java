package de.kablion.golf.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShapeActor extends Actor {

    //Position attribute - (x, y)
    public static final int POSITION_COMPONENTS = 2;

    //Color attribute - floatBits(r, g, b, a)
    public static final int COLOR_COMPONENTS = 1;

    //Texture attribute - (u, v)
    public static final int TEXTURE_COMPONENTS = 2;

    //Total number of components for all attributes
    public static final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS + TEXTURE_COMPONENTS;

    public static final int NONE = 0;
    public static final int CIRCLE = 1;
    public static final int RECTANGLE = 2;
    public static final int POLYGON = 3;


    private Shape2D shape;
    private int type;
    private Texture texture;
    private Mesh mesh;

    public ShapeActor(Shape2D shape, float positionX, float positionY) {
        super();
        setOrigin(positionX,positionY);
        setShape(shape);
        init();
    }

    public ShapeActor(Array<Vector2> polygonPoints) {
        super();
        setOrigin(0,0);
        setShape(findShape(polygonPoints,0,0));
        init();
    }

    public ShapeActor(Array<Vector2> polygonPoints, float positionX, float positionY) {
        super();
        setOrigin(positionX,positionY);
        setShape(findShape(polygonPoints,positionX,positionY));
        init();
    }

    public ShapeActor() {
        super();
        setShape(null);
        setOrigin(0,0);
        init();
    }

    private void init() {
        setColor(Color.PINK);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(mesh != null) {
            //number of vertices we need to render
            int indicesCount = (mesh.getNumIndices());

            //render the mesh
            mesh.render(batch.getShader(), GL20.GL_TRIANGLE_FAN, 0, indicesCount);
        }
    }

    @Override
    public void clear() {
        super.clear();
        if(mesh != null) {
            mesh.dispose();
        }
        if(texture != null) {
            texture.dispose();
        }
    }

    public static Shape2D findShape(Array<Vector2> polygonPoints, float positionX, float positionY) {
        Shape2D result;

        if(polygonPoints == null | polygonPoints.size == 0){
            return null;
        }else if(polygonPoints.size== 1) {
            // type = CIRCLE;
            result = new Circle(positionX,positionY,polygonPoints.get(0).len());
        }else if(polygonPoints.size == 2) {
            // type = RECTANGLE;
            result = new Rectangle();
            float x = polygonPoints.get(0).x + positionX;
            float y = polygonPoints.get(0).y + positionY;
            float width = polygonPoints.get(1).x - polygonPoints.get(0).x;
            float height = polygonPoints.get(1).y - polygonPoints.get(0).y;
            result = new Rectangle(x,y,width,height);
        }else {
            // type = POLYGON;
            float[] vertices = new float[polygonPoints.size*2];
            for(int i = 0; i < polygonPoints.size; i++) {
                vertices[i*2] = polygonPoints.get(i).x;
                vertices[i*2+1] = polygonPoints.get(i).y;
            }
            result = new Polygon(vertices);
            ((Polygon)result).setPosition(positionX,positionY);


        }

        return result;
    }

    private static int findShapeType(Shape2D shape) {
        int result;

        if (shape == null) {
            result = NONE;
        } else if (shape instanceof Circle){
            result = CIRCLE;
        } else if (shape instanceof Rectangle){
            result = RECTANGLE;
        } else if (shape instanceof Polygon){
            result = POLYGON;
        } else {
            result = NONE;
        }

        return result;
    }

    public int getType() {
        return type;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setShape(Shape2D shape) {
        this.shape = shape;
        type = findShapeType(this.shape);
        updateMesh();
        if(mesh != null) {
            //setSize(mesh.calculateBoundingBox().getWidth(),mesh.calculateBoundingBox().getHeight());
            BoundingBox boundingBox = mesh.calculateBoundingBox();
            float left = getOriginX()-boundingBox.getWidth()/2;
            float bottom = getOriginY()-boundingBox.getHeight()/2;
            setBounds(left,bottom,boundingBox.getWidth(),boundingBox.getHeight());
        }
    }

    private void updateMesh() {
        this.mesh = null;
        if(type == NONE) {

        }else {
            float[] meshVertices = null;
            short[] indices = null;
            int i = 0; // Index of meshVertices Array
            int currentVertex = 0; // increments after every full Vertex
            float bitColor = getColor().toFloatBits();

            if(type == CIRCLE) {

            }else if(type == RECTANGLE) {
                Rectangle rect = ((Rectangle)shape);
                meshVertices = new float[4*NUM_COMPONENTS];

                // Bottom Left
                meshVertices[i++] = rect.getX();
                meshVertices[i++] = rect.getY();
                meshVertices[i++] = bitColor;
                meshVertices[i++] = 0;
                meshVertices[i++] = 0;
                currentVertex++;

                // Top Left
                meshVertices[i++] = rect.getX();
                meshVertices[i++] = rect.getY()+rect.getHeight();
                meshVertices[i++] = bitColor;
                meshVertices[i++] = 0;
                meshVertices[i++] = 0;
                currentVertex++;

                // Top Right
                meshVertices[i++] = rect.getX()+rect.getWidth();
                meshVertices[i++] = rect.getY()+rect.getHeight();
                meshVertices[i++] = bitColor;
                meshVertices[i++] = 0;
                meshVertices[i++] = 0;

                currentVertex++;

                // Bottom Right
                meshVertices[i++] = rect.getX()+rect.getWidth();
                meshVertices[i++] = rect.getY();
                meshVertices[i++] = bitColor;
                meshVertices[i++] = 0;
                meshVertices[i++] = 0;
                currentVertex++;

            }else if(type == POLYGON) {

                float[] vertices = ((Polygon)shape).getVertices();
                meshVertices = new float[(vertices.length/2+1)*5];

                // Origin Vertex For Drawing Triangle_Fan
                meshVertices[i++] = 0;
                meshVertices[i++] = 0;
                meshVertices[i++] = bitColor;
                meshVertices[i++] = 0;
                meshVertices[i++] = 0;
                currentVertex++;

                for (int j=0;j< vertices.length;j+=2) {
                    meshVertices[i++] = vertices[j];
                    meshVertices[i++] = vertices[j+1];
                    meshVertices[i++] = bitColor;
                    meshVertices[i++] = 0;
                    meshVertices[i++] = 0;
                    currentVertex++;
                }
            }

            if(meshVertices != null) {

                indices = new short[currentVertex];
                for (int j=0; j<currentVertex;j++){
                    indices[j] = (short)(j);
                }

                this.mesh = new Mesh(false, meshVertices.length / NUM_COMPONENTS, indices.length,
                        new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
                        new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                        new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, TEXTURE_COMPONENTS, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
                this.mesh.setVertices(meshVertices);
                this.mesh.setIndices(indices);
            }
        }
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
