package de.thi.dronesim.obstacle;

import com.jme3.math.Vector3f;
import de.thi.dronesim.obstacle.dto.ObstacleDTO;
import de.thi.dronesim.obstacle.dto.ObstacleJsonDTO;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.obstacle.entity.Obstacle;

import java.util.Set;

/**
 * @author Christian Schmied
 */
public interface IUfoObjs {

    /**
     * Generiert aus einem DTO ein neues Hindernis Objekt und fügt es der Physik hinzu
     * @param obstacleDto Beschreibung des Hindernis
     * @return neues Objekt
     */
    Obstacle addObstacle(ObstacleDTO obstacleDto);

    /**
     * Versucht ein Hindernis anhand des DTOs zu entfernen (globale ID von Objekten wird hierbei verwendet)
     * @param obstacleDTO Beschreibung des Hindernis
     * @return erfolgsfall
     */
    boolean removeObstacle(ObstacleDTO obstacleDTO);

    /**
     * Versucht ein Hindernis anhand eines ReferenzObjektes zu entfernen (globale IDvon Objekten wird hierbei verwendet)
     * @param obstacleObj Referenz Objekt
     * @return erfolgsfall
     */
    boolean removeObstacle(Obstacle obstacleObj);

    /**
     * Gibt alle Aktuell vorhandenen Hindernisse aus
     * @return Set der Aktuellen Hindernisse
     */
    Set<Obstacle> getObstacles();

    /**
     * Prüft auf Kollisionen entlang eines Kegels
     * Ein Kegelstumpf müsste demnach neu berechnet werden!
     * @param origin Spitzpunkt
     * @param orientation Richtungsvektor
     * @param range Reichweite
     * @param opening Öffnungswinkel angegeben als Steigung
     * @return Referenz zu einem Getroffenen Objekt
     */
    Set<HitMark> checkSensorCone(Vector3f origin, Vector3f orientation, float range, Vector3f opening);

    /**
     * Prüft auf Kollisionen entlang einer 4 Seitigen Pyramide
     * @param origin Spitzpunkt
     * @param orientation Richtungsvektor
     * @param range Reichweite
     * @param opening Öffnungswinkel angegeben als Steigung
     * @return Referenz zu einem Getroffenen Objekt
     */
    Set<HitMark> checkSensorPyramid(Vector3f origin, Vector3f orientation, float range, Vector3f opening);

    /**
     * Prüft auf Kollisionen entlang eines Quaders
     * @param origin Fußpunkt
     * @param orientation Richtung
     * @param dimension Dimension des Quaders (x, y, z/Reichweite)
     * @return Referenz zu einem Getroffenen Objekt
     */
    @Deprecated
    Set<HitMark> checkSensorCuboid(Vector3f origin, Vector3f orientation, Vector3f dimension);

    /**
     * Prüft auf Kollisionen entlang eines Quaders
     * @param origin Fußpunkt
     * @param orientation Richtung
     * @param dimension Dimension des Quaders (x, y, z/Reichweite)
     * @param rotation Roation around the orientation Vector in RAD where 0 is straight up into the sky
     * @return Referenz zu einem Getroffenen Objekt
     */
    Set<HitMark> checkSensorCuboid(Vector3f origin, Vector3f orientation, Vector3f dimension, float rotation);

    /**
     * Prüft auf Kollisionen entlang eines Zylinders
     * @param origin Fußpunkt
     * @param orientation Richtung
     * @param dimension Dimension des Zylinders (x/Durchmesser, y/Durchmesser, z/Reichweite)
     * @return Referenz zu einem Getroffenen Objekt
     */
    Set<HitMark> checkSensorCylinder(Vector3f origin, Vector3f orientation, Vector3f dimension);

    /**
     * Extracts the current state into the ConfigDTO
     */
    ObstacleJsonDTO save();
}
