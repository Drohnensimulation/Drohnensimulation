package de.thi.dronesim.sensor.dto;

import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.sensor.types.ASensor;

import java.util.List;

public class SensorResultDto {
    private ASensor sensor;
    private Obstacle obstacle;
    private List<Float> values;

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

    public List<Float> getValues() {
        return values;
    }

    public void setValues(List<Float> values) {
        this.values = values;
    }
}
