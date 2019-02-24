package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.actors.World;
import de.kablion.golf.actors.entities.Entity;
import de.kablion.golf.actors.entities.Wall;

public class WallData implements EntityData {
    public static final EntityType type = EntityType.WALL;
    public static final float DEFAULT_WIDTH = 10;

    public boolean isCircle = false;

    public Vector2 position = new Vector2();
    public Vector2 toPosition = new Vector2();

    public float width = DEFAULT_WIDTH;
    public float length = 0;
    public float rotation = 0;

    @Override
    public Entity toEntity(AssetManager assetManager, World world) {
        if (isCircle) {
            if (length != 0) {
                return new Wall(position.x, position.y, length, assetManager, this);
            } else {
                return new Wall(position.x, position.y, Vector2.dst(position.x, position.y, toPosition.x, toPosition.y), assetManager, this);
            }
        } else {
            if (length != 0) {
                return new Wall(position, length, rotation, width, assetManager, this);
            } else {
                return new Wall(position, toPosition, width, assetManager, this);
            }
        }
    }

    @Override
    public WallData clone() {
        WallData cloneData = new WallData();
        cloneData.position = new Vector2(position);
        cloneData.toPosition = new Vector2(toPosition);
        cloneData.width = width;
        cloneData.isCircle = isCircle;
        cloneData.length = length;
        cloneData.rotation = rotation;
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
