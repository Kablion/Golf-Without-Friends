package de.kablion.golf.data;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.Application;
import de.kablion.golf.actors.Ground;
import de.kablion.golf.actors.Hole;
import de.kablion.golf.actors.Wall;

public class MapData {

    private int par;
    private float maxShootSpeed;

    private Vector3 cameraStartingPosition;
    private float cmPerDisplayWidth;

    private Vector3 ballStartingPosition;
    private float ballRadius;

    private Array<Wall> walls;
    private Array<Ground> grounds;
    private Array<Hole> holes;

    public MapData(){

    }

    public static MapData loadMap(String path) {
        MapData mapData = null;

        return mapData;
    }

    public static MapData createDebugMap(Application app) {
        MapData mapData = new MapData();

        mapData.par = 2;
        mapData.maxShootSpeed = 5;

        mapData.cmPerDisplayWidth = 150;

        mapData.cameraStartingPosition = new Vector3(0, 0, 100);

        mapData.ballRadius = 5;
        mapData.ballStartingPosition = new Vector3(0, -25, 0);

        // create Walls
        mapData.walls = new Array<Wall>();
        Wall tempWall = new Wall(0,0,50,10,0);
        mapData.walls.add(tempWall);
        tempWall = new Wall(0, -50, 50, 10, 20);
        mapData.walls.add(tempWall);

        // create Grounds
        mapData.grounds = new Array<Ground>();
        Array<Vector2> polygonPoints = new Array<Vector2>();
        polygonPoints.add(new Vector2(-70, -100));
        //polygonPoints.add(new Vector2(-70, 100));
        polygonPoints.add(new Vector2(70, 100));
        //polygonPoints.add(new Vector2(70,-100));
        Ground tempGround = new Ground(0, 0, 0, polygonPoints, app.assets);
        mapData.grounds.add(tempGround);

        // create holes
        mapData.holes = new Array<Hole>();
        Hole tempHole = new Hole(0, 50, 7);
        mapData.holes.add(tempHole);

        return mapData;
    }

    public int getPar() {
        return par;
    }

    public float getMaxShootSpeed() {
        return maxShootSpeed;
    }

    public Vector3 getCameraStartingPosition() {
        return cameraStartingPosition;
    }

    public float getCmPerDisplayWidth() {
        return cmPerDisplayWidth;
    }

    public Vector3 getBallStartingPosition() {
        return ballStartingPosition;
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public Array<Wall> getWalls() {
        return walls;
    }

    public Array<Ground> getGrounds() {
        return grounds;
    }

    public Array<Hole> getHoles() {
        return holes;
    }
}
