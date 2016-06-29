package de.kablion.golf.data;


import com.badlogic.gdx.math.Vector2;

public class CollisionData {
    public Vector2 normalFirstToSecond = new Vector2();
    public float overlapDistance = 0;
    public boolean isFirstInSecond = false;
    public boolean isSecondInFirst = false;
}
