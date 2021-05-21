package de.thi.dronesim.gui.dview.component;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

/**
 * Test for custom {@link JCompass} component
 *
 * @author Michael Weichenrieder
 */
public class JCompassTest extends JFrame {

    public JCompassTest(JCompass compass) {
        super("JCompass Test");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(compass);
        setSize(300, 300);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatDarkLaf.install();
        JCompass compass = new JCompass();
        new JCompassTest(compass);
        for(double angle = 0; angle <= 360; angle = (angle + 1) % 360) {
            compass.setNeedleDirection(angle);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
