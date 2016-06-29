package de.kablion.golf.utils;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import de.kablion.golf.data.CollisionData;

public class Collision {

    public static CollisionData checkCircleCircle(Circle circle1, Circle circle2) {
        CollisionData collisionData = null;
        float distance = Vector2.dst(circle1.x, circle1.y, circle2.x, circle2.y);
        float sumRadius = circle1.radius + circle2.radius;
        if (distance < sumRadius) {
            collisionData = new CollisionData();
            collisionData.overlapDistance = sumRadius - distance;
            if (distance <= circle1.radius - circle2.radius) {
                collisionData.isSecondInFirst = true;
            } else if (distance <= circle2.radius - circle1.radius) {
                collisionData.isFirstInSecond = true;
            } else {
                collisionData.normalFirstToSecond.set(circle2.x - circle1.x, circle2.y - circle1.y);
                collisionData.normalFirstToSecond.setLength(1);
            }
        }
        return collisionData;
    }

    public static CollisionData checkCirclePolygon(Circle circle, Polygon polygon) {
        CollisionData collisionData = checkPolygonCircle(polygon, circle);
        if (collisionData != null) {
            if (collisionData.isFirstInSecond) {
                collisionData.isSecondInFirst = true;
                collisionData.isFirstInSecond = false;
            } else if (collisionData.isSecondInFirst) {
                collisionData.isFirstInSecond = true;
                collisionData.isSecondInFirst = false;
            }
            if (collisionData.normalFirstToSecond != null) {
                collisionData.normalFirstToSecond.scl(-1);
            }
        }
        return collisionData;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static CollisionData checkPolygonCircle(Polygon polygon, Circle circle) {

        int iMinOlap = 0;
        float minOverLap = 1000000;

        float[] polygonVerts = polygon.getTransformedVertices();

        Vector2 edgeNormal = new Vector2();
        Vector2 edgeNormalMinOlap = new Vector2();

        // check clockwise or counter-clockwise
        float sum = 0;
        for (int i = 0; i < polygonVerts.length; i += 2) {
            if (i + 2 >= polygonVerts.length) {
                sum += (polygonVerts[0] - polygonVerts[i]) * (polygonVerts[1] + polygonVerts[i + 1]);
            } else {
                sum += (polygonVerts[i + 2] - polygonVerts[i]) * (polygonVerts[i + 3] + polygonVerts[i + 1]);
            }
        }
        boolean clockwise = (sum >= 0);

        for (int i = 0; i < polygonVerts.length; i += 2) {
            // Loop through every edge of the Polygon
            int i2 = i + 2;
            if (i + 3 >= polygonVerts.length) {
                i2 = 0;
            }

            // setting the normal of the Edge (projection Vector)
            edgeNormal.set(polygonVerts[i2] - polygonVerts[i], polygonVerts[i2 + 1] - polygonVerts[i + 1]);
            // set to left or right normal
            if (clockwise) edgeNormal.rotate90(1);
            else edgeNormal.rotate90(-1);
            edgeNormal.setLength(1);

            if ((Math.abs(edgeNormal.x - edgeNormalMinOlap.x) < 0.001 && Math.abs(edgeNormal.y - edgeNormalMinOlap.y) < 0.001)
                    || (Math.abs(-edgeNormal.x - edgeNormalMinOlap.x) < 0.001 && Math.abs(-edgeNormal.y - edgeNormalMinOlap.y) < 0.001)) {
                // on this edgeNormal Projection the minOlap is already detected
                float projectedOld = Vector2.dot(polygonVerts[iMinOlap], polygonVerts[iMinOlap + 1], edgeNormal.x, edgeNormal.y);
                float projectedNew = Vector2.dot(polygonVerts[i], polygonVerts[i + 1], edgeNormal.x, edgeNormal.y);
                float projectedCircleCenter = Vector2.dot(circle.x, circle.y, edgeNormal.x, edgeNormal.y);
                if (Math.abs(projectedOld - projectedCircleCenter) > Math.abs(projectedNew - projectedCircleCenter)) {
                    // Distance Edge to ball
                    // the new Edge is closer
                    iMinOlap = i;
                    edgeNormalMinOlap.set(edgeNormal.x, edgeNormal.y);
                    // no need to project again on this edgeNormal
                    continue;
                }
            }

            //setting min max for the polygon on the edgeNormal
            float projectedCircleCenter = Vector2.dot(circle.x, circle.y, edgeNormal.x, edgeNormal.y);
            float circleMin = projectedCircleCenter - circle.radius;
            float circleMax = projectedCircleCenter + circle.radius;
            float polygonMin = Vector2.dot(polygonVerts[0], polygonVerts[1], edgeNormal.x, edgeNormal.y);
            float polygonMax = Vector2.dot(polygonVerts[0], polygonVerts[1], edgeNormal.x, edgeNormal.y);


            for (int j = 2; j < polygonVerts.length; j += 2) {
                // Loop to get Min and Max Vertices
                float currentProjection = Vector2.dot(polygonVerts[j], polygonVerts[j + 1], edgeNormal.x, edgeNormal.y);

                if (currentProjection > polygonMax) {
                    polygonMax = currentProjection;
                }

                if (currentProjection < polygonMin) {
                    polygonMin = currentProjection;
                }
            }

            boolean isSeparated = polygonMax < circleMin | circleMax < polygonMin;
            if (isSeparated) {
                // the only code possible if no collision is detected
                return null;


            } else {
                // get minOverLap
                float overlapDistance;
                if (polygonMax - circleMin < circleMax - polygonMin) {
                    overlapDistance = polygonMax - circleMin;
                } else {
                    overlapDistance = circleMax - polygonMin;
                }

                if (overlapDistance < minOverLap) {
                    minOverLap = overlapDistance;
                    iMinOlap = i;
                    edgeNormalMinOlap.set(edgeNormal.x, edgeNormal.y);
                }
            }
        }

        // A collision is detected (no separation was found)
        CollisionData collisionData = new CollisionData();

        // check if circle(first) is fully in polygon(second)
        if (minOverLap >= circle.radius * 2) {
            collisionData.isFirstInSecond = true;
            collisionData.normalFirstToSecond = null;
            return collisionData;
        }

        // check if polygon(second) is fully in circle(first)
        boolean isInCircle = true;
        for (int i = 0; i < polygonVerts.length; i += 2) {
            Vector2 inCircle = new Vector2(polygonVerts[i] - circle.x, polygonVerts[i + 1] - circle.y);
            if (inCircle.len() > circle.radius) {
                isInCircle = false;
            }
        }
        if (isInCircle) {
            collisionData.isSecondInFirst = true;
            collisionData.normalFirstToSecond = null;
            return collisionData;
        }


        collisionData.overlapDistance = minOverLap;
        int iMinOlap2 = iMinOlap + 2;
        if (iMinOlap + 3 >= polygonVerts.length) {
            iMinOlap2 = 0;
        }

        Vector2 projectOn = new Vector2(polygonVerts[iMinOlap2] - polygonVerts[iMinOlap], polygonVerts[iMinOlap2 + 1] - polygonVerts[iMinOlap + 1]);
        projectOn.setLength(1);
        float projectedMin = Vector2.dot(polygonVerts[iMinOlap], polygonVerts[iMinOlap + 1], projectOn.x, projectOn.y);
        float projectedMax = Vector2.dot(polygonVerts[iMinOlap2], polygonVerts[iMinOlap2 + 1], projectOn.x, projectOn.y);
        float projectedCircle = Vector2.dot(circle.x, circle.y, projectOn.x, projectOn.y);
        if (projectedCircle < projectedMin) {
            // Corner of iMinOlap
            collisionData.normalFirstToSecond.set(circle.x - polygonVerts[iMinOlap], circle.y - polygonVerts[iMinOlap + 1]);

        } else if (projectedCircle > projectedMax) {
            // Corner of iMinOlap2
            collisionData.normalFirstToSecond.set(circle.x - polygonVerts[iMinOlap2], circle.y - polygonVerts[iMinOlap2 + 1]);

        } else {
            // Edge
            collisionData.normalFirstToSecond.set(polygonVerts[iMinOlap2] - polygonVerts[iMinOlap], polygonVerts[iMinOlap2 + 1] - polygonVerts[iMinOlap + 1]);
            // set to left or right normal
            if (clockwise) collisionData.normalFirstToSecond.rotate90(1);
            else collisionData.normalFirstToSecond.rotate90(-1);
        }
        collisionData.normalFirstToSecond.setLength(1);

        return collisionData;
    }

    public static CollisionData checkPolygonPolygon(Polygon polygon1, Polygon polygon2) {
        return null;
    }

}
