package de.thi.dronesim.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.jme3.math.Vector3f;
import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.gui.drenderer.*;
import de.thi.dronesim.gui.dview.DView;
import de.thi.dronesim.gui.mview.MView;
import de.thi.dronesim.obstacle.UfoObjs;
import de.thi.dronesim.obstacle.entity.Obstacle;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for managing gui types (simple and dview)
 *
 * @author Michael Weichenrieder
 */
public class GuiManager implements ISimulationChild {

    private Simulation simulation;
    private IGuiView instrumentView;
    private boolean isDView;

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    public GuiManager() {
        // Dark mode
        FlatDarkLaf.install();
    }

    /**
     * Opens a {@link de.thi.dronesim.gui.mview.MView} gui
     */
    public void openMViewGui() {
        if (!existsGui()) {
            instrumentView = new MView(this);
            isDView = false;
        }
    }

    /**
     * Opens a {@link DRenderer} gui
     */
    public void openDViewGui() {
        if (!existsGui()) {
            DRenderer dRenderer = initDRenderer();
            instrumentView = new DView(this, dRenderer);
            isDView = true;
        }
    }

    /**
     * @return True if any gui is opened
     */
    public boolean existsGui() {
        return instrumentView != null;
    }

    /**
     * @return True if the opened gui is a {@link DRenderer}
     */
    public boolean isDView() {
        return isDView;
    }

    private DRenderer initDRenderer() {
        // Create DView
        DRenderer dView = new DRenderer();

        // Add map objects
        List<RenderableObject> mapObjects = new ArrayList<>();
        for (Obstacle obstacle : simulation.getChild(UfoObjs.class).getObstacles()) {
            Vector3f center = new Vector3f(obstacle.getPosition()[0], obstacle.getPosition()[1], obstacle.getPosition()[2]);
            Vector3f scale = new Vector3f(obstacle.getScale()[0], obstacle.getScale()[1], obstacle.getScale()[2]);
            Vector3f rotation = new Vector3f(obstacle.getRotation()[0], obstacle.getRotation()[1], obstacle.getRotation()[2]);
            switch(obstacle.getModelName()) {
                case "red_cube":
                    mapObjects.add(new RenderableCuboid(center, scale, rotation));
                    break;
                case "red_sphere":
                    mapObjects.add(new RenderableSphere(center, scale, rotation));
                    break;
                case "marker":
                    mapObjects.add(new RenderableMarker(center));
                    break;
                default:
                    // TODO: Read path and load from local object file
            }
        }
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

        // Return DView
        return dView;
    }
}
