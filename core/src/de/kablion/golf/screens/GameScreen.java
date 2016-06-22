package de.kablion.golf.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;

import de.kablion.golf.Application;
import de.kablion.golf.actors.World;
import de.kablion.golf.inputhandler.CameraHandler;
import de.kablion.golf.stages.HUDStage;
import de.kablion.golf.stages.WorldStage;

public class GameScreen implements Screen {

    private final Application app;
    private InputMultiplexer multiplexer;
    private World world;
    private WorldStage worldStage;
    private HUDStage hudStage;

    private GestureDetector cameraDetector;
    private CameraHandler cameraHandler;

    public GameScreen(final Application app) {
        this.app = app;
        this.world = new World(app, 1);
        this.worldStage = new WorldStage(app, world, 1);
        this.hudStage = new HUDStage(app, worldStage);
        this.cameraHandler = new CameraHandler(worldStage);
        this.cameraDetector = new GestureDetector(cameraHandler);
        this.multiplexer = new InputMultiplexer();
    }

    @Override
    public void show() {
        System.out.println("GAME");
        multiplexer.addProcessor(hudStage);
        multiplexer.addProcessor(worldStage);
        multiplexer.addProcessor(cameraDetector);
        multiplexer.addProcessor(cameraHandler);
        Gdx.input.setInputProcessor(multiplexer);

        world.reset();
        worldStage.reset();
        worldStage.setDebugAll(true);
        hudStage.reset();
        //hudStage.setDebugAll(true);
    }

    public void update(float delta){
        world.update(delta);
        hudStage.act();
        worldStage.act();
        cameraHandler.update(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(31/255f, 224/255f, 67/255f,1f);
        //Gdx.gl.glClearColor(0, 0,0,1f);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        worldStage.draw();
        hudStage.draw();

    }

    @Override
    public void resize(int width, int height) {
        float oldCameraX = worldStage.getCamera().position.x;
        float oldCameraY = worldStage.getCamera().position.y;
        float oldCameraZ = worldStage.getCamera().position.z;
        worldStage.getViewport().update(width, height, false);
        worldStage.getCamera().position.set(oldCameraX,oldCameraY,oldCameraZ);
        hudStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if(hudStage != null) {
            hudStage.dispose();
        }
        if(worldStage != null) {
            worldStage.dispose();
        }
    }
}
