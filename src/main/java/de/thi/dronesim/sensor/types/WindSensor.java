package de.thi.dronesim.sensor.types;

import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.sensor.ISensor;
import de.thi.dronesim.sensor.Vector3d;
import de.thi.dronesim.sensor.dto.SensorResultDto;

/**
 * The wind sensor can measure the wind direction and strength for a two dimensional plain.
 * The three dimensional wind will always be projected onto the sensor plain.
 * @author dominik bartl
 *
 */
public class WindSensor implements ISensor {

	private String name;

	private final Vector3d defaultDroneHeading = new Vector3d(1, 0, 0);
	
	//Sensor direction when drone is looking to defaultDroneHeading
	//It's the normal vector of the plain in which the sensor can measure the wind
	protected Vector3d relativeDirection;
	
	//If the measured wind blows in this direction, the measurement will return an angle of 0 degrees
	protected Vector3d zeroDegreeDirection;
	
	//If the measured wind blows in this direction, the measurement will return an angle of 90 degrees
	protected Vector3d nintyDegreeDirection;
	
	//The drone that the sensor was added to
	protected Drone drone;
	
	/**
	 * Configures the Wind Sensor. 
	 * First, the relativ direction of the sensor has to be set when the drone 
	 * is heading to (1, 0, 0). The Sensor can only measure wind from a angle that is perpendicular to it's
	 * relative direction vector. 
	 * Second, a direction has to be chosen that will lead to a angle of 0 if wind 
	 * from this direction hits the sensor. 
	 * Third, a direction has to be chosen that will lead to a angle of 90
	 * degree if wind from this direction hits the sensor (because a value growth (anti-)clockwise is not
	 * in all situations well defined.
	 *
	 * @param name
	 * @param sensorDirection
	 * @param zeroDegreeDirection
	 * @param nintyDegreeDirection
	 */
	public WindSensor(String name, Vector3d sensorDirection, Vector3d zeroDegreeDirection, Vector3d nintyDegreeDirection) {
		this.name = name;
		this.relativeDirection = sensorDirection.copy();
		
		if(Double.compare(this.relativeDirection.getX(), 0) == 0 &&
		   Double.compare(this.relativeDirection.getY(), 0) == 0 &&
		   Double.compare(this.relativeDirection.getZ(), 0) == 0) {
			//No direction was set
			throw new IllegalArgumentException("sensorDirection must not be (0,0,0)!");
		}
		
		this.zeroDegreeDirection = zeroDegreeDirection.copy();
		this.nintyDegreeDirection = nintyDegreeDirection.copy();
		
		if(Double.compare(this.scalarProduct(this.relativeDirection, this.zeroDegreeDirection), 0) != 0 ||
		   Double.compare(this.scalarProduct(this.relativeDirection, this.nintyDegreeDirection), 0) != 0 ||
		   Double.compare(this.scalarProduct(this.nintyDegreeDirection, this.zeroDegreeDirection), 0) != 0) {
			//The three vectors must all have an angle of 90 degrees
			throw new IllegalArgumentException("sensorDirection, zeroDegreeDirection and nintyDegreeDirection must be orthogonal!");
		}
	}
	
	/**
	 * Gets the vector in which the sensor points if the drone heading was (1,0,0)
	 * @return
	 */
	public Vector3d getRelativeSensorDirection() {
		return this.relativeDirection;
	}
	
	/**
	 * If the drone heading was (1,0,0), wind from this direction will lead to a measured wind angle of 0 degrees.
	 * @return
	 */
	public Vector3d getRelativeZeroDegreeDirection() {
		return this.zeroDegreeDirection;
	}
	
	/**
	 * If the drone heading was (1,0,0), wind from this direction will lead to a measured wind angle of 90 degrees.
	 * @return
	 */
	public Vector3d getRelativeNintyDegreeDirection() {
		return this.nintyDegreeDirection;
	}
	
	/**
	 * Adds the Sensor to a drone
	 * @param drone
	 */
	public void setDrone(Drone drone) {
		this.drone = drone;
	}
	
	/**
	 * Gets the drone that contains the sensor or null if the sensor was never added to a drone
	 * @return
	 */
	public Drone getDrone() {
		return this.drone;
	}
	
