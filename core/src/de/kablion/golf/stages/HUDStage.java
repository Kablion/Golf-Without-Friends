package de.kablion.golf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.kablion.golf.Application;
import de.kablion.golf.actors.entities.Ball;
import de.kablion.golf.data.MapData;

import static de.kablion.golf.utils.Constants.*;

public class HUDStage extends Stage {

    /**
     * Stage that handles and represents the overlay of one players WorldStage
     */

    private Application app;
    private WorldStage worldStage;
    private Skin hudSkin = new Skin();
    private Skin defaultSkin = new Skin();

    private Table rootTable = new Table();
    private Table shootBar = new Table();
    private Table buttons = new Table();

    private Image cameraOverlay;
    private Label strokeLabel;
    private Label fpsLabel;
    private ImageButton shootButton;
    private ImageButton cameraButton;
    private ImageButton followBallButton;
    private ImageButton backButton;
    private Slider powerBar;

    private Dialog mapEndingDialog;

    // FPS which is currently shown
    private float fpsCurrent;
    // When the fpsShown was last updated
    private long fpsUpdated;
    // How long will the fps Stay on the Screen
    private static final long fpsRestTime = 1;

    public HUDStage(Application application, WorldStage stage) {
        super(new ExtendViewport(UI_WIDTH, UI_HEIGHT), application.batch);
        this.app = application;
        this.worldStage = stage;


    }

    public void reset() {

        initSkins();
        initRoot();
        initButtons();
        rootTable.row();
        initCameraOverlay();
        rootTable.row();
        initStrokeLabel();
        rootTable.row();
        initShootBar();
    }

    private void initSkins() {
        hudSkin.addRegions(app.assets.get(HUD_ATLAS_PATH, TextureAtlas.class));
        hudSkin.load(Gdx.files.internal(HUD_SKIN_PATH));

        defaultSkin.addRegions(app.assets.get("skins/default.atlas", TextureAtlas.class));
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/game_continue.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 50;
        params.color = Color.BLACK;
        defaultSkin.add("default-font", generator.generateFont(params));
        generator.dispose();
        defaultSkin.load(Gdx.files.internal("skins/default.json"));
    }

    private void initRoot() {
        clear();
        rootTable.clear();
        rootTable.setFillParent(true);
        addActor(rootTable);
    }

