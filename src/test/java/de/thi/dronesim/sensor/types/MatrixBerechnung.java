package de.thi.dronesim.sensor.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Class to test getVectorAngel.
 *
 * @author Moris Breitenborn
 */
class MatrixBerechnung {

	@Test
	void test1() {
		InfraredSensor s = new InfraredSensor();
		s.directionX = 1;
		s.directionY = 0;
		s.directionZ = 0;
		s.sensorAngle = 45f;
		s.getVectorAngel();
		double grad = s.checkAngel(s.getVectorAngel(), s.getOrientation());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.sensorAngle, grad);
	}

	@Test
	void test2() {
		InfraredSensor s = new InfraredSensor();
		s.directionX = 1;
		s.directionY = 1;
		s.directionZ = 0;
		s.sensorAngle = 45f;
		s.getVectorAngel();
		double grad = s.checkAngel(s.getVectorAngel(), s.getOrientation());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.sensorAngle, grad);
	}

	@Test
	void test3() {
		InfraredSensor s = new InfraredSensor();
		s.directionX = 1;
		s.directionY = 1;
		s.directionZ = 1;
		s.sensorAngle = 56f;
		s.getVectorAngel();
		double grad = s.checkAngel(s.getVectorAngel(), s.getOrientation());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.sensorAngle, grad);
	}

	@Test
	void test4() {
		InfraredSensor s = new InfraredSensor();
		s.directionX = 0;
		s.directionY = 1;
		s.directionZ = 1;
		s.sensorAngle = 45f;
		s.getVectorAngel();
		double grad = s.checkAngel(s.getVectorAngel(), s.getOrientation());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.sensorAngle, grad);
	}

	@Test
	void test5() {
		InfraredSensor s = new InfraredSensor();
		s.directionX = 0;
		s.directionY = 0;
		s.directionZ = 1;
		s.sensorAngle = 45f;
		s.getVectorAngel();
		double grad = s.checkAngel(s.getVectorAngel(), s.getOrientation());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.sensorAngle, grad);
	}

}
