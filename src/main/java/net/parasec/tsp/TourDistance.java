package net.parasec.tsp;

public interface TourDistance<E extends Point> {
  double distance(E[] points);
  double distance(E[] points, int from, int to);
}