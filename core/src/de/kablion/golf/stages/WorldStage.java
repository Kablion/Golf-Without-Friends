package de.kablion.golf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.kablion.golf.Application;
import de.kablion.golf.data.MapData;
import de.kablion.golf.actors.Ball;
import de.kablion.golf.actors.Ground;
import de.kablion.golf.actors.Hole;
import de.kablion.golf.actors.Wall;

public class WorldStage extends Stage {

    private Application app;

    public static final int PLAY_MODE = 0;
    public static final int CAMERA_MODE = 1;

    private int mode;
    private int playerAmount;

    private int par;
    private float maxShootSpeed;

    private Array<Ball> balls;
    private Array<Wall> walls;
    private Array<Ground> grounds;
    private Array<Hole> holes;
    private Group root;

    private Image debug;
    private Image debug2;

    public WorldStage(Application app){
        super(new ExtendViewport(10,10),app.batch);
        this.app = app;
        root = new Group();
        addActor(root);
    }

    public void reset(){
        root.clear();
        mode = PLAY_MODE;
        playerAmount = 1;
        //createFromMapData(MapData.loadMap("Maps/default.json"));
        createFromMapData(MapData.createDebugMap(app));
    }

    public void createFromMapData(MapData mapData) {
        par = mapData.getPar();
        maxShootSpeed = mapData.getMaxShootSpeed();

        float aspectRatio = Gdx.graphics.getHeight() / Gdx.graphics.getHeight();

        ExtendViewport view = new ExtendViewport(mapData.getCmPerDisplayWidth(), mapData.getCmPerDisplayWidth()*aspectRatio);
        setViewport(view);
        getCamera().position.set(mapData.getCameraStartingPosition());

        // Debug Image
        /*debug = new Image(app.assets.get("badlogic.jpg", Texture.class));
        debug.setSize(256,256);
        debug.setPosition(0,0);
        debug.setVisible(true);
        root.addActor(debug);

        debug2 = new Image(app.assets.get("badlogic.jpg", Texture.class));
        debug2.setSize(256,256);
        debug2.setPosition(-256,-256);
        debug2.setVisible(true);
        root.addActor(debug2);*/

        grounds = mapData.getGrounds();
        if(grounds != null){
            for (int i=0; i<grounds.size;i++) root.addActor(grounds.get(i));
        }
        walls = mapData.getWalls();
        if(walls != null){
            for (int i=0; i<walls.size;i++){
                root.addActor(walls.get(i));
            }
        }
        holes = mapData.getHoles();
        if(holes != null){
            for (int i = 0; i < holes.size; i++) {
                root.addActor(holes.get(i));
            }
        }

        balls = new Array<Ball>();
        Vector3 startingPosition = mapData.getBallStartingPosition();
        for (int i=0; i<playerAmount;i++){
            Ball tempBall = new Ball(startingPosition.x, startingPosition.y, mapData.getBallRadius());
            balls.add(tempBall);
            root.addActor(tempBall);
        }

    }

    @Override
    public void act() {
        super.act();
    }

    @Override
    public void draw() {
        super.draw();

    }

    @Override
    public void dispose() {
        super.dispose();
        if(balls != null) {
            balls.clear();
        }
        if(walls != null) {
            walls.clear();
        }
        if(grounds != null) {
            grounds.clear();
        }
        if(holes != null) {
            holes.clear();
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        if (mode == CAMERA_MODE | mode == PLAY_MODE) {
            this.mode = mode;
        } else {
            throw new IllegalArgumentException("Mode is not defined: " + mode);
        }
    }

    public Ball getBall() {
        return balls.get(0);
    }
}
