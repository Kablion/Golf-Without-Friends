package de.kablion.golf.editor.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.kablion.golf.actors.World;
import de.kablion.golf.actors.entities.Entity;
import de.kablion.golf.data.actors.*;
import de.kablion.golf.editor.Application;

import static de.kablion.golf.utils.Constants.HUD_ATLAS_PATH;

public class WorldStage extends Stage {



    /**
     * Stage that handles and represents the World for one player
     */

    private Application app;

    private HUDStage hudStage;

    private World world;

    private Image cameraImage;

    private static final float vertRadius = 1.5f;

    public WorldStage(Application app, World world, int player) {
        super(new ExtendViewport(10, 10), app.polyBatch);
        this.app = app;
        this.world = world;
        addActor(world);
    }

    public void setHUDStage(HUDStage hudStage) {
        this.hudStage = hudStage;
    }

    public void reset() {
        clear();
        getWorld().reset();

        //float aspectRatio = Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        float aspectRatio = 18f/9f;

        //ExtendViewport view = new ExtendViewport(world.getMapData().camera.cmPerDisplayWidth, world.getMapData().camera.cmPerDisplayWidth * aspectRatio);
        //setViewport(view);
        ((ExtendViewport)getViewport()).setMinWorldWidth(world.getMapData().camera.cmPerDisplayWidth);
        ((ExtendViewport)getViewport()).setMinWorldHeight(world.getMapData().camera.cmPerDisplayWidth * aspectRatio);
        getCamera().position.set(world.getMapData().camera.position);
        ((OrthographicCamera)getCamera()).zoom = 1;
        getViewport().update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        this.addActor(getWorld());

        initCameraOverlay();
    }

    public void loadMap() {
        getWorld().reset();
    }

    private void initCameraOverlay() {
        TextureRegion overlayImage = ((TextureAtlas) app.assets.get(HUD_ATLAS_PATH)).findRegion("camera_overlay");
        cameraImage = new Image(overlayImage);
        cameraImage.setVisible(false);
        CameraData cameraData = getWorld().getMapData().camera;
        float width = cameraData.cmPerDisplayWidth;
        float height = width*1.5f;
        cameraImage.setBounds(cameraData.position.x-width/2f, cameraData.position.y-height/2f, width, height);
        this.addActor(cameraImage);
    }

    @Override
    public void act(float delta) {
        // super.act(delta);
    }

