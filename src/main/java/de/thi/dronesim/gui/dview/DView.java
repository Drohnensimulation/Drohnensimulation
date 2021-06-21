package de.thi.dronesim.gui.dview;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.gui.AGuiFrame;
import de.thi.dronesim.gui.GuiManager;
import de.thi.dronesim.gui.drenderer.DRenderer;
import de.thi.dronesim.gui.dview.component.JCompass;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.sensor.SensorModule;
import de.thi.dronesim.sensor.dto.SensorResultDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niklas Thurner
 */
public class DView extends AGuiFrame {

    private boolean toggleWind = false;      //tells, if the wind-sidebar is open
    private boolean toggleObstacles = false; //tells, if the obstacles-sidebar is open
    private boolean started = false;         //tells, if the simulation has started
    private boolean paused = false;          //tells, if the simulation is paused
    private boolean stopped = false;         //tells, if the simulation has stopped

    private int minutes;                     //stores the amount of minutes, that already have passed
    private int hours;                       //stores the amount of hours, that already have passed
    private int y = 0;                       //is responsible for the obstacle-panels to appear beneath one another

    Map<Integer, JPanel> panelsMap = new HashMap<>();            //stores the obstaclePanels, that are created and removed dynamically

    private final JCompass compass = new JCompass();             //describes the compass, being used to display the direction of the wind

    private Simulation simulation;                               //stores the simulation, that is currently ongoing

    //All JLabels, that are either static headers, or dynamic values
    private JLabel xCord;
    private JLabel yCord;
    private JLabel zCord;
    private JLabel direction;
    private JLabel airSpeed;
    private JLabel groundSpeed;
    private JLabel time;
    private JLabel windData;
    private JLabel status;

    //All JButtons
    private JButton thirdPerson;
    private JButton birdView;
    private JButton firstPerson;
    private JButton nearbyObstaclesButton;
    private JButton windButton;
    private JButton simulationButton;

    //All JPanel, which are either used for wrapping, or to frame something
    private JPanel graphicPanel;
    private JPanel obstacles;
    private JPanel wind;
    private JPanel mainPanel;
    private JPanel compassPanel;
    private JPanel obstaclePanel;
    private JPanel airSpeedPanel;
    private JPanel groundSpeedPanel;
    private JPanel directionPanel;
    private JPanel xCordPanel;
    private JPanel yCordPanel;
    private JPanel zCordPanel;
    private JPanel buttons;
    private JPanel dataPanel;
    private JPanel timePanel;

