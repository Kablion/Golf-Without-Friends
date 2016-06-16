package de.kablion.golf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.kablion.golf.Application;
import de.kablion.golf.actors.World;
import de.kablion.golf.actors.Ball;

public class WorldStage extends Stage {

    private Application app;

    public static final int PLAY_MODE = 0;
    public static final int CAMERA_MODE = 1;

    private int mode;
    private int player;
    private World world;

    public WorldStage(Application app, World world, int player) {
        super(new ExtendViewport(10,10),app.batch);
        this.app = app;
        this.world = world;
        this.player = player;
        addActor(world);
    }

    public void reset(){
        mode = PLAY_MODE;

        float aspectRatio = Gdx.graphics.getHeight() / Gdx.graphics.getHeight();

        ExtendViewport view = new ExtendViewport(world.getMapData().cameraData.cmPerDisplayWidth, world.getMapData().cameraData.cmPerDisplayWidth * aspectRatio);
        setViewport(view);
        getCamera().position.set(world.getMapData().cameraData.startingPosition);
    }

    @Override
    public void act() {
        super.act();
    }

    @Override
    public void draw() {
        super.draw();

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        if (mode == CAMERA_MODE | mode == PLAY_MODE) {
            this.mode = mode;
        } else {
            throw new IllegalArgumentException("Mode is not defined: " + mode);
        }
    }

    public Ball getBall() {
        return world.getBall(player);
    }

    public World getWorld() {
        return world;
    }
}
