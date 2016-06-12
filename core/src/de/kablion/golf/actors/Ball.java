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

    public Ball(float x, float y, float radius) {
        super();
        setOrigin(x, y);
        setColor(Color.WHITE);
        setShape(new Circle(x, y, radius));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
