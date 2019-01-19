package net.parasec.tsp.impl;

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

  /*
  public static double distance(final Point[] points) {
    final int len = points.length;
    double d = points[len-1].distance(points[0]);
    d += distance(points, 0, len - 1);
    return d;
  }

  public static double distance(Point[] points, int from, int to) {
    double d = 0d;
    for(int i = from+1; i <= to; i++) {
      final Point pre = points[i-1], cur = points[i];
      double distance = pre.distance(cur);
      if(i % 10 == 0 && !pre.isPrime()) {
        distance *= 1.1;
      }
      d += distance;
    }
    return d;
  }
  */

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