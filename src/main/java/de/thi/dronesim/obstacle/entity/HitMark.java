package de.thi.dronesim.obstacle.entity;

import de.thi.dronesim.helpers.Jme3MathHelper;
import de.thi.dronesim.obstacle.util.JBulletHitMark;

import com.jme3.math.Vector3f;

public class HitMark {
    private final float distance;
    private final Vector3f worldHit;
    private final Vector3f relativeHit;
    private final Obstacle obstacle;

    public HitMark(JBulletHitMark hitBody) {
        this.distance = hitBody.distance;
        this.worldHit = Jme3MathHelper.of(hitBody.worldHit);
        this.relativeHit = Jme3MathHelper.of(hitBody.relativeHit);
        this.obstacle = hitBody.body.getObstacle();
    }

    public HitMark(float distance, Vector3f worldHit, Vector3f relativeHit, Obstacle obstacle){
        this.distance = distance;
        this.worldHit = worldHit;
        this.relativeHit = relativeHit;
        this.obstacle = obstacle;
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
