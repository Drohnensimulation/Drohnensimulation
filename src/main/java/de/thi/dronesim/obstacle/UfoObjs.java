package de.thi.dronesim.obstacle;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.obstacle.dto.HitBoxDTO;
import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.obstacle.dto.ObstacleJsonDTO;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.obstacle.util.HitBoxRigidBody;
import de.thi.dronesim.obstacle.util.JBulletContext;
import de.thi.dronesim.obstacle.util.JBulletHitMark;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Bakri Aghyourli
 * @author Christian Schmied
 * @author Michael Küchenmeister
 */

public class UfoObjs implements ISimulationChild, IUfoObjs {
    private final JBulletContext jBullet;
    private final Set<Obstacle> obstacles;
    /**
     * Actually this Set is just for "rechecking", it's totally useless
     */
    private final Set<HitBoxRigidBody> hitBoxes;
    private ObstacleJsonDTO config;
    private Simulation simulation;

    public UfoObjs() {
        jBullet = new JBulletContext();
        obstacles = new HashSet<>();
        hitBoxes = new HashSet<>();
    }

    /**
     * Internal rayTest Method that will handle the Object Lookups
     *
     * @param from
     * @param direction
     * @param range
     * @return the HitMark or Null
     */
    private HitMark rayTest(Vector3f from, Vector3f direction, float range) {
        JBulletHitMark hitBody = jBullet.rayTest(from, direction, range);
        if (hitBody != null) {

            if (hitBoxes.contains(hitBody.body)) {
                return new HitMark(hitBody);
            } else {
                throw new RuntimeException("No Obstacle found for Provided HitBox");
            }
        }
        return null;
    }

    @Override
    public Obstacle addObstacle(ObstacleDTO obstacleDto) {
        // Create new Obstacle from DTO
        Obstacle obstacle = new Obstacle(obstacleDto.modelName, obstacleDto.modelPath, obstacleDto.id, obstacleDto.position, obstacleDto.rotation, obstacleDto.scale, obstacleDto.hitboxes);

        Set<HitBoxRigidBody> objectHitBoxes = new HashSet<>();

        // Get all hit boxes from obstacleDTO
        for (HitBoxDTO hit : obstacleDto.hitboxes) {
            float[] position = {hit.position[0], hit.position[1], hit.position[2]};
            float[] rotation = {hit.rotation[0], hit.rotation[1], hit.rotation[2]};
            float[] dimension = {hit.dimension[0], hit.dimension[1], hit.dimension[2]};

            // Add hit boxes to the jBullet context
            HitBoxRigidBody hitBoxRigidBody = jBullet.addHitBox(new javax.vecmath.Vector3f(position), new javax.vecmath.Vector3f(rotation), new javax.vecmath.Vector3f(dimension), obstacle);

            // Add hit boxes into a set for Obstacle setter method
            objectHitBoxes.add(hitBoxRigidBody);

            // Add Obstacle into "hitBoxes" set
            hitBoxes.add(hitBoxRigidBody);
        }

        // Set the hit boxes into the new Obstacle Object
        obstacle.setHitBoxRigidBodys(objectHitBoxes);

        // Add Object to obstacle set
        this.obstacles.add(obstacle);

        return obstacle;
    }

