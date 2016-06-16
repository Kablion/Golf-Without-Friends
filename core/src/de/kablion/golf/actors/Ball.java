package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.actors.World;
import de.kablion.golf.data.CollisionData;


public class Ball extends ShapeActor {

    public static final float SHOOT_MULTIPLICATOR = 4;

    private World world;

    private Vector3 velocity;
    private Vector3 positionBeforeShot;

    private boolean isInHole;
    private boolean isOffGround;

    private int stroke;

    public Ball(float x, float y, float radius, World world) {
        super();
        this.world = world;
        setOrigin(x, y);
        setColor(Color.WHITE);
        setShape(new Circle(x, y, radius));
        this.velocity = new Vector3();
        this.positionBeforeShot = new Vector3();
        this.isOffGround = false;
        this.isInHole = false;
        this.stroke = 0;
    }

    public void update(float delta) {
        checkCollision(delta);
        move(delta);
        slowOverTime(delta);
    }

    public void checkCollision(float delta) {

        CollisionData collisionData = null;
        // Check if ball is on a Ground
        isOffGround = true;
        for (int i = 0; i < world.getGrounds().size; i++) {
            collisionData = checkCollisionWith(world.getGrounds().get(i));
            if (collisionData != null) {
                isOffGround = false;
                break;
            }
        }

        // Check Ball collision with Holes
        for (int i = 0; i < world.getHoles().size; i++) {
            collisionData = checkCollisionWith(world.getHoles().get(i));
            if (collisionData != null) {
                collisionData = checkCollisionWith(world.getHoles().get(i));
                if (collisionData.overlapDistance > getWidth() / 2) {
                    if (getSpeed() < (world.getHoles().get(i).getWidth() / 2) * 30) {
                        setInHole(i);
                        return;
                    }
                }
            }
        }

        // Check Ball collides with Walls
        for (int i = 0; i < world.getWalls().size; i++) {
            collisionData = checkCollisionWith(world.getWalls().get(i));
            if (collisionData != null) {
                // Undo the move Step
                Vector2 moveback = new Vector2(velocity.x * (-1 * delta), velocity.y * (-1 * delta));
                moveback.setLength(0.1f);
                while (collisionData != null & getSpeed() != 0) {
                    moveBy(moveback);
                    CollisionData tempColData = checkCollisionWith(world.getWalls().get(i));
                    if (tempColData == null) {
                        break;
                    }
                    collisionData = tempColData;
                }
                deflect(collisionData.normalVector);
            }
        }

    }

    public void setInHole(int holeIndex) {
        Hole hole = world.getHoles().get(holeIndex);
        isInHole = true;
        velocity.setLength(0);
        setOrigin(hole.getOriginX() + 1, hole.getOriginY() - 1f);
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

    public void moveBy(Vector2 moveBy) {
        if (moveBy.x != 0) {
            setOriginX(getOriginX() + moveBy.x);
        }

        if (moveBy.y != 0) {
            setOriginY(getOriginY() + moveBy.y);
        }
    }

    public void deflect(Vector2 normalVector) {
        // reflection = inVector - 2*scalar(onVector, normal)*normal
        float dot2 = Vector2.dot(normalVector.x, normalVector.y, velocity.x, velocity.y) * 2;
        Vector2 reflection = new Vector2(velocity.x - (normalVector.x * dot2),
                velocity.y - (normalVector.y * dot2));
        if (reflection.angle(normalVector) < 90) {
            velocity.set(reflection.x, reflection.y, 0);
        } else {
            // Something went wrong
            velocity.setLength(0);
            Vector2 moveBy = new Vector2(normalVector.x * -(getWidth() / 2), normalVector.y * -(getWidth() / 2));
            moveBy(moveBy);

        }
    }

    public void slowOverTime(float delta) {
        if (isOffGround) {
            addToSpeed(-0.5f * (getSpeed() * 10 + 10) * delta);
        } else {
            addToSpeed(-0.5f * (getSpeed() + 10) * delta);
        }
        if (getSpeed() < 2.5f) {
            velocity.set(0, 0, 0);
        }
    }

    public void shoot(Vector3 shootVelocity) {
        positionBeforeShot.set(getOriginX(), getOriginY(), 0);
        velocity.x = shootVelocity.x * SHOOT_MULTIPLICATOR;
        velocity.y = shootVelocity.y * SHOOT_MULTIPLICATOR;
        stroke++;
    }

    public void addToSpeed(float number) {
        if (getSpeed() + number > 0) {
            velocity.setLength(getSpeed() + number);
        } else {
            velocity.setLength(0);
        }
    }

    public void resetBeforeShot() {
        velocity.set(0, 0, 0);
        setOrigin(positionBeforeShot.x, positionBeforeShot.y);
    }

    public float getSpeed() {
        return velocity.len();
    }

    public int getStroke() {
        return stroke;
    }

    public boolean isMoving() {
        return velocity.len() != 0;
    }

    public boolean isInHole() {
        return isInHole;
    }

    public boolean isOffGround() {
        return isOffGround;
    }
}
