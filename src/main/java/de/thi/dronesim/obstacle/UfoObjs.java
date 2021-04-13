package de.thi.dronesim.obstacle;

import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;

import javax.vecmath.Vector3f;
import java.util.Set;

public class UfoObjs implements IUfoObjs {
    private static UfoObjs instance;

    private UfoObjs() {

    }

    public static UfoObjs getInstance() {
        if (instance == null) {
            instance = new UfoObjs();
        }
        return instance;
    }

    @Override
    public Obstacle addObstacle(ObstacleDTO obstacleDto) {
        return null;
    }

    @Override
    public boolean removeObstacle(ObstacleDTO obstacleDTO) {
        return false;
    }

    @Override
    public boolean removeObstacle(Obstacle obstacleObj) {
        return false;
    }

    @Override
    public Set<Obstacle> getObstacles() {
        return null;
    }

    @Override
    public HitMark pruefeSensorCone(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        return null;
    }

    @Override
    public HitMark checkSensorPyramid(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        return null;
    }

    @Override
    public HitMark checkSensorCuboid(Vector3f origin, Vector3f orientation, Vector3f dimension) {
        return null;
    }

    @Override
    public HitMark checkSensorCylinder(Vector3f origin, Vector3f orientation, Vector3f dimension) {
        return null;
    }

    @Override
    public void load(Object configHolder) {

    }

    @Override
    public Object save() {
        return null;
    }
}
