package net.parasec.tsp.impl;

public class SantaGLSMoveCost extends GLSMoveCost {

  public SantaGLSMoveCost(final PenaltyMatrix penalties, double lamda, int numCities) {
    super(penalties, lamda, numCities);
  }

  private double reverseDelta(Point[] tour, int from, int to) {
    // need to reverse a segment in 2-opt.
    // this means a different score for reversed segment: primes will be in different places.
    // (this makes the problem difficult and hard to optimise)
    double curPrime = 0, newPrime = 0;
    int i = from + (10 - (from % 10)); // start (next 10th from b_idx)
    int j = to - (i - from); // opposite side
    while(i <= to) { // up until c (inclusive)
      Point curPoint = tour[i]; // current city
      if(curPoint.isPrime()) {
        curPrime += 0.1 * tour[i - 1].distance(curPoint);
      }
      Point revPoint = tour[j]; // city in this place when reversed
      if(revPoint.isPrime()) {
        newPrime += 0.1 * tour[j + 1].distance(revPoint); // previous will be next from revPoint.
      }
      i += 10;
      j -= 10;
    }
    return curPrime - newPrime;
  }

  public double moveCost(Point a, Point b, Point c, Point d,
                         int a_idx, int b_idx, int c_idx, int d_idx,
                         Point[] tour) {

    // distance delta: original edges (ab) (cd), candidate edges (ac) (bd).
    double d_ab = a.distance(b), d_cd = c.distance(d);
    double d_ac = a.distance(c), d_bd = b.distance(d);
    double deltaD = (d_ac + d_bd) - (d_ab + d_cd);

    // penalty delta
    double p_ab = getPenalty(a, b), p_cd = getPenalty(c, d);
    double p_ac = getPenalty(a, c), p_bd = getPenalty(b, d);
    double deltaP = lamda * ((p_ac + p_bd) - (p_ab + p_cd));

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

    double revPrime = reverseDelta(tour, b_idx, c_idx);

    double deltaPrime = (newPrime - curPrime) + revPrime;

    return deltaD + deltaP + deltaPrime;
  }
}
