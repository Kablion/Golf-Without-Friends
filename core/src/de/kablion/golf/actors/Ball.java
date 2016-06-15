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

    private Vector3 velocity;
    private Vector3 positionBeforeShot;

    public Ball(float x, float y, float radius) {
        super();
        setOrigin(x, y);
        setColor(Color.WHITE);
        setShape(new Circle(x, y, radius));
        velocity = new Vector3();
    }

    public void update(float delta) {
        move(delta);
        slowOverTime(delta);
    }

    public void move(float delta) {

        if (delta > 1) {
            return;
        }


        if (velocity.x != 0) {
            setOriginX(getOriginX() + velocity.x * delta);
        }

        if (velocity.y != 0) {
            setOriginY(getOriginY() + velocity.y * delta);
        }
    }

    public void slowOverTime(float delta) {
        addToSpeed(-0.5f * (getSpeed() + 10) * delta);
        if (getSpeed() < 2.5f) {
            velocity.set(0, 0, 0);
        }
    }

    public void shoot(Vector3 shootVelocity) {
        velocity.x = shootVelocity.x * 2;
        velocity.y = shootVelocity.y * 2;
    }

    public void addToSpeed(float number) {
        if (getSpeed() + number > 0) {
            velocity.setLength(getSpeed() + number);
        } else {
            velocity.setLength(0);
        }
    }

    public float getSpeed() {
        return velocity.len();
    }

    public boolean isMoving() {
        return velocity.len() != 0;
    }
}
