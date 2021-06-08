package de.thi.dronesim.obstacle.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

public class ObstacleDTO implements Serializable {
    public String modelName;
    public String modelPath;
    public Long id;
    public Set<HitBoxDTO> hitboxes;
    public Float[] position;
    public Float[] rotation;
    public Float[] scale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObstacleDTO that = (ObstacleDTO) o;

        if (modelName != null ? !modelName.equals(that.modelName) : that.modelName != null) return false;
        if (modelPath != null ? !modelPath.equals(that.modelPath) : that.modelPath != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (hitboxes != null ? !hitboxes.equals(that.hitboxes) : that.hitboxes != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(position, that.position)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(rotation, that.rotation)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(scale, that.scale);
    }

    @Override
    public int hashCode() {
        int result = modelName != null ? modelName.hashCode() : 0;
        result = 31 * result + (modelPath != null ? modelPath.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (hitboxes != null ? hitboxes.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(position);
        result = 31 * result + Arrays.hashCode(rotation);
        result = 31 * result + Arrays.hashCode(scale);
        return result;
    }
}
