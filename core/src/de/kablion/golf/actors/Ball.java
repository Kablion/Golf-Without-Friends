package de.kablion.golf.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.kablion.golf.data.CollisionData;


public class Ball extends Entity {

    public static final float SHOOT_MULTIPLICATOR = 4;

    private World world;
    private AssetManager assets;

    private Vector2 velocity = new Vector2();
    private Vector2 shootVelocity = new Vector2();
    private Vector3 positionBeforeShot = new Vector3();

    public boolean isInHole = false;
    public boolean isOffGround = false;

    private int stroke = 0;

    public Ball(float x, float y, float radius, World world, AssetManager assetManager) {
        super(new float[]{radius, 0});
        this.world = world;
        this.assets = assetManager;
        setPosition(x, y);
        setColor(Color.WHITE);
    }

    public void update(float delta) {
        move(delta);
        slowOverTime(delta);
        checkCollision(delta);
    }

    public void checkCollision(float delta) {

        isOffGround = true;
        boolean collisionHandled = false;
        Actor[] actors = world.getChildren().begin();
        for (int i = 0, n = world.getChildren().size; i < n; i++) {
            if (actors[i] instanceof Entity) {
                Entity entity = (Entity) actors[i];

                if (entity instanceof Ground && !isOffGround) continue;
                if (entity.checkCollisionWithBall(this)) {
                    collisionHandled = true;
                    if (entity instanceof Ground) {
                        collisionHandled = false;
                    }
                    if (entity instanceof Wall) {
                        break;
                    }
                    if (entity instanceof Hole) {
                        break;
                    }
                }
            }
        }


        /*//If the teleportation by moving the ball is a problem
        float stepSize = 1;
        if(!collisionHandled && velocity.len()*delta > 5*stepSize) {
            // test every 0.1 distance back due to teleportation if velocity.len is to big
            int stepAmount =  (int)Math.floor(velocity.len()*delta / stepSize);
            float actualX = getX();
            float actualY = getY();
            Vector2 step = new Vector2(velocity);
            step.scl(-1);
            step.setLength(stepSize);
            for (int stepBack = 0; stepBack < stepAmount;stepBack++) {
                // move the ball 0.1 back and test again
                moveBy(step.x,step.y);

                for (int i = 0, n = world.getChildren().size; i < n; i++) {
                    if (actors[i] instanceof Entity) {
                        Entity entity = (Entity) actors[i];

                        if (entity instanceof Ground || entity instanceof Hole) continue;
                        if (entity.checkCollisionWithBall(this)) {
                            collisionHandled = true;
                            Gdx.app.error("Collision Detection", " Ball is only collided due to the step Back Loop");
                            break;
                        }
                    }
                }
                if (collisionHandled) break;
            }
            setPosition(actualX,actualY);
        }*/
        world.getChildren().end();

    }

    public void setInHole(Hole hole) {
        isInHole = true;
        velocity.setLength(0);
        setPosition(hole.getX() + 1, hole.getY() - 1f);
    }

    public void move(float delta) {

        if (delta > 1) {
            return;
        }

        moveBy(velocity.x * delta, velocity.y * delta);
    }

    public void deflect(float normalX, float normalY, Entity collidingEntity) {
        Vector2 normalVector = new Vector2(normalX, normalY);
        // reflection = inVector - 2*scalar(onVector, normal)*normal
        float dot2 = Vector2.dot(normalVector.x, normalVector.y, velocity.x, velocity.y) * 2;
        Vector2 reflection = new Vector2(velocity.x - (normalVector.x * dot2),
                velocity.y - (normalVector.y * dot2));
        if (reflection.angle(normalVector) <= 90) {
            velocity.set(reflection.x, reflection.y);
        } else {
            Gdx.app.error("Deflection - NormalVector angle > 90: ", "Angle: " + reflection.angle(normalVector) + " Velocity: " + velocity.toString() + " Normal: " + normalVector.toString() + " CalcDeflection: " + reflection.toString());
            // Calc Again with normal rotate 180
            //normalVector.rotate(180);
            collidingEntity.checkCollisionWith(this);
            dot2 = Vector2.dot(normalVector.x, normalVector.y, velocity.x, velocity.y) * 2;
            reflection = new Vector2(velocity.x - (normalVector.x * dot2),
                    velocity.y - (normalVector.y * dot2));
            velocity.set(reflection.x, reflection.y);
            move(Gdx.graphics.getDeltaTime());
            // Something went wrong
            /*velocity.setLength(0);
            Vector2 moveBy = new Vector2(normalVector.x * -(getWidth() / 2), normalVector.y * -(getWidth() / 2));
            moveBy(moveBy);*/

        }
    }

    public void slowOverTime(float delta) {
        if (isOffGround) {
            addToSpeed(-0.5f * (getSpeed() * 10 + 10) * delta);
        } else {
            addToSpeed(-0.5f * (getSpeed() + 10) * delta);
        }
        if (getSpeed() < 2.5f) {
            velocity.set(0, 0);
        }
    }

    public void shoot() {
        positionBeforeShot.set(getX(), getY(), 0);
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

    public void setSpeed(float speed) {
        velocity.setLength(speed);
    }

    public void resetBeforeShot() {
        velocity.set(0, 0);
        setPosition(positionBeforeShot.x, positionBeforeShot.y);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getShootVelocity() {
        return new Vector2(shootVelocity);
    }

    public void setShootVelocity(Vector2 velocity) {
        this.shootVelocity.set(velocity);
    }

    public void setShootVelocity(float x, float y) {
        this.shootVelocity.set(x, y);
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
}
