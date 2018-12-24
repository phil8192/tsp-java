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

  private double reverseDelta(Point[] tour, int from, int to) {
    double curPrime = 0d;
    for(int i = from + 1; i <= to; i++) {
      if(i % 10 == 0) {
        Point curPoint = tour[i];
	      Point prePoint = tour[i - 1];
	      if(!prePoint.isPrime()) {
          curPrime += 0.1 * prePoint.distance(curPoint);
        }
      }
    }
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
    reverse(tour, from, to);
    return newPrime - curPrime;
  }

  private double reverseDelta2(Point[] tour, int from, int to) {
    // need to reverse a segment in 2-opt.
    // this means a different score for reversed segment: primes will be in different places.
    // (this makes the problem difficult and hard to optimise)
    double curPrime = 0, newPrime = 0;
    int i = from + (10 - (from % 10)); // start (next 10th from b_idx)
    int j = to - (i - from); // opposite side
    //System.out.println("i = " + i + " j = " + j);
    while(i <= to) { // up until c (inclusive)
      Point curPoint = tour[i]; // current city
      if(curPoint.isPrime()) {
        //int prev = (i > 0 ? i : tour.length) - 1;
        int prev = i - 1;
        curPrime += 0.1 * tour[prev].distance(curPoint);
      }
      Point revPoint = tour[j]; // city in this place when reversed
      if(revPoint.isPrime()) {
        //int next = (j < tour.length - 1 ? j + 1 : 0);
        int next = j + 1;
        newPrime += 0.1 * tour[next].distance(revPoint); // previous will be next from revPoint.
      }
      i += 10;
      j -= 10;
    }
    return newPrime - curPrime;
  }

  public double moveCost(Point a, Point b, Point c, Point d,
                         int a_idx, int b_idx, int c_idx, int d_idx,
                         Point[] tour) {

    // distance delta: original edges (ab) (cd), candidate edges (ac) (bd).
    double d_ab = a.distance(b), d_cd = c.distance(d);
    double d_ac = a.distance(c), d_bd = b.distance(d);
    double deltaD = (d_ac + d_bd) - (d_ab + d_cd);

    // penalty delta
    //double p_ab = getPenalty(a, b), p_cd = getPenalty(c, d);
    //double p_ac = getPenalty(a, c), p_bd = getPenalty(b, d);
    //double deltaP = lamda * ((p_ac + p_bd) - (p_ab + p_cd));

    // prime delta
    double curPrime = 0, newPrime = 0;

    if(b_idx % 10 == 0) {
      if(b.isPrime()) { // ab
        curPrime += 0.1 * d_ab;
      }
      if(c.isPrime()) { // proposed ac (c will take place of b)
        newPrime += 0.1 * d_ac;
      }
    }
    if(d_idx % 10 == 0) {
      if(d.isPrime()) { // cd
        curPrime += 0.1 * d_cd;
      }
      if(b.isPrime()) { // proposed bd (b will take place of c)
        newPrime += 0.1 * d_bd;
      }
    }

    // FLS currently reverses part of tour that does not need to be wrapped around.
    // todo: will need to select side which minimises prime penalty.
    // reverse(points, Math.min(a, c)+1, Math.max(a, c));

    final int from, to;
    if(a_idx < c_idx) {
      from = b_idx; // a_idx + 1;
      to = c_idx;
    } else {
      from = d_idx; //c_idx + 1;
      to = a_idx;
    }

    //double revPrime = reverseDelta(tour, Math.min(a_idx, c_idx)+1, Math.max(a_idx, c_idx));
    double revPrime = reverseDelta(tour, from, to);

    double deltaPrime = (newPrime - curPrime) + revPrime;


    //if(deltaD + deltaPrime < 0) {
      double pre = Point.distance(tour, from, to);
      reverse(tour, from, to);
      double rev = Point.distance(tour, from, to);
      double verifyDiff = rev - pre;
      reverse(tour, from, to);
      int i = from + (10 - (from % 10)); // start (next 10th from b_idx)
      int j = to - (i - from);
      System.out.println("from = " + from + " to = " + to + " i = " + i + " j = " + j + " deltaD = " + deltaD +
          " newPrime = " + newPrime + " curPrime = " + curPrime + " revPrime = " + revPrime + " pre = " + pre +
          " rev = " + rev + " verify_diff = " + verifyDiff);
    //}
    //return deltaD + deltaP + deltaPrime;
    return deltaD + deltaPrime;

  }
}
