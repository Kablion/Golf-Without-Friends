package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.actors.Ground;

public class GroundData {
    public static final float DEFAULT_ROTATION = 0;

    public Array<Vector2> polygonPoints;
    public Vector2 startingPosition;
    public float rotation;

    public GroundData() {
        polygonPoints = new Array<Vector2>();
        startingPosition = new Vector2();
        rotation = DEFAULT_ROTATION;
    }

    public Ground toActor(AssetManager assetManager) {
        return new Ground(startingPosition.x, startingPosition.y, rotation, polygonPoints, assetManager);
    }

}
