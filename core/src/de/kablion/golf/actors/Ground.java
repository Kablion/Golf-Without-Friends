package de.kablion.golf.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;


public class Ground extends ShapeActor {

    private final static float TEXTURE_WIDTH = 50;
    private final static float TEXTURE_HEIGHT = 50;

    public Ground(float x, float y, float rotation, Array<Vector2> polygonPoints, AssetManager assets) {
        super();
        setOrigin(x, y);
        setRotation(rotation);
        setColor(Color.GREEN);
        setTexture(assets.get("sprites/ground_texture.png", Texture.class), TEXTURE_WIDTH, TEXTURE_HEIGHT);
        setShape(findShape(polygonPoints, x, y));

    }

}
