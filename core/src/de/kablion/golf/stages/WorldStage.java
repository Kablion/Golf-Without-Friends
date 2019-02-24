package de.kablion.golf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.kablion.golf.Application;
import de.kablion.golf.actors.ShootArrow;
import de.kablion.golf.actors.World;
import de.kablion.golf.actors.entities.Ball;

public class WorldStage extends Stage {

    /**
     * Stage that handles and represents the World for one player
     */

    public enum InputMode {
        PLAY, CAMERA
    }

    private Application app;

    private World world;

    private InputMode mode;
    private int player;

    public boolean isCameraOnBall = true;

    public WorldStage(Application app, World world, int player) {
        super(new ExtendViewport(10, 10), app.polyBatch);
        this.app = app;
        this.world = world;
        this.player = player;
        addActor(world);
        addActor(new ShootArrow(this));
    }

    public void reset() {
        mode = InputMode.PLAY;

        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

        ExtendViewport view = new ExtendViewport(world.getMapData().camera.cmPerDisplayWidth, world.getMapData().camera.cmPerDisplayWidth * aspectRatio);
        setViewport(view);
        getCamera().position.set(world.getMapData().camera.position);
    }

    public void updateCamera() {
        //if (getMode() == InputMode.PLAY && isCameraOnBall && getBall().isMoving()) {
        if (getMode() == InputMode.PLAY && isCameraOnBall) {
            getCamera().position.set(getBall().getX(), getBall().getY(), getCamera().position.z);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateCamera();
    }

    @Override
    public void draw() {
        super.draw();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, false);
        getCamera().position.set(getCamera().position.x, getCamera().position.y, getCamera().position.z);
    }

    public void shoot() {

        if (!getBall().isMoving() && !getBall().isInHole && !getBall().isOffGround) {
            //if (powerBar.getValue() > 0.01f) {
            if (getBall().getShootVelocity().len() > getBall().getRadius()*1.5) {
                getBall().shoot();
                app.assets.get("sounds/shoot.wav", Sound.class).play();

            }
        }
        getBall().setShootVelocity(0,0);
    }

    @Override
    public void dispose() {
        clear();
    }

    public InputMode getMode() {
        return mode;
    }

    public void setMode(InputMode mode) {
        this.mode = mode;
    }

    public Ball getBall() {
        return world.getBall(player);
    }

    public World getWorld() {
        return world;
    }
}
