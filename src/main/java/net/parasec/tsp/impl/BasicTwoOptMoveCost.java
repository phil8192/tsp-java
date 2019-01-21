package net.parasec.tsp.impl;

public class BasicTwoOptMoveCost implements TwoOptMoveCost {

  /**
   * cost of a 2-Opt.
   * cost of replacing existing edges (ab), (cd) with new edges (ac) (bd).
   * returns the delta of a 2-Opt move. a negative delta indicates that
   * performing this 2-Opt will result in a shorter tour, and a positive delta
   * indicates that this 2-Opt will result in a longer tour.
   * <p>
   * this function is the main hotspot in the optimisation. it is not feasible
   * to pre-compute a matrix (a lookup table) for a tour with N cities, since
   * this will be O(N^2) and the most compact representation will be (N^2-N)/2.
   * <p>
   * good optimisation: most of the time the algorithm is evaluating bad moves,
   * in the obvious case where 2 edge exchanges would result in 2 longer
   * edges, avoid 4 square root operations by comparing squares. this results
   * in a 40% speed up in this code.
   */
  public double moveCost(Point a, Point b, Point c, Point d,
                         int a_idx, int b_idx, int c_idx, int d_idx,
                         Point[] tour) {

    // original edges (ab) (cd), candidate edges (ac) (bd)
    double _ab = a._distance(b), _cd = c._distance(d);
    double _ac = a._distance(c), _bd = b._distance(d);

    // triangle of inequality: at least 1 edge will be shorter.
    // if both will be longer, there will be no improvement.
    // return a positive delta to indicate no improvement.
    if(_ab < _ac && _cd < _bd) return 1;

    // otherwise must calculate distance delta.
    return (Maths.sqrt(_ac) + Maths.sqrt(_bd)) -
        (Maths.sqrt(_ab) + Maths.sqrt(_cd));
  }

}
