package de.thi.hindernis;

import de.thi.hindernis.dto.HindernisDTO;
import de.thi.hindernis.entity.Hindernis;
import de.thi.hindernis.entity.HitMark;

import javax.vecmath.Vector3f;
import java.io.FileNotFoundException;
import java.util.Set;

public interface IUfoObjs {

    /**
     * Generiert aus einem DTO ein neues Hindernis Objekt und fügt es der Physik hinzu
     * @param hindernisDto Beschreibung des Hindernis
     * @return neues Objekt
     */
    Hindernis addHindernis(HindernisDTO hindernisDto);

    /**
     * Versucht ein Hindernis anhand des DTOs zu entfernen (globale ID von Objekten wird hierbei verwendet)
     * @param hindernisDTO Beschreibung des Hindernis
     * @return erfolgsfall
     */
    boolean removeHindernis(HindernisDTO hindernisDTO);

    /**
     * Versucht ein Hindernis anhand eines ReferenzObjektes zu entfernen (globale IDvon Objekten wird hierbei verwendet)
     * @param hindernisObj Referenz Objekt
     * @return erfolgsfall
     */
    boolean removeHindernis(Hindernis hindernisObj);

    /**
     * Gibt alle Aktuell vorhandenen Hindernisse aus
     * @return Set der Aktuellen Hindernisse
     */
    Set<Hindernis> getHindernisse();

    /**
     * Prüft auf Kollisionen entlang eines Kegels
     * Ein Kegelstumpf müsste demnach neu berechnet werden!
     * @param origin Spitzpunkt
     * @param orientation Richtungsvektor
     * @param range Reichweite
     * @param opening Öffnungswinkel angegeben als Steigung
     * @return Referenz zu einem Getroffenen Objekt
     */
    HitMark pruefeSensorKegel(Vector3f origin, Vector3f orientation, float range, Vector3f opening);

    /**
     * Prüft auf Kollisionen entlang einer 4 Seitigen Pyramide
     * @param origin Spitzpunkt
     * @param orientation Richtungsvektor
     * @param range Reichweite
     * @param opening Öffnungswinkel angegeben als Steigung
     * @return Referenz zu einem Getroffenen Objekt
     */
    HitMark pruefeSensorPyramide(Vector3f origin, Vector3f orientation, float range, Vector3f opening);

    /**
     * Prüft auf Kollisionen entlang eines Quaders
     * @param origin Fußpunkt
     * @param orientation Richtung
     * @param dimension Dimension des Quaders (x, y, z/Reichweite)
     * @return Referenz zu einem Getroffenen Objekt
     */
    HitMark pruefeSensorQuader(Vector3f origin, Vector3f orientation, Vector3f dimension);

    /**
     * Prüft auf Kollisionen entlang eines Zylinders
     * @param origin Fußpunkt
     * @param orientation Richtung
     * @param dimension Dimension des Zylinders (x/Durchmesser, y/Durchmesser, z/Reichweite)
     * @return Referenz zu einem Getroffenen Objekt
     */
    HitMark pruefeSensorZylinder(Vector3f origin, Vector3f orientation, Vector3f dimension);


    /**
     * Läd den Umgebungskontext komplett aus der angegebenen Datei
     * @param filePath Pfad zu einer JSON im format {@link de.thi.hindernis.dto.HindernisJsonDTO}
     */
    void loadFromJson(String filePath) throws FileNotFoundException;

    /**
     * Speichert den Aktuellen Umgebungskontext in die angegebene Datei
     * @param filePath Pfad zu einer JSON im format {@link de.thi.hindernis.dto.HindernisJsonDTO}
     */
    void saveToJson(String filePath);
}
