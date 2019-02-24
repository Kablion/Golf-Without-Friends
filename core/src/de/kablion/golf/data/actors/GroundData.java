package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.World;
import de.kablion.golf.actors.entities.Entity;
import de.kablion.golf.actors.entities.Ground;

public class GroundData implements EntityData {
    public static final float DEFAULT_ROTATION = 0;
    public static final EntityType type = EntityType.GROUND;

    public float[] vertices;
    public Vector2 position = new Vector2();
    public float rotation = DEFAULT_ROTATION;
    public Vector2 textureOffset = new Vector2();

    @Override
    public Entity toEntity(AssetManager assetManager, World world) {
        return new Ground(position.x, position.y, rotation, vertices, textureOffset.x, textureOffset.y, assetManager, this);
    }

    @Override
    public GroundData clone() {
        GroundData cloneData = new GroundData();
        cloneData.position = new Vector2(position);
        cloneData.vertices = new float[vertices.length];
        System.arraycopy(vertices, 0, cloneData.vertices, 0, vertices.length);
        cloneData.rotation = rotation;
        cloneData.textureOffset = new Vector2(textureOffset);
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
