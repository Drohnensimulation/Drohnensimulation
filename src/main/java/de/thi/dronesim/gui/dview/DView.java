package de.thi.dronesim.gui.dview;

import de.thi.dronesim.gui.IGuiView;
import de.thi.dronesim.gui.drenderer.DRenderer;
import de.thi.dronesim.drone.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Niklas Thurner
 */
public class DView extends JFrame implements IGuiView {

    private boolean toggleWind = false;
    private boolean toggleObstacles = false;
    private JLabel xCord;
    private JLabel yCord;
    private JLabel zCord;
    private JLabel direction;
    private JPanel graphicPanel;
    private JLabel airSpeed;
    private JLabel groundSpeed;
    private JButton thirdPerson;
    private JButton birdView;
    private JButton firstPerson;
    private JButton nearbyObstaclesButton;
    private JButton windButton;
    private JPanel obstacles;
    private JPanel airSpeedPanel;
    private JPanel groundSpeedPanel;
    private JPanel directionPanel;
    private JPanel xCordPanel;
    private JPanel yCordPanel;
    private JPanel zCordPanel;
    private JPanel buttons;
    private JPanel wind;
    private JPanel mainPanel;
    private JPanel dataPanel;

    public DView(DRenderer dRenderer) {
        super("Ufo Simulation");
        getContentPane().add(mainPanel);
        Canvas canvas = dRenderer.getCanvas();
        graphicPanel.add(canvas);
        thirdPerson.addActionListener(e -> dRenderer.setPerspective(DRenderer.Perspective.THIRD_PERSON));
        birdView.addActionListener(e -> dRenderer.setPerspective(DRenderer.Perspective.BIRD_VIEW));
        firstPerson.addActionListener(e -> dRenderer.setPerspective(DRenderer.Perspective.FIRST_PERSON));
        nearbyObstaclesButton.addActionListener(this::toggleObstacles);
        windButton.addActionListener(this::toggleWind);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        disableObstacleSidebar();
        disableWindSidebar();
        pack();
        setVisible(true);
    }

    /**
     * updates the data displayed in the GUI
     * @param location
     */
    @Override
    public void updateDroneStatus(Location location) {
        xCord.setText(String.valueOf(location.getX()));
        yCord.setText(String.valueOf(location.getY()));
        zCord.setText(String.valueOf(location.getZ()));
        airSpeed.setText(String.valueOf(location.getAirspeed()));
        groundSpeed.setText(String.valueOf(location.getGroundSpeed()));
        direction.setText(String.valueOf(location.getHeading()));
    }

    public void disableObstacleSidebar () {
        toggleObstacles = false;
        obstacles.setVisible(false);
    }

    public void enableObstacleSidebar () {
        toggleObstacles = true;
        obstacles.setVisible(true);
    }

    /**
     * Opens/collapses the obstacles-sidebar
     */
    public void toggleObstacles(ActionEvent e) {
        if(toggleObstacles) {
            disableObstacleSidebar();
        } else {
            enableObstacleSidebar();
        }
        this.pack();
    }

    private void disableWindSidebar() {
        toggleWind = false;
        wind.setVisible(false);
    }

    private void enableWindSidebar() {
        toggleWind = true;
        wind.setVisible(true);
    }

    /**
     * Opens/collapses the wind-sidebar
     */
    public void toggleWind(ActionEvent e) {
        if(toggleWind) {
            disableWindSidebar();
        } else {
            enableWindSidebar();
        }
        this.pack();
    }
}
