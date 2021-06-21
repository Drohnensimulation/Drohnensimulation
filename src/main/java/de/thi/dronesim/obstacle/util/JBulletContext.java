package de.thi.dronesim.obstacle.util;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import de.thi.dronesim.helpers.VecMathHelper;
import de.thi.dronesim.obstacle.entity.Obstacle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Christian Schmied
 */
public class JBulletContext {
    private static final Vector3f GRAVITY = null;//new Vector3f(0, -9.81f, 0);
    private final Logger logger;
    private final DynamicsWorld dynamicsWorld;

    private final ReadWriteLock rwLock;

    public JBulletContext() {
        logger = LogManager.getLogger(JBulletContext.class);
        rwLock = new ReentrantReadWriteLock();
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
        this.dynamicsWorld = new DiscreteDynamicsWorld(collisionDispatcher, broadphase, constraintSolver, collisionConfiguration);

        if (GRAVITY != null)
            this.dynamicsWorld.setGravity(GRAVITY);
    }

    /**
     * Use this Method to add new Hitboxes to the Physics world
     *
     * @param position
     * @param rotation
     * @param dimension
     * @param obstacle
     * @return The gloryfied HitBox Object to persist in the List as Replica, actually never needed outside of Physics
     */
    public HitBoxRigidBody addHitBox(Vector3f position, Vector3f rotation, Vector3f dimension, Obstacle obstacle) {
        rwLock.writeLock().lock();
        try {
            CollisionShape boxShape = new BoxShape(dimension);
            MotionState boxMotionState = new DefaultMotionState(
                    new Transform(new Matrix4f(
                            new Quat4f(rotation.x, rotation.y, rotation.z, 1),
                            position,
                            1
                    )));

            RigidBodyConstructionInfo boxConstructionInfo = new RigidBodyConstructionInfo(0, boxMotionState, boxShape);
            HitBoxRigidBody boxRigidBody = new HitBoxRigidBody(boxConstructionInfo, obstacle);

            boxRigidBody.setCollisionFlags(boxRigidBody.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
            boxRigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

            dynamicsWorld.addRigidBody(boxRigidBody);
            return boxRigidBody;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public RigidBody createSphere(Vector3f position, float radius) {
        CollisionShape sphereShape = new SphereShape(radius);
        MotionState sphereMotionState = new DefaultMotionState(
                new Transform(new Matrix4f(
                        new Quat4f(1, 1, 1, 1),
                        position,
                        1
                )));
        RigidBodyConstructionInfo sphereConstructionInfo = new RigidBodyConstructionInfo(0, sphereMotionState, sphereShape);
        return new RigidBody(sphereConstructionInfo);
    }

    public Future<Boolean> checkCollision(final RigidBody body) {
        rwLock.writeLock().lock();
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        body.setCollisionFlags(body.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        dynamicsWorld.addCollisionObject(body);

        dynamicsWorld.setInternalTickCallback(new InternalTickCallback() {
            @Override
            public void internalTick(DynamicsWorld dynamicsWorld, float delta) {
                try {
                    int numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();
                    for (int i = 0; i < numManifolds; i++) {
                        PersistentManifold manifold = dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);
                        if (manifold.getBody0().equals(body) || manifold.getBody1().equals(body)) {
                            boolean contact = false;
                            for (int j = 0; j < manifold.getNumContacts(); j++) {
                                ManifoldPoint contactPoint = manifold.getContactPoint(j);
                                if (contactPoint.getDistance() < 0.0f) {
                                    contact = true;
                                    break;
                                }
                            }
                            if (contact) {
                                future.complete(true);
                                break;
                            }
                        }
                    }
                    if (!future.isDone()) {
                        future.complete(false);
                    }
                } finally {
                    dynamicsWorld.removeCollisionObject(body);
                    rwLock.writeLock().unlock();
                }
            }
        }, null);
        dynamicsWorld.performDiscreteCollisionDetection();
        dynamicsWorld.stepSimulation(1);

        return future;
    }

    public void removeHitBox(RigidBody hitBox) {
        rwLock.writeLock().lock();
        try {
            dynamicsWorld.removeRigidBody(hitBox);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public JBulletHitMark rayTest(com.jme3.math.Vector3f from, com.jme3.math.Vector3f direction, float range) {
        rwLock.readLock().lock();
        try {
            //Parameters
            Vector3f from_clone = VecMathHelper.of(from);
            Vector3f direction_clone = VecMathHelper.of(direction);

            //Calculate EndPoint
            direction_clone.normalize();
            direction_clone.scale(range);
            Vector3f to = new Vector3f(from_clone);
            to.add(direction_clone);

            CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(from_clone, to);

            this.dynamicsWorld.rayTest(from_clone, to, rayCallback);
            if (rayCallback.collisionObject instanceof RigidBody) {
                return new JBulletHitMark(rayCallback, from_clone);
            } else {
                return null;
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
