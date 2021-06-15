package de.thi.dronesim.gui.drenderer;

import com.formdev.flatlaf.FlatDarkLaf;
import com.jme3.math.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test for {@link DRenderer}
 *
 * @author Michael Weichenrieder
 */
public class DRendererTest extends JFrame {

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
    public DRendererTest(final DRenderer dView, RenderableDrone drone, int width, int height) {
        super("DRenderer-Test");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(rootPanel);
        dViewPanel.setMinimumSize(new Dimension(width, height));
        dViewPanel.add(dView.getCanvas());
        thirdPersonButton.addActionListener(e -> dView.setPerspective(DRenderer.Perspective.THIRD_PERSON, drone));
        birdViewButton.addActionListener(e -> dView.setPerspective(DRenderer.Perspective.BIRD_VIEW, drone));
        firstPersonButton.addActionListener(e -> dView.setPerspective(DRenderer.Perspective.FIRST_PERSON, drone));
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

        // Create DRenderer and add objects
        DRenderer dRenderer = new DRenderer();
        List<RenderableObject> mapObjects = getMapObjects();
        dRenderer.addRenderableObjects(mapObjects);

        // Add drone
        RenderableDrone drone = new RenderableDrone(new Vector3f(0, 1, 0), 1);
        drone.setTilt(new Vector3f((float) Math.PI * .05f, 0, (float) Math.PI * .05f));
        drone.setRotation(new Vector3f(0, (float) Math.PI * 1.5f, 0));
        drone.moveRelativeToWorld(new Vector3f(0, .2f, 0));
        drone.setRotateRotors(true);
        Runnable droneUpdater = () -> {
            drone.addRotation(new Vector3f(0, -.01f, 0));
            drone.moveRelativeToDrone(new Vector3f(0, 0, .012f));
        };
        dRenderer.addFrameUpdateListener(droneUpdater);
        dRenderer.addRenderableObject(drone);

        // Open swing frame
        new Thread(() -> new DRendererTest(dRenderer, drone, 1280, 720)).start();
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
