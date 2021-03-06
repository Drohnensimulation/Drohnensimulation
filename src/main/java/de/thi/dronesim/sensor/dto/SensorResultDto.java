package de.thi.dronesim.sensor.dto;

import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.sensor.ISensor;

import java.util.ArrayList;
import java.util.List;

public class SensorResultDto {
    private ISensor sensor;
    private List<Obstacle> obstacle = new ArrayList<>();
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

    public List<Float> getValues() {
        return values;
    }

    public void setValues(List<Float> values) {
        this.values = values;
    }
}
