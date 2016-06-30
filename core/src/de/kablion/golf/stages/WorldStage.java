package de.kablion.golf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.kablion.golf.Application;
import de.kablion.golf.actors.ShootArrow;
import de.kablion.golf.actors.World;
import de.kablion.golf.actors.Ball;

public class WorldStage extends Stage {

    private Application app;

    public static final int PLAY_MODE = 0;
    public static final int CAMERA_MODE = 1;

    private static final int NO_PRIME_POINTER = -1;
    private int primePointer;

    private int mode;
    private int player;
    private World world;

    public WorldStage(Application app, World world, int player) {
        super(new ExtendViewport(10, 10), app.polyBatch);
        this.app = app;
        this.world = world;
        this.player = player;
        addActor(world);
        addActor(new ShootArrow(this));
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean handled = super.touchDown(screenX, screenY, pointer, button);
        if (!handled && getMode() == PLAY_MODE && (pointer == NO_PRIME_POINTER || pointer == primePointer)) {
            Vector3 tempShootVelocity = getCamera().unproject(new Vector3(screenX, screenY, 0));
            tempShootVelocity.sub(getBall().getX(), getBall().getY(), 0);
            if (tempShootVelocity.len() <= getWorld().getMapData().maxShootSpeed * 2) {
                if (tempShootVelocity.len() > getWorld().getMapData().maxShootSpeed) {
                    tempShootVelocity.setLength(getWorld().getMapData().maxShootSpeed);
                }
                getBall().setShootVelocity(tempShootVelocity.x, tempShootVelocity.y);
                handled = true;
            }
            primePointer = pointer;
        }
        return handled;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean handled = super.touchUp(screenX, screenY, pointer, button);
        if (pointer == primePointer) primePointer = NO_PRIME_POINTER;
        return handled;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean handled = super.touchDragged(screenX, screenY, pointer);
        if (!handled && getMode() == PLAY_MODE) {
            Vector3 tempShootVelocity = getCamera().unproject(new Vector3(screenX, screenY, 0));
            tempShootVelocity.sub(getBall().getX(), getBall().getY(), 0);
            if (tempShootVelocity.len() <= getWorld().getMapData().maxShootSpeed * 2) {
                if (tempShootVelocity.len() > getWorld().getMapData().maxShootSpeed) {
                    tempShootVelocity.setLength(getWorld().getMapData().maxShootSpeed);
                }
                getBall().setShootVelocity(tempShootVelocity.x, tempShootVelocity.y);
                handled = true;
            }
            primePointer = pointer;
        }
        return handled;
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
