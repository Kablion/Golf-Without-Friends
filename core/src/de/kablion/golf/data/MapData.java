package de.kablion.golf.data;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.Application;
import de.kablion.golf.actors.Ground;
import de.kablion.golf.actors.Hole;
import de.kablion.golf.actors.Wall;
import de.kablion.golf.data.actors.BallData;
import de.kablion.golf.data.actors.CameraData;
import de.kablion.golf.data.actors.GroundData;
import de.kablion.golf.data.actors.HoleData;
import de.kablion.golf.data.actors.WallData;

public class MapData {

    public static final int DEFAULT_PAR = 2;
    public static final float DEFAULT_MAX_SHOOT_SPEED = 50;

    public int par;
    public float maxShootSpeed;

    public BallData ballData;
    public CameraData cameraData;

    public Array<WallData> wallDatas;
    public Array<GroundData> groundDatas;
    public Array<HoleData> holeDatas;

    public MapData(){
        par = DEFAULT_PAR;
        maxShootSpeed = DEFAULT_MAX_SHOOT_SPEED;

        ballData = new BallData();
        cameraData = new CameraData();
        wallDatas = new Array<WallData>();
        groundDatas = new Array<GroundData>();
        holeDatas = new Array<HoleData>();
    }

    public static MapData loadMap(String path) {
        MapData mapData = null;

        return mapData;
    }

    public static MapData createDebugMap() {
        MapData mapData = new MapData();

        mapData.par = 2;
        mapData.maxShootSpeed = 100;

        mapData.cameraData.cmPerDisplayWidth = 150;
        mapData.cameraData.startingPosition = new Vector3(0, 0, 0);

        mapData.ballData.radius = 5;
        mapData.ballData.startingPosition = new Vector3(0, -25, 0);

        // create Walls
        WallData tempWallData = new WallData();
        tempWallData.startingPosition.set(0, 0);
        tempWallData.length = 150;
        tempWallData.width = 10;
        tempWallData.rotation = 0;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.startingPosition.set(0, -50);
        tempWallData.length = 50;
        tempWallData.width = 10;
        tempWallData.rotation = 20;
        mapData.wallDatas.add(tempWallData);

        // create Grounds
        GroundData tempGroundData = new GroundData();
        tempGroundData.startingPosition.set(100, 100);
        tempGroundData.rotation = 0;
        tempGroundData.polygonPoints.add(new Vector2(-70, -100));
        tempGroundData.polygonPoints.add(new Vector2(-70, 100));
        tempGroundData.polygonPoints.add(new Vector2(70, 200));
        //tempGroundData.polygonPoints.add(new Vector2(70,-100));
        mapData.groundDatas.add(tempGroundData);

        // create holes
        HoleData tempHoleData = new HoleData();
        tempHoleData.startingPosition.set(0, 50);
        tempHoleData.radius = 7;
        mapData.holeDatas.add(tempHoleData);

        return mapData;
    }
}
