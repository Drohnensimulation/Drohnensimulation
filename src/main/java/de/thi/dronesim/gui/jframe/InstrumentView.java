package de.thi.dronesim.gui.jframe;

import de.thi.dronesim.gui.dview.DView;
import de.thi.dronesim.ufo.Location;

import javax.swing.*;
import java.awt.*;

public class InstrumentView extends JFrame implements InstrumentInterface {

    private JPanel mainPanel;
    private JLabel xCord;
    private JLabel yCord;
    private JLabel zCord;
    private JLabel direction;
    private JPanel coordinatePanel;
    private JPanel graphicPanel;
    private JPanel dataPanel;
    private JPanel directionPanel;
    private JPanel airSpeedPanel;
    private JPanel zCordPanel;
    private JPanel yCordPanel;
    private JPanel xCordPanel;
    private JButton startSimulationButton;
    private JPanel startPanel;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JPanel scenarioPanel;
    private JButton birdViewButton;
    private JButton thirdPersonButton;
    private JLabel airSpeed;
    private JLabel groundSpeed;
    private JPanel groundSpeedPanel;
    private JButton firstPersonButton;

    public InstrumentView(DView dView) {
        super("Ufo Simulation");
        getContentPane().add(mainPanel);
        Canvas canvas = dView.getCanvas();
        graphicPanel.add(canvas);
        thirdPersonButton.addActionListener(e -> dView.setPerspective(DView.Perspective.THIRD_PERSON));
        birdViewButton.addActionListener(e -> dView.setPerspective(DView.Perspective.BIRD_VIEW));
        firstPersonButton.addActionListener(e -> dView.setPerspective(DView.Perspective.FIRST_PERSON));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        coordinatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        graphicPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        dataPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        groundSpeedPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        airSpeedPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        directionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        zCordPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        yCordPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        xCordPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        startPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scenarioPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        startSimulationButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    @Override
    public void updateDroneStatus(Location location) {
        xCord.setText(String.valueOf(location.getX()));
        yCord.setText(String.valueOf(location.getY()));
        zCord.setText(String.valueOf(location.getZ()));
        airSpeed.setText(String.valueOf(location.getAirspeed()));
        groundSpeed.setText(String.valueOf(location.getGroundSpeed()));
        direction.setText(String.valueOf(location.getHeading()));
    }
}
