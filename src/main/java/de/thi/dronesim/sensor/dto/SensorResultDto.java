package de.thi.dronesim.sensor.dto;

import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.sensor.ISensor;
import de.thi.dronesim.sensor.types.DistanceSensor;

import java.util.List;

public class SensorResultDto {
    private ISensor sensor;
    private List<Obstacle> obstacle;
    private List<Float> values;

    public ISensor getSensor() {
        return sensor;
    }

    public void setSensor(ISensor sensor) {
        this.sensor = sensor;
    }

    public List<Obstacle> getObstacle() {
        return obstacle;
    }

    public void setObstacle(List<Obstacle> obstacle) {
        this.obstacle = obstacle;
    }

    public List<Float> getValues() {
        return values;
    }

    public void setValues(List<Float> values) {
        this.values = values;
    }
}
