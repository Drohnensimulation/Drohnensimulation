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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObstacleJsonDTO that = (ObstacleJsonDTO) o;

        if (obstacles != null ? !obstacles.equals(that.obstacles) : that.obstacles != null) return false;
        return config != null ? config.equals(that.config) : that.config == null;
    }

    @Override
    public int hashCode() {
        int result = obstacles != null ? obstacles.hashCode() : 0;
        result = 31 * result + (config != null ? config.hashCode() : 0);
        return result;
    }
}
