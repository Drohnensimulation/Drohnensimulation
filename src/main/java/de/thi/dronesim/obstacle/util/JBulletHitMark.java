package de.thi.dronesim.obstacle.util;

import com.bulletphysics.collision.dispatch.CollisionWorld;

import javax.vecmath.Vector3f;

/**
 * @author Christian Schmied
 */
public class JBulletHitMark {
    public final HitBoxRigidBody body;
    public final Vector3f worldHit;
    public final Vector3f relativeHit;
    public final float distance;

    public JBulletHitMark(CollisionWorld.ClosestRayResultCallback rayCallback, Vector3f origin){

        worldHit = new Vector3f(rayCallback.hitPointWorld);
        relativeHit = new Vector3f(rayCallback.hitPointWorld);
        relativeHit.sub(origin);
        distance = relativeHit.length();

        body = (HitBoxRigidBody) rayCallback.collisionObject;
    }
}
