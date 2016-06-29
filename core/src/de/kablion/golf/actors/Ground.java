package de.kablion.golf.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.data.CollisionData;
import de.kablion.golf.utils.RepeatablePolygonSprite;


public class Ground extends Entity {

    private final static float TEXTURE_WIDTH = 50;
    private final static float TEXTURE_HEIGHT = 50;

    public Ground(float x, float y, float rotation, float[] vertices, float textureOffsetX, float textureOffsetY, AssetManager assets) {
        super(vertices);
        setPosition(x, y);
        setRotation(rotation);
        setColor(Color.GREEN);
        setTextureRegion(assets.get("spritesheets/textures.atlas", TextureAtlas.class).findRegion("ground_texture"),
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                RepeatablePolygonSprite.WrapType.REPEAT,
                RepeatablePolygonSprite.WrapType.REPEAT);
        setTextureOffset(textureOffsetX, textureOffsetY);

    }

    public boolean checkCollisionWithBall(Ball ball) {
        CollisionData collisionData = checkCollisionWith(ball);
        if (collisionData != null) {
            if (collisionData.isSecondInFirst || collisionData.overlapDistance > getRadius()) {
                ball.isOffGround = false;
                return true;
            }
        }
        return false;
    }

}
