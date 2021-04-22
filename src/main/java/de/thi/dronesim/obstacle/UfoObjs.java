package de.thi.dronesim.obstacle;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.obstacle.dto.ObstacleJsonDTO;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.obstacle.util.HitBoxRigidBody;
import de.thi.dronesim.obstacle.util.JBulletContext;
import de.thi.dronesim.obstacle.util.JBulletHitMark;

import com.jme3.math.Vector3f;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

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
     * @return
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
        //TODO Create Obstacle from DTO
        //TODO Add Object to obstacles
        //TODO Add Obstacle into hitBoxes for each Created CollisionBox
        return null;
    }

    @Override
    public boolean removeObstacle(ObstacleDTO obstacleDTO) {
        //TODO Remove All HitBoxes from JBullet
        //TODO Remove Object from obstacles
        //TODO Remove All relevant HitBoxes from hitBoxes
        return false;
    }

    @Override
    public boolean removeObstacle(Obstacle obstacleObj) {
        //TODO Remove All HitBoxes from JBullet
        //TODO Remove Object from obstacles
        //TODO Remove All relevant HitBoxes from hitBoxes
        return false;
    }

    @Override
    public Set<Obstacle> getObstacles() {
        return obstacles;
    }

    @Override
    public Set<HitMark> checkSensorCone(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        return null;
    }

    @Override
    public Set<HitMark> checkSensorPyramid(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        return null;
    }

    @Override
    public Set<HitMark> checkSensorCuboid(Vector3f origin, Vector3f orientation, Vector3f dimension) {

        //TODO calc the rotation from the orientation
        // Orientation isn't a normal, orientation is more the angles creating the normal...

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
        return null;
    }

    @Override
    public ObstacleJsonDTO save() {
        //TODO Clone the Current State and write it to an ObstacleJsonDTO
        return null;
    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        this.config = simulation.getConfig().getObstacleConfigList().get(0);
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }
}
