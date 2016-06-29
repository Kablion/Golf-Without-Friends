package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;

import de.kablion.golf.data.CollisionData;


public class Hole extends Entity {


    public Hole(float x, float y,float radius){
        super(new float[]{radius, 0});
        setPosition(x, y);
        setColor(Color.BLACK);
    }

    public boolean checkCollisionWithBall(Ball ball) {
        CollisionData collisionData = checkCollisionWith(ball);
        if (collisionData != null) {
            if (collisionData.overlapDistance > ball.getRadius()) {
                if (ball.getSpeed() < (this.getWidth() / 2) * 50) {
                    ball.setInHole(this);
                    return true;
                }
            }
        }
        return false;
    }
}
