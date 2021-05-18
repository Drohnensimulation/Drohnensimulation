package de.thi.dronesim.obstacle.entity;

import de.thi.dronesim.helpers.Jme3MathHelper;
import de.thi.dronesim.obstacle.util.JBulletHitMark;

import com.jme3.math.Vector3f;

import java.util.Objects;

/**
 * @author Christian Schmied
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HitMark hitMark = (HitMark) o;
        return Float.compare(hitMark.distance, distance) == 0 && worldHit.equals(hitMark.worldHit) && relativeHit.equals(hitMark.relativeHit) && obstacle.equals(hitMark.obstacle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, worldHit, relativeHit, obstacle);
    }
}
