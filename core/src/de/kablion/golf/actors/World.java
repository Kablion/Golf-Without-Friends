package de.kablion.golf.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.Application;
import de.kablion.golf.actors.Ball;
import de.kablion.golf.actors.Ground;
import de.kablion.golf.actors.Hole;
import de.kablion.golf.actors.Wall;
import de.kablion.golf.data.MapData;

public class World extends Group {

    private Application app;

    private int playerAmount;

    private MapData mapData;

    private Array<Ball> balls;
    private Array<Wall> walls;
    private Array<Ground> grounds;
    private Array<Hole> holes;

    public World(Application app, int playerAmount) {
        this.app = app;
        this.playerAmount = playerAmount;
        balls = new Array<Ball>();
        walls = new Array<Wall>();
        grounds = new Array<Ground>();
        holes = new Array<Hole>();
        initMapData();
    }

    public void initMapData() {
        //this.mapData = MapData.loadMap("Maps/default.json");
        this.mapData = MapData.createDebugMap();
    }

    public void reset() {
        clear();
        balls.clear();
        walls.clear();
        grounds.clear();
        holes.clear();

        for (int i = 0; i < mapData.groundDatas.size; i++) {
            grounds.add(mapData.groundDatas.get(i).toActor(app.assets));
            addActor(grounds.get(i));
        }
        for (int i = 0; i < mapData.wallDatas.size; i++) {
            walls.add(mapData.wallDatas.get(i).toActor(app.assets));
            addActor(walls.get(i));
        }
        for (int i = 0; i < mapData.holeDatas.size; i++) {
            holes.add(mapData.holeDatas.get(i).toActor(app.assets));
            addActor(holes.get(i));
        }

        for (int i = 0; i < playerAmount; i++) {
            balls.add(mapData.ballData.toActor(app.assets));
            addActor(balls.get(i));
        }
    }

    public void update(float delta) {
        for(int i=0; i<balls.size;i++){
            balls.get(i).update(delta);
        }
    }

    public MapData getMapData() {
        return this.mapData;
    }

    public Ball getBall(int player) {
        if(balls.size >= player){
            return balls.get(player-1);
        } else {
            throw new IllegalArgumentException("There is no Player "+ player);
        }
    }
}
