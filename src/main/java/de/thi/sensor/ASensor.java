package de.thi.sensor;

import de.thi.ufo.Drone;

public abstract class ASensor {
	
	//TODO: -Beschreibung des Kegels
	//		-Vervollständigung der Methodenimplementierung
	
	protected Drone drone;
	protected double range;
	protected double angleOfViewHorizontal;
	protected double angleOfViewVertical;
	protected double sensorRadius;
	protected double measurementAccuracy;
	
	//Relative Ausrichtung zur Drohne.
	//Eine Drehung der Drohne hat keinen Einfluss auf die folgenden Werte.
	//Ausrichtung nach vorne (in Kopfrichtung): (x,y,z) = (1,0,0)
	//Ausrichtung nach senkrecht nach oben: (x,y,z) = (0,0,1)
	//Ausrichtung von Kopfrichtung nach links: (x,y,z) = (0,1,0)
	//Bildliche Vorstellung: Drohne schaut in x-Achsen-Richtung
	protected double directionX;
	protected double directionY;
	protected double directionZ;
	
	//Relative Anordnung zum Drohnenmittelpunkt.
	//Eine Drehung der Drohne hat keinen Einfluss auf die folgenden Werte.
	//Eine Einheit in Kopfrichtung bewegen: (x,y,z) = (1,0,0)
	//Eine Einheit nach oben bewegen: (x,y,z) = (0,0,1)
	//Eine Einheit von Kopfrichtung aus nach links bewegen: (x,y,z) = (0,1,0)
	//Bildliche Vorstellung: Drohne schaut in x-Achsen-Richtung
	protected double posX; 
	protected double posY;
	protected double posZ;
		
	public void initSensor() {
		this.drone = null;
		this.range = 1.0;
		this.angleOfViewHorizontal = 0.0;
		this.angleOfViewVertical = 0.0;
		this.sensorRadius = 1.0;
		this.measurementAccuracy = 0.0;
		this.directionX = 0.0;
		this.directionY = 0.0;
		this.directionZ = 0.0;
		this.posX = 0.0;
		this.posY = 0.0;
		this.posZ = 0.0;
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
	public void setDirection(double x, double y, double z) {
		this.directionX = x;
		this.directionY = y;
		this.directionZ = z;
	}
	
	/**
	 * Relative Anordnung zum Drohnenmittelpunkt.
	 * Eine Drehung der Drohne hat keinen Einfluss auf die folgenden Werte.
	 * Eine Einheit in Kopfrichtung bewegen: (x,y,z) = (1,0,0)
	 * Eine Einheit nach oben bewegen: (x,y,z) = (0,0,1)
	 * Eine Einheit von Kopfrichtung aus nach links bewegen: (x,y,z) = (0,1,0)
	 * Bildliche Vorstellung: Drohne schaut in x-Achsen-Richtung
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(double x, double y, double z) {
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
	protected void setRange(double range) {
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
	protected void setSize(double size) {
		if(Double.compare(size, 0.0) <= 0) {
			throw new IllegalArgumentException("Size must be greater than zero!");
		}
		this.sensorRadius = size;
	}
	
	/**
	 * Gibt den Bildwinkel des Sensors in horizontaler Richtung im Gradmaß zurück
	 * @return Bildwinkel im Gradmaß
	 */
	public double getAngleOfViewHorizontal() {
		return this.angleOfViewHorizontal;
	}

	/**
	 * Legt den horizontalen Bildwinkel des Sensors im Gradmaß fest
	 * @param deg Gültiges Interval [0;90]
	 */
	protected void setAngleOfViewHorizontal(double deg) {
		if(Double.compare(deg, 0.0) < 0) {
			throw new IllegalArgumentException("Horizontal angle of view must not be less than zero!");
		}
		if(Double.compare(deg, 90.0) > 0) {
			throw new IllegalArgumentException("Horizontal angle of view must not be greater than 90!");
		}
		this.angleOfViewHorizontal = deg;
	}
	
	/**
	 * Gibt den Bildwinkel des Sensors in vertikaler Richtung im Gradmaß zurück
	 * @return Bildwinkel im Gradmaß
	 */
	public double getAngleOfViewVertical() {
		return this.angleOfViewVertical;
	}

	/**
	 * Legt den vertikalen Bildwinkel des Sensors im Gradmaß fest
	 * @param deg Gültiges Interval [0;90]
	 */
	protected void setAngleOfViewVertical(double deg) {
		if(Double.compare(deg, 0.0) < 0) {
			throw new IllegalArgumentException("Vertical angle of view must not be less than zero!");
		}
		if(Double.compare(deg, 90.0) > 0) {
			throw new IllegalArgumentException("Vertical angle of view must not be greater than 90!");
		}
		this.angleOfViewVertical = deg;
	}
	
	/**
	 * Legt eine Messungenauigkeit fest (soll das von außerhalb möglich sein?)
	 * @param accuracy Bsp.: 0.5 -> Entfernung wird in 0,5er-Schritten gemessen,
	 * 						10.0 -> Entfernung wird in 10er-Schritten gemessen,
	 * 					 	 0.0 -> Es wird der exakte (ungerundete) Entfernungswert zurückgegeben,
	 * 			Double.MAX_VALUE -> Es wird nur zurückgegeben, ob der Sensor ein Hindernis sieht (1) oder nicht (0)
	 */
	protected void setMeasurementAccuracy(double accuracy) {
		if(Double.compare(accuracy, 0.0) < 0) {
			throw new IllegalArgumentException("Accuracy may not be less than zero!");
		}
		this.measurementAccuracy = accuracy;
	}
	
	/**
	 * Gibt die kürzeste Entfernung in Metern zu einem Objekt an
	 * @return Entfernung in Metern.
	 */
	public double getDistance() {
		double measurement = 0; /*TODO: Call Obstacle Team-Method*/;
		
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
	
	public void loadFromJSON(String filepath) {
		//TODO
	}
	
	public void saveToJSON(String filepath) {
		//TODO
	}
}
