package net.parasec.tsp.impl;

public class BasicTwoOptMoveCost implements TwoOptMoveCost {

/**
  * cost of a 2-Opt.
  * cost of replacing existing edges (ab), (cd) with new edges (ac) (bd).
  * returns the delta of a 2-Opt move. a negative delta indicates that
  * performing this 2-Opt will result in a shorter tour, and a positive delta
  * indicates that this 2-Opt will result in a longer tour.
  *
  * this function is the main hotspot in the optimisation. it is not feasible
  * to pre-compute a matrix (a lookup table) for a tour with N cities, since
  * this will be O(N^2) and the most compact representation will be (N^2-N)/2.
  *
  * good optimisation: most of the time the algorithm is evaluating bad moves,
  * in the obvious case where 2 edge exchanges would result in 2 longer
  * edges, avoid 4 square root operations by comparing squares. this results
  * in a 40% speed up in this code.
  */
  public double moveCost2(Point a, Point b, Point c, Point d,
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





  private double reverseDelta(Point[] tour, int from, int to) {
    // need to reverse a segment in 2-opt.
    // this means a different score for reversed segment: primes will be in different places.
    // (this makes the problem difficult and hard to optimise)
    double curPrime = 0, newPrime = 0;
    int i = from + (10 - (from % 10)); // start (next 10th from b_idx)
    int j = to - (i - from); // opposite side
    //System.out.println("i = " + i + " j = " + j);
    while(i <= to) { // up until c (inclusive)
      //System.out.println("i  = " + i);
      Point prePoint = tour[i - 1]; // pre city
      if(!prePoint.isPrime()) {
        //int prev = (i > 0 ? i : tour.length) - 1;
        curPrime += 0.1 * prePoint.distance(tour[i]);
      }
      Point revPrePoint = tour[j + 1]; // previous city in this place when reversed
      if(!revPrePoint.isPrime()) {
        //int next = (j < tour.length - 1 ? j + 1 : 0);
        newPrime += 0.1 * tour[j].distance(revPrePoint); // previous will be next from revPoint.
      }
      i += 10;
      j -= 10;
    }
    //System.out.println("reverse_delta curPrime = " + curPrime);
    //System.out.println("reverse_delta newPrime = " + newPrime);
    return newPrime - curPrime;
  }


  public double moveCost(Point a, Point b, Point c, Point d,
                         int a_idx, int b_idx, int c_idx, int d_idx,
                         Point[] tour) {

    // distance delta: original edges (ab) (cd), candidate edges (ac) (bd).
    double d_ab = a.distance(b), d_cd = c.distance(d);
    double d_ac = a.distance(c), d_bd = b.distance(d);
    double deltaD = (d_ac + d_bd) - (d_ab + d_cd);

    // prime delta
    double curPrime = 0, newPrime = 0;


    // FLS currently reverses part of tour that does not need to be wrapped around.
    // todo: will need to select side which minimises prime penalty.
    // reverse(points, Math.min(a, c)+1, Math.max(a, c));

    final int from, to;
    if(a_idx < c_idx) {

      // A -> B before C -> D. will reverse between B <-> C (inclusive)
      from = b_idx;
      to = c_idx;

      if(b_idx % 10 == 0) {
        // A stays in place.
        if(!a.isPrime()) {
          curPrime += 0.1 * d_ab; // current (A -> B)
          newPrime += 0.1 * d_ac; // new     (A -> C)
        }
      }
      if(d_idx != 0 && d_idx % 10 == 0) {
        // D stays in place.
        if(!c.isPrime()) {
          curPrime += 0.1 * d_cd; // current (C -> D)
        }
        if(!b.isPrime()) {
          newPrime += 0.1 * d_bd; // new     (B -> D)
        }
      }
    } else {

      // C -> D before A -> B. will reverse between D <-> A (inclusive)
      from = d_idx;
      to = a_idx;

      if(b_idx != 0 && b_idx % 10 == 0) {
        // B stays in place.
        if(!a.isPrime()) {
          curPrime += 0.1 * d_ab; // current (A -> B)
        }
        if(!d.isPrime()) {
          newPrime += 0.1 * d_bd; // new     (D -> B)
        }
      }
      if(d_idx % 10 == 0) {
        // C stays in place.
        if(!c.isPrime()) {
          curPrime += 0.1 * d_cd; // current (C -> D)
          newPrime += 0.1 * d_ac; // new     (C -> A)
        }
      }
    }

    double revPrime = reverseDelta(tour, from, to);

    double deltaPrime = (newPrime - curPrime) + revPrime;


    return deltaD + deltaPrime;

  }
}
