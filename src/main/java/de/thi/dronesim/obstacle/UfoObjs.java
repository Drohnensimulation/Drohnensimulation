package de.thi.dronesim.obstacle;

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

import com.jme3.math.Vector3f;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UfoObjs implements ISimulationChild, IUfoObjs {
    private final JBulletContext jBullet;
    private final Set<Obstacle> obstacles;

    private ObstacleJsonDTO config;

    /**
     * Actually this Set is just for "rechecking", it's totally useless
     */
    private final Set<HitBoxRigidBody> hitBoxes;
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
        Obstacle obstacle = new Obstacle(obstacleDto.modelName,obstacleDto.modelPath,obstacleDto.id,obstacleDto.position,obstacleDto.rotation,obstacleDto.scale);

        Set<HitBoxRigidBody> objectHitBoxes = new HashSet<>();

        // Get all hit boxes from obstacleDTO
        for(HitBoxDTO hit : obstacleDto.hitboxes) {
            float[] position = {hit.position[0],hit.position[1],hit.position[2]};
            float[] rotation = {hit.rotation[0],hit.rotation[1],hit.rotation[2]};
            float[] dimension = {hit.dimension[0],hit.dimension[1],hit.dimension[2]};

            // Add hit boxes to the jBullet context
            HitBoxRigidBody hitBoxRigidBody = jBullet.addHitBox(new javax.vecmath.Vector3f(position),new javax.vecmath.Vector3f(rotation), new javax.vecmath.Vector3f(dimension),obstacle);

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
        for(Obstacle o : obstacles) {
            if(o.getID().equals(obstacleDTO.id)) {
                obstacleToDelete = o;
            }
        }

        if(obstacleToDelete != null) {
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
        if(obstacles.contains(obstacleObj)) {
            Set<HitBoxRigidBody> hitBoxRigidBodies = obstacleObj.getHitboxes();
            for(HitBoxRigidBody h : hitBoxRigidBodies) {
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
    	Set<HitMark> hits = new HashSet<>();
    	
    	//TODO: Document algorithm process
    	float goldenRatio = (1f + (float) Math.sqrt(5)) / 2;
        float angle = 2 * (float) Math.PI * goldenRatio;
        float dist;
        
        Vector3f angleVec = opening.normalize();
        Vector3f direction = orientation.normalize();
        Vector3f rangeProjOnDir = direction.mult(angleVec.dot(direction));
        
        Vector3f i = rangeProjOnDir.subtract(angleVec);
        float radius = i.length();
        i.normalizeLocal();
        Vector3f j = i.cross(direction).normalizeLocal();
        
        Vector3f dI = new Vector3f();
        Vector3f dJ = new Vector3f();
        Vector3f ray = new Vector3f();
        int rayCount = 300;
        
        for (int l = 0; l < rayCount; l++) {
            dist = (float) Math.sqrt(l / (rayCount - 1f)) * radius;

            dI.set(i.mult(dist * (float) Math.cos(angle * l)));
            dJ.set(j.mult(dist * (float) Math.sin(angle * l)));
            ray.set(rangeProjOnDir.x + dI.x + dJ.x,
                    rangeProjOnDir.y + dI.y + dJ.y,
                    rangeProjOnDir.z + dI.z + dJ.z).normalizeLocal();
            
            HitMark hit = this.rayTest(origin, ray, range);
            if(hit != null) {
            	hits.add(hit);
            }
        }
        return hits;
    }

    @Override
    public Set<HitMark> checkSensorPyramid(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        return null;
    }

    @Override
    public Set<HitMark> checkSensorCuboid(Vector3f origin, Vector3f orientation, Vector3f dimension) {

        // TODO: calc the rotation from the orientation
        // Orientation isn't a normal, orientation is more the angles creating the normal...
        // So its possible to add a Facing Direction to an object...
        // Think of the Direction your Phone is facing to.
        // Attach a barbecuescrew to your phone, marking the origin at one end and the Vector being the screw.
        // You can freely spin your phone around it like a "Spanferkel" while its still having the same vector.

        float rotation = 0;

        Set<HitMark> hits = new HashSet<>();
        int ppm = this.config.config.rayDensity;

        Vector3f normal = orientation.normalize();
        float range = dimension.z;

        Vector3f center = new Vector3f(origin);

        Vector3f verticalVect;
        Vector3f horizontalVect;

        Vector3f rotVect = new Vector3f((float) Math.cos(rotation), (float) Math.sin(rotation), 0); // |rotVect| = 1

        // rotVect * normal = |rotVect|*|normal|*cos(angle(rotVect,normal))
        // rotVect * normal = cos(phi)
        float phi = rotVect.dot(normal);

        if (phi > 0.9999) {
            // Rotation Vector and Normal are too Paralell
            // TODO do other calculation for verticalVect and horizontalVect
            // TODO eg swap x and z Axis
            throw new IllegalArgumentException("TODO fix Calculations of Vectors when they are too similar");
        } else {
            verticalVect = normal.cross(rotVect).normalizeLocal();
            horizontalVect = normal.cross(verticalVect);
        }


        //topLeft Should be the Point where the Sensor will start rastering;
        Vector3f topLeft = center.subtract(horizontalVect);
        topLeft.subtract(verticalVect, topLeft);

        //Rasterization based on width (needed rays to match roughly density of rays)
        int neededRaysX = (int) dimension.x * ppm;
        int neededRaysY = (int) dimension.y * ppm;
        float stepSizeX = dimension.x / neededRaysX;
        float stepSizeY = dimension.y / neededRaysY;
        Vector3f horizontalStep = horizontalVect.mult(stepSizeX);
        Vector3f verticalStep = horizontalVect.mult(stepSizeY);

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
    	Set<HitMark> hits = new HashSet<>();
    	Random rand = new Random();

    	//TODO: Document algorithm process
    	//if dimensions of the cylinder base differs take the biggest one (error forgiving)
    	float radius = (dimension.x >= dimension.y)? dimension.x/2f : dimension.y/2f;
        float range = dimension.z;
        //calculate needed rays count based on cylinder base area = PI * r^2
        int density = config.config.rayDensity;
        int rayCount = (int) ((radius *density) * (radius * density) * Math.PI);
        float dist;

    	//using golden Ration Distribution to equally distribute rays
    	float goldenRatio = (1f + (float) Math.sqrt(5)) / 2;
        float angle = 2 * (float) Math.PI * goldenRatio;
        
        //create a random Point to generate a vector perpendicular to direction 
        Vector3f direction = orientation.normalize();
        Vector3f randomPoint = new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        Vector3f pointProjOnDir = direction.mult(randomPoint.dot(direction));
        
        //Vectors needed to generate Rays
        Vector3f i = randomPoint.subtract(pointProjOnDir).normalizeLocal();
        Vector3f j = i.cross(direction).normalizeLocal();
        Vector3f dI = new Vector3f();
        Vector3f dJ = new Vector3f();
        //Ray starting Point
        Vector3f startPoint = new Vector3f();
        
        for (int l = 0; l < rayCount; l++) {
            dist = (float) Math.sqrt(l / (rayCount - 1f)) * radius;

            dI.set(i.mult(dist * (float) Math.cos(angle * l)));
            dJ.set(j.mult(dist * (float) Math.sin(angle * l)));
            startPoint.set(origin.x + dI.x + dJ.x,
            		origin.y + dI.y + dJ.y,
            		origin.z + dI.z + dJ.z).normalizeLocal();
            
            HitMark hit = this.rayTest(startPoint, direction, range);
            if(hit != null) {
            	hits.add(hit);
            }
        }
        return hits;
    }

    @Override
    public ObstacleJsonDTO save() {
        //TODO Clone the Current State and write it to an ObstacleJsonDTO
        return null;
    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        try {
            this.config = simulation.getConfig().getObstacleConfigList().get(0);
        }catch (NullPointerException exception){
            // TODO load default config
        }
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }
}
