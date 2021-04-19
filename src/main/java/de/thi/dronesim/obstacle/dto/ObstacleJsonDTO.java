package de.thi.dronesim.obstacle.dto;

import java.io.Serializable;
import java.util.Set;

public class ObstacleJsonDTO implements Serializable {
    public Set<ObstacleDTO> hindernisse;
    public ObstacleConfigurationDTO config;
}
