package de.kablion.golf.inputhandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.kablion.golf.stages.WorldStage;

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
    private boolean isHandlingShootArrow = false;
    private boolean longPressFired = false;


    public WorldHandler(WorldStage stage) {
        this.worldStage = stage;
    }

    public void update(float deltatime) {
        if (worldStage.getMode() == WorldStage.InputMode.CAMERA) {
            int degreePerSec = 45;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                worldStage.getCamera().rotate(-degreePerSec * deltatime, 0, 0, 1);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                worldStage.getCamera().rotate(degreePerSec * deltatime, 0, 0, 1);
            }
        }
    }

    private boolean isJustPinched() {
        return isPinchActive || Math.abs(System.currentTimeMillis() - lastPinch) < 200;
    }

    public boolean handleShootArrow(float x, float y) {
        if (worldStage.getMode() == WorldStage.InputMode.PLAY) {
            if (isJustPinched()) return true;
            Vector3 tempShootVelocity = worldStage.getCamera().unproject(new Vector3(x, y, 0));
            tempShootVelocity.sub(worldStage.getBall().getX(), worldStage.getBall().getY(), 0);
            // other direction of the ball
            tempShootVelocity.scl(-1);
            if (tempShootVelocity.len() <= worldStage.getWorld().getMapData().maxShootSpeed * 2) {
                if (tempShootVelocity.len() > worldStage.getWorld().getMapData().maxShootSpeed) {
                    tempShootVelocity.setLength(worldStage.getWorld().getMapData().maxShootSpeed);
                }
                worldStage.getBall().setShootVelocity(tempShootVelocity.x, tempShootVelocity.y);
                isHandlingShootArrow = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        //if (handleShootArrow(x, y)) return true;
        //isHandlingShootArrow = false;
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        longPressFired = true;
        handleShootArrow(x, y);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        isPanActive = true;

        if (handleShootArrow(x, y)) return true;
        if (worldStage.getMode() == WorldStage.InputMode.CAMERA) {
            isHandlingShootArrow = false;
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
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        isHandlingShootArrow = false;
        if (worldStage.getMode() == WorldStage.InputMode.CAMERA || (worldStage.getMode() == WorldStage.InputMode.PLAY)) {

            OrthographicCamera cam = (OrthographicCamera) worldStage.getCamera();
            Viewport view = worldStage.getViewport();
            if (!isPinchActive) {
                initialX = initialPointer1.x;
                initialZoom = cam.zoom;
                initialRotation = getCameraCurrentXYAngle(cam);
                initialPosition.set(cam.position);
                initialPointerCenter.set(((initialPointer1.x + initialPointer2.x) * 0.5f), ((initialPointer1.y + initialPointer2.y) * 0.5f));
                isPinchActive = true;
                Gdx.app.log("Pinch", "Start");
                return true;
            }
            if (Math.abs(initialX - initialPointer1.x) > 0.001f)
                Gdx.app.error("Pinch", "Initial Logic in WorldHandler is failing.");


            Vector2 initial = new Vector2(initialPointer2.x - initialPointer1.x, initialPointer2.y - initialPointer1.y);
            Vector2 now = new Vector2(pointer2.x - pointer1.x, pointer2.y - pointer1.y);

            // Zoom
            float distFraction = initial.len() / now.len();
            cam.zoom = distFraction * initialZoom;

            // Rotation
            float deltaRot = (float) (Math.atan2(now.y, now.x) - Math.atan2(initial.y, initial.x));
            float deltaRotDeg = deltaRot * MathUtils.radiansToDegrees;
            //if(deltaRotDeg > 1) {
            float rotationBack = initialRotation - getCameraCurrentXYAngle(cam);
            cam.rotate(rotationBack);
            cam.rotate(-deltaRotDeg);
            //}

            // Position
            if (worldStage.getMode() == WorldStage.InputMode.CAMERA /*|| (worldStage.getMode() == WorldStage.InputMode.PLAY && (!worldStage.isCameraOnBall || !worldStage.getBall().isMoving()))*/) {
                Vector2 pointerCenter = new Vector2(((pointer1.x + pointer2.x) * 0.5f), ((pointer1.y + pointer2.y) * 0.5f));
                view.unproject(pointerCenter);
                view.unproject(initialPointerCenter);
                Vector2 deltaCenter = new Vector2(pointerCenter.x - initialPointerCenter.x, pointerCenter.y - initialPointerCenter.y);
                view.project(initialPointerCenter);
                cam.position.set(initialPosition.x - deltaCenter.x, initialPosition.y - deltaCenter.y, initialPosition.z);
            } else if (worldStage.isCameraOnBall && worldStage.getBall().isMoving()) {
                initialPointerCenter.set(((pointer1.x + pointer2.x) * 0.5f), ((pointer1.y + pointer2.y) * 0.5f));
                initialPosition.set(cam.position);
            }
            return true;
        }
        return false;
    }

    private float getCameraCurrentXYAngle(OrthographicCamera cam) {
        return (float) Math.atan2(cam.up.x, cam.up.y) * MathUtils.radiansToDegrees;
    }

    @Override
    public void pinchStop() {
        lastPinch = System.currentTimeMillis();
        isPinchActive = false;
        Gdx.app.log("Pinch", "Stop");
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
        if(isHandlingShootArrow) {
            isHandlingShootArrow = false;
            worldStage.shoot();
            longPressFired = false;
            isPanActive = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (worldStage.getMode() == WorldStage.InputMode.CAMERA || worldStage.getMode() == WorldStage.InputMode.PLAY) {
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
        return false;
    }


}
