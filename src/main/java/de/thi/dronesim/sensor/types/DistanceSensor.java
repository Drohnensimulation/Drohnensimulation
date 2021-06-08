package de.thi.dronesim.sensor.types;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import de.thi.dronesim.obstacle.UfoObjs;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.sensor.ISensor;
import de.thi.dronesim.sensor.SensorModule;
import de.thi.dronesim.sensor.dto.SensorResultDto;
import de.thi.dronesim.sensor.enums.CalcType;
import de.thi.dronesim.sensor.enums.SensorForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Abstract class for all sensor that measure a distance.
 *
 * @author Moris Breitenborn
 * @author Johannes Steierl
 * @author Dominik Bartl
 * @author Daniel Stolle
 */
public abstract class DistanceSensor implements ISensor {

    // /////////////////////////////////////////////////////////////////////////////
    // Logger
    // /////////////////////////////////////////////////////////////////////////////
    private static final Logger logger = LogManager.getLogger();

    //TODO: -Beschreibung des Kegels
    //		-Vervollständigung der Methodenimplementierung

    // /////////////////////////////////////////////////////////////////////////////
    // Fields
    // /////////////////////////////////////////////////////////////////////////////

    // see getter and setter for a documentation of the fields
    private String name;
    private int id;
    private float range;
    private float sensorAngle;
    private float sensorRadius;
    private float measurementAccuracy;
    private Vector3f directionVector;
    private Vector3f positionVector;
    private final SensorForm sensorForm;
    private final CalcType calcType;
    protected SensorResultDto sensorResultDtoValues;

    // /////////////////////////////////////////////////////////////////////////////
    // Init
    // /////////////////////////////////////////////////////////////////////////////

