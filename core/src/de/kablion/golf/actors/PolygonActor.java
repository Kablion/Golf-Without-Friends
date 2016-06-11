package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class PolygonActor extends Actor {


    public static final int NONE = 0;
    public static final int CIRCLE = 1;
    public static final int RECTANGLE = 2;
    public static final int POLYGON = 3;

    private int type;
    private Array<Vector2> polygonPoints;

    private Texture texture;
    private float boundsRadius;

    private PolygonSprite poly;
    PolygonSpriteBatch polyBatch;
    Texture textureSolid;

    public PolygonActor(){
        this.polygonPoints = new Array<Vector2>();
        this.texture = null;
        setColor(Color.PINK);
        this.findType();
    }

    private void findType() {
        if(polygonPoints == null | polygonPoints.size == 0){
            type = NONE;
            return;
        }
        if(polygonPoints.size== 1) {
            type = CIRCLE;
        }
        if(polygonPoints.size == 2) {
            type = RECTANGLE;
        }

        if(type == CIRCLE) {
            boundsRadius = polygonPoints.get(0).len();
        } else {
            float greatestDistance = 0;
            for (int i=0;i<polygonPoints.size;i++) {
                float distance = polygonPoints.get(i).len();
                if(distance > greatestDistance) {
                    greatestDistance = distance;
                }
            }
            boundsRadius = greatestDistance;
        }
    }

    public int getType() {
        return type;
    }

    public Array<Vector2> getPolygonPoints() {
        return polygonPoints;
    }

    public float getBoundsRadius() {
        return boundsRadius;
    }

    public Texture getTexture() {
        return texture;
    }

    protected void createPolygon(Array<Vector2> polygonPoints) {
        this.polygonPoints = polygonPoints;
        this.findType();
    }

    protected void createCircle(float radius){
        Array<Vector2> polygonPoints = new Array<Vector2>();
        polygonPoints.add(new Vector2(radius,0));
        this.polygonPoints = polygonPoints;
    }

    protected void createRectangle(float topLeftX, float topLeftY, float bottomRightX, float bottomRightY) {
        Array<Vector2> polygonPoints = new Array<Vector2>();
        polygonPoints.add(new Vector2(topLeftX,topLeftY));
        polygonPoints.add(new Vector2(bottomRightX,bottomRightY));
        this.polygonPoints = polygonPoints;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    private float[] getVertices() {
        float[] vertices = null;

        if(polygonPoints != null & polygonPoints.size > 0) {
            vertices = new float[polygonPoints.size*2];
            for(int i = 0; i < polygonPoints.size; i++) {
                vertices[i*2] = polygonPoints.get(i).x;
                vertices[i*2+1] = polygonPoints.get(i).y;
            }
        }

        return vertices;
    }

    /*private void updateDrawables(){
        PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid),getVertices());
        poly = new PolygonSprite(polyReg);
        poly.setOrigin(oX, oY);
        polyBatch = new PolygonSpriteBatch();
    }*/

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }
}