	/**
	 * Gets the sensor measurement
	 * @return
	 */
	public String getMeasurement() {
		//Dummy values for compiling.
		double droneHeadingDeg = 225;
		double droneMovementDeg = 225;
		double windDirectionDeg = 135; //Direction in which the wind blows
		double windSpeed = 200;
		double droneHorizontalSpeed = 50;
		double droneVerticalSpeed = 0; //Speed >0 means drone goes upwards, <0 drone goes downwards => -20 means drone goes down with 20 m/s 
		//TODO: which direction is 0 deg?
		
		//TODO: Get values from Drone / Wind interface
		
		Vector3d droneHeadingVec;
		Vector3d droneMovementVec;
		Vector3d windDirection;
		Vector3d absSensorDirection;
		
		//Transform to a vector
		droneHeadingVec = this.degToVector(droneHeadingDeg);
		
		//transform to vector and scale the it. The vector length should represent the drone speed
		droneMovementVec = this.scaleVector(this.degToVector(droneMovementDeg), droneHorizontalSpeed);
		
		//transform to vector and scale the it. The vector length should represent the drone speed.
		//The direction in which the wind blows, not from where the wind comes
		windDirection = this.scaleVector(this.degToVector(windDirectionDeg), windSpeed);
		
		//Add the vertical drone speed to the vector
		droneMovementVec = this.add(droneMovementVec, new Vector3d(0, droneVerticalSpeed, 0));
		
		//compute the absolute sensor direction
		absSensorDirection = this.mimicTransformation(this.defaultDroneHeading, droneHeadingVec, this.relativeDirection);
		
		//compute the wind strength and direction thats experienced by the drone.
		//It depends on the wind and the drone movement. 
		//If the drone moves in the same direction as the wind blows and moves in the same speed, no
		//wind is experienced.
		Vector3d trueWindVec = this.sub(windDirection, droneMovementVec);
		
		//Define the sensor plane:
		//To define the measured wind vector, we need to describe the plain with two orthogonal vectors
		//which are part of the plain. The absSensorDirection is the plain's normal vector and must be orthogonal
		//to the to plain vectors
		Vector3d[] plane = new Vector3d[2];
		if(Double.compare(absSensorDirection.getX(), 0.0) == 0 && 
				Double.compare(absSensorDirection.getY(), 0.0) == 0) {
			//for (0,0,z), this will always be a orthogonal vector.
			//We have to handle this special case because the else-case would give us (0,0,0) which is not 
			//a valid plain vector
			plane[0] = new Vector3d(1, 0, 0);
		} else {
			//for all (x,y,z) with !(x==0 && y==0), the vector (y,-x,0) is a valid orthogonal plain vector
			plane[0] = this.scaleVector(new Vector3d(absSensorDirection.getY(), -absSensorDirection.getX(), 0), 1);
		}
		
		//The second plain vector can be computed with the cross product
		plane[1] = new Vector3d(
					absSensorDirection.getY() * plane[0].getZ() - absSensorDirection.getZ() * plane[0].getY(),
					absSensorDirection.getZ() * plane[0].getX() - absSensorDirection.getX() * plane[0].getZ(),
					absSensorDirection.getX() * plane[0].getY() - absSensorDirection.getY() * plane[0].getX()
				);
		plane[1] = this.scaleVector(plane[1], 1);
		
		//Now the trueWindVec can be projected onto our plain
		Vector3d measuredWindVec = this.add(
				this.multiply(plane[0], this.scalarProduct(trueWindVec, plane[0])),
				this.multiply(plane[1], this.scalarProduct(trueWindVec, plane[1]))
				);
		
		
		//The Sensor can't just return the absolute direction, so it must be transformed to degrees starting
		//from the default direction
		//The absolute default direction at first be computed from the relative firections
		Vector3d absZeroDegreeVec = this.mimicTransformation(this.defaultDroneHeading, droneHeadingVec, this.zeroDegreeDirection);
		Vector3d absNintyDegreeVec = this.mimicTransformation(this.defaultDroneHeading, droneHeadingVec, this.nintyDegreeDirection);
		
		if(Double.compare(this.getLength(measuredWindVec), 0) == 0) {
			//If no wind is measured, we return a default value
			return "{\"direction\": -1, \"strength\": 0}";
		} else {
			double measuredWindAngle;
			
			//We compute the angle between the measured wind direction and the default direction. 
			//This will always give us an angle <180 degrees
			double smallAngle = Math.acos( 
					this.scalarProduct(measuredWindVec, absZeroDegreeVec) / 
					(this.getLength(measuredWindVec) * this.getLength(absZeroDegreeVec))
					);
			
			//But the true angle could be >180. In that case, our 90-degree-vector and the wind vector must have
			//an angle of more than 90 degrees
			double angleToNintyDegreeDirection = Math.acos( 
					this.scalarProduct(measuredWindVec, absNintyDegreeVec) / 
					(this.getLength(measuredWindVec) * this.getLength(absNintyDegreeVec))
					);
			
			if(Double.compare(angleToNintyDegreeDirection, 0.5 * Math.PI) > 0) {
				measuredWindAngle = 360 - Math.toDegrees(smallAngle);
			} else {
				measuredWindAngle = Math.toDegrees(smallAngle);
			}
			
			return "{\"direction\": " + Double.toString(measuredWindAngle) + ", \"strength\": " + Double.toString(this.getLength(measuredWindVec)) + "}";
		}
	}
	
