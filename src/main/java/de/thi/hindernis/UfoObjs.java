package de.thi.hindernis;

import com.google.gson.Gson;
import de.thi.hindernis.dto.HindernisDTO;
import de.thi.hindernis.dto.HindernisJsonDTO;
import de.thi.hindernis.entity.Hindernis;
import de.thi.hindernis.entity.HitMark;

import javax.vecmath.Vector3f;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Set;

public class UfoObjs implements IUfoObjs {

    private static UfoObjs instance;

    private UfoObjs() {

    }

    public static UfoObjs getInstance() {
        if (instance == null) {
            instance = new UfoObjs();
        }
        return instance;
    }

    @Override
    public Hindernis addHindernis(HindernisDTO hindernisDto) {
        return null;
    }

    @Override
    public boolean removeHindernis(HindernisDTO hindernisDTO) {
        return false;
    }

    @Override
    public boolean removeHindernis(Hindernis hindernisObj) {
        return false;
    }

    @Override
    public Set<Hindernis> getHindernisse() {
        return null;
    }

    @Override
    public HitMark pruefeSensorKegel(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        return null;
    }

    @Override
    public HitMark pruefeSensorPyramide(Vector3f origin, Vector3f orientation, float range, Vector3f opening) {
        return null;
    }

    @Override
    public HitMark pruefeSensorQuader(Vector3f origin, Vector3f orientation, Vector3f dimension) {
        return null;
    }

    @Override
    public HitMark pruefeSensorZylinder(Vector3f origin, Vector3f orientation, Vector3f dimension) {
        return null;
    }

    @Override
    public void loadFromJson(String filePath) throws FileNotFoundException {
        FileReader fr = new FileReader(filePath);
        Gson gson = new Gson();
        HindernisJsonDTO jsonDto = gson.fromJson(fr, HindernisJsonDTO.class);
        //TODO Clear old Physics Environment
        //TODO Load JSON Objects into Physics
    }

    @Override
    public void saveToJson(String filePath) {
    }
}
