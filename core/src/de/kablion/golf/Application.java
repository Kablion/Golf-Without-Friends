package de.kablion.golf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.kablion.golf.screens.GameScreen;
import de.kablion.golf.screens.LoadingScreen;
import de.kablion.golf.screens.MainMenuScreen;

import static de.kablion.golf.utils.Constants.*;

public class Application extends Game {

	public SpriteBatch batch;
	public OrthographicCamera cameraUI;
	public AssetManager assets;
	public ShapeRenderer shapeRenderer;

	public BitmapFont font24;

	//Screens
	public LoadingScreen loadingScreen;
	public MainMenuScreen mainMenuScreen;
	public GameScreen gameScreen;
	
	@Override
	public void create () {
		assets = new AssetManager();
		batch = new SpriteBatch();

        cameraUI = new OrthographicCamera();
		cameraUI.setToOrtho(false, UI_WIDTH,UI_HEIGHT);

		initFonts();

		shapeRenderer = new ShapeRenderer();

		loadingScreen = new LoadingScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		gameScreen = new GameScreen(this);

        this.setScreen(loadingScreen);
	}

	@Override
	public void render () {
		super.render();
		cameraUI.update();
	}

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
	public void dispose() {
		batch.dispose();
		assets.dispose();

		loadingScreen.dispose();
		mainMenuScreen.dispose();
		gameScreen.dispose();
	}

	private void initFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/game_continue.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

		params.size = 50;
		params.color = Color.BLACK;
		font24 = generator.generateFont(params);
		generator.dispose();
	}

}
