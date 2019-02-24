package de.kablion.golf.actors.entities;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.data.CollisionData;
import de.kablion.golf.data.actors.EntityData;
import de.kablion.golf.utils.RepeatablePolygonSprite;

import static de.kablion.golf.utils.Constants.TEXTURES_PATH;

public class Magnet extends Entity {

    private float strength = 0;
    private AssetManager assets;

    public Magnet(float x, float y, float range, float strength, AssetManager assets, EntityData entityData) {
        super(new float[]{range,0}, entityData);
        this.assets = assets;
        setPosition(x,y);
        setColor(Color.BLUE);
        setTextureRegion(assets.get(TEXTURES_PATH, TextureAtlas.class).findRegion("magnet_texture"),
                range,
                range,
                RepeatablePolygonSprite.WrapType.REPEAT_MIRRORED,
                RepeatablePolygonSprite.WrapType.REPEAT_MIRRORED);
        this.strength = strength;
    }

    @Override
    public boolean checkCollisionWithBall(Ball ball, float delta) {
            CollisionData collisionData = checkCollisionWith(ball);
            if (collisionData != null) {
                Vector2 ballToMag = new Vector2(getX() - ball.getX(), getY() - ball.getY());
                if (ballToMag.len() > ball.getRadius() && ball.isMoving()) {
                    Vector2 force = new Vector2(ballToMag);
                    force.setLength(strength/50 * delta);
                    // 1/r force
                    force.scl(ballToMag.len());
                    // TODO Magnet doesnt stop if ball is interacting with a wall in the middle
                    ball.addToVelocity(force.x, force.y);
                }
            }
        return false;
    }
}
