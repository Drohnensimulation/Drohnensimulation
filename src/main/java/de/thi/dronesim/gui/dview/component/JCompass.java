package de.thi.dronesim.gui.dview.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Swing component for displaying a compass
 *
 * @author Michael Weichenrieder
 */
public class JCompass extends JComponent {

    private double needleDegrees = 0;

    /**
     * Update needle direction
     *
     * @param degrees Needle direction in degrees clockwise. 0 degrees is north
     */
    public void setNeedleDirection(double degrees) {
        needleDegrees = degrees;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        // Get graphics
        Graphics2D graphics = (Graphics2D) g;

        // Enable antialiasing
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHints(renderingHints);

        // Paint background circle
        int marginMin = 5;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - marginMin;
        int textSize = radius / 5;
        int markerSize = radius / 20;
        graphics.setPaint(getForeground().darker().darker());
        graphics.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Add markers
        graphics.setPaint(getBackground());
        for (double angle = 0; angle < 360; angle += 22.5) {
            if (angle % 45 == 0) {
                continue;
            }
            double[] pos = getPos(centerX, centerY, radius - textSize / 2.0, angle);
            graphics.fillOval((int) pos[0] - markerSize / 2, (int) pos[1] - markerSize / 2, markerSize, markerSize);
        }

        // Paint direction chars
        graphics.setPaint(getBackground());
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        for (int i = 0; i < directions.length; i++) {
            double angle = i * (360.0 / directions.length);
            double[] pos = getPos(centerX, centerY, radius - textSize / 2.0, angle);
            int size = angle % 90 != 0 ? (int) (textSize * .6) : textSize;
            drawCenteredString(graphics, directions[i], (int) pos[0], (int) pos[1], size);
        }

        // Calculate needle coordinates
        double[] east = getPos(centerX, centerY, textSize / 2.0, (needleDegrees + 90) % 360);
        double[] west = getPos(centerX, centerY, textSize / 2.0, (needleDegrees + 270) % 360);
        double[] south = getPos(centerX, centerY, radius - textSize * 1.5, (needleDegrees + 180) % 360);
        double[] north = getPos(centerX, centerY, radius - textSize * 1.5, needleDegrees % 360);

        // Paint south part of needle
        graphics.setStroke(new BasicStroke(1));
        graphics.setPaint(getBackground());
        Path2D.Double southNeedle = new Path2D.Double();
        southNeedle.moveTo(south[0], south[1]);
        southNeedle.lineTo(east[0], east[1]);
        southNeedle.lineTo(west[0], west[1]);
        southNeedle.closePath();
        graphics.fill(southNeedle);

        // Paint north part of needle
        graphics.setPaint(new Color(168, 0, 0));
        Path2D.Double northNeedle = new Path2D.Double();
        northNeedle.moveTo(north[0], north[1]);
        northNeedle.lineTo(east[0], east[1]);
        northNeedle.lineTo(west[0], west[1]);
        northNeedle.closePath();
        graphics.fill(northNeedle);
    }

    /**
     * Helper to get coordinates by angle, center and radius
     *
     * @param centerX X of center
     * @param centerY Y of center
     * @param radius  Radius
     * @param angle   Rotation angle in degrees
     * @return Position array: {x, y}
     */
    private double[] getPos(int centerX, int centerY, double radius, double angle) {
        // Parse angle and get target quadrant
        angle %= 360;
        int quadrant = (int) (angle / 90);
        angle %= 90;
        angle = Math.toRadians(angle);

        // Calculate ak/gk
        double ak = Math.round(radius * Math.cos(angle));
        double gk = Math.round(radius * Math.sin(angle));

        // Calculate position by quadrant and return it
        double[] pos = {centerX, centerY};
        switch (quadrant) {
            case 0:
                pos[0] += gk;
                pos[1] -= ak;
                break;
            case 1:
                pos[0] += ak;
                pos[1] += gk;
                break;
            case 2:
                pos[0] -= gk;
                pos[1] += ak;
                break;
            case 3:
                pos[0] -= ak;
                pos[1] -= gk;
        }
        return pos;
    }

    /**
     * Helper to draw a String centered to a position
     *
     * @param graphics Graphics to draw to
     * @param str      String to draw
     * @param x        X of center
     * @param y        Y of center
     */
    private void drawCenteredString(Graphics2D graphics, String str, int x, int y, int size) {
        graphics.setFont(new Font("Impact", Font.PLAIN, size));
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.drawString(str, x - metrics.stringWidth(str) / 2, y - metrics.getHeight() / 2 + metrics.getAscent());
    }
}
