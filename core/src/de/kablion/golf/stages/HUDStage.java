package de.kablion.golf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import de.kablion.golf.Application;
import de.kablion.golf.actors.Ball;
import de.kablion.golf.actors.ShootArrow;
import javafx.scene.control.Tab;

import static de.kablion.golf.utils.Constants.*;

public class HUDStage extends Stage {

    private Application app;
    private WorldStage worldStage;
    private Skin hudSkin = new Skin();
    private Skin skin = new Skin();

    private Table rootTable = new Table();
    private Table shootBar = new Table();
    private Table buttons = new Table();

    private Image cameraOverlay;
    private Label strokeLabel;
    private Label fpsLabel;
    private ImageButton shootButton;
    private ImageButton cameraButton;
    private ImageButton backButton;
    private Slider powerBar;

    // FPS which is currently shown
    private float fpsCurrent;
    // When the fpsShown was last updated
    private long fpsUpdated;
    // How long will the fps Stay on the Screen
    private static final long fpsRestTime = 1;

    public HUDStage(Application application, WorldStage stage) {
        super(new ExtendViewport(UI_WIDTH,UI_HEIGHT),application.batch);
        this.app = application;
        this.worldStage = stage;


    }

    public void reset() {
        hudSkin.addRegions(app.assets.get("skins/game_hud.atlas", TextureAtlas.class));
        hudSkin.load(Gdx.files.internal("skins/game_hud.json"));
        this.skin.addRegions(app.assets.get("skins/default.atlas", TextureAtlas.class));
        this.skin.add("default-font", app.font24);
        this.skin.load(Gdx.files.internal("skins/default.json"));

        initRoot();
        initButtons();
        rootTable.row();
        initCameraOverlay();
        rootTable.row();
        initStrokeLabel();
        rootTable.row();
        initShootBar();
    }

    private void initRoot() {
        clear();
        rootTable.clear();
        rootTable.setFillParent(true);
        addActor(rootTable);
    }

    private void initShootBar() {
        shootBar.clear();
        powerBar = new Slider(0.001f, 1, 0.001f, false, skin);
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
        shootButton = new ImageButton(hudSkin, "shoot-button");
        shootButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!worldStage.getBall().isMoving() && !worldStage.getBall().isInHole && !worldStage.getBall().isOffGround) {
                    if (powerBar.getValue() > 0.01f) {
                        worldStage.getBall().shoot();
                        strokeLabel.setText("Stroke: " + worldStage.getBall().getStroke());
                    }
                } else if (worldStage.getBall().isInHole) {
                    app.setScreen(app.gameScreen);
                } else if (worldStage.getBall().isOffGround) {
                    worldStage.getBall().resetBeforeShot();
                }

            }
        });
        shootBar.add(powerBar).left().expandX().fill().padLeft(10).padRight(10);
        shootBar.add(shootButton).right().height(SHOOTBAR_HEIGHT).width(SHOOTBAR_HEIGHT);
        //shootBar.setHeight(UI_HEIGHT/12);
        shootBar.right().bottom();

        rootTable.add(shootBar).expandX().fillX().align(Align.bottom);
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
                app.setScreen(app.mainMenuScreen);
            }
        });
        leftButtons.add(backButton).width(HUD_BUTTON_SIZE).height(HUD_BUTTON_SIZE);

        Label.LabelStyle labelStyle = new Label.LabelStyle(app.font24, Color.BLACK);
        fpsLabel = new Label("FPS: ", labelStyle);
        fpsLabel.setFontScale(0.5f);
        centerLabels.add(fpsLabel);

        cameraButton = new ImageButton(hudSkin, "camera-mode-toggle");
        cameraButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if(!cameraButton.isChecked()){
                    cameraButton.setChecked(false);
                    cameraOverlay.setVisible(false);
                    worldStage.setMode(WorldStage.PLAY_MODE);
                } else {
                    cameraButton.setChecked(true);
                    cameraOverlay.setVisible(true);
                    worldStage.setMode(WorldStage.CAMERA_MODE);
                }
            }
        });
        rightButtons.add(cameraButton).width(HUD_BUTTON_SIZE).height(HUD_BUTTON_SIZE);

        rootTable.add(buttons).align(Align.top).expandX().fillX();
    }

    private void initCameraOverlay() {
        TextureRegion overlayImage = ((TextureAtlas)app.assets.get("skins/game_hud.atlas")).findRegion("camera_overlay");
        cameraOverlay = new Image(overlayImage);
        cameraOverlay.setVisible(false);
        rootTable.add(cameraOverlay).expand().fill();
    }

    private void initStrokeLabel() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(app.font24, Color.BLACK);
        strokeLabel = new Label("Stroke: 0", labelStyle);

        rootTable.add(strokeLabel).align(Align.bottomRight).padRight(20);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float fps = 1 / delta;
        if (fpsUpdated + fpsRestTime * 500 < System.currentTimeMillis()) {
            fpsCurrent = fps;
            fpsUpdated = System.currentTimeMillis();
            fpsLabel.setText("FPS: " + MathUtils.round(fpsCurrent));
        }

        Ball ball = worldStage.getBall();

        powerBar.setValue(ball.getShootVelocity().len() / worldStage.getWorld().getMapData().maxShootSpeed);

        if (worldStage.getBall().isMoving()) {
            powerBar.setValue(worldStage.getBall().getSpeed() / Ball.SHOOT_MULTIPLICATOR / worldStage.getWorld().getMapData().maxShootSpeed);
        }
        if (worldStage.getBall().isInHole) {
            powerBar.setValue(0);
        }
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        hudSkin.dispose();
        worldStage = null;
    }

    public WorldStage getWorldStage() {
        return worldStage;
    }
}
