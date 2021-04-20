package de.thi.dronesim.persistence.entity;

/**
 * Object that holds all Configurations to a Obstacle.
 */
public class ObstacleConfig {

    // TODO: add attributes, overwrite equals and hashcode

    private int dummy;

    // /////////////////////////////////////////////////////////////////////////////
    // Object Methods
    // /////////////////////////////////////////////////////////////////////////////


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObstacleConfig that = (ObstacleConfig) o;

        return dummy == that.dummy;
    }

    @Override
    public int hashCode() {
        return dummy;
    }
}
