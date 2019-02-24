package de.kablion.golf.actors.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

import de.kablion.golf.data.CollisionData;
import de.kablion.golf.data.actors.EntityData;

import static de.kablion.golf.utils.Constants.HOLE_SOUND;


public class Hole extends Entity {

    private AssetManager assets;

    public Hole(float x, float y, float radius, AssetManager assets, EntityData entityData) {
        super(new float[]{radius, 0}, entityData);
        this.assets = assets;
        setPosition(x, y);
        setColor(Color.BLACK);
    }

    public boolean checkCollisionWithBall(Ball ball, float delta) {
        CollisionData collisionData = checkCollisionWith(ball);
        if (collisionData != null) {
            if (!ball.isInHole && collisionData.overlapDistance > ball.getRadius()) {
                if (ball.getSpeed() < (this.getWidth() / 2) * 50) {
                    assets.get(HOLE_SOUND, Sound.class).play();
                    ball.setInHole(this);
                    return true;
                }
            }
        }
        return false;
    }
}
