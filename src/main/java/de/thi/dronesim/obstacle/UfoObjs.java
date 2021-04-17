package de.thi.dronesim.obstacle;

import com.bulletphysics.dynamics.RigidBody;
import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.obstacle.util.JBulletContext;
import de.thi.dronesim.obstacle.util.JBulletHitMark;

import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UfoObjs implements IUfoObjs {
    private static UfoObjs instance;
    private final JBulletContext jBullet;
    private final Map<RigidBody, Obstacle> bodyObstalceMap;

    private UfoObjs() {
        jBullet = new JBulletContext();
        bodyObstalceMap = new HashMap<>();
    }

    public static UfoObjs getInstance() {
        if (instance == null) {
            instance = new UfoObjs();
        }
        return instance;
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
            Obstacle obs = bodyObstalceMap.get(hitBody.body);
            if(obs != null){
                return new HitMark(hitBody, obs);
            }else{
                throw new RuntimeException("No Obstacle found for Provided HitBox");
            }
        }
        return null;
    }

    @Override
    public Obstacle addObstacle(ObstacleDTO obstacleDto) {
        //TODO Create Obstacle from DTO
        //TODO Add Obstacle into bodyObstacleMap for each Created CollisionBox
        return null;
    }

    @Override
    public boolean removeObstacle(ObstacleDTO obstacleDTO) {
        //TODO Remove All HitBoxes from JBullet
        //TODO Remove All HitBoxes from bodyObstacleMap
        return false;
    }

    @Override
    public boolean removeObstacle(Obstacle obstacleObj) {
        //TODO Remove All HitBoxes from JBullet
        //TODO Remove All HitBoxes from bodyObstacleMap
        return false;
    }

    @Override
    public Set<Obstacle> getObstacles() {
        //TODO
        return null;
    }

    @Override
    public Set<HitMark> pruefeSensorCone(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
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
}
