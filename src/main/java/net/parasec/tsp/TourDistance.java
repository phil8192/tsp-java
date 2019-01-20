package net.parasec.tsp;

import net.parasec.tsp.impl.Point;

public interface TourDistance<E extends Point> {
  double distance(E[] points);
  double distance(E[] points, int from, int to);
}