    /**
     * A constructor to initialize a Sensor from the Config
     *
     * @param config a SensorConfig-Object of the Big Config List
     *
     * @author Johannes Steierl
     */
    public DistanceSensor(SensorConfig config){
        this.name = config.getClassName();
        this.id = config.getSensorId();
        this.range = config.getRange();
        this.sensorAngle = config.getSensorAngle();
        this.sensorRadius = config.getSensorRadius();
        this.measurementAccuracy = config.getMeasurementAccuracy();
        this.directionVector = new Vector3f(config.getDirectionX(), config.getDirectionY(), config.getDirectionZ());
        this.positionVector = new Vector3f(config.getPosX(), config.getPosY(), config.getPosZ());
        SensorForm tempForm = SensorForm.CONE; //Default
        try{
            tempForm = SensorForm.valueOf(config.getSensorForm());
        }catch(IllegalArgumentException e){
            logger.error("SensorForm {} wird nicht unterstützt! Defaultwert ist CONE", config.getSensorForm());
        }finally {
            this.sensorForm = tempForm;
        }
        CalcType tempCalcType = CalcType.AVG;
        try{
            tempCalcType = CalcType.valueOf(config.getCalcType());
        }catch(IllegalArgumentException e){
            logger.error("CalcType {} wird nicht unterstützt! Defaultwert ist AVG", config.getCalcType());
            tempCalcType = CalcType.AVG;
        }finally {
            this.calcType = tempCalcType;
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////////

    /**
     * A GENERALLY interpretation of the calculation of the SensorResult.
     *
     * Steps:
     * 1. Group the Hitmarks by the hit obstacle -> a List wich contains Sets of Hitmarks
     * 2. Sorting the List by the avg distance of each Set:
     *      - building the Average Distance of each Set
     *      - storing the new information in the ObstacleAndDistanceDto with the affected obstacle
     *      -> List of ObstacleAndDistanceDtos sorted by the avg Distance
     * 3. Storing in the SensorResultDto:
     *      Sensor: is the used Sensor (this)
     *      Obstacle: is the nearest Obstacle
     *      values: the avg Distances of all ObstacleAndDistanceDtos sorted, so that the smalest float is on the first place
     *
     * @param origin
     * @param direction
     * @param range
     * @param opening
     *
     * @return SensorResultDto
     *
     * @author Johannes Steierl
     */
    public SensorResultDto getSensorResult(Vector3f origin, Vector3f direction, float range, Vector3f opening, SensorModule sensorModule) {
        //Helperclass only used in this method so far
        class ObstacleAndDistanceDTO {
            private Obstacle obstacle;
            private float avgDistance;

            private Obstacle getObstacle() {
                return obstacle;
            }

            private void setObstacle(Obstacle obstacle) {
                this.obstacle = obstacle;
            }

            private float getAvgDistance() {
                return avgDistance;
            }

            private void setAvgDistance(float avgDistance) {
                this.avgDistance = avgDistance;
            }
        }

        Set<HitMark> hitMarks = getSensorHits(origin, direction, range, opening, getSensorForm(), sensorModule);

        //grouping hitmarks by the hit object
        List<Set<HitMark>> hitmarksGroupedByObstacles = new ArrayList<>();
        for (HitMark newHitmark : hitMarks) {
            Obstacle obstacle = newHitmark.getObstacle();
            boolean existing = false;
            for (Set<HitMark> setH : hitmarksGroupedByObstacles) {
                if (!setH.isEmpty()) {
                    //first entry of the set
                    Iterator<HitMark> iterator = setH.iterator();
                    HitMark m = iterator.next();
                    //lookup if a set with marks containing this obstacle already exists
                    if (obstacle.equals(m.getObstacle())) {
                        setH.add(newHitmark);
                        existing = true;
                    }
                }
            }
            // creating new Set of marks with the unknown obstacle
            if (!existing) {
                Set<HitMark> newSet = new HashSet<>();
                newSet.add(newHitmark);
                hitmarksGroupedByObstacles.add(newSet);
            }
        }

        // sort the grouped hits by the avgDistance
        List<ObstacleAndDistanceDTO> obstacleAndDistanceDTOS = new ArrayList<>();
        for (Set<HitMark> setH : hitmarksGroupedByObstacles) {
            ObstacleAndDistanceDTO oADDTO = new ObstacleAndDistanceDTO();

            Iterator<HitMark> iterator = setH.iterator();
            HitMark m = iterator.next();
            oADDTO.setObstacle(m.getObstacle());

            float distance = -1f;
            switch (getCalcType()){
                case NEAREST:
                    for (HitMark h : setH){
                        if(distance == -1 ){
                            distance = h.getDistance();
                        }else if(distance > h.getDistance()){
                            distance = h.getDistance();

                        }
                    }
                    oADDTO.setAvgDistance(distance);
                    obstacleAndDistanceDTOS.add(oADDTO);
                    break;
                case FAREST:
                    for (HitMark h : setH){
                        if(distance < h.getDistance()){
                            distance = h.getDistance();

                        }
                    }
                    oADDTO.setAvgDistance(distance);
                    obstacleAndDistanceDTOS.add(oADDTO);
                    break;
                case AVG:
                    for (HitMark h : setH) {
                        distance += h.getDistance();
                    }
                    distance = distance / setH.size();
                    oADDTO.setAvgDistance(distance);
                    obstacleAndDistanceDTOS.add(oADDTO);
                    break;
            }

        }
        obstacleAndDistanceDTOS.sort((a, b) -> Float.compare(a.getAvgDistance(), b.getAvgDistance()));

        SensorResultDto sensorResultDto = new SensorResultDto();
        sensorResultDto.setSensor(this);
        obstacleAndDistanceDTOS.forEach(o -> sensorResultDto.getObstacle().add(o.getObstacle()));
        //first value of the values-array ist the nearest, the last is the farthest
        if (sensorResultDto.getValues() == null) {
            sensorResultDto.setValues(new ArrayList<>());
        }
        obstacleAndDistanceDTOS.forEach(o -> sensorResultDto.getValues().add(o.getAvgDistance()));

        return sensorResultDto;
    }

    /**
     * Returns the shortest distance to an object.
     *
     * @return distance in meter
     */
    public double getDistance() {
        double measurement = 0; /*TODO: Call Obstacle Team-Method*/

        return this.handleMeasurementAccuracy(measurement);
    }

    /**
     * Create a {@link SensorConfig} with all values
     *
     * @return the config-object to save
     */
    public SensorConfig saveToConfig() {
        SensorConfig config = new SensorConfig();
        config.setSensorId(id);

        config.setMeasurementAccuracy(measurementAccuracy);
        config.setDirectionX(directionVector.getX());
        config.setDirectionY(directionVector.getY());
        config.setDirectionZ(directionVector.getZ());
        config.setPosX(positionVector.getX());
        config.setPosY(positionVector.getY());
        config.setPosZ(positionVector.getZ());

        config.setRange(range);
        config.setSensorAngle(sensorAngle);
        config.setSensorRadius(sensorRadius);
        config.setSensorForm(sensorForm.name());
        config.setCalcType(calcType.name());

        return config;
    }

    // /////////////////////////////////////////////////////////////////////////////
    // protected Methods
    // /////////////////////////////////////////////////////////////////////////////

    /**
     * Change the direction of the sensor
     *
     * @param deltaX change in direction of x
     * @param deltaY change in direction of y
     * @param deltaZ change in direction of z
     */
    protected void changeDirection(float deltaX, float deltaY, float deltaZ) {
        directionVector = directionVector.add(new Vector3f(deltaX, deltaY, deltaZ));
    }

    /**
     * Change the position of the sensor
     *
     * @param deltaX movement in direction of x
     * @param deltaY movement in direction of y
     * @param deltaZ movement in direction of z
     */
    protected void changePosition(float deltaX, float deltaY, float deltaZ) {
        positionVector = positionVector.add(new Vector3f(deltaX, deltaY, deltaZ));
    }

    /**
     * Calculates the length from origin point to range end. This length is needed to
     * create the cone-object. This Method is needed in the "check"-methods (UfoObjs.java)
     *
     * @return float
     * @author Moris Breitenborn
     */
    protected float calcConeHeight() {
        return this.range + calcOriginToPositionLength();
    }

    /**
     * This method calculates a vector that is lying on the cone surface. This vector is needed
     * to calculate the entire cone.
     *
     * @return Vector3f
     * @author Moris Breitenborn
     */
    protected Vector3f calcSurfaceVector() {

        //Get the Vector of the Sensor orientation
        Vector3f directionVector = getDirectionVector();

        //Calculate the rotation angle to rotate the directionVector in to the XY-level
        double rotXY;
        if (directionVector.getY() == 0) {
            rotXY = Math.PI / 2;
        } else {
            rotXY = Math.atan((directionVector.getZ() / directionVector.getY()) * (-1));
        }

        //calculate all necessary variable for the rotation matrix and create matrix
        double cosPhi = Math.cos(rotXY);
        double sinPhi = Math.sin(rotXY);
        double minSinPhi = (Math.sin(rotXY) * (-1));
        Matrix3f transformMatrixX = new Matrix3f((float) 1, (float) 0, (float) 0, (float) 0, (float) cosPhi, (float) minSinPhi, (float) 0, (float) sinPhi, (float) cosPhi);
        //multiply the matrix with the vector
        Vector3f vectorXY = transformMatrixX.mult(directionVector);

        //To get the angle between the vectorXY and the x-Axses we call the function checkAngel();
        Vector3f xAxsis = new Vector3f(1, 0, 0);
        //give the angle the right operator to calculate the right vector. calculate variables and matrix
        double rotX = calcAngel(vectorXY, xAxsis);
        if (directionVector.getY() > 0) {
            rotX = rotX * (-1);
        }
        cosPhi = Math.cos(rotX);
        sinPhi = Math.sin(rotX);
        minSinPhi = (Math.sin(rotX) * (-1));
        Matrix3f transformMatrixZ = new Matrix3f((float) cosPhi, (float) minSinPhi, (float) 0, (float) sinPhi, (float) cosPhi, (float) 0, (float) 0, (float) 0, (float) 1);
        //rotate on x-Axsis
        Vector3f vectorX = transformMatrixZ.mult(vectorXY);

        //now we can rotate the vector around the y-Axses
        double sensorAngleAsRadiant = Math.toRadians(sensorAngle);
        cosPhi = (float) Math.cos(sensorAngleAsRadiant);
        sinPhi = (float) Math.sin(sensorAngleAsRadiant);
        minSinPhi = (float) (Math.sin(sensorAngleAsRadiant) * (-1));
        Matrix3f transformMatrixY = new Matrix3f((float) cosPhi, (float) 0, (float) sinPhi, (float) 0, (float) 1, (float) 0, (float) minSinPhi, (float) 0, (float) cosPhi);
        Vector3f vectorWithAngel = transformMatrixY.mult(vectorX);

        //rerotate the new vectors with all used angles. startt with the last one used
        rotX = rotX * (-1);
        cosPhi = Math.cos(rotX);
        sinPhi = Math.sin(rotX);
        minSinPhi = (Math.sin(rotX) * (-1));
        transformMatrixZ = new Matrix3f((float) cosPhi, (float) minSinPhi, (float) 0, (float) sinPhi, (float) cosPhi, (float) 0, (float) 0, (float) 0, (float) 1);
        Vector3f vectorWithAngelXY = transformMatrixZ.mult(vectorWithAngel);
        //Rotation at Y
        rotXY = rotXY * (-1);
        cosPhi = Math.cos(rotXY);
        sinPhi = Math.sin(rotXY);
        minSinPhi = (Math.sin(rotXY) * (-1));
        transformMatrixX = new Matrix3f((float) 1, (float) 0, (float) 0, (float) 0, (float) cosPhi, (float) minSinPhi, (float) 0, (float) sinPhi, (float) cosPhi);
        return transformMatrixX.mult(vectorWithAngelXY);
    }

    /**
     * Returns the angle between two vectors
     *
     * @return float
     * @author Moris Breitenborn
     */
    protected double calcAngel(Vector3f original, Vector3f calculated) {
        double x1 = original.getX();
        double y1 = original.getY();
        double z1 = original.getZ();
        double x2 = calculated.getX();
        double y2 = calculated.getY();
        double z2 = calculated.getZ();

        double nenner = x1 * x2 + y1 * y2 + z1 * z2;
        double zaeler = (Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1) * Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2));
        return Math.acos(nenner / zaeler);
    }

