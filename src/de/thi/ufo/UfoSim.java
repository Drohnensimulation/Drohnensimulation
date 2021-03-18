/* UfoSim
 *
 * Simulation of Unified Flying Objects
 *
 * Copyright (C) 2013-2021 R. Gold
 *
 * This file is intended for use as an example, and
 * may be used, modified, or distributed in source or
 * object code form, without restriction, as long as
 * this copyright notice is preserved.
 *
 * The code and information is provided "as-is" without
 * warranty of any kind, either expressed or implied.
 *
 * Version: 3.2.5mpro
 *              
 * The jars are created by Export... as JAR file 
 * (no main class required, resources should be 
 * in src\\de\\thi\\ufo) and can be started by
 * java -cp <all jars separated by ;> package.main_class_name 
 * 
 * Do not change this file! 
 *  
 * Use only the public interface:
 * 
 * public final static int VMAX
 * public final static double RADAR_RANGE
 * public final static int ACCELERATION
 * public static int SPEEDUP
 * 
 * public static Ufosim getInstance()
 * public void setSpeedup(int)
 * 
 * Methods for the View Window:
 * public void openViewWindow()
 * public boolean getTrigger()
 *
 * Getter:
 * public double getX()
 * public double getY()
 * public double getZ()
 * public int getV()
 * public int getD()
 * public int getI()
 * public double getDist()
 * public double getRadar()
 * public double getTime()
 * 
 * Delta Requester:
 * public void requestDeltaV(int)
 * public void requestDeltaD(int)
 * public void requestDeltaI(int)
 * 
 * Setter:
 * public void setD(int)
 * public void setI(int)
 * 
 * Resetter:
 * public void reset()
 */

package de.thi.ufo;

// class for the simulation of an ufo
public final class UfoSim implements Runnable {

  // constants
  public final static int VMAX = 50;                // maximal velocity [km/h]
  public final static double RADAR_RANGE = 50;      // range of the radar [m]
  public final static int ACCELERATION = 1;         // constant acceleration [km/h/0.1s]
  public static int SPEEDUP = 1;                    // real-time speedup factor > 0
  
  // defines and detects the obstacles
  private double detectObstacleAdapter() {
    double r =  detectObstacle(30, 50, 50, 65, 0, 30);  
    r =  min(r, detectObstacle(50, 70, 50, 65, 0, 30)); 
    
    return r;
  }

  // attributes
  private static UfoSim instance;      // singleton instance
  private UfoMView view = null;        // UfoView instance
  
  private volatile double x;           // x coordinate [m]
  private volatile double y;           // y coordinate [m]
  private volatile double z;           // z coordinate [m]
  private int v;                       // 0 <= velocity <= vMax [km/h]
  private int d;                       // 0 <= direction <= 359 [deg]
  private int i;                       // -90 <= inclination <= 90 [deg]
  private volatile double dist;        // distance covered since reset [m]
  private volatile double radar;       // distance of radar detected ground or obstacle [m]
  private volatile double time;        // elapsed flight time with v > 0 since reset [s]
  private double xvect;                // flight vector in x direction
  private double yvect;                // flight vector in y direction
  private double zvect;                // flight vector in z direction
  private int deltaV;                  // requested change of v
  private int deltaD;                  // requested change of d
  private int deltaI;                  // requested change of i
  private double vel;                  // velocity [m/s]

  // constructor
  private UfoSim() {
    reset();

    // start the thread that calculates the coordinates
    new Thread(this).start();
  }

  // get the instance
  public static UfoSim getInstance() {
    if (instance == null) {
      instance = new UfoSim();
    }
    
    return instance;
  }

  // set the speedup
  public void setSpeedup(int speedup) {
    if (speedup < 1 || speedup > 50)
      System.out.println("Warning: Speedup has to be between 1 and 50");
    else if (SPEEDUP == 1)
      SPEEDUP = speedup;
    else if (SPEEDUP != speedup)
      System.out.println("Warning: Speedup can be set only once");
  }

  // open a default view window
  public void openViewWindow() {
    view = new UfoMView();
  }
  
  // get the trigger from the view window and reset it if set
  public boolean getTrigger() {
    if (view != null)
      return view.getTrigger();
    else
      return false;
  }
  
  // get and set
  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public int getV() {
    return v;
  }

  public int getD() {
    return d;
  }

  public int getI() {
    return i;
  }

  public double getDist() {
    return dist;
  }

  public double getRadar() {
    return radar;
  }

