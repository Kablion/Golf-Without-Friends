package de.kablion.golf.editor.inputhandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.kablion.golf.actors.entities.Entity;
import de.kablion.golf.editor.stages.WorldStage;

public class WorldHandler implements GestureDetector.GestureListener, InputProcessor {

    /**
     * handles the input for one player for everything that is not handled by the HUD
     */

    private WorldStage worldStage;

    private float initialZoom = 0;
    private float initialRotation = 0;
    private Vector3 initialPosition = new Vector3();
    private Vector2 initialPointerCenter = new Vector2();

    private float initialX = 0;

    private long lastPinch = 0;
    private boolean isPinchActive = false;
    private boolean isPanActive = false;
    private boolean longPressFired = false;


    public WorldHandler(WorldStage stage) {
        this.worldStage = stage;
    }

    public void update(float deltatime) {
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            //Rotation with Arrow Keys
            int degreePerSec = 45;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                worldStage.getCamera().rotate(-degreePerSec * deltatime, 0, 0, 1);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                worldStage.getCamera().rotate(degreePerSec * deltatime, 0, 0, 1);
            }
        } else {
            //Translation with Arrow Keys
            int cmPerSec = 45;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                worldStage.getCamera().translate(-cmPerSec * deltatime, 0, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                worldStage.getCamera().translate(cmPerSec * deltatime, 0, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                worldStage.getCamera().translate(0,cmPerSec * deltatime, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                worldStage.getCamera().translate(0,-cmPerSec * deltatime, 0);
            }
        }
    }

    private boolean isJustPinched() {
        return isPinchActive || Math.abs(System.currentTimeMillis() - lastPinch) < 200;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        if(button == Input.Buttons.LEFT) {
            //Check click on Entities
            Vector3 touchPoint = worldStage.getCamera().unproject(new Vector3(x, y, 0));
            SnapshotArray<Actor> originalEntities = worldStage.getWorld().getChildren();
            Actor[] entities = originalEntities.begin();
            for (int i = originalEntities.size - 1; i > -1; i--) {
                if (entities[i] instanceof Entity) {
                    Entity entity = (Entity) entities[i];
                    if (entity.isPointInEntity(new Vector2(touchPoint.x, touchPoint.y))) {
                        if (worldStage.getSelectedEntity() != entity) {
                            worldStage.setSelectedEntity(entity);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        longPressFired = true;
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        isPanActive = true;
        if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            Viewport view = worldStage.getViewport();
            OrthographicCamera cam = (OrthographicCamera) worldStage.getCamera();
            Vector3 pos = view.unproject(new Vector3(x, y, 0));
            Vector3 delta = view.unproject(new Vector3(x - deltaX, y - deltaY, 0)).sub(pos);
            cam.translate(delta);
            return true;
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        isPanActive = false;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {

        return false;
    }

    private float getCameraCurrentXYAngle(OrthographicCamera cam) {
        return (float) Math.atan2(cam.up.x, cam.up.y) * MathUtils.radiansToDegrees;
    }

    @Override
    public void pinchStop() {
        lastPinch = System.currentTimeMillis();
        isPinchActive = false;
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        longPressFired = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(longPressFired) {

        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
            OrthographicCamera cam = (OrthographicCamera) worldStage.getCamera();
            if (amount < 0) {
                // zoom in
                cam.zoom *= Math.abs(0.75f * amount);
            } else {
                // zoom out
                cam.zoom /= Math.abs(0.75f * amount);
            }
            return true;
    }


}
