package de.thi.dronesim.gui.dview;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * DView for rendering the 3D-View into a canvas
 *
 * @author Michael Weichenrieder
 */
public class DView extends SimpleApplication implements ISimulationChild {

    /**
     * Enum of possible perspectives
     *
     * @author Michael Weichenrieder
     */
    public enum Perspective {
        THIRD_PERSON, FIRST_PERSON, BIRD_VIEW
    }

    // Main simulation
    private Simulation simulation;

    // Ground
    private volatile float xMin = 0, xMax = 0, zMin = 0, zMax = 0;
    private Spatial ground;

    // Objects
    private final HashMap<Integer, RenderableDrone> drones = new HashMap<>();

    // Render queue (because rendering needs to be done on render thread)
    private final List<RenderableObject> renderQueue = new ArrayList<>();

    // Params
    private volatile Perspective perspective = Perspective.THIRD_PERSON;
    private volatile int dronePerspectiveId = -1;

    // Frame update listeners
    private final List<Runnable> frameUpdateListeners = new ArrayList<>();

    /**
     * Creates a new DView
     */
    public DView() {
        super();
        //Logger.getLogger("com.jme3").setLevel(Level.OFF); // TODO: Remove for production
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setSamples(8);
        setSettings(settings);
        setDisplayStatView(false);
        setDisplayFps(true); // TODO: Set to false for production
        setPauseOnLostFocus(false);
    }

    /**
     * Adds a renderable object/obstacle to the map
     *
     * @param object Object to be added
     */
    public void addRenderableObject(RenderableObject object) {
        if (object.getXMax() > xMax) {
            xMax = object.getXMax();
        }
        if (object.getXMin() < xMin) {
            xMin = object.getXMin();
        }
        if (object.getZMax() > zMax) {
            zMax = object.getZMax();
        }
        if (object.getZMin() < zMin) {
            zMin = object.getZMin();
        }
        renderQueue.add(object);
    }

    /**
     * Adds multiple renderable objects/obstacles to the map
     *
     * @param objects Objects to be added
     */
    public void addRenderableObjects(Collection<RenderableObject> objects) {
        for (RenderableObject object : objects) {
            addRenderableObject(object);
        }
    }

    /**
     * Retrieve the display canvas
     *
     * @return Display canvas
     */
    public Canvas getCanvas() {
        createCanvas();
        JmeCanvasContext ctx = (JmeCanvasContext) getContext();
        ctx.setSystemListener(this);
        Dimension dim = new Dimension(settings.getWidth(), settings.getHeight());
        ctx.getCanvas().setPreferredSize(dim);
        return ctx.getCanvas();
    }

    /**
     * Set the perspective shown in the canvas
     *
     * @param perspective Perspective
     */
    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
        dronePerspectiveId = drones.size() > 0 ? drones.get(0).getId() : -1;
    }

    /**
     * Set the perspective shown in the canvas
     *
     * @param perspective Perspective
     * @param droneId ID of the perspectives drone
     */
    public void setPerspective(Perspective perspective, int droneId) {
        this.perspective = perspective;
        dronePerspectiveId = droneId;
    }

    /**
     * Set the perspective shown in the canvas
     *
     * @param perspective Perspective
     * @param drone Drone thats perspective is requested
     */
    public void setPerspective(Perspective perspective, RenderableDrone drone) {
        this.perspective = perspective;
        dronePerspectiveId = drone.getId();
    }

    /**
     * Initializes the scene
     */
    @Override
    public void simpleInitApp() {
        // Setup camera
        flyCam.setEnabled(false);
        cam.setFrustumPerspective(60f, cam.getWidth() / (float) cam.getHeight(), .1f, cam.getFrustumFar());

        // Setup map
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        addLight();
        updateGround();
    }

    /**
     * Adds light and shadows to the world
     */
    private void addLight() {
        // Main light
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White.mult(2));
        sun.setDirection(new Vector3f(-1, -3, -2));
        rootNode.addLight(sun);

        // Helper light so that back sides have colors
        DirectionalLight sun2 = new DirectionalLight();
        sun2.setColor(ColorRGBA.White.mult(1));
        sun2.setDirection(new Vector3f(1, -3, 2));
        rootNode.addLight(sun2);

        // Enable shadows
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        // Render shadows according to main light
        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(assetManager, 4096, 1);
        shadowFilter.setLight(sun);
        shadowFilter.setEnabled(true);
        shadowFilter.setShadowIntensity(.6f);
        FilterPostProcessor postProcessor = new FilterPostProcessor(assetManager);
        postProcessor.addFilter(shadowFilter);
        viewPort.addProcessor(postProcessor);
    }

    /**
     * Updates the ground position and size
     */
    private void updateGround() {
        if (ground == null) {
            ground = assetManager.loadModel("Objects/Obstacles/ground.obj");
            rootNode.attachChild(ground);
        }
        ground.setLocalTranslation((xMax + xMin) / 2, 0, (zMax + zMin) / 2);
        ground.setLocalScale(xMax - xMin + 2, 1, zMax - zMin + 2);
    }

    /**
     * Renders a frame (auto-called)
     */
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        // Update objects if in queue
        addRenderableObjectsToDisplay();

        // Do listeners
        for(Runnable listener : frameUpdateListeners) {
            listener.run();
        }

        // Update drone parameters
        RenderableDrone perspectiveDrone = null;
        for(int index : drones.keySet()) {
            RenderableDrone drone = drones.get(index);
            drone.updateParameters();
            if(drone.getId() == dronePerspectiveId) {
               perspectiveDrone = drone;
            }
        }

        // Set cam position/rotation according to perspective
        if(perspectiveDrone != null) {
            switch (perspective) {
                case THIRD_PERSON:
                    perspectiveDrone.adjustCamToThirdPerson(cam);
                    break;
                case BIRD_VIEW:
                    perspectiveDrone.adjustCamToBirdView(cam);
                    break;
                case FIRST_PERSON:
                    perspectiveDrone.adjustCamToFirstPerson(cam);
            }
        }
    }

    /**
     * Helper to add all objects in render queue to scene
     */
    private void addRenderableObjectsToDisplay() {
        if(renderQueue.isEmpty()) {
            return;
        }
        while(!renderQueue.isEmpty()) {
            RenderableObject renderableObject = renderQueue.remove(0);
            rootNode.attachChild(renderableObject.getObject(assetManager));
            if(renderableObject instanceof RenderableDrone) {
                drones.put(renderableObject.getId(), (RenderableDrone) renderableObject);
                if(dronePerspectiveId == -1) {
                    dronePerspectiveId = renderableObject.getId();
                }
            }
        }
        updateGround();
    }

    /**
     * Adds a listener on frame updates
     *
     * @param listener Runnable to be called before every frame update
     */
    public void addFrameUpdateListener(Runnable listener) {
        frameUpdateListeners.add(listener);
    }

    /**
     * Removes a listener
     *
     * @param listener Listener to be removed
     */
    public void removeFrameUpdateListener(Runnable listener) {
        frameUpdateListeners.remove(listener);
    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }
}
