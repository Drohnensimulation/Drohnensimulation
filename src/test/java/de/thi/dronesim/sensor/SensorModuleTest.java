package de.thi.dronesim.sensor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import de.thi.dronesim.sensor.types.InfraredSensor;
import de.thi.dronesim.sensor.types.RotationSensor;
import de.thi.dronesim.sensor.types.UltrasonicSensor;

class SensorModuleTest {

	//All values
	SimulationConfig simConfAll = new SimulationConfig();
	SensorConfig sensorConfigA = new SensorConfig();
	
	//Missing values
	SimulationConfig simConfMissing = new SimulationConfig();
	SensorConfig sensorConfigB = new SensorConfig();
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
    
	// Is testing if every sensor type (distance sensor) can be created 
	@Test
	void createSensorTest() {
		
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
	

}
