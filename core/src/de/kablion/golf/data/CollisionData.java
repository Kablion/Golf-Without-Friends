package de.kablion.golf.data;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CollisionData {
    public Vector2 normalVector;
    public float overlapDistance;

    public CollisionData() {
        normalVector = new Vector2();
        overlapDistance = 0;
    }
}