    /**
     * Calculate the origin vector from drone center to sensor cone origin point
     * by using intercept theorems.
     *
     * @return Vector3f
     * @author Moris Breitenborn
     */
    protected Vector3f calcOrigin() {

        // get the distance between origin and position
        float originToPositionLength = calcOriginToPositionLength();

        // normalize the direction vector to multiply it with the range later to get the needed vector
        Vector3f normalizedOrientationVector = getDirectionVector().normalize();

        // rotate the normalized direction vector in the opposite direction with scale(-1)
        normalizedOrientationVector = normalizedOrientationVector.negate();

        // get the normalized Vector on the calculated length with scale(originToPositionLength)
        normalizedOrientationVector = normalizedOrientationVector.mult(originToPositionLength);

        // with this vector we can calculate the origin point by simply adding the normalizedOrientationVector
        // to the position point of the Sensor
        return positionVector.add(normalizedOrientationVector);
    }

    /**
     * Calculates the length from origin point to sensor position. This length is needed to
     * create the cone-object.
     *
     * @return length between origin and sensor position
     * @author Moris Breitenborn started calculation with intercept theorems.
     * @author Daniel Stolle improved method by simplifying it.
     */
    protected float calcOriginToPositionLength() {
        return sensorRadius / (float) Math.tan(Math.toRadians(this.sensorAngle));
    }

