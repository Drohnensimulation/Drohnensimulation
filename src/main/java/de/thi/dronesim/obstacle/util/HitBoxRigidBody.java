package de.thi.dronesim.obstacle.util;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import de.thi.dronesim.obstacle.entity.Obstacle;

import javax.vecmath.Vector3f;

/**
 * @author Christian Schmied
 */
public class HitBoxRigidBody extends RigidBody {
    private final Obstacle obstacle;

    public HitBoxRigidBody(RigidBodyConstructionInfo constructionInfo, Obstacle obstacle) {
        super(constructionInfo);
        this.setUserPointer(obstacle.getID());
        this.obstacle = obstacle;
    }

    public HitBoxRigidBody(float mass, MotionState motionState, CollisionShape collisionShape, Obstacle obstacle) {
        super(mass, motionState, collisionShape);
        this.obstacle = obstacle;
    }

    public HitBoxRigidBody(float mass, MotionState motionState, CollisionShape collisionShape, Vector3f localInertia, Obstacle obstacle) {
        super(mass, motionState, collisionShape, localInertia);
        this.obstacle = obstacle;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }
}
