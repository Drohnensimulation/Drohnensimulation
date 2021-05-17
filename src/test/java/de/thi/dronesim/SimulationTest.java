package de.thi.dronesim;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {

    public static class MyTestChild implements ISimulationChild {

        private Simulation simulation;

        public MyTestChild(){
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
    public void hasSimulation(){
        Simulation simulation = new Simulation();
        simulation.prepare();

        MyTestChild childA = simulation.getChild(MyTestChild.class);
        assertNotNull(childA.getSimulation());
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

        assertEquals(6, simulation.getUpdateListeners().size());
        assertSame(l1, simulation.getUpdateListeners().get(1000));
        assertSame(l2, simulation.getUpdateListeners().get(799));
        assertSame(l3, simulation.getUpdateListeners().get(10));
        assertSame(l4, simulation.getUpdateListeners().get(9));

        simulation.start();
        Thread.sleep(1000);
        simulation.stop();
    }

}
