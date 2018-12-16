package net.parasec.tsp.impl;

public interface TwoOptMoveCost {
  double moveCost(Point a, Point b, Point c, Point d,
                  int a_idx, int b_idx, int c_idx, int d_idx,
                  Point[] tour);
}
