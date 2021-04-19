package de.thi.dronesim.obstacle.entity;

import de.thi.dronesim.obstacle.util.JBulletHitMark;

import javax.vecmath.Vector3f;

public class HitMark {
    private final float distance;
    private final Vector3f worldHit;
    private final Vector3f relativeHit;
    private final Obstacle obstacle;

    public HitMark(JBulletHitMark hitBody) {
        this.distance = hitBody.distance;
        this.worldHit = hitBody.worldHit;
        this.relativeHit = hitBody.relativeHit;
        this.obstacle = hitBody.body.getObstacle();
    }

    public float getDistance() {
        return distance;
    }

    public Vector3f worldHit() {
        return worldHit;
    }

    public Vector3f relativeHit() {
        return relativeHit;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }
}
