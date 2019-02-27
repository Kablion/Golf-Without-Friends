package de.kablion.golf.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.kablion.golf.Application;

import static de.kablion.golf.utils.Constants.BACKGROUND_MUSIC;
import static de.kablion.golf.utils.Constants.CLICK_SOUND;
import static de.kablion.golf.utils.Constants.DEFLECT_SOUND;
import static de.kablion.golf.utils.Constants.EXIT_SOUND;
import static de.kablion.golf.utils.Constants.HOLE_SOUND;
import static de.kablion.golf.utils.Constants.HUD_ATLAS_PATH;
import static de.kablion.golf.utils.Constants.HUD_SKIN_PATH;
import static de.kablion.golf.utils.Constants.LOADING_ATLAS_PATH;
import static de.kablion.golf.utils.Constants.LOADING_SKIN_PATH;
import static de.kablion.golf.utils.Constants.MENU_ATLAS_PATH;
import static de.kablion.golf.utils.Constants.MENU_SKIN_PATH;
import static de.kablion.golf.utils.Constants.SHOOT_SOUND;
import static de.kablion.golf.utils.Constants.Skins;
import static de.kablion.golf.utils.Constants.TEXTURES_PATH;
import static de.kablion.golf.utils.Constants.UI_HEIGHT;
import static de.kablion.golf.utils.Constants.UI_WIDTH;

public class LoadingScreen implements Screen {

    /**
     * Screen in Which all needed assets are load into RAM / app.assets
     */

    private final Application app;

    private final Stage stage;
    private final Table rootTable = new Table();
    private ProgressBar progressBar;


    public LoadingScreen(final Application app) {
        this.app = app;
        this.stage = new Stage(new ExtendViewport(UI_WIDTH, UI_HEIGHT), app.batch);
    }

    /**
     * Defines the Assets that should be load into RAM and in which order
     */
    private void queueAssets() {
        // Skins and Spritesheets
        app.assets.load(MENU_ATLAS_PATH, TextureAtlas.class);
        app.assets.load(HUD_ATLAS_PATH, TextureAtlas.class);
        //app.assets.load("spritesheets/power_bar_animation.atlas", TextureAtlas.class);
        app.assets.load(TEXTURES_PATH, TextureAtlas.class);

        // Sounds
        app.assets.load(DEFLECT_SOUND, Sound.class);
        app.assets.load(CLICK_SOUND, Sound.class);
        app.assets.load(EXIT_SOUND, Sound.class);
        app.assets.load(SHOOT_SOUND, Sound.class);
        app.assets.load(HOLE_SOUND, Sound.class);
        app.assets.load(BACKGROUND_MUSIC, Music.class);
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
        app.skins.put(Skins.LOADING, new Skin(Gdx.files.internal(LOADING_SKIN_PATH), new TextureAtlas(LOADING_ATLAS_PATH)));
    }

    private void initStage() {
        rootTable.clear();
        stage.clear();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
    }

    private void initProgressBar() {
        progressBar = new ProgressBar(0, 1, 0.01f, false, app.skins.get(Skins.LOADING), "default-horizontal");
        progressBar.setSize(290, progressBar.getPrefHeight());
        progressBar.setAnimateInterpolation(Interpolation.pow5Out);
        progressBar.setAnimateDuration(2);

        rootTable.add(progressBar).expand().center().fillX();
    }

    private void update(float delta) {
        progressBar.setValue(app.assets.getProgress());
        stage.act();
        if (app.assets.update() & Math.abs(progressBar.getVisualValue() - progressBar.getMaxValue()) < 0.001f) {

            //Skins
            app.skins.put(Skins.HUD, new Skin(Gdx.files.internal(HUD_SKIN_PATH), (TextureAtlas) app.assets.get(HUD_ATLAS_PATH)));
            app.skins.put(Skins.MENU, new Skin(Gdx.files.internal(MENU_SKIN_PATH), (TextureAtlas) app.assets.get(MENU_ATLAS_PATH)));//Fonts
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/game_continue.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = 25;
            params.color = Color.BLACK;
            app.skins.get(Skins.MENU).add("default-font", generator.generateFont(params));
            params.size = 50;
            app.skins.get(Skins.MENU).add("big-font", generator.generateFont(params));
            generator.dispose();

            // If everything is loaded continue to the MainMenu
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
