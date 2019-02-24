package de.kablion.golf.data;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import de.kablion.golf.data.actors.BallData;
import de.kablion.golf.data.actors.CameraData;
import de.kablion.golf.data.actors.EntityData;
import de.kablion.golf.data.actors.GroundData;
import de.kablion.golf.data.actors.HoleData;
import de.kablion.golf.data.actors.MagnetData;
import de.kablion.golf.data.actors.WallData;
import de.kablion.golf.utils.Constants;

public class MapData {

    public static final int DEFAULT_PAR = 2;
    public static final float DEFAULT_MAX_SHOOT_SPEED = 50;

    public String name = "";

    public int par = DEFAULT_PAR;
    public float maxShootSpeed = DEFAULT_MAX_SHOOT_SPEED;

    public BallData ball = new BallData();
    public CameraData camera = new CameraData();

    public Array<EntityData> entities = new Array<EntityData>();
    /*public Array<GroundData> groundDatas = new Array<GroundData>();
    public Array<MagnetData> magnetDatas = new Array<MagnetData>();
    public Array<WallData> wallDatas = new Array<WallData>();
    public Array<HoleData> holeDatas = new Array<HoleData>();*/

    public static MapData loadMap(String world, String map) {
        if(!Gdx.files.external(Constants.WORLDS_PATH+world).exists()) throw new IllegalArgumentException("The given World ( "+world+" ) does not exist");
        if(!Gdx.files.external(Constants.WORLDS_PATH+world+"/"+map+".json").exists()) throw new IllegalArgumentException("The given Map( "+map+" ) does not exist in the world "+world);
        Json json = new Json();
        return json.fromJson(MapData.class, Gdx.files.external(Constants.WORLDS_PATH+world+"/"+map+".json"));
    }

    public static MapData loadMap(String map) {
        if(!map.endsWith(".json")) map = map+".json";
        if(!Gdx.files.external(Constants.MAPS_PATH+map).exists()) throw new IllegalArgumentException("The given Map ( "+map+" ) does not exist");
        FileHandle mapFile = Gdx.files.external(Constants.MAPS_PATH+"/"+map);
        return loadMap(mapFile);
    }

    public static MapData loadMap(FileHandle mapFile) {
        Json json = new Json();
        return json.fromJson(MapData.class, mapFile);
    }

    public static void saveMap(MapData mapData, String fileName) {
        Json json = new Json();
        FileHandle fileHandle = Gdx.files.external(Constants.MAPS_PATH+fileName+".json");
        fileHandle.writeString(json.prettyPrint(mapData),false);

    }

    public static FileHandle[] getMaps(boolean withInternal) {
        // saveListOfMaps();
        FileHandle[] maps;
        if(withInternal) {
            FileHandle[] intMaps = getInternalMaps();
            FileHandle[] extMaps = Gdx.files.external(Constants.MAPS_PATH).list();
            maps = new FileHandle[intMaps.length + extMaps.length];
            System.arraycopy(extMaps, 0, maps, 0, extMaps.length);
            System.arraycopy(intMaps, 0, maps, extMaps.length, intMaps.length);
        } else {
            maps = Gdx.files.external(Constants.MAPS_PATH).list();
        }
        return maps;
    }

    private static void saveListOfMaps() {
        FileHandle[] intMaps = Gdx.files.internal(Constants.INTERNAL_MAPS_PATH).list();
        StringBuilder mapList = new StringBuilder();
        for (FileHandle map : intMaps) {
            if(map.extension().equals("json")){
                mapList.append(map.nameWithoutExtension());
                if(!map.equals(intMaps[intMaps.length-1])) {
                    mapList.append('/');
                }
            }
        }

        Gdx.files.external(Constants.APP_NAME+"/"+Constants.INTERNAL_MAPLIST_PATH).writeString(mapList.toString(), false);
    }

    public static FileHandle[] getInternalMaps () {
        FileHandle fileList = Gdx.files.internal(Constants.INTERNAL_MAPLIST_PATH);
        if (fileList.exists()) {
            String[] files = fileList.readString().split("/");
            String filesString = fileList.readString();
            FileHandle[] maps = new FileHandle[files.length];
            for (int i = 0; i < files.length; i++) {
                maps[i] = Gdx.files.internal(Constants.INTERNAL_MAPS_PATH + files[i] + ".json");
            }

            return maps;
        } else return null;
    }

    public static Array<MapData> createDebugMaps() {
        Array<MapData> maps = new Array<MapData>();
        maps.add(createFirstDebugMap());
        maps.add(createSecondDebugMap());
        return maps;
    }

