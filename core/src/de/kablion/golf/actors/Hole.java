package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class Hole extends ShapeActor {


    public Hole(float radius, Vector3 position){
        super();
        setPosition(position.x, position.y);
        setColor(Color.BLACK);
    }
}
