package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;


public class Ground extends ShapeActor {

    public Ground(Vector2 position, float rotation, Array<Vector2> polygonPoints, Texture texture) {
        super();
        setPosition(position.x,position.y);
        setRotation(rotation);
        setColor(Color.GREEN);
        setTexture(texture);

    }

}
