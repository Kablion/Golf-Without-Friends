package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class Hole extends ShapeActor {


    public Hole(float x, float y,float radius){
        super();
        setOrigin(x, y);
        setShape(new Circle(x,y,radius));
        setColor(Color.BLACK);
    }
}
