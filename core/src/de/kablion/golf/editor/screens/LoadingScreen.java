package de.kablion.golf.editor.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.kablion.golf.editor.Application;

import static de.kablion.golf.utils.Constants.*;

public class LoadingScreen implements Screen {

    /**
     * Screen in Which all needed assets are load into RAM / app.assets
     */

    private final Application app;

    private final Stage stage;
    private final Table rootTable = new Table();
    private ProgressBar progressBar;
    private Skin loadingSkin = new Skin();

    public LoadingScreen(final Application app) {
        this.app = app;
        this.stage = new Stage(new ExtendViewport(UI_WIDTH, UI_HEIGHT), app.batch);
    }

    /**
     * Defines the Assets that should be load into RAM and in which order
     */
    private void queueAssets() {
        // Skins and Spritesheets
        app.assets.load(EDITOR_SKIN_PATH+".atlas", TextureAtlas.class);
        app.assets.load(TEXTURES_PATH, TextureAtlas.class);
        app.assets.load(HUD_ATLAS_PATH, TextureAtlas.class);
    }

    @Override
    public void show() {
        Gdx.app.log("Screen:","LOADING");

        initSkin();
        initStage();
        initProgressBar();

        queueAssets();
    }

    private void initSkin() {
        loadingSkin.addRegions(new TextureAtlas(LOADING_ATLAS_PATH));
        loadingSkin.load(Gdx.files.internal(LOADING_SKIN_PATH));
    }

    private void initStage() {
        rootTable.clear();
        stage.clear();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
    }

    private void initProgressBar() {
        progressBar = new ProgressBar(0, 1, 0.01f, false, loadingSkin, "default-horizontal");
        progressBar.setSize(290, progressBar.getPrefHeight());
        progressBar.setAnimateInterpolation(Interpolation.pow5Out);
        progressBar.setAnimateDuration(2);

        rootTable.add(progressBar).expand().center().fillX();
    }

    private void update(float delta) {
        progressBar.setValue(app.assets.getProgress());
        stage.act();
        if (app.assets.update() & Math.abs(progressBar.getVisualValue() - progressBar.getMaxValue()) < 0.001f) {
            // If everything is loaded continue to the MainMenu
            app.setScreen(app.editorScreen);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.clear();
    }
}
