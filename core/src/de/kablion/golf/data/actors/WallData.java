package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.Hole;
import de.kablion.golf.actors.Wall;

public class WallData {
    public static final float DEFAULT_LENGTH = 50;
    public static final float DEFAULT_WIDTH = 10;
    public static final float DEFAULT_ROTATION = 0;

    public Vector2 startingPosition;
    public float length;
    public float width;
    public float rotation;

    public WallData() {
        startingPosition = new Vector2();
        length = DEFAULT_LENGTH;
        width = DEFAULT_WIDTH;
        rotation = DEFAULT_ROTATION;
    }

    public Wall toActor(AssetManager assetManager) {
        return new Wall(startingPosition.x, startingPosition.y, length, width, rotation, assetManager);
    }
}
