package de.thi.hindernis.dto;

import java.io.Serializable;
import java.util.Set;

public class ObstacleJsonDTO implements Serializable {
    public Set<ObstacleDTO> hindernisse;
    public ObstacleConfigurationDTO config;
}
