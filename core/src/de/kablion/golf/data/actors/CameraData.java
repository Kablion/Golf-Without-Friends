package de.kablion.golf.data.actors;


import com.badlogic.gdx.math.Vector3;

public class CameraData {
    public static final float DEFAULT_CMPERDISPLAYWIDTH = 150;

    public Vector3 startingPosition;
    public float cmPerDisplayWidth;

    public CameraData() {
        startingPosition = new Vector3();
        cmPerDisplayWidth = DEFAULT_CMPERDISPLAYWIDTH;
    }
}
