package net.parasec.tsp.impl;

public class SantaGLSMoveCost extends GLSMoveCost {

  public SantaGLSMoveCost(final PenaltyMatrix penalties, double lamda, int numCities) {
    super(penalties, lamda, numCities);
  }

  private void reverse(final Point[] x, final int from, final int to) {
    for(int i = from, j = to; i < j; i++, j--) {
      final Point tmp = x[i];
      x[i] = x[j];
      x[j] = tmp;
    }
  }

  private double reverseDelta2(Point[] tour, int from, int to) {
    double curPrime = 0d;
    for(int i = from + 1; i <= to; i++) {
      if(i % 10 == 0) {
       // System.out.println("ii = " + i);
        Point curPoint = tour[i];
	      Point prePoint = tour[i - 1];
	      if(!prePoint.isPrime()) {
          curPrime += 0.1 * prePoint.distance(curPoint);
        }
      }
    }
    //System.out.println("reverse_delta2 curPrime = " + curPrime);
    reverse(tour, from, to);
    double newPrime = 0d;
    for(int i = from + 1; i <= to; i++) {
      if(i % 10 == 0) {
        Point curPoint = tour[i];
	      Point prePoint = tour[i - 1];
	      if(!prePoint.isPrime()) {
          newPrime += 0.1 * prePoint.distance(curPoint);
        }
      }
    }
    //System.out.println("reverse_delta2 newPrime = " + newPrime);
    reverse(tour, from, to);
    return newPrime - curPrime;
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

    double _ab = a._distance(b), _cd = c._distance(d);
    double _ac = a._distance(c), _bd = b._distance(d);
    // triangle of inequality: at least 1 edge will be shorter.
    // if both will be longer, there will be no improvement.
    // return a positive delta to indicate no improvement.
    if(_ab < _ac && _cd < _bd) return 1;

    // distance delta: original edges (ab) (cd), candidate edges (ac) (bd).
    //double d_ab = a.distance(b), d_cd = c.distance(d);
    //double d_ac = a.distance(c), d_bd = b.distance(d);
    double d_ab = Maths.sqrt(_ab), d_cd = Maths.sqrt(_cd);
    double d_ac = Maths.sqrt(_ac), d_bd = Maths.sqrt(_bd);
    double deltaD = (d_ac + d_bd) - (d_ab + d_cd);

    // penalty delta
    double p_ab = getPenalty(a, b), p_cd = getPenalty(c, d);
    double p_ac = getPenalty(a, c), p_bd = getPenalty(b, d);
    // l * new(ac, bd) - old(ab, cd)
    double deltaP = lamda * ((p_ac + p_bd) - (p_ab + p_cd));

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


    if(deltaD + deltaPrime < 0) {
      double pre = Point.distance(tour, from, to);
      reverse(tour, from, to);
      double rev = Point.distance(tour, from, to);
      double verifyDiff = rev - pre;
      reverse(tour, from, to);

      int i = from + (10 - (from % 10)); // start (next 10th from b_idx)
      int j = to - (i - from);
      reverseDelta2(tour, from, to);
      System.out.println("from = " + from + " to = " + to + " i = " + i + " j = " + j + " deltaD = " + deltaD +
          " newPrime = " + newPrime + " curPrime = " + curPrime + " revPrime = " + revPrime + " pre = " + pre +
          " rev = " + rev + " verify_diff = " + verifyDiff + " a_idx = " + a_idx + " b_idx = " + b_idx + " c_idx = " +
          c_idx + " d_idx = " + d_idx);
    }
    return deltaD + deltaP + deltaPrime;
    //return deltaD + deltaPrime;

  }
}
