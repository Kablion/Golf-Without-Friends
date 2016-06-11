package de.kablion.golf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
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
        createFromMapData(MapData.createDebugMap());
    }

    public void createFromMapData(MapData mapData) {
        par = mapData.getPar();
        maxShootSpeed = mapData.getMaxShootSpeed();

        float aspectRatio = Gdx.graphics.getHeight() / Gdx.graphics.getHeight();

        ExtendViewport view = new ExtendViewport(mapData.getCmPerDisplayWidth(), mapData.getCmPerDisplayWidth()*aspectRatio);
        setViewport(view);
        getCamera().position.set(mapData.getCameraStartingPosition());
        balls = null;
        for (int i=0; i<playerAmount;i++){
            //balls.add(new Ball(mapData.getBallRadius(), mapData.getBallStartingPosition()));
        }

        // Debug Image
        debug = new Image(app.assets.get("badlogic.jpg", Texture.class));
        debug.setSize(256,256);
        debug.setPosition(0,0);
        debug.setVisible(true);
        root.addActor(debug);

        debug2 = new Image(app.assets.get("badlogic.jpg", Texture.class));
        debug2.setSize(256,256);
        debug2.setPosition(-256,-256);
        debug2.setVisible(true);
        root.addActor(debug2);

        walls = mapData.getWalls();
        if(walls != null){
            for (int i=0; i<walls.size;i++) root.addActor(walls.get(i));
        }
        grounds = mapData.getGrounds();
        if(grounds != null){
            for (int i=0; i<grounds.size;i++) root.addActor(grounds.get(i));
        }
        holes = mapData.getHoles();
        if(holes != null){
            for (int i=0; i<holes.size;i++) root.addActor(holes.get(i));
        }

    }

    public void createDebugMap() {
        par = 2;
        maxShootSpeed = 5;

        float cmPerWidth = 150;

        float aspectRatio = Gdx.graphics.getHeight() / Gdx.graphics.getHeight();
        Viewport view = new ExtendViewport(cmPerWidth, cmPerWidth*aspectRatio);
        setViewport(view);
        getCamera().position.set(0,0,0);
        getCamera().update();

        balls = new Array<Ball>();
        for (int i=0; i<playerAmount;i++){
            // Load balls
            balls.add(new Ball(5,0,-40));
        }

        // create Walls
        walls = new Array<Wall>();
        Wall tempWall = new Wall(0,0,50,20,0);
        root.addActor(tempWall);
        walls.add(tempWall);


        // Debug Image
        debug = new Image(app.assets.get("badlogic.jpg", Texture.class));
        debug.setSize(256,256);
        debug.setPosition(0,0);
        debug.setVisible(true);
        root.addActor(debug);


    }

    @Override
    public void act() {
        super.act();
        getCamera().position.add(0.5f);
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
}
