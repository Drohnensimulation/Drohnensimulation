package de.thi.dronesim.obstacle.entity;

import de.thi.dronesim.obstacle.dto.HitBoxDTO;

import java.util.Set;

public class Obstacle {
    private String modelName;
    private String modelPath;
    private Long id;
    private Set<HitBoxDTO> hitboxes;
    private Float[] position;
    private Float[] rotation;
    private Float[] scale;

    public Obstacle() {

    }

    public Obstacle(String modelName, String modelPath, Long id, Set<HitBoxDTO> hitboxes, Float[] position, Float[] rotation, Float[] scale) {
        this.modelName = modelName;
        this.modelPath = modelPath;
        this. id = id;
        this.hitboxes = hitboxes;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public String getModelName() {
        return this.modelName;
    }

    public String getModelPath() {
        return this.modelPath;
    }

    public Long getID() {
        return this.id;
    }

    public Set<HitBoxDTO> getHitboxes() {
        return this.hitboxes;
    }

    public Float[] getPosition() {
        return this.position;
    }

    public Float[] getRotation() {
        return this.rotation;
    }

    public Float[] getScale() {
        return this.scale;
    }
}
