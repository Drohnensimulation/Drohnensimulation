package de.thi.dronesim.obstacle;

import com.jme3.math.Vector3f;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.SimulationUpdateListener;
import de.thi.dronesim.drone.Drone;

/**
 * @author Christian Schmied
 */
public class DroneCrashListener implements SimulationUpdateListener {

    public static final int LISTENER_PRIORITY = 600;

    private final IUfoObjs ufoObjs;

    DroneCrashListener(IUfoObjs ufoObjs) {
        this.ufoObjs = ufoObjs;
    }

    @Override
    public void onUpdate(SimulationUpdateEvent event) {
        if (event.getDrone().isCrashed()) {
            return;
        }

        Drone theDrone = event.getDrone();
        Vector3f dronePosition = theDrone.getLocation().getPosition();
        float droneRadius = theDrone.getRadius();

        if (ufoObjs.checkSphereCollision(dronePosition, droneRadius)) {
            theDrone.setCrashed(true);
        }
    }
}
