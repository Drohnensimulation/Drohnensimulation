package de.thi.dronesim.sensor.dto;

import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.sensor.ASensor;

import java.util.Set;

public class SensorResultDto {
    private ASensor sensor;
    private Obstacle obstacle;
    private Set<Float> values;

    public ASensor getSensor() {
        return sensor;
    }

    public void setSensor(ASensor sensor) {
        this.sensor = sensor;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public void setObstacle(Obstacle obstacle) {
        this.obstacle = obstacle;
    }

    public Set<Float> getValues() {
        return values;
    }

    public void setValues(Set<Float> values) {
        this.values = values;
    }
}