    public static MapData createFirstDebugMap() {
        MapData mapData = new MapData();

        mapData.name = "1st Debug Map";

        mapData.par = 2;
        mapData.maxShootSpeed = 200;

        mapData.camera.cmPerDisplayWidth = CameraData.DEFAULT_CMPERDISPLAYWIDTH;
        mapData.camera.position = new Vector3(0, 130, 0);

        mapData.ball.radius = 5;
        mapData.ball.position = new Vector2(0, 85);

        // create Grounds
        GroundData tempGroundData = new GroundData();
        int numOfVerts = 2;
        tempGroundData.position.set(0, 25);
        tempGroundData.rotation = 0;
        int i = 0;
        tempGroundData.vertices = new float[numOfVerts * 2];

        tempGroundData.vertices[i++] = -100;
        tempGroundData.vertices[i++] = -50;

        tempGroundData.vertices[i++] = 100;
        tempGroundData.vertices[i++] = 100;

        tempGroundData.textureOffset.set(100, 225);
        mapData.entities.add(tempGroundData);

        tempGroundData = new GroundData();
        numOfVerts = 1;
        tempGroundData.position.set(0, 200);
        tempGroundData.rotation = 0;
        i = 0;
        tempGroundData.vertices = new float[numOfVerts * 2];

        tempGroundData.vertices[i++] = 150;
        tempGroundData.vertices[i++] = 0;
        tempGroundData.textureOffset.set(150, 150);
        mapData.entities.add(tempGroundData);

        // create Magnets
        MagnetData tempMagData = new MagnetData();
        tempMagData.position.set(-60,220);
        tempMagData.range = 50;
        tempMagData.strength = 300;
        mapData.entities.add(tempMagData);

        // create Walls
        createDebugWalls(mapData);

        // create holes
        HoleData tempHoleData = new HoleData();
        tempHoleData.position.set(0, 300);
        tempHoleData.radius = 7;
        mapData.entities.add(tempHoleData);

        Json json = new Json();
        FileHandle fileHandle = Gdx.files.external(Constants.MAPS_PATH+"default"+".json");
        fileHandle.writeString(json.prettyPrint(mapData),false);

        return mapData;
    }
    private static MapData createSecondDebugMap() {
        MapData mapData = new MapData();

        mapData.name = "2nd Debug Map";

        mapData.par = 2;
        mapData.maxShootSpeed = 200;

        mapData.camera.cmPerDisplayWidth = CameraData.DEFAULT_CMPERDISPLAYWIDTH;
        mapData.camera.position = new Vector3(0, 130, 0);

        mapData.ball.radius = 5;
        mapData.ball.position = new Vector2(0, 85);

        // create Grounds
        GroundData tempGroundData = new GroundData();
        int numOfVerts = 2;
        tempGroundData.position.set(0, 25);
        tempGroundData.rotation = 0;
        int i = 0;
        tempGroundData.vertices = new float[numOfVerts * 2];
        tempGroundData.vertices[i++] = -100;
        tempGroundData.vertices[i++] = -50;
        tempGroundData.vertices[i++] = 100;
        tempGroundData.vertices[i++] = 100;
        tempGroundData.textureOffset.set(100, 225);
        mapData.entities.add(tempGroundData);

        tempGroundData = new GroundData();
        numOfVerts = 1;
        tempGroundData.position.set(0, 200);
        tempGroundData.rotation = 0;
        i = 0;
        tempGroundData.vertices = new float[numOfVerts * 2];
        tempGroundData.vertices[i++] = 150;
        tempGroundData.vertices[i++] = 0;
        tempGroundData.textureOffset.set(150, 150);
        mapData.entities.add(tempGroundData);

        // create holes
        HoleData tempHoleData = new HoleData();
        tempHoleData.position.set(0, 300);
        tempHoleData.radius = 7;
        mapData.entities.add(tempHoleData);

        Json json = new Json();
        FileHandle fileHandle = Gdx.files.external(Constants.MAPS_PATH+"default2"+".json");
        fileHandle.writeString(json.prettyPrint(mapData),false);

        return mapData;
    }

    private static MapData createDebugWalls(MapData mapData) {
        WallData tempWallData;

        // obstacles
        /*tempWallData = new WallData();
        tempWallData.position.set(25, 220);
        tempWallData.length = 20;
        tempWallData.isCircle = true;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(-30, 190);
        tempWallData.length = 15;
        tempWallData.isCircle = true;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(-15, 260);
        tempWallData.length = 18;
        tempWallData.isCircle = true;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(40, 150);
        tempWallData.length = 60;
        tempWallData.rotation = 35;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(-65, 150);
        tempWallData.length = 50;
        tempWallData.rotation = 130;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(-90, 100);
        tempWallData.length = 125;
        tempWallData.rotation = -20;
        mapData.entities.add(tempWallData);*/


        // outer Walls
        tempWallData = new WallData();
        tempWallData.position.set(-90, 0);
        tempWallData.toPosition = new Vector2(-90, 100);
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(-90, 100);
        tempWallData.toPosition = new Vector2(-150, 200);
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(-150, 200);
        tempWallData.toPosition = new Vector2(0, 350);
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(0, 350);
        tempWallData.toPosition = new Vector2(150, 200);
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(150, 200);
        tempWallData.toPosition = new Vector2(90, 100);
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(90, 0);
        tempWallData.toPosition = new Vector2(90, 100);
        mapData.entities.add(tempWallData);

        // outer Corners
        tempWallData = new WallData();
        tempWallData.position.set(-90, 100);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(-150, 200);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(0, 350);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(150, 200);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.entities.add(tempWallData);

        tempWallData = new WallData();
        tempWallData.position.set(90, 100);
        tempWallData.length = 10;
        tempWallData.isCircle = true;
        mapData.entities.add(tempWallData);


        return mapData;
    }
}
