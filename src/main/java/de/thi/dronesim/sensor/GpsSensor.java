package de.thi.dronesim.sensor;

import de.thi.dronesim.drone.Drone;

public class GpsSensor {
	//How long the measured values should be delayed until they are displayed
	private final int measurementDelayInMS = 100;
	
	private final int horizontalNoise = 5;
	private final int verticalNoise = 10;
	
	//The approx horizontal and vertical speed is the avg measured speed over the following time
	private final int hSpeedObservationTimeInMS = this.horizontalNoise * 500;
	private final int vSpeedObservationTimeInMS = this.verticalNoise * 500;
	
	private final List<Long, Coordinates> measurements;
	private final List<Long, Float> lastHorizontalDistanceDeltas;
	private final List<Long, Float> lastVerticalDistanceDeltas;
	
	private Coordinates posLastFrame;
	
	//approx speed in meters per second
	private Float hSpeed = null;
	private Float vSpeed = null;
	
	private String lastResult;
	
	private Drone drone;
	
	/**
	 * Creates a new GPS Sensor
	 */
	public GpsSensor() {
		this.measurements = new List<>();
		this.lastHorizontalDistanceDeltas = new List<>();
		this.lastVerticalDistanceDeltas = new List<>();
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
	 * Runs the measurement for gps coordinates and approximates the drone's speed.
	 * Must be called every frame 
	 */
	public void runMeasurement() {
		float xCoord = 0;
		float yCoord = 0;
		float zCoord = 0;
		//TODO: get Coordinates from Drone
	
		xCoord = this.addNoise(xCoord, this.horizontalNoise);
		zCoord = this.addNoise(zCoord, this.horizontalNoise);
		yCoord = this.addNoise(yCoord, this.verticalNoise);
		
		long currentTime = System.currentTimeMillis();
		
		//Adds the measurement to the queue
		this.measurements.addBack(currentTime, new Coordinates(xCoord, yCoord, zCoord));
		
		//Build the result string
		StringBuilder resultString = new StringBuilder();
		resultString.append("\"pos\": {");
		//Get the measurement from the queue that was delayed long enough
		Coordinates vals = this.getDelayedMeasurementAndClearEntries(this.measurements, currentTime, this.measurementDelayInMS);
		if(vals == null) {
			resultString.append("\"x\": \"NaN\", \"y\": \"NaN\", \"z\": \"NaN\"");
		} else {
			resultString.append("\"x\": ").append(vals.x)
			.append(", \"y: \"").append(vals.y)
			.append(", \"z: \"").append(vals.z);
		}
		resultString.append("}, \"speed\": {");
	
		//calculate the position deltas relative to the positions last frame
		if(vals != null) {
			if(this.posLastFrame != null) {
				this.lastHorizontalDistanceDeltas.addBack(currentTime, 
						(float)Math.sqrt(Math.pow(vals.x-posLastFrame.x, 2) + Math.pow(vals.z-posLastFrame.z, 2)));
				this.lastVerticalDistanceDeltas.addBack(currentTime, Math.abs(vals.y-this.posLastFrame.y));
			}
			this.posLastFrame = new Coordinates(vals.x, vals.y, vals.z);
		}
		
		//approx the speed
		Float newHorizontalSpeed = this.getApproxSpeedAndClearEntries(lastHorizontalDistanceDeltas, 
				currentTime, this.hSpeedObservationTimeInMS);
		if(newHorizontalSpeed != null) {
			this.hSpeed = newHorizontalSpeed;
		}
		
		Float newVerticalSpeed = this.getApproxSpeedAndClearEntries(lastVerticalDistanceDeltas, 
				currentTime, this.vSpeedObservationTimeInMS);
		if(newVerticalSpeed != null) {
			this.vSpeed = newVerticalSpeed;
		}
		
		//append speed to result string
		resultString.append("\"approxHorizontalSpeed\": ").append("\"" + (this.hSpeed != null ? this.hSpeed : "NaN") + " m/s\"")
			.append(", \"approxVerticalSpeed\": ").append("\"" + (this.vSpeed != null ? this.vSpeed : "NaN") + " m/s\"");
		
		this.lastResult = resultString.toString();
	}
	
	/**
	 * Gets the result from the last measurement
	 * @return
	 */
	public String getLastResult() {
		return this.lastResult;
	}
	
	/**
	 * Gets the latest measurement from the queue that was delayed long enough. This and all older measurements 
	 * will be removed from queue.
	 * @param list the queue
	 * @param currentTime
	 * @param delay time in MS a measurement must be delayed
	 * @return The coordinates of the measurements
	 */
	private Coordinates getDelayedMeasurementAndClearEntries(List<Long, Coordinates> list, long currentTime, int delay) {
		Coordinates last = null;
		boolean run = true;
		while(run && !list.isEmpty()) {
			Pair<Long, Coordinates> pair = list.getFront();
			if((int)(currentTime - pair.first) >= delay) {
				last = pair.second;
				list.removeFront();
			} else {
				run = false;
			}
		}
		
		return last;
	}
	
	/**
	 * Calculates the avarage speed from the position deltas over a given time period. Entries out of the period
	 * will be removed from the list.
	 * @param posDeltas 
	 * @param currentTime
	 * @param observationTime the time period
	 * @return the aprox speed in meters per second
	 */
	private Float getApproxSpeedAndClearEntries(List<Long, Float> posDeltas, long currentTime, int observationTime) {
		if(posDeltas.isEmpty() || (int)(currentTime - posDeltas.getFront().first) < observationTime) {
			return null;
		}
		
		float sumDistance = 0;
		List<Long, Float>.Iterator<Long, Float> it = posDeltas.getIterator();
		while(it.hasNext()) {
			sumDistance+= it.getNext().second;
		}
		float timeDif = currentTime - posDeltas.getFront().first;
		
		boolean delete = true;
		while(delete && !posDeltas.isEmpty()) {
			if((int)(currentTime - posDeltas.getFront().first) > observationTime) {
				posDeltas.removeFront();
			} else {
				delete = false;
			}
		}
		
		return sumDistance / timeDif * 1000;
	}
	
	/**
	 * Adds a noise to a value
	 * @param val
	 * @param maxNoise
	 * @return
	 */
	private float addNoise(float val, int maxNoise) {
		return (int)(val / maxNoise + 0.5) * (float)maxNoise;
	}
	
	private class Coordinates {
		public final float x;
		public final float y;
		public final float z;
		
		public Coordinates(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	private class Pair<S, T> {
		public final S first;
		public final T second;
		
		public Pair(S s, T t) {
			this.first = s;
			this.second = t;
		}
	}
	
	private class List<S, T> {
		Node<S, T> front;
		Node<S, T> back;
		
		public List() {
			this.front = null;
			this.back = null;
		}
		
		void addBack(S s, T t) {
			Node<S, T> n = new Node<>(s, t);
			if(this.back == null) {
				this.front = n;
			} else {
				this.back.setNext(n);
			}
			this.back = n;
		}
		
		Pair<S, T> getFront() {
			if(this.front == null) {
				return null;
			}
			
			return new Pair<>(this.front.u, this.front.v);
		}
		
		void removeFront() {
			if(this.front != null) {
				this.front = this.front.getNext();
			}
		}
		
		Iterator<S, T> getIterator() {
			return new Iterator<>(this.front);
		}
		
		boolean isEmpty() {
			return this.front == null;
		}
		
		private class Node<U, V> {
			public final U u;
			public final V v;
			Node<U, V> next;
			
			Node(U u, V v) {
				this.u = u;
				this.v = v;
			}
			
			void setNext(Node<U, V> n) {
				this.next = n;
			}
			
			Node<U, V> getNext() {
				return this.next;
			}
		}
		
		private class Iterator<U, V> {
			private Node<U, V> current;
			private Node<U, V> next;
			
			public Iterator(Node<U, V> next) {
				this.current = null;
				this.next = next;
			}
			
			public boolean hasNext() {
				return this.next != null;
			}
			
			public Pair<U, V> getNext() {
				this.current = next;
				this.next = current.getNext();
				return new Pair<>(this.current.u, this.current.v);
			}
		}
	}
}
