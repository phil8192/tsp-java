package net.parasec.tsp.santa;

import net.parasec.tsp.algo.Point;
import net.parasec.tsp.cost.TwoOptMoveCost;
import net.parasec.tsp.util.Maths;
import net.parasec.tsp.algo.gls.PenaltyMatrix;

public class SantaGLSMoveCost implements TwoOptMoveCost<SantaPoint> {
  private static final double PEN = 0.1;

  private final PenaltyMatrix penalties;
  private double lambda;


  public SantaGLSMoveCost(final PenaltyMatrix penalties, double lambda) {
    this.penalties = penalties;
    this.lambda = lambda;
  }

  private double getPenalty(Point from, Point to) {
    return penalties.getPenalty(from.getId(), to.getId());
  }

  private double reverseDelta(SantaPoint[] tour, int from, int to) {
    // todo: can swap either side in 2-opt, this does not check which side minimises prime penalty most.
    // need to reverse a segment in 2-opt.
    // this means a different score for reversed segment: primes will be in different places.
    // (this makes the problem difficult and hard to optimise)
    double curPrime = 0, newPrime = 0;
    int i = from + (10 - (from % 10)); // start (next 10th from b_idx)
    int j = to - (i - from); // opposite side

    while(i <= to) { // up until c (inclusive)
      SantaPoint prePoint = tour[i - 1]; // pre city
      if(!prePoint.isPrime()) {
        curPrime += PEN * prePoint.distance(tour[i]);
      }
      SantaPoint revPrePoint = tour[j + 1]; // previous city in this place when reversed
      if(!revPrePoint.isPrime()) {
        newPrime += PEN * tour[j].distance(revPrePoint); // previous will be next from revPoint.
      }
      i += 10;
      j -= 10;
    }
    return newPrime - curPrime;
  }

  public double moveCost(SantaPoint a, SantaPoint b, SantaPoint c, SantaPoint d,
                         int a_idx, int b_idx, int c_idx, int d_idx,
                         SantaPoint[] tour) {

    double _ab = a._distance(b), _cd = c._distance(d); // current
    double _ac = a._distance(c), _bd = b._distance(d); // new

    if(_ab < _ac && _cd < _bd) return 1;

    // distance delta: original edges (ab) (cd), candidate edges (ac) (bd).
    double d_ab = Maths.sqrt(_ab), d_cd = Maths.sqrt(_cd);
    double d_ac = Maths.sqrt(_ac), d_bd = Maths.sqrt(_bd);
    double deltaD = (d_ac + d_bd) - (d_ab + d_cd);

    double ab_pen = getPenalty(a, b), cd_pen = getPenalty(c, d);
    double ac_pen = getPenalty(a, c), bd_pen = getPenalty(b, d);
    double cur_penalty = ab_pen + cd_pen;
    double new_penalty = ac_pen + bd_pen;
    double deltaP = lambda * (new_penalty - cur_penalty);

    // prime delta
    double curPrime = 0, newPrime = 0;

    final int from, to;
    if(a_idx < c_idx) {

      // A -> B before C -> D. will reverse between B <-> C (inclusive)
      from = b_idx;
      to = c_idx;

      if(b_idx % 10 == 0) {
        // A stays in place.
        if(!a.isPrime()) {
          curPrime += PEN * d_ab; // current (A -> B)
          newPrime += PEN * d_ac; // new     (A -> C)

        }
      }
      if(d_idx != 0 && d_idx % 10 == 0) {
        // D stays in place.
        if(!c.isPrime()) {
          curPrime += PEN * d_cd; // current (C -> D)
        }
        if(!b.isPrime()) {
          newPrime += PEN * d_bd; // new     (B -> D)
        }
      }
    } else {

      // C -> D before A -> B. will reverse between D <-> A (inclusive)
      from = d_idx;
      to = a_idx;

      if(b_idx != 0 && b_idx % 10 == 0) {
        // B stays in place.
        if(!a.isPrime()) {
          curPrime += PEN * d_ab; // current (A -> B)
        }
        if(!d.isPrime()) {
          newPrime += PEN * d_bd; // new     (D -> B)
        }
      }
      if(d_idx % 10 == 0) {
        // C stays in place.
        if(!c.isPrime()) {
          curPrime += PEN * d_cd; // current (C -> D)
          newPrime += PEN * d_ac; // new     (C -> A)
        }
      }
    }

    double revPrime = reverseDelta(tour, from, to);
    double deltaPrime = (newPrime - curPrime) + revPrime;

    return deltaD + deltaP + deltaPrime;
  }
}