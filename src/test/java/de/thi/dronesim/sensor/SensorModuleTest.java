package de.thi.dronesim.sensor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.sensor.types.*;
import org.junit.jupiter.api.*;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.persistence.entity.SimulationConfig;

/**
 * Test for {@link SensorModule}
 *
 * @author Moris Breitenborn, Daniel Stolle
 */
class SensorModuleTest {

	//All values
	private SimulationConfig simConfAll = new SimulationConfig();
	private SensorConfig sensorConfigA = new SensorConfig();
	
	//Missing values
	private SimulationConfig simConfMissing = new SimulationConfig();
	private SensorConfig sensorConfigB = new SensorConfig();

    @BeforeEach
    public void init() {
    	//All
    	List<SensorConfig> sensorConfigsA = new ArrayList<>();
    	sensorConfigA.setRange(10);
    	sensorConfigA.setSensorAngle(11);
    	sensorConfigA.setSensorRadius(13);
        sensorConfigA.setMeasurementAccuracy(14);
        sensorConfigA.setDirectionX(15);
        sensorConfigA.setDirectionY(16);
        sensorConfigA.setDirectionZ(17);
        sensorConfigA.setPosX(18);
        sensorConfigA.setPosY(19);
        sensorConfigA.setPosZ(20);
        //UltrasonicSensor
        sensorConfigA.setRangeIncreaseVelocity(4);
        sensorConfigA.setStartIncreaseTime(4);
        //RotationSensor
        sensorConfigA.setSpinsPerSecond(4);
        sensorConfigA.setStartRotationTime(4);
        sensorConfigsA.add(sensorConfigA);
        //Form
        sensorConfigA.setSensorForm("CONE");
        sensorConfigA.setCalcType("AVG");
        
        this.simConfAll.setSensorConfigList(sensorConfigsA);
        
        // Missing
        List<SensorConfig> sensorConfigsB = new ArrayList<>();
    	sensorConfigB.setRange(10);
    	sensorConfigB.setSensorAngle(11);
    	sensorConfigB.setSensorRadius(13);
    	sensorConfigB.setMeasurementAccuracy(14);
    	sensorConfigB.setDirectionX(15);
    	sensorConfigB.setDirectionY(16);
    	sensorConfigB.setDirectionZ(17);
    	sensorConfigB.setPosX(18);
    	sensorConfigB.setPosY(19);
    	sensorConfigB.setPosZ(20);
        //UltrasonicSensor
    	sensorConfigB.setRangeIncreaseVelocity(4);
    	sensorConfigB.setStartIncreaseTime(4);
        //RotationSensor
    	sensorConfigB.setSpinsPerSecond(0);
    	sensorConfigB.setStartRotationTime(0);
    	//Form
    	sensorConfigB.setSensorForm("CONE");
    	sensorConfigB.setCalcType("AVG");
    	sensorConfigsB.add(sensorConfigB);
        
        this.simConfMissing.setSensorConfigList(sensorConfigsB);
    }

	/**
	 * Is testing if every sensor type (distance sensor) can be created
	 */
	@Test
	void createDistanceSensorTest() {
		
		SensorModule moduleTest = new SensorModule();
		//RotationSensor
		sensorConfigA.setClassName("RotationSensor");
		ISensor module = moduleTest.createSensor(sensorConfigA);
		RotationSensor rotationSensor = new RotationSensor(sensorConfigA);
		assertEquals(module.getId(), rotationSensor.getId());
		//UltrasonicSensor
		sensorConfigA.setClassName("UltrasonicSensor");
		module = moduleTest.createSensor(sensorConfigA);
		UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(sensorConfigA);
		assertEquals(module.getId(), ultrasonicSensor.getId());
		//InfraredSensor
		sensorConfigA.setClassName("InfraredSensor");
		module = moduleTest.createSensor(sensorConfigA);
		InfraredSensor infraredSensor = new InfraredSensor(sensorConfigA);
		assertEquals(module.getId(), infraredSensor.getId());
	}

