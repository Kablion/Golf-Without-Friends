package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;


public class Wall extends ShapeActor {

    public Wall(float x, float y,float length, float width, float rotation) {
        super();
        setOrigin(x, y);
        setRotation(rotation);

        float left = -length/2;
        float right = length/2;
        float top = +width;
        float bottom = -width;
        Array<Vector2> polygonPoints = new Array<Vector2>(2);
        polygonPoints.add(new Vector2(left,bottom));
        polygonPoints.add(new Vector2(right,top));
        setPosition(x,y);
        setColor(Color.BROWN);
        setShape(findShape(polygonPoints, x, y));
    }
}
