package de.thi.dronesim.obstacle.entity;

import de.thi.dronesim.obstacle.dto.HitBoxDTO;
import de.thi.dronesim.obstacle.util.HitBoxRigidBody;

import java.util.Set;

/**
 * @author Christian Schmied
 * @author Michael Küchenmeister
 */
public class Obstacle {
    private String modelName;
    private String modelPath;
    private Long id;
    private Float[] position;
    private Float[] rotation;
    private Float[] scale;
    private Set<HitBoxRigidBody> hitboxes;
    private Set<HitBoxDTO> dtoHitboxes;

    public Obstacle() {

    }

    public Obstacle(String modelName, String modelPath, Long id,  Float[] position, Float[] rotation, Float[] scale, Set<HitBoxDTO> dtoHitboxes) {
        this.modelName = modelName;
        this.modelPath = modelPath;
        this. id = id;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.dtoHitboxes = dtoHitboxes;
    }

    public void setHitBoxRigidBodys(Set<HitBoxRigidBody> rigidBody) {
        this.hitboxes = rigidBody;
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

    public Set<HitBoxRigidBody> getHitboxes() {
        return this.hitboxes;
    }

    public Set<HitBoxDTO> getDtoHitboxes() { return this.dtoHitboxes; }

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