    /**
     * The DView is used to display the data from the simulation.
     * The DView is the complexer UI-Form, in which one
     * can see the drone flying through the scenario
     *
     * @param guiManager
     * @param dRenderer
     */
    public DView(GuiManager guiManager, DRenderer dRenderer) {

        //TODO simulation must be initialized
        super("Ufo Simulation");
        getContentPane().add(mainPanel);
        Canvas canvas = dRenderer.getCanvas();
        graphicPanel.add(canvas);
        JCompass compass = new JCompass();
        compassPanel.add(compass);
        simulationButton.setText("Start");
        status.setText("Resting");
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

    /**
     * Initializes the simulation
     *
     * @param simulation, the current simulation
     */
    @Override
    public void init(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Updates the data displayed in the GUI.
     *
     * @param simulationUpdateEvent
     */
    @Override
    public void updateDroneStatus(SimulationUpdateEvent simulationUpdateEvent) {
        Location location = simulationUpdateEvent.getDrone().getLocation();
        if (simulation.getDrone().isCrashed()) {
            status.setText("Crashed!");
            stopped = true;
            simulationButton.setText("Stop");
            simulationButton.setBackground(Color.RED);
        }
        xCord.setText(String.valueOf(Math.round(location.getX() * 100.0) / 100.0));
        yCord.setText(String.valueOf(Math.round(location.getY() * 100.0) / 100.0));
        zCord.setText(String.valueOf(Math.round(location.getZ() * 100.0) / 100.0));
        airSpeed.setText(String.valueOf(Math.round(location.getAirspeed() * 100.0) / 100.0));
        groundSpeed.setText(String.valueOf(Math.round(location.getGroundSpeed() * 100.0) / 100.0));
        direction.setText(String.valueOf(Math.round(location.getHeading() * 100.0) / 100.0));
        double t = simulationUpdateEvent.getTime() / 1000;
        StringBuilder time = calculateTime(t);
        this.time.setText(time.toString());
        //TODO windData should be updated as well, when there is data to display
        SensorModule sensorModule = simulation.getChild(SensorModule.class);
        List<SensorResultDto> sensorResultDtos = sensorModule.getResultsFromAllSensors();
        setObstacleAndWind(sensorResultDtos);
    }

    /**
     * This method divides the sensor into their different types, reads and
     * displays the data, that is being given by them. Therefore, the obstacles,
     * that are recognized by the sensor have to be stored, so that they can be deleted
     * when they are no longer in range of the sensor.
     *
     * @param sensorResultDtos, all the data from the sensors
     */
    private void setObstacleAndWind(List<SensorResultDto> sensorResultDtos) {
        for (SensorResultDto sensor : sensorResultDtos) {
            if (sensor != null) {

                //Sets the data for the windSensor
                if (sensor.getSensor().getType().equals("WindSensor")) {
                    if (sensor.getValues() != null) {
                        if (!sensor.getValues().get(1).isNaN()) {
                            windData.setText(sensor.getValues().get(1) + "km/h");
                        } else {
                            windData.setText("No Wind");
                        }
                        compass.setNeedleDirection(sensor.getValues().get(0));
                    }
                }

                //Sets the data for the distanceSensor
                float distance;
                if (sensor.getSensor().getType().equals("DistanceSensor")) {
                    if (sensor.getObstacle() != null && !sensor.getObstacle().isEmpty()) {
                        List<Obstacle> obstacles = sensor.getObstacle();
                        List<Integer> ids = new ArrayList<>();
                        for (Obstacle obstacle : obstacles) {
                            if (panelsMap.get(obstacle.getID().intValue()) != null) {
                                distance = sensor.getValues().get(0);
                                updateJLabel(panelsMap.get(obstacle.getID().intValue()), obstacle.getPosition(), distance);
                            } else {
                                if (sensor.getValues() != null && !sensor.getValues().isEmpty()) {
                                    distance = sensor.getValues().get(0);
                                    JPanel addedObstacle = addObstacle(obstacle.getID().intValue(), obstacle.getPosition(), distance);
                                    panelsMap.put(obstacle.getID().intValue(), addedObstacle);
                                }
                            }
                            ids.add(obstacle.getID().intValue());
                        }

                        //iterates through a few lists, to sort out the JPanels, which are no longer displayed in
                        //the data, given by the sensors
                        List<Integer> idsToBeRemoved = new ArrayList<>();
                        for (int id : ids) {
                            if (panelsMap.containsKey(id)) {
                                idsToBeRemoved.add(id);
                            }
                        }
                        for (int id : idsToBeRemoved) {
                            ids.remove((Integer) id);
                        }
                        for (int id : ids) {
                            removeObstacle(id);
                        }
                    }
                }
            }
        }
    }


    /**
     * This method updates the already existing JPanels for the current Obstacles.
     *
     * @param jPanel,   the panel in which the data of one obstacle is displayed
     * @param position, the position of the obstacle
     * @param distance, the distance between the obstacle and the drone
     */
    private void updateJLabel(JPanel jPanel, Float[] position, float distance) {

        //Every obstacle-panel is structured the same (header, X, Y, Z, Distance), therefore the magic numbers
        Component[] components = jPanel.getComponents();
        JLabel xPosition = (JLabel) components[1];
        xPosition.setText("X: " + position[0]);
        JLabel yPosition = (JLabel) components[2];
        yPosition.setText("Y: " + position[1]);
        JLabel zPosition = (JLabel) components[3];
        zPosition.setText("Z: " + position[2]);
        JLabel distanceLabel = (JLabel) components[4];
        distanceLabel.setText("Distance: " + distance);
    }

    /**
     * Calculates the time out of milliseconds and
     * displays it in the format of hh:mm:ss.
     *
     * @param t, the time given in milliseconds
     * @return StringBuilder, which stores the format of the current time in hh:mm:ss.
     */
    private StringBuilder calculateTime(double t) {

        int seconds = (int) t % 1000 - 60 * (minutes + 60 * hours);

        if (seconds >= 60) {
            seconds = 0;
            minutes++;

            if (minutes >= 60) {
                minutes = 0;
                hours++;
            }
        }
        StringBuilder time = new StringBuilder();
        if (hours < 10) {
            time.append(String.format("0%d", hours));
        } else {
            time.append(String.format("%d", hours));
        }

        if (minutes < 10) {
            time.append(String.format(":0%d", minutes));
        } else {
            time.append(String.format(":%d", minutes));
        }

        if (seconds < 10) {
            time.append(String.format(":0%d", seconds));
        } else {
            time.append(String.format(":%d", seconds));
        }

        return time;
    }

    /**
     * Collapses the obstacle-sidebar
     */
    public void disableObstacleSidebar() {
        toggleObstacles = false;
        obstacles.setVisible(false);
    }

    /**
     * Opens the obstacle-sidebar
     */
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

    /**
     * Collapses the wind-sidebar
     */
    private void disableWindSidebar() {
        toggleWind = false;
        wind.setVisible(false);
    }

    /**
     * Opens the obstacle-sidebar
     */
    private void enableWindSidebar() {
        toggleWind = true;
        wind.setVisible(true);
        compass.setNeedleDirection(0);
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
            started = true;
            simulationButton.setText("Pause");
            status.setText("Flying");
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
     * @return JPanel, which is used to work with the given data
     */
    public JPanel addObstacle(int id, Float[] position, float distance) {
        JPanel obstacle = new JPanel(new GridBagLayout());
        JLabel header = new JLabel("Obstacle " + id + ":");
        JLabel xPosition = new JLabel();
        JLabel yPosition = new JLabel();
        JLabel zPosition = new JLabel();
        JLabel distanceLabel = new JLabel();
        int yInside = 0;

        //A type of constraint is defined to keep things simple, however, the JLabels should
        //be placed beneath one another, which is realized by the gridy-paramater, always incrementing
        //before the next JLabel is placed, which refers to the y-axis value
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = yInside;
        constraints.insets = new Insets(5, 5, 5, 5);
        obstacle.add(header, constraints);

        constraints.gridy = ++yInside;
        xPosition.setHorizontalAlignment(JLabel.CENTER);
        xPosition.setVerticalAlignment(JLabel.CENTER);
        xPosition.setText("X: " + position[0]);
        obstacle.add(xPosition, constraints);

        constraints.gridy = ++yInside;
        yPosition.setHorizontalAlignment(JLabel.CENTER);
        yPosition.setVerticalAlignment(JLabel.CENTER);
        yPosition.setText("Y: " + position[1]);
        obstacle.add(yPosition, constraints);

        constraints.gridy = ++yInside;
        zPosition.setHorizontalAlignment(JLabel.CENTER);
        zPosition.setVerticalAlignment(JLabel.CENTER);
        zPosition.setText("Z: " + position[2]);
        obstacle.add(zPosition, constraints);

        constraints.gridy = ++yInside;
        distanceLabel.setHorizontalAlignment(JLabel.CENTER);
        distanceLabel.setVerticalAlignment(JLabel.CENTER);
        distanceLabel.setText("Distance: " + distance + "m");
        obstacle.add(distanceLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = y;

        obstacle.setBorder(BorderFactory.createRaisedBevelBorder());
        obstaclePanel.add(obstacle, constraints);
        y++;
        pack();
        return obstacle;
    }

    /**
     * Removes a data-panel of an obstacle from the sidebar
     *
     * @param id the id of the obstacle-panel, that should be removed
     */
    public void removeObstacle(int id) {
        JPanel obstacle = panelsMap.get(id);
        obstaclePanel.remove(obstacle);
        y--;
        panelsMap.remove(id);
        pack();
    }
}
