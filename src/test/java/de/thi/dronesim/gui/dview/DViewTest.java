package de.thi.dronesim.gui.dview;

import com.formdev.flatlaf.FlatDarkLaf;
import com.jme3.math.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test for DView
 *
 * @author Michael Weichenrieder
 */
public class DViewTest extends JFrame {

    private static final int width = 1280, height = 720;

    private JButton thirdPersonButton;
    private JButton birdViewButton;
    private JButton firstPersonButton;
    private JPanel dViewPanel;
    private JPanel rootPanel;

    /**
     * Creates a new swing frame
     *
     * @param dView Scene to display
     * @param width Display width
     * @param height Display height
     */
    public DViewTest(final DView dView, RenderableDrone drone, int width, int height) {
        super("DView-Test");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(rootPanel);
        dViewPanel.setMinimumSize(new Dimension(width, height));
        dViewPanel.add(dView.getCanvas());
        thirdPersonButton.addActionListener(e -> dView.setPerspective(DView.Perspective.THIRD_PERSON, drone));
        birdViewButton.addActionListener(e -> dView.setPerspective(DView.Perspective.BIRD_VIEW, drone));
        firstPersonButton.addActionListener(e -> dView.setPerspective(DView.Perspective.FIRST_PERSON, drone));
        pack();
        setVisible(true);
    }

    /**
     * Init point
     *
     * @param args Irrelevant parameters
     */
    public static void main(String[] args) {
        // Dark gui theme for swing
        FlatDarkLaf.install();

        // Create DView and add objects
        DView dView = new DView(width, height);
        List<RenderableObject> mapObjects = getMapObjects();
        dView.addRenderableObjects(mapObjects);

        // Add drone
        RenderableDrone drone = new RenderableDrone(new Vector3f(0, 1, 0));
        drone.setTilt(new Vector3f((float) Math.PI * .05f, 0, (float) Math.PI * .05f));
        drone.setRotation(new Vector3f(0, (float) Math.PI * 1.5f, 0));
        drone.moveRelativeToWorld(new Vector3f(0, .2f, 0));
        drone.setRotateRotors(true);
        Runnable droneUpdater = () -> {
            drone.addRotation(new Vector3f(0, -.0005f, 0));
            drone.moveRelativeToDrone(new Vector3f(0, 0, .0006f));
        };
        dView.addFrameUpdateListener(droneUpdater);
        dView.addRenderableObject(drone);

        // Open swing frame
        new Thread(() -> new DViewTest(dView, drone, width, height)).start();
    }

    /**
     * Creates example map
     *
     * @return List of renderable objects
     */
    public static List<RenderableObject> getMapObjects() {
        List<RenderableObject> objects = new ArrayList<>();
        objects.add(new RenderableCuboid(new Vector3f(0, .5f, 0)));
        objects.add(new RenderableCuboid(new Vector3f(1, .5f, 0)));
        objects.add(new RenderableCuboid(new Vector3f(0, .5f, -1)));
        objects.add(new RenderableCuboid(new Vector3f(0, 1.5f, -1)));
        objects.add(new RenderableCuboid(new Vector3f(1, .5f, 0)));
        objects.add(new RenderableSphere(new Vector3f(0, .5f, 2)));
        objects.add(new RenderableSphere(new Vector3f(0, 1.5f, 2)));
        objects.add(new RenderableSphere(new Vector3f(-1, .5f, -2)));
        objects.add(new RenderableMarker(new Vector3f(0, 1, 0)));
        return objects;
    }
}
