package de.thi.dronesim;

import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.persistence.ConfigReader;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    private volatile double time;                                           // elapsed simulation time since reset [s]

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
     * @return time Elapsed simulation time in s
     */
    public double getTime() {
        return time;
    }

    /**
     * Create Child Instances and propably start the Simulation...
     */
    public void prepare() {
        this.instantiateChildren();
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
                instance.initialize(this);
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
