package de.thi.dronesim.gui.mview;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.gui.GuiManager;
import de.thi.dronesim.gui.IGuiView;
import de.thi.dronesim.obstacle.entity.Obstacle;
import de.thi.dronesim.sensor.SensorModule;
import de.thi.dronesim.sensor.dto.SensorResultDto;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Alternative GUI for text-only display.
 *
 * @author Daniel Dunger
 */

@SuppressWarnings("FieldCanBeLocal")
public class MView extends JFrame implements IGuiView {

    // These values are currently set to fit the current information
    private static final int WINDOW_MIN_WIDTH = 550;
    private static final int WINDOW_MIN_HEIGHT = 500;

    // Current Manager
    private final GuiManager guiManager;
    private final Simulation sim;

    // Main Panel
    private final JPanel contentPane;

    // Top Panel Info
    private final JPanel panelTop;
    private final JLabel status;
    private final JLabel statusValue;
    private final JLabel runtime;
    private final JLabel runtimeValue;
    private int seconds, minutes, hours;

    // Coordinates/Position
    private final JPanel panelCoordsOuter;
    private final JLabel coords;

    private final JPanel panelCoordsInner;
    private final JLabel coordinateX;
    private final JLabel coordinateXValue;
    private final JLabel coordinateY;
    private final JLabel coordinateYValue;
    private final JLabel coordinateZ;
    private final JLabel coordinateZValue;

    // Velocity Panel
    private final JPanel panelVelocityOuter;
    private final JLabel velocities;

    private JPanel panelVelocityInner;
    private final JLabel airSpeed;
    private final JLabel airSpeedValue;
    private final JLabel verticalVelocity;
    private final JLabel verticalVelocityValue;
    private final JLabel groundSpeed;
    private final JLabel groundSpeedValue;

    // Direction/Tilts
    private final JPanel panelDirectionOuter;
    private final JLabel directionTilts;

    private final JPanel panelDirectionInner;
    private final JLabel heading;
    private final JLabel headingValue;
    private final JLabel pitch;
    private final JLabel pitchValue;
    private final JLabel roll;
    private final JLabel rollValue;
    private final JLabel yaw;
    private final JLabel yawValue;

    // Obstacles
    private final JPanel panelObstaclesWindInner;
    private final JLabel obstacleWind;

    private final JPanel panelObstaclesWindOuter;
    private final JLabel obstacle;
    private final JLabel obstacleValue;
    private final JLabel wind;
    private final JLabel windValue;

    // Buttons
    private final JButton startButton;
    private final JButton loadButton;

