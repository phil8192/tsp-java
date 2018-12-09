package net.parasec.tsp.impl;

public final class Point {

  private int id;
  private final double x;
  private final double y;
  //private boolean active = true;
  private boolean active = false;

  public Point(final int id, final double x, final double y) {
    this.id = id;
    this.x = x;
    this.y = y;
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

  /**
  * Euclidean distance.
  * tour wraps around N-1 to 0.
  */
  public static double distance(final Point[] points) {
    final int len = points.length;
    double d = points[len-1].distance(points[0]);
    for(int i = 1; i < len; i++) {
      d += points[i-1].distance(points[i]);
    }
    return d;
  }

  /**
  * Euclidean distance.
  */
  public final double distance(final Point to) {
    return Maths.sqrt(_distance(to));
  }

  /**
  * compare 2 points.
  * no need to square when comparing.
  * http://en.wikibooks.org/wiki/Algorithms/Distance_approximations
  */
  public final double _distance(final Point to) {
    final double dx = this.x-to.x;
    final double dy = this.y-to.y;
    return (dx*dx)+(dy*dy);
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

  public static Point copy(final Point point) {
    final Point _point = new Point(point.getId(), point.getX(), point.getY());
    //_point.setActive(point.isActive());
    return _point;
  }

  public static Point[] copy(final Point[] points) {
    final Point[] _points = new Point[points.length];
    for(int i = 0; i < points.length; i++) {
      _points[i] = Point.copy(points[i]);
    }
    return _points;
  }

  public String toString() {
    return id + " " + x + " " + y;
  }
}
