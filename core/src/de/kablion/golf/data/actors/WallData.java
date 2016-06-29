package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.Wall;

public class WallData {
    public static final float DEFAULT_WIDTH = 10;

    public float width = DEFAULT_WIDTH;
    public boolean isCircle = false;

    public Vector2 fromPos = new Vector2();
    public Vector2 toPos = null;

    public float length = 0;
    public float rotation = 0;

    public Wall toActor(AssetManager assetManager) {
        if (isCircle) {
            if (toPos == null) {
                return new Wall(fromPos.x, fromPos.y, length, assetManager);
            } else {
                return new Wall(fromPos.x, fromPos.y, Vector2.dst(fromPos.x, fromPos.y, toPos.x, toPos.y), assetManager);
            }
        } else {
            if (toPos == null) {
                return new Wall(fromPos, length, rotation, width, assetManager);
            } else {
                return new Wall(fromPos, toPos, width, assetManager);
            }
        }
    }
}
