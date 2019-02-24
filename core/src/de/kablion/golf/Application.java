package de.kablion.golf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

import de.kablion.golf.screens.GameScreen;
import de.kablion.golf.screens.LoadingScreen;
import de.kablion.golf.screens.MainMenuScreen;
import de.kablion.golf.utils.Constants;

public class Application extends Game {

    /**
     * Handles the Life Circle of The whole Game
     */

    // Res Heavy Objects that have to be initialized only once
    public SpriteBatch batch;
    public PolygonSpriteBatch polyBatch;
    public ShapeRenderer shapeRenderer;
    public AssetManager assets;
    public HashMap<Constants.Skins, Skin> skins;

    //Screens
    public LoadingScreen loadingScreen;
    public MainMenuScreen mainMenuScreen;
    public GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        polyBatch = new PolygonSpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assets = new AssetManager();
        skins = new HashMap<Constants.Skins, Skin>();

        loadingScreen = new LoadingScreen(this);
        mainMenuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);

        // the Game Starts with the LoadingScreen
        this.setScreen(loadingScreen);
    }

    @Override
    public void render() {
            super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        loadingScreen.dispose();
        mainMenuScreen.dispose();
        gameScreen.dispose();

        batch.dispose();
        polyBatch.dispose();
        shapeRenderer.dispose();
        assets.dispose();
    }

}
