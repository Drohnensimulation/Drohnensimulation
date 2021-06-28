package de.thi.dronesim.gui.drenderer;

import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.MTLLoader;

import java.io.File;

/**
 * Renderable objects
 *
 * @author Michael Weichenrieder
 */
public class RenderableObject implements Comparable<RenderableObject> {

    private static int nextId = 0;

    private final boolean externalModel;

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
     * @param externalModel Set to true if the model is not included in the library
     */
    public RenderableObject(Vector3f center, Vector3f scale, Vector3f rotation, String model, boolean externalModel) {
        this.center = center;
        this.scale = scale;
        this.rotation = rotation;
        this.model = model;
        this.externalModel = externalModel;
    }

    /**
     * Instantiates a new renderable object
     *
     * @param center Coordinates of the object center
     * @param scale Scale factors along the object-axes
     * @param model Path to the object file
     * @param externalModel Set to true if the model is not included in the library
     */
    public RenderableObject(Vector3f center, Vector3f scale, String model, boolean externalModel) {
        this(center, scale, new Vector3f(0, 0, 0), model, externalModel);
    }

    /**
     * Instantiates a new renderable object
     *
     * @param center Coordinates of the object center
     * @param model Path to the object file
     * @param externalModel Set to true if the model is not included in the library
     */
    public RenderableObject(Vector3f center, String model, boolean externalModel) {
        this(center, new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), model, externalModel);
    }

    /**
     * Creates a Spatial object for rendering
     *
     * @param assetManager Asset manager
     * @return Spatial object
     */
    Spatial getObject(AssetManager assetManager) {
        if(object == null) {
            if(externalModel) {
                assetManager.registerLocator(getModelPath(), FileLocator.class);
                object = assetManager.loadModel(getModelName());
                assetManager.unregisterLocator(getModelPath(), FileLocator.class);
            } else {
                object = assetManager.loadModel(model);
            }
            object.setName("object-" + id);
            object.setLocalTranslation(center);
            object.setLocalScale(scale);
            object.setLocalRotation(new Quaternion().fromAngles(rotation.toArray(new float[3])));
        }
        return object;
    }

    /**
     * @return Path of the model-containing folder
     */
    String getModelPath() {
        if(model.contains(File.separator)) {
            return model.substring(0, model.indexOf(File.separator));
        }
        return File.separator.equals("/") ? "/" : "C:\\";
    }

    /**
     * @return Name of the model file
     */
    String getModelName() {
        if(model.contains(File.separator)) {
            return model.substring(model.indexOf(File.separator) + 1);
        }
        return model;
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
