package de.kablion.golf.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.utils.RepeatablePolygonSprite;


public class Wall extends ShapeActor {

    private final static float TEXTURE_WIDTH = 20;

    public Wall(float x, float y, float length, float width, float rotation, AssetManager assets) {
        super();
        setPosition(x, y);
        setRotation(rotation);

        float left = -length/2;
        float right = length/2;
        float top = +width / 2;
        float bottom = -width / 2;
        Array<Vector2> polygonPoints = new Array<Vector2>(2);
        polygonPoints.add(new Vector2(left,bottom));
        polygonPoints.add(new Vector2(right,top));
        setColor(Color.BROWN);
        setTextureRegion(assets.get("spritesheets/textures.atlas", TextureAtlas.class).findRegion("wall_texture"),
                TEXTURE_WIDTH,
                0,
                RepeatablePolygonSprite.WrapType.REPEAT,
                RepeatablePolygonSprite.WrapType.STRETCH);
        setShape(findShape(polygonPoints));
    }
}