    @Override
    public boolean removeObstacle(ObstacleDTO obstacleDTO) {
        // Get the obstacle to delete by id
        Obstacle obstacleToDelete = null;
        for (Obstacle o : obstacles) {
            if (o.getID().equals(obstacleDTO.id)) {
                obstacleToDelete = o;
            }
        }

        if (obstacleToDelete != null) {
            Set<HitBoxRigidBody> hitBoxRigidBodies = obstacleToDelete.getHitboxes();
            for (HitBoxRigidBody h : hitBoxRigidBodies) {
                // Remove all hit boxes of the obstacle from jBullet and the "hitBoxes" set
                jBullet.removeHitBox(h);
                hitBoxes.remove(h);
            }
            // Remove the obstacle from the set
            obstacles.remove(obstacleToDelete);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeObstacle(Obstacle obstacleObj) {
        if (obstacles.contains(obstacleObj)) {
            Set<HitBoxRigidBody> hitBoxRigidBodies = obstacleObj.getHitboxes();
            for (HitBoxRigidBody h : hitBoxRigidBodies) {
                // Remove all hit boxes of the obstacle from jBullet and the "hitBoxes" set
                jBullet.removeHitBox(h);
                hitBoxes.remove(h);
            }
            obstacles.remove(obstacleObj);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<Obstacle> getObstacles() {
        return obstacles;
    }

    @Override
    public Set<HitMark> checkSensorCone(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        /*
         * this algorithm uses golden ratio ((1 + sqrt(5))/2) to create a golden angle and
         * use this angle to rotate a vector around its origin n times depending on vector number
         * then creating a point at the end of the vector. The end result looks like sunflower seeds
         * distributed evenly on a disc. Then use those points coordinations to send rays in a cone
         * shaped projection.
         *
         * To read more about golden ratio and distributing points evenly on a disc check:
         * http://blog.marmakoide.org/?p=1
         * https://youtu.be/bqtqltqcQhw?t=128
         */
        Set<HitMark> hits = new HashSet<>();
        //using golden Ration Distribution to equally distribute rays
        float goldenRatio = (1f + (float) Math.sqrt(5)) / 2;
        float angle = 2 * (float) Math.PI * goldenRatio;
        float dist;

        Vector3f angleVec = opening.normalize();
        Vector3f direction = orientation.normalize();
        //create a projection of angleVec on direction (to use for creating 2 perpendicular vectors to direction)
        //proj(d) a = d * a / (|d|² == 1)
        Vector3f angleProjOnDir = direction.mult(angleVec.dot(direction));
        //use the projected vector to create a vector perpendicular to direction
        Vector3f i = angleProjOnDir.subtract(angleVec);
        //record the radius range to use for distributing rays
        float radius = i.length();
        i.normalizeLocal();
        //create a second vector that is perpendicular to direction vector and i vector
        Vector3f j = i.cross(direction).normalizeLocal();

        //reuse loop variables
        Vector3f dI = new Vector3f();
        Vector3f dJ = new Vector3f();
        Vector3f ray = new Vector3f();

        //rays count is dependent on cone base area and given density/m
        float r = radius / angleProjOnDir.length() * range * config.config.rayDensity;
        int rayCount = (int) (r * r * Math.PI);

        for (int l = 0; l < rayCount; l++) {
            //determine distance from circle center based on ray number
            dist = (float) Math.sqrt(l / (rayCount - 1f)) * radius;

            //use the golden angle and ray number to get x and y coordinates relative
            //to circle center and transform it to world x, y, z coordinates
            dI.set(i.mult(dist * (float) Math.cos(angle * l)));
            dJ.set(j.mult(dist * (float) Math.sin(angle * l)));
            ray.set(angleProjOnDir.x + dI.x + dJ.x,
                    angleProjOnDir.y + dI.y + dJ.y,
                    angleProjOnDir.z + dI.z + dJ.z).normalizeLocal();

            //check for collisions and add the hitmark to the list if a collision is found
            HitMark hit = this.rayTest(origin, ray, range);
            if (hit != null) {
                hits.add(hit);
            }
        }
        return hits;
    }

    @Override
    public Set<HitMark> checkSensorPyramid(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        Set<HitMark> hits = new HashSet<>();

        Vector3f direction = orientation.normalize();
        Vector3f angleVec = opening.normalize();
        //create a projection of angleVec on direction (to use for creating 2 perpendicular vectors to direction)
        //proj(d) a = d * a / (|d|² == 1)
        Vector3f angleProjOnDir = direction.mult(direction.dot(angleVec));
        //use the projected vector to create a vector perpendicular to direction
        Vector3f i = angleVec.subtract(angleProjOnDir);
        //record the width range to use for distributing rays
        float width = 2 * i.length();
        i.normalizeLocal();
        //create a second vector that is perpendicular to direction vector and i vector
        Vector3f j = i.cross(direction).normalizeLocal();

        //reuse loop variables
        Vector3f dI = new Vector3f();
        Vector3f dJ = new Vector3f();
        Vector3f ray = new Vector3f();

        //rays count is dependent on pyramid base area and given density/m
        int rayPerRow = (int) (width / angleProjOnDir.length() * range * config.config.rayDensity);
        float step = width / rayPerRow;

        for (float y = -width / 2; y <= width / 2; y += step) {
            for (float x = -width / 2; x <= width / 2; x += step) {

                //get x and y coordinates relative to rectangle center
                //and transform it to world x, y, z coordinates
                dI.set(i.mult(x));
                dJ.set(j.mult(y));
                ray.set(angleProjOnDir.x + dI.x + dJ.x,
                        angleProjOnDir.y + dI.y + dJ.y,
                        angleProjOnDir.z + dI.z + dJ.z).normalizeLocal();

                //check for collisions and add the hitmark to the list if a collision is found
                HitMark hit = this.rayTest(origin, ray, range);
                if (hit != null) {
                    hits.add(hit);
                }
            }
        }

        return hits;
    }

    @Override
    public Set<HitMark> checkSensorCuboid(Vector3f origin, Vector3f orientation, Vector3f dimension) {
        return checkSensorCuboid(origin, orientation, dimension, 0);
    }

    @Override
    public Set<HitMark> checkSensorCuboid(Vector3f origin, Vector3f orientation, Vector3f dimension, float rotation) {

        // Orientation is a normal,
        // The rotation angle is axis aligned, so its the rotation around the orientation vector (so the normal)
        // Where rotation 0 is like facing directly upwards...

        Set<HitMark> hits = new HashSet<>();
        int ppm = this.config.config.rayDensity;

        Vector3f normal = orientation.normalize();
        float range = dimension.z;

        Vector3f center = new Vector3f(origin);

        //Place the two vectors directly infront of the drone
        Vector3f verticalVect = new Vector3f(dimension.x / 2f, 0, 0);
        Vector3f horizontalVect = new Vector3f(0, dimension.y / 2f, 0);

        //Rotate upwards (pitch)
        Vector3f pitchVect = new Vector3f(0, orientation.y, orientation.z);
        float angelPitch = pitchVect.angleBetween(Vector3f.UNIT_Y);
        Quaternion pitchRotation = new Quaternion();
        pitchRotation.fromAngleAxis(angelPitch, Vector3f.UNIT_X);

        //Rotate sideways (yaw)
        Vector3f yawVect = new Vector3f(orientation.x, 0, orientation.z);
        float angelYaw = yawVect.angleBetween(Vector3f.UNIT_Z);
        Quaternion yawRotation = new Quaternion();
        yawRotation.fromAngleAxis(angelYaw, Vector3f.UNIT_Y);

        //Rotate the voctors around the orientation vector
        Quaternion rollRotation = new Quaternion();
        rollRotation.fromAngleAxis(rotation, orientation);

        //TODO Check if this is correct...
        pitchRotation.multLocal(verticalVect);
        pitchRotation.multLocal(horizontalVect);
        yawRotation.multLocal(verticalVect);
        yawRotation.multLocal(horizontalVect);
        rollRotation.multLocal(verticalVect);
        rollRotation.multLocal(horizontalVect);

        //topLeft Should be the Point where the Sensor will start rastering;
        Vector3f topLeft = center.subtract(horizontalVect);
        topLeft.subtract(verticalVect, topLeft);

        //Rasterization based on width (needed rays to match roughly density of rays)
        int neededRaysX = (int) Math.ceil(dimension.x * ppm);
        int neededRaysY = (int) Math.ceil(dimension.y * ppm);
        float stepSizeX = dimension.x / neededRaysX;
        float stepSizeY = dimension.y / neededRaysY;
        Vector3f horizontalStep = horizontalVect.mult(stepSizeX);
        Vector3f verticalStep = verticalVect.mult(stepSizeY);

        for (int y = 0; y < neededRaysY; y++) {
            Vector3f currentPoint = topLeft.add(verticalStep.mult(y));
            for (int x = 0; x < neededRaysX; x++) {
                //Do the actual Ray Test
                //TODO propably directly calc the range Vector, because the Range Vector will stay always the same.
                HitMark hit = this.rayTest(currentPoint, normal, range);
                if (hit != null) {
                    hits.add(hit);
                }
                currentPoint.addLocal(horizontalStep);
            }
        }

        return hits;
    }

    @Override
    public Set<HitMark> checkSensorCylinder(Vector3f origin, Vector3f orientation, Vector3f dimension) {
        /*
         * this algorithm uses golden ratio ((1 + sqrt(5))/2) to create a golden angle and
         * use this angle to rotate a vector around its origin n times depending on vector number
         * then creating a point at the end of the vector. The end result looks like sunflower seeds
         * distributed evenly on a disc. Then use those points coordinations to send rays in a cone
         * shaped projection.
         *
         * To read more about golden ratio and distributing points evenly on a disc check:
         * http://blog.marmakoide.org/?p=1
         * https://youtu.be/bqtqltqcQhw?t=128
         */
        Set<HitMark> hits = new HashSet<>();
        Random rand = new Random();

        //if dimensions of the cylinder base differs take the biggest one (error forgiving)
        float radius = (dimension.x >= dimension.y) ? dimension.x / 2f : dimension.y / 2f; //cylinder radius
        float range = dimension.z; //cylinder height

        //using golden Ration Distribution to equally distribute rays
        float goldenRatio = (1f + (float) Math.sqrt(5)) / 2;
        float angle = 2 * (float) Math.PI * goldenRatio;
        float dist;

        //create a random Point to generate a vector perpendicular to direction 
        Vector3f direction = orientation.normalize();
        Vector3f randomPoint = new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        //proj(d) p = d * p / (|d|² == 1)
        Vector3f pointProjOnDir = direction.mult(randomPoint.dot(direction));

        //use the projected point to create a vector perpendicular to direction
        Vector3f i = randomPoint.subtract(pointProjOnDir).normalizeLocal();
        //create a second vector that is perpendicular to direction vector and i vector
        Vector3f j = i.cross(direction).normalizeLocal();

        //reuse loop variables
        Vector3f dI = new Vector3f();
        Vector3f dJ = new Vector3f();
        Vector3f startPoint = new Vector3f(); //Ray starting Point

        //calculate needed rays count based on cylinder base area = PI * r^2
        int density = config.config.rayDensity;
        int rayCount = (int) ((radius * density) * (radius * density) * Math.PI);

        for (int l = 0; l < rayCount; l++) {
            //determine distance from circle center based on ray number
            dist = (float) Math.sqrt(l / (rayCount - 1f)) * radius;

            //use the golden angle and ray number to get x and y coordinates relative
            //to circle center and transform it to world x, y, z coordinates
            dI.set(i.mult(dist * (float) Math.cos(angle * l)));
            dJ.set(j.mult(dist * (float) Math.sin(angle * l)));
            startPoint.set(origin.x + dI.x + dJ.x,
                    origin.y + dI.y + dJ.y,
                    origin.z + dI.z + dJ.z);

            //check for collisions and add the hitmark to the list if a collision is found
            HitMark hit = this.rayTest(startPoint, direction, range);
            if (hit != null) {
                hits.add(hit);
            }
        }
        return hits;
    }

    @Override
    public boolean checkDroneCollision(Vector3f origin, float radius) {
        float goldenRatio = (1f + (float) Math.sqrt(5)) / 2;
        float angle = 2 * (float) Math.PI * goldenRatio;

        Vector3f ray = new Vector3f();

        int rayCount = 300;
        float inclination;
        float azimuth;
        float sin;

        for (int l = 0; l < rayCount; l++) {
            inclination = (float) Math.acos(1 - 2 * l / (float) rayCount);
            azimuth = angle * l;
            sin = (float) Math.sin(inclination);

            ray.set(sin * (float) Math.cos(azimuth),
                    sin * (float) Math.sin(azimuth),
                    (float) Math.cos(inclination)).normalizeLocal();

            //check for collision
            if (this.rayTest(origin, ray, radius) != null)
                return true;
        }
        return false;
    }

    @Override
    public ObstacleJsonDTO save() {
        Set<Obstacle> obstacles = this.getObstacles();
        HashSet<ObstacleDTO> obstacleDTOSet = new HashSet<>();
        for (Obstacle o : obstacles) {
            // Clone all obstacles and add them to the obstacleDTOSet
            ObstacleDTO cloneObsDTO = new ObstacleDTO();
            cloneObsDTO.modelName = o.getModelName();
            cloneObsDTO.modelPath = o.getModelPath();
            cloneObsDTO.id = o.getID();
            cloneObsDTO.hitboxes = new HashSet<>(o.getDtoHitboxes());
            cloneObsDTO.position = Arrays.copyOf(o.getPosition(), 3);
            cloneObsDTO.rotation = Arrays.copyOf(o.getRotation(), 3);
            cloneObsDTO.scale = Arrays.copyOf(o.getScale(), 3);
            obstacleDTOSet.add(cloneObsDTO);
        }
        ObstacleJsonDTO jsonDTO = new ObstacleJsonDTO();
        jsonDTO.config.rayDensity = this.config.config.rayDensity;
        jsonDTO.obstacles = obstacleDTOSet;

        return jsonDTO;
    }

    @Override
    public void initialize(Simulation simulation) {
        this.simulation = simulation;
        // Clear old Obstacles when Simulation is changed
        if (!this.obstacles.isEmpty()) {
            Set<Obstacle> obs = new HashSet<>(this.getObstacles());
            for (Obstacle o : obs) {
                this.removeObstacle(o);
            }
        }
        try {
            if (this.simulation.getConfig().getObstacleConfigList().isEmpty())
                return;
            this.config = simulation.getConfig().getObstacleConfigList().get(0);

            // Add obstacles to the context
            Set<ObstacleDTO> obstacleDTOSet = this.config.obstacles;
            for (ObstacleDTO o : obstacleDTOSet) {
                this.addObstacle(o);
            }
        } catch (NullPointerException exception) {
            // TODO load default config
        }
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }
}