    // /////////////////////////////////////////////////////////////////////////////
    // private Methods
    // /////////////////////////////////////////////////////////////////////////////

    /**
     * Versieht die gemessene Entfernung zum Hindernis mit einer Messungenauigkeit
     *
     * @param measurement die gemessene Entfernung
     * @return Die Entfernung, versehen mit der Ungenauigkeit
     */
    private double handleMeasurementAccuracy(double measurement) {
        if (Double.compare(this.measurementAccuracy, Double.MAX_VALUE) == 0) {
            return Double.compare(measurement, 0.0) > 0 ? 1.0 : 0.0;
        } else if (Double.compare(this.measurementAccuracy, 0.0) == 0) {
            return measurement;
        } else {
            return (int) (measurement / this.measurementAccuracy + 0.5) * this.measurementAccuracy;
        }
    }

    /**
     * Method to get the angle between the orientation of the drone and a hitpoint
     *
     * @param hitMark the hitpoint of an ray
     * @param origin  the position of the drone
     * @return the angel of the orientation of the drone and the hitpoint
     *
     * @author Johannes Steierl
     */
    private float calcHitAngle(HitMark hitMark, Vector3f origin) {
        Vector3f relativ = hitMark.relativeHit();
        return (float) calcAngel(origin, relativ);
    }


    /**
     *Gets the Rays that hit an Obstacle
     *
     * the dimension Vector represents the sensorform, so by a cubuid the x = width, y = length and z = height
     *
     * @param origin coords of the top of the sensorform
     * @param orientation Drone is heading this direction
     * @param range sensorrange
     * @param opening Example: if the angle is 45° the vector would be 	1 (x)
     *                													0 (y)
     *                													1 (z)
     * @param sensorForm indicates the form of the sensor
     * @return a Set of the rays that hit objects
     *
     * @author Johannes Steierl
     */
    private Set<HitMark> getSensorHits(Vector3f origin, Vector3f orientation, float range, Vector3f opening, SensorForm sensorForm, SensorModule sensorModule) {
        UfoObjs ufoObjs = sensorModule.getSimulation().getChild(UfoObjs.class);

        Vector3f dimension;
        switch (sensorForm){
            case CONE:      return ufoObjs.checkSensorCone(origin, orientation, range, opening);
            case CUBOID:
                dimension = new Vector3f(sensorRadius, sensorRadius, range);
                return ufoObjs.checkSensorCuboid(origin,orientation, dimension);
            case PYRAMID: 	return ufoObjs.checkSensorPyramid(origin,orientation,range,opening);
            case CYLINDER:
                dimension = new Vector3f(sensorRadius, sensorRadius, range);
                return ufoObjs.checkSensorCylinder(origin,orientation,dimension);
            default: throw new IllegalArgumentException("No supported SensorForm given (CONE, CYLINDER, CUBOID, PYRAMID");
        }
    }


