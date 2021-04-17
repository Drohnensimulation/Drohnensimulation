package de.thi.dronesim.obstacle.util;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.RigidBody;

import javax.vecmath.Vector3f;

public class JBulletHitMark {
    public final RigidBody body;
    public final Vector3f worldHit;
    public final Vector3f relativeHit;
    public final float distance;

    public JBulletHitMark(CollisionWorld.ClosestRayResultCallback rayCallback, Vector3f origin){

        worldHit = new Vector3f(rayCallback.hitPointWorld);
        relativeHit = new Vector3f(rayCallback.hitPointWorld);
        relativeHit.sub(origin);
        distance = relativeHit.length();

        body = (RigidBody) rayCallback.collisionObject;
    }
}
