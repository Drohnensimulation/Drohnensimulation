package de.thi.dronesim.gui.drenderer;

import com.jme3.math.Vector3f;

/**
 * Renderable sphere
 *
 * @author Michael Weichenrieder
 */
public class RenderableSphere extends RenderableObject {

    private static final String MODEL = "Objects/Obstacles/red_sphere.obj";

    /**
     * Instantiates a new renderable sphere
     *
     * @param center Coordinates of the object center
     * @param scale Scale factors along the object-axes
     * @param rotation Rotations around the object-axes (arc measure)
     */
    public RenderableSphere(Vector3f center, Vector3f scale, Vector3f rotation) {
        super(center, scale, rotation, MODEL);
    }

    /**
     * Instantiates a new renderable sphere
     *
     * @param center Coordinates of the object center
     * @param scale Scale factors along the object-axes
     */
    public RenderableSphere(Vector3f center, Vector3f scale) {
        super(center, scale, MODEL);
    }

    /**
     * Instantiates a new renderable sphere
     *
     * @param center Coordinates of the object center
     */
    public RenderableSphere(Vector3f center) {
        super(center, MODEL);
    }
}