  public double getTime() {
    return time;
  }

  public void requestDeltaV(int delta) {
    deltaV = deltaV + delta;
  }

  public void requestDeltaD(int delta) {
    deltaD = deltaD + delta;
  }

  public void requestDeltaI(int delta) {
    deltaI = deltaI + delta;
  }

  public void setD(int d) {
    if (d >= 0 && d <= 359)
      this.d = d;
  }

  public void setI(int i) {
    if (i >= -90 && i <= 90)
      this.i = i;
  }

  public void reset() {
    x = 0;
    y = 0;
    z = 0;
    v = 0;
    d = 90;
    i = 90;
    dist = 0;
    radar = -1;
    time = 0;
    xvect = 0;
    yvect = 0;
    zvect = 0;
    deltaV = 0;
    deltaD = 0;
    deltaI = 0;
    vel = 0;
  }

  // thread function
  public void run() {
    while (true) {
      // update time if flying
      if (z > 0)
        time = time + 0.1;

      // update v, d, i, dist, x, y, z if not crashed
      if (z >= 0) {
        // update v
        if (deltaV > 0) {
          if (deltaV - ACCELERATION > 0) {
            v = (v + ACCELERATION < VMAX ? v + ACCELERATION : VMAX);
            deltaV = deltaV - ACCELERATION;
          }
          else {
            v = (v + deltaV < VMAX ? v + deltaV : VMAX);
            deltaV = 0;
          }
        }
        else if (deltaV < 0) { 
          if (deltaV + ACCELERATION < 0) {
            v = (v - ACCELERATION > 0 ? v - ACCELERATION : 0);
            deltaV = deltaV + ACCELERATION;
          }
          else {
            v = (v + deltaV > 0 ? v + deltaV : 0);
            deltaV = 0;
          }
        }  

        // update d
        if      (deltaD > 0 && d == 359) { deltaD--; d = 0; }
        else if (deltaD > 0 && d < 359)  { deltaD--; d++; }
        else if (deltaD < 0 && d == 0)   { deltaD++; d = 359; }
        else if (deltaD < 0 && d > 0)    { deltaD++; d--; }
        else                               deltaD = 0;
     
        // update i
        if      (deltaI > 0 && i < 90)   { deltaI--; i++; }
        else if (deltaI < 0 && i > -90)  { deltaI++; i--; }
        else                               deltaI = 0;
      
        // update velocity in m/s
        vel = v / 3.6;
        
        // update distance
        dist = dist + vel / 10;

        // convert flight vector from polar coordinates to cartesian coordinates
        xvect = Math.sin((double)(-i+90)/180 * Math.PI) * Math.cos((double)d/180 * Math.PI);
        yvect = Math.sin((double)(-i+90)/180 * Math.PI) * Math.sin((double)d/180 * Math.PI);
        zvect = Math.cos((double)(-i+90)/180 * Math.PI);
      
        // calculate new position every 100 ms with 1/10 of v
        x = x + vel / 10 * xvect;
        y = y + vel / 10 * yvect;
        z = z + vel / 10 * zvect;
      }

      // stop if landed or crashed
      if (z <= 0) {
        if (v == 1) {                            // landed with slow velocity
          z = 0;
          v = 0;
          radar = -1;
        }
        else if (v > 1) {                        // crashed to the ground
          v = 0;
          radar = -1;
        }
      }
      else {
        // detect obstacles with the radar
        radar = detectObstacleAdapter();  

        if (radar == 0) {                        // crashed into obstacle
          z = -1;
          v = 0;
          radar = -1;
        }
        else {                                   // detect ground with the radar
          radar = min(radar, detectGround(zvect));
        }
      }
 
      try  {
        Thread.sleep(100/SPEEDUP);
      }
      catch (InterruptedException ex) { }
    }
  }

  // private detection functions

