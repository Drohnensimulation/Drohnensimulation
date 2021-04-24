package de.thi.dronesim.obstacle;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.obstacle.util.HitBoxRigidBody;
import de.thi.dronesim.obstacle.util.JBulletContext;
import de.thi.dronesim.obstacle.util.JBulletHitMark;

import com.jme3.math.Vector3f;
import java.util.HashSet;
import java.util.Set;

public class UfoObjs implements ISimulationChild, IUfoObjs{
    private final JBulletContext jBullet;
    private final Set<Obstacle> obstacles;

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
     * @param from
     * @param direction
     * @param range
     * @return
     */
    private HitMark rayTest(Vector3f from, Vector3f direction, float range) {
        JBulletHitMark hitBody = jBullet.rayTest(from, direction, range);
        if (hitBody != null) {

            if(hitBoxes.contains(hitBody.body)){
                return new HitMark(hitBody);
            }else{
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
        return null;
    }

    @Override
    public Set<HitMark> checkSensorCylinder(Vector3f origin, Vector3f orientation, Vector3f dimension) {
        return null;
    }

    @Override
    public void load(Object configHolder) {
        //TODO Load from ObstacleJsonDTO
    }

    @Override
    public Object save() {
        //TODO Load the Current State and write it to ObstacleJsonDTO
        return null;
    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }
}
