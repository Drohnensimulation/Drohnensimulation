package de.thi.dronesim.sensor.types;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;

import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.sensor.ISensor;
import de.thi.dronesim.sensor.dto.SensorResultDto;
import de.thi.dronesim.wind.Wind;
import de.thi.dronesim.wind.Wind.CurrentWind;

/**
 * The wind sensor can measure the wind direction and strength for a two dimensional plain.
 * The three dimensional wind will always be projected onto the sensor plain.
 * @author dominik bartl
 *
 */
public class WindSensor implements ISensor {

	//TODO: Replace Vector3 with Vector3f from the jme libary

	private String name;
	private int id;
	
	private SensorResultDto lastMeasurement;
	
	private final Vector3f defaultDroneHeading = new Vector3f(1f, 0f, 0f);
	
	//Sensor direction when drone is looking to defaultDroneHeading
	//It's the normal vector of the plain in which the sensor can measure the wind
	protected Vector3f relativeDirection;
	
	//If the measured wind blows in this direction, the measurement will return an angle of 0 degrees
	protected Vector3f zeroDegreeDirection;
	
	//If the measured wind blows in this direction, the measurement will return an angle of 90 degrees
	protected Vector3f nintyDegreeDirection;
	
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
	 * @param config
	 */
	public WindSensor(SensorConfig config) {
		this.name = "Windsensor";
		this.id = config.getSensorId();
		this.relativeDirection = new Vector3f(config.getDirectionX(), config.getDirectionY(), config.getDirectionZ());
		
		if(Double.compare(this.relativeDirection.x, 0) == 0 &&
		   Double.compare(this.relativeDirection.y, 0) == 0 &&
		   Double.compare(this.relativeDirection.z, 0) == 0) {
			//No direction was set
			throw new IllegalArgumentException("sensorDirection must not be (0,0,0)!");
		}
		
		this.zeroDegreeDirection = new Vector3f(config.getZeroDegreeDirectionX(), config.getZeroDegreeDirectionY(), config.getZeroDegreeDirectionZ());
		this.nintyDegreeDirection = new Vector3f(config.getNintyDegreeDirectionX(), config.getNintyDegreeDirectionY(), config.getNintyDegreeDirectionZ());
		
		if(Double.compare(this.relativeDirection.dot(this.zeroDegreeDirection), 0) != 0 ||
		   Double.compare(this.relativeDirection.dot(this.nintyDegreeDirection), 0) != 0 ||
		   Double.compare(this.nintyDegreeDirection.dot(this.zeroDegreeDirection), 0) != 0) {
			//The three vectors must all have an angle of 90 degrees
			throw new IllegalArgumentException("sensorDirection, zeroDegreeDirection and nintyDegreeDirection must be orthogonal!");
		}
		
		this.lastMeasurement = new SensorResultDto();
		this.lastMeasurement.setSensor(this);
	}
	
	/**
	 * Gets the vector in which the sensor points if the drone heading was (1,0,0)
	 * @return
	 */
	public Vector3f getRelativeSensorDirection() {
		return this.relativeDirection;
	}
	
	/**
	 * If the drone heading was (1,0,0), wind from this direction will lead to a measured wind angle of 0 degrees.
	 * @return
	 */
	public Vector3f getRelativeZeroDegreeDirection() {
		return this.zeroDegreeDirection;
	}
	
	/**
	 * If the drone heading was (1,0,0), wind from this direction will lead to a measured wind angle of 90 degrees.
	 * @return
	 */
	public Vector3f getRelativeNintyDegreeDirection() {
		return this.nintyDegreeDirection;
	}

