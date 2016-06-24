package de.kablion.golf.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import de.kablion.golf.Application;

import static de.kablion.golf.utils.Constants.*;

public class MainMenuScreen implements Screen {

    private final Application app;
    private Stage stage;
    private Skin skin;
    private Table rootTable = new Table();
    private Table menuTable = new Table();
    private Label labelTitle;

    // Menu Buttons

    TextButton buttonPlay;
    TextButton buttonExit;

    public MainMenuScreen(final Application app) {
        this.app = app;
        this.stage = new Stage(new ExtendViewport(UI_WIDTH,UI_HEIGHT),app.batch);
    }

    @Override
    public void show() {
        stage.clear();
        rootTable.clear();
        menuTable.clear();

        System.out.println("MAIN MENU");
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin();
        this.skin.addRegions(app.assets.get("skins/default.atlas", TextureAtlas.class));
        this.skin.add("default-font", app.font24);
        this.skin.add("big-font", app.font48);
        this.skin.load(Gdx.files.internal("skins/default.json"));


        initStage();
        initButtons();
    }

    public void update(float delta) {
        stage.act();
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
        stage.dispose();
    }

    private void initStage(){
        rootTable.setFillParent(true);
        labelTitle = new Label("Main Menu", skin, "big-font", Color.BLACK);
        menuTable.add(labelTitle).padBottom(40);
        menuTable.row();
        menuTable.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));

        rootTable.add(menuTable).center().top().width(300).padTop(100).expandY();
        stage.addActor(rootTable);
    }

    private void initButtons() {
        buttonPlay = new TextButton("Play", skin, "default");
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.gameScreen);
            }
        });

        buttonExit = new TextButton("Exit", skin, "default");
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        menuTable.add(buttonPlay).expandX().fill().padBottom(20);
        menuTable.row();
        menuTable.add(buttonExit).expandX().fill();

    }
}
