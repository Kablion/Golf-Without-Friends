package de.kablion.golf.data.actors;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.Ground;
import de.kablion.golf.actors.Hole;

public class HoleData {
    public static final float DEFAULT_RADIUS = 7;

    public Vector2 startingPosition = new Vector2();
    public float radius = DEFAULT_RADIUS;


    public Hole toActor(AssetManager assetManager) {
        return new Hole(startingPosition.x, startingPosition.y, radius);
    }
}