    public MView(GuiManager guiManager) {
        super("Drone Simulation (No GFX)");

        // Holding a ref to the manager, so we can access the simulation, so our buttons can start/pause it.
        this.guiManager = guiManager;
        this.sim = guiManager.getSimulation();

        // Init
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(600, 800, WINDOW_MIN_WIDTH, WINDOW_MIN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        panelTop = new JPanel();
        panelCoordsOuter = new JPanel();
        panelCoordsInner = new JPanel();
        panelVelocityOuter = new JPanel();
        panelVelocityInner = new JPanel();
        panelDirectionOuter = new JPanel();
        panelDirectionInner = new JPanel();
        panelObstaclesWindOuter = new JPanel();
        panelObstaclesWindInner = new JPanel();

        startButton = new JButton("Start | Pause | Stop Simulation");
        loadButton = new JButton("Load Scenario");

        // Button Actions
        startButton.addActionListener(e -> runSimulation());

        loadButton.addActionListener(e -> {
            // TODO: Once this is actually possible (If, else the whole button can be removed).
            // Simulation.loadScenario();
        });

        // Group Layout setup of the main panel
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        // Horizontal Layout:
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(panelTop, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(panelCoordsOuter, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                                        .addComponent(panelDirectionOuter, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                                        .addComponent(startButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(loadButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(panelObstaclesWindOuter, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                                        .addComponent(panelVelocityOuter, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))))
                                .addContainerGap(18, Short.MAX_VALUE))
        );
        // Vertical Layout:
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGap(10)
                                .addComponent(panelTop, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addGap(10)
                                .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(panelVelocityOuter, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panelCoordsOuter, GroupLayout.DEFAULT_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                .addGap(10)
                                .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(panelObstaclesWindOuter, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panelDirectionOuter, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(startButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(loadButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        // Panels and Labels:

        // Obstacles
        GridBagLayout gbl_panelObstaclesWind = new GridBagLayout();
        gbl_panelObstaclesWind.columnWidths = new int[]{250};
        gbl_panelObstaclesWind.rowHeights = new int[]{50, 200};
        gbl_panelObstaclesWind.columnWeights = new double[]{1.0};
        gbl_panelObstaclesWind.rowWeights = new double[]{0.0, 1.0};
        panelObstaclesWindOuter.setLayout(gbl_panelObstaclesWind);

        obstacleWind = new JLabel("Obstacles");
        GridBagConstraints gbc_obstacles = new GridBagConstraints();
        gbc_obstacles.insets = new Insets(0, 0, 5, 0);
        gbc_obstacles.gridx = 0;
        gbc_obstacles.gridy = 0;
        panelObstaclesWindOuter.add(obstacleWind, gbc_obstacles);

        GridBagConstraints gbc_panelObstaclesInner = new GridBagConstraints();
        gbc_panelObstaclesInner.insets = new Insets(0, 10, 10, 10);
        gbc_panelObstaclesInner.fill = GridBagConstraints.BOTH;
        gbc_panelObstaclesInner.gridx = 0;
        gbc_panelObstaclesInner.gridy = 1;
        panelObstaclesWindOuter.add(panelObstaclesWindInner, gbc_panelObstaclesInner);
        panelObstaclesWindInner.setLayout(new GridLayout(0, 2, 0, 0));

        obstacle = new JLabel("Next Obstacle");
        obstacle.setHorizontalAlignment(SwingConstants.CENTER);
        panelObstaclesWindInner.add(obstacle);

        obstacleValue = new JLabel("[50 m] [NE]");
        obstacleValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelObstaclesWindInner.add(obstacleValue);

        wind = new JLabel("Current Wind");
        wind.setHorizontalAlignment(SwingConstants.CENTER);
        panelObstaclesWindInner.add(wind);

        windValue = new JLabel("[3 m/s] [SW]");
        windValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelObstaclesWindInner.add(windValue);

        // Direction and Tilts
        GridBagLayout gbl_panelDirection = new GridBagLayout();
        gbl_panelDirection.columnWidths = new int[]{250};
        gbl_panelDirection.rowHeights = new int[]{50, 200};
        gbl_panelDirection.columnWeights = new double[]{1.0};
        gbl_panelDirection.rowWeights = new double[]{0.0, 1.0};
        panelDirectionOuter.setLayout(gbl_panelDirection);

        directionTilts = new JLabel("Direction and Tilts");
        GridBagConstraints gbc_directionTilts = new GridBagConstraints();
        gbc_directionTilts.insets = new Insets(0, 0, 5, 0);
        gbc_directionTilts.gridx = 0;
        gbc_directionTilts.gridy = 0;
        panelDirectionOuter.add(directionTilts, gbc_directionTilts);

        GridBagConstraints gbc_DirectionPanelInner = new GridBagConstraints();
        gbc_DirectionPanelInner.insets = new Insets(0, 10, 10, 10);
        gbc_DirectionPanelInner.fill = GridBagConstraints.BOTH;
        gbc_DirectionPanelInner.gridx = 0;
        gbc_DirectionPanelInner.gridy = 1;
        panelDirectionOuter.add(panelDirectionInner, gbc_DirectionPanelInner);
        panelDirectionInner.setLayout(new GridLayout(4, 2, 0, 0));

        heading = new JLabel("Heading");
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        panelDirectionInner.add(heading);

        headingValue = new JLabel("North,East,South,West");
        headingValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelDirectionInner.add(headingValue);

        pitch = new JLabel("Pitch");
        pitch.setHorizontalAlignment(SwingConstants.CENTER);
        panelDirectionInner.add(pitch);

        pitchValue = new JLabel("0");
        pitchValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelDirectionInner.add(pitchValue);

        roll = new JLabel("Roll");
        roll.setHorizontalAlignment(SwingConstants.CENTER);
        panelDirectionInner.add(roll);

        rollValue = new JLabel("0");
        rollValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelDirectionInner.add(rollValue);

        yaw = new JLabel("Yaw");
        yaw.setHorizontalAlignment(SwingConstants.CENTER);
        panelDirectionInner.add(yaw);

        yawValue = new JLabel("0");
        yawValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelDirectionInner.add(yawValue);

        // Top Panel Information
        panelTop.setLayout(new GridLayout(1, 0, 0, 0));
        status = new JLabel("Current Status:");
        status.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(status);

        statusValue = new JLabel("Not Running");
        statusValue.setHorizontalAlignment(SwingConstants.CENTER);
        statusValue.setBackground(Color.RED);
        panelTop.add(statusValue);

        runtime = new JLabel("Current Runtime:");
        runtime.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(runtime);

        runtimeValue = new JLabel("00:00:00");
        runtimeValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(runtimeValue);

        // Velocities
        GridBagLayout gbl_panelVelocityOuter = new GridBagLayout();
        gbl_panelVelocityOuter.rowHeights = new int[]{50, 200};
        gbl_panelVelocityOuter.columnWidths = new int[]{250};
        gbl_panelVelocityOuter.columnWeights = new double[]{1.0};
        gbl_panelVelocityOuter.rowWeights = new double[]{0.0, 1.0};
        panelVelocityOuter.setLayout(gbl_panelVelocityOuter);

        velocities = new JLabel("Velocities");
        GridBagConstraints gbc_lb_currentSpeeds = new GridBagConstraints();
        gbc_lb_currentSpeeds.gridx = 0;
        gbc_lb_currentSpeeds.gridy = 0;
        panelVelocityOuter.add(velocities, gbc_lb_currentSpeeds);

        panelVelocityInner = new JPanel();
        GridBagConstraints gbc_panelVelocityInner = new GridBagConstraints();
        gbc_panelVelocityInner.insets = new Insets(0, 10, 10, 10);
        gbc_panelVelocityInner.fill = GridBagConstraints.BOTH;
        gbc_panelVelocityInner.gridx = 0;
        gbc_panelVelocityInner.gridy = 1;
        panelVelocityOuter.add(panelVelocityInner, gbc_panelVelocityInner);
        panelVelocityInner.setLayout(new GridLayout(3, 2, 0, 0));

        airSpeed = new JLabel("Airspeed");
        airSpeed.setHorizontalAlignment(SwingConstants.CENTER);
        panelVelocityInner.add(airSpeed);

        airSpeedValue = new JLabel(String.valueOf(Integer.MIN_VALUE));
        airSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelVelocityInner.add(airSpeedValue);

        verticalVelocity = new JLabel("Vertical Velocity");
        verticalVelocity.setHorizontalAlignment(SwingConstants.CENTER);
        panelVelocityInner.add(verticalVelocity);

        verticalVelocityValue = new JLabel(String.valueOf(Integer.MIN_VALUE));
        verticalVelocityValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelVelocityInner.add(verticalVelocityValue);

        groundSpeed = new JLabel("Ground Speed");
        groundSpeed.setHorizontalAlignment(SwingConstants.CENTER);
        panelVelocityInner.add(groundSpeed);

        groundSpeedValue = new JLabel(String.valueOf(Integer.MIN_VALUE));
        groundSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelVelocityInner.add(groundSpeedValue);

        // Position / Coordinates
        GridBagLayout gbl_panelCoordsOuter = new GridBagLayout();
        gbl_panelCoordsOuter.columnWidths = new int[]{250};
        gbl_panelCoordsOuter.rowHeights = new int[]{50, 100};
        gbl_panelCoordsOuter.columnWeights = new double[]{0.0};
        gbl_panelCoordsOuter.rowWeights = new double[]{0.0, 0.0};
        panelCoordsOuter.setLayout(gbl_panelCoordsOuter);

        coords = new JLabel("Position");
        coords.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_position = new GridBagConstraints();
        gbc_position.fill = GridBagConstraints.BOTH;
        gbc_position.gridx = 0;
        gbc_position.gridy = 0;
        panelCoordsOuter.add(coords, gbc_position);

        GridBagConstraints gbc_panelCoordsInner = new GridBagConstraints();
        gbc_panelCoordsInner.insets = new Insets(0, 5, 10, 5);
        gbc_panelCoordsInner.fill = GridBagConstraints.BOTH;
        gbc_panelCoordsInner.gridx = 0;
        gbc_panelCoordsInner.gridy = 1;
        panelCoordsOuter.add(panelCoordsInner, gbc_panelCoordsInner);
        panelCoordsInner.setLayout(new GridLayout(3, 2, 0, 0));

        coordinateX = new JLabel("X:");
        coordinateX.setHorizontalAlignment(SwingConstants.CENTER);
        panelCoordsInner.add(coordinateX);

        coordinateXValue = new JLabel(String.valueOf(Integer.MIN_VALUE));
        coordinateXValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelCoordsInner.add(coordinateXValue);

        coordinateY = new JLabel("Y:");
        coordinateY.setHorizontalAlignment(SwingConstants.CENTER);
        panelCoordsInner.add(coordinateY);

        coordinateYValue = new JLabel(String.valueOf(Integer.MIN_VALUE));
        coordinateYValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelCoordsInner.add(coordinateYValue);

        coordinateZ = new JLabel("Z:");
        coordinateZ.setHorizontalAlignment(SwingConstants.CENTER);
        panelCoordsInner.add(coordinateZ);

        coordinateZValue = new JLabel(String.valueOf(Integer.MIN_VALUE));
        coordinateZValue.setHorizontalAlignment(SwingConstants.CENTER);
        panelCoordsInner.add(coordinateZValue);
        contentPane.setLayout(gl_contentPane);

        // Just so everything is a bit more visible, black borders:
        contentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panelTop.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelCoordsOuter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelCoordsInner.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelVelocityOuter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelVelocityInner.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelDirectionOuter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelDirectionInner.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelObstaclesWindOuter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelObstaclesWindInner.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        setVisible(true);
    }

    @Override
    public void updateDroneStatus(SimulationUpdateEvent simulationUpdateEvent) {
        Drone d = simulationUpdateEvent.getDrone();
        Location location = d.getLocation();

        // Update runtime
        double t = simulationUpdateEvent.getTime() / 1000;

        seconds = (int) t % 1000 - 60 * (minutes + 60 * hours);

        if (seconds >= 60) {
            seconds = 0;
            minutes++;

            if (minutes >= 60) {
                minutes = 0;
                hours++;
            }
        }

        // Only update if we have a change, small save I guess
        // Just for a better visual representation, if times are single digit, we add a 0 in front
        StringBuilder time = new StringBuilder();
        if (hours < 10)
            time.append(String.format("0%d", hours));
        else
            time.append(String.format("%d", hours));

        if (minutes < 10)
            time.append(String.format(":0%d", minutes));
        else
            time.append(String.format(":%d", minutes));

        if (seconds < 10)
            time.append(String.format(":0%d", seconds));
        else
            time.append(String.format(":%d", seconds));

        runtimeValue.setText(time.toString());


        // Update Position
        coordinateXValue.setText(String.format("%.3f", location.getX()));
        coordinateYValue.setText(String.format("%.3f", location.getY()));
        coordinateZValue.setText(String.format("%.3f", location.getZ()));

        // Update Heading and Tilts
        double hdg = location.getHeading() % 360.00;
        String dir = "???";
        if (hdg < 22.5 && hdg >= 0.0)
            dir = "E";
        else if (hdg < 72.5 && hdg >= 22.5)
            dir = "NE";
        else if (hdg < 112.5 && hdg >= 72.5)
            dir = "N";
        else if (hdg < 157.5 && hdg >= 112.5)
            dir = "NW";
        else if (hdg < 202.5 && hdg >= 157.5)
            dir = "W";
        else if (hdg < 247.5 && hdg >= 202.5)
            dir = "SW";
        else if (hdg < 292.5 && hdg >= 247.5)
            dir = "S";
        else if (hdg < 337.5 && hdg >= 292.5)
            dir = "SE";
        else if (hdg > 292.5 && hdg <= 360.1)
            dir = "E";

        headingValue.setText(dir.concat(String.format(" @ %.3f", hdg) + " Degree"));
        pitchValue.setText(String.format("%.3f", location.getPitch()));

        // These values do not exist currently, but can be added
        //rollValue.setText(String.format("%.3f", location.getRoll()));
        //yawValue.setText(String.format("%.3f", location.getYaw()));

        // Velocities
        airSpeedValue.setText(String.format("%.3f", location.getAirspeed()));
        verticalVelocityValue.setText(String.format("%.3f", location.getVerticalSpeed()));
        groundSpeedValue.setText(String.format("%.3f", location.getGroundSpeed()));

        // Obstacles and Wind
        if (sim != null) {
            SensorModule sensorModule = sim.getChild(SensorModule.class);

            // If we have a sensormodule, read it
            if (sensorModule != null) {
                List<SensorResultDto> sensorList = sensorModule.getResultsFromAllSensors();
                Obstacle obstacle = null;
                for (SensorResultDto res : sensorList) {
                    // Handle Wind
                    if (res.getSensor() != null) {
                        float wind = res.getValues().get(0);

                        // It is possible for there to be no wind, which results in NaN
                        if (Float.isNaN(wind)) {
                            windValue.setText("Windless");
                        } else {
                            windValue.setText(String.format("%3.2f", wind));
                        }

                        continue;
                    } // if (res.getSensor)

                    // Handle Obstacles ... TODO: Needs the actual module running...
                    List<Obstacle> list = res.getObstacle();
                    if (list.isEmpty())
                        continue;

                    obstacleValue.setText(String.valueOf(res.getValues().get(0)) + " m");
                } // for

            } // (sensorModule != null)
        }

        if (!sim.isRunning() && !wasStopped && wasStarted) {
            startButton.setText("Exit Simulation");
            statusValue.setText("Simulation Ended");
            wasStopped = true;
        }
    }

    // TODO: Remove once simulation was updated to return its state (?). Just to prevent an exception.
    boolean wasStarted = false;
    boolean wasStopped = false;

    private void runSimulation() {
        // Simulation is running and was not stopped yet, so stop it:
        if (sim.isRunning() && !wasStopped) {
            wasStopped = true;
            sim.stop();

            startButton.setText("Exit Simulation");
            statusValue.setText("Simulation Ended");
            startButton.removeAll();
        // Simulation was running, but was stopped and now the button function as "exit":
        } else if (wasStopped && wasStarted) {
            System.exit(0);
        // Simulation has yet to start, so do that:
        } else if (!wasStarted){
            wasStarted = true;
            sim.start();

            startButton.setText("Stop Simulation");
            statusValue.setText("Running...");
        }
    }

}
