package de.thi.dronesim;

import com.google.common.util.concurrent.AtomicDouble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marvin Wittschen
 */
public class SimulationTest {

    public static class MyTestChild implements ISimulationChild {

        private Simulation simulation;

        public MyTestChild() {
        }

        public String foo() {
            return "bar";
        }

        @Override
        public void initialize(Simulation simulation) {
            this.simulation = simulation;
        }

        @Override
        public Simulation getSimulation() {
            return simulation;
        }
    }

    public static abstract class AnAbstractChild implements ISimulationChild {
        public String foo() {
            return "bar";
        }

        @Override
        public void initialize(Simulation simulation) {
        }

        @Override
        public Simulation getSimulation() {
            return null;
        }
    }

    public static class ExtendedAbstractChild extends AnAbstractChild {
        public String foo() {
            return "foo" + super.foo();
        }
    }

    @Test
    public void testChildDetection() {
        Simulation simulation = new Simulation();
        simulation.prepare();

        assertNotNull(simulation.getChild(MyTestChild.class));
    }

    @Test
    public void testChildSameInstance() {
        Simulation simulation = new Simulation();
        simulation.prepare();

        Object childA = simulation.getChild(MyTestChild.class);
        Object childB = simulation.getChild(MyTestChild.class);
        assertSame(childA, childB);
    }

    @Test
    public void testChildMethodExecution() {
        Simulation simulation = new Simulation();
        simulation.prepare();

        MyTestChild childA = simulation.getChild(MyTestChild.class);
        assertEquals("bar", childA.foo());
    }

    @Test
    public void hasSimulation() {
        Simulation simulation = new Simulation();
        simulation.prepare();

        MyTestChild childA = simulation.getChild(MyTestChild.class);
        assertNotNull(childA.getSimulation());
    }

    @Test
    public void skipAbstractClass(){
        Simulation simulation = new Simulation();
        simulation.prepare();

        AnAbstractChild abstractChild = simulation.getChild(AnAbstractChild.class);
        assertNull(abstractChild);
    }

    @Test
    public void instanceOfExtendedClass() {
        Simulation simulation = new Simulation();
        simulation.prepare();

        ExtendedAbstractChild extendedAbstractChild = simulation.getChild(ExtendedAbstractChild.class);
        assertNotNull(extendedAbstractChild);

        assertEquals("foobar", extendedAbstractChild.foo());
    }

    @Test
    @Timeout(2000)
    void registerUpdateHandler() throws InterruptedException {
        Simulation simulation = new Simulation();
        // Remove all listeners of other modules
        simulation.getUpdateListeners().clear();
        // Register default listeners
        simulation.prepare();

        // Counter to verify call order
        LinkedList<Integer> actualList = new LinkedList<>();

        SimulationUpdateListener l0 = event -> actualList.add(1);
        simulation.registerUpdateListener(l0, 1000);
        SimulationUpdateListener l1 = event -> actualList.add(2);
        simulation.registerUpdateListener(l1, 1001);
        SimulationUpdateListener l2 = event -> actualList.add(3);
        simulation.registerUpdateListener(l2, 800);
        SimulationUpdateListener l3 = event -> actualList.add(4);
        simulation.registerUpdateListener(l3, 10);
        SimulationUpdateListener l4 = event -> actualList.add(5);
        simulation.registerUpdateListener(l4, 10);

        assertSame(l0, simulation.getUpdateListeners().get(1000));
        assertSame(l1, simulation.getUpdateListeners().get(999));
        assertSame(l2, simulation.getUpdateListeners().get(799));
        assertSame(l3, simulation.getUpdateListeners().get(10));
        assertSame(l4, simulation.getUpdateListeners().get(9));

        simulation.start();
        Thread.sleep(1000);
        simulation.stop();

        // Test call order
        for (int i = 1; i <= 5; i++) {
            assertEquals(i, actualList.removeFirst());
        }
    }

    @Test
    @Timeout(value = 11000, unit = TimeUnit.MILLISECONDS)
    void start() throws InterruptedException {
        Simulation simulation = new Simulation();
        simulation.prepare();
        // Set default values
        simulation.setSpeed(1);
        simulation.setTps(32);

        AtomicInteger i = new AtomicInteger();
        simulation.registerUpdateListener(event -> i.incrementAndGet());

        simulation.start();
        Thread.sleep(5000);
        simulation.stop();

        assertEquals(32 * 5, i.get(), 1,"Wrong tps");
        assertEquals(5000, simulation.getTime(), 50,"Wrong time");
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    void speedUp() throws InterruptedException {
        Simulation simulation = new Simulation();
        simulation.prepare();

        simulation.setSpeed(2);

        simulation.start();
        Thread.sleep(2000);
        simulation.stop();

        assertEquals(4000, simulation.getTime(), 100);
    }

    @Test
    @Timeout(value = 7000, unit = TimeUnit.MILLISECONDS)
    void reschedule() throws InterruptedException {
        Simulation simulation = new Simulation();
        simulation.prepare();

        AtomicInteger i = new AtomicInteger();
        // Crash drone after 1s
        simulation.registerUpdateListener(event -> i.incrementAndGet());

        simulation.start();
        Thread.sleep(500);
        i.set(0);
        simulation.setTps(10);
        Thread.sleep(5000);
        simulation.stop();

        assertEquals(10 * 5, i.get(), 5,"Wrong tps");
    }

    @Test
    @Timeout(3000)
    void crash() throws InterruptedException {
        Simulation simulation = new Simulation();
        simulation.prepare();

        AtomicInteger i = new AtomicInteger();
        // Crash drone after 1s
        simulation.registerUpdateListener(event -> {
            if (i.incrementAndGet() == 32) {
                event.getDrone().setCrashed(true);
            }
        });

        simulation.start();
        Thread.sleep(2000);
        simulation.stop();

        assertEquals(1000, simulation.getTime(), 100);
    }

    /**
     * @author Christian Schmied
     */
    @Test
    void pauseResumeSimulation() throws InterruptedException {

        final Logger logger = LogManager.getLogger(SimulationTest.class);

        Simulation simulation = new Simulation();
        simulation.prepare();

        AtomicInteger i = new AtomicInteger();
        AtomicDouble flag = new AtomicDouble();

        SimulationUpdateListener listener = event -> {
            if(i.get() == 10){
                logger.info("Pausing Simulation");
                flag.set(event.getTime());
                simulation.pause();
            }else if(i.get() == 11){
                double delta = event.getTime() - flag.get();
                logger.info("Pause Delta is {}", delta);
                assertTrue(delta < 1_000);
                simulation.stop();
            }
            i.incrementAndGet();
        };
        simulation.registerUpdateListener(listener);

        simulation.start();

        //Jep Busy Wait ;D
        while(simulation.getState() != SimulationState.PAUSED){
            Thread.sleep(10);
        }
        logger.info("Pause Detected, sleeping 1.5 Seconds to resume the Sim");
        Thread.sleep(1_500);

        simulation.start();
    }

}
