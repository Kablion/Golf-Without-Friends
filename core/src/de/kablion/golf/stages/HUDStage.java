package de.kablion.golf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import de.kablion.golf.Application;

import static de.kablion.golf.utils.Constants.*;

public class HUDStage extends Stage {

    private Application app;
    private WorldStage worldStage;
    private Skin hudSkin;
    private Skin skin;

    private Table rootTable;
    private Table shootBar;
    private Table buttons;

    private Image cameraOverlay;
    private Label strokeLabel;
    private ImageButton shootButton;
    private ImageButton cameraButton;
    private ImageButton backButton;
    private Slider powerBar;

    private Vector3 shootVelocity;

    public HUDStage(Application application, WorldStage stage) {
        super(new ExtendViewport(UI_WIDTH,UI_HEIGHT),application.batch);
        this.app = application;
        this.worldStage = stage;
        this.hudSkin = new Skin();
        this.skin = new Skin();
        this.shootVelocity = new Vector3();


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
        rootTable = new Table();
        rootTable.setFillParent(true);
        addActor(rootTable);
    }

    private void initShootBar() {
        shootBar = new Table();
        powerBar = new Slider(0,1,0.01f,false,skin);
        shootButton = new ImageButton(hudSkin, "shoot-button");
        shootButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!worldStage.getBall().isMoving()) {
                    worldStage.getBall().shoot(shootVelocity);
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
        buttons = new Table();
        Table leftButtons = new Table();
        leftButtons.align(Align.left);
        buttons.add(leftButtons).expand().fill().left();
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
        strokeLabel = new Label("Stroke: ",labelStyle);

        rootTable.add(strokeLabel).align(Align.bottomRight);
    }

    @Override
    public void draw() {
        super.draw();
        if (!worldStage.getBall().isMoving()) {
            app.shapeRenderer.setColor(Color.GRAY);
            app.shapeRenderer.setProjectionMatrix(worldStage.getCamera().combined);
            app.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            app.shapeRenderer.rectLine(worldStage.getBall().getOriginX(),
                    worldStage.getBall().getOriginY(),
                    worldStage.getBall().getOriginX() + shootVelocity.x,
                    worldStage.getBall().getOriginY() + shootVelocity.y,
                    1.5f);
            app.shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        hudSkin.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean handled = super.touchDown(screenX, screenY, pointer, button);
        if (worldStage.getMode() == WorldStage.PLAY_MODE) {
            if (!handled & pointer == 0) {
                shootVelocity = worldStage.getCamera().unproject(new Vector3(screenX, screenY, 0));
                shootVelocity.sub(worldStage.getBall().getOriginX(), worldStage.getBall().getOriginY(), 0);
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean handled = super.touchDragged(screenX, screenY, pointer);
        if (worldStage.getMode() == WorldStage.PLAY_MODE) {
            if (!handled & pointer == 0) {
                shootVelocity = worldStage.getCamera().unproject(new Vector3(screenX, screenY, 0));
                shootVelocity.sub(worldStage.getBall().getOriginX(), worldStage.getBall().getOriginY(), 0);
                handled = true;
            }
        }
        return handled;
    }
}
