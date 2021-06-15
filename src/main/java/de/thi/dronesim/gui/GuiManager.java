package de.thi.dronesim.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.jme3.math.Vector3f;
import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.SimulationUpdateListener;
import de.thi.dronesim.drone.Location;
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
public class GuiManager implements ISimulationChild, SimulationUpdateListener {

    private Simulation simulation;
    private AGuiFrame instrumentView;
    private DRenderer dRenderer;

    @Override
    public void initialize(Simulation simulation) {
        this.simulation = simulation;
        simulation.registerUpdateListener(this, 0);
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public void onUpdate(SimulationUpdateEvent event) {
        if (instrumentView != null) {
            instrumentView.updateDroneStatus(event);
        }
        if (dRenderer != null) {
            RenderableDrone drone = dRenderer.getDrone();
            updateDroneLocation(drone, event.getDrone().getLocation());
        }
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
            instrumentView.init(simulation);
        }
    }

    /**
     * Opens a {@link DRenderer} gui
     */
    public void openDViewGui() {
        if (!existsGui()) {
            dRenderer = initDRenderer();
            instrumentView = new DView(this, dRenderer);
            instrumentView.init(simulation);
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
        return dRenderer != null;
    }

    private void updateDroneLocation(RenderableDrone drone, Location location) {
        if (drone != null) {
            drone.setPosition(location.getPosition());
            float rotation = (float) (location.getHeading() * (Math.PI / 180.0));
            drone.setRotation(new Vector3f(0, -rotation, 0));
            drone.setRotateRotors(location.getAirspeed() != 0 || location.getVerticalSpeed() != 0);
        }
    }

    private DRenderer initDRenderer() {
        // Create DRenderer
        DRenderer dRenderer = new DRenderer();

        // Add map objects
        List<RenderableObject> mapObjects = new ArrayList<>();
        for (Obstacle obstacle : simulation.getChild(UfoObjs.class).getObstacles()) {
            Vector3f center = new Vector3f(obstacle.getPosition()[0], obstacle.getPosition()[1], obstacle.getPosition()[2]);
            Vector3f scale = new Vector3f(obstacle.getScale()[0], obstacle.getScale()[1], obstacle.getScale()[2]);
            Vector3f rotation = new Vector3f(obstacle.getRotation()[0], obstacle.getRotation()[1], obstacle.getRotation()[2]);
            switch (obstacle.getModelName()) {
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
        dRenderer.addRenderableObjects(mapObjects);

        // Add drone
        RenderableDrone drone = new RenderableDrone(simulation.getDrone().getRadius());
        updateDroneLocation(drone, simulation.getDrone().getLocation());
        dRenderer.addRenderableObject(drone);

        // Return DRenderer
        return dRenderer;
    }
}
