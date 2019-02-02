package net.parasec.tsp.algo;

import net.parasec.tsp.util.Maths;

public class Point {

  private final int id;
  private final double x;
  private final double y;

  private boolean active;


  public Point(int id, double x, double y) {
    this(id, x, y, true);
  }

  public Point(int id, double x, double y, boolean active) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.active = active;
  }

  public int getId() {
    return id;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double distance(final Point to) {
    return Maths.sqrt(_distance(to));
  }

  public double _distance(final Point to) {
    final double dx = this.x - to.x;
    final double dy = this.y - to.y;
    return (dx * dx) + (dy * dy);
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Point copy() {
    return new Point(id, x, y, active);
  }

  public String toString() {
    return id + " " + x + " " + y;
  }

  public static Point[] copy(Point[] points) {
    final Point[] _points = new Point[points.length];
    for(int i = 0; i < points.length; i++) {
      _points[i] = points[i].copy();
    }
    return _points;
  }
}