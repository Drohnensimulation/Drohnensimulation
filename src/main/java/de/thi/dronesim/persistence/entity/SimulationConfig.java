package de.thi.dronesim.persistence.entity;

import java.util.List;
import java.util.Objects;

/**
 * Object that holds all configurations for the Simulation.
 *
 * @author Daniel Stolle
 */
public class SimulationConfig {

    private LocationConfig locationConfig;
    private float droneRadius;

    private List<ObstacleConfig> obstacleConfigList;
    private List<SensorConfig> sensorConfigList;
    private List<WindConfig> windConfigList;

    // /////////////////////////////////////////////////////////////////////////////
    // Object Methods
    // /////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimulationConfig that = (SimulationConfig) o;

        if (Float.compare(that.droneRadius, droneRadius) != 0) return false;
        if (locationConfig != null ? !locationConfig.equals(that.locationConfig) : that.locationConfig != null)
            return false;
        if (obstacleConfigList != null ? !obstacleConfigList.equals(that.obstacleConfigList) : that.obstacleConfigList != null)
            return false;
        if (sensorConfigList != null ? !sensorConfigList.equals(that.sensorConfigList) : that.sensorConfigList != null)
            return false;
        return windConfigList != null ? windConfigList.equals(that.windConfigList) : that.windConfigList == null;
    }

    @Override
    public int hashCode() {
        int result = locationConfig != null ? locationConfig.hashCode() : 0;
        result = 31 * result + (droneRadius != +0.0f ? Float.floatToIntBits(droneRadius) : 0);
        result = 31 * result + (obstacleConfigList != null ? obstacleConfigList.hashCode() : 0);
        result = 31 * result + (sensorConfigList != null ? sensorConfigList.hashCode() : 0);
        result = 31 * result + (windConfigList != null ? windConfigList.hashCode() : 0);
        return result;
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Getter/Setter
    // /////////////////////////////////////////////////////////////////////////////

    public LocationConfig getLocationConfig() {
        return locationConfig;
    }

    public void setLocationConfig(LocationConfig locationConfig) {
        this.locationConfig = locationConfig;
    }

    public List<ObstacleConfig> getObstacleConfigList() {
        return obstacleConfigList;
    }

    public void setObstacleConfigList(List<ObstacleConfig> obstacleConfigList) {
        this.obstacleConfigList = obstacleConfigList;
    }

    public List<SensorConfig> getSensorConfigList() {
        return sensorConfigList;
    }

    public void setSensorConfigList(List<SensorConfig> sensorConfigList) {
        this.sensorConfigList = sensorConfigList;
    }

    public List<WindConfig> getWindConfigList() {
        return windConfigList;
    }

    public void setWindConfigList(List<WindConfig> windConfigList) {
        this.windConfigList = windConfigList;
    }

    public float getDroneRadius() {
        return droneRadius;
    }

    public void setDroneRadius(float droneRadius) {
        this.droneRadius = droneRadius;
    }
}
