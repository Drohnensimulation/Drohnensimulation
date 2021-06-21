package de.thi.dronesim.helpers;

import javax.vecmath.Vector3f;

/**
 * Contains Helper Methods to generate VecMath Vectors
 *
 * @author Christian Schmied
 */
public class VecMathHelper {
    public static Vector3f of(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    public static Vector3f of(com.jme3.math.Vector3f direction) {
        return new Vector3f(direction.x, direction.y, direction.z);
    }

    public static Vector3f of(float[] array) {
        if (array.length < 3) {
            throw new IllegalArgumentException("Array must hold at least 3 values");
        }
        return new Vector3f(array);
    }

    public static Vector3f of(Float[] array) {
        if (array.length < 3) {
            throw new IllegalArgumentException("Array must hold at least 3 values");
        }
        return new Vector3f(array[0], array[1], array[2]);
    }


    public static float[] toArray(Vector3f vect) {
        return new float[]{vect.x, vect.y, vect.z};
    }
}
