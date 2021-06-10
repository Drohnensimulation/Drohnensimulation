package de.thi.dronesim;

import com.google.common.util.concurrent.AtomicDouble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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
    void registerUpdateHandler() throws InterruptedException {
        Simulation simulation = new Simulation();
        simulation.prepare();

        AtomicInteger i = new AtomicInteger();

        SimulationUpdateListener l1 = event -> assertEquals(1, i.incrementAndGet());
        simulation.registerUpdateListener(l1, 1000);
        SimulationUpdateListener l2 = event -> assertEquals(2, i.incrementAndGet());
        simulation.registerUpdateListener(l2, 800);
        SimulationUpdateListener l3 = event -> assertEquals(3, i.incrementAndGet());
        simulation.registerUpdateListener(l3, 10);
        SimulationUpdateListener l4 = event -> assertEquals(4, i.getAndSet(0));
        simulation.registerUpdateListener(l4, 10);

        assertEquals(8, simulation.getUpdateListeners().size());
        assertSame(l1, simulation.getUpdateListeners().get(1000));
        assertSame(l2, simulation.getUpdateListeners().get(799));
        assertSame(l3, simulation.getUpdateListeners().get(10));
        assertSame(l4, simulation.getUpdateListeners().get(9));

        simulation.start();
        Thread.sleep(1000);
        simulation.stop();
    }

    @Test
    void pauseResumeSimulation() throws InterruptedException {

        final Logger logger = LogManager.getLogger(this.getClass());

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
