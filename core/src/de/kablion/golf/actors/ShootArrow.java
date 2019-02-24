package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.kablion.golf.stages.WorldStage;
import de.kablion.golf.utils.RepeatablePolygonSprite;

public class ShootArrow extends Actor {

    private final float SHAFT_WIDTH = 2;
    private final float ARROW_WIDTH = 10;

    WorldStage worldStage;
    RepeatablePolygonSprite arrowSprite = new RepeatablePolygonSprite();
    RepeatablePolygonSprite shaftSprite = new RepeatablePolygonSprite();

    public ShootArrow(WorldStage worldStage) {
        this.worldStage = worldStage;
        setColor(Color.GRAY);
        initSprites();
    }

    private void initSprites() {
        // Length of 1 so the ScaleX defines its length in WorldCoords
        shaftSprite.setVertices(new float[]
                {0, -SHAFT_WIDTH / 2,
                        0, SHAFT_WIDTH / 2,
                        1, SHAFT_WIDTH / 2,
                        1, -SHAFT_WIDTH / 2});

        arrowSprite.setVertices(new float[]
                {0, 0,
                        -ARROW_WIDTH, -ARROW_WIDTH / 2,
                        -ARROW_WIDTH * 0.6f, 0,
                        -ARROW_WIDTH, ARROW_WIDTH / 2});

    }

    private void updateSprites() {
        de.kablion.golf.actors.entities.Ball ball = worldStage.getBall();
        Vector2 currentShootVelocity = ball.getShootVelocity();
        float shootVelocityAngle = currentShootVelocity.angle();
        Vector2 shaftOffset = new Vector2(ball.getRadius() * 0.95f, 0);
        shaftOffset.rotate(shootVelocityAngle);
        shaftSprite.setPosition(ball.getX() + shaftOffset.x, ball.getY() + shaftOffset.y);
        arrowSprite.setPosition(ball.getX() + currentShootVelocity.x, ball.getY() + currentShootVelocity.y);
        if (currentShootVelocity.len() > (SHAFT_WIDTH * 0.5f) + ball.getRadius()) {
            shaftSprite.setScaleX(currentShootVelocity.len() - (ARROW_WIDTH * 0.5f) - ball.getRadius());
        } else {
            shaftSprite.setScaleX(0);
        }
        shaftSprite.setRotation(shootVelocityAngle);
        arrowSprite.setRotation(shootVelocityAngle);
        shaftSprite.setColor(getColor());
        arrowSprite.setColor(getColor());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        de.kablion.golf.actors.entities.Ball ball = worldStage.getBall();
        if (!ball.isMoving() && !ball.isInHole && !ball.isOffGround && ball.getShootVelocity().len() / worldStage.getWorld().getMapData().maxShootSpeed > 0.015f) {
            updateSprites();

            shaftSprite.draw((PolygonSpriteBatch) batch);
            arrowSprite.draw((PolygonSpriteBatch) batch);
        }
    }

}
