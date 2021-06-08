package de.thi.dronesim.sensor.types;

import org.junit.jupiter.api.Test;

import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.persistence.entity.SimulationConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
/**
 * Class to test getVectorAngel.
 *
 * @author Moris Breitenborn
 */
class MatrixCalculation {

	
	//All values
	SensorConfig sensorConfigA = new SensorConfig();
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
    }
	
	
	
	@Test
	void test1() {
		InfraredSensor s = new InfraredSensor(sensorConfigA);
		s.setDirection(1, 0, 0);
		s.setSensorAngle(45);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}

	@Test
	void test2() {
		InfraredSensor s = new InfraredSensor(sensorConfigA);
		s.setDirection(1, 1, 0);
		s.setSensorAngle(45);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}

	@Test
	void test3() {
		InfraredSensor s = new InfraredSensor(sensorConfigA);
		s.setDirection(1, 1, 1);
		s.setSensorAngle(56);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}

	@Test
	void test4() {
		InfraredSensor s = new InfraredSensor(sensorConfigA);
		s.setDirection(0, 1, 1);
		s.setSensorAngle(45);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}

	@Test
	void test5() {
		InfraredSensor s = new InfraredSensor(sensorConfigA);
		s.setDirection(0, 0, 1);
		s.setSensorAngle(45);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}
}
