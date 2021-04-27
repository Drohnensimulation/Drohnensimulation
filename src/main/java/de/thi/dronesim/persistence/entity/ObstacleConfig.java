package de.thi.dronesim.persistence.entity;

import de.thi.dronesim.obstacle.dto.ObstacleJsonDTO;

/**
 * Object that holds all Configurations to a Obstacle.
 *
 * @author Daniel Stolle
 * @see ObstacleJsonDTO
 */
public class ObstacleConfig extends ObstacleJsonDTO {

    // /////////////////////////////////////////////////////////////////////////////
    // Object Methods
    // /////////////////////////////////////////////////////////////////////////////

    public ObstacleConfig(){
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObstacleConfig that = (ObstacleConfig) o;

        return this.hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        return this.obstacles.hashCode() * 8969 + this.config.rayDensity;
    }
}
