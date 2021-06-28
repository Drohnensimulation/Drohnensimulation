package de.thi.dronesim.gui.drenderer;

import com.jme3.math.Vector3f;

/**
 * Renderable beacon
 *
 * @author Michael Weichenrieder
 */
public class RenderableMarker extends RenderableObject {

    private static final String MODEL = "Objects/Markers/marker.obj";

    /**
     * Instantiates a new renderable cuboid
     *
     * @param center Coordinates of the object center
     */
    public RenderableMarker(Vector3f center) {
        super(center, MODEL, false);
    }
}
