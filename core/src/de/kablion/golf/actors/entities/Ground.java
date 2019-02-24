package de.kablion.golf.actors.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import de.kablion.golf.data.CollisionData;
import de.kablion.golf.data.actors.EntityData;
import de.kablion.golf.utils.RepeatablePolygonSprite;

import static de.kablion.golf.utils.Constants.TEXTURES_PATH;


public class Ground extends Entity {

    private final static float TEXTURE_WIDTH = 50;
    private final static float TEXTURE_HEIGHT = 50;

    public Ground(float x, float y, float rotation, float[] vertices, float textureOffsetX, float textureOffsetY, AssetManager assets, EntityData entityData) {
        super(vertices, entityData);
        setPosition(x, y);
        setRotation(rotation);
        setColor(Color.GREEN);
        setTextureRegion(assets.get(TEXTURES_PATH, TextureAtlas.class).findRegion("ground_texture"),
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                RepeatablePolygonSprite.WrapType.REPEAT,
                RepeatablePolygonSprite.WrapType.REPEAT);
        setTextureOffset(textureOffsetX, textureOffsetY);

    }

    public boolean checkCollisionWithBall(Ball ball, float delta) {
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
