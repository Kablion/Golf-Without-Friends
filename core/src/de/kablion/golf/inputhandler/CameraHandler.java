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
import com.sun.org.apache.xpath.internal.operations.Or;

import de.kablion.golf.stages.WorldStage;

public class CameraHandler implements GestureDetector.GestureListener, InputProcessor {

    private WorldStage worldStage;

    private float initialZoom;
    private float initialRotation;
    private float rotation;

    public CameraHandler(WorldStage stage) {
        this.worldStage = stage;
        initialZoom = 0;
        initialRotation = 0;
        rotation = 0;
    }

    public void update(float deltatime) {
        if (worldStage.getMode() == WorldStage.CAMERA_MODE) {
            int degreePerSec = 45;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                worldStage.getCamera().rotate(-degreePerSec * deltatime, 0, 0, 1);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                worldStage.getCamera().rotate(degreePerSec * deltatime, 0, 0, 1);
            }
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (worldStage.getMode() == WorldStage.CAMERA_MODE) {
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
        if (worldStage.getMode() == WorldStage.CAMERA_MODE) {
            OrthographicCamera cam = (OrthographicCamera) worldStage.getCamera();
            cam.zoom = (initialDistance / distance) * initialZoom;
            return true;
        }
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        if (worldStage.getMode() == WorldStage.CAMERA_MODE) {
            OrthographicCamera cam = (OrthographicCamera) worldStage.getCamera();
            if (initialZoom == 0) {
                initialZoom = cam.zoom;
            }
            if (initialRotation == 0) {
                initialRotation = getCameraCurrentXYAngle(cam);
            }

            Vector2 initial = new Vector2(initialPointer2.x - initialPointer1.x, initialPointer2.y - initialPointer1.y);
            Vector2 now = new Vector2(pointer2.x - pointer1.x, pointer2.y - pointer1.y);
            float deltaRot = (float) (Math.atan2(now.y, now.x) - Math.atan2(initial.y, initial.x));
            float deltaRotDeg = deltaRot * MathUtils.radiansToDegrees;
            //float deltaRotDeg = (float)(((deltaRot*180)/Math.PI + 360) % 360);
            float rotationBack = initialRotation - getCameraCurrentXYAngle(cam);
            cam.rotate(rotationBack);
            cam.rotate(-deltaRotDeg);


            return true;
        }
        return false;
    }

    private float getCameraCurrentXYAngle(OrthographicCamera cam) {
        return (float) Math.atan2(cam.up.x, cam.up.y) * MathUtils.radiansToDegrees;
    }

    @Override
    public void pinchStop() {
        initialZoom = 0;
        initialRotation = 0;
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
        if (worldStage.getMode() == WorldStage.CAMERA_MODE) {
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
