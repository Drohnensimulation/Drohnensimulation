package de.thi.dronesim.sensor;

import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.drone.Drone;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import java.util.Random;
import java.util.Set;

public abstract class ASensor {
	
	//TODO: -Beschreibung des Kegels
	//		-Vervollständigung der Methodenimplementierung
	
	protected Drone drone;
	protected float range; // range from sensor position to cone bottom
	protected float coneHeight; // range from Cone origin point to cone bottom
	protected float sensorAngle;
	protected Vector3f vectorAngle;
	protected float sensorRadius;
	protected float measurementAccuracy;
	
	//Relative Ausrichtung zur Drohne.
	//Eine Drehung der Drohne hat keinen Einfluss auf die folgenden Werte.
	//Ausrichtung nach vorne (in Kopfrichtung): (x,y,z) = (1,0,0)
	//Ausrichtung nach senkrecht nach oben: (x,y,z) = (0,1,0)
	//Ausrichtung von Kopfrichtung nach links: (x,y,z) = (0,0,1)
	//Bildliche Vorstellung: Drohne schaut in x-Achsen-Richtung
	protected float directionX;
	protected float directionY;
	protected float directionZ;
	
	//Relative Anordnung zum Drohnenmittelpunkt.
	//Eine Drehung der Drohne hat keinen Einfluss auf die folgenden Werte.
	//Eine Einheit in Kopfrichtung bewegen: (x,y,z) = (1,0,0)
	//Eine Einheit nach oben bewegen: (x,y,z) = (0,1,0)
	//Eine Einheit von Kopfrichtung aus nach links bewegen: (x,y,z) = (0,0,1)
	//Bildliche Vorstellung: Drohne schaut in x-Achsen-Richtung
	protected float posX; 
	protected float posY;
	protected float posZ;
		
	public void initSensor() {
		this.drone = null;
		this.range = 1.0f;
		this.sensorAngle = 45.0f;
		this.sensorRadius = 1.0f;
		this.measurementAccuracy = 0.0f;
		this.directionX = 0.0f;
		this.directionY = 0.0f;
		this.directionZ = 0.0f;
		this.posX = 0.0f;
		this.posY = 0.0f;
		this.posZ = 0.0f;
	}
	
	/**
	 * Gibt den Namen oder Typ des Sensors zurück (Infrarot, Ultraschall, ...)
	 * @return Name oder Typ
	 */
	public abstract String getType();

	public void addToDrone(Drone drone) {
		if(drone == null) {
			throw new NullPointerException("Drone must not be null!");
		}
		this.drone = drone;
	}
	
	/**
	 * Entfernt den Sensor von der Drohne, falls er einer zugeordnet war.
	 */
	public void removeFromDrone() {
		this.drone = null;
	}

	/**
	 * Gibt die Drohne zurück, an der der Sensor montiert ist.
	 * @return Drohne
	 */
	public Drone getDrone() {
		return this.drone;
	}
	
	/**
	 * Relative Ausrichtung zur Drohne.
	 * Eine Drehung der Drohne hat keinen Einfluss auf die folgenden Werte.
	 * Ausrichtung nach vorne (in Kopfrichtung): (x,y,z) = (1,0,0)
	 * Ausrichtung nach senkrecht nach oben: (x,y,z) = (0,0,1)
	 * Ausrichtung von Kopfrichtung nach links: (x,y,z) = (0,1,0)
	 * Bildliche Vorstellung: Drohne schaut in x-Achsen-Richtung
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setDirection(float x, float y, float z) {
		this.directionX = x;
		this.directionY = y;
		this.directionZ = z;
	}
	
	/**
	 * Relative Anordnung zum Drohnenmittelpunkt.
	 * Eine Drehung der Drohne hat keinen Einfluss auf die folgenden Werte.
	 * Eine Einheit in Kopfrichtung bewegen: (x,y,z) = (1,0,0)
	 * Eine Einheit nach oben bewegen: (x,y,z) = (0,1,0)
	 * Eine Einheit von Kopfrichtung aus nach links bewegen: (x,y,z) = (0,0,1)
	 * Bildliche Vorstellung: Drohne schaut in x-Achsen-Richtung
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}
	
	/**
	 * Ändert die Ausrichtung des Sensors
	 * @param deltaX Änderung der x-Richtung
	 * @param deltaY Änderung der y-Richtung
	 * @param deltaZ Änderung der z-Richtung
	 */
	public void changeDirection(double deltaX, double deltaY, double deltaZ) {
		this.directionX+= deltaX;
		this.directionY+= deltaY;
		this.directionZ+= deltaZ;
	}
	
