package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;


public class Ball extends ShapeActor {

    public Ball(float radius, float positionX, float positionY) {
        super();
        setPosition(positionX,positionY);
        setShape(new Circle(positionX,positionY,radius));
    }


   /* public Ball(float radius, Vector3 position){
        super(new Array<Vector2>().add(new Vector2());
        setPosition(position.x, position.y);
        setColor(Color.WHITE);
    }*/

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
