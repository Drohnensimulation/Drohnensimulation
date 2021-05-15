package de.thi.dronesim.sensor.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Class to test getVectorAngel.
 *
 * @author Moris Breitenborn
 */
class MatrixCalculation {

	@Test
	void test1() {
		InfraredSensor s = new InfraredSensor();
		s.setDirection(1, 0, 0);
		s.setSensorAngle(45);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}

	@Test
	void test2() {
		InfraredSensor s = new InfraredSensor();
		s.setDirection(1, 1, 0);
		s.setSensorAngle(45);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}

	@Test
	void test3() {
		InfraredSensor s = new InfraredSensor();
		s.setDirection(1, 1, 1);
		s.setSensorAngle(56);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}

	@Test
	void test4() {
		InfraredSensor s = new InfraredSensor();
		s.setDirection(0, 1, 1);
		s.setSensorAngle(45);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}

	@Test
	void test5() {
		InfraredSensor s = new InfraredSensor();
		s.setDirection(0, 0, 1);
		s.setSensorAngle(45);
		s.calcSurfaceVector();
		double grad = s.calcAngel(s.calcSurfaceVector(), s.getDirectionVector());
		grad = Math.round(Math.toDegrees(grad));
		assertEquals(s.getSensorAngle(), grad);
	}
}