    // /////////////////////////////////////////////////////////////////////////////
    // Getter/Setter
    // /////////////////////////////////////////////////////////////////////////////

    /**
     * The defined name of this sensor.
     *
     * @return the name from the configuration
     */
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * Relative direction of the sensor to the drone
     *
     * @return a vector where the head of the drone is pointed to the x-axis
     */
    protected Vector3f getDirectionVector() {
        return directionVector;
    }

    /**
     * Relative direction of the sensor to the drone
     *
     * @param x direction of the head of the drone
     * @param y upwards from the drone
     * @param z left to the drone
     */
    protected void setDirection(float x, float y, float z) {
        directionVector = new Vector3f(x, y, z);
    }

    /**
     * Relative direction of the sensor to the drone
     *
     * @param direction the direction as vector
     */
    protected void setDirection(Vector3f direction) {
        directionVector = direction;
    }

    /**
     * Relative position of the sensor to the drone
     *
     * @return a vector where the head of the drone is pointed to the x-axis
     */
    protected Vector3f getPositionVector() {
        return positionVector;
    }

    /**
     * Relative position of the sensor to the drone
     *
     * @param x direction of the head of the drone
     * @param y upwards from the drone
     * @param z left to the drone
     */
    protected void setPosition(float x, float y, float z) {
        positionVector = new Vector3f(x, y, z);
    }

    /**
     * Range from sensor position to cone bottom
     *
     * @return the range as Float
     */
    protected float getRange() {
        return this.range;
    }

    /**
     * Set the range of the sensor
     *
     * @param range Range from sensor position to cone bottom
     */
    protected void setRange(float range) {
        if (Double.compare(range, 0.0) <= 0) {
            throw new IllegalArgumentException("Range must be greater than zero!");
        }
        this.range = range;
    }

    /**
     * The size of the sensor surface
     *
     * @return the size as float
     */
    protected float getSize() {
        return this.sensorRadius;
    }

    /**
     * Set the size of the sensor surface
     *
     * @param size The size of the sensor surface as float
     */
    protected void setSize(float size) {
        if (Double.compare(size, 0.0) <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero!");
        }
        this.sensorRadius = size;
    }

    /**
     * The angle between surface and cone height
     *
     * @return the angle as float
     */
    protected float getSensorAngle() {
        return sensorAngle;
    }

    /**
     * Defines the angle between surface and cone height in degrees
     *
     * @param deg allowed interval [0;90]
     */
    protected void setSensorAngle(float deg) {
        if (Double.compare(deg, 0.0) < 0) {
            throw new IllegalArgumentException("Angle of view must not be less than zero!");
        }
        if (Double.compare(deg, 90.0) >= 0) {
            throw new IllegalArgumentException("Angle of view must not be greater or equal than 90!");
        }
        this.sensorAngle = deg;
    }

	/**
     * Set a measurement inaccuracy.
     * <ul>
     *     <li>0.5 - distance is measured in 0,5-intervalls</li>
     *     <li>10.0 - distance is measured in 10-intervalls</li>
     *     <li>0.0 - the exact distance is returned</li>
     *     <li>Double.MAX_VALUE - the sensor only returns if a obstacle detected (1) or not (0)</li>
     * </ul>
     *
	 * @param accuracy the accuracy for the sensor
	 */
	protected void setMeasurementAccuracy(float accuracy) {
		if (Double.compare(accuracy, 0.0) < 0) {
			throw new IllegalArgumentException("Accuracy may not be less than zero!");
		}
		this.measurementAccuracy = accuracy;
	}

    protected SensorForm getSensorForm() {
        return sensorForm;
    }

    protected CalcType getCalcType() {
        return calcType;
    }

    public boolean equals(ISensor sensor){
	    return this.getId() == sensor.getId();
    }

    @Override
    public String getType() {
        return "DistanceSensor";
    }
}
