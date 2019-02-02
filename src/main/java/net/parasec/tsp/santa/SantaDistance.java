package net.parasec.tsp.santa;

import net.parasec.tsp.distance.TourDistance;

public class SantaDistance implements TourDistance<SantaPoint> {

  @Override
  public double distance(SantaPoint[] points) {
    final int len = points.length;
    double d = points[len - 1].distance(points[0]);
    d += distance(points, 0, len - 1);
    return d;
  }

  @Override
  public double distance(SantaPoint[] points, int from, int to) {
    double d = 0d;
    for(int i = from + 1; i <= to; i++) {
      final SantaPoint pre = points[i - 1], cur = points[i];
      double distance = pre.distance(cur);
      if(i % 10 == 0 && !pre.isPrime()) {
        distance *= 1.1;
      }
      d += distance;
    }
    return d;
  }

}
