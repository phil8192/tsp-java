package net.parasec.tsp.distance;

import net.parasec.tsp.algo.Point;

public class DefaultDistance implements TourDistance<Point> {

  @Override
  public double distance(final Point[] points) {
    final int len = points.length;
    double d = points[len - 1].distance(points[0]);
    d += distance(points, 0, len - 1);
    return d;
  }

  @Override
  public double distance(Point[] points, int from, int to) {
    double d = 0d;
    for(int i = from + 1; i <= to; i++) {
      final Point pre = points[i - 1], cur = points[i];
      d += pre.distance(cur);
    }
    return d;
  }
}