    @Override
    public void draw() {
        super.draw();


        app.shapeRenderer.setProjectionMatrix(getCamera().combined);

        app.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setAutoShapeType(true);


        // Draw (0,0) Circle
        app.shapeRenderer.setColor(Color.GRAY);
        app.shapeRenderer.circle(0,0, 3);

        // Draw Grid
        int gridSize = 1000;
        int gridCellSize = 25;
        Vector2 startOfGrid = new Vector2(-gridSize/2f, -gridSize/2f);
        int maxRows = gridSize/gridCellSize;
        int maxCols = maxRows;
        // Draw Horrizontal Lines
        for (int row=0; row<=maxRows; row++) {
            float yOfLine = startOfGrid.y+(row*gridCellSize);
            app.shapeRenderer.line(startOfGrid.x,yOfLine,startOfGrid.x+gridSize,yOfLine);
        }
        // Draw Vertical Lines
        for (int col=0; col<=maxCols; col++) {
            float xOfLine = startOfGrid.x+(col*gridCellSize);
            app.shapeRenderer.line(xOfLine, startOfGrid.y, xOfLine,startOfGrid.y+gridSize);
        }

        //Draw Selected EntityData
        app.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        EntityData entityData = getSelectedEntity().entityData;

        Vector2 origin = entityData.getPosition();

        // Draw Origin
        app.shapeRenderer.setColor(Color.RED);
        app.shapeRenderer.circle(origin.x, origin.y,vertRadius*1.5f);

        // Draw Vertices
        float[] verts = null;
        Color colorOfVerts = Color.BLACK;
        float r = 0;
        float rotation = 0;
        switch (entityData.getType()) {
            case MAGNET:
                MagnetData magnetData = (MagnetData) entityData;
                r = magnetData.range;
                verts = new float[]{0, 0, r, 0};

                break;
            case GROUND:
                GroundData groundData = (GroundData) entityData;
                verts = groundData.vertices;
                if(verts == null) verts = new float[]{0,0};
                if(verts.length == 2) {
                    verts = new float[]{0, 0, verts[0], verts[1]};
                }
                rotation = groundData.rotation;
                break;
            case WALL:
                WallData wallData = (WallData) entityData;
                if (wallData.isCircle) {
                    r = wallData.length;
                    verts = new float[]{0, 0, r, 0};
                } else {
                    if(wallData.length == 0) {
                        // From To Position
                        verts = new float[]{origin.x, origin.y, wallData.toPosition.x, wallData.toPosition.y};
                        // To Position has not to be added with origin
                        origin = new Vector2(0,0);
                    } else {
                        r = wallData.length;
                        verts = new float[]{0, 0, r, 0};
                    }
                }
                rotation = wallData.rotation;
                break;
            case HOLE:
                HoleData holeData = (HoleData) entityData;
                r = holeData.radius;
                verts = new float[]{0, 0, r, 0};
                colorOfVerts = Color.WHITE;
                break;
            case BALL:
                BallData ballData = (BallData) entityData;
                r = ballData.radius;
                verts = new float[]{0, 0, r, 0};
                break;

                default:
                    break;
        }
        drawVerts(verts, origin, rotation, colorOfVerts);

        // Draw Selected Vertices
        Vector2 vert = hudStage.getSelectedVertices();
        if(vert != null) {
            float[] selVert = {vert.x, vert.y};
            drawVerts(selVert, origin, rotation, Color.YELLOW);
        }




        // Old Draw Vertices of the Selected Entity
        /*Entity selectedEntity = hudStage.getSelectedEntity();
        if(selectedEntity != null) {
            float[] verts;
            if(selectedEntity.getShape() instanceof Circle) {
                verts = new float[4];
                Circle circle = (Circle) selectedEntity.getShape();
                verts[0] = selectedEntity.getX();
                verts[1] = selectedEntity.getY();
                verts[2] = selectedEntity.getX()+circle.radius;
                verts[3] = selectedEntity.getY();
            } else if (selectedEntity.getShape() instanceof Polygon) {
                verts = selectedEntity.getRepeatablePolygonSprite().getTransformedVertices();
            } else {
                return;
            }*/

            // Draw Camera
            CameraData cameraData = getWorld().getMapData().camera;
            float width = cameraData.cmPerDisplayWidth;
            float height = width*1.5f;
            app.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            app.shapeRenderer.setColor(Color.BLACK);
            app.shapeRenderer.rect(cameraData.position.x-width/2f, cameraData.position.y-height/2f, width, height);



            app.shapeRenderer.end();

    }

    private void drawVerts (float[] verts, Vector2 origin, float rotation, Color color) {
        float oX = origin.x;
        float oY = origin.y;
        app.shapeRenderer.setColor(color);
        for(int i=0;i<verts.length;i+=2) {
            float x = verts[i];
            float y = verts[i+1];
            float x2 = 0;
            float y2 = 0;
            if(i+3 < verts.length) {
                x2 = verts[i+2];
                y2 = verts[i+3];
            } else {
                x2 = verts[0];
                y2 = verts[1];
            }
            if (rotation != 0) {
                float tempX = x;
                float tempY = y;
                float rotationInRads = rotation * MathUtils.degreesToRadians;
                x = tempX * (float) Math.cos(rotationInRads) - tempY * (float) Math.sin(rotationInRads);
                y = tempY * (float) Math.cos(rotationInRads) + tempX * (float) Math.sin(rotationInRads);

                float tempX2 = x2;
                float tempY2 = y2;
                x2 = tempX2 * (float) Math.cos(rotationInRads) - tempY2 * (float) Math.sin(rotationInRads);
                y2 = tempY2 * (float) Math.cos(rotationInRads) + tempX2 * (float) Math.sin(rotationInRads);
            }
            app.shapeRenderer.circle(x+oX,y+oY,vertRadius);
            if(verts.length > 2)
                app.shapeRenderer.line(x+oX,y+oY,x2+oX,y2+oY);
        }
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, false);
        getCamera().position.set(getCamera().position.x, getCamera().position.y, getCamera().position.z);
    }

    @Override
    public void dispose() {
        clear();
    }

    public World getWorld() {
        return world;
    }

    public Entity getSelectedEntity() {
        return this.hudStage.getSelectedEntity();
    }

    public void setSelectedEntity(Entity entity) {
        this.hudStage.setSelectedEntity(entity);
    }
}
