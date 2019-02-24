package de.kablion.golf.data.actors;

import com.badlogic.gdx.assets.AssetManager;

import com.badlogic.gdx.math.Vector2;
import de.kablion.golf.actors.World;
import de.kablion.golf.actors.entities.Entity;

public interface EntityData {

    enum EntityType {
        GROUND, HOLE, MAGNET, WALL, BALL
    }

    Entity toEntity(AssetManager assetManager, World world);

    EntityData clone();

    Vector2 getPosition();
    EntityType getType();
}
