package de.thi.dronesim.sensor.dto;

import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.sensor.ASensor;

import java.util.Set;

public class SensorResultDto {
    private ASensor sensor;
    private Obstacle obstacle;
    private Set<Float> values;
}
