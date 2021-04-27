package de.thi.dronesim.persistence.entity;

/**
 * Object that holds all configurations to a Wind-Layer.
 *
 * @author Daniel Stolle
 */
public class WindConfig {

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

        WindConfig that = (WindConfig) o;

        return dummy == that.dummy;
    }

    @Override
    public int hashCode() {
        return dummy;
    }
}
