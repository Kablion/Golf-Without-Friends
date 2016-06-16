package de.kablion.golf.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.sun.javafx.scene.control.behavior.TableViewBehavior;

import de.kablion.golf.Application;

import static de.kablion.golf.utils.Constants.*;

public class LoadingScreen implements Screen {

    private final Application app;

    private Stage stage;
    private Table rootTable;
    private ProgressBar progressBar;
    private Skin skin;

    public LoadingScreen(final Application app) {
        this.app = app;
        this.stage = new Stage(new ExtendViewport(UI_WIDTH,UI_HEIGHT),app.batch);
        rootTable = new Table();
        this.skin = new Skin(Gdx.files.internal("skins/loadingScreen.json"));
        skin.addRegions(new TextureAtlas("skins/loadingScreen.atlas"));
    }

    private void queueAssets() {
        app.assets.load("badlogic.jpg", Texture.class);
        app.assets.load("skins/default.atlas", TextureAtlas.class);
        app.assets.load("skins/game_hud.atlas",TextureAtlas.class);
        app.assets.load("sprites/textures/ground_texture.png", Texture.class);
        app.assets.load("sprites/textures/wall_texture.png", Texture.class);
    }

    @Override
    public void show() {
        System.out.println("LOADING");

        initStage();
        initProgressBar();

        queueAssets();
    }

    private void initStage() {
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
    }

    private void initProgressBar() {
        progressBar = new ProgressBar(0,1,0.01f,false,skin,"default-horizontal");
        progressBar.setSize(290, progressBar.getPrefHeight());
        progressBar.setAnimateInterpolation(Interpolation.pow5Out);
        progressBar.setAnimateDuration(2);

        rootTable.add(progressBar).expand().center().fillX();
    }

    public void update(float delta){
        progressBar.setValue( app.assets.getProgress());
        stage.act();
        if (app.assets.update() & progressBar.getVisualValue() == progressBar.getMaxValue()) {
            app.setScreen(app.mainMenuScreen);
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
        stage.getViewport().update(width,height,true);
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
        stage.dispose();
    }
}
