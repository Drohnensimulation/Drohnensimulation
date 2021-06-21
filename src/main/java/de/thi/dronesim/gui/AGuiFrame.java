package de.thi.dronesim.gui;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;

import javax.swing.*;

/**
 * GuiFrame abstract class
 *
 * @author Michael Weichenrieder
 */
public abstract class AGuiFrame extends JFrame {

    public AGuiFrame(String title) {
        super(title);
        ImageIcon img = new ImageIcon(ClassLoader.getSystemResource("Icons/icon.png"));
        setIconImage(img.getImage());
    }

    public abstract void init(Simulation simulation);

    public abstract void updateDroneStatus(SimulationUpdateEvent simulationUpdateEvent);
}
