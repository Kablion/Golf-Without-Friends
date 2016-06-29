package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.actors.Ground;

public class GroundData {
    public static final float DEFAULT_ROTATION = 0;

    public float[] vertices;
    public Vector2 startingPosition = new Vector2();
    public float rotation = DEFAULT_ROTATION;
    public Vector2 textureOffset = new Vector2();

    public Ground toActor(AssetManager assetManager) {
        return new Ground(startingPosition.x, startingPosition.y, rotation, vertices, textureOffset.x, textureOffset.y, assetManager);
    }

}
