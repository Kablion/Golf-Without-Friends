package de.kablion.golf.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class Wall extends ShapeActor {

    private final static float TEXTURE_WIDTH = 20;

    public Wall(float x, float y, float length, float width, float rotation, AssetManager assets) {
        super();
        setOrigin(x, y);
        setRotation(rotation);

        float left = -length/2;
        float right = length/2;
        float top = +width;
        float bottom = -width;
        Array<Vector2> polygonPoints = new Array<Vector2>(2);
        polygonPoints.add(new Vector2(left,bottom));
        polygonPoints.add(new Vector2(right,top));
        setPosition(x,y);
        setColor(Color.BROWN);
        setTextureRegion(assets.get("spritesheets/textures.atlas", TextureAtlas.class).findRegion("wall_texture"), TEXTURE_WIDTH, 0, false, false);
        setShape(findShape(polygonPoints, x, y));
    }
}
