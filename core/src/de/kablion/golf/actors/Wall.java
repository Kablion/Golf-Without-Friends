package de.kablion.golf.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.data.CollisionData;
import de.kablion.golf.utils.RepeatablePolygonSprite;


public class Wall extends Entity {

    private final static float TEXTURE_WIDTH = 20;
    private final static float TEXTURE_HEIGHT = 20;

    // As Rectangle With From and To vector
    public Wall(Vector2 from, Vector2 to, float width, AssetManager assets) {
        super(null);
        setPosition((from.x + to.x) / 2, (from.y + to.y) / 2);
        Vector2 fromToV = new Vector2(to.x - from.x, to.y - from.y);
        setRotation(fromToV.angle());
        float length = fromToV.len();
        float left = -length / 2;
        float right = length / 2;
        float top = +width / 2;
        float bottom = -width / 2;
        setColor(Color.BROWN);
        setTextureRegion(assets.get("spritesheets/textures.atlas", TextureAtlas.class).findRegion("wall_texture"),
                TEXTURE_WIDTH,
                0,
                RepeatablePolygonSprite.WrapType.REPEAT,
                RepeatablePolygonSprite.WrapType.STRETCH);
        float[] vertices = new float[]{left, bottom, right, top};
        setVertices(vertices);
    }

    // As Rectangle With From and length and rotation vector
    public Wall(Vector2 from, float length, float rotation, float width, AssetManager assets) {
        super(null);
        Vector2 fromToV = new Vector2(1, 0);
        fromToV.setLength(length).rotate(rotation).scl(0.5f);
        setPosition((from.x + fromToV.x), (from.y + fromToV.y));
        setRotation(rotation);
        float left = -length/2;
        float right = length/2;
        float top = +width / 2;
        float bottom = -width / 2;
        setColor(Color.BROWN);
        setTextureRegion(assets.get("spritesheets/textures.atlas", TextureAtlas.class).findRegion("wall_texture"),
                TEXTURE_WIDTH,
                0,
                RepeatablePolygonSprite.WrapType.REPEAT,
                RepeatablePolygonSprite.WrapType.STRETCH);
        float[] vertices = new float[]{left, bottom, right, top};
        setVertices(vertices);
    }

    // As Circle
    public Wall(float x, float y, float radius, AssetManager assets) {
        super(null);
        setPosition(x, y);
        setColor(Color.BROWN);
        setTextureRegion(assets.get("spritesheets/textures.atlas", TextureAtlas.class).findRegion("round_wall_texture"), 0, 0,
                RepeatablePolygonSprite.WrapType.STRETCH,
                RepeatablePolygonSprite.WrapType.STRETCH);

        float[] vertices = new float[]{radius, 0};
        setVertices(vertices);
    }

    public boolean checkCollisionWithBall(Ball ball) {
        CollisionData collisionData = checkCollisionWith(ball);
        if (collisionData != null) {
            // Undo the move Step
            float delta = Gdx.graphics.getDeltaTime();
            Vector2 moveback = new Vector2(ball.getVelocity().x * (-1 * delta), ball.getVelocity().y * (-1 * delta));
            moveback.setLength(0.1f);
            while (ball.getSpeed() != 0) {
                ball.moveBy(moveback.x, moveback.y);
                CollisionData tempColData = checkCollisionWith(ball);
                if (tempColData == null) {
                    break;
                }
                collisionData = tempColData;
            }
            //ball.setSpeed(0);
            ball.deflect(collisionData.normalFirstToSecond.x, collisionData.normalFirstToSecond.y, this);
            return true;
        }
        return false;
    }
}
