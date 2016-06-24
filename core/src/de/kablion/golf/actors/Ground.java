package de.kablion.golf.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.kablion.golf.utils.RepeatablePolygonSprite;


public class Ground extends ShapeActor {

    private final static float TEXTURE_WIDTH = 50;
    private final static float TEXTURE_HEIGHT = 50;

    public Ground(float x, float y, float rotation, Array<Vector2> polygonPoints, float textureOffsetX, float textureOffsetY, AssetManager assets) {
        super();
        setOrigin(x, y);
        setRotation(rotation);
        setColor(Color.GREEN);
        setTextureRegion(assets.get("spritesheets/textures.atlas", TextureAtlas.class).findRegion("ground_texture"),
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                RepeatablePolygonSprite.WrapType.REPEAT,
                RepeatablePolygonSprite.WrapType.REPEAT);
        setShape(findShape(polygonPoints));
        setTextureOffset(textureOffsetX, textureOffsetY);

    }

}
