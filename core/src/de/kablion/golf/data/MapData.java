package de.kablion.golf.data;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.data.actors.BallData;
import de.kablion.golf.data.actors.CameraData;
import de.kablion.golf.data.actors.GroundData;
import de.kablion.golf.data.actors.HoleData;
import de.kablion.golf.data.actors.WallData;

public class MapData {

    public static final int DEFAULT_PAR = 2;
    public static final float DEFAULT_MAX_SHOOT_SPEED = 50;

    public int par = DEFAULT_PAR;
    public float maxShootSpeed = DEFAULT_MAX_SHOOT_SPEED;

    public BallData ballData = new BallData();
    public CameraData cameraData = new CameraData();

    public Array<WallData> wallDatas = new Array<WallData>();
    public Array<GroundData> groundDatas = new Array<GroundData>();
    public Array<HoleData> holeDatas = new Array<HoleData>();

    public static MapData loadMap(String path) {
        MapData mapData = null;

        return mapData;
    }

    public static MapData createDebugMap() {
        MapData mapData = new MapData();

        mapData.par = 2;
        mapData.maxShootSpeed = 200;

        mapData.cameraData.cmPerDisplayWidth = 310;
        mapData.cameraData.startingPosition = new Vector3(0, 130, 0);

        mapData.ballData.radius = 5;
        mapData.ballData.startingPosition = new Vector3(0, 85, 0);

        // create Grounds
        GroundData tempGroundData = new GroundData();
        int numOfVerts = 2;
        tempGroundData.startingPosition.set(0, 50);
        tempGroundData.rotation = 0;
        int i = 0;
        tempGroundData.vertices = new float[numOfVerts * 2];

        tempGroundData.vertices[i++] = -100;
        tempGroundData.vertices[i++] = -50;

        tempGroundData.vertices[i++] = 100;
        tempGroundData.vertices[i++] = 100;

        tempGroundData.textureOffset.set(100, 225);
        mapData.groundDatas.add(tempGroundData);

        tempGroundData = new GroundData();
        numOfVerts = 1;
        tempGroundData.startingPosition.set(0, 200);
        tempGroundData.rotation = 0;
        i = 0;
        tempGroundData.vertices = new float[numOfVerts * 2];

        tempGroundData.vertices[i++] = 150;
        tempGroundData.vertices[i++] = 0;
        tempGroundData.textureOffset.set(150, 150);
        mapData.groundDatas.add(tempGroundData);

        // create Walls
        createDebugWalls(mapData);

        // create holes
        HoleData tempHoleData = new HoleData();
        tempHoleData.startingPosition.set(0, 300);
        tempHoleData.radius = 7;
        mapData.holeDatas.add(tempHoleData);

        return mapData;
    }

    private static MapData createDebugWalls(MapData mapData) {
        WallData tempWallData;

        // obstacles
        tempWallData = new WallData();
        tempWallData.fromPos.set(25, 220);
        tempWallData.length = 20;
        tempWallData.isCircle = true;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(-30, 190);
        tempWallData.length = 15;
        tempWallData.isCircle = true;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(-15, 260);
        tempWallData.length = 18;
        tempWallData.isCircle = true;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(40, 150);
        tempWallData.length = 60;
        tempWallData.rotation = 35;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(-65, 150);
        tempWallData.length = 50;
        tempWallData.rotation = 130;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(-90, 100);
        tempWallData.length = 125;
        tempWallData.rotation = -20;
        mapData.wallDatas.add(tempWallData);


        // outer Walls
        tempWallData = new WallData();
        tempWallData.fromPos.set(-90, 0);
        tempWallData.toPos = new Vector2(-90, 100);
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(-90, 100);
        tempWallData.toPos = new Vector2(-150, 200);
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(-150, 200);
        tempWallData.toPos = new Vector2(0, 350);
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(0, 350);
        tempWallData.toPos = new Vector2(150, 200);
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(150, 200);
        tempWallData.toPos = new Vector2(90, 100);
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(90, 0);
        tempWallData.toPos = new Vector2(90, 100);
        mapData.wallDatas.add(tempWallData);

        // outer Corners
        tempWallData = new WallData();
        tempWallData.fromPos.set(-90, 100);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(-150, 200);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(0, 350);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(150, 200);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.wallDatas.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.fromPos.set(90, 100);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.wallDatas.add(tempWallData);



        return mapData;
    }
}
