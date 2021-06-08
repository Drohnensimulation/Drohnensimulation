package de.thi.dronesim.drone;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

@Deprecated(since = "Use MView instead.", forRemoval = true)
public class UfoMView extends JFrame implements Runnable, KeyListener {

  private UfoSim sim  = UfoSim.getInstance(); // UfoView instance
  private JTextArea dataTextArea;             // text area where flight data are displayed
  private volatile boolean trigger = false;   // event triggered by key pressed in view window
  
  // constructor
  public UfoMView() {

    setLayout(new FlowLayout());

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    add(mainPanel);

    Font dataFont = new Font("Courier", Font.PLAIN, 18);

    JLabel headlineLabel = new JLabel("Flight Data:");
    headlineLabel.setHorizontalAlignment(JLabel.CENTER);
    headlineLabel.setFont(dataFont);
    mainPanel.add(headlineLabel, BorderLayout.NORTH);    

    JPanel dataFlow = new JPanel(new FlowLayout());
    dataFlow.setBackground(Color.WHITE);
    mainPanel.add(dataFlow, BorderLayout.SOUTH);

    dataTextArea = new JTextArea();
    dataTextArea.setText(getDataString());
    dataTextArea.setPreferredSize(new Dimension(220, 240));
    dataTextArea.setFont(dataFont);
    dataTextArea.setCaretColor(Color.WHITE);
    DefaultCaret caret = (DefaultCaret) dataTextArea.getCaret();
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    dataTextArea.addKeyListener(this);
    dataFlow.add(dataTextArea);

    // configure window
    pack();
    setTitle("Ufo Simulation");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
    setLocationRelativeTo(null);

    try { 
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
    } 
    catch (Exception e) { }

    // start thread for refreshing flight data
    new Thread(this).start();
  }

  // construct the flight data string which is displayed
  private String getDataString() {
    return (" x     " + String.format("%6.1f", sim.getX()) + "  m" + 
            "\n y     " + String.format("%6.1f", sim.getY()) + "  m" + 
            "\n z     " + String.format("%6.1f", sim.getZ()) + "  m" + 
            "\n v       " + String.format("%2d", sim.getV()) + "    km/h" +
            "\n d      " + String.format("%3d", sim.getD())  + "    deg"+ 
            "\n i      " + String.format("%3d", sim.getI())  + "    deg"+
            "\n dist  " + String.format("%6.1f", sim.getDist()) + "  m" +
            "\n radar  " + String.format("%6.2f", sim.getRadar()) + " m" +
            "\n time  " + String.format("%6.1f", sim.getTime())) + "  s";
  }

  // return the trigger set by key pressed an reset it
  public boolean getTrigger() {
    if (trigger) {
      trigger = false;
      return true;
    }
    else
      return false;
  }
  
  // thread function
  
  public void run() {
    while (true) {
      dataTextArea.setText(getDataString());
      
      // refresh flight data in the same rate as simulation
      try {
        Thread.sleep(100/UfoSim.SPEEDUP);
      }
      catch (java.lang.InterruptedException ex) { }
    }
  }

  // set trigger if key pressed
  @Override
  public void keyPressed(KeyEvent arg0) {
    trigger = true;
  }

  @Override
  public void keyReleased(KeyEvent arg0) { }

  @Override
  public void keyTyped(KeyEvent arg0) { }
  
}
