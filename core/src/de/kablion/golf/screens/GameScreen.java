package de.kablion.golf.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;

import de.kablion.golf.Application;
import de.kablion.golf.actors.World;
import de.kablion.golf.data.WorldData;
import de.kablion.golf.inputhandler.WorldHandler;
import de.kablion.golf.stages.HUDStage;
import de.kablion.golf.stages.WorldStage;

import static de.kablion.golf.utils.Constants.BACKGROUND_MUSIC;

public class GameScreen implements Screen {

    /**
     * Screen that handles the main game for both single and multiplayer
     */

    public WorldData worldData;
    public int currentMapNumber = 0;
    public int[] neededStrokesPerMap = new int[1];

    private final Application app;
    private InputMultiplexer multiplexer;
    private World world;
    private WorldStage worldStage;
    private HUDStage hudStage;

    private GestureDetector worldDetector;
    private WorldHandler worldHandler;

    private Music music;

    public GameScreen(final Application app) {
        this.app = app;
    }

    @Override
    public void show() {
        Gdx.app.log("Screen:", "GAME");

        reset();
        playMusic();
    }

    public void reset() {

        if (world != null) {
            world.clear();
        }
        if (worldStage != null) {
            worldStage.dispose();
        }
        if (hudStage != null) {
            hudStage.dispose();
        }

        this.world = new World(app.assets, 1, worldData.maps.get(currentMapNumber));
        this.worldStage = new WorldStage(app, world, 1);
        this.hudStage = new HUDStage(app, worldStage);

        this.worldHandler = new WorldHandler(worldStage);
        this.worldDetector = new GestureDetector(worldHandler);
        worldDetector.setLongPressSeconds(0.2f);
        worldDetector.setTapSquareSize(Gdx.graphics.getWidth() / 20f);
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hudStage);
        multiplexer.addProcessor(worldDetector);
        multiplexer.addProcessor(worldHandler);
        Gdx.input.setInputProcessor(multiplexer);

        worldStage.reset();
        //worldStage.setDebugAll(true);
        hudStage.reset();
        //hudStage.setDebugAll(true);
    }

    public void update(float delta) {
        worldStage.act();
        hudStage.act();
        worldHandler.update(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(31 / 255f, 224 / 255f, 67 / 255f, 1f);
        //Gdx.gl.glClearColor(0, 0,0,1f);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        worldStage.draw();
        hudStage.draw();

    }

    private void playMusic() {
        music = app.assets.get(BACKGROUND_MUSIC, Music.class);
        music.setVolume(0.15f);
        music.setLooping(true);
        music.play();
    }

    public void stopMusic() {
        music.stop();
    }

    @Override
    public void resize(int width, int height) {
        worldStage.resize(width, height);
        hudStage.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        stopMusic();
    }

    @Override
    public void dispose() {
        if (hudStage != null) {
            hudStage.dispose();
        }
        if (worldStage != null) {
            worldStage.dispose();
        }
    }
}
