package de.kablion.golf.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.kablion.golf.Application;
import de.kablion.golf.data.MapData;
import de.kablion.golf.data.WorldData;
import de.kablion.golf.utils.Constants;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static de.kablion.golf.utils.Constants.*;
public class MainMenuScreen implements Screen {

    private enum MenuPage {
        MAIN,WORLD_SELECT,MAP_SELECT,PLAY,SETTINGS
    }

    /**
     * Screen to navigate around
     */

    private final Application app;
    private final Stage stage;
    private Table rootTable = new Table();
    private Table menuTable = new Table();
    private Label titleLabel;

    private MenuPage currentPage;

    TextButton tempButton;

    public MainMenuScreen(final Application app) {
        this.app = app;
        this.stage = new Stage(new ExtendViewport(UI_WIDTH, UI_HEIGHT), app.batch);
    }

    @Override
    public void show() {
        Gdx.app.log("Screen:","MAIN_MENU");

        stage.clear();
        rootTable.clear();
        menuTable.clear();

//        initSkin();
        initStage();
        setPage(MenuPage.MAIN);

        Gdx.input.setInputProcessor(stage);

    }

/*    private void initSkin() {

        //Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/game_continue.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 50;
        params.color = Color.BLACK;
        app.skins.get(Skins.MENU).add("default-font", generator.generateFont(params));
        params.size = 100;
        app.skins.get(Skins.MENU).add("big-font", generator.generateFont(params));
        generator.dispose();


        mainMenuSkin.load(Gdx.files.internal("skins/default.json"));
    }*/

    private void initStage() {
        rootTable.setFillParent(true);
        rootTable.add(menuTable).center().top().width(UI_WIDTH*0.75f).padTop(UI_HEIGHT*0.12f).expandY();
        stage.addActor(rootTable);

        // Title Label
        titleLabel = new Label("Test", app.skins.get(Skins.MENU), "big-font", Color.BLACK);
        menuTable.add(titleLabel).padBottom(40);
        menuTable.row();
        menuTable.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
    }

    private void update(float delta) {
        stage.act();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.draw();

    }

    private void setPage(MenuPage menuPage) {
        currentPage = menuPage;

        menuTable.clear();
        switch (menuPage) {

            case MAIN :
                titleLabel.setText("Golf Without Friends");

                // Play Button
                tempButton = new TextButton("Play", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/button_click.wav", Sound.class).play();
                        //app.setScreen(app.gameScreen);
                        setPage(MenuPage.PLAY);
                    }
                });
                menuTable.add(tempButton).expandX().fill().padBottom(20);
                menuTable.row();

