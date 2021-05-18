package de.thi.dronesim.gui.drenderer;

import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Renderable objects
 *
 * @author Michael Weichenrieder
 */
public class RenderableObject implements Comparable<RenderableObject> {

    private static int nextId = 0;
    protected final int id = nextId++;

    protected Spatial object;

    protected volatile Vector3f center, scale, rotation;
    protected String model;

    /**
     * Instantiates a new renderable object
     *
     * @param center Coordinates of the object center
     * @param scale Scale factors along the object-axes
     * @param rotation Rotations around the object-axes (arc measure)
     * @param model Path to the object file
     */
    public RenderableObject(Vector3f center, Vector3f scale, Vector3f rotation, String model) {
        this.center = center;
        this.scale = scale;
        this.rotation = rotation;
        this.model = model;
    }

    /**
     * Instantiates a new renderable object
     *
     * @param center Coordinates of the object center
     * @param scale Scale factors along the object-axes
     * @param model Path to the object file
     */
    public RenderableObject(Vector3f center, Vector3f scale, String model) {
        this(center, scale, new Vector3f(0, 0, 0), model);
    }

    /**
     * Instantiates a new renderable object
     *
     * @param center Coordinates of the object center
     * @param model Path to the object file
     */
    public RenderableObject(Vector3f center, String model) {
        this(center, new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), model);
    }

    /**
     * Creates a Spatial object for rendering
     *
     * @param assetManager Asset manager
     * @return Spatial object
     */
    Spatial getObject(AssetManager assetManager) {
        if(object == null) {
            object = assetManager.loadModel(model);
            object.setName("object-" + id);
            object.setLocalTranslation(center);
            object.setLocalScale(scale);
            object.setLocalRotation(new Quaternion().fromAngles(rotation.toArray(new float[3])));
        }
        return object;
    }

    /**
     * @return Maximum x-position overlapped by object
     */
    float getXMin() {
        return center.getX() - scale.getX() / 2;
    }

    /**
     * @return Minimum x-position overlapped by object
     */
    float getXMax() {
        return center.getX() + scale.getX() / 2;
    }

    /**
     * @return Minimum z-position overlapped by object
     */
    float getZMin() {
        return center.getZ() - scale.getZ() / 2;
    }

    /**
     * @return Maximum z-position overlapped by object
     */
    float getZMax() {
        return center.getZ() + scale.getZ() / 2;
    }

    /**
     * @return Unique id of the object
     */
    public int getId() {
        return id;
    }

    @Override
    public int compareTo(RenderableObject o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RenderableObject) {
            return compareTo((RenderableObject) obj) == 0;
        }
        return super.equals(obj);
    }
}
