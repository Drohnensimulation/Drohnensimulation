package de.thi.dronesim.sensor.types;

import com.jme3.math.Vector3f;

import de.thi.dronesim.persistence.entity.SensorConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

/**
 * Class to test Sensors.
 *
 * @author Daniel Stolle
 */
class SensorTest {

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
        sensorConfigA.setSpinsPerSecond(1);
        sensorConfigA.setStartRotationTime(4);
        sensorConfigsA.add(sensorConfigA);
        //Form
        sensorConfigA.setSensorForm("CONE");
        sensorConfigA.setCalcType("AVG");
    }
	
    /**
     * Sensor pointing in direction of x.
     * With a angle of 45 degrees and a size of 1 the origin should be at -1 for x.
     */
    @Test
    void testGetOrigin1() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(1, 0, 0);
        sensor.setPosition(0, 0, 0);
        sensor.setSensorAngle(45);
        sensor.setRange(1);
        sensor.setSize(1);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(-1F, 0F, 0F);
        assertEquals(expected, origin);
    }

    /**
     * Same as {@link SensorTest#testGetOrigin1()} but shifted 1 in x direction.
     */
    @Test
    void testGetOrigin2() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(1, 0, 0);
        sensor.setPosition(1, 0, 0);
        sensor.setSensorAngle(45);
        sensor.setRange(1);
        sensor.setSize(1);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(0F, 0F, 0F);
        assertEquals(expected, origin);
    }

    /**
     * Same as {@link SensorTest#testGetOrigin1()} but in y direction.
     */
    @Test
    void testGetOrigin3() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(0, 1, 0);
        sensor.setPosition(0, 0, 0);
        sensor.setSensorAngle(45);
        sensor.setRange(1);
        sensor.setSize(1);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(0F, -1F, 0F);
        assertEquals(expected, origin);
    }

    /**
     * Same as {@link SensorTest#testGetOrigin1()} but in z direction.
     */
    @Test
    void testGetOrigin4() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(0, 0, 1);
        sensor.setPosition(0, 0, 0);
        sensor.setSensorAngle(45);
        sensor.setRange(1);
        sensor.setSize(1);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(0F, 0F, -1F);
        assertEquals(expected, origin);
    }

    /**
     * With direction (1, 1, 0) the origin should be {@code -1 * (1 / sqrt(2))} for x and y.
     */
    @Test
    void testGetOrigin5() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(1, 1, 0);
        sensor.setPosition(0, 0, 0);
        sensor.setSensorAngle(45);
        sensor.setRange(1);
        sensor.setSize(1);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(-0.70710677F, -0.70710677F, 0F);
        assertEquals(expected, origin);
    }

    /**
     * With angle 45 and size 1 the origin should be the normalized vector in opposite direction
     * of the direction vector.
     */
    @Test
    void testGetOrigin6() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(1, 1, 1);
        sensor.setPosition(0, 0, 0);
        sensor.setSensorAngle(45);
        sensor.setRange(1);
        sensor.setSize(1);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(-1F, -1F, -1F).normalize();
        assertEquals(expected, origin);
    }

    /**
     * With double the size, the origin should be twice as far away at 45 degrees.
     */
    @Test
    void testGetOrigin7() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(1, 0, 0);
        sensor.setPosition(0, 0, 0);
        sensor.setSensorAngle(45);
        sensor.setRange(1);
        sensor.setSize(2);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(-2F, 0F, 0F);
        assertEquals(expected, origin);
    }

    /**
     * With 60 degrees, the origin should be {@code -1 * (1 / sqrt(3))}.
     */
    @Test
    void testGetOrigin8() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(1, 0, 0);
        sensor.setPosition(0, 0, 0);
        sensor.setSensorAngle(60F);
        sensor.setRange(1);
        sensor.setSize(1);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(-0.57735026F, 0F, 0F);
        assertEquals(expected, origin);
    }

    /**
     * Same as {@link SensorTest#testGetOrigin6()} but shifted with (1, 2, 3).
     */
    @Test
    void testGetOrigin9() {

        // given
        DistanceSensor sensor = new InfraredSensor(sensorConfigA);
        sensor.setDirection(1, 1, 1);
        sensor.setPosition(1, 2, 3);
        sensor.setSensorAngle(45);
        sensor.setRange(1);
        sensor.setSize(1);

        // when
        Vector3f origin = sensor.calcOrigin();

        // then
        Vector3f expected = new Vector3f(-1F, -1F, -1F).normalize().add(new Vector3f(1F, 2F, 3F));
        assertEquals(expected, origin);
    }
}