                // Settings Button
                tempButton = new TextButton("Settings", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/button_click.wav", Sound.class).play();
                        //app.setScreen(app.gameScreen);
                        setPage(MenuPage.SETTINGS);
                    }
                });
                menuTable.add(tempButton).expandX().fill().padBottom(20);
                menuTable.row();

                // Exit Button
                tempButton = new TextButton("Exit", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/exit.wav", Sound.class).play();
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                Gdx.app.exit();
                            }
                        },0.5f);
                    }
                });
                menuTable.add(tempButton).expandX().fill();
                break;

            case PLAY:
                titleLabel.setText("Want to play a World or just a Map?");

                // Map Button
                tempButton = new TextButton("A Map", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/button_click.wav", Sound.class).play();
                        //app.setScreen(app.gameScreen);
                        setPage(MenuPage.MAP_SELECT);
                    }
                });
                menuTable.add(tempButton).expandX().fill().padBottom(20);
                menuTable.row();
                // World Button
                tempButton = new TextButton("A World", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/button_click.wav", Sound.class).play();
                        //app.setScreen(app.gameScreen);
                        setPage(MenuPage.WORLD_SELECT);
                    }
                });
                menuTable.add(tempButton).expandX().fill().padBottom(20);
                menuTable.row();
                //Back Button
                tempButton = new TextButton("Back", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/exit.wav", Sound.class).play();
                        setPage(MenuPage.MAIN);
                    }
                });
                menuTable.add(tempButton).expandX().fill();
                break;

            case MAP_SELECT:
                titleLabel.setText("Select a Map");

                //Map Buttons
                FileHandle[] mapFiles = MapData.getMaps(true);
                if(mapFiles.length == 0) {
                    MapData.createFirstDebugMap();
                    mapFiles = Gdx.files.external(Constants.MAPS_PATH).list();
                }
                for(FileHandle mapFile : mapFiles) {
                    Gdx.app.log("MapFile: ", mapFile.path());
                    if(mapFile.extension().equals("json")) {
                        try {
                            final MapData mapData = MapData.loadMap(mapFile);
                            tempButton = new TextButton(mapData.name, app.skins.get(Skins.MENU), "default");
                            if(mapFile.path().contains(INTERNAL_MAPS_PATH)) tempButton.setText("Default: "+ mapData.name);
                            tempButton.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    app.assets.get("sounds/button_click.wav", Sound.class).play();
                                    app.gameScreen.worldData = new WorldData();
                                    app.gameScreen.worldData.maps.add(mapData);
                                    app.gameScreen.currentMapNumber = 0;
                                    app.setScreen(app.gameScreen);
                                }
                            });
                            menuTable.add(tempButton).expandX().fill().padBottom(20);
                            menuTable.row();
                        } catch (IllegalArgumentException e) {
                            Gdx.app.error("Error:",e.getMessage());
                        }
                    }
                }

                //Back Button
                tempButton = new TextButton("Back", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/exit.wav", Sound.class).play();
                        setPage(MenuPage.PLAY);
                    }
                });
                menuTable.add(tempButton).expandX().fill();
                break;

            case WORLD_SELECT:
                titleLabel.setText("Select a World");

                //World Buttons
                FileHandle[] worldFiles = WorldData.getWorlds(true);
                if(worldFiles.length == 0) {
                    WorldData.createDebugWorld();
                    worldFiles = Gdx.files.external(Constants.WORLDS_PATH).list();
                }
                for(FileHandle worldFile : worldFiles) {
                    Gdx.app.log("WorldFile: ", worldFile.path());
                    if(worldFile.extension().equals("json")) {
                        try {
                            final WorldData worldData = WorldData.loadWorld(worldFile.name());
                            tempButton = new TextButton(worldData.name, app.skins.get(Skins.MENU), "default");
                            if(worldFile.path().contains(INTERNAL_WORLDS_PATH)) tempButton.setText("Default: "+ worldData.name);
                            tempButton.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    app.assets.get("sounds/button_click.wav", Sound.class).play();
                                    app.gameScreen.worldData = worldData;
                                    app.gameScreen.currentMapNumber = 0;
                                    app.gameScreen.neededStrokesPerMap = new int[worldData.maps.size];
                                    app.setScreen(app.gameScreen);
                                }
                            });
                            menuTable.add(tempButton).expandX().fill().padBottom(20);
                            menuTable.row();
                        } catch (IllegalArgumentException e) {
                            Gdx.app.error("Error:",e.getMessage());
                        }
                    }
                }

                //Back Button
                tempButton = new TextButton("Back", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/exit.wav", Sound.class).play();
                        setPage(MenuPage.PLAY);
                    }
                });
                menuTable.add(tempButton).expandX().fill();

                break;

            case SETTINGS:
                titleLabel.setText("Settings");

                // Play Mode
                Table tempTable = new Table();
                Label tempLabel = new Label("Play Mode: ",app.skins.get(Skins.MENU),"default-font", Color.BLACK);
                CheckBox tempCheckBox = new CheckBox("",app.skins.get(Skins.MENU));
                tempCheckBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                    }
                });

                tempTable.add(tempLabel).pad(15);
                tempTable.add(tempCheckBox);
                menuTable.add(tempTable).expandX().fill().pad(15);
                menuTable.row();

                //Back Button
                tempButton = new TextButton("Back", app.skins.get(Skins.MENU), "default");
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.assets.get("sounds/exit.wav", Sound.class).play();
                        setPage(MenuPage.MAIN);
                    }
                });
                menuTable.add(tempButton).expandX().fill();
                default:
        }
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
