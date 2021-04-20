package de.thi.dronesim.persistence.entity;

import java.util.List;
import java.util.Objects;

/**
 * Object that holds all configurations for the Simulation.
 */
public class SimulationConfig {

    private LocationConfig locationConfig;

    private List<ObstacleConfig> obstacleConfigList;
    private List<SensorConfig> sensorConfigList;
    private List<WindConfig> windConfigList;

    // /////////////////////////////////////////////////////////////////////////////
    // Object Methods
    // /////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimulationConfig that = (SimulationConfig) o;

        if (!Objects.equals(locationConfig, that.locationConfig)) {
            return false;
        }
        if (!Objects.equals(obstacleConfigList, that.obstacleConfigList)) {
            return false;
        }
        if (!Objects.equals(sensorConfigList, that.sensorConfigList)) {
            return false;
        }
        return Objects.equals(windConfigList, that.windConfigList);
    }

    @Override
    public int hashCode() {
        int result = locationConfig != null ? locationConfig.hashCode() : 0;
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
}
