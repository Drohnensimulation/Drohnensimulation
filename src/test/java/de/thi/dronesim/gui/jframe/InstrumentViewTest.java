package de.thi.dronesim.gui.jframe;

import com.formdev.flatlaf.FlatDarkLaf;
import com.jme3.math.Vector3f;
import de.thi.dronesim.gui.dview.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for InstrumentView
 *
 * @author Michael Weichenrieder
 */
public class InstrumentViewTest {

    private static final int width = 1280, height = 720;

    /**
     * Init point
     *
     * @param args Irrelevant parameters
     */
    public static void main(String[] args) {
        // Dark gui theme for swing
        FlatDarkLaf.install();

        // Create DView and add objects
        DView dView = new DView(width, height);
        List<RenderableObject> mapObjects = getMapObjects();
        dView.addRenderableObjects(mapObjects);

        // Add drone
        RenderableDrone drone = new RenderableDrone(new Vector3f(0, 1, 0));
        drone.setTilt(new Vector3f((float) Math.PI * .05f, 0, (float) Math.PI * .05f));
        drone.setRotation(new Vector3f(0, (float) Math.PI * 1.5f, 0));
        drone.moveRelativeToWorld(new Vector3f(0, .2f, 0));
        drone.setRotateRotors(true);
        Runnable droneUpdater = () -> {
            drone.addRotation(new Vector3f(0, -.0005f, 0));
            drone.moveRelativeToDrone(new Vector3f(0, 0, .0006f));
        };
        dView.addFrameUpdateListener(droneUpdater);
        dView.addRenderableObject(drone);

        // Open swing frame
        new Thread(() -> new InstrumentView(dView)).start();
    }

    /**
     * Creates example map
     *
     * @return List of renderable objects
     */
    public static List<RenderableObject> getMapObjects() {
        List<RenderableObject> objects = new ArrayList<>();
        objects.add(new RenderableCuboid(new Vector3f(0, .5f, 0)));
        objects.add(new RenderableCuboid(new Vector3f(1, .5f, 0)));
        objects.add(new RenderableCuboid(new Vector3f(0, .5f, -1)));
        objects.add(new RenderableCuboid(new Vector3f(0, 1.5f, -1)));
        objects.add(new RenderableCuboid(new Vector3f(1, .5f, 0)));
        objects.add(new RenderableSphere(new Vector3f(0, .5f, 2)));
        objects.add(new RenderableSphere(new Vector3f(0, 1.5f, 2)));
        objects.add(new RenderableSphere(new Vector3f(-1, .5f, -2)));
        objects.add(new RenderableMarker(new Vector3f(0, 1, 0)));
        return objects;
    }
}
