package de.thi.dronesim.obstacle.util;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import de.thi.dronesim.helpers.VecMathHelper;
import de.thi.dronesim.obstacle.entity.Obstacle;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * @author Christian Schmied
 */
public class JBulletContext {
    private static final Vector3f GRAVITY = null;//new Vector3f(0, -9.81f, 0);
    private final DynamicsWorld dynamicsWorld;

    public JBulletContext(){
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
        this.dynamicsWorld = new DiscreteDynamicsWorld(collisionDispatcher, broadphase, constraintSolver, collisionConfiguration);

        if(GRAVITY != null)
            this.dynamicsWorld.setGravity(GRAVITY);
    }

    /**
     * Use this Method to add new Hitboxes to the Physics world
     * @param postition
     * @param rotation
     * @param dimension
     * @param obstacle
     * @return The gloryfied HitBox Object to persist in the List as Replica, actually never needed outside of Physics
     */
    public HitBoxRigidBody addHitBox(Vector3f postition, Vector3f rotation, Vector3f dimension, Obstacle obstacle){
        CollisionShape boxShape = new BoxShape(dimension);
        MotionState boxMotionState = new DefaultMotionState(
                new Transform(new Matrix4f(
                        new Quat4f(rotation.x, rotation.y, rotation.z, 1),
                        postition,
                        1
                )));

        RigidBodyConstructionInfo boxConstructionInfo = new RigidBodyConstructionInfo(0, boxMotionState, boxShape);
        HitBoxRigidBody boxRigidBody = new HitBoxRigidBody(boxConstructionInfo, obstacle);

        dynamicsWorld.addRigidBody(boxRigidBody);
        return boxRigidBody;
    }

    public void removeHitBox(RigidBody hitBox){
        dynamicsWorld.removeRigidBody(hitBox);
    }

    public JBulletHitMark rayTest(com.jme3.math.Vector3f from, com.jme3.math.Vector3f direction, float range){
        //Parameters
        float maxDistance = 10;
        Vector3f from_clone = VecMathHelper.of(from);
        Vector3f direction_clone = VecMathHelper.of(direction);

        //Calculate EndPoint
        direction_clone.normalize();
        direction_clone.scale(maxDistance);
        Vector3f to = new Vector3f(from_clone);
        to.add(direction_clone);

        CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(from_clone, to);
        this.dynamicsWorld.rayTest(from_clone, to, rayCallback);
        if (rayCallback.collisionObject instanceof RigidBody) {
            return new JBulletHitMark(rayCallback, from_clone);
        } else {
            return null;
        }
    }
}