	/**
	 * Gets the sensor measurement
	 * @return
	 */
	@Override
	public void runMeasurement(SimulationUpdateEvent event) {
		double droneHeadingDeg = event.getDrone().getLocation().getHeading();
//		double droneMovementDeg = event.getDrone().getLocation().getTrack();		
		
//		Should be already done by the location
//		double droneVerticalSpeed = event.getDrone().getLocation().getVerticalSpeed(); //TODO: Speed >0 means drone goes upwards, <0 drone goes downwards => -20 means drone goes down with 20 m/s
		CurrentWind cw = Wind.getWindAt(event.getDrone().getLocation());
		float windDirectionDeg = (float) cw.getWindDirection(); //Direction from where the wind comes
		float windSpeed = (float) cw.getWindSpeed();

		Vector3f droneHeadingVec;
		Vector3f droneMovementVec;
		Vector3f windDirection;
		Vector3f absSensorDirection;

		//Transform to a vector
		droneHeadingVec = this.degToVector(droneHeadingDeg);

		//get Drone-Movement vector. The length must be equal to the speed in m/s
		droneMovementVec = this.toVector3(event.getDrone().getLocation().getMovement());

		//transform to vector and scale the it. The vector length must represent the wind speed in m/s.
		//The direction in which the wind blows, not from where the wind comes
		windDirection = this.degToVector(windDirectionDeg);
		windDirection.multLocal(-1f); //Now it's the direction in which the wind blows
		windDirection.normalize();
		windDirection.multLocal(windSpeed);

		//Should be already done by the location
		//Add the vertical drone speed to the vector
		//droneMovementVec = this.add(droneMovementVec, new Vector3d(0, droneVerticalSpeed, 0));

		//compute the absolute sensor direction
		absSensorDirection = this.mimicTransformation(this.defaultDroneHeading, droneHeadingVec, this.relativeDirection);

		//compute the wind strength and direction thats experienced by the drone.
		//It depends on the wind and the drone movement.
		//If the drone moves in the same direction as the wind blows and moves in the same speed, no
		//wind is experienced.
		Vector3f trueWindVec = (new Vector3f(windDirection)).subtract(droneMovementVec);

		//Define the sensor plane:
		//To define the measured wind vector, we need to describe the plain with two orthogonal vectors
		//which are part of the plain. The absSensorDirection is the plain's normal vector and must be orthogonal
		//to the to plain vectors
		Vector3f[] plane = new Vector3f[2];
		if(Double.compare(absSensorDirection.x, 0.0) == 0 &&
				Double.compare(absSensorDirection.y, 0.0) == 0) {
			//for (0,0,z), this will always be a orthogonal vector.
			//We have to handle this special case because the else-case would give us (0,0,0) which is not
			//a valid plain vector
			plane[0] = new Vector3f(1, 0, 0);
		} else {
			//for all (x,y,z) with !(x==0 && y==0), the vector (y,-x,0) is a valid orthogonal plain vector
			plane[0] = new Vector3f(absSensorDirection.y, -absSensorDirection.x, 0);
			plane[0].normalize();
		}

		//The second plain vector can be computed with the cross product
		plane[1] = new Vector3f(
					absSensorDirection.y * plane[0].z - absSensorDirection.z * plane[0].y,
					absSensorDirection.z * plane[0].x - absSensorDirection.x * plane[0].z,
					absSensorDirection.x * plane[0].y - absSensorDirection.y * plane[0].x
				);
		plane[1].normalize();

		//Now the trueWindVec can be projected onto our plain
		
		Vector3f p0 = new Vector3f(plane[0]);
		p0.multLocal(trueWindVec.dot(plane[0]));
		
		Vector3f p1 = new Vector3f(plane[1]);
		p1.multLocal(trueWindVec.dot(plane[1]));
		
		Vector3f measuredWindVec = (new Vector3f(p0)).add(p1);
		
//		Vector3d measuredWindVec = this.add(
//				this.multiply(plane[0], this.scalarProduct(trueWindVec, plane[0])),
//				this.multiply(plane[1], this.scalarProduct(trueWindVec, plane[1]))
//				);


		//The sensor can't just return the absolute direction (because it cannot know it), so it must be transformed to degrees starting
		//from the relative default direction.
		//The absolute default direction must computed from the relative directions at first
		Vector3f currentZeroDegreeVec = this.mimicTransformation(this.defaultDroneHeading, droneHeadingVec, this.zeroDegreeDirection);
		Vector3f currentNintyDegreeVec = this.mimicTransformation(this.defaultDroneHeading, droneHeadingVec, this.nintyDegreeDirection);

		List<Float> list = new ArrayList<>(2);
		this.lastMeasurement = new SensorResultDto();
		if(Double.compare(measuredWindVec.length(), 0) == 0) {
			//If no wind is measured, we return a default value
			list.add(Float.NaN);
			list.add(0f);
		} else {
			double measuredWindAngle;

			//We compute the angle between the measured wind direction and the default direction.
			//This will always give us an angle <180 degrees
			double smallAngle = Math.acos(
					measuredWindVec.dot(currentZeroDegreeVec) /
					(measuredWindVec.length() * currentZeroDegreeVec.length())
					);

			//But the true angle could be >=180. In that case, our 90-degree-vector and the wind vector must have
			//an angle of more than 90 degrees
			double angleToNintyDegreeDirection = Math.acos(
					measuredWindVec.dot(currentNintyDegreeVec) /
					(measuredWindVec.length() * currentNintyDegreeVec.length())
					);
			
			if(Double.compare(angleToNintyDegreeDirection, 0.5 * Math.PI) > 0) {
				measuredWindAngle = (360 - Math.toDegrees(smallAngle)) % 360;
				//Modulo shouldn't be necessary. But sometimes values that should be equal slip through Double.compare because of a few fraction digits
			} else {
				measuredWindAngle = Math.toDegrees(smallAngle);
			}

			list.add((float) measuredWindAngle);
			list.add((float) measuredWindVec.length());
		}
		this.lastMeasurement.setValues(list);
	}