  // detect whether the obstacle is within the radar interval s = 0, t = radarRange
  // this is a very sophisticated algorithm, do not try to understand it
  // optimized version
  private double detectObstacle(double x1, double x2, 
                                double y1, double y2, 
                                double z1, double z2) {
    Interval parameter = new Interval(); // parameter interval of ufo radar
    Interval obstacle = new Interval();  // coordinate interval of obstacle
    Interval ufo = new Interval();       // coordinate interval of ufo radar
    Interval cut = new Interval();       // cut of obstacle with ufo radar

    parameter.set(0, RADAR_RANGE);

    obstacle.set(x1, x2);
    ufo.set(x + parameter.left * xvect, x + parameter.right * xvect);
    cut = obstacle.cut(ufo);

    if (cut == null)
      return -1;

    if (Math.abs(xvect) > 10e-5)
      parameter.set((cut.left - x)/xvect, (cut.right - x)/xvect);

    obstacle.set(y1, y2);
    ufo.set(y + parameter.left * yvect, y + parameter.right * yvect);
    cut = obstacle.cut(ufo);

    if (cut == null)
        return -1;

    if (Math.abs(yvect) > 10e-5)
      parameter.set((cut.left - y)/yvect, (cut.right - y)/yvect);

    obstacle.set(z1, z2);
    ufo.set(z + parameter.left * zvect, z + parameter.right * zvect);
    cut = obstacle.cut(ufo);

    if (cut == null)
      return -1;

    if (Math.abs(zvect) > 10e-5)
      parameter.set((cut.left - z)/zvect, (cut.right - z)/zvect);

    return parameter.left;
  }

  // detect whether the obstacle is within the radar interval s = 0, t = radarRange
  // this is a very sophisticated algorithm, do not try to understand it
  // basic version
  /*private double detectObstacle(double x1, double x2, 
                                double y1, double y2, 
                                double z1, double z2) {
    // crashed into the obstacle?
    if (x1 <= x && x <= x2 && y1 <= y && y <= y2 && z1 <= z && z <= z2) return 0;

    // calculate the x-parameter interval that intersects the obstacle
    double sx, tx, temp;

    // no x-flight? look whether x is in the x-interval or not
    if (xvect == 0) {
      if (x1 <= x && x <= x2) { sx = 0; tx = RADAR_RANGE; } else return -1;
    }
    else {
      sx = (x1 - x) / xvect;
      tx = (x2 - x) / xvect;

      // borders of x-parameter interval wrongly odered?
      if (tx < sx) { temp = sx; sx = tx; tx = temp;}

      // x-parameter interval left or right of radar interval?
      if (tx < 0 || RADAR_RANGE < sx) return -1;
    }

    // calculate the y-parameter interval that intersects the obstacle
    double sy, ty;

    if (yvect == 0) {
      if (y1 <= y && y <= y2) { sy = 0; ty = RADAR_RANGE; } else return -1;
    }
    else {
      sy = (y1 - y) / yvect;
      ty = (y2 - y) / yvect;
      if (ty < sy) { temp = sy; sy = ty; ty = temp; }
      if (ty < 0 || RADAR_RANGE < sy) return -1;
    }

    // calculate the z-parameter interval that intersects the obstacle
    double sz, tz;

    if (zvect == 0) {
      if (z1 <= z && z <= z2) { sz = 0; tz = RADAR_RANGE; } else return -1;
    }
    else {
      sz = (z1 - z) / zvect;
      tz = (z2 - z) / zvect;
      if (tz < sz) { temp = sz; sz = tz; tz = temp; }
      if (tz < 0 || RADAR_RANGE < sz) return -1;
    }

    // calculate intersection of parameter intervals
    double s, t;

    if (sx > sy) s = (sx > sz ? sx : sz);
    else         s = (sy > sz ? sy : sz);

    if (tx < ty) t = (tx < tz ? tx : tz);
    else         t = (ty < tz ? ty : tz);

    if (s <= t && 0 <= s && s <= RADAR_RANGE) return s; else return -1;
  }*/

  // detect whether the ground is within the radar interval s = 0, t = radarRange
  private double detectGround(double zvect) {
    double sz = (0 - z) / zvect;

    if (0 <= sz && sz <= RADAR_RANGE)  // detected
      return sz;
    else
      return -1;
  }

  // a special minimum function
  private double min(double z1, double z2) {
    if (z1 != -1) {
      if (z2 != -1) 
        return (z1 < z2 ? z1 : z2);
      else
        return z1;
    }
    else
      return z2;
  }

  // internal class for intervals [left;right]
  private class Interval {

    public double left = 0;
    public double right = 0;

    public void set(double l, double r) {
      if (l <= r) {
        left = l;
        right = r;
      }
      else {
        left = r;
        right = l;
      }
    }

    public Interval cut(Interval other) {
      if (this.left > other.left)
        return other.cut(this);

      if (this.right < other.left)
        return null;

      if (this.right < other.right) {
        Interval result = new Interval();
        result.set(other.left, this.right);
        return result;
      }
      else
        return other;
    }

  }

}
