package de.thi.dronesim;

import org.junit.jupiter.api.Test;

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
        public void setSimulation(Simulation simulation) {
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
}