	/**
	 * When v0AfterTransformation is a vector that can be optained when v0BeforeTransmission is rotated
	 * around y-axis, this transformation is computed and executed on the
	 * v1 vector. The v1 after this transformation is returned. So the relative position of v1 and
	 * v0BeforeTransformation is equivalent to v0AfterTransmission and the returned vector.
	 * More formally:
	 * 		T is a specific transformation operation
	 * 		T(v0BeforeTransformation) == v0AfterTransformation
	 * 		T(v1) == result of this method
	 * 
	 * As long as the Drone won't be able to tile, the single transformation around the y-axis will be sufficient.
	 * Otherwise this method must be expanded with transformations around the x- and z-axis.
	 *
	 * @param v0BeforeTransformation
	 * @param v0AfterTransformation
	 * @param v1
	 * @return v1AfterTransmission
	 */
	private Vector3f mimicTransformation(Vector3f v0BeforeTransformation, Vector3f v0AfterTransformation, Vector3f v1) {
		//The vector v0BeforeTransformation rotated around the y-Axis to be transformed into
		//v0AfterTransformation. We need to compute the angle between
		//v0AfterTransformation and v0BeforeTransformation. With this angle, we can transform v1 in the same way
		//v0AfterTransformation was transformed.
		
		///////////////////////////////////////////////
		//Compute all angles. Right now it's just one
		///////////////////////////////////////////////
		
		//We now compute the angle between the vector in xy-plain (equals v0AfterTransformation) and v0BeforeTransformation
		double rotationBackToDefault = Math.abs(
			Math.acos(
				v0AfterTransformation.dot(v0BeforeTransformation) / v0AfterTransformation.length()
			)
		);

		//Now we have to take the negativ angle if z is negative regarding to the rotation matrix.
		if(Double.compare(v0AfterTransformation.z, 0) < 0) {
			rotationBackToDefault*= -1;
		}

		///////////////////////////////////////////////
		//With all computed angles, we do the inverted 
		//transformations. Right now it's just one.
		///////////////////////////////////////////////
		
		//Now we have to invert the angles. We convert into degrees because of the better modulo.
		double rotationYAxis = Math.toRadians((360-Math.toDegrees(rotationBackToDefault)) % 360);

		//Rotate the v1 around the y-Axis
		Vector3f vecSensorYRotation = new Vector3f(
				(float) (v1.x*Math.cos(rotationYAxis) + v1.z*Math.sin(rotationYAxis)),
			v1.y,
				(float) (-v1.x*Math.sin(rotationYAxis) + v1.z*Math.cos(rotationYAxis))
		);

		return vecSensorYRotation;
	}

	@Override
	public SensorResultDto getLastMeasurement() {
		return this.lastMeasurement;
	}
	
	/**
	 * Converts the angles obtained from the wind interface into a 3d vector
	 * @param deg
	 * @return a 3d vector
	 */
	private Vector3f degToVector(double deg) {
		return new Vector3f((float) Math.sin(Math.toRadians(deg)),
				0,
				(float) Math.cos(Math.toRadians(deg)));
	}
	
	private Vector3f toVector3(Vector3f v) {
		return new Vector3f(v.x, v.y, v.z);
	}

	@Override
	public String getType() {
		return "WindSensor";
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public SensorConfig saveToConfig() {
		SensorConfig config = new SensorConfig();
		config.setClassName(this.getType());
		config.setSensorId(this.getId());
		config.setDirectionX(this.relativeDirection.x);
		config.setDirectionY(this.relativeDirection.y);
		config.setDirectionZ(this.relativeDirection.z);
		config.setZeroDegreeDirectionX(this.zeroDegreeDirection.x);
		config.setZeroDegreeDirectionY(this.zeroDegreeDirection.y);
		config.setZeroDegreeDirectionZ(this.zeroDegreeDirection.z);
		config.setNintyDegreeDirectionX(this.nintyDegreeDirection.x);
		config.setNintyDegreeDirectionY(this.nintyDegreeDirection.y);
		config.setNintyDegreeDirectionZ(this.nintyDegreeDirection.z);
		return config;
	}

	public boolean equals(ISensor sensor){
		return this.getId() == sensor.getId();
	}
}
