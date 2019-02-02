package net.parasec.tsp.distance;

import net.parasec.tsp.algo.Point;

public interface TourDistance<E extends Point> {
  double distance(E[] points);
  double distance(E[] points, int from, int to);
}