	/**
	 * When v0AfterTransformation is a vector that can be optained when v0BeforeTransmission is rotated 
	 * around y-axis and afterwards around x-Axis, these transformations are computed and executed on the
	 * v1 vector. The v1 after these transformations are returned. So the relative position of v1 and 
	 * v0BeforeTransformation is equivalent to v0AfterTransmission and the returned vector.
	 * More formally:
	 * 		Transformation(v0BeforeTransformation) -> v0AfterTransformation
	 * 		Transformation(v1) -> result of this method
	 * 
	 * @param v0BeforeTransformation
	 * @param v0AfterTransformation
	 * @param v1
	 * @return v1AfterTransmission
	 */
	private Vector3d mimicTransformation(Vector3d v0BeforeTransformation, Vector3d v0AfterTransformation, Vector3d v1) {
		//The vector v0BeforeTransformation rotated around the y-Axis, then the x-Axis to be transformed into
		//v0AfterTransformation. We need to compute the angle between v0AfterTransformation and the xy-plain,
		//rotate it back into xy-plain (so we get the vector v') and compute the angle between 
		//v' and v0BeforeTransformation. With this to angles, we can transform v1 in the same way 
		//v0AfterTransformation was transformed.
		
		//At first we compute the angle between v0AfterTransformation and the xy-plain
		Vector3d normalVecXZPlane = new Vector3d(0, 1, 0);
		double rotationBackToXZPlane = Math.abs(
			Math.asin(
				this.scalarProduct(v0AfterTransformation, normalVecXZPlane) / this.getLength(v0AfterTransformation)
			)
		);
			
		//If exactly one of x and y is negative, we take the negativ angle. This will be important regarding
		//to the rotation matrix.
		if(v0AfterTransformation.getY() * v0AfterTransformation.getX() > 0) {
			rotationBackToXZPlane*= -1;
		}
		
		//So we take the v0AfterTransformation and rotate it around the x-Axis with our computed angle.
		//(We don't have the complete rotation matrix, but the computation you get if you'd multiply 
		//the vector and the matrix)
		Vector3d vecInXYPlane = new Vector3d(
			v0AfterTransformation.getX(), 
			0, 
			-v0AfterTransformation.getY()*Math.sin(rotationBackToXZPlane) + v0AfterTransformation.getZ()*Math.cos(rotationBackToXZPlane)
		);
		
		//We now compute the angle between the vector in xy-plain and v0BeforeTransformation
		double rotationBackToDefault = Math.abs(
			Math.acos(
				this.scalarProduct(vecInXYPlane, v0BeforeTransformation) / this.getLength(vecInXYPlane)
			)
		);
		
		//Now we have to take the negativ angle if z is negative regardiing to the rotation matrix.
		if(vecInXYPlane.getZ() < 0) {
			rotationBackToDefault*= -1;
		}
		
		//So now we have both angles we need.
		
		//Now we have to invert the angles. The convertion into degrees is because of the better modulo.
		double rotationYAxis = Math.toRadians((360-Math.toDegrees(rotationBackToDefault)) % 360);
		double rotationXAxis = Math.toRadians((360-Math.toDegrees(rotationBackToXZPlane)) % 360);
		
		//Rotate the v1 around the y-Axis
		Vector3d vecSensorYRotation = new Vector3d(
			v1.getX()*Math.cos(rotationYAxis) + v1.getZ()*Math.sin(rotationYAxis), 
			v1.getY(),
			-v1.getX()*Math.sin(rotationYAxis) + v1.getZ()*Math.cos(rotationYAxis)
		);
		
		//Rotate the v1 around x-Axis
		Vector3d absSensorDirection = new Vector3d(
			vecSensorYRotation.getX(),
			vecSensorYRotation.getY()*Math.cos(rotationXAxis) + vecSensorYRotation.getZ()*Math.sin(rotationXAxis),
			-vecSensorYRotation.getY()*Math.sin(rotationXAxis) + vecSensorYRotation.getZ()*Math.cos(rotationXAxis)
		);
		
		return absSensorDirection;
	}
	
