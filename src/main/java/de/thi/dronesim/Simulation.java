package de.thi.dronesim;

import com.google.gson.Gson;
import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.gui.GuiManager;
import de.thi.dronesim.obstacle.UfoObjs;
import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.persistence.ConfigReader;
import de.thi.dronesim.persistence.entity.ObstacleConfig;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import de.thi.dronesim.wind.Wind;
import org.reflections.Reflections;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Holds the whole Context of a Simulation
 *
 * @author Christian Schmied
 */
public class Simulation {
    private static Set<Class<? extends ISimulationChild>> implementingChildren;
    private final SimulationConfig config;
    private final Map<Class<? extends ISimulationChild>, ISimulationChild> children;
    private final Drone drone;
    private boolean running = false;

    private volatile int time;                                           // elapsed simulation time since reset [ms]

    /**
     * Constructor with empty SimulationConfig SimulationConfig
     */
    public Simulation() {
        Simulation.scanForChildren();
        this.config = new SimulationConfig(); //Empty Config
        this.children = new HashMap<>();
        this.drone = new Drone();
    }

    public Simulation(String configPath) {
        Simulation.scanForChildren();
        this.config = ConfigReader.readConfig(configPath);
        this.children = new HashMap<>();
        this.drone = new Drone();
    }

    /**
     *
     * @return time Elapsed simulation time in ms
     */
    public int getTime() {
        return time;
    }

    public boolean isRunning() {
        return running;
    }

    public void toggleRunning() {
        running = !running;
    }

    /**
     * Create Child Instances and propably start the Simulation...
     */
    public void prepare() {
        this.instantiateChildren();
        //System.out.println(children);
        UfoObjs obs = this.getChild(UfoObjs.class);
        Gson gson = new Gson();
        ObstacleConfig obstacleConfig = null;


        /**
         * Reads a obstacle config file and adds all obstacles to the Simulation-Obstacle class
         */
        try {
            Reader reader = Files.newBufferedReader(Paths.get("src/main/java/de/thi/dronesim/example/obsconf.json"));
            obstacleConfig = gson.fromJson(reader, ObstacleConfig.class);
        } catch (Exception e) {
            System.out.println("Obstacle config could not be loaded.");
            e.printStackTrace();
        }

        for(ObstacleDTO dto : obstacleConfig.obstacles) {
            obs.addObstacle(dto);
        }
        //System.out.println(obs.getObstacles());

        Wind wind = this.getChild(Wind.class);
        //System.out.println(wind.getWindLayers());


        GuiManager gui = this.getChild(GuiManager.class);

        //gui.openDViewGui();

    }

    //TODO start the Simulation and provide an way how to inform the Children about a Tick

    /**
     * Get the currently Single Drone from the Simulation
     * @return a Drone Object
     */
    public Drone getDrone() {
        return drone;
    }

    /**
     * Get the Configuration for the current Simulation
     * @return The Config
     */
    public SimulationConfig getConfig() {
        return this.config;
    }

    public <T extends ISimulationChild> T getChild(Class<T> instanceClass) {
        if (instanceClass == null) {
            throw new IllegalArgumentException();
        }
        ISimulationChild foundChild = children.get(instanceClass);

        // Jep it's hacky but it shouldWork ;D
        if (foundChild != null && foundChild.getClass().equals(instanceClass)) {
            return (T) children.get(instanceClass);
        }
        return null;
    }

    /**
     * Invoke the Constructors of all Children
     * and assign the Simulation to it
     */
    private void instantiateChildren() {
        for (Class<? extends ISimulationChild> childClass : implementingChildren) {
            try {
                Constructor<? extends ISimulationChild> constructor = childClass.getConstructor();
                ISimulationChild instance = constructor.newInstance();
                instance.setSimulation(this);
                this.children.put(childClass, instance);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Handle Reflection, to discover all the Simulation Children
    private synchronized static void scanForChildren() {
        if (Simulation.implementingChildren == null) {
            Reflections reflections = new Reflections(Simulation.class.getPackageName());
            Simulation.implementingChildren = reflections.getSubTypesOf(ISimulationChild.class);
        }
    }
}
