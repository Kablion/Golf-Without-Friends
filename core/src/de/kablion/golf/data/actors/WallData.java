package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.Hole;
import de.kablion.golf.actors.Wall;

public class WallData {
    public static final float DEFAULT_LENGTH = 50;
    public static final float DEFAULT_WIDTH = 10;
    public static final float DEFAULT_ROTATION = 0;

    public Vector2 startingPosition = new Vector2();
    public float length = DEFAULT_LENGTH;
    public float width = DEFAULT_WIDTH;
    public float rotation = DEFAULT_ROTATION;

    public Wall toActor(AssetManager assetManager) {
        return new Wall(startingPosition.x, startingPosition.y, length, width, rotation, assetManager);
    }
}
