package de.thi.dronesim;

import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.persistence.ConfigReader;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.reflections.Reflections;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Holds the whole Context of a Simulation
 *
 * @author Christian Schmied
 */
public class Simulation {

    private static final Logger logger = LogManager.getLogger();
    private static Set<Class<? extends ISimulationChild>> implementingChildren;
    private final SimulationConfig config;
    private final Map<Class<? extends ISimulationChild>, ISimulationChild> children;
    private final Drone drone;

    private double time;                                           // elapsed simulation time since reset [s]
    private int tps = 32;
    private final ScheduledExecutorService executorService;
    private ScheduledFuture<?> status;

    private final SortedMap<Integer, SimulationUpdateListener> updateListeners = new TreeMap<>();

    /**
     * Constructor with empty SimulationConfig SimulationConfig
     */
    public Simulation() {
        executorService = Executors.newScheduledThreadPool(1);
        Simulation.scanForChildren();
        this.config = new SimulationConfig(); //Empty Config
        this.children = new HashMap<>();
        this.drone = new Drone();
    }

    public Simulation(String configPath) {
        executorService = Executors.newScheduledThreadPool(1);
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
     *
     * @return The amount of ticks happening each second
     */
    public int getTps() {
        return tps;
    }

    /**
     * Sets the amount of ticks per second.
     * @param tps Ticks per second
     */
    public void setTps(int tps) {
        this.tps = tps;
    }

    /**
     * Creates Child Instances and registers default update listeners
     */
    public void prepare() {
        // Register location handlers
        this.registerUpdateListener(event -> drone.getLocation().updateDelta(event.getTps()), 900);
        this.registerUpdateListener(event -> drone.getLocation().updatePosition(event.getTps()), 800);

        this.instantiateChildren();
    }

    /**
     * Starts the simulation
     * <p>If the simulation is already running, nothing will happen.</p>
     */
    public void start() {
        if (status != null && !status.isCancelled() && !status.isDone()) {
            // Simulation already running
            return;
        }

        // Notify all children that the simulation is about to start
        children.forEach((key1, value1) -> value1.onSimulationStart());
        // Start execution
        status = executorService.scheduleAtFixedRate(() -> {
            // Create event
            final SimulationUpdateEvent event = new SimulationUpdateEvent(drone, time, tps);
            // Notify listeners
            updateListeners.forEach((priority, listener) -> listener.onUpdate(event));
            // Update time
            time += 1000.0 / tps;
        }, 0, 1000000 / tps, TimeUnit.MICROSECONDS);

        logger.info("Simulation started");
    }

    /**
     * Stop the simulation.
     * <p>If some tasks are still scheduled, they will still be executed</p>
     */
    public void stop() {
        status.cancel(true);
        // Notify all children that the simulation has stopped
        children.forEach((aClass, iSimulationChild) -> iSimulationChild.onSimulationStop());

        logger.printf(Level.INFO, "Simulation stopped after %.2f seconds.",time);
    }

    /**
     *
     * @return True if the simulation is running
     */
    public boolean isRunning() {
        return executorService.isTerminated();
    }

    /**
     * Registers a listener for an simulation update by priority.
     * @param listener Listener which will get notified each tick
     * @param priority Priority of the listener.
     *                  <ul>
     *                    <li>Higher priorities will be triggered first.</li>
     *                    <li>If the listener has the same priority than an already added listener, its priority get decremented</li>
     *                    <li>Reserved priorities:
     *                      <ul>
     *                          <li>900 - Location: requested input update</li>
     *                          <li>800 - Location: position update</li>
     *                      </ul>
     *                    </li>
     *                    <li>If no priority is required, priority zero should be used.</li>
     *                 </ul>
     */
    public void registerUpdateListener(SimulationUpdateListener listener, int priority) {
        if (priority > 1000)
            priority = 1000;
        while (updateListeners.containsKey(priority)) {
            priority--;
        }
        updateListeners.put(priority, listener);
    }

    /**
     * {@code priority} defaults to 0
     * @see Simulation#registerUpdateListener(SimulationUpdateListener, int)
     */
    public void registerUpdateListener(SimulationUpdateListener listener) {
        registerUpdateListener(listener, 0);
    }

    /**
     * @return All registered listeners
     */
    protected SortedMap<Integer, SimulationUpdateListener> getUpdateListeners() {
        return updateListeners;
    }

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
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException ignored) {
                // Allow abstract classes to inherit the interface
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