	/**
	 * Ändert die Ausrichtung des Sensors
	 * @param deltaX Verschiebung in x-Richtung
	 * @param deltaY Verschiebung in y-Richtung
	 * @param deltaZ Verschiebung in z-Richtung
	 */
	public void changePosition(double deltaX, double deltaY, double deltaZ) {
		this.posX+= deltaX;
		this.posY+= deltaY;
		this.posZ+= deltaZ;
	}
	
	public double getDirectionX() {
		return this.directionX;
	}
	
	public double getDirectionY() {
		return this.directionY;
	}
	
	public double getDirectionZ() {
		return this.directionZ;
	}
	
	public double getPositionX() {
		return this.posX;
	}
	
	public double getPositionY() {
		return this.posY;
	}
	
	public double getPositionZ() {
		return this.posZ;
	}

	/**
	 * Gibt die Reichweite des Sensors zurück.
	 * @return Reichweite des Sensors
	 */
	public double getRange() {
		return this.range;
	}

	/**
	 * Setzt die Reichweite
	 * @param range
	 */
	protected void setRange(float range) {
		if(Double.compare(range, 0.0) <= 0) {
			throw new IllegalArgumentException("Range must be greater than zero!");
		}
		this.range = range;
	}

	/**
	 * Gibt den Radius der Sensorfläche in Metern zurück
	 * @return
	 */
	public double getSize() {
		return this.sensorRadius;
	}
	
	/**
	 * Setzt den Radius der Sensorfläche in Metern
	 * @param size Radius der Sensorfläche in Metern
	 */
	protected void setSize(float size) {
		if(Double.compare(size, 0.0) <= 0) {
			throw new IllegalArgumentException("Size must be greater than zero!");
		}
		this.sensorRadius = size;
	}
	
	/**
	 * calculates the lenght from origin point to range end. This length is needed to
	 * create the cone-object. This Method is needed in the "check"-methods (UfoObjs.java)
	 * 
	 * @author: Moris Breitenborn
	 *  
	 * @return float
	 */
	
	public float getConeHeight() {
		
		return this.range + originToPositionLength();
	}
	
	/**
	 * calculates the length from origin point to sensor position end. This length is needed to
	 * create the cone-object 
	 * 
	 * @author: Moris Breitenborn
	 *  
	 * @return float
	 */
	
	public float originToPositionLength(){
		float hypoLength = (float) (this.range/Math.cos(Math.toRadians(this.sensorAngle)));
		// use the Pythagorean theorem to calculate bottomShort
		float bottomShort  = (float) Math.sqrt((hypoLength*hypoLength)-(this.range*this.range));
		// the relation from range to bottomShort is equaly to the relation bottomLong to range+x. 
		// x = the length between the sensor origin point and the sensor position point
		float bottomLong = bottomShort + this.sensorRadius;
		float originToPositionLength = (bottomLong*this.sensorRadius)/bottomShort;
		return originToPositionLength;
	}
	
	/**
	 * calculate the origin vector from drone center to sensor cone origin point
	 * by using intercept theorems.
	 * 
	 * @author: Moris Breitenborn
	 *  
	 * @return Vector3f
	 */
	public Vector3f getOrigin() {
		
		// normalzie the vector to multiplie it with the range and get the neede vector
		Vector3f normalziedOrientationVector = getOrientation().normalize();
		
		float originToPositionLength = originToPositionLength();
		// rotate the normalziedOrientation vector in the opposit direction with scale(-1)
		normalziedOrientationVector = normalziedOrientationVector.mult(-1);
		// get the normalized Vector on the calculated length with scale(originToPositionLength)
		normalziedOrientationVector = normalziedOrientationVector.mult(originToPositionLength);
		// with this vector we can calculate the origing point by simply adding the normalziedOrientationVector
		// to the position point of the Sensor
		float originPointX = this.posX + normalziedOrientationVector.getX();
		float originPointY = this.posY + normalziedOrientationVector.getY();
		float originPointZ = this.posZ + normalziedOrientationVector.getZ();
		//calculate vector from drone center to originPoint and return
		Vector3f origin = new Vector3f(originPointX, originPointY, originPointZ);
	
		return origin;
		
	}
	
	/**
	 * calculate the direction vector in with the sensor is pointing to. 
	 * 
	 * @author: Moris Breitenborn
	 * 
	 * @return Vector3f
	 */
	public Vector3f getOrientation() {
		
		float directionVectorX = this.directionX -  this.posX;
		float directionVectorY = this.directionY -  this.posY;
		float directionVectorZ = this.directionZ -  this.posZ;
		Vector3f directionVector = new Vector3f(directionVectorX, directionVectorY, directionVectorZ); 
		return directionVector;
		
	}
	
	/** 
	 * This method calculates a vector that is lying on the cone surface. This vector is needed 
	 * to calculate the entire cone.
	 * 
	 * @author: Moris Breitenborn
	 * 
	 * @return Vector3f
	 */
	
