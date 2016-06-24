package de.kablion.golf.data.actors;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.kablion.golf.actors.World;
import de.kablion.golf.actors.Ball;

public class BallData {

    public static final float DEFAULT_RADIUS = 5;

    public Vector3 startingPosition = new Vector3();
    public float radius = DEFAULT_RADIUS;

    public Ball toActor(AssetManager assetManager, World world) {
        return new Ball(startingPosition.x, startingPosition.y, radius, world);
    }
}
