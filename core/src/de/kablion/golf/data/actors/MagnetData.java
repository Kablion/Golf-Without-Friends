package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.World;
import de.kablion.golf.actors.entities.Entity;
import de.kablion.golf.actors.entities.Magnet;

public class MagnetData implements EntityData {
    public static final EntityType type = EntityType.MAGNET;

    public static final float DEFAULT_RANGE = 75;
    public static final float DEFAULT_STRENGTH = 500;

    public Vector2 position = new Vector2();
    public float range = DEFAULT_RANGE;
    public float strength = DEFAULT_STRENGTH;

    @Override
    public Entity toEntity(AssetManager assetManager, World world) {
        return new Magnet(position.x, position.y, range, strength, assetManager, this);
    }

    @Override
    public MagnetData clone() {
        MagnetData cloneData = new MagnetData();
        cloneData.position = new Vector2(position);
        cloneData.range = range;
        cloneData.strength = strength;
        return cloneData;
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