	/**
	 * Tests the creation of sensors with a Json file
	 */
	@Test
	void createSensorsWithJson() {
		// given
		Simulation simulation = new Simulation("src/test/resources/de/thi/dronesim/sensor/sensorModuleInit.json");
		simulation.prepare();

		// when
		SensorModule child = simulation.getChild(SensorModule.class);
		child.runAllMeasurements(new SimulationUpdateEvent(simulation.getDrone(), 100.0, 10));

		// then
		assertNotNull(child);
		assertEquals(5, child.getResultsFromAllSensors().size());

		assertEquals(GpsSensor.class, child.getResultFromSensor(1).getSensor().getClass());
		assertEquals(InfraredSensor.class, child.getResultFromSensor(2).getSensor().getClass());
		assertEquals(RotationSensor.class, child.getResultFromSensor(3).getSensor().getClass());
		assertEquals(UltrasonicSensor.class, child.getResultFromSensor(4).getSensor().getClass());
		assertEquals(WindSensor.class, child.getResultFromSensor(5).getSensor().getClass());

		assertEquals("GpsSensor", child.getResultFromSensor(1).getSensor().getType());
		assertEquals("InfrarotSensor", child.getResultFromSensor(2).getSensor().getType());
		assertEquals("RotationSensor", child.getResultFromSensor(3).getSensor().getType());
		assertEquals("UltrasonicSensor", child.getResultFromSensor(4).getSensor().getType());
		assertEquals("WindSensor", child.getResultFromSensor(5).getSensor().getType());

		assertEquals("Gpssensor", child.getResultFromSensor(1).getSensor().getName());
		assertEquals("InfraredSensor", child.getResultFromSensor(2).getSensor().getName());
		assertEquals("RotationSensor", child.getResultFromSensor(3).getSensor().getName());
		assertEquals("UltrasonicSensor", child.getResultFromSensor(4).getSensor().getName());
		assertEquals("Windsensor", child.getResultFromSensor(5).getSensor().getName());

		assertTrue(child.getResultFromSensor(1).getSensor().equals(child.getResultFromSensor(1).getSensor()));
		assertFalse(child.getResultFromSensor(1).getSensor().equals(child.getResultFromSensor(2).getSensor()));
		assertTrue(child.getResultFromSensor(2).getSensor().equals(child.getResultFromSensor(2).getSensor()));
		assertFalse(child.getResultFromSensor(2).getSensor().equals(child.getResultFromSensor(3).getSensor()));
		assertTrue(child.getResultFromSensor(3).getSensor().equals(child.getResultFromSensor(3).getSensor()));
		assertFalse(child.getResultFromSensor(3).getSensor().equals(child.getResultFromSensor(4).getSensor()));
		assertTrue(child.getResultFromSensor(4).getSensor().equals(child.getResultFromSensor(4).getSensor()));
		assertFalse(child.getResultFromSensor(4).getSensor().equals(child.getResultFromSensor(5).getSensor()));
		assertTrue(child.getResultFromSensor(5).getSensor().equals(child.getResultFromSensor(5).getSensor()));
		assertFalse(child.getResultFromSensor(5).getSensor().equals(child.getResultFromSensor(1).getSensor()));

		assertEquals(1, child.getResultFromSensor(1).getSensor().getId());
		assertEquals(2, child.getResultFromSensor(2).getSensor().getId());
		assertEquals(3, child.getResultFromSensor(3).getSensor().getId());
		assertEquals(4, child.getResultFromSensor(4).getSensor().getId());
		assertEquals(5, child.getResultFromSensor(5).getSensor().getId());

		assertEquals(0, child.getResultFromSensor(1).getObstacle().size());
		assertEquals(0, child.getResultFromSensor(2).getObstacle().size());
		assertEquals(0, child.getResultFromSensor(3).getObstacle().size());
		assertEquals(0, child.getResultFromSensor(4).getObstacle().size());
		assertEquals(0, child.getResultFromSensor(5).getObstacle().size());
	}

	/**
	 * Tests the creation of sensors with duplicate IDs
	 */
	@Test
	void createSensorsWithDuplicates() {
		// given
		Simulation simulation = new Simulation("src/test/resources/de/thi/dronesim/sensor/sensorModuleInitDuplicates.json");

		// when
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, simulation::prepare);

		// then
		assertEquals("Duplicate sensor ID", illegalArgumentException.getMessage());
	}

	/**
	 * Tests the creation of sensors with wrong sensor Name.
	 */
	@Test
	void createSensorsWithWrongName() {
		// given
		Simulation simulation = new Simulation("src/test/resources/de/thi/dronesim/sensor/sensorModuleInitWrongName.json");

		// when
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, simulation::prepare);

		// then
		assertEquals("No sensor implementation available for value: undefined", illegalArgumentException.getMessage());
	}

	/**
	 * Tests the creation of sensors with wrong sensor Name.
	 */
	@Test
	void createSensorsWithNoName() {
		// given
		Simulation simulation = new Simulation("src/test/resources/de/thi/dronesim/sensor/sensorModuleInitNoName.json");

		// when
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, simulation::prepare);

		// then
		assertEquals("Missing className", illegalArgumentException.getMessage());
	}

	@Test
	void createWithNoConfig() {
		// given
		Simulation simulation = new Simulation();
		simulation.prepare();

		// when
		SensorModule child = simulation.getChild(SensorModule.class);
		child.runAllMeasurements(new SimulationUpdateEvent(simulation.getDrone(), 100.0, 10));

		// then
		assertNotNull(child);
		assertEquals(0, child.getResultsFromAllSensors().size());
	}

	@Test
	void saveToConfig() {
		// given
		Simulation simulation = new Simulation("src/test/resources/de/thi/dronesim/sensor/sensorModuleInit.json");
		simulation.prepare();

		// when
		SensorModule child = simulation.getChild(SensorModule.class);
		child.runAllMeasurements(new SimulationUpdateEvent(simulation.getDrone(), 0.0, 10));

		// then
		assertNotNull(child);
		List<SensorConfig> createdConfigs = child.getResultsFromAllSensors().stream().map(o -> o.getSensor().saveToConfig()).collect(Collectors.toList());
		createdConfigs.sort(Comparator.comparing(SensorConfig::getSensorId));

		assertEquals(simulation.getConfig().getSensorConfigList(), createdConfigs);
	}
}