	public Vector3f getVectorAngel() {
		
		//Get the Vector of the Sensor orientation
		Vector3f directionVector = getOrientation();
		
		//Calculate the rotation angle to rotate the directionVector in to the XY-level
		float rotXY = (float) Math.atan((directionVector.getZ()/directionVector.getY())*(-1));
		//calculate all necessary variable for the rotation matrix and create matrix
		float cosPhi= (float) Math.cos(rotXY);
		float sinPhi= (float) Math.sin(rotXY);
		float minSinPhi= (float) (Math.sin(rotXY)*(-1));
		Matrix3f transformMatrixX = new Matrix3f(1, 0, 0 ,0, cosPhi, minSinPhi, 0, sinPhi, cosPhi);
		//multiply the matrix with the vector 
		Vector3f vectorXY = transformMatrixX.mult(directionVector);
	
		//To get the angle between the vectorXY and the x-Axses we call the function checkAngel();
		Vector3f xAxsis = new Vector3f(1,0,0);
		//give the angle the right operator to calculate the right vector. calculate variables and matrix
		float rotX = checkAngel(vectorXY, xAxsis);
		if(directionVector.getY()>0) {
			rotX=rotX*(-1);
		}
		cosPhi= (float) Math.cos(rotX);
		sinPhi= (float) Math.sin(rotX);
		minSinPhi= (float) (Math.sin(rotX)*(-1));
		Matrix3f transformMatrixZ = new Matrix3f(cosPhi, minSinPhi, 0, sinPhi, cosPhi, 0, 0, 0, 1); 
        //rotate on x-Axsis
		Vector3f vectorX = transformMatrixZ.mult(vectorXY);
		

		//now we can rotate the vector around the y-Axses 
		float sensorAngleAsRadiant = (float) Math.toRadians(sensorAngle);
		cosPhi= (float) Math.cos(sensorAngleAsRadiant);
		sinPhi= (float) Math.sin(sensorAngleAsRadiant);
		minSinPhi= (float) (Math.sin(sensorAngleAsRadiant)*(-1));
		Matrix3f transformMatrixY = new Matrix3f(cosPhi, 0, sinPhi, 0, 1, 0, minSinPhi, 0, cosPhi);
		Vector3f vectorWithAngel = transformMatrixY.mult(vectorX);
		
		//rerotate the new vectors with all used angles. startt with the last one used 
		rotX = rotX*(-1);
		cosPhi= (float) Math.cos(rotX);
		sinPhi= (float) Math.sin(rotX);
		minSinPhi= (float) (Math.sin(rotX)*(-1));
		transformMatrixZ = new Matrix3f(cosPhi, minSinPhi, 0, sinPhi, cosPhi, 0, 0, 0, 1); 
		Vector3f vectorWithAngelXY = transformMatrixZ.mult(vectorWithAngel);
		//Rotation at Y
		rotXY = rotXY*(-1);
		cosPhi= (float) Math.cos(rotXY);
		sinPhi= (float) Math.sin(rotXY);
		minSinPhi= (float) (Math.sin(rotXY)*(-1));
		transformMatrixX = new Matrix3f(1, 0, 0 ,0, cosPhi, minSinPhi, 0, sinPhi, cosPhi);
		vectorAngle = transformMatrixX.mult(vectorWithAngelXY);
		
		
		return vectorAngle;
		
	}
	/**
	 *Returns the angle between two vectors
	 * 
	 * @author: Moris Breitenborn
	 * 
	 * @return float
	 */
	
	public float checkAngel(Vector3f original, Vector3f calculated) {
		float x1 = original.getX();
		float y1 = original.getY();
		float z1 = original.getZ();
		float x2 = calculated.getX();
		float y2 = calculated.getY();
		float z2 = calculated.getZ();
		
		float nenner= x1*x2+y1*y2+z1*z2;
		float zaeler= (float) (Math.sqrt(x1*x1+y1*y1+z1*z1)*Math.sqrt(x2*x2+y2*y2+z2*z2));
		float ergebnis = (float) Math.acos(nenner/zaeler);
		return ergebnis;
	}
	
	

	/**
	 * Legt den horizontalen Bildwinkel des Sensors im Gradmaß fest
	 * @param deg Gültiges Interval [0;90]
	 */
	protected void setAngleOfViewHorizontal(float deg) {
		if(Double.compare(deg, 0.0) < 0) {
			throw new IllegalArgumentException("Horizontal angle of view must not be less than zero!");
		}
		if(Double.compare(deg, 90.0) > 0) {
			throw new IllegalArgumentException("Horizontal angle of view must not be greater than 90!");
		}
		this.sensorAngle = deg;
	}
	
	
	/**
	 * Legt eine Messungenauigkeit fest (soll das von außerhalb möglich sein?)
	 * @param accuracy Bsp.: 0.5 -> Entfernung wird in 0,5er-Schritten gemessen,
	 * 						10.0 -> Entfernung wird in 10er-Schritten gemessen,
	 * 					 	 0.0 -> Es wird der exakte (ungerundete) Entfernungswert zurückgegeben,
	 * 			Double.MAX_VALUE -> Es wird nur zurückgegeben, ob der Sensor ein Hindernis sieht (1) oder nicht (0)
	 */
	protected void setMeasurementAccuracy(float accuracy) {
		if(Double.compare(accuracy, 0.0) < 0) {
			throw new IllegalArgumentException("Accuracy may not be less than zero!");
		}
		this.measurementAccuracy = accuracy;
	}

