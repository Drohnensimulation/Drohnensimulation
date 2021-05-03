package de.thi.dronesim.obstacle.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ObstacleJsonDTO implements Serializable {
    public Set<ObstacleDTO> obstacles;
    public ObstacleConfigurationDTO config;

    public ObstacleJsonDTO(){
        this.obstacles = new HashSet<>();
        this.config = new ObstacleConfigurationDTO();
    }
}
