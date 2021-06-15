package de.thi.dronesim.gui.dview;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.gui.GuiManager;
import de.thi.dronesim.gui.IGuiView;
import de.thi.dronesim.gui.drenderer.DRenderer;
import de.thi.dronesim.gui.dview.component.JCompass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niklas Thurner
 */
public class DView extends JFrame implements IGuiView {

    private boolean toggleWind = false;
    private boolean toggleObstacles = false;
    private int obstaclePanels = 0;
    Map<Integer, JPanel> panelsMap = new HashMap<>();
    private JCompass compass = new JCompass();
    private int y = 0;
    private boolean started = false;
    private boolean paused = false;
    private boolean stopped = false;
    private Simulation simulation;
    private static final Logger logger = LogManager.getLogger();

    private JLabel xCord;
    private JLabel yCord;
    private JLabel zCord;
    private JLabel direction;
    private JLabel airSpeed;
    private JLabel groundSpeed;
    private JButton thirdPerson;
    private JButton birdView;
    private JButton firstPerson;
    private JButton nearbyObstaclesButton;
    private JButton windButton;
    private JPanel graphicPanel;
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
    private JPanel compassPanel;
    private JPanel obstaclePanel;
    private JLabel time;
    private JPanel timePanel;
    private JButton simulationButton;
    private JLabel windData;

    public DView(GuiManager guiManager, DRenderer dRenderer) {

        //TODO simulation must be initialized
        super("Ufo Simulation");
        getContentPane().add(mainPanel);
        Canvas canvas = dRenderer.getCanvas();
        graphicPanel.add(canvas);
        JCompass compass = new JCompass();
        compassPanel.add(compass);
        simulationButton.setText("Start");
        simulationButton.setBackground(Color.GREEN);
        thirdPerson.addActionListener(e -> dRenderer.setPerspective(DRenderer.Perspective.THIRD_PERSON));
        birdView.addActionListener(e -> dRenderer.setPerspective(DRenderer.Perspective.BIRD_VIEW));
        firstPerson.addActionListener(e -> dRenderer.setPerspective(DRenderer.Perspective.FIRST_PERSON));
        nearbyObstaclesButton.addActionListener(this::toggleObstacles);
        windButton.addActionListener(this::toggleWind);
        simulationButton.addActionListener(this::changeButton);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        disableObstacleSidebar();
        disableWindSidebar();
        pack();
        setVisible(true);
    }

    @Override
    public void init(Simulation simulation) {
        // TODO: Use simulation if needed
    }

    /**
     * updates the data displayed in the GUI
     *
     * @param simulationUpdateEvent
     */
    @Override
    public void updateDroneStatus(SimulationUpdateEvent simulationUpdateEvent) {
        Location location = simulationUpdateEvent.getDrone().getLocation();

        xCord.setText(String.valueOf(location.getX()));
        yCord.setText(String.valueOf(location.getY()));
        zCord.setText(String.valueOf(location.getZ()));
        airSpeed.setText(String.valueOf(location.getAirspeed()));
        groundSpeed.setText(String.valueOf(location.getGroundSpeed()));
        direction.setText(String.valueOf(location.getHeading()));
        time.setText(String.valueOf(simulationUpdateEvent.getTime()));
        //TODO windData should be updated as well, when there is data to display

    }

    public void disableObstacleSidebar() {
        toggleObstacles = false;
        obstacles.setVisible(false);
    }

    public void enableObstacleSidebar() {
        toggleObstacles = true;
        obstacles.setVisible(true);
    }

    /**
     * Opens/collapses the obstacles-sidebar
     */
    public void toggleObstacles(ActionEvent e) {
        if (toggleObstacles) {
            disableObstacleSidebar();
        } else {
            enableObstacleSidebar();
        }
        pack();
    }

    private void disableWindSidebar() {
        toggleWind = false;
        wind.setVisible(false);
    }

    private void enableWindSidebar() {
        toggleWind = true;
        wind.setVisible(true);
        compass.setNeedleDirection(70);
        compass.setSize(400, 400);
    }

    /**
     * Opens/collapses the wind-sidebar
     */
    public void toggleWind(ActionEvent e) {
        if (toggleWind) {
            disableWindSidebar();
        } else {
            enableWindSidebar();
        }
        pack();
    }

    /**
     * Starts/pauses or stops the simulation
     */
    private void changeButton(ActionEvent actionEvent) {
        if (!started) {
            simulation.start();
            Thread process = new SimulationThread();
            process.start();
            started = true;
            simulationButton.setText("Pause");
            simulationButton.setBackground(Color.YELLOW);
        } else if (!paused && simulation.isRunning()) {
            //TODO pause Button missing
            paused = true;
            simulationButton.setText("Resume");
            simulationButton.setBackground(Color.GREEN);
        } else if (paused) {
            //TODO continue Button missing
            paused = false;
            simulationButton.setText("Pause");
            simulationButton.setBackground(Color.YELLOW);
        } else if (stopped) {
            simulation.stop();
        }
    }

    /**
     * Adds a data-panel for an obstacle to the sidebar
     *
     * @param id the id of the new obstacle-panel
     */
    public void addObstacle(int id) {
        JPanel obstacle = new JPanel(new GridBagLayout());
        JLabel header = new JLabel("Obstacle:");  //TODO, the name or the Id of the obstacle displayed in the header
        JLabel value = new JLabel(); //TODO, the value/values of the obstacle
        int yInside = 0;

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = yInside;
        constraints.insets = new Insets(5, 5, 5, 5);
        obstacle.add(header, constraints);

        constraints.gridy = ++yInside;
        value.setHorizontalAlignment(JLabel.CENTER);
        value.setVerticalAlignment(JLabel.CENTER);
        obstacle.add(value, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = y;
        panelsMap.put(id, obstacle);
        obstacle.setBorder(BorderFactory.createRaisedBevelBorder());
        obstaclePanel.add(obstacle, constraints);

        y++;
        pack();
    }

    /**
     * Removes a data-panel of an obstacle from the sidebar
     *
     * @param id the id of the obstacle-panel, that should be removed
     */
    public void removeObstacle(int id) {
        JPanel obstacle = panelsMap.get(id);
        obstaclePanel.remove(obstacle);
        panelsMap.remove(id);
        obstaclePanels--;
        pack();
    }


    /**
     * does nothing while the simulation is running
     * but changes the button once it stops, this is not the final solution
     */
    private class SimulationThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (!simulation.isRunning())
                    break;
                // do Nothing
            }
            stopped = true;
            simulationButton.setText("Stop");
            simulationButton.setBackground(Color.RED);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                logger.error("Interrupted the process", e);
            }
        }
    }
}
