package de.kablion.golf.data;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import de.kablion.golf.utils.Constants;

public class WorldData {

    public String name = "";
    public Array<MapData> maps = new Array<MapData>();

    public static WorldData loadWorld(String worldFileName) {
        if(!Gdx.files.external(Constants.WORLDS_PATH+worldFileName).exists()) throw new IllegalArgumentException("The given World("+worldFileName+") does not exist");
        Json json = new Json();
        return json.fromJson(WorldData.class, Gdx.files.external(Constants.WORLDS_PATH+worldFileName));
    }

    public static FileHandle[] getWorlds(boolean withInternal) {
        // saveListOfWorlds();
        FileHandle[] worlds;
        if(withInternal) {
            FileHandle[] intWorlds = getInternalWorlds();
            FileHandle[] extWorlds = Gdx.files.external(Constants.WORLDS_PATH).list();
            worlds = new FileHandle[intWorlds.length + extWorlds.length];
            System.arraycopy(extWorlds, 0, worlds, 0, extWorlds.length);
            System.arraycopy(intWorlds, 0, worlds, 0 + extWorlds.length, intWorlds.length);
        } else {
            worlds = Gdx.files.external(Constants.WORLDS_PATH).list();
        }
        return worlds;
    }

    public static FileHandle[] getInternalWorlds () {
        FileHandle fileList = Gdx.files.internal(Constants.INTERNAL_WORLDLIST_PATH);
        if (fileList.exists()) {
            String[] files = fileList.readString().split("/");
            FileHandle[] worlds = new FileHandle[files.length];
            for (int i = 0; i < files.length; i++) {
                worlds[i] = Gdx.files.internal(Constants.INTERNAL_WORLDS_PATH + files[i] + ".json");
            }

            return worlds;
        } else return null;
    }

    private static void saveListOfWorlds() {
        FileHandle[] intWorlds = Gdx.files.internal(Constants.INTERNAL_WORLDS_PATH).list();
        StringBuilder worldList = new StringBuilder();
        for (FileHandle world : intWorlds) {
            if(world.extension().equals("json")){
                worldList.append(world.nameWithoutExtension());
                if(!world.equals(intWorlds[intWorlds.length-1])) {
                    worldList.append("/");
                }
            }
        }

        Gdx.files.external(Constants.APP_NAME+"/"+Constants.INTERNAL_WORLDLIST_PATH).writeString(worldList.toString(), false);
    }

    public static WorldData createDebugWorld() {
        WorldData worldData = new WorldData();
        worldData.maps = MapData.createDebugMaps();
        worldData.name = "Debug World";
        Json json = new Json();
        FileHandle fileHandle = Gdx.files.external(Constants.WORLDS_PATH+"default.json");
        fileHandle.writeString(json.prettyPrint(worldData),false);
        //if(fileHandle.exists()) fileHandle.writeString(json.prettyPrint(worldData),false);
        return worldData;
    }
}
