package de.kablion.golf.actors;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.actors.entities.Ball;
import de.kablion.golf.data.MapData;

public class World extends Group {

    private int playerAmount;

    private MapData mapData;

    private AssetManager assets;

    private Array<Ball> balls = new Array<de.kablion.golf.actors.entities.Ball>();

    public World(AssetManager assets, int playerAmount, MapData mapData) {
        this.playerAmount = playerAmount;
        this.mapData = mapData;
        this.assets = assets;
        reset();
    }

    public void reset() {
        clear();
        balls.clear();

        for (int i = 0; i < mapData.entities.size; i++) {
            addActor(mapData.entities.get(i).toEntity(assets, this));
        }

        for (int i = 0; i < playerAmount; i++) {
            balls.add((Ball) mapData.ball.toEntity(assets, this));
            addActor(balls.get(i));
        }
    }

    public MapData getMapData() {
        return this.mapData;
    }

    public void setMapData(MapData newMap) {
        this.mapData = newMap;
    }

    public Ball getBall(int player) {
        if (balls.size >= player) {
            return balls.get(player - 1);
        } else {
            throw new IllegalArgumentException("There is no Player " + player);
        }
    }

    public Array<Ball> getBalls() {
        return balls;
    }

    @Override
    public void clear() {
        super.clear();
    }
}
