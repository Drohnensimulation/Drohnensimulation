package de.thi.dronesim.obstacle.dto;

import java.io.Serializable;
import java.util.Set;

public class ObstacleDTO implements Serializable {
    public String modelName;
    public String modelPath;
    public Long id;
    public Set<HitBoxDTO> hitboxes;
    public Float[] position;
    public Float[] rotation;
    public Float[] scale;
}
