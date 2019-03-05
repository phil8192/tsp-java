package net.parasec.tsp.cost;

import net.parasec.tsp.algo.Point;

public interface TwoOptMoveCost<E extends Point> {
  double moveCost(E a, E b, E c, E d,
                  int a_idx, int b_idx, int c_idx, int d_idx,
                  E[] tour);
}
