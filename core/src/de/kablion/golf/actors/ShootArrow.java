package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.kablion.golf.stages.WorldStage;
import de.kablion.golf.utils.RepeatablePolygonSprite;

public class ShootArrow extends Actor {

    private final float WIDTH = 3;

    WorldStage worldStage;
    RepeatablePolygonSprite arrowSprite = new RepeatablePolygonSprite();
    RepeatablePolygonSprite shaftSprite = new RepeatablePolygonSprite();

    public ShootArrow(WorldStage worldStage) {
        this.worldStage = worldStage;
        initSprites();
    }

    private void initSprites() {
        // Length of 1 so the ScaleX defines its length in WorldCoords
        shaftSprite.setVertices(new float[]
                {0, -WIDTH / 2,
                        0, WIDTH / 2,
                        1, WIDTH / 2,
                        1, -WIDTH / 2});
        float arrowFactor = 1.5f;
        arrowSprite.setVertices(new float[]
                {0, 0,
                        -WIDTH * arrowFactor, -WIDTH * arrowFactor,
                        -WIDTH * arrowFactor * 0.6f, 0,
                        -WIDTH * arrowFactor, WIDTH * arrowFactor});

    }

    private void updateSprites() {
        Ball ball = worldStage.getBall();
        Vector2 currentShootVelocity = ball.getShootVelocity();
        float shootVelocityAngle = currentShootVelocity.angle();
        Vector2 shaftOffset = new Vector2(ball.getRadius(), 0);
        shaftOffset.rotate(shootVelocityAngle);
        shaftSprite.setPosition(ball.getX() + shaftOffset.x, ball.getY() + shaftOffset.y);
        shaftSprite.setScaleX(currentShootVelocity.len() - (WIDTH * 0.5f) - ball.getRadius());
        shaftSprite.setRotation(shootVelocityAngle);
        arrowSprite.setPosition(ball.getX() + currentShootVelocity.x, ball.getY() + currentShootVelocity.y);
        arrowSprite.setRotation(shootVelocityAngle);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Ball ball = worldStage.getBall();
        if (!ball.isMoving() & !ball.isInHole & !ball.isOffGround) {
            updateSprites();

            shaftSprite.draw((PolygonSpriteBatch) batch);
            arrowSprite.draw((PolygonSpriteBatch) batch);
        }
    }

}