	/**
	 *Gets the Rays that hit an Obstacle
	 *
	 * @param origin coords of the drone
	 * @param orientation Drone is heading this direction
	 * @param range sensorrange
	 * @param opening Example: if the angle is 45° the vector would be 	1 (x)
	 *                													0 (y)
	 *                													1 (z)
	 * @return a Set of the rays that hit objects
	 */
	public Set<HitMark> getSensorHits(Vector3f origin, Vector3f orientation, float range, Vector3f opening){

		//returns only dummy data, no real reference to real data
		Set<HitMark> hitMarks = Set.of();
		Random random = new Random();

		origin = new Vector3f(5,5,0);

		Obstacle obstacle1 = new Obstacle();
		Obstacle obstacle2 = new Obstacle();
		Obstacle obstacle3 = new Obstacle();
		Obstacle obstacle = new Obstacle();


		Vector3f relativeHit = new Vector3f(5,7,0);

		float x;
		float y;
		float z;

		for(int i = 0; i<30; i++){
			int xmax;
			int xmin;
			int ymax;
			int ymin;
			if(i<10){
				xmax = 14;
				xmin = 8;
				ymax = 14;
				ymin = 10;
				obstacle = obstacle1;

			}else if(i>10 && i<20){
				xmax = 20;
				xmin = 15;
				ymax = 4;
				ymin = 1;
				obstacle = obstacle2;

			}else{
				xmax = 5;
				xmin = 1;
				ymax = 13;
				ymin = 10;
				obstacle = obstacle3;

			}
			x = Float.parseFloat(Integer.toString(random.nextInt(xmax - xmin + 1) + xmin));
			y = Float.parseFloat(Integer.toString(random.nextInt(ymax - ymin + 1) + ymin));
			z = 3;
			Vector3f worldHit = new Vector3f(x,y,z);
			relativeHit.x = x - origin.x;
			relativeHit.y = y - origin.y;
			relativeHit.z = y - origin.z;
			float distance = relativeHit.length();
			HitMark mark = new HitMark(distance, worldHit, relativeHit, obstacle3);

			hitMarks.add(mark);
		}

		return hitMarks;
	}
	
	/**
	 * Gibt die kürzeste Entfernung in Metern zu einem Objekt an
	 * @return Entfernung in Metern.
	 */
	public double getDistance() {
		double measurement = 0; /*TODO: Call Obstacle Team-Method*/
		
		return this.handleMeasurementAccuracy(measurement);
	}
	
	/**
	 * Versieht die gemessene Entfernung zum Hindernis mit einer Messungenauigkeit
	 * @param measurement die gemessene Entfernung
	 * @return Die Entfernung, versehen mit der Ungenauigkeit
	 */
	private double handleMeasurementAccuracy(double measurement) {
		if(Double.compare(this.measurementAccuracy, Double.MAX_VALUE) == 0) {
			return Double.compare(measurement, 0.0) > 0 ? 1.0 : 0.0;
		} else if(Double.compare(this.measurementAccuracy, 0.0) == 0) {
			return measurement;
		} else {
			return (int)(measurement / this.measurementAccuracy + 0.5) * this.measurementAccuracy;
		}
	}
	
	public void loadFromConfig(SensorConfig config) {
		range = (float) config.getRange();
		sensorAngle = (float) config.getSensorAngle();
		sensorRadius = (float) config.getSensorRadius();
		measurementAccuracy = (float) config.getMeasurementAccuracy();
		directionX = (float) config.getDirectionX();
		directionY = (float) config.getDirectionY();
		directionZ = (float) config.getDirectionZ();
		posX = (float) config.getPosX();
		posY = (float) config.getPosY();
		posZ = (float)config.getPosZ();
	}
	
	public SensorConfig saveToConfig() {
		SensorConfig config = new SensorConfig();
		config.setRange(range);
		config.setSensorAngle(sensorAngle);
		config.setSensorRadius(sensorRadius);
		config.setMeasurementAccuracy(measurementAccuracy);
		config.setDirectionX(directionX);
		config.setDirectionY(directionY);
		config.setDirectionZ(directionZ);
		config.setPosX(posX);
		config.setPosY(posY);
		config.setPosZ(posZ);
		return config;
	}
}
