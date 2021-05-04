package de.thi.dronesim.sensor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import de.thi.dronesim.sensor.types.InfraredSensor;

class MatrixBerechnung {

	@Test
	void test() {
		InfraredSensor s = new InfraredSensor();
		s.posX = 3;
		s.posY = 8;
		s.posZ = 7;
		s.directionX = 4;
		s.directionX = 11;
		s.directionX = 5;
		s.sensorAngle = 90f;
		s.getVectorAngel();
		float grad = s.checkAngel(s.getVectorAngel(), s.getOrientation());
		grad = (float)  Math.round(Math.toDegrees(grad));
		assertEquals(s.sensorAngle, grad);
	}

}