    private void initShootBar() {
        shootBar.clear();
        Slider.SliderStyle powerBarStyle = new Slider.SliderStyle();
        TextureAtlas hudAtlas = app.assets.get(HUD_ATLAS_PATH, TextureAtlas.class);
        powerBarStyle.background = new TextureRegionDrawable(hudAtlas.findRegion("power_bar_background"));
        powerBarStyle.background.setMinHeight(SHOOTBAR_HEIGHT);
        powerBarStyle.background.setMinWidth(2);

        //Filling with grey transparent
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        powerBarStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        powerBarStyle.knobBefore.setMinHeight(SHOOTBAR_HEIGHT);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        powerBarStyle.knobAfter = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        powerBarStyle.knobAfter.setMinHeight(SHOOTBAR_HEIGHT);
        pixmap.dispose();

        powerBar = new Slider(0.01f, 1, 0.001f, false, powerBarStyle);
        powerBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Vector2 tempShootVelocity = worldStage.getBall().getShootVelocity();
                if (tempShootVelocity.x == 0 && tempShootVelocity.y == 0) {
                    tempShootVelocity.set(0, 1);
                }
                tempShootVelocity.setLength(powerBar.getValue() * worldStage.getWorld().getMapData().maxShootSpeed);
                worldStage.getBall().setShootVelocity(tempShootVelocity);
            }
        });

        powerBar.setDisabled(true);


        /*shootButton = new ImageButton(hudSkin, "shoot-button");
        shootButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (worldStage.getBall().isOffGround) {
                    worldStage.getBall().resetBeforeShot();
                }

            }
        });
        shootBar.add(shootButton).right().height(SHOOTBAR_HEIGHT).width(SHOOTBAR_HEIGHT);*/

        shootBar.add(powerBar).left().expand().fill().pad(10);
        //shootBar.setHeight(UI_HEIGHT/12);
        shootBar.right().bottom();
        rootTable.add(shootBar).expandX().fillX().align(Align.bottom);


        // Animated Knob try
       /* TextureAtlas animationAtlas = app.assets.get("spritesheets/power_bar_animation.atlas", TextureAtlas.class);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(animationAtlas.findRegion("1"));
        frames.add(animationAtlas.findRegion("2"));
        frames.add(animationAtlas.findRegion("3"));
        frames.add(animationAtlas.findRegion("4"));
        frames.add(animationAtlas.findRegion("5"));
        frames.add(animationAtlas.findRegion("6"));
        Animation animation = new Animation(0.5f,frames);
        animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        powerBarStyle.knob = new AnimationDrawable(animation);
        powerBarStyle.knob.setMinHeight(SHOOTBAR_HEIGHT);
        powerBarStyle.knob.setMinWidth(20);*/
    }

    private void initButtons() {
        buttons.clear();
        Table leftButtons = new Table();
        leftButtons.align(Align.left);
        buttons.add(leftButtons).expand().fill().left();
        Table centerLabels = new Table();
        centerLabels.align(Align.center);
        buttons.add(centerLabels).width(0).center();
        Table rightButtons = new Table();
        rightButtons.align(Align.right);
        buttons.add(rightButtons).expand().fill().right();


        backButton = new ImageButton(hudSkin, "back-button");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                app.assets.get(CLICK_SOUND, Sound.class).play();
                app.setScreen(app.mainMenuScreen);
            }
        });
        leftButtons.add(backButton).width(HUD_BUTTON_SIZE).height(HUD_BUTTON_SIZE);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/game_continue.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 50;
        params.color = Color.BLACK;
        Label.LabelStyle labelStyle = new Label.LabelStyle(generator.generateFont(params), Color.BLACK);
        fpsLabel = new Label("FPS: ", labelStyle);
        fpsLabel.setFontScale(0.5f);
        centerLabels.add(fpsLabel);

        followBallButton = new ImageButton(hudSkin, "follow-ball-toggle");
        followBallButton.setChecked(worldStage.isCameraOnBall);
        followBallButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                app.assets.get(CLICK_SOUND, Sound.class).play();

                if (!followBallButton.isChecked()) {
                    followBallButton.setChecked(false);
                    worldStage.isCameraOnBall = false;
                } else {
                    followBallButton.setChecked(true);
                    worldStage.getCamera().position.set(worldStage.getBall().getX(), worldStage.getBall().getY(), worldStage.getCamera().position.z);
                    worldStage.isCameraOnBall = true;
                }
            }
        });
        rightButtons.add(followBallButton).width(HUD_BUTTON_SIZE).height(HUD_BUTTON_SIZE);

        cameraButton = new ImageButton(hudSkin, "camera-mode-toggle");
        cameraButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                app.assets.get(CLICK_SOUND, Sound.class).play();

                if (!cameraButton.isChecked()) {
                    cameraButton.setChecked(false);
                    cameraOverlay.setVisible(false);
                    worldStage.setMode(WorldStage.InputMode.PLAY);
                    if (worldStage.isCameraOnBall) {
                        worldStage.updateCamera();
                    }
                } else {
                    cameraButton.setChecked(true);
                    cameraOverlay.setVisible(true);
                    worldStage.setMode(WorldStage.InputMode.CAMERA);
                }
            }
        });
        rightButtons.add(cameraButton).width(HUD_BUTTON_SIZE).height(HUD_BUTTON_SIZE);

        rootTable.add(buttons).align(Align.top).expandX().fillX();
    }

    private void initCameraOverlay() {
        TextureRegion overlayImage = ((TextureAtlas) app.assets.get(HUD_ATLAS_PATH)).findRegion("camera_overlay");
        cameraOverlay = new Image(overlayImage);
        cameraOverlay.setVisible(false);
        rootTable.add(cameraOverlay).expand().fill();
    }

    private void initStrokeLabel() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/game_continue.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 50;
        params.color = Color.BLACK;
        Label.LabelStyle labelStyle = new Label.LabelStyle(generator.generateFont(params), Color.BLACK);
        strokeLabel = new Label("Stroke: 0", labelStyle);

        rootTable.add(strokeLabel).align(Align.bottomRight).padRight(20);
    }

    private void initMapEndingDialog() {
        mapEndingDialog = new Dialog("Congrats, Results:", app.skins.get(Skins.MENU));

        //Content
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/game_continue.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 30;
        params.color = Color.BLACK;

        Label.LabelStyle labelStyle = new Label.LabelStyle(generator.generateFont(params), Color.BLACK);
        Label mapNameTopLabel = new Label("Map",labelStyle);
        Label strokeFinalTopLabel = new Label("Strokes", labelStyle);

        mapEndingDialog.getContentTable().add(mapNameTopLabel).align(Align.center);
        mapEndingDialog.getContentTable().add(strokeFinalTopLabel).align(Align.center);
        mapEndingDialog.getContentTable().row();

        for(int i=0; i<=app.gameScreen.currentMapNumber; i++) {
            Array<MapData> maps = app.gameScreen.worldData.maps;
            Label mapNameLabel = new Label(maps.get(i).name+":",labelStyle);
            Label strokeFinalLabel = new Label(""+app.gameScreen.neededStrokesPerMap[i], labelStyle);

            mapEndingDialog.getContentTable().add(mapNameLabel).align(Align.center);
            mapEndingDialog.getContentTable().add(strokeFinalLabel).align(Align.center);
            mapEndingDialog.getContentTable().row();
        }

        //Buttons
        if (app.gameScreen.currentMapNumber+1 < app.gameScreen.worldData.maps.size) {
            //Next Map
            TextButton nextButton = new TextButton("Next Map", app.skins.get(Skins.MENU), "default");
            nextButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    app.assets.get(CLICK_SOUND, Sound.class).play();
                    app.gameScreen.currentMapNumber++;
                    app.setScreen(app.gameScreen);
                }
            });
            mapEndingDialog.getButtonTable().add(nextButton).fill();
        } else {
            //End of the World
            TextButton nextButton = new TextButton("Return to Main Menu", app.skins.get(Skins.MENU), "default");
            nextButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    app.assets.get(CLICK_SOUND, Sound.class).play();
                    app.setScreen(app.mainMenuScreen);
                }
            });
            mapEndingDialog.getButtonTable().add(nextButton).fill();
        }

        //Position and Add to HUDStage
        mapEndingDialog.align(Align.center);
        mapEndingDialog.setWidth(UI_WIDTH*0.75f);
        mapEndingDialog.setHeight(UI_HEIGHT*0.30f);
        mapEndingDialog.setPosition(UI_WIDTH/2f - mapEndingDialog.getWidth()/2,
                UI_HEIGHT/2f - mapEndingDialog.getHeight()/2);
        addActor(mapEndingDialog);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //((AnimationDrawable)powerBar.getStyle().knob).act(delta);

        float fps = 1 / delta;
        if (fpsUpdated + fpsRestTime * 500 < System.currentTimeMillis()) {
            fpsCurrent = fps;
            fpsUpdated = System.currentTimeMillis();
            fpsLabel.setText("FPS: " + MathUtils.round(fpsCurrent));
        }

        updateCameraOverlay();
        updatePowerBar();
        updateStrokeCounter();

        checkMapEnding();
    }

    public void updateCameraOverlay() {
        if (cameraButton.isChecked() != (worldStage.getMode() == WorldStage.InputMode.CAMERA)) {
            cameraButton.setChecked((worldStage.getMode() == WorldStage.InputMode.CAMERA));
            cameraOverlay.setVisible((worldStage.getMode() == WorldStage.InputMode.CAMERA));
        }
    }

    public void updatePowerBar() {
        Ball ball = worldStage.getBall();

        powerBar.setValue(ball.getShootVelocity().len() / worldStage.getWorld().getMapData().maxShootSpeed);

        if (ball.isMoving()) {
            powerBar.setValue(ball.getSpeed() / Ball.SHOOT_MULTIPLICATOR / worldStage.getWorld().getMapData().maxShootSpeed);
        } else {
            if (ball.isInHole || ball.isOffGround) {
                powerBar.setValue(0.01f);
            }
        }
    }

    public void updateStrokeCounter() {
        strokeLabel.setText("Stroke: " + worldStage.getBall().getStroke());
    }

    public void checkMapEnding() {
        if (worldStage.getBall().isInHole) {
            if (worldStage.getBall().inHoleTime > 2) {
                if(mapEndingDialog == null) {
                    app.gameScreen.neededStrokesPerMap[app.gameScreen.currentMapNumber] = worldStage.getBall().getStroke();
                    initMapEndingDialog();
                }
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        clear();
        defaultSkin.dispose();
        defaultSkin.remove("default-font", BitmapFont.class);
        hudSkin.dispose();
        worldStage = null;
    }

    public WorldStage getWorldStage() {
        return worldStage;
    }
}
