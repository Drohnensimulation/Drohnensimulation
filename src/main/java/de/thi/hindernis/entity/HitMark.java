package de.thi.hindernis.entity;

import javax.vecmath.Vector3f;

public class HitMark {
    private final float distance;
    private final Vector3f worldPosition;
    private final Vector3f relativePosition;
    private final Obstacle obstacle;

    public HitMark(Vector3f origin, Vector3f hit, Obstacle obstacle) {
        this.obstacle = obstacle;

        this.worldPosition = new Vector3f(hit);

        this.relativePosition = new Vector3f(hit);
        this.relativePosition.sub(origin);

        this.distance = this.relativePosition.length();
    }

    public float getDistance() {
        return distance;
    }

    public Vector3f getWorldPosition() {
        return worldPosition;
    }

    public Vector3f getRelativePosition() {
        return relativePosition;
    }

    public Obstacle getHindernis() {
        return obstacle;
    }
}
