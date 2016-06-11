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

    //Color attribute - (u, v)
    public static final int UV_COMPONENTS = 2;

    //Total number of components for all attributes
    public static final int NUM_COMPONENTS = POSITION_COMPONENTS;

    public static final int NONE = 0;
    public static final int CIRCLE = 1;
    public static final int RECTANGLE = 2;
    public static final int POLYGON = 3;


    private Shape2D shape;
    private int type;
    private Texture texture;
    private Mesh mesh;
    private ShaderProgram shader;

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
        this.shader = createMeshShader();
    }

    protected ShaderProgram createMeshShader() {
        final String VERT_SHADER =
                "attribute vec"+POSITION_COMPONENTS+" "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
                        //"attribute vec"+UV_COMPONENTS+" "+ShaderProgram.TEXCOORD_ATTRIBUTE+";\n" +
                        "uniform mat4 u_projTrans;\n" +
                        "varying vec4 vColor;\n" +
                        "void main() {\n" +
                        "	gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" +
                        "}";

        final String FRAG_SHADER =
                "#ifdef GL_ES\n" +
                        "precision mediump float;\n" +
                        "#endif\n"+
                        "void main() {\n" +
                        "   gl_FragColor = vec4("+getColor().r+", "+getColor().g+", "+getColor().b+", "+getColor().a+");\n" +
                        "}";

        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(VERT_SHADER,FRAG_SHADER);
        String log = shader.getLog();
        if (!shader.isCompiled())
            throw new GdxRuntimeException(log);
        if (log!=null && log.length()!=0)
            System.out.println("Shader Log: "+log);
        return shader;
    }

    private void updateMesh() {
        this.mesh = null;
        if(type == NONE) {

        }else if(type == CIRCLE) {

        }else if(type == RECTANGLE) {
            Rectangle rect = ((Rectangle)shape);
            float[] meshVertices = new float[4*NUM_COMPONENTS];
            int i = 0;

            // Bottom Left
            meshVertices[i++] = rect.getX();
            meshVertices[i++] = rect.getY();

            // Top Left
            meshVertices[i++] = rect.getX();
            meshVertices[i++] = rect.getY()+rect.getHeight();

            // Top Right
            meshVertices[i++] = rect.getX()+rect.getWidth();
            meshVertices[i++] = rect.getY()+rect.getHeight();

            // Bottom Right
            meshVertices[i++] = rect.getX()+rect.getWidth();
            meshVertices[i++] = rect.getY();

            short[] indices = new short[]{ 0, 1, 2, 2, 3, 0}; // two triangles

            this.mesh = new Mesh( true, 4, 6,  // static mesh with 4 vertices and 6 indices
                    new VertexAttribute( VertexAttributes.Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE ) );
            this.mesh.setVertices(meshVertices);
            this.mesh.setIndices(indices);
        }else if(type == POLYGON) {
            float[] vertices = ((Polygon)shape).getVertices();
            float[] meshVertices = new float[(vertices.length/2+1)*5];
            // Origin Verter For Drawing Triangle_Fan
            meshVertices[0] = 0;
            meshVertices[1] = 0;

            for (int i=0;i< vertices.length;i+=2) {
                int vertex = i/2+1;
                meshVertices[vertex*NUM_COMPONENTS] = vertices[i];
                meshVertices[vertex*NUM_COMPONENTS+1] = vertices[i+1];
            }

            short[] indices = new short[meshVertices.length/NUM_COMPONENTS];
            for (int i=0; i<meshVertices.length;i+=NUM_COMPONENTS){
                indices[i/NUM_COMPONENTS] = (short)(i/NUM_COMPONENTS);
            }

            this.mesh = new Mesh( true, meshVertices.length/NUM_COMPONENTS, meshVertices.length/NUM_COMPONENTS,  // static mesh with vertices.length/2 vertices and no indices
                    new VertexAttribute( VertexAttributes.Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE ) );
            this.mesh.setVertices(meshVertices);
            this.mesh.setIndices(indices);
        }

        this.shader = createMeshShader();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        if(getType() == RECTANGLE) {
            //no need for depth...
            Gdx.gl.glDepthMask(false);

            //enable blending, for alpha
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            //number of vertices we need to render
            int indicesCount = (mesh.getNumIndices());

            //start the shader before setting any uniforms
            shader.begin();

            //update the projection matrix so our triangles are rendered in 2D
            shader.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());

            //render the mesh
            mesh.render(shader, GL20.GL_TRIANGLE_FAN, 0, indicesCount);

            shader.end();

            //re-enable depth to reset states to their default
            Gdx.gl.glDepthMask(true);

        }
        batch.begin();
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

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    private void updatePolygonRenderer() {

    }
}
