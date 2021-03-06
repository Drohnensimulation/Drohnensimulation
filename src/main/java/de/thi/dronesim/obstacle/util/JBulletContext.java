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

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Christian Schmied
 */
public class JBulletContext {
    private static final Vector3f GRAVITY = null;//new Vector3f(0, -9.81f, 0);
    private final DynamicsWorld dynamicsWorld;

    private final ReadWriteLock rwLock;

    public JBulletContext() {
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
     * @param halfDimension
     * @param obstacle
     * @return The gloryfied HitBox Object to persist in the List as Replica, actually never needed outside of Physics
     */
    public HitBoxRigidBody addHitBox(Vector3f position, Vector3f rotation, Vector3f halfDimension, Obstacle obstacle) {
        rwLock.writeLock().lock();
        try {
            CollisionShape boxShape = new BoxShape(halfDimension);
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

    /**
     * @param origin
     * @param radius
     * @return a new Sphere as RigidBody
     * @author Christian Schmied
     */
    public RigidBody createSphere(Vector3f origin, float radius) {
        CollisionShape sphereShape = new SphereShape(radius);
        MotionState sphereMotionState = new DefaultMotionState(
                new Transform(new Matrix4f(
                        new Quat4f(1, 1, 1, 1),
                        origin,
                        1
                )));
        RigidBodyConstructionInfo sphereConstructionInfo = new RigidBodyConstructionInfo(0, sphereMotionState, sphereShape);
        RigidBody body = new RigidBody(sphereConstructionInfo);
        body.setUserPointer(UUID.randomUUID().toString());
        return body;
    }

    /**
     * @param body The RigidBody used as CollisionBody
     * @return Feature resolving to the collision state
     * @author Christian Schmied
     */
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

    /**
     * @author Christian Schmied
     * @param body
     * @param signum
     * @return
     */
    public Future<Boolean> checkCollisionHalf(RigidBody body, int signum) {
        rwLock.writeLock().lock();
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        body.setCollisionFlags(body.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);


        Transform sphereTransform = new Transform();
        body.getWorldTransform(sphereTransform);

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
                                    Vector3f theSpherePoint = manifold.getBody0().equals(body) ? contactPoint.positionWorldOnA : contactPoint.positionWorldOnB;
                                    //Vector3f theOtherPoint = manifold.getBody0().equals(body) ? contactPoint.positionWorldOnB : contactPoint.positionWorldOnA;
                                    float signedDistance = (theSpherePoint.y - sphereTransform.origin.y) * signum;
                                    contact = signedDistance >= 0; // Whenn distance is equal to 0 the collision was horizontally...
                /*
                                    System.out.printf("So%s\n", sphereTransform.origin);
                                    System.out.printf("S %s\n", theSpherePoint);
                                    System.out.printf("Distance=%f\n", contactPoint.getDistance());
                                    System.out.printf("SignedDifference=%f\n", signedDistance);
                                    System.out.printf("Contact=%b\n", contact);
                 */
                                    if (contact) {
                                        break;
                                    }
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
