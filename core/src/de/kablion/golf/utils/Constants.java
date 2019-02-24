package de.kablion.golf.utils;


public class Constants {
    public static final int UI_WIDTH = 480;
    public static final int UI_HEIGHT = 800;

    public static final int EDITOR_UI_WIDTH = 1280;
    public static final int EDITOR_UI_HEIGHT = 720;

    public static final int SHOOTBAR_HEIGHT = UI_HEIGHT / 100 * 12;

    public static final int HUD_BUTTON_SIZE = UI_WIDTH / 100 * 15;

    public static final String APP_NAME = "Golf Without Friends";
    public static final String WORLDS_PATH = APP_NAME + "/worlds/";
    public static final String MAPS_PATH = APP_NAME + "/maps/";
    public static final String INTERNAL_WORLDS_PATH = "data/worlds/";
    public static final String INTERNAL_MAPS_PATH = "data/maps/";
    public static final String INTERNAL_MAPLIST_PATH = "data/maps.txt";
    public static final String INTERNAL_WORLDLIST_PATH = "data/worlds.txt";
    public static final String ERRORLOG_PATH = "data/errorlog.txt";


    public static final String SKINS_PATH = "skins/";

    public static final String LOADING_SKIN_PATH = SKINS_PATH+"loading.json";
    public static final String LOADING_ATLAS_PATH = SKINS_PATH+"loading.atlas";

    public static final String MENU_SKIN_PATH = SKINS_PATH+"default.json";
    public static final String MENU_ATLAS_PATH = SKINS_PATH+"default.atlas";

    public static final String HUD_SKIN_PATH = SKINS_PATH+"game_hud.json";
    public static final String HUD_ATLAS_PATH = SKINS_PATH+"game_hud.atlas";

    public static final String EDITOR_SKIN_PATH = SKINS_PATH+"editor/menu";

    public static final String TEXTURES_PATH = SKINS_PATH+"textures.atlas";

    public static final String SOUNDS_PATH = "sounds/";

    public static final String DEFLECT_SOUND = SOUNDS_PATH+"deflect.wav";
    public static final String CLICK_SOUND = SOUNDS_PATH+"button_click.wav";
    public static final String EXIT_SOUND = SOUNDS_PATH+"exit.wav";
    public static final String SHOOT_SOUND = SOUNDS_PATH+"shoot.wav";
    public static final String HOLE_SOUND = SOUNDS_PATH+"in_hole.wav";

    public static final String BACKGROUND_MUSIC = SOUNDS_PATH+"music_back.mp3";

    public enum Skins {
        LOADING, MENU, HUD, EDITOR
    }

}
