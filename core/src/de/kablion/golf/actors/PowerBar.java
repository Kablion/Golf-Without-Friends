package de.kablion.golf.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import de.kablion.golf.Application;
import de.kablion.golf.utils.RepeatablePolygonSprite;

public class PowerBar extends Slider {

    public static final float MIN = 0.01f;
    public static final float MAX = 1;
    public static final float STEP_SIZE = 1;
    public static final boolean VERTICAL = false;

    private final Application app;
    private RepeatablePolygonSprite background = new RepeatablePolygonSprite();
    private RepeatablePolygonSprite content = new RepeatablePolygonSprite();
    private Sprite container = new Sprite();
    private float[] outerVertices;

    public PowerBar(final Application app) {
        super(MIN, MAX, STEP_SIZE, VERTICAL, new SliderStyle());
        this.app = app;
        initOuterVertices();
        initSprites();
        getStyle().background = new SpriteDrawable(container);
    }

    @Override
    public boolean setValue(float value) {
        if(!super.setValue(value)) return false;
        calcContentVertices();
        return true;
    }

    private void calcContentVertices() {
                     Polygon outerPoly = new Polygon(outerVertices);
        float percentage = getPercent();
        Polygon rawContentPoly = new Polygon(new float[]{0,0, 0,getHeight(), getWidth()*percentage,getHeight(), getWidth()*percentage,0});
        Polygon intersection = new Polygon();
        Intersector.intersectPolygons(outerPoly,rawContentPoly,intersection);
        if(intersection.getVertices().length >= 6) {
            content.setVertices(intersection.getVertices());
        }
    }

    private void initOuterVertices() {
        outerVertices = calcOuterVertices(getWidth(),getHeight());
    }

    private static float[] calcOuterVertices(float width, float height) {
         return new float[]
                {0,0, 0,height, width,height, width,0};
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        getStyle().background = new SpriteDrawable(container);
        if(getWidth() != container.getWidth() || getHeight() != container.getHeight()) {
            outerVertices = calcOuterVertices(getWidth(),getHeight());
            background.setVertices(outerVertices);
            if(getValue() != getMinValue()) calcContentVertices();
            container.setSize(getWidth(),getHeight());
        }
        if(getX() != container.getX() || getY() != container.getY()) {
            background.setPosition(getX(),getY());
            content.setPosition(getX(),getY());
            container.setPosition(getX(),getY());
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!(batch instanceof PolygonSpriteBatch)) return;

        PolygonSpriteBatch polyBatch = (PolygonSpriteBatch) batch;
        background.draw(polyBatch);
        content.draw(polyBatch);
        container.draw(polyBatch);
    }

    private void initSprites() {
        TextureAtlas atlas = app.assets.get("skins/game_hud.atlas",TextureAtlas.class);
        background.setTextureRegion(atlas.findRegion("power_bar_background"), RepeatablePolygonSprite.WrapType.REPEAT, RepeatablePolygonSprite.WrapType.STRETCH);
        background.setTextureSize(getWidth(),0);
        background.setVertices(outerVertices);
        background.setPosition(getX(),getY());

        content.setTextureRegion(atlas.findRegion("power_bar_content"), RepeatablePolygonSprite.WrapType.REPEAT, RepeatablePolygonSprite.WrapType.STRETCH);
        content.setTextureSize(getWidth(),0);
        content.setPosition(getX(),getY());

        container = new Sprite(atlas.findRegion("power_bar_container"));
        container.setSize(getWidth(),getHeight());
        container.setPosition(getX(),getY());

    }

}
