package de.thi.dronesim.gui.drenderer;

import com.jme3.math.Vector3f;

/**
 * Renderable cuboid
 *
 * @author Michael Weichenrieder
 */
public class RenderableCuboid extends RenderableObject {

    private static final String MODEL = "Objects/Obstacles/red_cube.obj";

    /**
     * Instantiates a new renderable cuboid
     *
     * @param center Coordinates of the object center
     * @param scale Scale factors along the object-axes
     * @param rotation Rotations around the object-axes (arc measure)
     */
    public RenderableCuboid(Vector3f center, Vector3f scale, Vector3f rotation) {
        super(center, scale, rotation, MODEL, false);
    }

    /**
     * Instantiates a new renderable cuboid
     *
     * @param center Coordinates of the object center
     * @param scale Scale factors along the object-axes
     */
    public RenderableCuboid(Vector3f center, Vector3f scale) {
        super(center, scale, MODEL, false);
    }

    /**
     * Instantiates a new renderable cuboid
     *
     * @param center Coordinates of the object center
     */
    public RenderableCuboid(Vector3f center) {
        super(center, MODEL, false);
    }
}
