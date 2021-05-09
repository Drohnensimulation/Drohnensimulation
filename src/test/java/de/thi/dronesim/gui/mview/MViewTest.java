package de.thi.dronesim.gui.mview;

import com.formdev.flatlaf.FlatDarkLaf;
import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.drone.Location;

/**
 * Tests the MView GUI.
 *
 * @author Daniel Dunger
 */
public class MViewTest {

    public static void main(String[] args) {
        // Dark theme, just to be consistent with DView
        FlatDarkLaf.install();

        new Thread(() -> new MView()).start();
    }
}
