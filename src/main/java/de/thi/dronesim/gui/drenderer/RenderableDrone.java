package de.thi.dronesim.gui.drenderer;

import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Renderable drone
 *
 * @author Michael Weichenrieder
 */
public class RenderableDrone extends RenderableObject {

    // Drone objects
    private final Node droneTilt = new Node("Drone Tilt");
    private final Spatial[] rotorsCw = new Spatial[2];
    private final Spatial[] rotorsCcw = new Spatial[2];

    // Params
    private volatile Vector3f tilt = new Vector3f(0, 0, 0);
    private volatile Vector3f droneRelativePosition = new Vector3f(0, 0, 0);
    private volatile boolean rotateRotors = false;

    /**
     * Instantiates a new renderable drone
     *
     * @param center Coordinates of the object center
     * @param rotation Rotations around the object-axes (arc measure)
     * @param radius Radius of drone
     */
    public RenderableDrone(Vector3f center, Vector3f rotation, float radius) {
        super(center, new Vector3f(radius > .15 ? 2 * radius : .3f, radius > .15 ? 2 * radius : .3f, radius > .15 ? 2 * radius : .3f), rotation, null);
    }

    /**
     * Instantiates a new renderable drone
     *
     * @param center Coordinates of the object center
     * @param radius Radius of drone
     */
    public RenderableDrone(Vector3f center, float radius) {
        super(center, new Vector3f(radius > .15 ? 2 * radius : .3f, radius > .15 ? 2 * radius : .3f, radius > .15 ? 2 * radius : .3f), null);
    }

    /**
     * Instantiates a new renderable drone at 0/0/0
     *
     * @param radius Radius of drone
     */
    public RenderableDrone(float radius) {
        super(new Vector3f(0, 0, 0), new Vector3f(radius > .15 ? 2 * radius : .3f, radius > .15 ? 2 * radius : .3f, radius > .15 ? 2 * radius : .3f), null);
    }

    /**
     * Create the spatial object (called by rendering thread)
     *
     * @param assetManager Asset manager
     * @return Created object
     */
    @Override
    Spatial getObject(AssetManager assetManager) {
        if(object == null) {
            object = new Node("Drone");
            object.setName("object-" + id);
            ((Node) object).attachChild(droneTilt);

            // Add drone body
            Spatial droneBody = assetManager.loadModel("Objects/Drone/drone.obj");
            droneBody.setLocalTranslation(0, 0, 0);
            droneTilt.attachChild(droneBody);

            // Add rotors
            rotorsCw[0] = assetManager.loadModel("Objects/Drone/rotor_cw.obj");
            rotorsCw[0].setLocalTranslation(.23501f, .04893f, -.22202f);
            droneTilt.attachChild(rotorsCw[0]);
            rotorsCcw[0] = assetManager.loadModel("Objects/Drone/rotor_ccw.obj");
            rotorsCcw[0].setLocalTranslation(-.23501f, .04893f, -.22202f);
            droneTilt.attachChild(rotorsCcw[0]);
            rotorsCw[1] = assetManager.loadModel("Objects/Drone/rotor_cw.obj");
            rotorsCw[1].setLocalTranslation(-.23788f, .08369f, .22155f);
            droneTilt.attachChild(rotorsCw[1]);
            rotorsCcw[1] = assetManager.loadModel("Objects/Drone/rotor_ccw.obj");
            rotorsCcw[1].setLocalTranslation(.23788f, .08369f, .22155f);
            droneTilt.attachChild(rotorsCcw[1]);

            // Update parameters
            updateParameters();
        }
        return object;
    }

    /**
     * Set the drones position
     *
     * @param center New center position of the drone
     */
    public void setPosition(Vector3f center) {
        this.center = center;
    }

    /**
     * Move drone relative to the world
     *
     * @param movement Vector to be added to center position of the drone relative to the world
     */
    public void moveRelativeToWorld(Vector3f movement) {
        this.center = this.center.add(movement);
    }

    /**
     * Move drone relative to itself
     *
     * @param movement Vector to be added to center position of the drone relative to the drone
     */
    public void moveRelativeToDrone(Vector3f movement) {
        droneRelativePosition = droneRelativePosition.add(movement);
    }

    /**
     * Set the drones rotation
     *
     * @param rotation New rotations around the object-axes (arc measure)
     */
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    /**
     * Update the drones rotation
     *
     * @param rotation Rotation around the object-axes to be added (arc measure)
     */
    public void addRotation(Vector3f rotation) {
        this.rotation = this.rotation.add(rotation);
    }

    /**
     * Set the drones tilt
     *
     * @param tilt New tilt around the object-axes (arc measure)
     */
    public void setTilt(Vector3f tilt) {
        this.tilt = tilt;
    }

    /**
     * Update the drones tilt
     *
     * @param tilt Tilt around the object-axes to be added (arc measure)
     */
    public void addTilt(Vector3f tilt) {
        this.tilt = this.tilt.add(tilt);
    }

    /**
     * Set rotor rotation
     *
     * @param rotate True if rotors should rotate, else false
     */
    public void setRotateRotors(Boolean rotate) {
        rotateRotors = rotate;
    }

    /**
     * Updates the drones parameters for render (called by rendering thread)
     */
    void updateParameters() {
        droneTilt.setLocalRotation(new Quaternion().fromAngles(tilt.toArray(new float[3])));
        object.setLocalTranslation(center);
        object.setLocalRotation(new Quaternion().fromAngles(rotation.toArray(new float[3])));
        object.setLocalTranslation(object.localToWorld(droneRelativePosition, null));
        droneRelativePosition = new Vector3f(0, 0, 0);
        center = object.getLocalTranslation();
        if(rotateRotors) {
            for (int i = 0; i < 2; i++) {
                rotorsCw[i].rotate(0, -.5f, 0);
                rotorsCcw[i].rotate(0, .5f, 0);
            }
        }
        object.setLocalScale(scale);
    }

    /**
     * Adjust camera position and angle to match first person view
     *
     * @param cam Camera to adjust
     */
    void adjustCamToFirstPerson(Camera cam) {
        cam.setLocation(droneTilt.localToWorld(new Vector3f(0, .07f, .11f), null));
        cam.setRotation(droneTilt.getWorldRotation());
    }

    /**
     * Adjust camera position and angle to match third person view
     *
     * @param cam Camera to adjust
     */
    void adjustCamToThirdPerson(Camera cam) {
        cam.setLocation(object.getWorldTranslation().add(new Vector3f(2, 4, 5)));
        cam.lookAt(object.getWorldTranslation(), Vector3f.UNIT_Y);
    }

    /**
     * Adjust camera position and angle to match bird view
     *
     * @param cam Camera to adjust
     */
    void adjustCamToBirdView(Camera cam) {
        cam.setLocation(object.getWorldTranslation().add(new Vector3f(0, 5, 0)));
        cam.lookAt(object.getWorldTranslation(), Vector3f.UNIT_Y);
    }
}
