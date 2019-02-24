package de.kablion.golf.data.actors;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.World;
import de.kablion.golf.actors.entities.Ball;
import de.kablion.golf.actors.entities.Entity;

public class BallData implements EntityData {

    public static final float DEFAULT_RADIUS = 5;
    public static final EntityType type = EntityType.BALL;

    public Vector2 position = new Vector2();
    public float radius = DEFAULT_RADIUS;

    @Override
    public Entity toEntity(AssetManager assetManager, World world) {
        return new Ball(position.x, position.y, radius, world, assetManager, this);
    }

    @Override
    public BallData clone() {
        BallData ballData = new BallData();
        ballData.position = new Vector2(position);
        ballData.radius = radius;
        return ballData;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public EntityType getType() {
        return type;
    }
}
