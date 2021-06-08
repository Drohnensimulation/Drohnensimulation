package de.thi.dronesim.obstacle.dto;

import java.io.Serializable;

public class ObstacleConfigurationDTO implements Serializable {
    public Integer rayDensity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObstacleConfigurationDTO that = (ObstacleConfigurationDTO) o;

        return rayDensity != null ? rayDensity.equals(that.rayDensity) : that.rayDensity == null;
    }

    @Override
    public int hashCode() {
        return rayDensity != null ? rayDensity.hashCode() : 0;
    }
}