	/**
	 * Converts the angles obtained from the wind interface into a 3d vector
	 * @param deg
	 * @return a 3d vector
	 */
	private Vector3d degToVector(double deg) {
		//TODO: which direction is 0 deg?
		return new Vector3d(Math.cos(Math.toRadians(deg)),
				   			0,
				   			-Math.sin(Math.toRadians(deg)));
	}
	
	/**
	 * Computes the scalar product between v0 and v1
	 * @param v0
	 * @param v1
	 * @return
	 */
	private double scalarProduct(Vector3d v0, Vector3d v1) {
		return v0.getX() * v1.getX() + 
			   v0.getY() * v1.getY() + 
			   v0.getZ() * v1.getZ();
	}
	
	/**
	 * multiplies a factor to a vector
	 * @param vector
	 * @param factor
	 * @return the new vector
	 */
	private Vector3d multiply(Vector3d vector, double factor) {
		return new Vector3d(vector.getX() * factor,
						    vector.getY() * factor,
				            vector.getZ() * factor);
	}
	
	/**
	 * adds to vectors
	 * @param v0
	 * @param v1
	 * @return the new vector
	 */
	private Vector3d add(Vector3d v0, Vector3d v1) {
		return new Vector3d(v0.getX() + v1.getX(),
							v0.getY() + v1.getY(),
							v0.getZ() + v1.getZ());
	}
	
	/**
	 * subtracts v1 from v0
	 * @param v0
	 * @param v1
	 * @return the new vector
	 */
	private Vector3d sub(Vector3d v0, Vector3d v1) {
		return new Vector3d(v0.getX() - v1.getX(),
							v0.getY() - v1.getY(),
							v0.getZ() - v1.getZ());
	}
	
	/**
	 * scales a vector to a specific length
	 * @param vector
	 * @param newLength
	 * @return the new vector
	 */
	private Vector3d scaleVector(Vector3d vector, double newLength) {
		double currentLength = Math.sqrt(
				Math.pow(vector.getX(), 2) + 
				Math.pow(vector.getY(), 2) + 
				Math.pow(vector.getZ(), 2));
		
		if(Double.compare(currentLength, 0) == 0) {
			return new Vector3d(0,0,0);
		}
		double scaleFactor = newLength / currentLength;
		
		return this.multiply(vector, scaleFactor);
	}
	
	/**
	 * Gets the length of a vector
	 * @param vector
	 * @return
	 */
	private double getLength(Vector3d vector) {
		return Math.sqrt(
					Math.pow(vector.getX(), 2) +
					Math.pow(vector.getY(), 2) +
					Math.pow(vector.getZ(), 2)
				);
	}

	@Override
	public String getType() {
		return "WindSensor";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SensorConfig saveToConfig() {
		// TODO
		return null;
	}

	@Override
	public void runMeasurement() {
	// TODO
	}

	@Override
	public SensorResultDto getLastMeasurement() {
		// TODO
		return null;
	}
}
