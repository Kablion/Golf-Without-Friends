package de.kablion.golf.data.actors;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.World;
import de.kablion.golf.actors.entities.Entity;
import de.kablion.golf.actors.entities.Hole;

public class HoleData implements EntityData {
    public static final float DEFAULT_RADIUS = 7;
    public static final EntityType type = EntityType.HOLE;

    public Vector2 position = new Vector2();
    public float radius = DEFAULT_RADIUS;


    public Hole toActor(AssetManager assetManager) {
        return new Hole(position.x, position.y, radius, assetManager, this);
    }

    @Override
    public Entity toEntity(AssetManager assetManager, World world) {
        return new Hole(position.x, position.y, radius, assetManager, this);
    }

    @Override
    public HoleData clone() {
        HoleData cloneData = new HoleData();
        cloneData.position = new Vector2(position);
        cloneData.radius = radius;
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
