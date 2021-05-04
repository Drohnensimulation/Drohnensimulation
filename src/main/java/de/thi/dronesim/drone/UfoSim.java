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

package de.thi.dronesim.drone;

/**
 * @deprecated
 * @see de.thi.dronesim.Simulation
 *
 * class for the simulation of an ufo
 */
@Deprecated
public final class UfoSim implements Runnable {

  // constants
  public final static double VMAX = 50 / 3.6;                // maximal velocity [m/s]
  public final static double RADAR_RANGE = 50;      // range of the radar [m]
  public final static double ACCELERATION = 10 / 3.6;         // constant acceleration [m/s^2]
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

  private final Location location = new Location(0, 0, 0);

  private volatile double dist;        // distance covered since reset [m]
  private volatile double radar;       // distance of radar detected ground or obstacle [m]
  private volatile double time;        // elapsed flight time with v > 0 since reset [s]

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

  /**
   *
   * @return Total travel distance in m
   */
  public double getDist() {
    return dist;
  }

  public double getRadar() {
    return radar;
  }

  /**
   *
   * @return Past simulation time in s
   */
  public double getTime() {
    return time;
  }

  /**
   *
   * @return Location of the UFO.
   */
  public Location getLocation() {
    return location;
  }

  /* -------------------------------------------------------------------------------------------------------------------
   * Mapper methods to support legacy methods. Use Location methods instead!
   * -----------------------------------------------------------------------------------------------------------------*/

  private int  i = 0; // Inclination in deg

  /**
   * @deprecated use {@link Location#getX()} instead
   * @return X Position of UFO.
   */
  public double getX() {
    return location.getX();
  }

  /**
   * @deprecated use {@link Location#getY()} ()} instead
   * @return X Position of UFO.
   */
  public double getY() {
    return location.getY();
  }

  /**
   * @deprecated use {@link Location#getZ()} instead
   * @return Z Position of UFO.
   */
  public double getZ() {
    return location.getZ();
  }

  /**
   * @deprecated use {@link Location#getAirspeed()} instead
   * @return The airspeed in km/h
   */
  public int getV() {
    return (int) Math.round(Math.sqrt(Math.pow(location.getGroundSpeed(), 2)
            + Math.pow(location.getVerticalSpeed(), 2)) * 3.6);
  }

  /**
   * @deprecated use {@link Location#getHeading()} ()} instead
   * @return The heading in deg.
   */
  public int getD() {
    return (int) Math.round(location.getHeading());
  }

  /**
   * @deprecated use {@link Location#getDeltaVerticalSpeed()} and {@link Location#getAirspeed()} instead.
   * @return The inclination of the z-axis in deg.
   */
  public int getI() {
    return i;
  }

  /**
   * Requests a change im air speed.
   * @deprecated use {@link Location#requestDeltaAirspeed(double)} instead.
   *
   * @param delta Change of air speed in km/h
   */
  public void requestDeltaV(int delta) {
    requestDeltaIV(0,  delta / 3.6);
  }

  /**
   * Requests a change in direction.
   * @deprecated use {@link Location#requestDeltaHeading(double)} instead.
   * @param delta Change of direction in deg
   */
  public void requestDeltaD(int delta) {
    location.requestDeltaHeading(delta);
  }

  /**
   * Requests a change of inclination.
   * @deprecated use {@link Location#requestDeltaAirspeed(double)} and {@link Location#requestDeltaVerticalSpeed(double)}
   *              instead.
   * @param delta Change if inclination in deg
   */
  public void requestDeltaI(int delta) {
    requestDeltaIV(delta, 0);
  }

  /**
   * Converts delta of I and V into vs and tas to support legacy methods.
   * @param deltaI Delta I in deg
   * @param deltaV Delta V in km/h
   */
  private void requestDeltaIV(double deltaI, double deltaV) {
    double tasCurr = location.getAirspeed() + location.getDeltaAirspeed();
    // Calculate legacy d
    double vsCurr = location.getVerticalSpeed() + location.getDeltaVerticalSpeed();
    double v = Math.sqrt(Math.pow(tasCurr, 2) + Math.pow(vsCurr, 2));

    // Apply delta
    v += deltaV;
    i += deltaI;

    // Calculate resulting vs and tas
    double vsNext = Math.sin(Math.toRadians(i)) * v;
    double tasNext = Math.cos(Math.toRadians(i)) * v;

    location.requestDeltaVerticalSpeed(vsNext - vsCurr);
    location.requestDeltaAirspeed(tasNext - tasCurr);
  }

  /**
   * Sets the heading.
   * @deprecated use {@link Location#setHeading(double)} instead.
   * @param d Heading in deg.
   */
  public void setD(int d) {
    if (d >= 0 && d <= 359)
      location.setHeading(d);
  }

  /**
   * Sets the inclination.
   * @deprecated use {@link Location#setVerticalSpeed(double)} and {@link Location#setAirspeed(double)} instead.
   * @param i Inclination in deg.
   */
  public void setI(int i) {
    if (i >= -90 && i <= 90) {
      this.i = i;
      // Recalculate speed vectors
      requestDeltaIV(0, 0);
    }
  }

  public void reset() {
    location.reset();
    dist = 0;
    radar = -1;
    time = 0;
  }

  /* -------------------------------------------------------------------------------------------------------------------
   * Simulation threat
   * ---------------------------------------------------------------------------------------------------------------- */

  // thread function
  public void run() {
    while (true) {
      // Sync over location to avoid race condition when calculating next position
      synchronized (location) {
        // update time if flying
        if (location.getZ() > 0)
          time += 0.1;

        // update location and dist if not crashed
        if (location.getZ() >= 0) {
          location.updateDelta(10);

          // update distance
          dist += location.getGroundSpeed() / 10;

          // Do amy external updates on track and ground speed here

          // update position
          location.updatePosition(10);
        }

        // stop if landed or crashed
        if (location.getZ() <= 0) {
          if (location.getVerticalSpeed() > 1 / 3.6) {  // crashed to the ground
            location.setZ(-1);
          } else {  // landed with slow velocity
            location.setZ(0);
          }
          location.setVerticalSpeed(0);
          location.setAirspeed(0);
          radar = -1;

        } else {
          // detect obstacles with the radar
          radar = detectObstacleAdapter();

          if (radar == 0) {                        // crashed into obstacle
            location.setZ(-1);
            location.setAirspeed(0);
            location.setVerticalSpeed(0);
            radar = -1;
          } else {                                   // detect ground with the radar
            radar = min(radar, detectGround(location.getMovement().y));
          }
        }
      }
      try  {
        // FIXME actual task execution will be less then 10 times per second due to execution time. Is this ok?
        Thread.sleep(100/SPEEDUP);
      }
      catch (InterruptedException ignored) { }
    }
  }

  /* -------------------------------------------------------------------------------------------------------------------
   * Senor methods
   * ---------------------------------------------------------------------------------------------------------------- */

  // private detection functions

  // detect whether the obstacle is within the radar interval s = 0, t = radarRange
  // this is a very sophisticated algorithm, do not try to understand it
  // optimized version
  private double detectObstacle(double x1, double x2, 
                                double y1, double y2, 
                                double z1, double z2) {

    // Mapping for old definitions
    double x = location.getX();
    double xvect = location.getMovement().x;
    double y = location.getY();
    double yvect = location.getMovement().y;
    double z = location.getZ();
    double zvect = location.getMovement().z;

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
    double sz = (0 - location.getZ()) / zvect;

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
